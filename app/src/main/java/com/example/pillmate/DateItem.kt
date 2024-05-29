package com.example.pillmate

import java.time.LocalDate

// day: 요일 date: 날짜
data class DateItem(
    val dayOfWeek: String,
    val formattedDate: String,
    val progress: Int,
    val isToday: Boolean,
    val date: LocalDate
) {
    // 오늘 이후의 날짜 여부를 판단하는 메서드
    fun isAfterToday(today: LocalDate): Boolean {
        return date.isAfter(today)
    }
}
