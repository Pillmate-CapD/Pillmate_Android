package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary3Binding

class HDEdit3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary3Binding
    private val logTag = "HealthDiary3ActivityLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼 설정
        binding.tsBack.setOnClickListener {
            onBackPressed()
        }

        // Intent로 전달받은 데이터 받기
        val date = intent.getStringExtra("date")
        val selectedSymptoms = intent.getStringArrayListExtra("selectedSymptoms")
        val painScore = intent.getIntExtra("painScore", 1)
        val record = intent.getStringExtra("record")

        // 전달받은 데이터 로그 출력
        Log.d(logTag, "Received date: $date")
        Log.d(logTag, "Received selectedSymptoms: $selectedSymptoms")
        Log.d(logTag, "Received painScore: $painScore")
        Log.d(logTag, "Received record: $record")

        // diary_input EditText에 300자 제한 설정 및 record 값 표시
        binding.diaryInput.filters = arrayOf(InputFilter.LengthFilter(300))
        binding.diaryInput.setText(record ?: "") // record 값이 null인 경우 빈 문자열 표시

        // 선택 사항: 텍스트 입력 제한 도달 시 경고 표시
        binding.diaryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length >= 300) {
                    // 제한에 도달했을 때 메시지 표시 또는 UI 변경 가능
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
