package com.example.pillmate

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class Onboard1Activity : AppCompatActivity() {

    private val selectedDiseases = mutableListOf<String>()
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard1)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val card1: CardView = findViewById(R.id.card1)
        setCardContent(card1, "고지혈증", "혈중 지질 수치가 높은 질환\n", R.drawable.o_blood_cells)
        card1.setOnClickListener {
            toggleSelection(card1, "고지혈증", R.drawable.o_blood_cells)
        }
        val card2: CardView = findViewById(R.id.card2)
        setCardContent(card2, "고혈압", "혈압이 지속적으로 높은 질환\n", R.drawable.o_heartrate)
        card2.setOnClickListener {
            toggleSelection(card2, "고혈압", R.drawable.o_heartrate)
        }
        val card3: CardView = findViewById(R.id.card3)
        setCardContent(card3, "당뇨", "혈당 조절이 어려워 혈당\n수치가 높은 질환", R.drawable.o_diabetes)
        card3.setOnClickListener {
            toggleSelection(card3, "당뇨", R.drawable.o_diabetes)
        }
        val card4: CardView = findViewById(R.id.card4)
        setCardContent(card4, "심혈관질환", "심장과 혈관에 영향을 미치는\n질환", R.drawable.o_overweight)
        card4.setOnClickListener {
            toggleSelection(card4, "심혈관질환", R.drawable.o_overweight)
        }
        val card5: CardView = findViewById(R.id.card5)
        setCardContent(card5, "호흡기질환", "호흡기에 장기적인 문제가\n생기는 질환", R.drawable.o_coughing_alt)
        card5.setOnClickListener {
            toggleSelection(card5, "호흡기질환", R.drawable.o_coughing_alt)
        }

        val card6: CardView = findViewById(R.id.card6)
        setCardContent(card6, "기타", "이외의 모든 질환\n", R.drawable.o_plus)
        card6.setOnClickListener {
            toggleSelection(card6, "기타", R.drawable.o_plus)
        }


        // 다른 버튼들의 초기화 및 클릭 리스너 설정

        btnNext = findViewById(R.id.btn_next)
        updateNextButtonState() // 초기 상태 설정
        btnNext.setOnClickListener {
            if (selectedDiseases.isNotEmpty()) {
                // 선택된 질병을 가나다순으로 정렬
                val sortedDiseases = selectedDiseases.sorted()

                // 전달받은 이메일, 비밀번호, 이름 가져오기
                val email = intent.getStringExtra("email") ?: ""
                val password = intent.getStringExtra("password") ?: ""
                val name = intent.getStringExtra("name") ?: ""

                // Onboard2Activity로 데이터 전달
                val intent = Intent(this, Onboard2Activity::class.java).apply {
                    putExtra("email", email)
                    putExtra("password", password)
                    putExtra("name", name)
                    putStringArrayListExtra("selectedDiseases", ArrayList(sortedDiseases))
                }
                startActivity(intent)
            }
        }
    }
    private fun setCardContent(card: CardView, title: String, description: String, imageResId: Int) {
        val titleView: TextView = card.findViewById(R.id.title)
        val descriptionView: TextView = card.findViewById(R.id.description)
        val iconView: ImageView = card.findViewById(R.id.icon)

        titleView.text = title
        descriptionView.text = description
        iconView.setImageResource(imageResId)
        card.setCardBackgroundColor(Color.parseColor("#F5F6F8"))
        // 원래 이미지를 태그로 저장
        card.tag = imageResId
    }
    private fun toggleSelection(card: CardView, disease: String, selectedImageResId: Int) {
        val titleView: TextView = card.findViewById(R.id.title)
        val iconView: ImageView = card.findViewById(R.id.icon)

        if (selectedDiseases.contains(disease)) { //선택 해제시
            selectedDiseases.remove(disease)
            card.isSelected = false
            card.setCardBackgroundColor(Color.parseColor("#F5F6F8"))
            titleView.setTextColor(Color.parseColor("#3e3e3e"))
            iconView.setImageResource(card.tag as Int)  // 원래 이미지로 복원
            setCardStroke(card, Color.TRANSPARENT)
        } else {
            selectedDiseases.add(disease)
            card.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            titleView.setTextColor(Color.parseColor("#3e3e3e"))
            iconView.setImageResource(selectedImageResId)
            setCardStroke(card, Color.parseColor("#08D2C8"))
        }
        updateNextButtonState() // 상태 업데이트
    }
    private fun updateNextButtonState() {
        if (selectedDiseases.isNotEmpty()) {
            btnNext.isEnabled = true
            btnNext.setBackgroundResource(R.drawable.onboard_btn_active)
            btnNext.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            btnNext.isEnabled = false
            btnNext.setBackgroundResource(R.drawable.onboard_btn_inactive_real)
            btnNext.setTextColor(Color.parseColor("#898989"))
        }
    }

    private fun setCardStroke(card: CardView, color: Int) {
        val drawable = GradientDrawable().apply {
            setStroke(4, color)
            cornerRadius = dpToPx(9f) // dp를 px로 변환
            //cornerRadius = 9f
        }
        card.foreground = drawable
    }
    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }


}