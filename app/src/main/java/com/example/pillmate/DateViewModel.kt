package com.example.pillmate

import androidx.lifecycle.ViewModel
import java.time.DayOfWeek
import java.time.LocalDate

class DateViewModel : ViewModel() {
    val dateItems = mutableListOf<DateItem>()

    init {
        generateDateItems()
    }

    private fun generateDateItems() {
        if (dateItems.isEmpty()) {
            val todayDate = LocalDate.now()
            val firstDayOfWeek = todayDate.with(DayOfWeek.SUNDAY).minusWeeks(1)

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
        }
    }
}