package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary3Binding

class HealthDiary3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // d_btn_f 클릭 시 CalendarFragment가 포함된 Activity로 돌아가도록 설정
        binding.dBtnF.setOnClickListener {
            val intent =
                Intent(this, MainActivity::class.java) // MainActivity에서 CalendarFragment를 로드하는 경우
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)

        }
    }
}