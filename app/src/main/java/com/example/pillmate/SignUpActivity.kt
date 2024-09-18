package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.pillmate.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var isEmailValid = false
    private var isPasswordValid = false
    private var isPasswordConfirmed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼 클릭 시 로그인 액티비티로 이동
        binding.ivBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        // 이메일 형식 확인 및 중복 확인 버튼
        binding.btnEmailCheck.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // 올바르지 않은 이메일 형식
                binding.emailcheckErrorm.text = "올바른 이메일을 입력해 주세요"
                binding.emailcheckErrorm.visibility = View.VISIBLE
                isEmailValid = false
            } else if (isEmailDuplicated(email)) {
                // 중복된 이메일
                binding.emailcheckErrorm.text = "이메일 중복 확인이 필요합니다"
                binding.emailcheckErrorm.visibility = View.VISIBLE
                isEmailValid = false
            } else {
                // 올바른 이메일
                binding.emailcheckErrorm.visibility = View.INVISIBLE
                binding.btnEmailCheck.text = "확인 완료"
                binding.btnEmailCheck.isEnabled = false
                binding.btnEmailCheck.setBackgroundResource(R.drawable.bt_signup_check_c) // 배경 변경
                binding.btnEmailCheck.setTextColor(ContextCompat.getColor(this, R.color.selected_icon_color)) // 텍스트 색상 변경
                isEmailValid = true
            }
            updateRegisterButtonState()
        }

        // 비밀번호 입력 시 유효성 검사
        binding.etPassword.addTextChangedListener {
            val password = binding.etPassword.text.toString()
            if(password.isEmpty()){
                //아무 입력 없는 경우
                binding.passwordErrorm.text = "7자 이상의 영문, 숫자, 특수문자 포함"
                binding.passwordErrorm.visibility = View.VISIBLE
                binding.passwordErrorm.setTextColor(Color.parseColor("#C0C0C0"))
                binding.passwordToggle1.visibility = View.INVISIBLE
                isPasswordValid = false
            } else if (isValidPassword(password)) {
                // 유효한 비밀번호
                binding.passwordErrorm.text = "7자 이상의 영문, 숫자, 특수문자 포함"
                binding.passwordErrorm.setTextColor(Color.parseColor("#C0C0C0"))
                binding.passwordErrorm.visibility = View.VISIBLE
                binding.passwordToggle1.visibility = View.VISIBLE
                isPasswordValid = true
            } else {
                // 유효하지 않은 비밀번호
                binding.passwordErrorm.text = "7자 이상의 영문, 숫자, 특수문자를 입력해 주세요"
                binding.passwordErrorm.visibility = View.VISIBLE
                binding.passwordErrorm.setTextColor(Color.parseColor("#DC1818"))
                binding.passwordToggle1.visibility = View.VISIBLE
                isPasswordValid = false
            }
            updateRegisterButtonState()
        }

        // 비밀번호 토글 버튼
        binding.passwordToggle1.setOnClickListener {
            togglePasswordVisibility(binding.etPassword, binding.passwordToggle1)
        }

        // 비밀번호 확인 입력 시 일치 여부 확인
        binding.etPasswordConfirm.addTextChangedListener {
            val confirmPassword = binding.etPasswordConfirm.text.toString()
            if (confirmPassword.isEmpty()) {
                binding.passwordConfirmErrorm.visibility = View.INVISIBLE
                isPasswordConfirmed = false
                binding.passwordToggle2.visibility = View.INVISIBLE
            } else if (confirmPassword == binding.etPassword.text.toString()) {
                binding.passwordConfirmErrorm.visibility = View.INVISIBLE
                binding.passwordToggle2.visibility = View.VISIBLE
                isPasswordConfirmed = true
            } else {
                binding.passwordConfirmErrorm.visibility = View.VISIBLE
                binding.passwordToggle2.visibility = View.VISIBLE
                isPasswordConfirmed = false
            }
            updateRegisterButtonState()
        }

        // 비밀번호 확인 토글 버튼
        binding.passwordToggle2.setOnClickListener {
            togglePasswordVisibility(binding.etPasswordConfirm, binding.passwordToggle2)
        }

        // 회원가입 버튼 클릭 시
        binding.btnRegister.setOnClickListener {
            if (isEmailValid && isPasswordValid && isPasswordConfirmed) {
                // 사용자 입력 정보 가져오기
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val name = binding.etName.text.toString()

                // 임시로 설정한 시간 값 (실제 구현 시 사용자가 입력한 값을 넣으면 됨)
                val wakeUp = "07:00"
                val morning = "08:00"
                val lunch = "12:00"
                val dinner = "18:00"
                val bed = "22:00"

                // 질병 및 역할 목록
                val diseases = listOf(DiseaseData("고혈압", "2020-01-01"))
                val roles = listOf("USER")

                // 회원가입 요청 데이터 생성
                val signUpRequest = SignUpRequest(
                    email = email,
                    password = password,
                    name = name,
                    wakeUp = wakeUp,
                    morning = morning,
                    lunch = lunch,
                    dinner = dinner,
                    bed = bed,
                    diseases = diseases,
                    roles = roles
                )

                // Retrofit API 호출
                val call = RetrofitApi.getRetrofitService.signup(signUpRequest)

                // 비동기적으로 회원가입 요청 보내기
                call.enqueue(object : retrofit2.Callback<SignUpResponse> {
                    override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                        if (response.isSuccessful) {
                            // 회원가입 성공 처리 (예: 다음 액티비티로 이동)
                            val signUpResponse = response.body()
                            val accessToken = signUpResponse?.accessToken
                            // 성공 시 액세스 토큰을 로그로 출력하거나 저장 (필요 시)
                            Log.d("SignUp", "회원가입 성공. AccessToken: $accessToken")
                            // 다른 화면으로 이동 (예: 메인 화면)
                            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // 실패 시 처리 (예: 에러 메시지 표시)
                            Log.e("SignUp", "회원가입 실패: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                        // 네트워크 또는 요청 실패 시 처리
                        Log.e("SignUp", "회원가입 요청 실패: ${t.message}")
                    }
                })
            }
        }
    }

    // 이메일 중복 확인 (서버 로직과 연동 필요)
    private fun isEmailDuplicated(email: String): Boolean {
        return false // 중복 확인 로직은 서버 연동 필요
    }

    // 비밀번호 유효성 검사 함수
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#\$%^&+=]).{7,}$"
        return password.matches(Regex(passwordPattern))
    }

    // 비밀번호 토글 (보이기/숨기기)
    private fun togglePasswordVisibility(editText: EditText, toggleButton: ImageView) {
        if (editText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleButton.setImageResource(R.drawable.login_eyeon)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleButton.setImageResource(R.drawable.login_eyeoff)
        }
        editText.setSelection(editText.text.length) // 커서 위치 조정
    }

    // 모든 조건 만족 시 가입 버튼 활성화
    private fun updateRegisterButtonState() {
        binding.btnRegister.isEnabled = isEmailValid && isPasswordValid && isPasswordConfirmed
        if (binding.btnRegister.isEnabled) {
            binding.btnRegister.setBackgroundResource(R.drawable.bt_login_enable)
            binding.btnRegister.setTextColor(Color.WHITE)
        } else {
            binding.btnRegister.setBackgroundResource(R.drawable.bt_login_disable)
        }
    }
}
