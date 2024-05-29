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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard1)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val card1: CardView = findViewById(R.id.card1)
        setCardContent(card1, "당뇨", "혈당 조절이 어려워져\n혈당 수치가 비정상적으로 높아지는\n만성질환", R.drawable.diabetes)
        card1.setOnClickListener {
            toggleSelection(card1, "당뇨", R.drawable.diabetes_select)
        }

        val card2: CardView = findViewById(R.id.card2)
        setCardContent(card2, "고혈압", "혈압이 지속적으로\n높은 만성질환\n ", R.drawable.heartrate)
        card2.setOnClickListener {
            toggleSelection(card2, "고혈압", R.drawable.heartrate_select)
        }

        val card3: CardView = findViewById(R.id.card3)
        setCardContent(card3, "고지혈증", "혈중 지질 수치가\n높은 만성질환\n  ", R.drawable.blood_cells)
        card3.setOnClickListener {
            toggleSelection(card3, "고지혈증", R.drawable.blood_cells_select)
        }

        val card4: CardView = findViewById(R.id.card4)
        setCardContent(card4, "호흡기질환", "호흡기에\n장기적인 문제가 생기는 질병\n ", R.drawable.coughing_alt)
        card4.setOnClickListener {
            toggleSelection(card4, "호흡기질환", R.drawable.coughing_alt_select)
        }

        val card5: CardView = findViewById(R.id.card5)
        setCardContent(card5, "심혈관질환", "체지방이 과도하게\n축적된 만성 질환\n ", R.drawable.overweight)
        card5.setOnClickListener {
            toggleSelection(card5, "심혈관질환", R.drawable.overweight_select)
        }

        val card6: CardView = findViewById(R.id.card6)
        setCardContent(card6, "기타", "장기간에 걸쳐 지속되는 다양한 질병\n \n ", R.drawable.etc)
        card6.setOnClickListener {
            toggleSelection(card6, "기타", R.drawable.etc)
        }


        // 다른 버튼들의 초기화 및 클릭 리스너 설정

        val btnNext: Button = findViewById(R.id.btn_next)
        btnNext.setOnClickListener {
            val intent = Intent(this, Onboard2Activity::class.java)
            intent.putStringArrayListExtra("selectedDiseases", ArrayList(selectedDiseases))
            startActivity(intent)
        }
    }
    private fun setCardContent(card: CardView, title: String, description: String, imageResId: Int) {
        val titleView: TextView = card.findViewById(R.id.title)
        val descriptionView: TextView = card.findViewById(R.id.description)
        val iconView: ImageView = card.findViewById(R.id.icon)

        titleView.text = title
        descriptionView.text = description
        iconView.setImageResource(imageResId)
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
            titleView.setTextColor(Color.parseColor("#1E54DF"))
            iconView.setImageResource(selectedImageResId)
            setCardStroke(card, Color.parseColor("#1E54DF"))
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