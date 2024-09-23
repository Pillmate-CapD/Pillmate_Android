package com.example.pillmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pillmate.databinding.ActivityPreMediBinding
import com.example.pillmate.databinding.ActivityPrescriptBinding

class PreMediActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreMediBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreMediBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}