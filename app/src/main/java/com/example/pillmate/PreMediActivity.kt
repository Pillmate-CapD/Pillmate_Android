package com.example.pillmate

import TimeSlotAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pillmate.databinding.ActivityPreMediBinding
import com.example.pillmate.databinding.ActivityPrescriptBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class PreMediActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreMediBinding

    private var currentIndex = 0 // 현재 보여주는 객체의 인덱스
    private var updatedData: ArrayList<Map<String, String>> = arrayListOf() // 전체 데이터

    private lateinit var timeSlotAdapter: TimeSlotAdapter


    private val medicineList = mutableListOf<MedicineInfo>() // 모든 항목을 저장할 리스트

    // 고유 ID 생성을 위한 변수 및 함수
    private var idCounter = 0
    private fun generateUniqueId(): Int {
        return idCounter++
    }

    // 복약 시간대를 저장할 리스트
    // private val timeSlotslist = mutableListOf<TimeSlotItem>()

    // TimeSlotItem 리스트로 변경 및 초기 아이템 추가
    private val timeSlots = mutableListOf(
        TimeSlotItem(generateUniqueId(), "기상 직후")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreMediBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 updatedData를 수신
        updatedData = intent.getSerializableExtra("updatedData") as? ArrayList<Map<String, String>> ?: arrayListOf()

        // updatedData 사용
        updatedData?.let {
            // 데이터를 UI에 반영하거나 처리
            Log.d("PreMediActivity", "수신된 데이터: $it")
        }

        // 첫 번째 객체를 표시

        if(currentIndex == 10000){
            finish()
        }
        else{
            displayData(currentIndex)
        }

        // "다음" 또는 "등록" 버튼 클릭 리스너
        binding.tvSave.setOnClickListener {
            if (checkFieldsAndShowToast()) {
                if (currentIndex < updatedData.size - 1) {
                    currentIndex++
                    displayData(currentIndex)
                    if (currentIndex == updatedData.size - 1) {
                        binding.tvSave.text = "등록"
                    }
                    resetTimeSlots()
                } else {
                    submitAllData()
                    finish()
                }
            }
        }



        // 리사이클러뷰 어댑터 설정
        timeSlotAdapter = TimeSlotAdapter(this,timeSlots) { position ->
            showTimePickerBottomSheet(position)
        }

        // RecyclerView 설정
        binding.timeSlotRecy.apply {
            layoutManager = LinearLayoutManager(this@PreMediActivity)
            adapter = timeSlotAdapter
        }

        //binding.spinnerDisease.isSelected = true

        // 시간대 추가 버튼 클릭 리스너
        binding.btnAddTime.setOnClickListener {
            addNewTimeSlot()
        }

        binding.spinnerDisease.setOnClickListener {
            showDiseaseBottomSheet()
        }

        binding.tvCancel.setOnClickListener {
            if (currentIndex < updatedData.size - 1 || currentIndex == 10000) {
                Log.d("currentIndex", "CurrentIndex, ${currentIndex}")
                finish()
            }
            finish()
        }
    }

//    // 필드를 비우는 함수
//    private fun clearFields() {
//        binding.editMedi.text.clear()         // 약품명 초기화
//        // 질병명 초기화
//        binding.editOneEat.text.clear()       // 1회 복약량 초기화
//        binding.editOneDay.text.clear()       // 1일 복약횟수 초기화
//        binding.editAllDay.text.clear()       // 총 복약일수 초기화
//        binding.imgMedi.setImageResource(R.drawable.bg_zoom_null) // 이미지 초기화
//    }


    // 필수 필드 확인 및 저장 로직
    private fun checkFieldsAndShowToast(): Boolean {
        // 필수 필드 체크
        val isMediNameEmpty = binding.editMedi.text.isNullOrEmpty()
        val isDiseaseEmpty = binding.spinnerDisease.text.isNullOrEmpty()
        val isOneEatEmpty = binding.editOneEat.text.isNullOrEmpty()
        val isOneDayEmpty = binding.editOneDay.text.isNullOrEmpty()
        val isAllDayEmpty = binding.editAllDay.text.isNullOrEmpty()

        // 모든 시간대가 선택되었는지 확인 (spinnerTime과 pickerTime 모두)
        val areAllTimeSlotsSelected = timeSlots.all { it.isTimeSelected && it.isTimeChanged }

        // 각 필드별로 확인하고 메시지 띄우기
        return when {
            isMediNameEmpty -> {
                showCustomToast("약품명을 입력해주세요.")
                false
            }
            isDiseaseEmpty -> {
                showCustomToast("질병을 선택해주세요.")
                false
            }
            isOneEatEmpty -> {
                showCustomToast("1회 복약량을 입력해주세요.")
                false
            }
            isOneDayEmpty -> {
                showCustomToast("1일 복약횟수를 입력해주세요.")
                false
            }
            isAllDayEmpty -> {
                showCustomToast("총 복약일수를 입력해주세요.")
                false
            }
            !areAllTimeSlotsSelected -> {
                showCustomToast("복약 시간을 입력해주세요.")
                false
            }
            else -> {
                // 모든 필드가 채워져 있으면 저장 로직 수행
                val mediName = binding.editMedi.text.toString()
                val category = binding.spinnerDisease.text.toString()
                val oneEat = binding.editOneEat.text.toString().toInt()
                val oneDay = binding.editOneDay.text.toString().toInt()
                val allDay = binding.editAllDay.text.toString().toInt()

                // 복약 시간대 리스트 생성
                val timeSlotRequests = timeSlots.map { timeSlot ->
                    TimeSlotRequest(
                        spinnerTime = timeSlot.timeLabel,
                        pickerTime = convertTo24HourFormat(timeSlot.time)
                    )
                }

                // MedicineInfo 객체 생성
                val medicineInfo = MedicineInfo(
                    mediName = mediName,
                    category = category,
                    oneEat = oneEat,
                    oneDay = oneDay,
                    allDay = allDay,
                    timeSlotList = timeSlotRequests
                )

                // MedicineInfo 객체를 리스트에 추가
                medicineList.add(medicineInfo)
                Log.d("MedicineInfo", "저장된 항목: $medicineInfo")

                true
            }
        }
    }

    private fun submitAllData() {
        Log.d("MedicineList", "최종 저장된 데이터: $medicineList")

        if (currentIndex == 10000) {
            finish() // 이미 종료 상태라면 더 이상 액티비티를 열지 않음
            return
        }

        // 다음 화면으로 데이터 전달
        val intent = Intent(this, EndPreActivity::class.java)
        intent.putExtra("medicineList", ArrayList(medicineList)) // ArrayList를 Serializable로 전달

        // 기존 스택을 정리하고 새 액티비티로 이동
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }




//    // 마지막 항목인지 확인하는 함수 (임의로 정의)
//    private fun isLastEntry(): Boolean {
//        // 마지막 항목인지 확인하는 로직을 구현할 수 있음 (예: 인덱스 비교 등)
//        return currentIndex == updatedData.size - 1
//    }

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

    // 새로운 Intent가 전달될 때 UI 업데이트를 처리하는 onNewIntent
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            // 새로운 updatedData를 받아옴
            updatedData = it.getSerializableExtra("updatedData") as? ArrayList<Map<String, String>> ?: arrayListOf()

            // 데이터가 있다면 UI를 다시 업데이트
            if (updatedData.isNotEmpty()) {
                currentIndex = 0 // 인덱스를 처음으로 리셋
                displayData(currentIndex)
            }
        }
    }

    private fun displayData(index: Int) {
        if (index >= 0 && index < updatedData.size) { // 인덱스가 리스트 크기 내에 있는지 확인
            val data = updatedData[index]

            // 데이터 바인딩
            binding.editMedi.text = data["명칭"]?.toEditable()
            binding.spinnerDisease.text = data["고지혈증"]?.toEditable()

//                if (data["category"] == "db에서 해당 약을 찾을 수 없습니다") {
//                "기타".toEditable() // 카테고리 기본값 설정
//            } else {
//                data["고지혈증"]?.toEditable()
//            }
            binding.editOneEat.text = data["1회 투약량"]?.toEditable()
            binding.editOneDay.text = data["1일 투여횟수"]?.toEditable()
            binding.editAllDay.text = data["총 투약일수"]?.toEditable()


            // Glide를 사용하여 이미지 로드, 기본 이미지 처리
            val imageUrl = data["photo"]
            if (imageUrl == "db에서 해당 약을 찾을 수 없습니다") {
                Glide.with(this)
                    .load(R.drawable.bg_zoom_null) // 기본 이미지를 설정 (default_image는 res/drawable에 있어야 함)
                    .into(binding.imgMedi)
            } else {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.bg_zoom_null) // 로딩 중에 표시할 이미지
                    .error(R.drawable.bg_zoom_null)   // 로드 실패 시 표시할 이미지
                    .into(binding.imgMedi)
            }

            // 로그로 현재 데이터를 출력
            Log.d("PreMediActivity", "현재 데이터: $data")
        } else {
            Log.e("PreMediActivity", "잘못된 인덱스: $index")
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

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

    // 시간대 초기화 함수
    private fun resetTimeSlots() {
        // 기존 타임슬롯을 모두 지움
        timeSlots.clear()

        // 초기 상태의 타임슬롯을 추가
        timeSlots.add(TimeSlotItem(generateUniqueId(), "기상 직후", isTimeSelected = false))

        // 어댑터에 데이터가 변경되었음을 알림
        timeSlotAdapter.notifyDataSetChanged()
    }

}


