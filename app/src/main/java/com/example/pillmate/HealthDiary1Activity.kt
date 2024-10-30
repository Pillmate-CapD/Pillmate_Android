package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary1Binding

class HealthDiary1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        // d_btn_f 버튼 클릭 시 HealthDiary2Activity로 이동
        binding.dBtnF.setOnClickListener {
            val intent = Intent(this, HealthDiary2Activity::class.java)
            startActivity(intent)
        }

    }
}