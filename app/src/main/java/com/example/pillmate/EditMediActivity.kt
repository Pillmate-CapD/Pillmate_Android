package com.example.pillmate

import AllFragment
import TimeSlotAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.ActivityEditMediBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Locale


class EditMediActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMediBinding
    private lateinit var timeSlotAdapter: TimeSlotAdapter

    // 고유 ID 생성을 위한 변수 및 함수
    private var idCounter = 0
    private fun generateUniqueId(): Int {
        return idCounter++
    }

    // 복약 시간대를 저장할 리스트
    private val timeSlots = mutableListOf<TimeSlotItem>()

    // 약품 이름을 저장할 변수
    private var oldMediName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding을 사용하여 레이아웃 설정
        binding = ActivityEditMediBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 MediListResponse 객체 받기
        val medi: MediListResponse? = intent.getSerializableExtra("medi_data") as? MediListResponse

        // 복약 시간대를 저장할 리스트
        //val timeSlots = mutableListOf<TimeSlotItem>()

        // medi 객체의 정보를 사용하여 화면에 데이터 표시
        medi?.let {

            oldMediName = it.name
            // 약품명 설정
            binding.editMedi.setText(it.name)

            // 복약량, 복약 횟수, 복약일수 설정
            binding.editOneEat.setText(it.amount.toString())  // 1회 복약량
            binding.editOneDay.setText(it.timesPerDay.toString())  // 1일 복약 횟수
            binding.editAllDay.setText(it.day.toString())  // 총 복약 일수

            // 질병 정보 (예: 카테고리 사용)
            binding.spinnerDisease.text = it.category

            /// 받아온 복약 시간대 추가
            it.timeSlotList.forEach { slot ->
                val formattedSpinnerTime = formatTimeTo12Hour(slot.spinnerTime)  // 오전/오후 형식으로 변환
                val formattedPickerTime = formatTimeTo12Hour(slot.pickerTime)  // 오전/오후 형식으로 변환
                val timeSlotItem = TimeSlotItem(
                    generateUniqueId(),
                    formattedSpinnerTime,
                    formattedPickerTime,
                    isTimeSelected = true,
                    isTimeChanged = true
                )
                timeSlots.add(timeSlotItem)
            }
        }

        binding.spinnerDisease.isSelected = true

        // 리사이클러뷰 어댑터 설정
        timeSlotAdapter = TimeSlotAdapter(this,timeSlots) { position ->
            showTimePickerBottomSheet(position)
        }

        // RecyclerView 설정
        binding.timeSlotRecy.apply {
            layoutManager = LinearLayoutManager(this@EditMediActivity)
            adapter = timeSlotAdapter
        }

        // 시간대 추가 버튼 클릭 리스너
        binding.btnAddTime.setOnClickListener {
            addNewTimeSlot()
        }

        binding.spinnerDisease.setOnClickListener {
            showDiseaseBottomSheet()
        }

        // 취소 버튼 눌렀을 때 액티비티 종료
        binding.tvEditCancel.setOnClickListener {
            this@EditMediActivity.finish()
        }

        // 수정 버튼 눌렀을 때 수정 로직 추가 (예시로 수정된 데이터를 저장하는 로직)
        binding.tvEditDone.setOnClickListener {
            // 수정된 데이터를 서버로 전송하거나 데이터베이스에 저장하는 로직 추가
            checkFieldsAndShowToast()
        }
    }

    // 필수 필드 확인 및 커스텀 토스트 메시지 표시
    private fun checkFieldsAndShowToast() {
        // 필수 필드 체크
        val isMediNameEmpty = binding.editMedi.text.isNullOrEmpty()
        val isDiseaseEmpty = binding.spinnerDisease.text.isNullOrEmpty()
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
                showCustomToast("질병을 선택해주세요.")
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
                val oldMediName = oldMediName
                val newMediName = binding.editMedi.text.toString()  // 약품명
                val category = binding.spinnerDisease.text.toString()
                val oneEat = binding.editOneEat.text.toString().toInt()  // 1회 복약량
                val oneDay = binding.editOneDay.text.toString().toInt()  // 1일 복약횟수
                val allDay = binding.editAllDay.text.toString().toInt()  // 총 복약일수
                // 복약 시간대도 데이터를 받을 필요가 있다면 처리 추가
                //val times = timeSlots.map { it.timeValue }  // 시간대 데이터

                // TimeSlotItem 리스트를 TimeSlotRequest 리스트로 변환
                val timeSlotRequests = timeSlots.map { timeSlot ->
                    TimeSlotRequest(
                        spinnerTime = timeSlot.timeLabel, // 예: "기상 직후"
                        pickerTime = convertTo24HourFormat(timeSlot.time) // 12시간제를 24시간제로 변환한 값
                    )
                }

                // 예: MediAddRequest 객체 생성 및 전송
                val mediEditRequest = oldMediName?.let {
                    MediEditRequest(
                        oldMedicineName = it,
                        newMedicineName = newMediName,
                        category = category,
                        amount = oneEat,
                        timesPerDay = oneDay,
                        day = allDay,
                        timeSlotList = timeSlotRequests
                    )
                }
                // 모든 필드가 채워져 있으면 저장 로직 수행
                // 예: 데이터 저장, 서버로 전송 등
                Log.d("mediEditRequest", "MediEditRequest : ${mediEditRequest}")
                if (mediEditRequest != null) {
                    patchMediEdit(mediEditRequest)
                }
                //addAlarm()
            }
        }
    }

    // 12시간제(오전/오후) 시간을 24시간제 시간으로 변환하는 함수
    private fun convertTo24HourFormat(time: String): String {
        // time이 "오전 06:00" 또는 "오후 09:00" 형식인 경우 처리
        val amPm = time.split(" ")[0] // "오전" 또는 "오후"
        val hourMinute = time.split(" ")[1] // "06:00"
        val hour = hourMinute.split(":")[0].toInt()
        val minute = hourMinute.split(":")[1].toInt()

        // 오전/오후에 따라 시간을 24시간 형식으로 변환
        val hourIn24Format = if (amPm == "오후" && hour < 12) {
            hour + 12
        } else if (amPm == "오전" && hour == 12) {
            0 // 오전 12시는 0시로 변환
        } else {
            hour
        }

        // 24시간 형식으로 포맷팅
        return String.format("%02d:%02d", hourIn24Format, minute)
    }

    private fun patchMediEdit(mediEditRequest: MediEditRequest) {
        val service = RetrofitApi.getRetrofitService // Retrofit 인스턴스 가져오기
        val call = service.patchMedi(mediEditRequest)   // MediAddRequest 전체를 한 번에 전송

        val gson = Gson()
        val requestJson = gson.toJson(mediEditRequest)
        Log.d("EditMediActivity", "보낸 요청: $requestJson")

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val message = response.body()
                    // 성공 시 처리할 로직 추가
                    message?.let {
                        Log.d("EditMediActivity", "약 정보 수정 성공: $it")
                        //showPerfectToast("약 리스트 수정이 완료되었습니다") // 서버에서 보낸 메시지를 토스트로 표시

                        val intent = Intent(this@EditMediActivity, AddMediFinActivity::class.java)
                        intent.putExtra("successMessage", "약 수정이 완료되었어요!")
                        startActivity(intent)
                        finish() // 액티비티 종료
                    }
                } else {
                    Log.d("EditMediActivity", "약 추가 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    showCustomToast("약 수정에 실패했습니다: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("EditMediActivity", "API 호출 실패", t)
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

    // 커스텀 토스트 메시지를 띄우는 함수
    private fun showPerfectToast(message: String) {
        // 커스텀 토스트 레이아웃을 인플레이트
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_perfect_toast, null)

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

    // 시간을 오전/오후 형식으로 변환하는 함수
    private fun formatTimeTo12Hour(time: String): String {
        return try {
            // 원본 시간 형식 (24시간제)
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(time)

            // 변환된 시간 형식 (오전/오후 12시간제)
            val outputFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
            outputFormat.format(date ?: return time) // 변환 실패 시 원본 반환
        } catch (e: Exception) {
            time // 변환 실패 시 원본 반환
        }
    }

    // 새로운 시간대 항목을 추가하는 함수
    private fun addNewTimeSlot() {
        val newItem = TimeSlotItem(generateUniqueId(), "기상 직후", isTimeSelected = false)
        timeSlotAdapter.addTimeSlot(newItem)
    }

    // BottomSheetDialog를 열어 시간대를 선택하는 함수
    private fun showTimePickerBottomSheet(position: Int) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val bottomSheetView = layoutInflater.inflate(R.layout.write_time_picker, null)

        // BottomSheetDialog에 레이아웃 설정
        bottomSheetDialog.setContentView(bottomSheetView)

        // 배경 흐림과 색상 설정
        bottomSheetDialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.3f)
            decorView.setBackgroundColor(Color.parseColor("#66000000"))
        }

        // 선택된 시간대와 매칭
        val selectedTime = timeSlots[position].timeLabel

        // 각 시간대와 체크 아이콘 View 연결
        val timeOptions = mapOf(
            bottomSheetView.findViewById<TextView>(R.id.tv_after_wu) to bottomSheetView.findViewById<View>(R.id.btn_after_wu),
            bottomSheetView.findViewById<TextView>(R.id.tv_after_bf) to bottomSheetView.findViewById<View>(R.id.btn_after_bf),
            bottomSheetView.findViewById<TextView>(R.id.tv_before_lunch) to bottomSheetView.findViewById<View>(R.id.btn_before_lunch),
            bottomSheetView.findViewById<TextView>(R.id.tv_after_lunch) to bottomSheetView.findViewById<View>(R.id.btn_after_lunch),
            bottomSheetView.findViewById<TextView>(R.id.tv_before_dinner) to bottomSheetView.findViewById<View>(R.id.btn_before_dinner),
            bottomSheetView.findViewById<TextView>(R.id.tv_after_dinner) to bottomSheetView.findViewById<View>(R.id.btn_after_dinner),
            bottomSheetView.findViewById<TextView>(R.id.tv_before_sleep) to bottomSheetView.findViewById<View>(R.id.btn_before_sleep),
            bottomSheetView.findViewById<TextView>(R.id.tv_else) to bottomSheetView.findViewById<View>(R.id.btn_else)
        )

        // 기본 체크 상태 초기화 및 선택된 시간대에 대한 상태 반영
        timeOptions.forEach { (textView, checkView) ->
            if (textView.text == selectedTime) {
                checkView.visibility = View.VISIBLE
            } else {
                checkView.visibility = View.INVISIBLE
            }

            textView.setOnClickListener {
                timeSlotAdapter.updateTimeSlot(position, textView.text.toString())
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

    private fun showDiseaseBottomSheet() {
        val diBottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val diBottomSheetView = layoutInflater.inflate(R.layout.write_disease_picker, null)

        // 각 질병 항목 레이아웃과 체크 아이콘을 맵핑
        val diseaseLayouts = listOf(
            diBottomSheetView.findViewById<ConstraintLayout>(R.id.di_layout_0) to diBottomSheetView.findViewById<View>(R.id.check_0),
            diBottomSheetView.findViewById<ConstraintLayout>(R.id.di_layout_1) to diBottomSheetView.findViewById<View>(R.id.check_1),
            diBottomSheetView.findViewById<ConstraintLayout>(R.id.di_layout_2) to diBottomSheetView.findViewById<View>(R.id.check_2),
            diBottomSheetView.findViewById<ConstraintLayout>(R.id.di_layout_3) to diBottomSheetView.findViewById<View>(R.id.check_3),
            diBottomSheetView.findViewById<ConstraintLayout>(R.id.di_layout_4) to diBottomSheetView.findViewById<View>(R.id.check_4),
            diBottomSheetView.findViewById<ConstraintLayout>(R.id.di_layout_5) to diBottomSheetView.findViewById<View>(R.id.check_5)
        )

        diseaseLayouts.forEach { (_, checkView) ->
            checkView.visibility = View.INVISIBLE
        }

        val selectedCategory = binding.spinnerDisease.text.toString()

        diseaseLayouts.forEachIndexed { index, (layout, checkView) ->
            val diseaseTextView = layout.findViewById<TextView>(resources.getIdentifier("tv_di_$index", "id", packageName))
            if (diseaseTextView.text == selectedCategory) {
                checkView.visibility = View.VISIBLE
            }

            layout.setOnClickListener {
                diseaseLayouts.forEach { (_, check) -> check.visibility = View.INVISIBLE }
                checkView.visibility = View.VISIBLE

                diBottomSheetView.findViewById<TextView>(R.id.btn_done).setOnClickListener {
                    binding.spinnerDisease.setText(diseaseTextView.text)
                    binding.spinnerDisease.setBackgroundResource(R.drawable.bg_mint_spinner)
                    diBottomSheetDialog.dismiss()
                }
            }
        }

        diBottomSheetDialog.setContentView(diBottomSheetView)
        diBottomSheetDialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.3f)
            decorView.setBackgroundColor(Color.parseColor("#66000000"))
        }

        diBottomSheetDialog.show()
    }
}
