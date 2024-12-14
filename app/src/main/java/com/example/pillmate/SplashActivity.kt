package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // SharedPreferences에서 온보딩 실행 횟수 확인
        val preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        preferences.edit().putInt("onboardingCount", 0).apply()
        val onboardingCount = preferences.getInt("onboardingCount", 0)

        //val isFirstLaunch = preferences.getBoolean("isFirstLaunch", true)

        Handler(Looper.getMainLooper()).postDelayed({
            if (onboardingCount < 2) {
                // 첫 실행 플래그 업데이트
                preferences.edit().putInt("onboardingCount", onboardingCount + 1).apply()

                // 온보딩 화면으로 이동
                navigateToOnon()
            } else {
                // SharedPreferences에서 자동 로그인 정보 확인
                val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
                val autoLoginUse = auto.getBoolean("autoLoginUse", false)
                val autoId = auto.getString("Id", null)
                val autoPw = auto.getString("Pw", null)

                // 자동 로그인 정보가 있을 경우 처리
                if (autoLoginUse && !autoId.isNullOrEmpty() && !autoPw.isNullOrEmpty()) {
                    val requestBodyData = LoginRequest(autoId, autoPw)

                    // 자동 로그인 처리 (Retrofit을 통한 API 호출)
                    RetrofitApi.getRetrofitService.login(requestBodyData).enqueue(object :
                        Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()

                                if (loginResponse != null) {
                                    val accessToken = "Bearer ${loginResponse.tokenInfo.accessToken}"
                                    Log.d("SplashActivity", "자동 로그인 성공! 액세스 토큰: $accessToken")

                                    // 로그인 성공 후 메인 액티비티로 이동
                                    navigateToMain()
                                }
                            } else {
                                // 로그인 실패 시 로그인 화면으로 이동
                                Log.e("SplashActivity", "자동 로그인 실패: ${response.code()} - ${response.message()}")
                                navigateToLogin()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            // 네트워크 오류 등 예외 처리
                            Log.e("SplashActivity", "네트워크 오류: ${t.message}")
                            navigateToLogin()
                        }
                    })
                } else {
                    // 자동 로그인 정보가 없을 경우 로그인 화면으로 이동
                    navigateToLogin()
                }
            }
        }, 2000) // 2초 후 실행
    }

    // 온보딩 화면으로 이동하는 함수
    private fun navigateToOnon() {
        val intent = Intent(this, OnOnActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    // 로그인 화면으로 이동하는 함수
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 메인 화면으로 이동하는 함수
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
