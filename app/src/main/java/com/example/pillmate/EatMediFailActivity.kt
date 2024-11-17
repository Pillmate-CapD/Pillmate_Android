package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pillmate.databinding.ActivityAfterScanBinding
import com.example.pillmate.databinding.ActivityFailBinding

class EatMediFailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvChange.text= "지금 먹어야 할 약이 아니에요"
        binding.tvChange1.text= "지금 내가 먹을 약이 맞는지 다시 한 번 확인해 주세요"

        val checkActivity = intent.getStringExtra("CheckActivity")
        if (checkActivity == "preActivity") {
            binding.tvChange.text= "처방전 인식에 실패했어요"
            binding.tvChange1.text= "카메라에 이물질이 있거나 초점이 맞지 않으면\n" + "오류가 발생할 수 있어요\n" + "텍스트가 잘 보이도록 다시 촬영해 주세요"

            // 이전으로
            binding.btnBack.setOnClickListener {
                finish()
            }

            // 다시 사진 찍기
            binding.btnRetake.setOnClickListener {val intent =
                Intent(this@EatMediFailActivity, PrescriptActivity::class.java).apply {
                }
                startActivity(intent)
                finish()
            }
        }
        else{
            // 이전으로
            binding.btnBack.setOnClickListener {
                finish()
            }

            // 다시 사진 찍기
            binding.btnRetake.setOnClickListener {val intent =
                Intent(this@EatMediFailActivity, EatMediScanActivity::class.java).apply {
                }
                startActivity(intent)
                finish()
            }
        }
    }
}