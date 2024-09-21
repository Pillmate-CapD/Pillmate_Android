package com.example.pillmate

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityAfterPreBinding

class AfterPreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfterPreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 설정
        binding = ActivityAfterPreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 이미지 파일 경로를 받음
        val photoPath = intent.getStringExtra("photoPath")
        if (photoPath != null) {
            // BitmapFactory를 사용하여 이미지 파일을 Bitmap으로 로드
            val bitmap = BitmapFactory.decodeFile(photoPath)

            // ImageView에 비트맵 설정
            binding.imgMediCamera.setImageBitmap(bitmap)
        }

        binding.btnX.setOnClickListener{
            finish()
        }

        binding.btnAgain.setOnClickListener{
            finish()
        }

        binding.btnNext.setOnClickListener {
            //performOcr()
        }
    }
}
