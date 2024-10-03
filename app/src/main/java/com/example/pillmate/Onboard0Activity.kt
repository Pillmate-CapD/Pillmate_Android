package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityOnboard0Binding


class Onboard0Activity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboard0Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivityOnboard0Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시작하기 버튼 클릭 시 Onboard1Activity로 이동
        binding.startbt.setOnClickListener {
            // 전달받은 데이터 가져오기
            val email = intent.getStringExtra("email") ?: ""
            val password = intent.getStringExtra("password") ?: ""
            val name = intent.getStringExtra("name") ?: ""

            // Onboard1Activity로 데이터 전달
            val intent = Intent(this, Onboard1Activity::class.java).apply {
                putExtra("email", email)
                putExtra("password", password)
                putExtra("name", name)
            }
            startActivity(intent)
        }
    }
}