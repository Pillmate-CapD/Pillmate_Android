package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityOnboard3Binding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyHealthEditSActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboard3Binding
    private val selectedSymptoms = mutableSetOf<String>()
    private lateinit var savedDiseases: List<EditGMyHealthInfo>
    private lateinit var savedSymptoms: List<EditGMySymptom>

    private val symptomMap = mapOf(
        1 to "피로감", 2 to "몸살", 3 to "근육통", 4 to "관절통",
        5 to "두통", 6 to "건망증", 7 to "인후통", 8 to "기침∙가래",
        9 to "호흡곤란", 10 to "두근거림", 11 to "복통", 12 to "소화불량",
        13 to "구토", 14 to "변비", 15 to "불면증", 16 to "수면장애",
        17 to "우울", 18 to "기타"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboard3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            // MyHealthInfoActivity로 이동
            val intent = Intent(this, MyHealthInfoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 현재 Activity 종료
        }

        val btnFinish: Button = findViewById(R.id.btn_finish)
        btnFinish.text = "수정하기"
        btnFinish.setOnClickListener { updateEditHealthInfo() }

        fetchEditHealthInfo()
    }

    private fun fetchEditHealthInfo() {
        RetrofitApi.getRetrofitService.getEditMyHealthInfo().enqueue(object : Callback<EditGMyHealthInfoResponse> {
            override fun onResponse(call: Call<EditGMyHealthInfoResponse>, response: Response<EditGMyHealthInfoResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    savedDiseases = data?.diseases ?: listOf()
                    savedSymptoms = data?.symptoms ?: listOf()

                    selectedSymptoms.clear()
                    selectedSymptoms.addAll(savedSymptoms.map { it.name })

                    Log.d("MyHealthEditSActivity", "선택된 증상 리스트: $selectedSymptoms")

                    initializeSymptomButtons()
                    setupButtons()
                } else {
                    Log.e("MyHealthEditSActivity", "데이터 가져오기 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EditGMyHealthInfoResponse>, t: Throwable) {
                Log.e("MyHealthEditSActivity", "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun initializeSymptomButtons() {
        (1..18).forEach { id ->
            val overlay = findViewById<View>(resources.getIdentifier("overlay$id", "id", packageName))
            val checkIcon = findViewById<View>(resources.getIdentifier("check_icon$id", "id", packageName))

            val symptomName = symptomMap[id] ?: return@forEach
            val isSelected = selectedSymptoms.contains(symptomName)

            overlay?.visibility = if (isSelected) View.VISIBLE else View.GONE
            checkIcon?.visibility = if (isSelected) View.VISIBLE else View.GONE
        }
    }

    private fun setupButtons() {
        (1..18).forEach { id ->
            val button = findViewById<View>(resources.getIdentifier("btn$id", "id", packageName))

            button?.setOnClickListener {
                val symptomName = symptomMap[id] ?: return@setOnClickListener

                if (selectedSymptoms.contains(symptomName)) {
                    selectedSymptoms.remove(symptomName)
                    updateButtonState(id, false)
                } else {
                    selectedSymptoms.add(symptomName)
                    updateButtonState(id, true)
                }

                Log.d("MyHealthEditSActivity", "현재 선택된 증상 리스트: $selectedSymptoms")
            }
        }
    }

    private fun updateButtonState(symptomId: Int, isSelected: Boolean) {
        val overlay = findViewById<View>(resources.getIdentifier("overlay$symptomId", "id", packageName))
        val checkIcon = findViewById<View>(resources.getIdentifier("check_icon$symptomId", "id", packageName))

        overlay?.visibility = if (isSelected) View.VISIBLE else View.GONE
        checkIcon?.visibility = if (isSelected) View.VISIBLE else View.GONE
    }

    private fun updateEditHealthInfo() {
        val updatedSymptoms = selectedSymptoms.map { EditPMySymptom(name = it) }
        val updatedDiseases = savedDiseases.map { EditPMyHealthInfo(disease = it.disease, startDate = it.startDate) }

        val requestBody = EditPMyHealthInfoRequest(
            diseases = updatedDiseases,
            symptoms = updatedSymptoms
        )

        val requestBodyJson = Gson().toJson(requestBody)
        Log.d("MyHealthEditSActivity", "PATCH 요청 바디: $requestBodyJson")

        RetrofitApi.getRetrofitService.updateEditMyHealthInfo(requestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MyHealthEditSActivity, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("MyHealthEditSActivity", "수정 실패: ${response.message()}")
                    response.errorBody()?.let { errorBody ->
                        Log.e("MyHealthEditSActivity", "에러 본문: ${errorBody.string()}")
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MyHealthEditSActivity", "네트워크 오류: ${t.message}")
            }
        })
    }
}







