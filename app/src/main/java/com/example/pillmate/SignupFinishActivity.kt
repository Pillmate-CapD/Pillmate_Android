package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivitySignupFinBinding

class SignupFinishActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupFinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivitySignupFinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인하기 버튼 클릭 시 LoginActivity로 이동
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}