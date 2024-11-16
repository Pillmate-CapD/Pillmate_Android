package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary3Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthDiary3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary3Binding
    private val logTag = "HealthDiary3ActivityLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로 전달받은 데이터 받기
        val date = intent.getStringExtra("date")
        val selectedSymptoms = intent.getStringArrayListExtra("selectedSymptoms")
        val painScore = intent.getIntExtra("painScore", 1)

        // 전달받은 데이터 로그 출력
        Log.d(logTag, "Received date: $date")
        Log.d(logTag, "Received selectedSymptoms: $selectedSymptoms")
        Log.d(logTag, "Received painScore: $painScore")

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

        // d_btn_f 버튼 클릭 시 다이어리 추가 API 호출
        binding.dBtnF.setOnClickListener {
            val record = binding.diaryInput.text.toString()
            addDiary(date, selectedSymptoms, painScore, record)
        }
    }
    // 다이어리 추가 API 호출 함수
    private fun addDiary(date: String?, selectedSymptoms: ArrayList<String>?, painScore: Int, record: String) {
        if (date.isNullOrEmpty()) {
            Toast.makeText(this, "날짜 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val diaryRequest = DiaryaddRequest(
            date = date,
            symptom = selectedSymptoms,
            score = painScore,
            record = record
        )

        // Retrofit API 호출
        val call = RetrofitApi.getRetrofitService.addDiary(diaryRequest)
        call.enqueue(object : Callback<DiaryaddResponse> {
            override fun onResponse(call: Call<DiaryaddResponse>, response: Response<DiaryaddResponse>) {
                if (response.isSuccessful) {
                    val diaryId = response.body()?.diaryId
                    Log.d(logTag, "Diary added successfully with ID: $diaryId")
                    Toast.makeText(this@HealthDiary3Activity, "다이어리가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show()

                    // CalendarFragment로 돌아가기
                    val intent = Intent(this@HealthDiary3Activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
                    Log.e(logTag, "Failed to add diary: ${response.errorBody()?.string()}")
                    Toast.makeText(this@HealthDiary3Activity, "다이어리 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DiaryaddResponse>, t: Throwable) {
                Log.e(logTag, "API call failed: ${t.message}")
                Toast.makeText(this@HealthDiary3Activity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
