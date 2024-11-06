package com.example.pillmate

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
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

        findViewById<ImageView>(R.id.back).setOnClickListener {
            showExitDialog()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Intent에서 전달된 데이터를 받아옴
        val pillName = intent.getStringExtra("pill_name") ?: "Unknown"
        val photourl = intent.getStringExtra("pill_image_url")
        val source = intent.getStringExtra("source") ?: "default"

        // 단계 리스트 초기화
        steps.add(EatMedi(R.layout.eat_medi_item1, isVisible = true, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item2, isVisible = false, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item3, isVisible = false, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item4, isVisible = false, isCompleted = false))

        // 어댑터 초기화 및 RecyclerView에 설정
        adapter = EatMediAdapter(steps, this::onStepButtonClick, this::onSkipButtonClick)
        recyclerView.adapter = adapter

        // Intent에서 전달된 사진 경로를 받아옴
        val photoPath = intent.getStringExtra("photoPath")

        // 사진 경로가 있다면 두 번째 단계에 해당 사진을 설정
        if (!photoPath.isNullOrEmpty()) {
            steps[1].isVisible = true
            steps[1].photoPath = photoPath
        }

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
    // 다이얼로그를 표시하는 메서드
    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_eatmedi_f, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 확인 버튼 클릭 시 이전 화면으로 돌아가는 동작
        dialogView.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            finish() // 이전 화면으로 돌아가는 코드
            dialog.dismiss()
        }

        // 취소 버튼 클릭 시 다이얼로그만 닫히고 현재 페이지 유지
        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}