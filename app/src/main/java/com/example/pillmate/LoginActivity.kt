package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pillmate.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false
    private var isAutoLoginChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        App.prefs.token = null

        val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
        val autoLoginUse = auto.getBoolean("autoLoginUse", false)

        if(autoLoginUse){
            val autoId = auto.getString("Id", "")
            val autoPw = auto.getString("Pw", "")
            val requestBodyData = LoginRequest(autoId!!,autoPw!!)

            RetrofitApi.getRetrofitService.login(requestBodyData).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()

                        // 가현 추가 중
                        if(loginResponse != null){
                            val accessToken = "Bearer ${loginResponse.tokenInfo.accessToken}"
                            Log.d("LoginActivity", "로그인 성공! 액세스 토큰: $accessToken")

                            val userName = loginResponse.tokenInfo.name
                            Log.e("userName", "${userName}")

                            val preferences = getSharedPreferences("userName", MODE_PRIVATE)
                            val editor = preferences.edit()

                            editor.putString("userName", userName)

                            editor.commit()

                            App.prefs.token = accessToken

                            Log.d("Login_Token", "Access Token : ${accessToken}")

                            // MainActivity로 이동
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // 로그인 실패 처리
                        showError()
                        Log.e("LoginActivity", "로그인 실패: ${response.code()} - ${response.message()}")
                        Log.e("LoginActivity", "에러 응답: ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // 네트워크 오류 등 예외 처리
                    Log.e("LoginActivity", "네트워크 오류: ${t.message}")
                    Toast.makeText(this@LoginActivity, "로그인 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 초기 로그인 버튼 비활성화
        binding.btnLogin.isEnabled = false

        // 이메일, 비번 입력시 로그인 버튼 활성화
        binding.etEmail.addTextChangedListener(loginTextWatcher)
        binding.passwordEditText.addTextChangedListener(loginTextWatcher)

        // 자동 로그인 체크 이벤트
        binding.ivAutoLogin.setOnClickListener {
            toggleAutoLogin()
        }

        // 회원가입 클릭 이벤트
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)  // 회원가입 액티비티로 이동
            startActivity(intent)
        }

        // 비밀번호 보기/숨기기 아이콘 클릭 이벤트
        binding.passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }

        // 이메일, 비밀번호 다시 입력 시 배경 복구 및 오류 메시지 숨김
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                resetBackground()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                resetBackground()
                if (binding.passwordEditText.text.isNotEmpty()) {
                    binding.passwordToggle.visibility = View.VISIBLE
                } else {
                    binding.passwordToggle.visibility = View.INVISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 로그인 버튼 클릭 이벤트
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
    }

    // 텍스트 입력 확인 후 로그인 버튼 활성화 처리
    private val loginTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val emailInput = binding.etEmail.text.toString().trim()
            val passwordInput = binding.passwordEditText.text.toString().trim()

            binding.btnLogin.isEnabled = emailInput.isNotEmpty() && passwordInput.isNotEmpty()

            // 버튼 활성화 시 색상 변경
            if (binding.btnLogin.isEnabled) {
                binding.btnLogin.setBackgroundResource(R.drawable.bt_login_enable)
                binding.btnLogin.setTextColor(Color.WHITE)
            } else {
                binding.btnLogin.setBackgroundResource(R.drawable.bt_login_disable)
                binding.btnLogin.setTextColor(Color.GRAY)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // 자동 로그인 토글 처리
    private fun toggleAutoLogin() {
        isAutoLoginChecked = !isAutoLoginChecked
        if (isAutoLoginChecked) {
            binding.ivAutoLogin.setImageResource(R.drawable.autologincheck_after)
        } else {
            binding.ivAutoLogin.setImageResource(R.drawable.autologincheck_before)
        }
    }

    // 비밀번호 보기/숨기기 토글 처리
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.login_eyeoff)
        } else {
            binding.passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT
            binding.passwordToggle.setImageResource(R.drawable.login_eyeon)
        }
        isPasswordVisible = !isPasswordVisible
        binding.passwordEditText.setSelection(binding.passwordEditText.length())  // 커서를 끝으로 이동
    }

    // 로그인 버튼 클릭 시 처리
    private fun performLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.passwordEditText.text.toString()

        // 로그인 요청 데이터 생성
        val loginRequest = LoginRequest(email, password)

        // Retrofit을 이용한 로그인 API 호출
        RetrofitApi.getRetrofitService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    // 가현 추가 중
                    if(loginResponse != null){
                        val accessToken = "Bearer ${loginResponse.tokenInfo.accessToken}"
                        Log.d("LoginActivity", "로그인 성공! 액세스 토큰: $accessToken")

                        val userName = loginResponse.tokenInfo.name
                        Log.e("Login_Click","Login_Btn_Click: $userName",)

                        if (isAutoLoginChecked){
                            val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
                            val autoLoginEdit = auto.edit()

                            autoLoginEdit.putBoolean("autoLoginUse", true)
                            autoLoginEdit.putString("Id", email)
                            autoLoginEdit.putString("Pw", password)
                            autoLoginEdit.commit()
                        }

                        Log.e("userName","${userName}")

                        val preferences = getSharedPreferences("userName", MODE_PRIVATE)
                        val editor = preferences.edit()

                        editor.putString("userName", userName)
                        editor.commit()

                        App.prefs.token = accessToken

                        // MainActivity로 이동
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    // 로그인 성공 처리 (토큰 정보 저장 등)
//                    val accessToken = loginResponse?.tokenInfo?.accessToken
//                    // MainActivity로 이동
//                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
                } else {
                    // 로그인 실패 처리
                    showError()
                    Log.e("LoginActivity", "로그인 실패: ${response.code()} - ${response.message()}")
                    Log.e("LoginActivity", "에러 응답: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 네트워크 오류 등 예외 처리
                Log.e("LoginActivity", "네트워크 오류: ${t.message}")
                Toast.makeText(this@LoginActivity, "로그인 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showError() {
        binding.email.setBackgroundResource(R.drawable.login_email_btn_wrong)
        binding.password.setBackgroundResource(R.drawable.login_password_btn_wrong)
        binding.tvErrorMessage.visibility = View.VISIBLE
    }

    // 이메일, 비밀번호 입력 시 배경 복구
    private fun resetBackground() {
        binding.email.setBackgroundResource(R.drawable.login_email_btn)
        binding.password.setBackgroundResource(R.drawable.login_password_btn)
        binding.tvErrorMessage.visibility = View.INVISIBLE
    }

    // 유효한 이메일, 비밀번호인지 확인 (서버로 실제 요청하는 부분은 서버와 연동 시 작성)
    //private fun isValidCredentials(email: String, password: String): Boolean {
        // 예시로 이메일, 비밀번호를 간단히 체크 (실제 환경에서는 서버와의 연동 필요)
    //    return email == "test@gmail.com" && password == "passwd123"
    //}
}