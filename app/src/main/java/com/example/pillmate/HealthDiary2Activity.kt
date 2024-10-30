package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary2Binding

class HealthDiary2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        // d_btn_f 버튼 클릭 시 HealthDiary2Activity로 이동
        binding.dBtnF.setOnClickListener {
            val intent = Intent(this, HealthDiary3Activity::class.java)
            startActivity(intent)
        }

    }
}