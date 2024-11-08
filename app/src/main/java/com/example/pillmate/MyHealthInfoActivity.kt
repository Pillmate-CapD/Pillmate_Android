package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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
        // 뒤로 가기 버튼 설정
        val backButton = findViewById<ImageView>(R.id.pwc_back)
        backButton.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        val edits = findViewById<Button>(R.id.btnEditSymptoms)
        edits.setOnClickListener {
            // Intent 생성
            val intent = Intent(this, MyHealthEditSActivity::class.java)
            startActivity(intent)
        }

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
                        Log.d("MyHealthInfoActivity", "Diseases 데이터: ${healthInfo.diseases}")
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

        // 날짜순으로 정렬 (오름차순: 오래된 날짜가 위로)
        val sortedDiseases = diseases.sortedBy { it.startDate }
        Log.d("MyHealthInfoActivity", "정렬된 Diseases 리스트 크기: ${sortedDiseases.size}")

        for ((index, disease) in sortedDiseases.withIndex()) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.myhealthinfo_item1, diseaseContainer, false)

            val tvDate: TextView = itemView.findViewById(R.id.tv_date)
            val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
            val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
            val tvSubtitle: TextView = itemView.findViewById(R.id.tv_subtitle)
            val flCircle1: View = itemView.findViewById(R.id.fl_circles1)
            val flCircle2: View = itemView.findViewById(R.id.fl_circles2)
            val verticalLine: View = itemView.findViewById(R.id.vertical_line)

            tvDate.text = disease.startDate
            tvTitle.text = disease.disease
            setDiseaseContent(disease.disease, ivIcon, tvSubtitle)
            Log.d("MyHealthInfoActivity", "Displaying disease: ${disease.disease}, startDate: ${disease.startDate}")

            if (index % 2 == 0) { // 짝수일 때
                flCircle1.setBackgroundResource(R.drawable.hinfo_outercircle)
                flCircle2.setBackgroundResource(R.drawable.hinfo_innercircle)
                verticalLine.setBackgroundResource(R.drawable.hinfo_line)
            } else { // 홀수일 때
                flCircle1.setBackgroundResource(R.drawable.hinfo_outercircle2)
                flCircle2.setBackgroundResource(R.drawable.hinfo_innercircle2)
                verticalLine.setBackgroundResource(R.drawable.hinfo_line2)
            }

            if (index == sortedDiseases.lastIndex) {
                verticalLine.visibility = View.GONE
                // 마지막 항목에 bottomMargin을 1dp로 설정
                val layoutParams = itemView.layoutParams as LinearLayout.LayoutParams
                layoutParams.bottomMargin = 5.toPx() // 1dp를 픽셀로 변환
                itemView.layoutParams = layoutParams
            }
            // 두 번째 항목부터 marginTop을 -8dp로 설정
            if (index > 0) {
                val layoutParams = itemView.layoutParams as LinearLayout.LayoutParams
                layoutParams.topMargin = (-8).toPx() // -8dp를 픽셀로 변환
                itemView.layoutParams = layoutParams
            }

            diseaseContainer.addView(itemView)
        }
    }
    // 특정 질병 이름에 따른 아이콘과 설명을 설정하는 함수
    private fun setDiseaseContent(diseaseName: String, ivIcon: ImageView, tvSubtitle: TextView) {
        when (diseaseName) {
            "고지혈증" -> {
                ivIcon.setImageResource(R.drawable.o_blood_cells)
                tvSubtitle.text = "혈중 지질 수치가 높은 질환"
            }

            "고혈압" -> {
                ivIcon.setImageResource(R.drawable.o_heartrate)
                tvSubtitle.text = "혈압이 지속적으로 높은 질환"
            }

            "당뇨" -> {
                ivIcon.setImageResource(R.drawable.o_diabetes)
                tvSubtitle.text = "혈당 조절이 어려워 혈당 수치가 높은 질환"
            }

            "심혈관질환" -> {
                ivIcon.setImageResource(R.drawable.o_overweight)
                tvSubtitle.text = "심장과 혈관에 영향을 미치는 질환"
            }

            "호흡기질환" -> {
                ivIcon.setImageResource(R.drawable.o_coughing_alt)
                tvSubtitle.text = "호흡기에 장기적인 문제가 생기는 질환"
            }

            "기타" -> {
                ivIcon.setImageResource(R.drawable.o_plus)
                tvSubtitle.text = "이외의 모든 질환"
            }

            else -> {
                ivIcon.setImageResource(R.drawable.o_plus)
                tvSubtitle.text = "알 수 없는 질환"
            }
        }
    }

    // dp 값을 px로 변환
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()


    private fun displaySymptoms(symptoms: List<MySymptom>) {
        symptomContainer.removeAllViews()
        Log.d("MyHealthInfoActivity", "Symptoms 리스트 크기: ${symptoms.size}")

        for ((index, symptom) in symptoms.withIndex()) {
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

            // 두 번째 항목부터 marginTop을 7dp로 설정
            if (index > 0) {
                val layoutParams = itemView.layoutParams as LinearLayout.LayoutParams
                layoutParams.topMargin = 7.toPx()
                itemView.layoutParams = layoutParams
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
    override fun onResume() {
        super.onResume()
        loadMyHealthInfo()
    }
}


