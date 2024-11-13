package com.example.pillmate

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate

//로그인
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class TokenInfo(
    val name: String,
    val grantType: String,
    val accessToken: String
)

data class LoginResponse(
    val tokenInfo: TokenInfo
)
//회원가입
data class Disease(
    val disease: String,
    val startDate: LocalDate
) : Serializable

data class Symptom(
    val name: String
) : Serializable

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
    val diseases: List<Disease>,
    val symptoms: List<Symptom>,
    val roles: List<String>
)

data class SignUpResponse(
    val name: String,
    val grantType: String,
    val accessToken: String
)
//이메일 중복 체크
data class EmailCheckRequest(
    val email: String
)

// EmailCheckResponse.kt
data class EmailCheckResponse(
    val message: String
)

//로그아웃
data class LogoutResponse(
    val memberId: Int
)

//마이페이지 복약정보
data class UserStatusResponse(
    val grade: String,      // "매우 우수"
    val takenDay: Int,      // 복용한 일수 (예: 28)
    val month: Int,         // 한 달의 총 일수 (예: 31)
    val rate: Int           // 점수 (예: 90)
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

//일기 1페이지
data class DiarySymptom(
    val id: Int,
    val name: String
)
data class HealthDataResponse(
    val symptoms: List<DiarySymptom>
)

//내 건강정보
data class MyHealthInfo(
    val id: Int,
    val disease: String,
    val startDate: String
)

data class MyHealthInfoResponse(
    val diseases: List<MyHealthInfo>,
    val symptoms: List<MySymptom>
)
data class MySymptom(
    val id: Int,
    val name: String
)

data class MyHealthInfoRequest(
    val diseases: List<MyHealthInfo>, // 기존 질병 데이터 유지
    val symptoms: List<MySymptom>     // 수정된 증상 데이터
)

//내건강정보 수정
data class EditGMyHealthInfo(
    val id: Int,
    val disease: String,
    val startDate: String
)

data class EditGMySymptom(
    val id: Int,
    val name: String
): Serializable

data class EditGMyHealthInfoResponse(
    val diseases: List<EditGMyHealthInfo>,
    val symptoms: List<EditGMySymptom>
)
data class EditPMyHealthInfo(
    val disease: String,
    val startDate: String
)

data class EditPMySymptom(
    val name: String
)

data class EditPMyHealthInfoRequest(
    val diseases: List<EditPMyHealthInfo>,
    val symptoms: List<EditPMySymptom>
)

//복약과정 다음 알람정보
data class MedicineResponse(
    val medicineName: String,
    val time: String,
    val category: String
)

//복약과정(약명,카테고리,이미지url)
data class MedicineInfoResponse(
    val name: String,
    val photo: String,
    val category: String
)
data class MedicineNameRequest(
    val name: String
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
    val spinnerTime: String,
    val pickerTime: String
) : Serializable


data class MediAddResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("code")
    val code: String
)

// 약리스트 화면에서 메디 리스트 얻기
data class MediListResponse(
    val id: Int,
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
    val timeSlot: TimeSlotResponse,  // timeSlotList 대신 단일 timeSlot 사용
    val isAvailable: Boolean
)


data class MainPageResponse(
    val upcomingAlarm: UpcomingAlarmRequest,
    val missedAlarms: List<MissedAlarmsRequest>,
    val medicineAlarmRecords: List<MedicineAlarmRecordsRequest>,
    val remainingMedicine: List<RemainingMedicineRequest>,
    val bestRecord: BestRecordData,
    val worstRecord: WorstRecordData
)

data class UpcomingAlarmRequest(
    val medicineName: String,
    val time: String
)

data class MissedAlarmsRequest(
    val name: String,
    val time: String
)

data class MedicineAlarmRecordsRequest(
    val name: String,
    val time: String,
    val category: String,
    val isEaten: Boolean,
    val medicineId: Int,
    val alarmId: Int
)

data class RemainingMedicineRequest(
    val name: String,
    val category: String,
    val day: Int
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
    @SerializedName("oldMedicineName")
    val oldMedicineName: String,
    @SerializedName("newMedicineName")
    val newMedicineName: String,
    @SerializedName("category")
    val category: String,
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

data class MedicineInfo(
    val mediName: String,
    val category: String,
    val oneEat: Int,
    val oneDay: Int,
    val allDay: Int,
    val timeSlotList: List<TimeSlotRequest>
) : Serializable

data class HealthInfoResponse(
    val diseases: List<DiseaseInfo>,
    val symptoms: List<SymptomInfo>
)

data class DiseaseInfo(
    val id: Int,
    val disease: String,
    val startDate: String
)

data class SymptomInfo(
    val id: Int,
    val name: String
)

data class MediScanResponse(
    val name: String,
    val photo: String,
    val category: String
)