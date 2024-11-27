package com.example.pillmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.pillmate.databinding.ActivityHelp1Binding

class Help1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHelp1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelp1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // X 아이콘 클릭 이벤트: UserFragment로 이동
        binding.closeIcon.setOnClickListener {
            navigateToUserFragment()
        }

        // 다음 아이콘 클릭 이벤트: Help2Activity로 이동
        binding.nextIcon.setOnClickListener {
            val intent = Intent(this, Help2Activity::class.java)
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
