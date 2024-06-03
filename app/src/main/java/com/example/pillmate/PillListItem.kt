package com.example.pillmate

// time: 알람 시간, pillName: 약 이름
data class PillListItem(val time: String, val name: String) {
    // 시간 문자열에서 시간 부분만을 추출하는 함수
    fun extractHour(): Int {
        // 시간 문자열을 공백을 기준으로 분할하여 시간 부분을 가져옵니다.
        val parts = time.split(" ")
        val timePart = parts.getOrNull(1) // "오전", "오후" 이후의 시간 부분을 가져옵니다.

        // 시간 부분이 null이면 0을 반환합니다.
        if (timePart == null) {
            return 0
        }

        // 시간 부분에서 "오전"인 경우 시간을 그대로 반환하고,
        // "오후"인 경우 시간을 12시간 더한 값을 반환합니다.
        val hourPart = parts.getOrNull(2)?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 0
        return if (timePart == "오전") {
            hourPart
        } else {
            (hourPart + 12) % 24
        }
    }
}