package com.example.pillmate

import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate

object RetrofitApi {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()

    private const val BASE_URL = "https://www.pillmate.shop/api/v1/"
    private const val BASE_URL_MEDI = "https://c784-34-147-64-158.ngrok-free.app/"

    // Gson 빌더 설정 (lenient 모드 활성화)
    private val gson = GsonBuilder().registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
        JsonPrimitive(src.toString())
    })
        .setLenient()
        .create()

    private val getRetrofit by lazy{
        Retrofit.Builder()
            .client(okHttpClient) //토큰 인터셉터
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val getMediRetrofit by lazy{
        Retrofit.Builder()
            .client(okHttpClient) //토큰 인터셉터
            .baseUrl(BASE_URL_MEDI)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val getRetrofitService:RetrofitService by lazy{getRetrofit.create(RetrofitService::class.java)}
    val getMediRetrofitService: RetrofitService by lazy { getMediRetrofit.create(RetrofitService::class.java) }
}