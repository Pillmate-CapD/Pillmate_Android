package com.example.pillmate

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ViewUtils.dpToPx
import java.util.Calendar
import java.util.Locale

class Onboard2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard2)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            onBackPressed() // 이전 화면으로 돌아가기
        }

        val selectedDiseases = intent.getStringArrayListExtra("selectedDiseases") ?: arrayListOf()
        val linearLayout: LinearLayout = findViewById(R.id.linearLayout)

        selectedDiseases.forEach { disease ->
            // 세로로 배치되는 LinearLayout 생성
            val verticalLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            linearLayout.addView(verticalLayout)
            // 여백 추가
            val space1 = Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(10f).toInt()
                )
            }
            verticalLayout.addView(space1)
            // 질병을 표시하는 TextView 생성
            val diseaseTextView = TextView(this).apply {
                text = disease
                textSize = 20f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setTextColor(Color.parseColor("#3E3E3E"))
            }
            verticalLayout.addView(diseaseTextView)

            // 여백 추가
            val space = Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(7f).toInt()
                )
            }
            verticalLayout.addView(space)

            // 날짜를 선택하는 에디트텍스트 생성
            val dateEditText = EditText(this).apply {
                hint = "2024년 4월"
                isFocusable = false
                setBackgroundResource(R.drawable.gray_stroke_et)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelSize(R.dimen.onboard_et_height)
                )
                setOnClickListener {
                    showDatePickerDialog(this)
                }
                setHintTextColor(Color.parseColor("#6C6B6B")) // 힌트 색상 변경
                setPadding(dpToPx(20f).toInt(), 0, 0, 0) // 왼쪽 여백 추가
            }
            verticalLayout.addView(dateEditText)
        }

        val btnNext: Button = findViewById(R.id.btn_next)
        btnNext.setOnClickListener {
            // Save selected dates and pass to the next activity
            val intent = Intent(this, Onboard3Activity::class.java)
            startActivity(intent)
        }
    }


    /*private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            editText.setText(date)
        }, year, month, day)
        datePickerDialog.show()
    }*/
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
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}