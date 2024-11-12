package com.example.pillmate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var calendar: Calendar
    private lateinit var adapter: Calendar1Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        calendar = Calendar.getInstance()

        // Locale을 한국어로 설정
        Locale.setDefault(Locale.KOREAN)

        // 현재 날짜로 초기화
        updateYearMonthText()
        updateDateTitle()

        // RecyclerView 설정
        setupRecyclerView()


        // 드롭다운 버튼 클릭 시 DatePickerDialog 표시
        binding.dropdownButton.setOnClickListener {
            showSpinnerDatePickerDialog()
        }

        // editDiaryButton 클릭 시 HealthDiary1Activity로 이동
        binding.editDiaryButton.setOnClickListener {
            val intent = Intent(requireContext(), HealthDiary1Activity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
    // 현재 년/월 텍스트 업데이트 함수
    private fun updateYearMonthText() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        binding.ymTxt.text = "${year}년 ${month}월"
    }

    // 날짜 제목 업데이트 함수 ("MM월 dd일 E요일" 형식)
    private fun updateDateTitle() {
        val dateFormat = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREAN)
        binding.dateTitle.text = dateFormat.format(calendar.time)
    }

    // RecyclerView 설정 함수
    private fun setupRecyclerView() {
        val days = generateDaysForMonth()
        adapter = Calendar1Adapter(days) { day, month, year ->
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.YEAR, year)
            updateDateTitle()
        }
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7) // 수정함: GridLayoutManager 설정
        binding.calendarRecyclerView.adapter = adapter
    }

    // 날짜 리스트 생성 함수 (이전 달/다음 달 날짜 포함)
    private fun generateDaysForMonth(): List<Pair<Int?, Boolean>> {
        val days = mutableListOf<Pair<Int?, Boolean>>()
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 이전 달 날짜 추가
        tempCalendar.add(Calendar.MONTH, -1)
        val daysInPrevMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        tempCalendar.add(Calendar.MONTH, 1)

        for (i in firstDayOfWeek downTo 1) {
            days.add(Pair(daysInPrevMonth - i + 1, false))
        }

        // 현재 달 날짜 추가
        for (i in 1..daysInMonth) {
            days.add(Pair(i, true))
        }

        // 다음 달 날짜 추가
        val totalCells = 35
        val nextMonthDays = totalCells - days.size
        for (i in 1..nextMonthDays) {
            days.add(Pair(i, false))
        }

        return days
    }

    // Spinner 모드로 DatePickerDialog 표시 함수
    private fun showSpinnerDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Spinner 모드로 DatePickerDialog 생성
        val datePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(Calendar.YEAR, selectedYear)
                    calendar.set(Calendar.MONTH, selectedMonth)
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                    updateYearMonthText()
                    updateDateTitle()
                    // RecyclerView 업데이트
                    setupRecyclerView()

                    // 선택된 날짜를 업데이트하고 회색 동그라미 표시
                    adapter.updateSelectedDate(selectedDay, selectedMonth + 1, selectedYear)
                },
                year,
                month,
                day
            )
        } else {
            DatePickerDialog(requireContext())
        }

        // Spinner 모드 강제 설정
        datePickerDialog.datePicker.calendarViewShown = false
        datePickerDialog.datePicker.spinnersShown = true

        // DatePickerDialog 표시
        datePickerDialog.show()
    }
}
