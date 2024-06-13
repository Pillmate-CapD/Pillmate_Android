package com.example.pillmate

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Onboard3Activity : AppCompatActivity() {

    private lateinit var btnFinish: Button
    private val editTextList = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard3)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            onBackPressed() // 이전 화면으로 돌아가기
        }

        val editTextWakeup: EditText = findViewById(R.id.et_wakeup)
        val editTextBreakfast: EditText = findViewById(R.id.et_breakfast)
        val editTextLunch: EditText = findViewById(R.id.et_lunch)
        val editTextDinner: EditText = findViewById(R.id.et_dinner)
        val editTextSleep: EditText = findViewById(R.id.et_sleep)

        editTextList.add(editTextWakeup)
        editTextList.add(editTextBreakfast)
        editTextList.add(editTextLunch)
        editTextList.add(editTextDinner)
        editTextList.add(editTextSleep)

        editTextWakeup.setOnClickListener { showTimePickerDialog(editTextWakeup) }
        editTextBreakfast.setOnClickListener { showTimePickerDialog(editTextBreakfast) }
        editTextLunch.setOnClickListener { showTimePickerDialog(editTextLunch) }
        editTextDinner.setOnClickListener { showTimePickerDialog(editTextDinner) }
        editTextSleep.setOnClickListener { showTimePickerDialog(editTextSleep) }

        btnFinish = findViewById(R.id.btn_finish)
        updateFinishButtonState() // 초기 상태 설정

        btnFinish.setOnClickListener {
            // 다음 화면으로 이동 또는 완료 액션
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.time_picker)

        val timePicker: TimePicker = dialog.findViewById(R.id.timePicker)
        val btnCancel: TextView = dialog.findViewById(R.id.btn_cancel)
        val btnConfirm: TextView = dialog.findViewById(R.id.btn_confirm)

        timePicker.setIs24HourView(false)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val selectedHour = if (android.os.Build.VERSION.SDK_INT >= 23) {
                timePicker.hour
            } else {
                timePicker.currentHour
            }
            val selectedMinute = if (android.os.Build.VERSION.SDK_INT >= 23) {
                timePicker.minute
            } else {
                timePicker.currentMinute
            }

            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedTime.set(Calendar.MINUTE, selectedMinute)

            val formatter = SimpleDateFormat("a  hh:mm", Locale.KOREA)
            val formattedTime = formatter.format(selectedTime.time)

            editText.setText(formattedTime)
            editText.setBackgroundResource(R.drawable.onboard_et_blue) // 배경색 변경
            updateFinishButtonState() // 상태 업데이트
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateFinishButtonState() {
        if (editTextList.all { it.text.isNotBlank() }) {
            btnFinish.isEnabled = true
            btnFinish.setBackgroundResource(R.drawable.onboard_btn_active)
        } else {
            btnFinish.isEnabled = false
            btnFinish.setBackgroundResource(R.drawable.onboard_btn_inactive)
        }
    }
}
