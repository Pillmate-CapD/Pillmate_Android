package com.example.pillmate

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PATCH


interface RetrofitService {
    //로그인
    @POST("members/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 회원가입
    @POST("members/signup")
    fun signup(@Body request: SignUpRequest): Call<SignUpResponse>


    //로그아웃
    @POST("members/logout")
    suspend fun logout(): LogoutResponse


    //마이페이지 기존 비번 확인
    @POST("check/password")
    suspend fun checkPassword(
        @Body passwordCheckRequest: PasswordCheckRequest): PasswordCheckResponse

    //마이페이지 비번 변경 (new password)
    @PATCH("members/password")
    fun changePassword(@Body passwordChangeRequest: PasswordChangeRequest): Call<Void>

    @POST("medicines")
    fun addMedi(@Body request : MediAddRequest): Call<String>

    @GET("medicines/all")
    fun getMediAll() : Call<List<MediListResponse>>

    @PATCH("medicines")
    fun patchMedi(@Body request : MediEditRequest): Call<String>

    @GET("medicines/name")
    fun getMediInfo(@Body request: MediInfoRequest) : Call<MediInfoResponse>

    @POST("alarms")
    fun postAlarm(@Body request: AddAlarmRequest) : Call<String>

    // 로직 수정중
    @GET("alarms")
    fun getAlarm() : Call<AlarmListResponse>

    @GET("main")
    fun getMain() : Call<MainPageResponse>
}