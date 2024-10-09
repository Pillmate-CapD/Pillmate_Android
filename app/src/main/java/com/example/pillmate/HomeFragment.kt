package com.example.pillmate

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(requireContext())
    }

    private val REQUEST_CODE_EAT_MEDI = 1001

    private val dateViewModel: DateViewModel by viewModels()
    private val pillViewModel: PillViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카테고리 아이템 추가
        categoryItems.add(CategoryItem("전체", true))
        categoryItems.add(CategoryItem("고혈압"))
        categoryItems.add(CategoryItem("고지혈증"))
        categoryItems.add(CategoryItem("당뇨"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 홈화면 => 주간 달력
        dateAdapter = DateAdapter(dateViewModel.dateItems as ArrayList<DateItem>)
        binding.dateList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dateList.adapter = dateAdapter

        // 홈화면 => 약 카테고리 파트
        categoryAdapter = CategoryAdapter(categoryItems as ArrayList<CategoryItem>)
        binding.pillCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.pillCategory.adapter = categoryAdapter

        // 홈화면 => 약 리스트 파트
        pillListAdapter = PillListAdapter(arrayListOf(), this, preferencesHelper)
        binding.pillList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.pillList.adapter = pillListAdapter

        binding.alertImg.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.alarmListActivity)
        }

        binding.homePillProgressBar.setMaxProgress(100)
        binding.homePillProgressBar.setProgress(100)

        // ViewModel을 사용하여 데이터 로드
        pillViewModel.loadPillItems(
            listOf(
                PillListItem("오전 8:00", "트윈스타정"),
                PillListItem("오전 11:00", "디아미크롱서방정"),
                PillListItem("오후 12:00", "파스틱정"),
                PillListItem("오후 6:30", "파스틱정"),
                PillListItem("오후 9:00", "바이토린")
            )
        )

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

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EAT_MEDI && resultCode == Activity.RESULT_OK) {
            val completed = data?.getBooleanExtra("completed", false) ?: false
            val position = data?.getIntExtra("position", -1) ?: -1
            if (completed && position != -1) {
                pillViewModel.setPillCompleted(position, true)
                pillListAdapter.notifyItemChanged(position)
            }
        }
    }

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
                            binding.homeNonDataLayout.visibility = View.INVISIBLE
                        }
                    }

                }
            }
            override fun onFailure(call: Call<MainPageResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        fetchMain()

        // Getting the current month using Calendar
        val calendar = Calendar.getInstance()
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        // Updating the TextView for current month
        binding.homeMonth.text = month
        super.onResume()
    }
}