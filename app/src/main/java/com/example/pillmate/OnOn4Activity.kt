package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pillmate.databinding.ActivityOnon2Binding
import com.example.pillmate.databinding.ActivityOnon3Binding
import com.example.pillmate.databinding.ActivityOnon4Binding

class OnOn4Activity : AppCompatActivity() {
    private lateinit var binding : ActivityOnon4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnon4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}