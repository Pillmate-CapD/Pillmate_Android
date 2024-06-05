package com.example.pillmate

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EatMediActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EatMediAdapter
    private val steps = mutableListOf<EatMedi>()  // 단계 리스트를 저장하는 MutableList
    private val CAMERA_REQUEST_CODE = 1001

    private var itemPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat_medi)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 단계 리스트 초기화
        steps.add(EatMedi(R.layout.eat_medi_item1, isVisible = true, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item2, isVisible = false, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item3, isVisible = false, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item4, isVisible = false, isCompleted = false))

        // 어댑터 초기화 및 RecyclerView에 설정
        adapter = EatMediAdapter(steps, this::onStepButtonClick, this::onSkipButtonClick)
        recyclerView.adapter = adapter

        // Intent에서 전달된 position 값을 저장
        itemPosition = intent.getIntExtra("position", -1)
    }

    // 단계 버튼 클릭 시 호출되는 메서드
    private fun onStepButtonClick(position: Int) {
        if (position == 0) {
            // 첫 번째 단계에서 버튼 클릭 시 CameraActivity로 이동
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else if (position < steps.size - 1) {
            steps[position].isVisible = false
            steps[position].isCompleted = true
            steps[position + 1].isVisible = true
        } else if (position == steps.size - 1) {
            // 마지막 단계에서 버튼 클릭 시 메인 페이지로 이동
            val resultIntent = Intent()
            resultIntent.putExtra("completed", true)
            resultIntent.putExtra("position", itemPosition) // position 값도 전달
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        adapter.notifyDataSetChanged()
    }

    // Pass 버튼 클릭 시 호출되는 메서드 (첫 번째 단계에만 적용)
    private fun onSkipButtonClick(position: Int) {
        if (position == 0 && steps.size > 2) {
            steps[position].isVisible = false
            steps[position].isCompleted = true
            steps[1].isCompleted = true // 두 번째 단계를 완료 상태로 변경
            steps[2].isVisible = true
        }
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // CameraActivity에서 사진을 찍고 돌아왔을 때
            val photoPath = data?.getStringExtra("photoPath")
            if (photoPath != null) {
                // 사진을 Bitmap으로 변환하여 2단계 이미지 뷰에 설정
                val bitmap = BitmapFactory.decodeFile(photoPath)
                steps[0].isVisible = false
                steps[0].isCompleted = true
                steps[1].isVisible = true
                steps[1].photoPath = photoPath  // 2단계에 사진 경로 저장
                adapter.notifyDataSetChanged()
            }
        }
    }
}