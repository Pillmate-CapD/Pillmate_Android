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

        // 확인 버튼에 리스너 설정
        binding.btnRegister.setOnClickListener {
            // UserFragment로 전환
            val fragment = UserFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // 프래그먼트를 넣을 레이아웃의 ID를 사용하세요
                .addToBackStack(null) // 뒤로 가기 눌렀을 때 이전 상태로 돌아가기 위해 백스택에 추가
                .commit()
        }
    }
    }