package com.example.pillmate

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitApi {
    private const val BASE_URL = "http://localhost:8080/api/v1"
    private val getRetrofit by lazy{
        Retrofit.Builder()
//            .client(okHttpClient) //토큰 인터셉터
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val getRetrofitService:RetrofitService by lazy{getRetrofit.create(RetrofitService::class.java)}
}