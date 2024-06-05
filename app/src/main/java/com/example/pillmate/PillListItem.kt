package com.example.pillmate

// time: 알람 시간, pillName: 약 이름
data class PillListItem(val time: String, val name: String, var isCompleted: Boolean = false) {
    // 시간 문자열에서 시간 부분만을 추출하는 함수
    fun extractHour(): Int {
        // 시간 문자열을 공백을 기준으로 분할하여 시간 부분을 가져옵니다.
        val parts = time.split(" ")
        val amPm = parts.getOrNull(0) // "오전", "오후" 부분을 가져옵니다.
        val timePart = parts.getOrNull(1) // "7:00" 부분을 가져옵니다.

        // 시간 부분이 null이면 0을 반환합니다.
        if (amPm == null || timePart == null) {
            return 0
        }

        // 시간 부분에서 "오전"인 경우 시간을 그대로 반환하고,
        // "오후"인 경우 시간을 12시간 더한 값을 반환합니다.
        val hourPart = timePart.split(":")[0].toInt()
        return if (amPm == "오전") {
            hourPart
        } else {
            if (hourPart == 12) hourPart else hourPart + 12
        }
    }

    // 시간 문자열에서 분 부분을 추출하는 함수
    fun extractMinute(): Int {
        val parts = time.split(" ")
        val timePart = parts.getOrNull(1) // "7:00" 부분을 가져옵니다.

        // 시간 부분이 null이면 0을 반환합니다.
        if (timePart == null) {
            return 0
        }

        // 시간 부분에서 분을 반환합니다.
        return timePart.split(":")[1].toInt()
    }
}