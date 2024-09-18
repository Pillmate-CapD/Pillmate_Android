package com.example.pillmate

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    // 회원가입
    @POST("/members/signup")
    fun signup(@Body request: SignUpRequest): Call<SignUpResponse>
}
