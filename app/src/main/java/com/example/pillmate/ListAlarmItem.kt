package com.example.pillmate

data class ListAlarmItem(
    val amPm: String,
    val time: String,
    val medicationTitle: String,
    val medicationInfo: String,
    val medicationTime: String,
    var isAlarmOn: Boolean
)