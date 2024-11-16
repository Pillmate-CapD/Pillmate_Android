package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary1Binding
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthDiary1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary1Binding
    private var isNoSymptomSelected = false
    private var isAnySymptomSelected = false
    private var selectedSymptoms = mutableListOf<String>()
    private var date: String? = null
    private val symptomNames = listOf(
        "피로감", "몸살", "근육통", "관절통", "두통", "건망증", "인후통", "기침∙가래", "호흡곤란",
        "두근거림", "복통", "소화불량", "구토", "변비", "불면증", "수면장애", "우울"
    )
    private val logTag = "HealthDiary1ActivityLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로 date 값 받기
        date = intent.getStringExtra("date")

        // d_btn_f 버튼 초기화 및 비활성화 설정
        binding.dBtnF.isEnabled = false
        binding.dBtnF.setBackgroundResource(R.drawable.button_skyblue)
        binding.dBtnF.setTextColor(Color.parseColor("#898989"))

        // 뒤로가기 버튼 설정
        binding.tsBack.setOnClickListener {
            onBackPressed()
        }

        // "증상 없음" 카드 클릭 이벤트 설정
        binding.noSymptomRoot.setOnClickListener {
            toggleNoSymptomSelection(binding.noSymptomRoot)
        }

        // d_btn_f 버튼 클릭 시 HealthDiary2Activity로 이동
        binding.dBtnF.setOnClickListener {
            val intent = Intent(this, HealthDiary2Activity::class.java)
            intent.putExtra("date", date)
            if (isNoSymptomSelected) {
                // "증상 없음"이 선택된 경우
                intent.putExtra("selectedSymptoms", arrayListOf("증상 없음"))
            } else {
                // 선택된 증상들을 전달
                intent.putExtra("selectedSymptoms", ArrayList(selectedSymptoms))
            }
            startActivity(intent)
        }

        // API 호출
        RetrofitApi.getRetrofitService.getDiarySymptoms().enqueue(object : Callback<HealthDataResponse> {
            override fun onResponse(call: Call<HealthDataResponse>, response: Response<HealthDataResponse>) {
                if (response.isSuccessful) {
                    val symptoms = response.body()?.symptoms ?: emptyList()
                    displaySymptoms(symptoms)
                    displayOtherSymptoms(symptoms)
                } else {
                    Log.e(logTag, "API 응답 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<HealthDataResponse>, t: Throwable) {
                Log.e(logTag, "API 호출 실패", t)
            }
        })
    }

    // "증상 없음" 클릭 시 버튼 상태 업데이트
    private fun toggleNoSymptomSelection(cardView: CardView) {
        val drawable = cardView.background as? GradientDrawable ?: GradientDrawable().apply {
            cornerRadius = 23 * resources.displayMetrics.density
        }
        val currentColor = drawable.color?.defaultColor ?: Color.parseColor("#FFFFFF")

        if (currentColor == Color.parseColor("#FFFFFF")) {
            drawable.setColor(Color.parseColor("#E6EBFA"))
            drawable.setStroke(1, Color.parseColor("#1E54DF"))
            isNoSymptomSelected = true
            selectedSymptoms.clear()
            selectedSymptoms.add("증상 없음")
            deselectAndDisableOtherSymptoms()
        } else {
            drawable.setColor(Color.parseColor("#FFFFFF"))
            drawable.setStroke(0, ContextCompat.getColor(this, android.R.color.transparent))
            isNoSymptomSelected = false
            selectedSymptoms.clear()
            enableOtherSymptoms()
        }
        cardView.background = drawable
        updateButtonState()
    }

    private fun toggleSymptomCardSelection(cardView: CardView, checkImageView: ImageView?, symptomName: String) {
        handleCardToggle(cardView, checkImageView)
        if (selectedSymptoms.contains(symptomName)) {
            selectedSymptoms.remove(symptomName)
        } else {
            selectedSymptoms.add(symptomName)
        }
        updateButtonState()
    }

    private fun handleCardToggle(cardView: CardView, checkImageView: ImageView?) {
        val drawable = cardView.background as? GradientDrawable ?: GradientDrawable().apply {
            cornerRadius = 20.dpToPx().toFloat()
            setColor(Color.parseColor("#F5F6F8"))
        }
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

    private fun updateButtonState() {
        val isAnySymptomSelected = selectedSymptoms.isNotEmpty()
        binding.dBtnF.isEnabled = isAnySymptomSelected

        if (isAnySymptomSelected) {

            binding.dBtnF.isEnabled = true
            binding.dBtnF.setBackgroundResource(R.drawable.button_blue)
            binding.dBtnF.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            binding.dBtnF.isEnabled = false
            binding.dBtnF.setBackgroundResource(R.drawable.button_skyblue)
            binding.dBtnF.setTextColor(Color.parseColor("#898989"))
        }
    }

    private fun hasSelectedSymptom(): Boolean {
        for (i in 0 until binding.symptomGrid.childCount) {
            val childView = binding.symptomGrid.getChildAt(i)
            val checkImageView = childView.findViewById<ImageView>(R.id.symptom_check)
            if (checkImageView?.drawable?.constantState == ContextCompat.getDrawable(this, R.drawable.symptom_ycheck)?.constantState) {
                return true
            }
        }
        return false
    }

    private fun deselectAndDisableOtherSymptoms() {
        for (i in 0 until binding.symptomGrid.childCount) {
            val childView = binding.symptomGrid.getChildAt(i)
            val cardView = childView.findViewById<CardView>(R.id.symptom_card)
            val checkImageView = childView.findViewById<ImageView>(R.id.symptom_check)
            setCardToDeselectedState(cardView, checkImageView)
            cardView.isClickable = false
        }
        for (i in 0 until binding.anotherSumptom.childCount) {
            val childView = binding.anotherSumptom.getChildAt(i)
            val cardView = childView.findViewById<CardView>(R.id.a_symptom_card)
            val checkImageView = childView.findViewById<ImageView>(R.id.symptom_check)
            setCardToDeselectedState(cardView, checkImageView)
            cardView.isClickable = false
        }
    }

    private fun enableOtherSymptoms() {
        for (i in 0 until binding.symptomGrid.childCount) {
            val cardView = binding.symptomGrid.getChildAt(i).findViewById<CardView>(R.id.symptom_card)
            cardView.isClickable = true
        }
        for (i in 0 until binding.anotherSumptom.childCount) {
            val cardView = binding.anotherSumptom.getChildAt(i).findViewById<CardView>(R.id.a_symptom_card)
            cardView.isClickable = true
        }
    }

    private fun displaySymptoms(symptoms: List<DiarySymptom>) {
        binding.symptomGrid.removeAllViews()
        val inflater = LayoutInflater.from(this)

        symptoms.forEach { symptom ->
            val symptomView = inflater.inflate(R.layout.card2_usual_symptom1, binding.symptomGrid, false)
            val symptomName = symptomView.findViewById<TextView>(R.id.symptom_name)
            val symptomImg = symptomView.findViewById<ImageView>(R.id.symptom_img)
            val symptomCheck = symptomView.findViewById<ImageView>(R.id.symptom_check)
            val cardView = symptomView.findViewById<CardView>(R.id.symptom_card)

            symptomName.text = symptom.name
            symptomImg.setImageResource(resources.getIdentifier(getImageResourceName(symptom.name), "drawable", packageName))

            cardView.setOnClickListener {
                toggleSymptomCardSelection(cardView, symptomCheck, symptom.name)
            }
            binding.symptomGrid.addView(symptomView)
        }
    }

    private fun displayOtherSymptoms(symptoms: List<DiarySymptom>) {
        binding.anotherSumptom.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val selectedSymptoms = symptoms.map { it.name }

        symptomNames.filterNot { it in selectedSymptoms }.forEach { symptomName ->
            val symptomView = inflater.inflate(R.layout.card3_another_symptom, binding.anotherSumptom, false)
            val symptomNameTextView = symptomView.findViewById<TextView>(R.id.a_symptom_name)
            val symptomImg = symptomView.findViewById<ImageView>(R.id.a_symptom_img)
            val symptomCheck = symptomView.findViewById<ImageView>(R.id.symptom_check)
            val cardView = symptomView.findViewById<CardView>(R.id.a_symptom_card)

            symptomNameTextView.text = symptomName
            symptomImg.setImageResource(resources.getIdentifier(getImageResourceName(symptomName), "drawable", packageName))

            // 여기에 symptomName 전달
            cardView.setOnClickListener {
                toggleSymptomCardSelection(cardView, symptomCheck, symptomName)
            }

            binding.anotherSumptom.addView(symptomView)
        }
    }
    private fun setCardToDeselectedState(cardView: CardView, checkImageView: ImageView) {
        val drawable = GradientDrawable().apply {
            cornerRadius = 23 * resources.displayMetrics.density
        }
        drawable.setColor(Color.parseColor("#F5F6F8"))
        drawable.setStroke(0, ContextCompat.getColor(this, android.R.color.transparent))
        checkImageView.setImageResource(R.drawable.symptom_ncheck)
        cardView.background = drawable
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
            else -> "default_image"
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}



