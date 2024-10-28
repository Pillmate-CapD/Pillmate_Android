package com.example.pillmate

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pillListAdapter: PillListAdapter
    private lateinit var remainMediAdapter: RemainMediAdapter

    private var userName :String? = null

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(requireContext())
    }

    private val pillViewModel: PillViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("userName", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "손해인")
        if (userName != null) {
            // 약 리스트 RecyclerView 설정
            pillListAdapter = PillListAdapter(arrayListOf(), this, preferencesHelper)
            binding.pillList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.pillList.adapter = pillListAdapter

            // RemainMediAdapter 초기화 및 설정
            remainMediAdapter = RemainMediAdapter(listOf()) // 빈 리스트로 초기화
            binding.remainMediRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.remainMediRecycler.adapter = remainMediAdapter

            // ViewModel을 통해 약 리스트 업데이트
            pillViewModel.pillItems.observe(viewLifecycleOwner) { pillItems ->
                pillListAdapter.updateItems(pillItems) // 필터링 없이 전체 약 리스트 업데이트
            }

//            binding.homePillProgressBar.setMaxProgress(100)
//            binding.homePillProgressBar.setProgress(100)


            // 현재 월 업데이트
            // 현재 월과 일 업데이트
            val calendar = Calendar.getInstance()
            val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val day = calendar.get(Calendar.DAY_OF_MONTH).toString()

            binding.calMonth.text = month
            binding.calDay.text = day

            //binding.homeMonth.text = month

            binding.btnPillList.setOnClickListener {
                findNavController().navigate(R.id.listFragment)
            }

            binding.nonBtnAddMedi.setOnClickListener {
                findNavController().navigate(R.id.listFragment)
            }
        }
        return binding.root
    }

    private fun fetchMain(){
        val service = RetrofitApi.getRetrofitService
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        Log.d("fetchMain", "Current Time: $currentTime")

        // API 호출 준비 단계
        val call = service.getMain(currentTime)
        Log.d("fetchMain", "API Call Initiated with time parameter: $currentTime")

        call.enqueue(object : Callback<MainPageResponse> {
            override fun onResponse(
                call: Call<MainPageResponse>,
                response: Response<MainPageResponse>
            ) {
                Log.d("fetchMain", "API Response Received")
                Log.d("fetchMain", "Response Code: ${response.code()}")
                Log.d("fetchMain", "Response Message: ${response.message()}")

                if (response.isSuccessful) {
                    val mainResponse = response.body()
                    Log.d("fetchMain", "Response Body: $mainResponse")

                    mainResponse?.let { response ->
                        Log.d("fetchMain", "Parsed Response Data: $response")

                        // 데이터를 ViewModel에 전달
                        pillViewModel.loadPillItems(
                            response.medicineAlarmRecords.map {
                                val formattedTime = convertTimeTo12HourFormat(it.time)
                                PillListItem(time = formattedTime, name = it.name, isEaten = it.isEaten, medicineId = it.medicineId)
                            }
                        )

                        // medicineAlarmRecords 개수 로그 출력
                        val medicineRecordCount = response.medicineAlarmRecords.size
                        binding.mediNum.text="총 ${medicineRecordCount}정"

                        // upcomingAlarm의 medicineName과 time을 변환하여 UI에 표시
                        val upcomingAlarm = response.upcomingAlarm
                        val formattedTime = convertTimeTo12HourFormat(upcomingAlarm.time)
                        binding.tvNextMedi.text = "$formattedTime ${upcomingAlarm.medicineName}"

                        val remainingAlarm = response.remainingMedicine.size
                        binding.tvLastMediGuide.text="복용 안한 약 일정이 ${remainingAlarm}개 있어!"

                        // remainingMedicine의 name 필드를 | 구분자로 연결하여 표시
                        val remainingMedicineNames = response.remainingMedicine.joinToString(" | ") { it.name }
                        binding.tvLastMedi.text = remainingMedicineNames

                        val remainingMedicineItems = response.remainingMedicine.map { RemainMediItem(it.name, it.day) }
                        remainMediAdapter.updateItems(remainingMedicineItems)

                        // 화면 업데이트
                        if (response.medicineAlarmRecords.isNullOrEmpty()) {
                            Log.d("fetchMain", "No medicine records found - updating UI to show no data layout")
                            binding.pillList.visibility = View.GONE
                            binding.btnAllPill.visibility = View.GONE
                            binding.homeNonDataLayout.visibility = View.VISIBLE
                        } else {
                            Log.d("fetchMain", "Medicine records found - updating UI to show data")
                            binding.pillList.visibility = View.VISIBLE
                            binding.btnAllPill.visibility = View.VISIBLE
                            binding.homeNonDataLayout.visibility = View.GONE
                        }
                    }
                } else {
                    Log.d("fetchMain", "Response was unsuccessful")
                }
            }

            override fun onFailure(call: Call<MainPageResponse>, t: Throwable) {
                Log.e("fetchMain", "API Call Failed", t)
            }
        })
    }


    private fun convertTimeTo12HourFormat(time: String): String {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val localTime = LocalTime.parse(time, timeFormatter)
        val hour = localTime.hour
        val minute = localTime.minute
        val amPm = if (hour < 12) "오전" else "오후"
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12
        return "$amPm $formattedHour:${String.format("%02d", minute)}"
    }

    override fun onResume() {
        fetchMain()
        val calendar = Calendar.getInstance()
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        //binding.homeMonth.text = month

        val sharedPreferences = requireActivity().getSharedPreferences("userName", MODE_PRIVATE)
        userName = sharedPreferences.getString("userName", "Loading name failed")
        binding.userTxt.text = userName
        super.onResume()
    }
}
