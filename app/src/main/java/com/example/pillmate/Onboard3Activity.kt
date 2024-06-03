package com.example.pillmate

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Onboard3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard3)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            onBackPressed() // 이전 화면으로 돌아가기
        }
        val btn_finish:Button=findViewById(R.id.btn_finish)
        btn_finish.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val editTextWakeup: EditText = findViewById(R.id.et_wakeup)
        val editTextBreakfast: EditText = findViewById(R.id.et_breakfast)
        val editTextLunch: EditText = findViewById(R.id.et_lunch)
        val editTextDinner: EditText = findViewById(R.id.et_dinner)
        val editTextSleep: EditText = findViewById(R.id.et_sleep)

        editTextWakeup.setOnClickListener { showTimePickerDialog(editTextWakeup) }
        editTextBreakfast.setOnClickListener { showTimePickerDialog(editTextBreakfast) }
        editTextLunch.setOnClickListener { showTimePickerDialog(editTextLunch) }
        editTextDinner.setOnClickListener { showTimePickerDialog(editTextDinner) }
        editTextSleep.setOnClickListener { showTimePickerDialog(editTextSleep) }

        val btnFinish: Button = findViewById(R.id.btn_finish)
        btnFinish.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
            dialog.dismiss()
        }

        dialog.show()
    }
    /*private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            editText.setText(time)
        }, hour, minute, true)

        // TimePickerDialog의 스피너만 표시하고 다른 요소들을 숨김
        timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        timePickerDialog.show()
    }*/
}