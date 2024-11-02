package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary3Binding

class HealthDiary3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // diary_input EditText에 300자 제한 설정
        binding.diaryInput.filters = arrayOf(InputFilter.LengthFilter(300))

        // 선택 사항: 제한 도달 시 경고 표시
        binding.diaryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length >= 300) {
                    // 필요에 따라 제한에 도달했을 때 메시지 표시 또는 UI 변경
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // d_btn_f 버튼 클릭 시 CalendarFragment로 돌아가도록 설정
        binding.dBtnF.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // MainActivity에서 CalendarFragment 로드
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }
}
