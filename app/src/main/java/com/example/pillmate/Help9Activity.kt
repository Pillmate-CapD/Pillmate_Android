package com.example.pillmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pillmate.databinding.ActivityHelp9Binding

class Help9Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHelp9Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelp9Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // X 아이콘 클릭 이벤트: UserFragment로 이동
        binding.closeIcon.setOnClickListener {
            navigateToUserFragment()
        }

        // 다음 아이콘 클릭 이벤트: Help3Activity로 이동
        binding.nextIcon.setOnClickListener {
            val intent = Intent(this, Help10Activity::class.java)
            startActivity(intent)
        }

        // 이전 아이콘 클릭 이벤트: Help1Activity로 이동
        binding.prevIcon.setOnClickListener {
            val intent = Intent(this, Help1Activity::class.java)
            startActivity(intent)
        }
    }

    // UserFragment로 이동
    private fun navigateToUserFragment() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, UserFragment())
            .commit()
    }
}