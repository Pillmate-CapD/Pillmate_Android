package com.example.pillmate

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        //val photourl = intent.getStringExtra("pill_image_url")
        val source = intent.getStringExtra("source") ?: "default"
        //val pillTime = intent.getStringExtra("pill_time") ?: "14:00:00"
        //val medicineId = intent.getIntExtra("medicineId", 0)
        // 복약 과정 다음 알람 정보를 API로 받아옴
        val pillTime = intent.getStringExtra("pill_time") ?: "Unknown"
        val medicineId = intent.getIntExtra("pill_id", -1)
        // Intent에서 받아온 값 로그 출력
        Log.d("fetchNextMedicineInfo", "Intent로 받은 값 - pillName: $pillName, pillTime: $pillTime, medicineId: $medicineId")

        //fetchNextMedicineInfo(pillTime, medicineId)

        // 단계 리스트 초기화
        steps.add(EatMedi(R.layout.eat_medi_item1, isVisible = true, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item2, isVisible = false, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item3, isVisible = false, isCompleted = false))
        steps.add(EatMedi(R.layout.eat_medi_item4, isVisible = false, isCompleted = false))

        // 어댑터 초기화 및 RecyclerView에 설정
        adapter = EatMediAdapter(steps, this::onStepButtonClick, this::onSkipButtonClick)
        recyclerView.adapter = adapter

        steps[0].pillName = pillName // 약명
        steps[1].pillName = pillName

        // Intent에서 전달된 사진 경로를 받아옴 2단계
        val photoPath = intent.getStringExtra("photoPath")

        // 사진 경로가 있다면 두 번째 단계에 해당 사진을 설정
        if (!photoPath.isNullOrEmpty()) {
            // 1단계 접힘 설정
            steps[0].isVisible = false
            steps[0].isCompleted = true
            steps[1].isVisible = true
            steps[1].photoPath = photoPath
        }

        // Intent에서 전달된 position 값을 저장
        itemPosition = intent.getIntExtra("position", -1)
    }

    // 다음 알람 정보를 API로 받아오는 메서드
    private fun fetchNextMedicineInfo(time: String, medicineId: Int) {
        val localTime = convertToLocalTime(time)
        val formattedTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        Log.d("fetchNextMedicineInfo", "변환된 요청 파라미터 - time: $formattedTime, medicineId: $medicineId")

        if (medicineId == -1) {
            Log.e("fetchNextMedicineInfo", "잘못된 medicineId: $medicineId")
            return
        }

        val service = RetrofitApi.getRetrofitService
        val call = service.getMedicineInfo(formattedTime, medicineId)

        call.enqueue(object : Callback<MedicineResponse> {
            override fun onResponse(call: Call<MedicineResponse>, response: Response<MedicineResponse>) {
                if (response.isSuccessful) {
                    val medicineInfo = response.body()
                    medicineInfo?.let {
                        val formattedTimeText = formatTimeTo12Hour(it.time)
                        //val nextMedicineText = "다음에 먹을 약은\n오늘 $formattedTimeText ${it.medicineName}\n이에요"
                        val nextMedicineText = "오늘 $formattedTimeText\n${it.medicineName} 입니다"


                        steps[3].pillName = nextMedicineText
                        Log.d("fetchNextMedicineInfo", "4단계 텍스트 설정: $nextMedicineText")
                        //adapter.notifyItemChanged(3)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("fetchNextMedicineInfo", "API 호출 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MedicineResponse>, t: Throwable) {
                Log.e("fetchNextMedicineInfo", "API 호출 실패: ${t.message}")
            }
        })
    }

    // 시간을 LocalTime 형식("HH:mm:ss")으로 변환
    private fun convertToLocalTime(time: String): LocalTime {
        return try {
            // "오전/오후 h:mm" 형식인지 확인하고 변환
            if (time.matches(Regex("^(오전|오후) \\d{1,2}:\\d{2}$"))) {
                val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA) // 한국 로케일 사용
                val parsedTime = LocalTime.parse(time, formatter)

                // 오전/오후에 따라 24시간 형식으로 변환
                if (time.startsWith("오후") && parsedTime.hour < 12) {
                    parsedTime.plusHours(12)
                } else if (time.startsWith("오전") && parsedTime.hour == 12) {
                    LocalTime.of(0, parsedTime.minute)
                } else {
                    parsedTime
                }
            }
            // 이미 "HH:mm:ss" 형식인 경우 그대로 반환
            else if (time.matches(Regex("\\d{2}:\\d{2}:\\d{2}"))) {
                LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"))
            } else {
                throw IllegalArgumentException("유효하지 않은 시간 형식: $time")
            }
        } catch (e: Exception) {
            Log.e("convertToLocalTime", "시간 변환 오류: ${e.message}")
            LocalTime.MIDNIGHT
        }
    }


    // 24시간 형식("HH:mm:ss")을 12시간 형식("오전/오후")으로 변환
    private fun formatTimeTo12Hour(time: String): String {
        return try {
            val inputFormat = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
            val outputFormat = DateTimeFormatter.ofPattern("a h시 mm분", Locale.KOREA)
            val localTime = LocalTime.parse(time, inputFormat)
            localTime.format(outputFormat)
        } catch (e: Exception) {
            Log.e("formatTimeTo12Hour", "시간 변환 오류: ${e.message}")
            "오전 12시 00분"
        }
    }




    // 단계 버튼 클릭 시 호출되는 메서드
    private fun onStepButtonClick(position: Int) {
        if (position == 0) {
            // 첫 번째 단계에서 버튼 클릭 시 CameraActivity로 이동
            val pillName = intent.getStringExtra("pill_name") ?: "Unknown" // Intent에서 pillName을 가져옴
            val intent = Intent(this, EatMediScanActivity::class.java)
            intent.putExtra("pill_name", pillName) // pillName을 Intent에 추가
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else if (position < steps.size - 1) {

            // Intent에서 전달된 데이터를 받아옴
            val pillTime = intent.getStringExtra("pill_time") ?: "Unknown"
            val medicineId = intent.getIntExtra("pill_id", -1)
            steps[position].isVisible = false
            steps[position].isCompleted = true
            steps[position + 1].isVisible = true

            // 네 번째 단계에서 추가 동작 호출
            if (position == 2) {
                fetchNextMedicineInfo(pillTime, medicineId)
                Log.d("복용단계 3단계-1", "$pillTime,$medicineId")
            }

            Log.d("복용단계 3단계-2", "$pillTime,$medicineId")

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
        Log.d("onActivityResult", "Called with requestCode: $requestCode, resultCode: $resultCode")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // AfterEatMediScanActivity에서 사진을 찍고 돌아왔을 때
            val photoPath = data?.getStringExtra("photoPath")
            if (photoPath != null) {
                // 사진을 Bitmap으로 변환하여 2단계 이미지 뷰에 설정
                val bitmap = BitmapFactory.decodeFile(photoPath)
                steps[0].isVisible = false
                steps[0].isCompleted = true
                steps[1].isVisible = true
                steps[1].photoPath = photoPath  // 2단계에 사진 경로 저장
                // 로그 추가
                Log.d("StepVisibility", "Step 0 Visible: ${steps[0].isVisible}, Completed: ${steps[0].isCompleted}")
                Log.d("StepVisibility", "Step 1 Visible: ${steps[1].isVisible}, PhotoPath: ${steps[1].photoPath}")
                // RecyclerView 갱신
                adapter.notifyItemRangeChanged(0, 2) // 1단계와 2단계만 갱신
                //adapter.notifyDataSetChanged()
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