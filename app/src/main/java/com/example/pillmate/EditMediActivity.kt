package com.example.pillmate

import TimeSlotAdapter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.ActivityEditMediBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        timeSlotAdapter = TimeSlotAdapter(timeSlots) { position ->
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
            finish()
        }

        // 수정 버튼 눌렀을 때 수정 로직 추가 (예시로 수정된 데이터를 저장하는 로직)
        binding.tvEditDone.setOnClickListener {
            // 수정된 데이터를 서버로 전송하거나 데이터베이스에 저장하는 로직 추가
            finish()
        }
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
