package com.example.pillmate

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pillmate.databinding.ActivityAfterPreBinding
import com.example.pillmate.databinding.ActivityAfterScanBinding

class AfterScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfterScanBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityAfterScanBinding.inflate(layoutInflater)
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
            binding.loadingLayout.visibility = View.GONE
            this@AfterScanActivity.finish()
        }

        binding.btnAgain.setOnClickListener{
            binding.loadingLayout.visibility = View.GONE
            this@AfterScanActivity.finish()
        }

        binding.btnNext.setOnClickListener {
            //OCR 실행을 여기서 바로 할건지 아니면,
//            if(photoPath != null){
//                // BitmapFactory를 사용하여 이미지 파일을 Bitmap으로 로드
//                val bitmap = BitmapFactory.decodeFile(photoPath)
//
//                performOcrWithBitmap(bitmap)
//                binding.loadingLayout.visibility = View.VISIBLE
//            }

            //performOcrWithLocalImage()
            //performOcrWithBitmap(bitmap)
            //binding.loadingLayout.visibility = View.VISIBLE

            this@AfterScanActivity.finish()
        }

        super.onCreate(savedInstanceState)
    }
}