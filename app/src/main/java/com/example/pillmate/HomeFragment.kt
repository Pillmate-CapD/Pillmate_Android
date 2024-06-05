package com.example.pillmate

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.databinding.FragmentHomeBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dateAdapter: DateAdapter
    private val dateItems = mutableListOf<DateItem>()

    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryItems = mutableListOf<CategoryItem>()

    private lateinit var pillListAdapter: PillListAdapter
    private val pillListItems = mutableListOf<PillListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // 현재 날짜를 가져옴
//        val todayDate = LocalDate.now()
//
//        // 현재 주의 첫 번째 날을 계산
//        val firstDayOfWeek = todayDate.with(DayOfWeek.SUNDAY)
//
//        // 이전 주의 첫 번째 날을 계산
//        val previousWeekFirstDay = firstDayOfWeek.minusDays(7)
//
//        // 이전 주의 일요일부터 토요일까지의 날짜를 계산하여 추가
//        for (i in 0..6) {
//            val date = previousWeekFirstDay.plusDays(i.toLong())
//            val formattedDate = date.format(DateTimeFormatter.ofPattern("dd"))
//            val dayOfWeek = when (date.dayOfWeek) {
//                DayOfWeek.SUNDAY -> "일"
//                DayOfWeek.MONDAY -> "월"
//                DayOfWeek.TUESDAY -> "화"
//                DayOfWeek.WEDNESDAY -> "수"
//                DayOfWeek.THURSDAY -> "목"
//                DayOfWeek.FRIDAY -> "금"
//                DayOfWeek.SATURDAY -> "토"
//            }
//            val isToday = date == todayDate // 오늘 날짜인지 확인
//            dateItems.add(DateItem(dayOfWeek, formattedDate, 50, isToday, LocalDate.now()))
//        }

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
        dateAdapter = DateAdapter(dateItems as ArrayList<DateItem>)
        binding.dateList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dateList.adapter = dateAdapter

        // 홈화면 => 약 카테고리 파트
        categoryAdapter = CategoryAdapter(categoryItems as ArrayList<CategoryItem>)
        binding.pillCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.pillCategory.adapter = categoryAdapter

        // 홈화면 => 약 리스트 파트
        pillListAdapter = PillListAdapter(pillListItems as ArrayList<PillListItem>)
        binding.pillList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.pillList.adapter = pillListAdapter

        binding.alertImg.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.alarmListActivity)
        }

        binding.homePillProgressBar.setMaxProgress(100)
        binding.homePillProgressBar.setProgress(90)

        pillListAdapter.apply {
            pillListItems.clear()
            pillListItems.add(PillListItem("오전 1:00", "트윈스타정"))
            pillListItems.add(PillListItem("오전 1:47", "디아미크롱서방정"))
            pillListItems.add(PillListItem("오후 12:00", "파스틱정"))
            pillListItems.add(PillListItem("오후 7:30", "파스틱정"))
            pillListItems.add(PillListItem("오후 9:00", "바이토린"))
            notifyDataSetChanged()
        }

        // 현재 날짜를 가져옴
        val todayDate = LocalDate.now()

        // 현재 주의 첫 번째 날(일요일)을 계산
        val firstDayOfWeek = todayDate.with(DayOfWeek.SUNDAY).minusWeeks(1)

        // 현재 주의 일요일부터 토요일까지의 날짜를 계산하여 추가
        for (i in 0..6) {
            val date = firstDayOfWeek.plusDays(i.toLong())
            val formattedDate = date.dayOfMonth.toString()
            val dayOfWeek = when (date.dayOfWeek) {
                DayOfWeek.SUNDAY -> "일"
                DayOfWeek.MONDAY -> "월"
                DayOfWeek.TUESDAY -> "화"
                DayOfWeek.WEDNESDAY -> "수"
                DayOfWeek.THURSDAY -> "목"
                DayOfWeek.FRIDAY -> "금"
                DayOfWeek.SATURDAY -> "토"
            }
            val isToday = date == todayDate
            dateItems.add(DateItem(dayOfWeek, formattedDate, 50, isToday, date))
        }

        return binding.root
    }
}