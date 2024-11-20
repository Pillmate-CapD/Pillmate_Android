package com.example.pillmate

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class Onboard2Activity : AppCompatActivity() {

    private lateinit var btnNext: Button
    private val dateEditTexts = mutableListOf<EditText>()
    private val diseaseStartDates = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard2)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            onBackPressed() // 이전 화면으로 돌아가기
        }
        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val selectedDiseases = intent.getStringArrayListExtra("selectedDiseases") ?: arrayListOf()
        val linearLayout: LinearLayout = findViewById(R.id.linearLayout)

        selectedDiseases.forEach { disease ->
            // 수평으로 배치되는 LinearLayout 생성
            val horizontalLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, dpToPx(10f).toInt(), 0, dpToPx(10f).toInt()) // 상하 여백 추가
            }
            linearLayout.addView(horizontalLayout)

            // 질병을 표시하는 TextView 생성
            val diseaseTextView = TextView(this).apply {
                text = disease
                textSize = 20f
                layoutParams = LinearLayout.LayoutParams(
                    0, // 너비를 0으로 설정하고 weight를 사용하여 비율로 설정
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f // 가중치 설정
                )
                //gravity = Gravity.END // 텍스트를 오른쪽 정렬
                setTextColor(Color.parseColor("#3E3E3E"))
            }
            horizontalLayout.addView(diseaseTextView)

            // 가로 여백 추가 (10dp)
            val space = Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(10f).toInt(), // 너비
                    0, // 높이
                    0f // 가중치 설정
                )
            }
            horizontalLayout.addView(space)

            // 날짜를 선택하는 에디트텍스트 생성
            val dateEditText = EditText(this).apply {
                // 로컬 날짜의 현재 년도와 월 가져오기
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
                val formattedDate = currentDate.format(formatter)
                hint = formattedDate
                //hint = "2024년 11월"
                isFocusable = false
                setBackgroundResource(R.drawable.onboard_et_gray)
                layoutParams = LinearLayout.LayoutParams(
                    0, // 너비를 0으로 설정하고 weight를 사용하여 비율로 설정
                    resources.getDimensionPixelSize(R.dimen.onboard_et_height),
                    2f // 가중치 설정
                )
                gravity = Gravity.CENTER // 텍스트를 중앙 정렬
                setOnClickListener {
                    showDatePickerDialog(this)
                }
                setHintTextColor(Color.parseColor("#6C6B6B")) // 힌트 색상 변경
                setPadding(dpToPx(0f).toInt(), dpToPx(10f).toInt(), 0, dpToPx(10f).toInt()) // 왼쪽 여백 추가
            }
            horizontalLayout.addView(dateEditText)
            dateEditTexts.add(dateEditText)
        }

        btnNext = findViewById(R.id.btn_next)
        updateNextButtonState() // 초기 상태 설정

        btnNext.setOnClickListener {
            if (dateEditTexts.all { it.text.isNotBlank() }) {
                // 모든 EditText의 값을 HashMap에 저장
                dateEditTexts.forEachIndexed { index, editText ->
                    val diseaseName = selectedDiseases[index] // 질병 이름
                    diseaseStartDates[diseaseName] = editText.text.toString() // 질병과 날짜 매핑
                    Log.d("DiseaseStartDates", "Saved $diseaseName with date: ${editText.text}")
                }
                Log.d("DiseaseStartDates", "Final map: $diseaseStartDates")
                val intent = Intent(this, Onboard3Activity::class.java).apply {
                    putExtra("email", email)
                    putExtra("password", password)
                    putExtra("name", name)
                    putStringArrayListExtra("selectedDiseases", ArrayList(selectedDiseases))
                    putExtra("diseaseStartDates", diseaseStartDates) // HashMap 전달
                }
                startActivity(intent)
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.date_picker)

        val yearPicker: NumberPicker = dialog.findViewById(R.id.yearPicker)
        val monthPicker: NumberPicker = dialog.findViewById(R.id.monthPicker)
        val btnCancel: TextView = dialog.findViewById(R.id.btn_cancel)
        val btnConfirm: TextView = dialog.findViewById(R.id.btn_confirm)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 100..currentYear + 100).map { "${it}년" }.toTypedArray()
        yearPicker.minValue = 0
        yearPicker.maxValue = years.size - 1
        yearPicker.displayedValues = years
        yearPicker.value = years.indexOf("${currentYear}년")

        val months = (1..12).map { "${it}월" }.toTypedArray()
        monthPicker.minValue = 0
        monthPicker.maxValue = months.size - 1
        monthPicker.displayedValues = months
        monthPicker.value = Calendar.getInstance().get(Calendar.MONTH)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val selectedYear = years[yearPicker.value]
            val selectedMonth = months[monthPicker.value]

            val selectedDate = "$selectedYear $selectedMonth"
            editText.setText(selectedDate)
            editText.setBackgroundResource(R.drawable.onboard_et_blue) // 배경색 변경
            updateNextButtonState() // 상태 업데이트
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateNextButtonState() {
        if (dateEditTexts.all { it.text.isNotBlank() }) {
            btnNext.isEnabled = true
            btnNext.setBackgroundResource(R.drawable.onboard_btn_active)
        } else {
            btnNext.isEnabled = false
            btnNext.setBackgroundResource(R.drawable.onboard_btn_inactive)
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}