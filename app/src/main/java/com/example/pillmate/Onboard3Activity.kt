package com.example.pillmate

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityOnboard3Binding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class Onboard3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboard3Binding
    private val selectedSymptoms = mutableSetOf<String>() // 선택된 증상을 저장
    private lateinit var btnFinish: Button

    private val symptomMap = mapOf(
        1 to "피로감",
        2 to "몸살",
        3 to "근육통",
        4 to "관절통",
        5 to "두통",
        6 to "건망증",
        7 to "인후통",
        8 to "기침∙가래",
        9 to "호흡곤란",
        10 to "두근거림",
        11 to "복통",
        12 to "소화불량",
        13 to "구토",
        14 to "변비",
        15 to "불면증",
        16 to "수면장애",
        17 to "우울",
        18 to "기타"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboard3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // 전달받은 데이터 가져오기
        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val selectedDiseases = intent.getStringArrayListExtra("selectedDiseases") ?: arrayListOf()
        val diseaseStartDates = intent.getSerializableExtra("diseaseStartDates") as? HashMap<String, String> ?: hashMapOf()

        // 버튼 설정
        setupButtons()

        btnFinish = findViewById(R.id.btn_finish)
        btnFinish.isEnabled = false

        btnFinish.setOnClickListener {
            if (selectedSymptoms.isNotEmpty()) {
                // 질병 리스트 생성
                val diseases = selectedDiseases.mapNotNull { disease ->
                    val startDateString = diseaseStartDates[disease] ?: return@mapNotNull null
                    val cleanDateString = startDateString.replace(Regex("[^0-9-]"), "")
                    val startDateParts = cleanDateString.split("-")

                    // startDateParts의 길이를 체크하여 올바르게 처리
                    if (startDateParts.size == 2) {
                        val year = startDateParts[0].toIntOrNull()
                        val month = startDateParts[1].toIntOrNull()
                        if (year != null && month != null) {
                            // 숫자 추출 후 LocalDate로 변환 (일(day)은 1로 설정)
                            val startDate = LocalDate.of(year, month, 1)
                            Disease(disease, startDate)
                        } else null
                    } else {
                        null
                    }
                }

                // 증상 리스트 생성
                val symptoms = selectedSymptoms.map { symptomName ->
                    Symptom(symptomName)
                }

                // SignUpRequest 객체 생성
                val signUpRequest = SignUpRequest(
                    email = email,
                    password = password,
                    name = name,
                    diseases = diseases,
                    symptoms = symptoms,
                    roles = listOf("USER")
                )

                // JSON 로그 출력
                val signUpRequestJson = Gson().toJson(signUpRequest)
                Log.d("SignUpRequestJson", signUpRequestJson)

                // API 호출
                RetrofitApi.getRetrofitService.signup(signUpRequest).enqueue(object :
                    Callback<SignUpResponse> {
                    override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                        if (response.isSuccessful) {
                            val signUpResponse = response.body()
                            if (signUpResponse != null) {
                                Log.d("Onboard3Activity", "회원가입 성공: ${signUpResponse.accessToken}")
                                Toast.makeText(this@Onboard3Activity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                                // SignUpCompleteActivity로 이동
                                val intent = Intent(this@Onboard3Activity, SignupFinishActivity::class.java).apply {
                                    putExtra("accessToken", signUpResponse.accessToken)
                                    putExtra("name", signUpResponse.name)
                                }
                                startActivity(intent)
                                finish()
                            } else {
                                Log.e("Onboard3Activity", "응답 오류: 응답 본문이 null입니다.")
                            }
                        } else {
                            Log.e("Onboard3Activity", "회원가입 실패: ${response.message()}")
                            response.errorBody()?.let { errorBody ->
                                val errorBodyString = errorBody.string()
                                Log.e("Onboard3Activity", "에러 본문: $errorBodyString")
                            }
                        }
                    }

                    override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                        Log.e("Onboard3Activity", "네트워크 오류: ${t.message}")
                    }
                })
            }
        }

    }

    private fun setupButtons() {
        setupButton(binding.btn1, binding.overlay1, binding.checkIcon1, 1)
        setupButton(binding.btn2, binding.overlay2, binding.checkIcon2, 2)
        setupButton(binding.btn3, binding.overlay3, binding.checkIcon3, 3)
        setupButton(binding.btn4, binding.overlay4, binding.checkIcon4, 4)
        setupButton(binding.btn5, binding.overlay5, binding.checkIcon5, 5)
        setupButton(binding.btn6, binding.overlay6, binding.checkIcon6, 6)
        setupButton(binding.btn7, binding.overlay7, binding.checkIcon7, 7)
        setupButton(binding.btn8, binding.overlay8, binding.checkIcon8, 8)
        setupButton(binding.btn9, binding.overlay9, binding.checkIcon9, 9)
        setupButton(binding.btn10, binding.overlay10, binding.checkIcon10, 10)
        setupButton(binding.btn11, binding.overlay11, binding.checkIcon11, 11)
        setupButton(binding.btn12, binding.overlay12, binding.checkIcon12, 12)
        setupButton(binding.btn13, binding.overlay13, binding.checkIcon13, 13)
        setupButton(binding.btn14, binding.overlay14, binding.checkIcon14, 14)
        setupButton(binding.btn15, binding.overlay15, binding.checkIcon15, 15)
        setupButton(binding.btn16, binding.overlay16, binding.checkIcon16, 16)
        setupButton(binding.btn17, binding.overlay17, binding.checkIcon17, 17)
        setupButton(binding.btn18, binding.overlay18, binding.checkIcon18, 18)
    }

    private fun setupButton(button: View, overlay: View, checkIcon: View, symptomId: Int) {
        button.setOnClickListener {
            val symptomName = symptomMap[symptomId] ?: return@setOnClickListener
            if (selectedSymptoms.contains(symptomName)) {
                selectedSymptoms.remove(symptomName)
                overlay.visibility = View.GONE
                checkIcon.visibility = View.GONE
            } else {
                selectedSymptoms.add(symptomName)
                overlay.visibility = View.VISIBLE
                checkIcon.visibility = View.VISIBLE
            }
            btnFinish.isEnabled = selectedSymptoms.isNotEmpty()
        }
    }
}




