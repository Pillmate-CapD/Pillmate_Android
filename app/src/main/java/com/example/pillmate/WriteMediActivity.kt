package com.example.pillmate

import TimeSlotAdapter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.ActivityWriteMediBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriteMediActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteMediBinding
    private lateinit var timeSlotAdapter: TimeSlotAdapter

    // TimeSlotItem 리스트로 변경 및 초기 아이템 추가
    private val timeSlots = mutableListOf(
        TimeSlotItem(generateUniqueId(), "기상 직후")
    )

    // 고유 ID 생성을 위한 변수 및 함수
    private var idCounter = 0
    private fun generateUniqueId(): Int {
        return idCounter++
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 설정
        binding = ActivityWriteMediBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 리사이클러뷰 설정
        timeSlotAdapter = TimeSlotAdapter(timeSlots) { position ->
            showTimePickerBottomSheet(position)
        }
        binding.timeSlotRecy.apply {
            layoutManager = LinearLayoutManager(this@WriteMediActivity)
            adapter = timeSlotAdapter
        }

        // 취소 버튼 클릭 리스너
        binding.tvCancel.setOnClickListener {
            finish()
        }

        // 시간대 추가 버튼 클릭 리스너
        binding.btnAddTime.setOnClickListener {
            addNewTimeSlot()
        }

        // 저장 버튼 클릭 리스너
        binding.tvSave.setOnClickListener {
            checkFieldsAndShowToast()
        }
    }

    // TODO: 1) spinnerTime이랑 pickerTime 다 받아야되니까 그 값을 같이 저장할 수 있도록 해줘 => 서버도 변경되어야 할듯
    // TODO: 2) spinnerTime이랑 pickerTime이랑 리스트형태로 받을 수 있는지? => 서버


    // 필수 필드 확인 및 커스텀 토스트 메시지 표시
    private fun checkFieldsAndShowToast() {
        // 필수 필드 체크
        val isMediNameEmpty = binding.editMedi.text.isNullOrEmpty()
        val isDiseaseEmpty = binding.editDisease.text.isNullOrEmpty()
        val isOneEatEmpty = binding.editOneEat.text.isNullOrEmpty()
        val isOneDayEmpty = binding.editOneDay.text.isNullOrEmpty()
        val isAllDayEmpty = binding.editAllDay.text.isNullOrEmpty()

        // 모든 시간대가 선택되었는지 확인 (spinnerTime과 pickerTime 모두)
        val areAllTimeSlotsSelected = timeSlots.all { it.isTimeSelected && it.isTimeChanged }

        // 각 필드별로 확인하고 메시지 띄우기
        when {
            isMediNameEmpty -> {
                showCustomToast("약품명을 입력해주세요.")
            }
            isDiseaseEmpty -> {
                showCustomToast("질병을 입력해주세요.")
            }
            isOneEatEmpty -> {
                showCustomToast("1회 복약량을 입력해주세요.")
            }
            isOneDayEmpty -> {
                showCustomToast("1일 복약횟수를 입력해주세요.")
            }
            isAllDayEmpty -> {
                showCustomToast("총 복약일수를 입력해주세요.")
            }
            !areAllTimeSlotsSelected -> {
                showCustomToast("복약 시간을 입력해주세요.")
            }
            else -> {
                // 모든 필드가 채워져 있으면 저장 로직 수행
                // EditText에서 값을 받아서 처리
                val mediName = binding.editMedi.text.toString()  // 약품명
                val disease = binding.editDisease.text.toString() // 질병
                val oneEat = binding.editOneEat.text.toString().toInt()  // 1회 복약량
                val oneDay = binding.editOneDay.text.toString().toInt()  // 1일 복약횟수
                val allDay = binding.editAllDay.text.toString().toInt()  // 총 복약일수
                // 복약 시간대도 데이터를 받을 필요가 있다면 처리 추가
                //val times = timeSlots.map { it.timeValue }  // 시간대 데이터

                // TimeSlotItem 리스트를 TimeSlotRequest 리스트로 변환
                val timeSlotRequests = timeSlots.map { timeSlot ->
                    TimeSlotRequest(
                        spinnerTime = timeSlot.timeLabel,
                        pickerTime = timeSlot.time,
                    )
                }
                // 예: MediAddRequest 객체 생성 및 전송
                val mediAddRequest = MediAddRequest(
                    medicineName = mediName,
                    disease = disease,
                    amount = oneEat,
                    timesPerDay = oneDay,
                    day = allDay,
                    timeSlotList = timeSlotRequests
                )
                // 모든 필드가 채워져 있으면 저장 로직 수행
                // 예: 데이터 저장, 서버로 전송 등
                sendMediAdd(mediAddRequest)
            }
        }
    }

    private fun sendMediAdd(mediAddRequest: MediAddRequest) {
        val service = RetrofitApi.getRetrofitService // Retrofit 인스턴스 가져오기
        val call = service.addMedi(mediAddRequest)

        val gson = Gson()
        val requestJson = gson.toJson(mediAddRequest)
        Log.d("WriteMediActivity", "보낸 요청: $requestJson")

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    val message = response.body()
                    // 성공 시 처리할 로직 추가
                    message?.let {
                        Log.d("WriteMediActivity", "직접 작성 성공: $it")
                        showCustomToast(it) // 서버에서 보낸 메시지를 토스트로 표시
                        finish()
                    }
                } else {
                    Log.d("WriteMediActivity", "직접 작성하기 실패 : ${response.code()}, ${response.errorBody()?.string()}")
                    showCustomToast("약 추가에 실패했습니다: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("WriteMediActivity", "API 호출 실패", t)
                showCustomToast("API 호출 실패: ${t.message}")
            }
        })
    }

    // 커스텀 토스트 메시지를 띄우는 함수
    private fun showCustomToast(message: String) {
        // 커스텀 토스트 레이아웃을 인플레이트
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        // 메시지 설정
        val textView = layout.findViewById<TextView>(R.id.tv_toast)
        textView.text = message

        // 커스텀 토스트 생성 및 설정
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM, 0, 80) // 화면 하단에 표시
        toast.show()
    }

    // 새로운 시간대 항목을 추가하는 함수
    private fun addNewTimeSlot() {
        val newItem = TimeSlotItem(generateUniqueId(), "기상 직후")
        timeSlotAdapter.addTimeSlot(newItem)
    }

    // BottomSheetDialog를 열어 시간대를 선택하는 함수
    private fun showTimePickerBottomSheet(position: Int) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.write_time_picker, null)

        // BottomSheetDialog에 레이아웃 설정
        bottomSheetDialog.setContentView(bottomSheetView)

        // 배경 흐림과 색상 설정
        bottomSheetDialog.window?.apply {
            // 다이얼로그 자체 배경을 투명하게 설정
            setBackgroundDrawableResource(android.R.color.transparent)

            // 배경 흐림 설정
            setDimAmount(0.6f) // 0.0f ~ 1.0f로 흐림 정도 설정

            // 뒷배경 색상 설정 (검정색에 40% 투명도)
            decorView.setBackgroundColor(Color.parseColor("#66000000")) // 검정색 + 40% 투명도
        }

        // 옵션별 텍스트뷰와 레이블 매핑
        val timeOptions = mapOf(
            bottomSheetView.findViewById<TextView>(R.id.tv_after_wu) to "기상 직후",
            bottomSheetView.findViewById<TextView>(R.id.tv_after_bf) to "아침 식후",
            bottomSheetView.findViewById<TextView>(R.id.tv_before_lunch) to "점심 식전",
            bottomSheetView.findViewById<TextView>(R.id.tv_after_lunch) to "점심 식후",
            bottomSheetView.findViewById<TextView>(R.id.tv_before_dinner) to "저녁 식전",
            bottomSheetView.findViewById<TextView>(R.id.tv_after_dinner) to "저녁 식후",
            bottomSheetView.findViewById<TextView>(R.id.tv_before_sleep) to "취침 직전",
            bottomSheetView.findViewById<TextView>(R.id.tv_else) to "기타         "
        )

        // 각 옵션에 클릭 리스너 설정
        timeOptions.forEach { (view, label) ->
            view.setOnClickListener {
                timeSlotAdapter.updateTimeSlot(position, label)
                bottomSheetDialog.dismiss()
            }
        }

        // BottomSheetDialog 표시
        bottomSheetDialog.show()
    }
}
