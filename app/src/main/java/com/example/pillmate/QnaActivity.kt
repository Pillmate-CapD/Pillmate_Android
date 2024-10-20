package com.example.pillmate

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityQnaBinding

class QnaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQnaBinding
    private var isAnswer1Visible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQnaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼 클릭 시 이전 화면으로 돌아가기
        binding.pwcBack.setOnClickListener {
            finish()
        }

        /*// 첫 번째 질문 토글 동작 설정
        binding.tvQuestion1.setOnClickListener {
            Log.d("QnaActivity", "Question 1 clicked")
            toggleAnswer(
                isAnswer1Visible,
                binding.ivArrow1,
                binding.tvAnswer1
            )
            isAnswer1Visible = !isAnswer1Visible
            Log.d("QnaActivity", "isAnswer1Visible: $isAnswer1Visible")
        }*/
    }

    // 답변 토글 메소드
    private fun toggleAnswer(isVisible: Boolean, arrow: ImageView, answer: TextView) {
        Log.d("QnaActivity", "Toggling answer visibility: isVisible = $isVisible")
        if (isVisible) {
            // 답변 숨기기
            answer.visibility = View.GONE
            arrow.setImageResource(R.drawable.qnadown)
            Log.d("QnaActivity", "Answer hidden")
            //arrow.animate().rotation(0f).setDuration(300).start()  // 화살표 원상복귀
        } else {
            // 답변 보이기
            answer.visibility = View.VISIBLE
            arrow.setImageResource(R.drawable.qnadown)
            Log.d("QnaActivity", "Answer visible")
            //arrow.animate().rotation(180f).setDuration(300).start()  // 화살표 180도 회전
        }
    }
}
