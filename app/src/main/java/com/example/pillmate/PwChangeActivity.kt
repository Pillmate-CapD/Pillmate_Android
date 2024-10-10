package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pillmate.databinding.ActivityPwchangeBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PwChangeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPwchangeBinding
    private var isPwVisible2 = false
    private var isPwVisible3 = false
    private lateinit var oldPassword: String // 멤버 변수로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivityPwchangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // PwChange1Activity에서 oldPassword 전달 받음
        oldPassword = intent.getStringExtra("oldPassword") ?: ""
        Log.d("PwChangeActivity", "Received oldPassword: $oldPassword")

        // 뒤로 가기 버튼 클릭 시 이전 페이지로 이동
        binding.pwcBack.setOnClickListener {
            finish()  // 현재 액티비티 종료하고 이전 액티비티로 돌아감
        }

        // 버튼 기본 비활성화
        binding.btnRegister.isEnabled = false

        // 새 비밀번호 조건 문구를 기본적으로 보이게 설정
        binding.pwErrorm2.visibility = View.VISIBLE
        binding.pwErrorm2.text = "7자 이상의 영문, 숫자, 특수문자 포함"
        binding.pwErrorm2.setTextColor(Color.parseColor("#C0C0C0"))

        // 비밀번호 확인 오류 문구를 기본적으로 숨김
        binding.pwErrorm3.visibility = View.INVISIBLE
        binding.pwErrorm3.text = "비밀번호가 일치하지 않습니다."
        binding.pwErrorm3.setTextColor(Color.parseColor("#DC1818"))

        // 새 비밀번호 토글 기능 설정
        binding.pwToggle2.visibility = View.GONE // 초기에는 숨김
        binding.etNewpw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 한 글자 이상 입력되면 토글 아이콘 표시
                binding.pwToggle2.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.GONE
                // 새 비밀번호 조건 문구 및 텍스트 색상 변경
                updatePasswordCondition(s.toString())
                // 비밀번호 확인 필드도 업데이트
                handlePasswordConfirm(binding.etPasswordConfirm.text.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.pwToggle2.setOnClickListener {
            isPwVisible2 = !isPwVisible2
            togglePasswordVisibility(binding.etNewpw, binding.pwToggle2, isPwVisible2)
        }

        // 비밀번호 확인 토글 기능 설정
        binding.pwToggle3.visibility = View.GONE // 초기에는 숨김
        binding.etPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 한 글자 이상 입력되면 토글 아이콘 표시
                binding.pwToggle3.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.GONE
                // 비밀번호 확인 오류 문구 처리
                handlePasswordConfirm(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.pwToggle3.setOnClickListener {
            isPwVisible3 = !isPwVisible3
            togglePasswordVisibility(binding.etPasswordConfirm, binding.pwToggle3, isPwVisible3)
        }

        // 텍스트 변경 리스너 설정
        binding.etNewpw.addTextChangedListener(newPwWatcher)
        binding.etPasswordConfirm.addTextChangedListener(confirmPwWatcher)

        // 전체 폼 검증
        validateForm()
    }

    // 비밀번호 가시성 토글 함수
    private fun togglePasswordVisibility(editText: EditText, toggle: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggle.setImageResource(R.drawable.login_eyeon)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggle.setImageResource(R.drawable.login_eyeoff)
        }
        editText.setSelection(editText.text.length)  // 커서 위치 유지
    }

    // 새 비밀번호 조건 문구 및 텍스트 색상 업데이트 함수
    private fun updatePasswordCondition(password: String) {
        val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{7,}\$")

        when {
            password.isEmpty() -> {
                binding.pwErrorm2.text = "7자 이상의 영문, 숫자, 특수문자 포함"
                binding.pwErrorm2.setTextColor(Color.parseColor("#C0C0C0"))
                binding.etNewpw.setTextColor(Color.parseColor("#C0C0C0"))
            }
            passwordPattern.containsMatchIn(password) -> {
                binding.pwErrorm2.text = "7자 이상의 영문, 숫자, 특수문자 포함"
                binding.pwErrorm2.setTextColor(Color.parseColor("#08D2C8"))
                binding.etNewpw.setTextColor(Color.parseColor("#000000"))
            }
            else -> {
                binding.pwErrorm2.text = "7자 이상의 영문, 숫자, 특수문자를 입력해 주세요"
                binding.pwErrorm2.setTextColor(Color.parseColor("#DC1818"))
                binding.etNewpw.setTextColor(Color.parseColor("#DC1818"))
            }
        }
    }

    // 비밀번호 확인 오류 문구 및 텍스트 색상 처리 함수
    private fun handlePasswordConfirm(confirmPassword: String) {
        val newPassword = binding.etNewpw.text.toString()
        when {
            confirmPassword.isEmpty() -> {
                binding.pwErrorm3.visibility = View.INVISIBLE
                binding.etPasswordConfirm.setTextColor(Color.parseColor("#C0C0C0"))
            }
            confirmPassword == newPassword -> {
                binding.pwErrorm3.visibility = View.INVISIBLE
                binding.etPasswordConfirm.setTextColor(Color.parseColor("#000000"))
            }
            else -> {
                binding.pwErrorm3.visibility = View.VISIBLE
                binding.etPasswordConfirm.setTextColor(Color.parseColor("#DC1818"))
            }
        }
    }

    // 새 비밀번호 텍스트 변경 리스너
    private val newPwWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateForm()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    // 비밀번호 확인 텍스트 변경 리스너
    private val confirmPwWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateForm()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    // 전체 폼 검증 함수
    private fun validateForm() {
        val newPw = binding.etNewpw.text.toString()
        val confirmPw = binding.etPasswordConfirm.text.toString()

        // 비밀번호 조건: 최소 7자, 영문, 숫자, 특수문자 포함
        val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{7,}\$")
        val isPasswordValid = passwordPattern.containsMatchIn(newPw)
        val isConfirmPasswordValid = confirmPw.isNotEmpty() && confirmPw == newPw
        val isDifferentFromOld = newPw != oldPassword
        val isFormValid = isPasswordValid && isConfirmPasswordValid && isDifferentFromOld

        // 새 비밀번호 조건 문구 및 텍스트 색상 업데이트
        updatePasswordCondition(newPw)

        // 비밀번호 확인 오류 문구 및 텍스트 색상 업데이트
        if (confirmPw.isNotEmpty()) {
            if (isConfirmPasswordValid) {
                binding.pwErrorm3.visibility = View.INVISIBLE
                binding.etPasswordConfirm.setTextColor(Color.parseColor("#000000"))
            } else {
                binding.pwErrorm3.visibility = View.VISIBLE
                binding.etPasswordConfirm.setTextColor(Color.parseColor("#DC1818"))
            }
        } else {
            binding.pwErrorm3.visibility = View.INVISIBLE
            binding.etPasswordConfirm.setTextColor(Color.parseColor("#C0C0C0"))
        }

        // 입력 조건이 모두 만족하면 버튼 활성화
        if (isFormValid) {
            binding.btnRegister.isEnabled = true
            binding.btnRegister.background = ContextCompat.getDrawable(this, R.drawable.bt_login_enable)
            binding.btnRegister.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnRegister.setOnClickListener {
                val newPassword = binding.etNewpw.text.toString()

                // 비밀번호 변경 API 호출
                changePassword(oldPassword, newPassword)
            }
        } else {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.background = ContextCompat.getDrawable(this, R.drawable.bt_login_disable)
            binding.btnRegister.setTextColor(Color.parseColor("#898989"))
        }
    }

    // 비밀번호 변경 API 호출 함수
    private fun changePassword(oldPassword: String, newPassword: String) {
        Log.d("PwChangeActivity", "Changing password. Old Password: $oldPassword, New Password: $newPassword")
        val request = PasswordChangeRequest(oldPassword, newPassword)
        val call = RetrofitApi.getRetrofitService.changePassword(request)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("PwChangeActivity", "Password changed successfully")
                    // 성공 시 PwChangeOkActivity로 이동
                    val intent = Intent(this@PwChangeActivity, PwChangeOkActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PwChangeActivity", "Password change failed: $errorBody")
                    // 실패 시 사용자에게 알림 (예: 토스트 메시지)
                    runOnUiThread {
                        val message = parseErrorMessage(errorBody)
                        Toast.makeText(this@PwChangeActivity, "비밀번호 변경에 실패했습니다: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("PwChangeActivity", "API call failed", t)
                // 실패 시 사용자에게 알림 (예: 토스트 메시지)
                runOnUiThread {
                    Toast.makeText(this@PwChangeActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // 서버에서 받은 오류 메시지를 파싱하여 사용자에게 보여줄 메시지를 반환하는 함수
    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val json = JSONObject(errorBody)
            val code = json.getString("code")
            when (code) {
                "MISMATCH_PASSWORD" -> "기존 비밀번호가 일치하지 않습니다."
                "SAME_PASSWORD" -> "새 비밀번호가 기존 비밀번호와 동일합니다."
                else -> "알 수 없는 오류가 발생했습니다."
            }
        } catch (e: Exception) {
            "알 수 없는 오류가 발생했습니다."
        }
    }
}




