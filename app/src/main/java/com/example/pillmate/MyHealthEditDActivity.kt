package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class MyHealthEditDActivity : AppCompatActivity() {

    private val selectedDiseases = mutableSetOf<String>()
    private val savedDiseases = mutableListOf<String>() // API로 받아온 질병 목록
    private val savedDiseaseDates = mutableMapOf<String, LocalDate>() // LocalDate 형식의 날짜
    private val selectedSymptoms = mutableSetOf<String>()
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard1)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        btnNext = findViewById(R.id.btn_next)
        btnNext.text = "다음"
        btnNext.isEnabled = false

        fetchDiseases()
        initializeCardViews()
        setupNextButton()
    }

    private fun fetchDiseases() {
        RetrofitApi.getRetrofitService.getEditMyHealthInfo().enqueue(object : Callback<EditGMyHealthInfoResponse> {
            override fun onResponse(call: Call<EditGMyHealthInfoResponse>, response: Response<EditGMyHealthInfoResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    savedDiseases.clear()
                    savedDiseaseDates.clear()

                    // 질병 목록 처리
                    data?.diseases?.forEach {
                        val diseaseName = it.disease
                        val startDateStr = it.startDate

                        // startDate를 LocalDate로 변환
                        try {
                            val startDate = LocalDate.parse(startDateStr)
                            savedDiseases.add(diseaseName)
                            savedDiseaseDates[diseaseName] = startDate
                        } catch (e: Exception) {
                            Log.e("MyHealthEditDActivity", "Error parsing startDate for $diseaseName: ${e.message}")
                        }
                    }

                    // 증상 목록 추가 (기존 데이터 유지)
                    data?.symptoms?.let { symptoms ->
                        symptoms.forEach { symptom ->
                            selectedSymptoms.add(symptom.name) // API에서 증상 이름 추가
                        }
                        Log.d("MyHealthEditDActivity", "Received Symptoms: ${selectedSymptoms.joinToString(", ")}")
                    }

                    initializeCardViews()
                } else {
                    Log.e("MyHealthEditDActivity", "API 응답 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<EditGMyHealthInfoResponse>, t: Throwable) {
                Log.e("MyHealthEditDActivity", "API 호출 실패: ${t.message}")
            }
        })
    }


    private fun initializeCardViews() {
        val card1: CardView = findViewById(R.id.card1)
        setCardContent(card1, "고지혈증", "혈중 지질 수치가 높은 질환\n", R.drawable.o_blood_cells)
        checkAndActivateCard(card1, "고지혈증", R.drawable.o_blood_cells)

        val card2: CardView = findViewById(R.id.card2)
        setCardContent(card2, "고혈압", "혈압이 지속적으로 높은 질환\n", R.drawable.o_heartrate)
        checkAndActivateCard(card2, "고혈압", R.drawable.o_heartrate)

        val card3: CardView = findViewById(R.id.card3)
        setCardContent(card3, "당뇨", "혈당 조절이 어려워 혈당\n" + "수치가 높은 질환", R.drawable.o_diabetes)
        checkAndActivateCard(card3, "당뇨", R.drawable.o_diabetes)

        val card4: CardView = findViewById(R.id.card4)
        setCardContent(card4, "심혈관질환", "심장과 혈관에 영향을 미치는\n" +
                "질환", R.drawable.o_overweight)
        checkAndActivateCard(card4, "심혈관질환", R.drawable.o_overweight)

        val card5: CardView = findViewById(R.id.card5)
        setCardContent(card5, "호흡기질환", "호흡기에 장기적인 문제가\n" +
                "생기는 질환", R.drawable.o_coughing_alt)
        checkAndActivateCard(card5, "호흡기질환", R.drawable.o_coughing_alt)

        val card6: CardView = findViewById(R.id.card6)
        setCardContent(card6, "기타", "이외의 모든 질환\n", R.drawable.o_plus)
        checkAndActivateCard(card6, "기타", R.drawable.o_plus)
    }

    private fun setCardContent(card: CardView, title: String, description: String, imageResId: Int) {
        val titleView: TextView = card.findViewById(R.id.title)
        val descriptionView: TextView = card.findViewById(R.id.description)
        val iconView: ImageView = card.findViewById(R.id.icon)

        titleView.text = title
        descriptionView.text = description
        iconView.setImageResource(imageResId)
        card.tag = imageResId
    }

    private fun checkAndActivateCard(card: CardView, disease: String, selectedImageResId: Int) {
        if (savedDiseases.contains(disease)) {
            selectedDiseases.add(disease)
            card.isSelected = true
            card.setCardBackgroundColor(Color.WHITE)
            val titleView: TextView = card.findViewById(R.id.title)
            val iconView: ImageView = card.findViewById(R.id.icon)
            titleView.setTextColor(Color.parseColor("#3e3e3e"))
            iconView.setImageResource(selectedImageResId)
            setCardStroke(card, Color.parseColor("#08D2C8"), 2)
        } else {
            card.setCardBackgroundColor(Color.parseColor("#F5F6F8"))
            setCardStroke(card, Color.TRANSPARENT, 0)
        }

        card.setOnClickListener {
            toggleSelection(card, disease, selectedImageResId)
        }
    }

    private fun toggleSelection(card: CardView, disease: String, selectedImageResId: Int) {
        val titleView: TextView = card.findViewById(R.id.title)
        val iconView: ImageView = card.findViewById(R.id.icon)

        if (selectedDiseases.contains(disease)) {
            selectedDiseases.remove(disease)
            card.isSelected = false
            card.setCardBackgroundColor(Color.parseColor("#F5F6F8"))
            titleView.setTextColor(Color.parseColor("#3e3e3e"))
            iconView.setImageResource(card.tag as Int)
            setCardStroke(card, Color.TRANSPARENT, 0)
        } else {
            selectedDiseases.add(disease)
            card.isSelected = true
            card.setCardBackgroundColor(Color.WHITE)
            titleView.setTextColor(Color.parseColor("#3e3e3e"))
            iconView.setImageResource(selectedImageResId)
            setCardStroke(card, Color.parseColor("#08D2C8"), 2)
        }
        updateNextButtonState()
    }

    private fun setCardStroke(card: CardView, color: Int, strokeWidthDp: Int) {
        val drawable = GradientDrawable().apply {
            setColor(Color.WHITE)
            setStroke(strokeWidthDp.toPx(), color)
            cornerRadius = dpToPx(9f)
        }
        card.background = drawable
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun updateNextButtonState() {
        btnNext.isEnabled = selectedDiseases.isNotEmpty()
        btnNext.setBackgroundResource(if (btnNext.isEnabled) R.drawable.onboard_btn_active else R.drawable.onboard_btn_inactive)
    }

    private fun setupNextButton() {
        btnNext.setOnClickListener {
            try {
                val formattedDates = savedDiseaseDates.mapValues { it.value.toString() }
                val intent = Intent(this, MyHealthEditD2Activity::class.java).apply {
                    putStringArrayListExtra("selectedDiseases", ArrayList(selectedDiseases))
                    putExtra("savedDiseaseDates", HashMap(formattedDates))
                    putStringArrayListExtra("selectedSymptoms", ArrayList(selectedSymptoms)) // 증상 목록 추가
                }
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("MyHealthEditDActivity", "Error during setupNextButton: ${e.message}")
            }
        }
    }
}


