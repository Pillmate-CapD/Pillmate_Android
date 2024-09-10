package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pillmate.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false
    private var isAutoLoginChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // 이메일, 비밀번호 일치하지 않을 경우
        if (!isValidCredentials(email, password)) {
            // 이메일과 비밀번호 배경색 변경
            binding.email.setBackgroundResource(R.drawable.login_email_btn_wrong)
            binding.password.setBackgroundResource(R.drawable.login_password_btn_wrong)

            // 오류 메시지 표시
            binding.tvErrorMessage.visibility = View.VISIBLE
        } else {
            // 로그인 성공 처리
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            // 다른 액티비티로 이동 등 추가 로직
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // 이메일, 비밀번호 입력 시 배경 복구
    private fun resetBackground() {
        binding.email.setBackgroundResource(R.drawable.login_email_btn)
        binding.password.setBackgroundResource(R.drawable.login_password_btn)
        binding.tvErrorMessage.visibility = View.INVISIBLE
    }

    // 유효한 이메일, 비밀번호인지 확인 (서버로 실제 요청하는 부분은 서버와 연동 시 작성)
    private fun isValidCredentials(email: String, password: String): Boolean {
        // 예시로 이메일, 비밀번호를 간단히 체크 (실제 환경에서는 서버와의 연동 필요)
        return email == "test@gmail.com" && password == "passwd123"
    }
}