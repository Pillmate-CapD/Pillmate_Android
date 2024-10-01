package com.example.pillmate

data class TimeSlotItem(
    val id: Int,
    var timeLabel: String,
    var time: String = "오전 06:00",
    var isTimeSelected: Boolean = false, // spinnerTime이 선택되었는지 여부
    var isTimeChanged: Boolean = false // pickerTime이 변경되었는지 여부
)



