package com.example.pillmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pillmate.databinding.ActivityAddMediFinBinding
import com.example.pillmate.databinding.ActivityMediCheckBinding

class MediCheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}