package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityMyhealthinfoOkBinding

class MyHealthEditOkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyhealthinfoOkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 설정
        binding = ActivityMyhealthinfoOkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 버튼 클릭 리스너 설정
        binding.btnRegister.setOnClickListener {
            // MyHealthInfoActivity로 이동
            val intent = Intent(this, MyHealthInfoActivity::class.java)
            startActivity(intent)
            // 현재 액티비티 종료
            finish()
        }
    }
}

