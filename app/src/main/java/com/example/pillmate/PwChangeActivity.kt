package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pillmate.databinding.ActivityPwchangeBinding

class PwChangeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPwchangeBinding
    private var isPwVisible1 = false
    private var isPwVisible2 = false
    private var isPwVisible3 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivityPwchangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 버튼 기본 비활성화
        binding.btnRegister.isEnabled = false

        // 현재 비밀번호 토글 기능
        binding.pwToggle1.setOnClickListener {
            togglePasswordVisibility(binding.etPwnow, binding.pwToggle1, isPwVisible1)
            isPwVisible1 = !isPwVisible1
        }

        // 새 비밀번호 토글 기능
        binding.pwToggle2.setOnClickListener {
            togglePasswordVisibility(binding.etNewpw, binding.pwToggle2, isPwVisible2)
            isPwVisible2 = !isPwVisible2
        }

        // 비밀번호 확인 토글 기능
        binding.pwToggle3.setOnClickListener {
            togglePasswordVisibility(binding.etPasswordConfirm, binding.pwToggle3, isPwVisible3)
            isPwVisible3 = !isPwVisible3
        }

        // 텍스트 변경 리스너 설정
        binding.etPwnow.addTextChangedListener(textWatcher)
        binding.etNewpw.addTextChangedListener(textWatcher)
        binding.etPasswordConfirm.addTextChangedListener(textWatcher)
    }

    // 비밀번호 가시성 토글 함수
    private fun togglePasswordVisibility(editText: EditText, toggle: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggle.setImageResource(R.drawable.login_eyeoff)
        } else {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT
            toggle.setImageResource(R.drawable.login_eyeon)
        }
        editText.setSelection(editText.text.length)  // 커서 위치 유지
    }

    // 텍스트 변경 리스너
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateInputs()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    // 입력 검증 함수
    private fun validateInputs() {
        val currentPw = binding.etPwnow.text.toString()
        val newPw = binding.etNewpw.text.toString()
        val confirmPw = binding.etPasswordConfirm.text.toString()

        // 현재 비밀번호가 입력된 경우 토글 보이기
        if (currentPw.isNotEmpty()) {
            binding.pwToggle1.visibility = View.VISIBLE
        } else {
            binding.pwToggle1.visibility = View.INVISIBLE
        }

        // 새 비밀번호 조건 체크
        if (newPw.length >= 7 && newPw.matches(".*[a-zA-Z].*".toRegex()) && newPw.matches(".*[0-9].*".toRegex()) && newPw.matches(".*[!@#\$%^&*(),.?\":{}|<>].*".toRegex())) {
            binding.pwErrorm2.visibility = View.INVISIBLE
            binding.pwErrorm2.text = "7자 이상의 영문, 숫자, 특수문자 포함"
            binding.pwErrorm2.setTextColor(Color.parseColor("#C0C0C0"))
        } else {
            binding.pwErrorm2.visibility = View.VISIBLE
            binding.pwErrorm2.text = "7자 이상의 영문, 숫자, 특수문자를 입력해 주세요"
            binding.pwErrorm2.setTextColor(Color.parseColor("#DC1818"))

        }

        // 새 비밀번호와 비밀번호 확인 비교
        if (newPw == confirmPw && confirmPw.isNotEmpty()) {
            binding.pwErrorm3.visibility = View.INVISIBLE
        } else {
            binding.pwErrorm3.visibility = View.VISIBLE
        }

        // 입력 조건이 모두 만족하면 버튼 활성화
        val isFormValid = currentPw.isNotEmpty() && newPw.length >= 7 && newPw == confirmPw
        if (isFormValid) {
            binding.btnRegister.isEnabled = true
            binding.btnRegister.background = ContextCompat.getDrawable(this, R.drawable.bt_login_enable)
            binding.btnRegister.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnRegister.setOnClickListener {
                // 비밀번호 변경 완료 후 PwChangeOkActivity로 이동
                val intent = Intent(this, PwChangeOkActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.background = ContextCompat.getDrawable(this, R.drawable.bt_login_disable)
            binding.btnRegister.setTextColor(Color.parseColor("#898989"))
        }
    }
}//일단 새비밀번호랑 비밀번호 확인 부분 눈 아이콘 보이는거 수정 필요,