package com.example.pillmate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.FragmentCalendarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var calendar: Calendar
    private lateinit var adapter: Calendar1Adapter
    private lateinit var days: List<Pair<Int?, Boolean>>
    private var painsPerDayList: List<PainPerDay> = emptyList()
    private var totalInfoList: List<TotalInfo> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        calendar = Calendar.getInstance()

        // Locale을 한국어로 설정
        Locale.setDefault(Locale.KOREAN)

        // 현재 날짜로 초기화
        updateYearMonthText()
        updateDateTitle()

        // RecyclerView 설정
        setupRecyclerView()

        // 데이터 불러오기
        fetchDiaryData()  // 초기 화면 로드 시 데이터 불러오기
        fetchMonthDiaryData()  // 월별 데이터 불러오기


        // 드롭다운 버튼 클릭 시 DatePickerDialog 표시
        binding.dropdownButton.setOnClickListener {
            showSpinnerDatePickerDialog()
        }

        /*// editDiaryButton 클릭 시 HealthDiary1Activity로 이동
        binding.editDiaryButton.setOnClickListener {
            val intent = Intent(requireContext(), HealthDiary1Activity::class.java)
            startActivity(intent)
        }*/
        // editDiaryButton 클릭 시 데이터 체크 후 페이지 이동
        binding.editDiaryButton.setOnClickListener {
            fetchDiaryDataAndNavigate()
        }


        fetchDiaryData() // 추가된 기능
        fetchMonthDiaryData()

        return binding.root
    }
    private fun fetchDiaryDataAndNavigate() {
        val date = formatDateForApi() // 선택된 날짜 포맷 가져오기
        Log.d("CalendarFragment", "선택된 날짜: $date")

        RetrofitApi.getRetrofitService.getDiaryByDate(date).enqueue(object :
            Callback<DiaryResponse> {
            override fun onResponse(call: Call<DiaryResponse>, response: Response<DiaryResponse>) {
                if (response.isSuccessful) {
                    val diary = response.body()
                    if (diary != null && (diary.symptoms.isNullOrEmpty() && diary.score == null && diary.comment.isNullOrEmpty() && diary.record.isNullOrEmpty())) {
                        // 모든 값이 null 또는 비어 있는 경우, HealthDiary1Activity로 이동
                        val intent = Intent(requireContext(), HealthDiary1Activity::class.java)
                        intent.putExtra("date", date) // 날짜 전달
                        startActivity(intent)
                    } else {
                        // 하나라도 값이 있는 경우, HDEdit1Activity로 이동
                        val intent = Intent(requireContext(), HDEdit1Activity::class.java)
                        intent.putExtra("date", date) // 날짜 전달
                        intent.putExtra("id", diary?.id ?: 0) // ID 전달
                        intent.putExtra("symptoms", diary?.symptoms?.joinToString(",") ?: "") // 증상 전달
                        intent.putExtra("score", diary?.score ?: 0) // 점수 전달
                        intent.putExtra("comment", diary?.comment ?: "") // 코멘트 전달
                        intent.putExtra("record", diary?.record ?: "") // 기록 전달
                        Log.d("CalendarFragment", """
                        HDEdit1Activity로 이동:
                        date=$date,
                        symptoms=${diary?.symptoms?.joinToString(",")},
                        score=${diary?.score ?: 0},
                        comment=${diary?.comment ?: ""},
                        record=${diary?.record ?: ""}
                    """.trimIndent())
                        startActivity(intent)

                    }
                } else {
                    Log.e("CalendarFragment", "API 응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DiaryResponse>, t: Throwable) {
                Log.e("CalendarFragment", "API 요청 실패: ${t.message}")
            }
        })
    }

    // 현재 년/월 텍스트 업데이트 함수
    private fun updateYearMonthText() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        binding.ymTxt.text = "${year}년 ${month}월"
    }

    // 날짜 제목 업데이트 함수 ("MM월 dd일 E요일" 형식)
    private fun updateDateTitle() {
        val dateFormat = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREAN)
        binding.dateTitle.text = dateFormat.format(calendar.time)
        fetchDiaryData() // 추가된 기능
        updateEditDiaryButtonState()//미래는 일기쓰기 버튼 클릭X
        fetchMonthDiaryData()
    }

    // RecyclerView 설정 함수
    private fun setupRecyclerView() {
        days = generateDaysForMonth()
        adapter = Calendar1Adapter(days,painsPerDayList,totalInfoList) { day, month, year ->
            calendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateTitle()
        }
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7) // 수정함: GridLayoutManager 설정
        binding.calendarRecyclerView.adapter = adapter
    }

    // 날짜 리스트 생성 함수 (이전 달/다음 달 날짜 포함)
    private fun generateDaysForMonth(): List<Pair<Int?, Boolean>> {
        val days = mutableListOf<Pair<Int?, Boolean>>()
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 이전 달 날짜 추가
        tempCalendar.add(Calendar.MONTH, -1)
        val daysInPrevMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        tempCalendar.add(Calendar.MONTH, 1)

        for (i in firstDayOfWeek downTo 1) {
            days.add(Pair(daysInPrevMonth - i + 1, false))
        }

        // 현재 달 날짜 추가
        for (i in 1..daysInMonth) {
            days.add(Pair(i, true))
        }

        // 다음 달 날짜 추가
        val totalCells = 35
        val nextMonthDays = totalCells - days.size
        for (i in 1..nextMonthDays) {
            days.add(Pair(i, false))
        }

        return days
    }

    // Spinner 모드로 DatePickerDialog 표시 함수
    private fun showSpinnerDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Spinner 모드로 DatePickerDialog 생성
        val datePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(Calendar.YEAR, selectedYear)
                    calendar.set(Calendar.MONTH, selectedMonth)
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                    updateYearMonthText()
                    updateDateTitle()
                    // RecyclerView 업데이트
                    setupRecyclerView()

                    // 선택된 날짜를 업데이트하고 회색 동그라미 표시
                    adapter.updateSelectedDate(selectedDay, selectedMonth + 1, selectedYear)
                    fetchDiaryData() // 추가
                    updateEditDiaryButtonState()//미래는 일기쓰기 버튼 클릭X
                    fetchMonthDiaryData()
                },
                year,
                month,
                day
            )
        } else {
            DatePickerDialog(requireContext())
        }

        // Spinner 모드 강제 설정
        datePickerDialog.datePicker.calendarViewShown = false
        datePickerDialog.datePicker.spinnersShown = true

        // DatePickerDialog 표시
        datePickerDialog.show()
    }
    private fun formatDateForApi(): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val localDate = LocalDate.of(year, month, day)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA)
        return localDate.format(formatter)
    }

    private fun fetchDiaryData() {
        val date = formatDateForApi()
        Log.d("CalendarFragment", "API 요청 날짜: $date")

        RetrofitApi.getRetrofitService.getDiaryByDate(date).enqueue(object :
            Callback<DiaryResponse> {
            override fun onResponse(call: Call<DiaryResponse>, response: Response<DiaryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("CalendarFragment", "API 응답 데이터: $it")
                        updateAlarmUI(it.alarms)
                        updateDiaryUI(it)
                    }
                } else {
                    Log.e("CalendarFragment", "API 응답 실패: ${response.code()}")
                    showNoMedicineMessage()
                }
            }

            override fun onFailure(call: Call<DiaryResponse>, t: Throwable) {
                Log.e("CalendarFragment", "API 요청 실패: ${t.message}")
                showNoMedicineMessage()
            }
        })
    }

    private fun updateAlarmUI(alarms: List<Alarm>?) {
        Log.d("CalendarFragment", "updateAlarmUI 호출됨. 알람 수: ${alarms?.size ?: 0}")

        if (alarms.isNullOrEmpty()) {
            // 약물이 없을 경우 "약물이 없는 경우" UI를 표시
            binding.noMedicineSection.visibility = View.VISIBLE
            binding.medicineListSection.visibility = View.GONE
            return
        }

        // 약물이 있을 경우 "약물이 없는 경우" UI 숨기기
        binding.noMedicineSection.visibility = View.GONE
        binding.medicineListSection.visibility = View.VISIBLE

        // 이전에 추가된 약물 항목 제거
        binding.medicineListSection.removeAllViews()

        // 이전 뷰 ID 초기값 설정
        var previousViewId = View.NO_ID
        var isFirstItem = true

        alarms.forEach { alarm ->
            Log.d("CalendarFragment", "알람 추가: ${alarm.name}, ${alarm.category}, ${alarm.time}")

            val itemView = layoutInflater.inflate(R.layout.layout_medicine_entry, binding.medicineListSection, false)

            val colorIndicator = itemView.findViewById<View>(R.id.colorIndicator)
            colorIndicator.setBackgroundResource(getColorByCategory(alarm.category))

            val medicineTime = itemView.findViewById<TextView>(R.id.medicineTime)
            medicineTime.text = formatTime(alarm.time)

            val medicineName = itemView.findViewById<TextView>(R.id.medicineName)
            medicineName.text = alarm.name

            // 동적으로 추가된 뷰의 아이디 설정
            val itemId = View.generateViewId()
            itemView.id = itemId

            // 레이아웃 파라미터 설정
            val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
            //layoutParams.topMargin = 16
            // 첫 번째 아이템은 topMargin 16dp, 나머지는 8dp
            if (isFirstItem) {
                layoutParams.topMargin = 16.dpToPx()
                isFirstItem = false
            } else {
                layoutParams.topMargin = 8.dpToPx()
            }

            // 이전 뷰가 있으면, 이전 뷰 아래에 위치
            if (previousViewId != View.NO_ID) {
                (itemView.layoutParams as ConstraintLayout.LayoutParams).apply {
                    topToBottom = previousViewId
                    startToStart = binding.medicineListSection.id
                    endToEnd = binding.medicineListSection.id
                }
            } else {
                // 첫 번째 뷰는 상단에 위치
                (itemView.layoutParams as ConstraintLayout.LayoutParams).apply {
                    topToTop = binding.medicineListSection.id
                    startToStart = binding.medicineListSection.id
                    endToEnd = binding.medicineListSection.id
                }
            }

            // medicineListSection에 뷰 추가
            binding.medicineListSection.addView(itemView)
            previousViewId = itemId
        }
    }

    // dp 값을 px 값으로 변환하는 확장 함수
    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun updateDiaryUI(diary: DiaryResponse) {
        binding.diaryListSection.removeAllViews()
        if (diary.symptoms.isNullOrEmpty() && diary.score == null && diary.comment.isNullOrEmpty() && diary.record.isNullOrEmpty()) {
            showNoDiaryMessage()
            return
        }

        binding.noDiaryText.visibility = View.GONE

        val itemView = layoutInflater.inflate(R.layout.layout_diary_entry, binding.diaryListSection, false)
        itemView.findViewById<TextView>(R.id.painTags).text = diary.symptoms?.joinToString(" #", prefix = "#") ?: ""
        val painScore = itemView.findViewById<TextView>(R.id.painScore)
        painScore.text = "${diary.score ?: 0}점"
        //itemView.findViewById<TextView>(R.id.painScore).text = "${diary.score ?: 0}점"
        itemView.findViewById<TextView>(R.id.painDescription).text = diary.comment ?: ""
        itemView.findViewById<TextView>(R.id.diaryContent).text = diary.record ?: ""

        // 점수에 따른 색상 설정 (나 지금 추가했어요!)
        val score = diary.score ?: 0
        when (score) {
            in 1..4 -> {
                painScore.setTextColor(android.graphics.Color.parseColor("#06BFB6"))
                painScore.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D6F0EF"))
            }
            in 5..7 -> {
                painScore.setTextColor(android.graphics.Color.parseColor("#FB7F02"))
                painScore.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFEAB1"))
            }
            in 8..10 -> {
                painScore.setTextColor(android.graphics.Color.parseColor("#FF453A"))
                painScore.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFD1CE"))
            }
            else -> {
                // 기본 색상 설정
                painScore.setTextColor(android.graphics.Color.parseColor("#3E3E3E"))
                painScore.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F0F0F0"))
            }
        }

        binding.diaryListSection.addView(itemView)
    }

    private fun showNoDiaryMessage() {
        binding.noDiaryText.visibility = View.VISIBLE
    }


    // "약물이 없는 경우" 메시지 표시 함수
    private fun showNoMedicineMessage() {
        Log.d("CalendarFragment", "showNoMedicineMessage 호출됨: XML 기본 UI 유지")
        // 아무것도 하지 않음. XML 레이아웃 기본 상태 그대로 유지.
    }
    private fun getColorByCategory(category: String): Int {
        return when (category) {
            "고지혈증" -> R.drawable.color_indicator_background
            "고혈압" -> R.drawable.color2_indicator_background
            "호흡기질환" -> R.drawable.color3_indicator_background
            "당뇨" -> R.drawable.color4_indicator_background
            "심혈관질환" -> R.drawable.color5_indicator_background
            "기타" -> R.drawable.color6_indicator_background
            else -> R.drawable.color6_indicator_background
        }
    }

    private fun formatTime(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREAN)
        val outputFormat = SimpleDateFormat("a hh:mm", Locale.KOREAN)
        return try {
            val date = inputFormat.parse(time)
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("CalendarFragment", "시간 형식 변환 오류: ${e.message}")
            time
        }
    }
    override fun onResume() {
        super.onResume()
        // API 호출하여 데이터를 갱신
        fetchDiaryData()
    }
    private fun updateEditDiaryButtonState() {
        val currentDate = LocalDate.now()
        val selectedDate = LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // 미래 날짜 선택 시 버튼 비활성화, 현재/과거 날짜 선택 시 버튼 활성화
        if (selectedDate.isAfter(currentDate)) {
            binding.editDiaryButton.isEnabled = false
            binding.editDiaryButton.alpha = 0.5f // 수정됨: 비활성화 상태 표시
        } else {
            binding.editDiaryButton.isEnabled = true
            binding.editDiaryButton.alpha = 1.0f // 수정됨: 활성화 상태 표시
        }
    }
    private fun fetchMonthDiaryData() {
        val date = formatDateForApi()
        Log.d("CalendarFragment", "한 달 다이어리 데이터 요청 날짜: $date")

        // 코루틴에서 API 호출
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // API 호출을 IO 스레드에서 수행
                val response = withContext(Dispatchers.IO) {
                    RetrofitApi.getRetrofitService.getMonthDiary(date)
                }

                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        Log.d("CalendarFragment", "한 달 다이어리 API 응답 데이터: $data")

                        // 데이터 저장
                        painsPerDayList = data.painsPerDay
                        // painsPerDayList의 내용 로그 출력
                        Log.d("CalendarFragment", "업데이트된 painsPerDayList 데이터: $painsPerDayList")

                        totalInfoList = data.totalInfo
                        totalInfoList.forEachIndexed { index, totalInfo ->
                            val category = totalInfo.category.toByteArray().toString(Charsets.UTF_8)
                            Log.d(
                                "CalendarFragment",
                                "totalInfo[$index]: category=${totalInfo.category}, startDate=${totalInfo.startDate}, endDate=${totalInfo.endDate}"
                            )
                        }

                        adapter.updateData(days, painsPerDayList)
                        adapter.notifyDataSetChanged()

                        // duration 값을 eatmedi_date에 표시
                        binding.eatmediDate.text = "${data.duration}"
                        //adapter.updateData(days, painsPerDayList)
                    }
                } else {
                    Log.e("CalendarFragment", "한 달 다이어리 API 응답 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CalendarFragment", "API 요청 실패: ${e.message}")
            }
        }
    }
}
