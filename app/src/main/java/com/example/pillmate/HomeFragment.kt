package com.example.pillmate

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dateAdapter: DateAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var pillListAdapter: PillListAdapter
    private val categoryItems = mutableListOf<CategoryItem>()

    private var userName :String? = null

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(requireContext())
    }

    private val REQUEST_CODE_EAT_MEDI = 1001

    private val dateViewModel: DateViewModel by viewModels()
    private val pillViewModel: PillViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // 카테고리 아이템 추가
//        categoryItems.add(CategoryItem("전체", true))
//        categoryItems.add(CategoryItem("고혈압"))
//        categoryItems.add(CategoryItem("고지혈증"))
//        categoryItems.add(CategoryItem("당뇨"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("userName", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "손해인?")
        if (userName!=null){
                // 홈화면 => 주간 달력
                dateAdapter = DateAdapter(dateViewModel.dateItems as ArrayList<DateItem>)
                binding.dateList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.dateList.adapter = dateAdapter

                // 카테고리 RecyclerView 설정
                categoryAdapter = CategoryAdapter(categoryItems) { selectedCategory ->
                    pillViewModel.filterPillItemsByCategory(selectedCategory) // 선택된 카테고리에 맞춰 필터링
                }
                binding.pillCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.pillCategory.adapter = categoryAdapter

                // 약 리스트 RecyclerView 설정
                pillListAdapter = PillListAdapter(arrayListOf(), this, preferencesHelper)
                binding.pillList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.pillList.adapter = pillListAdapter

                // ViewModel을 통해 약 리스트 업데이트
                pillViewModel.pillItems.observe(viewLifecycleOwner) { pillItems ->
                    pillListAdapter.updateItems(pillItems) // 필터링된 약 리스트로 업데이트
                }

                binding.alertImg.setOnClickListener {
                    val navController = findNavController()
                    navController.navigate(R.id.alarmListActivity)
                }

                binding.homePillProgressBar.setMaxProgress(100)
                binding.homePillProgressBar.setProgress(100)

//        // ViewModel을 사용하여 데이터 로드
//        pillViewModel.loadPillItems(
//            listOf(
//                PillListItem("오전 8:00", "트윈스타정", fal),
//                PillListItem("오전 11:00", "디아미크롱서방정"),
//                PillListItem("오후 12:00", "파스틱정"),
//                PillListItem("오후 6:30", "파스틱정"),
//                PillListItem("오후 9:00", "바이토린")
//            )
//        )
                fetchHealthInfo()

                // Getting the current month using Calendar
                val calendar = Calendar.getInstance()
                val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

                // Updating the TextView for current month
                binding.homeMonth.text = month

                pillViewModel.pillItems.observe(viewLifecycleOwner) { pillItems ->
                    pillListAdapter.updateItems(pillItems)
                }

                binding.btnPillList.setOnClickListener {
                    findNavController().navigate(R.id.listFragment)
                }

                binding.nonBtnAddMedi.setOnClickListener {
                    val navController = findNavController()
                    navController.navigate(R.id.alarmListActivity)
                }
        }



        return binding.root
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_EAT_MEDI && resultCode == Activity.RESULT_OK) {
//            val completed = data?.getBooleanExtra("completed", false) ?: false
//            val position = data?.getIntExtra("position", -1) ?: -1
//            if (completed && position != -1) {
//                pillViewModel.setPillCompleted(position, true)
//                pillListAdapter.notifyItemChanged(position)
//            }
//        }
//    }

    private fun fetchMain(){
        val service = RetrofitApi.getRetrofitService
        val call = service.getMain()

        call.enqueue(object : Callback<MainPageResponse>{
            override fun onResponse(
                call: Call<MainPageResponse>,
                response: Response<MainPageResponse>
            ) {
                if (response.isSuccessful) {
                    val mainResponse= response.body()

                    mainResponse?.let { response ->
                        // Binding data to the TextViews
                        binding.homePillLevel.text = "${response.grade}"
                        binding.homePillUser.text = "${response.takenDay}일"
                        binding.homePillDay.text = "/${response.month}일"
                        binding.homePercent.text = "${response.rate}%"

                        binding.homePillProgressBar.setProgress(response.rate)

                        binding.homeGoodPillName.text = "${response.bestRecord.name}"
                        binding.homePillGoodUser.text = "${response.bestRecord.taken} 정 "
                        binding.homePillGoodNum.text = "/ ${response.bestRecord.scheduled} 정"

                        binding.homeBadPillName.text = "${response.worstRecord.name}"
                        binding.homePillBadUser.text = "${response.worstRecord.taken} 정 "
                        binding.homePillBadNum.text ="/ ${response.worstRecord.scheduled} 정"

                        binding.goodProgressBar.progress = response.bestRecord.scheduled
                        binding.badProgressBar.progress = response.worstRecord.scheduled
                        // Setting the progress bar
                        //binding.homePillProgressBar.progress = response.rate


                        pillViewModel.loadPillItems(
                            response.medicineAlarmRecords.map {
                                val formattedTime = convertTimeTo12HourFormat(it.time)
                                PillListItem(time = formattedTime, name = it.name, isEaten = it.isEaten, category = it.category)
                            }
                        )

                        // 서버에서 받은 progress 리스트를 ViewModel에 전달
//                        val progressValues = response.weekRateInfoList.map { it.rate }
//                        dateViewModel.updateDateItemsWithProgress(progressValues)


                        if(response.medicineAlarmRecords.isNullOrEmpty()) {
                            binding.pillCategory.visibility = View.INVISIBLE
                            binding.pillList.visibility = View.GONE
                            binding.btnAllPill.visibility = View.INVISIBLE
                            binding.homeNonDataLayout.visibility = View.VISIBLE
                        }
                        else{
                            binding.pillCategory.visibility = View.VISIBLE
                            binding.pillList.visibility = View.VISIBLE
                            binding.btnAllPill.visibility = View.VISIBLE
                            binding.homeNonDataLayout.visibility = View.GONE
                        }
                    }

                }
            }
            override fun onFailure(call: Call<MainPageResponse>, t: Throwable) {
                TODO("Not yet implemented")

            }

        })
    }

    private fun fetchHealthInfo(){
        val service = RetrofitApi.getRetrofitService
        val call = service.getHealthInfo()

        call.enqueue(object : Callback<HealthInfoResponse>{
            override fun onResponse(
                call: Call<HealthInfoResponse>,
                response: Response<HealthInfoResponse>
            ) {
                if(response.isSuccessful){
                    val healthResponse = response.body()

                    healthResponse?.let{ response->
                        // 카테고리 아이템 초기화 및 "전체" 항목 추가
                        categoryItems.clear() // 기존 항목을 지우고 새로 추가
                        categoryItems.add(CategoryItem("전체", true)) // 전체 항목 추가

                        // diseases 리스트에서 disease 이름을 카테고리로 추가
                        response.diseases.forEach { diseaseInfo ->
                            categoryItems.add(CategoryItem(diseaseInfo.disease))
                        }

                        // RecyclerView 등의 어댑터에 반영
                        categoryAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<HealthInfoResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    // 시간을 24시간 형식에서 12시간 형식으로 변환하는 함수
    private fun convertTimeTo12HourFormat(time: String): String {
        // "HH:mm:ss" 형식을 LocalTime으로 파싱
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val localTime = LocalTime.parse(time, timeFormatter)

        // 12시간 형식으로 변환
        val hour = localTime.hour
        val minute = localTime.minute
        val amPm = if (hour < 12) "오전" else "오후"
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12

        // "오전 7:00", "오후 10:00" 형식으로 반환
        return "$amPm $formattedHour:${String.format("%02d", minute)}"
    }

    override fun onResume() {
        fetchMain()
        fetchHealthInfo()

        // Getting the current month using Calendar
        val calendar = Calendar.getInstance()
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        // Updating the TextView for current month
        binding.homeMonth.text = month

        val sharedPreferences = requireActivity().getSharedPreferences("userName", MODE_PRIVATE)
        userName  = sharedPreferences.getString("userName","Loading name failed")
        binding.userTxt.text = userName
        super.onResume()
    }
}