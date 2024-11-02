package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary1Binding
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthDiary1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary1Binding
    private var isNoSymptomSelected = false // 추가: 증상 없음 버튼 상태 추적
    private var isAnySymptomSelected = false
    private val symptomNames = listOf(
        "피로감", "몸살", "근육통", "관절통", "두통", "건망증", "인후통", "기침∙가래", "호흡곤란", "두근거림", "복통", "소화불량", "구토", "변비", "불면증", "수면장애", "우울"
    )
    private val logTag = "HealthDiary1ActivityLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼 설정
        binding.tsBack.setOnClickListener {
            onBackPressed()
        }

        // "증상 없음" 카드 클릭 이벤트 설정
        binding.noSymptomRoot.setOnClickListener { // 바인딩 사용으로 변경
            toggleNoSymptomSelection(binding.noSymptomRoot) // 메소드 변경
        }

        // d_btn_f 버튼 클릭 시 HealthDiary2Activity로 이동
        binding.dBtnF.setOnClickListener {
            val intent = Intent(this, HealthDiary2Activity::class.java)
            startActivity(intent)
        }

        // API 호출
        RetrofitApi.getRetrofitService.getDiarySymptoms().enqueue(object : Callback<HealthDataResponse> {
            override fun onResponse(
                call: Call<HealthDataResponse>,
                response: Response<HealthDataResponse>
            ) {
                if (response.isSuccessful) {
                    val symptoms = response.body()?.symptoms ?: emptyList()
                    Log.d(logTag, "API 응답 성공: ${symptoms.size}개의 증상")
                    displaySymptoms(symptoms)
                    displayOtherSymptoms(symptoms)
                } else {
                    Log.e(logTag, "API 응답 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<HealthDataResponse>, t: Throwable) {
                // 실패 처리 코드
                Log.e(logTag, "API 호출 실패", t)
            }
        })
    }

    private fun toggleSymptomCardSelection(cardView: CardView, checkImageView: ImageView?) {
        val drawable = cardView.background as? GradientDrawable ?: GradientDrawable().apply {
            cornerRadius = 20.dpToPx().toFloat() // `symptom_grid` 반경
            setColor(Color.parseColor("#F5F6F8"))
        }

        handleCardToggle(cardView, checkImageView, drawable)
    }

    private fun toggleAnotherSymptomCardSelection(cardView: CardView, checkImageView: ImageView?) {
        val drawable = cardView.background as? GradientDrawable ?: GradientDrawable().apply {
            cornerRadius = 35.dpToPx().toFloat() // `another_sumptom` 반경
            setColor(Color.parseColor("#F5F6F8"))
        }

        handleCardToggle(cardView, checkImageView, drawable)
    }

    private fun handleCardToggle(cardView: CardView, checkImageView: ImageView?, drawable: GradientDrawable) {
        val currentColor = drawable.color?.defaultColor ?: Color.parseColor("#F5F6F8")

        if (currentColor == Color.parseColor("#F5F6F8")) {
            drawable.setColor(Color.parseColor("#B1EDEA"))
            drawable.setStroke(1, Color.parseColor("#08D2C8"))
            checkImageView?.setImageResource(R.drawable.symptom_ycheck)
        } else {
            drawable.setColor(Color.parseColor("#F5F6F8"))
            drawable.setStroke(0, ContextCompat.getColor(this, android.R.color.transparent))
            checkImageView?.setImageResource(R.drawable.symptom_ncheck)
        }

        cardView.background = drawable
    }

    private fun toggleNoSymptomSelection(cardView: CardView) {
        val drawable = cardView.background as? GradientDrawable ?: GradientDrawable().apply {
            cornerRadius = 23 * resources.displayMetrics.density
        }

        val currentColor = (cardView.background as? GradientDrawable)?.color?.defaultColor
            ?: Color.parseColor("#FFFFFF")

        if (currentColor == Color.parseColor("#FFFFFF")) {
            // 선택되지 않은 상태에서 클릭됨
            drawable.setColor(Color.parseColor("#E6EBFA")) // 배경색 설정
            drawable.setStroke(1, Color.parseColor("#1E54DF")) // 테두리 설정
            isNoSymptomSelected = true // 상태 업데이트
            deselectAndDisableOtherSymptoms() // 다른 증상 선택 해제 및 비활성화
        } else {
            // 선택된 상태에서 클릭됨 (선택 취소)
            drawable.setColor(Color.parseColor("#FFFFFF")) // 기본 배경색
            drawable.setStroke(0, ContextCompat.getColor(this, android.R.color.transparent)) // 테두리 제거
            isNoSymptomSelected = false // 상태 업데이트
            enableOtherSymptoms() // 다른 증상 활성화
        }

        cardView.background = drawable
    }

    private fun deselectAndDisableOtherSymptoms() {
        for (i in 0 until binding.symptomGrid.childCount) {
            val childView = binding.symptomGrid.getChildAt(i)
            val cardView = childView.findViewById<CardView>(R.id.symptom_card)
            val checkImageView = childView.findViewById<ImageView>(R.id.symptom_check)
            if (cardView != null && checkImageView != null) {
                // 선택 해제 및 비활성화
                setCardToDeselectedState(cardView, checkImageView)
                cardView.isClickable = false
            }
        }
        for (i in 0 until binding.anotherSumptom.childCount) {
            val childView = binding.anotherSumptom.getChildAt(i)
            val cardView = childView.findViewById<CardView>(R.id.a_symptom_card)
            val checkImageView = childView.findViewById<ImageView>(R.id.symptom_check)
            if (cardView != null && checkImageView != null) {
                // 선택 해제 및 비활성화
                setCardToDeselectedState(cardView, checkImageView)
                cardView.isClickable = false
            }
        }
    }

    private fun enableOtherSymptoms() {
        for (i in 0 until binding.symptomGrid.childCount) {
            val childView = binding.symptomGrid.getChildAt(i)
            val cardView = childView.findViewById<CardView>(R.id.symptom_card)
            if (cardView != null) {
                cardView.isClickable = true // 활성화
            }
        }
        for (i in 0 until binding.anotherSumptom.childCount) {
            val childView = binding.anotherSumptom.getChildAt(i)
            val cardView = childView.findViewById<CardView>(R.id.a_symptom_card)
            if (cardView != null) {
                cardView.isClickable = true // 활성화
            }
        }
    }

    private fun setCardToDeselectedState(cardView: CardView, checkImageView: ImageView) {
        val drawable = cardView.background as? GradientDrawable ?: GradientDrawable().apply {
            cornerRadius = 23 * resources.displayMetrics.density
        }
        drawable.setColor(Color.parseColor("#F5F6F8")) // 기본 배경색으로 설정
        drawable.setStroke(0, ContextCompat.getColor(this, android.R.color.transparent)) // 테두리 제거
        checkImageView.setImageResource(R.drawable.symptom_ncheck) // 체크 이미지 해제
        cardView.background = drawable
    }

    private fun displaySymptoms(symptoms: List<DiarySymptom>) {
        Log.d(logTag, "displaySymptoms 호출, 증상 개수: ${symptoms.size}")
        binding.symptomGrid.removeAllViews()
        val inflater = LayoutInflater.from(this)

        symptoms.forEachIndexed { index, symptom ->
            Log.d(logTag, "추가할 증상: ${symptom.name}")
            val symptomView = inflater.inflate(R.layout.card2_usual_symptom1, binding.symptomGrid, false)
            val symptomName = symptomView.findViewById<TextView>(R.id.symptom_name)
            val symptomImg = symptomView.findViewById<ImageView>(R.id.symptom_img)
            val symptomCheck = symptomView.findViewById<ImageView>(R.id.symptom_check)
            val cardView = symptomView.findViewById<CardView>(R.id.symptom_card)
            cardView.setCardBackgroundColor(Color.parseColor("#F5F6F8"))

            symptomName.text = symptom.name
            val imageResId = resources.getIdentifier("onboard3btn${index + 1}", "drawable", packageName)
            Log.d(logTag, "이미지 리소스 ID: $imageResId")
            symptomImg.setImageResource(imageResId)

            cardView.setOnClickListener {
                toggleSymptomCardSelection(cardView, symptomCheck)
            }

            binding.symptomGrid.addView(symptomView)
        }
    }

    private fun displayOtherSymptoms(symptoms: List<DiarySymptom>) {
        Log.d(logTag, "displayOtherSymptoms 호출")
        binding.anotherSumptom.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val selectedSymptomNames = symptoms.map { it.name }
        val remainingSymptoms = symptomNames.filterNot { selectedSymptomNames.contains(it) }

        Log.d(logTag, "남은 증상 개수: ${remainingSymptoms.size}")

        remainingSymptoms.forEach { symptomName ->
            Log.d(logTag, "추가할 나머지 증상: $symptomName")
            val symptomView = inflater.inflate(R.layout.card3_another_symptom, binding.anotherSumptom, false)
            val symptomNameTextView = symptomView.findViewById<TextView>(R.id.a_symptom_name)
            val symptomImg = symptomView.findViewById<ImageView>(R.id.a_symptom_img)
            val symptomCheck = symptomView.findViewById<ImageView>(R.id.symptom_check)
            val cardView = symptomView.findViewById<CardView>(R.id.a_symptom_card)
            cardView.setCardBackgroundColor(Color.parseColor("#F5F6F8"))

            symptomNameTextView.text = symptomName
            val imageResId = resources.getIdentifier(getImageResourceName(symptomName), "drawable", packageName)
            Log.d(logTag, "이미지 리소스 ID (나머지 증상): $imageResId")
            symptomImg.setImageResource(imageResId)

            cardView.setOnClickListener {
                toggleAnotherSymptomCardSelection(cardView, symptomCheck)
            }

            binding.anotherSumptom.addView(symptomView)
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

    // 확장 함수: dp를 px로 변환
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}


