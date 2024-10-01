package com.example.pillmate

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityOnboard3Binding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Onboard3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboard3Binding
    private val selectedButtons = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboard3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 각 버튼에 대해 클릭 리스너 설정
        setupButton(binding.btn1, binding.overlay1, binding.checkIcon1, 1)
        setupButton(binding.btn2, binding.overlay2, binding.checkIcon2, 2)
        setupButton(binding.btn3, binding.overlay3, binding.checkIcon3, 3)
        setupButton(binding.btn4, binding.overlay4, binding.checkIcon4, 4)
        // 나머지 버튼도 동일한 방식으로 추가
        // setupButton(binding.btn5, binding.overlay5, binding.checkIcon5, 5)
        // setupButton(binding.btn6, binding.overlay6, binding.checkIcon6, 6)
        // ...

        // 처음에 '다음' 버튼 비활성화
        binding.btnFinish.isEnabled = false
    }

    private fun setupButton(button: View, overlay: View, checkIcon: View, buttonId: Int) {
        button.setOnClickListener {
            if (overlay.visibility == View.GONE && checkIcon.visibility == View.GONE) {
                // 선택됨 - 투명 오버레이와 체크 아이콘 보이기
                overlay.visibility = View.VISIBLE
                checkIcon.visibility = View.VISIBLE
                selectedButtons.add(buttonId)
            } else {
                // 선택 해제됨 - 투명 오버레이와 체크 아이콘 숨기기
                overlay.visibility = View.GONE
                checkIcon.visibility = View.GONE
                selectedButtons.remove(buttonId)
            }
            // '다음' 버튼 활성화 여부 결정
            binding.btnFinish.isEnabled = selectedButtons.isNotEmpty()
        }
    }
}

