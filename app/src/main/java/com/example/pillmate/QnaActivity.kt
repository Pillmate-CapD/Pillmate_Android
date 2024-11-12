package com.example.pillmate

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QnaActivity : AppCompatActivity() {

    // TextView와 ImageView 리스트 선언
    private val answerTextViews = mutableListOf<TextView>()
    private val arrowImageViews = mutableListOf<ImageView>()
    private val answerVisibilities = BooleanArray(9) // 각 답변의 가시성 상태 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qna)

        // 뒤로 가기 버튼 설정
        val backButton = findViewById<ImageView>(R.id.pwc_back)
        backButton.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        // 반복문으로 ID 연결 및 클릭 리스너 설정
        for (i in 1..9) {
            // TextView와 ImageView, 질문 Layout을 동적으로 가져오기
            val questionLayoutId = resources.getIdentifier("qt$i", "id", packageName)
            val answerId = resources.getIdentifier("tv_answer$i", "id", packageName)
            val arrowId = resources.getIdentifier("iv_arrow$i", "id", packageName)

            val questionLayout = findViewById<LinearLayout>(questionLayoutId)
            val tvAnswer = findViewById<TextView>(answerId)
            val ivArrow = findViewById<ImageView>(arrowId)

            answerTextViews.add(tvAnswer)
            arrowImageViews.add(ivArrow)

            // 질문 Layout에 클릭 리스너 설정
            questionLayout.setOnClickListener {
                toggleAnswer(i - 1) // 인덱스 i-1 전달 (0부터 시작하도록)
            }
        }
    }

    private fun toggleAnswer(index: Int) {
        // 현재 상태에 따라 가시성 및 애니메이션 설정
        val isCurrentlyVisible = answerVisibilities[index]
        if (isCurrentlyVisible) {
            // 답변이 보이는 상태에서 감추기
            answerTextViews[index].visibility = View.GONE
            // 화살표 이미지 180도에서 0도로 회전 (접기)
            ObjectAnimator.ofFloat(arrowImageViews[index], "rotation", 180f, 0f).start()
        } else {
            // 답변이 숨겨진 상태에서 보이게 하기
            answerTextViews[index].visibility = View.VISIBLE
            // 화살표 이미지 0도에서 180도로 회전 (펼치기)
            ObjectAnimator.ofFloat(arrowImageViews[index], "rotation", 0f, 180f).start()
        }
        // 가시성 상태 반대로 토글
        answerVisibilities[index] = !isCurrentlyVisible
    }
}

