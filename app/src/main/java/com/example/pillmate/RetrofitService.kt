package com.example.pillmate

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface RetrofitService {
    //로그인
    @POST("members/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 회원가입
    @POST("members/signup")
    fun signup(@Body request: SignUpRequest): Call<SignUpResponse>

    //이메일 중복 체크
    @POST("members/check/email")
    suspend fun checkEmail(@Body request: EmailCheckRequest): Response<EmailCheckResponse>

    //로그아웃
    @POST("members/logout")
    suspend fun logout(): LogoutResponse


    //마이페이지 기존 비번 확인
    @POST("members/check/password")
    suspend fun checkPassword(
        @Body passwordCheckRequest: PasswordCheckRequest
    ): Boolean
    //@POST("members/check/password")
    //suspend fun checkPassword(
    //@Body passwordCheckRequest: PasswordCheckRequest): PasswordCheckResponse

    //마이페이지 비번 변경 (new password)
    @PATCH("members/password")
    fun changePassword(@Body passwordChangeRequest: PasswordChangeRequest): Call<Void>

    @POST("medicines")
    fun addMedi(@Body request : MediAddRequest): Call<String>

    @GET("medicines/all")
    fun getMediAll() : Call<List<MediListResponse>>

    @PATCH("medicines")
    fun patchMedi(@Body request : MediEditRequest): Call<String>

    @POST("medicines/name")
    fun postMediInfo(@Body request: List<MediInfoRequest>) : Call<List<MediInfoResponse>>

//    @POST("alarms")
//    fun postAlarm(@Body request: AddAlarmRequest) : Call<String>

    // 로직 수정중
    @GET("alarms")
    fun getAlarm() : Call<List<AlarmListResponse>>

    @GET("main")
    fun getMain(@Query(value = "time", encoded = true) time: String) : Call<MainPageResponse>

    @DELETE("medicines")
    fun delMedicine( @Query("medicineId") medicineId: Int) : Call<String>

    // 알람 True->False 현재 수정중
    @PATCH("alarms/{alarmId}/{available}")
    fun patchAlarm(
        @Path("alarmId") alarmId: Int,   // 알람 ID를 경로로 전달
        @Path("available") isAvailable: Boolean // 알람 상태 (true/false)를 경로로 전달
    ): Call<String>

    @GET("members/healthinfo")
    fun getHealthInfo() : Call<HealthInfoResponse>


    @Multipart
    @POST("predict")
    fun postScanMedi(
        @Part file: MultipartBody.Part // 파일 전송을 위한 파라미터
    ): Call<MediScanResponse>
}