package com.example.pillmate

import java.time.LocalDate

// day: 요일 date: 날짜
data class DateItem(val dayOfWeek: String, val formattedDate: String, val progress: Int, val isToday: Boolean, val date: LocalDate) {
}
