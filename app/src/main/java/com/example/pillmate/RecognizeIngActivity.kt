package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class RecognizeIngActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognizemedi_ing)

        Handler().postDelayed({
            // 사진 경로를 Intent로 전달 받음
            val photoPath = intent.getStringExtra("photoPath")
            val resultIntent = Intent(this, EatMediActivity::class.java)
            resultIntent.putExtra("photoPath", photoPath)
            setResult(RESULT_OK, resultIntent)
            finish()
        }, 2000) // 2초 후에 RecognitionActivity를 종료하고 결과를 전달
    }
}