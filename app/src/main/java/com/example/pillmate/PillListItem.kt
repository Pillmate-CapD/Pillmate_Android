package com.example.pillmate

import android.util.Log

// time: 알람 시간, pillName: 약 이름
data class PillListItem(val time: String, val name: String, var isEaten: Boolean, var medicineId: Int, var alarmId:Int) {
    // 시간 문자열에서 시간 부분을 추출하는 함수
    fun extractHour(): Int {
        val parts = time.split(" ")
        val amPm = parts.getOrNull(0) // "오전" 또는 "오후"
        val timePart = parts.getOrNull(1) // "21:46:00" 등

        if (timePart != null) {
            val hour = timePart.split(":")[0].toIntOrNull() ?: return 0
            Log.d("PillListItem", "Extracted hour before AM/PM check: $hour")

            return when (amPm) {
                "오전" -> if (hour == 12) 0 else hour // 오전 12시는 0시로 변환
                "오후" -> if (hour == 12) 12 else hour + 12 // 오후 12시는 그대로 12시, 그 외 오후는 12 추가
                else -> hour // 오전/오후 없을 경우 그대로 사용
            }
        }
        Log.e("PillListItem", "Failed to extract hour from time: $time")
        return 0
    }

    // 시간 문자열에서 분 부분을 추출하는 함수
    fun extractMinute(): Int {
        val timePart = time.split(" ").getOrNull(1) ?: return 0 // "21:46:00"과 같은 부분만 가져옴
        val minute = timePart.split(":").getOrNull(1)?.toIntOrNull() ?: 0
        Log.d("PillListItem", "Extracted minute: $minute from time: $time")
        return minute
    }

    // 시간 문자열에서 시 부분을 추출하는 함수
    fun extractHourAlarm(): Int {
        return time.split(":").getOrNull(0)?.toIntOrNull() ?: 0
    }

    // 시간 문자열에서 분 부분을 추출하는 함수
    fun extractMinuteAlarm(): Int {
        return time.split(":").getOrNull(1)?.toIntOrNull() ?: 0
    }

    // 오후인지 확인하는 메서드
    fun isPm(): Boolean {
        return time.startsWith("오후")
    }

    // 24시간 형식으로 반환하는 함수 (HH:mm 형식)
    fun to24HourFormat(): String {
        val hour = extractHour()
        val minute = extractMinute()
        return String.format("%02d:%02d", hour, minute)
    }
}