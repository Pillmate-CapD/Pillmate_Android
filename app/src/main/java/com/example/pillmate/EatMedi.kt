package com.example.pillmate

data class EatMedi(
    val layoutRes: Int,           // 레이아웃 리소스 ID
    var isVisible: Boolean,       // 단계의 가시성 상태
    var isCompleted: Boolean,     // 단계의 완료 상태
    var photoPath: String? = null, // 사진 경로 (로컬 경로)
    var pillName: String = "",    // 약명
    var medicineCategory: String = "", // 약의 카테고리
    var photoUrl: String? = null  // API에서 받아온 이미지 URL
)