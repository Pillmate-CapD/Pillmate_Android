package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityPwchangeOkBinding

class PwChangeOkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPwchangeOkBinding // ViewBinding 변수를 lateinit으로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivityPwchangeOkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // btn_register 버튼 클릭 시 이전 화면(UserFragment)으로 돌아가기
        binding.btnRegister.setOnClickListener {
            finish()  // 현재 Activity 종료
        }
    }
}