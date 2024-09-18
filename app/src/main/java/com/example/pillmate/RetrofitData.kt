package com.example.pillmate

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("wakeUp")
    val wakeUp: String,
    @SerializedName("morning")
    val morning: String,
    @SerializedName("lunch")
    val lunch: String,
    @SerializedName("dinner")
    val dinner: String,
    @SerializedName("bed")
    val bed: String,
    @SerializedName("diseases")
    val diseases: List<DiseaseData>,
    @SerializedName("roles")
    val roles: List<String>
)

data class DiseaseData(
    @SerializedName("disease")
    val disease: String,
    @SerializedName("startDate")
    val startDate: String
)

data class SignUpResponse(
    @SerializedName("grantType")
    val grantType: String,
    @SerializedName("accessToken")
    val accessToken: String
)

// 약 직접 추가
data class MediAddRequest(
    @SerializedName("medicineName")
    val medicineName: String,
    @SerializedName("disease")
    val disease: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("timesPerDay")
    val timesPerDay: Int,
    @SerializedName("day")
    val day: Int,
    @SerializedName("timeSlotList")
    val timeSlotList: List<TimeSlotRequest>
)

data class TimeSlotRequest(
    @SerializedName("spinnerTime")
    val spinnerTime: String,
    @SerializedName("pickerTime")
    val pickerTime: String
)

data class MediAddResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("code")
    val code: String
)

// 약리스트 화면에서 메디 리스트 얻기
data class MediListResponse(
    val picture: String,
    val name: String,
    val category: String,
    val amount: Int,
    val timesPerDay: Int,
    val day: Int,
    val timeSlotList: List<TimeSlotResponse>
)

data class TimeSlotResponse(
    val id: Int,
    val spinnerTime: String,
    val pickerTime: String
)
