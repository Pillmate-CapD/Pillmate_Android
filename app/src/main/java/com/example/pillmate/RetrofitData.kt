package com.example.pillmate

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//로그인
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String)
data class TokenInfo(
    val name: String,
    val grantType: String,
    val accessToken: String)
data class LoginResponse(
    val tokenInfo: TokenInfo
)
//회원가입
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

//마이페이지 기존 비밀번호 확인
data class PasswordCheckRequest(
    val password: String
)

data class PasswordCheckResponse(
    val isValid: Boolean
)

//마이페이지 비번 변경
data class PasswordChangeRequest(
    val oldPassword: String,
    val newPassword: String
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
) : Serializable

data class TimeSlotResponse(
    val id: Int,
    val spinnerTime: String,
    val pickerTime: String
):Serializable

// 알람 추가 post
data class AddAlarmRequest(
    val medicineName: String,
    val time: String
)

data class AlarmListResponse(
    val id: Int,
    val name: String,
    val category: String,
    val amount: Int,
    val timesPerDay: Int,
    val day: Int,
    val timeSlotList: List<TimeSlotResponse>,
    val isAvailable: Boolean
)


data class MainPageResponse(
    val weekRateInfoList: List<WeekRateRequest>,
    val medicineAlarmRecords: List<MedicineAlarmRequest>,
    val grade: String,
    val takenDay: Int,
    val month: Int,
    val rate: Int,
    val bestRecord: BestRecordData,
    val worstRecord: WorstRecordData
)

data class WeekRateRequest(
    val date: String,
    val rate: Int
)

data class MedicineAlarmRequest(
    val name: String,
    val time: String,
    val category: String,
    val isEaten: Boolean
)

data class BestRecordData(
    val name: String,
    val taken: Int,
    val scheduled: Int,
)

data class WorstRecordData(
    val name: String,
    val taken: Int,
    val scheduled: Int,
)

data class MediEditRequest(
    @SerializedName("medicineName")
    val medicineName: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("timesPerDay")
    val timesPerDay: Int,
    @SerializedName("day")
    val day: Int,
    @SerializedName("timeSlotList")
    val timeSlotList: List<TimeSlotRequest>
)

data class MediInfoRequest(
    @SerializedName("name")
    val name: String
)

data class MediInfoResponse(
    val name:String,
    val photo: String,
    val category: String
)