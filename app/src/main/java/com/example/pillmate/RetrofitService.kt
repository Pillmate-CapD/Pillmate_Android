package com.example.pillmate

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET


interface RetrofitService {
    // 회원가입
    @POST("members/signup")
    fun signup(@Body request: SignUpRequest): Call<SignUpResponse>

    @POST("medicines/directly")
    fun addMedi(@Body request : MediAddRequest): Call<String>

    @GET("medicines/all")
    fun getMediAll() : Call<List<MediListResponse>>

    @POST("alarms")
    fun postAlarm(@Body request: AddAlarmRequest) : Call<String>

    // 로직 수정중
    @GET("alarms")
    fun getAlarm() : Call<AlarmListResponse>

    @GET("main")
    fun getMain() : Call<MainPageResponse>
}