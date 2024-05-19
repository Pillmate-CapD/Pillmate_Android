package com.example.pillmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.FragmentHomeBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: DateAdapter
    private val dateItems = mutableListOf<DateItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 현재 날짜를 가져옴
        val currentDate = LocalDate.now()

        // 현재 주의 첫 번째 날을 계산
        val firstDayOfWeek = currentDate.with(DayOfWeek.SUNDAY)

        // 이전 주의 첫 번째 날을 계산
        val previousWeekFirstDay = firstDayOfWeek.minusDays(7)

        // 이전 주의 일요일부터 토요일까지의 날짜를 계산하여 추가
        for (i in 0..6) {
            val date = previousWeekFirstDay.plusDays(i.toLong())
            val formattedDate = date.format(DateTimeFormatter.ofPattern("dd"))
            val dayOfWeek = when (date.dayOfWeek) {
                DayOfWeek.SUNDAY -> "일"
                DayOfWeek.MONDAY -> "월"
                DayOfWeek.TUESDAY -> "화"
                DayOfWeek.WEDNESDAY -> "수"
                DayOfWeek.THURSDAY -> "목"
                DayOfWeek.FRIDAY -> "금"
                DayOfWeek.SATURDAY -> "토"
            }
            dateItems.add(DateItem(dayOfWeek, formattedDate))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container, false)

        adapter = DateAdapter(dateItems as ArrayList<DateItem>)
        binding.dateList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dateList.adapter = adapter


        return binding.root
    }
}