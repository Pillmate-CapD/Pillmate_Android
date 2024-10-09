package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pillmate.R
import com.example.pillmate.databinding.ActivityPwchange1Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PwChange1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPwchange1Binding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwchange1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 비밀번호 가시성 토글 설정
        binding.pwToggle1.setOnClickListener {
            togglePasswordVisibility(binding.etPwnow, binding.pwToggle1, isPasswordVisible)
            isPasswordVisible = !isPasswordVisible // 상태 변경
        }

        // '다음' 버튼 활성화 및 토글 가시성 제어
        binding.etPwnow.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 비밀번호 입력 필드에 텍스트가 있을 경우
                if (s?.isNotEmpty() == true) {
                    binding.pwToggle1.visibility = View.VISIBLE // 토글 버튼 보이기
                    binding.btnNextt.isEnabled = true
                    binding.btnNextt.background = ContextCompat.getDrawable(this@PwChange1Activity, R.drawable.bt_login_enable)
                    binding.btnNextt.setTextColor(ContextCompat.getColor(this@PwChange1Activity, R.color.white))
                } else {
                    binding.pwToggle1.visibility = View.GONE // 토글 버튼 숨기기
                    binding.btnNextt.isEnabled = false
                    binding.btnNextt.background = ContextCompat.getDrawable(this@PwChange1Activity, R.drawable.bt_login_disable)
                    binding.btnNextt.setTextColor(Color.parseColor("#898989"))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // '다음' 버튼 기본 비활성화
        binding.btnNextt.isEnabled = false

        // 뒤로 가기 버튼 클릭 시 이전 페이지로 이동
        binding.pwcBack.setOnClickListener {
            finish()  // 현재 액티비티 종료하고 이전 액티비티로 돌아감
        }

        // '다음' 버튼 클릭 시 API 호출
        binding.btnNextt.setOnClickListener {
            val currentPassword = binding.etPwnow.text.toString()
            // 비밀번호 확인 API 호출
            checkPassword(currentPassword)
        }
    }

    // 비밀번호 가시성 토글 함수
    private fun togglePasswordVisibility(editText: EditText, toggle: ImageView, isVisible: Boolean) {
        if (!isVisible) {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggle.setImageResource(R.drawable.login_eyeoff)
        } else {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT
            toggle.setImageResource(R.drawable.login_eyeon)
        }
        editText.setSelection(editText.text.length)  // 커서 위치 유지
    }

    private fun checkPassword(password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // API 호출
                val isValid = RetrofitApi.getRetrofitService.checkPassword(PasswordCheckRequest(password))
                withContext(Dispatchers.Main) {
                    if (isValid) { // 수정된 부분
                        // 비밀번호가 맞으면 PwChangeActivity로 이동
                        val intent = Intent(this@PwChange1Activity, PwChangeActivity::class.java)
                        intent.putExtra("oldPassword", password) // oldPassword 전달
                        startActivity(intent)
                    } else {
                        // 비밀번호가 틀리면 에러 메시지 표시
                        binding.pwErrorm1.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("PwChange1Activity", "API call failed: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PwChange1Activity, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}

