package com.example.pillmate

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyHealthInfoActivity : AppCompatActivity() {

    private lateinit var diseaseContainer: LinearLayout
    private lateinit var symptomContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myhealthinfo)

        diseaseContainer = findViewById(R.id.diseaseContainer)
        symptomContainer = findViewById(R.id.symptomContainer)
        loadMyHealthInfo()
    }

    private fun loadMyHealthInfo() {
        val call = RetrofitApi.getRetrofitService.getMyHealthInfo()
        call.enqueue(object : Callback<MyHealthInfoResponse> {
            override fun onResponse(
                call: Call<MyHealthInfoResponse>,
                response: Response<MyHealthInfoResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { healthInfo ->
                        Log.d("MyHealthInfoActivity", "API 호출 성공, 데이터 수신됨")
                        displayDiseases(healthInfo.diseases)
                        displaySymptoms(healthInfo.symptoms)
                    } ?: run {
                        Log.e("MyHealthInfoActivity", "응답 본문이 null입니다.")
                    }
                } else {
                    Log.e("MyHealthInfoActivity", "API 호출 성공했으나 응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MyHealthInfoResponse>, t: Throwable) {
                Log.e("MyHealthInfoActivity", "API 호출 실패: ${t.message}")
            }
        })
    }

    private fun displayDiseases(diseases: List<MyHealthInfo>) {
        diseaseContainer.removeAllViews()
        Log.d("MyHealthInfoActivity", "Diseases 리스트 크기: ${diseases.size}")

        for ((index, disease) in diseases.withIndex()) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.myhealthinfo_item1, diseaseContainer, false)

            val tvDate: TextView = itemView.findViewById(R.id.tv_date)
            val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
            val flCircle1: View = itemView.findViewById(R.id.fl_circles1)
            val flCircle2: View = itemView.findViewById(R.id.fl_circles2)
            val verticalLine: View = itemView.findViewById(R.id.vertical_line)

            tvDate.text = disease.startDate
            tvTitle.text = disease.disease
            Log.d("MyHealthInfoActivity", "Displaying disease: ${disease.disease}, startDate: ${disease.startDate}")

            if (index % 2 == 0) { // 짝수 (빨간색)
                flCircle1.setBackgroundColor(Color.parseColor("#30FF453A"))
                flCircle2.setBackgroundColor(Color.parseColor("#FF453A"))
                verticalLine.setBackgroundResource(R.drawable.hinfo_line2)
            } else { // 홀수 (파란색)
                flCircle1.setBackgroundColor(Color.parseColor("#4D2E67FF"))
                flCircle2.setBackgroundColor(Color.parseColor("#1E54DF"))
                verticalLine.setBackgroundResource(R.drawable.hinfo_line)
            }

            if (index == diseases.lastIndex) {
                verticalLine.visibility = View.GONE
            }

            diseaseContainer.addView(itemView)
        }
    }

    private fun displaySymptoms(symptoms: List<MySymptom>) {
        symptomContainer.removeAllViews()
        Log.d("MyHealthInfoActivity", "Symptoms 리스트 크기: ${symptoms.size}")

        for (symptom in symptoms) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.myhealthinfo_item2, symptomContainer, false)

            val symptomTitle: TextView = itemView.findViewById(R.id.symptom_title)
            val symptomIcon: ImageView = itemView.findViewById(R.id.symptom_icon)

            symptomTitle.text = symptom.name
            val imageResource = getImageResourceName(symptom.name)
            val resId = resources.getIdentifier(imageResource, "drawable", packageName)
            if (resId != 0) {
                symptomIcon.setImageResource(resId)
            } else {
                symptomIcon.setImageResource(R.drawable.onboard3btn1) // 기본 이미지
            }

            symptomContainer.addView(itemView)
        }
    }

    private fun getImageResourceName(symptomName: String): String {
        return when (symptomName) {
            "피로감" -> "onboard3btn1"
            "몸살" -> "onboard3btn2"
            "근육통" -> "onboard3btn3"
            "관절통" -> "onboard3btn4"
            "두통" -> "onboard3btn5"
            "건망증" -> "onboard3btn6"
            "인후통" -> "onboard3btn7"
            "기침∙가래" -> "onboard3btn8"
            "호흡곤란" -> "onboard3btn9"
            "두근거림" -> "onboard3btn10"
            "복통" -> "onboard3btn11"
            "소화불량" -> "onboard3btn12"
            "구토" -> "onboard3btn13"
            "변비" -> "onboard3btn14"
            "불면증" -> "onboard3btn15"
            "수면장애" -> "onboard3btn20"
            "우울" -> "onboard3btn17"
            else -> "default_image" // 기본 이미지 이름
        }
    }
}


