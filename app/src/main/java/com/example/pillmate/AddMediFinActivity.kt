package com.example.pillmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.example.pillmate.databinding.ActivityAddMediFinBinding
import com.example.pillmate.databinding.ActivityAfterPreBinding

class AddMediFinActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddMediFinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMediFinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 전달된 메시지를 받아옴
        val successMessage = intent.getStringExtra("successMessage")
        val editMessage = intent.getStringExtra("editMessage")

        // 전달된 메시지가 null이 아닐 경우 텍스트를 변경
        successMessage?.let {
            binding.tv1.text = it // 원하는 텍스트뷰의 ID로 변경
            binding.tv2.text = "수정된 약은 [약 리스트] 화면에서\n" +
                    "확인 가능해요"
        }

        editMessage?.let{
            binding.tv1.text = it // 원하는 텍스트뷰의 ID로 변경
            binding.tv2.text = "[약 리스트] 화면에서 삭제된 약\n" +
                    "확인이 가능해요"
        }

        binding.btnConfirm.setOnClickListener {
            finish()
        }
    }
}