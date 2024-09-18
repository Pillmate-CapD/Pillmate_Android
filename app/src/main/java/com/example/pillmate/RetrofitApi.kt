package com.example.pillmate

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitApi {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()

    private const val BASE_URL = "http://ec2-13-209-62-93.ap-northeast-2.compute.amazonaws.com:8080/api/v1/"
    private val getRetrofit by lazy{
        Retrofit.Builder()
            .client(okHttpClient) //토큰 인터셉터
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val getRetrofitService:RetrofitService by lazy{getRetrofit.create(RetrofitService::class.java)}
}