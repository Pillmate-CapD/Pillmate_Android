package com.example.pillmate

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //val token = App.prefs.token

        // 김가현 임시토큰 넣어놓음 1234@naver.com , 234
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIzIiwiaWF0IjoxNzI2NjcyMDk5LCJleHAiOjE3MjY3NTg0OTksImF1dGgiOlt7ImF1dGhvcml0eSI6IlVTRVIifV19.LLfInKajyI9m5hP9-HX2aCzR4vWA_Bongqwpt8hYJNQ"
        val requestBuilder = chain.request().newBuilder()

        // 토큰이 존재하는 경우에만 Authorization 헤더 추가
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()

        // 로그에 헤더 정보 출력
        Log.d("AuthInterceptor", "Request Headers: ${request.headers().toMultimap()}")

        return chain.proceed(request)
    }
}