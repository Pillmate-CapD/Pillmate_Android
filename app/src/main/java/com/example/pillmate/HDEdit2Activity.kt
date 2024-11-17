package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityHalthdiary2Binding

class HDEdit2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHalthdiary2Binding
    private var id: Int = 0
    private var date: String? = null
    private var selectedSymptoms: ArrayList<String>? = null
    private var painScore: Int = 1 // 기본 점수는 1로 설정
    private var comment: String? = null
    private var record: String? = null

    private val logTag = "HDEdit2ActivityLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalthdiary2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        // 뒤로가기 버튼 설정
        binding.tsBack.setOnClickListener {
            onBackPressed()
        }

        // Intent로 date, score, comment, record, 선택된 증상 리스트 받기
        date = intent.getStringExtra("date")
        id = intent.getIntExtra("id", 0)
        painScore = intent.getIntExtra("score", 1)
        comment = intent.getStringExtra("comment")
        record = intent.getStringExtra("record")
        selectedSymptoms = intent.getStringArrayListExtra("selectedSymptoms")

        // 받은 데이터를 로그로 확인

        Log.d(logTag, "받은 데이터: date=$date,id=$id, score=$painScore, comment=$comment, record=$record, selectedSymptoms=$selectedSymptoms")

        // SeekBar 초기값 설정
        binding.seekBar.progress = painScore
        updateSeekBarUI(painScore)

        // SeekBar 리스너 설정
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                painScore = progress
                updateSeekBarUI(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // d_btn_f 버튼 클릭 시 HealthDiary3Activity로 이동
        binding.dBtnF.setOnClickListener {
            val intent = Intent(this, HDEdit3Activity::class.java)
            intent.putExtra("date", date)
            intent.putExtra("id", id)
            intent.putStringArrayListExtra("selectedSymptoms", selectedSymptoms)
            intent.putExtra("painScore", painScore)
            intent.putExtra("comment", comment)
            intent.putExtra("record", record)

            // 전송할 데이터 로그 확인
            Log.d(logTag, "전송할 데이터: date=$date,id=$id, painScore=$painScore, comment=$comment, record=$record, selectedSymptoms=$selectedSymptoms")

            startActivity(intent)
        }
    }

    // SeekBar의 UI 업데이트 함수
    private fun updateSeekBarUI(score: Int) {
        val drawableResId: Int
        val scoreText: String
        val descriptionText: String

        when (score) {
            1 -> {
                drawableResId = R.drawable.outer_circle_drawable
                scoreText = "1점"
                descriptionText = "\"통증이 미미하거나 없어요\""
            }
            2 -> {
                drawableResId = R.drawable.outer_circle2_drawable
                scoreText = "2점"
                descriptionText = "\"약간의 통증이 있지만 문제는 없어요\""
            }
            3 -> {
                drawableResId = R.drawable.outer_circle3_drawable
                scoreText = "3점"
                descriptionText = "\"통증이 상당한 편이에요\""
            }
            4 -> {
                drawableResId = R.drawable.outer_circle4_drawable
                scoreText = "4점"
                descriptionText = "\"통증이 일상생활을 방해해요\""
            }
            5 -> {
                drawableResId = R.drawable.outer_circle5_drawable
                scoreText = "5점"
                descriptionText = "\"통증때문에 일상생활이 어려워요\""
            }
            6 -> {
                drawableResId = R.drawable.outer_circle6_drawable
                scoreText = "6점"
                descriptionText = "\"통증 때문에 다른 일을 할 수 없어요\""
            }
            7 -> {
                drawableResId = R.drawable.outer_circle7_drawable
                scoreText = "7점"
                descriptionText = "\"통증이 상당한 편이에요\""
            }
            8 -> {
                drawableResId = R.drawable.outer_circle8_drawable
                scoreText = "8점"
                descriptionText = "\"통증이 상당해서 참기가 어려워요\""
            }
            9 -> {
                drawableResId = R.drawable.outer_circle9_drawable
                scoreText = "9점"
                descriptionText = "\"거의 최대의 통증이에요\""
            }
            10 -> {
                drawableResId = R.drawable.outer_circle10_drawable
                scoreText = "10점"
                descriptionText = "\"표현할 수 없는 최대의 통증이에요\""
            }
            else -> {
                drawableResId = R.drawable.outer_circle_drawable
                scoreText = "1점"
                descriptionText = "\"통증이 미미하거나 없어요\""
            }
        }

        binding.outerCircle.setBackgroundResource(drawableResId)
        binding.tvScore.text = scoreText
        binding.tvDescription.text = descriptionText
    }
}
