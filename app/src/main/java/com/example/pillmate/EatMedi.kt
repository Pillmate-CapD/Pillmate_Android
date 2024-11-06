package com.example.pillmate

data class EatMedi(
    val layoutRes: Int,  // 레이아웃 리소스 ID
    var isVisible: Boolean,  // 단계의 가시성 상태
    var isCompleted: Boolean,  // 단계의 완료 상태
    var photoPath: String? = null,  // 사진 경로를 저장할 변수 추가
    var pillName: String = ""  // pillName 필드 추가
)
