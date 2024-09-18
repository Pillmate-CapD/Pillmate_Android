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
}