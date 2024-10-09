package com.example.pillmate

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // 현재 요청의 URL
        val requestUrl = chain.request().url().toString()

        // 회원가입 API 호출 시 Authorization 헤더를 추가하지 않음
        if (!requestUrl.contains("members/signup")) {
            val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzI4NDcyMTcxLCJleHAiOjE3Mjg1NTg1NzEsImF1dGgiOlt7ImF1dGhvcml0eSI6IlVTRVIifV19.ZfJ1t3oGwlf4RYWY9SJ7E9AvKMuV1TtIP3SAm1xCraw"
            // 토큰이 존재하는 경우에만 Authorization 헤더 추가
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        val request = requestBuilder.build()

        // 로그에 헤더 정보 출력
        Log.d("AuthInterceptor", "Request Headers: ${request.headers().toMultimap()}")

        return chain.proceed(request)
    }
}