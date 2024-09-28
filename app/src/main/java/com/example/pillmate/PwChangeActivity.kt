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
        binding.etPwnow.addTextChangedListener(currentPwWatcher)
        binding.etNewpw.addTextChangedListener(newPwWatcher)
        binding.etPasswordConfirm.addTextChangedListener(confirmPwWatcher)
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

    // 현재 비밀번호 텍스트 변경 리스너
    private val currentPwWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateCurrentPw()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    // 새 비밀번호 텍스트 변경 리스너
    private val newPwWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateNewPw()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    // 비밀번호 확인 텍스트 변경 리스너
    private val confirmPwWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateConfirmPw()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    // 현재 비밀번호 검증 함수
    private fun validateCurrentPw() {
        val currentPw = binding.etPwnow.text.toString()

        // 현재 비밀번호가 입력된 경우 토글 보이기
        binding.pwToggle1.visibility = if (currentPw.isNotEmpty()) View.VISIBLE else View.INVISIBLE

        // 전체 폼 검증
        validateForm()
    }

    // 새 비밀번호 검증 함수
    private fun validateNewPw() {
        val newPw = binding.etNewpw.text.toString()

        // 새 비밀번호 조건 체크
        if (newPw.length >= 7 && newPw.matches(".*[a-zA-Z].*".toRegex()) && newPw.matches(".*[0-9].*".toRegex()) && newPw.matches(".*[!@#\$%^&*(),.?\":{}|<>].*".toRegex())) {
            binding.pwErrorm2.visibility = View.VISIBLE
            binding.pwErrorm2.text = "7자 이상의 영문, 숫자, 특수문자 포함"
            binding.pwErrorm2.setTextColor(Color.parseColor("#C0C0C0"))

        } else {
            binding.pwErrorm2.visibility = View.VISIBLE
            binding.pwErrorm2.text = "7자 이상의 영문, 숫자, 특수문자를 입력해 주세요"
            binding.pwErrorm2.setTextColor(Color.parseColor("#DC1818"))
        }

        // 새 비밀번호 토글 표시
        binding.pwToggle2.visibility = if (newPw.isNotEmpty()) View.VISIBLE else View.INVISIBLE

        // 전체 폼 검증
        validateForm()
    }

    // 비밀번호 확인 검증 함수
    private fun validateConfirmPw() {
        val newPw = binding.etNewpw.text.toString()
        val confirmPw = binding.etPasswordConfirm.text.toString()

        // 새 비밀번호와 비밀번호 확인 비교
        if (confirmPw == newPw && confirmPw.isNotEmpty()) {
            binding.pwErrorm3.visibility = View.INVISIBLE
        } else {
            binding.pwErrorm3.visibility = View.VISIBLE
        }

        // 비밀번호 확인 토글 표시
        binding.pwToggle3.visibility = if (confirmPw.isNotEmpty()) View.VISIBLE else View.INVISIBLE

        // 전체 폼 검증
        validateForm()
    }

    // 전체 폼 검증 함수
    private fun validateForm() {
        val currentPw = binding.etPwnow.text.toString()
        val newPw = binding.etNewpw.text.toString()
        val confirmPw = binding.etPasswordConfirm.text.toString()

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
                finish()
            }
        } else {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.background = ContextCompat.getDrawable(this, R.drawable.bt_login_disable)
            binding.btnRegister.setTextColor(Color.parseColor("#898989"))
        }
    }
}