package com.example.pillmate

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.R
import com.example.pillmate.databinding.Calendar1DayItemBinding
import java.util.Calendar

class Calendar1Adapter(
    private var days: List<Pair<Int?, Boolean>>,
    private var painsPerDayList: List<PainPerDay>,
    private val onDayClickListener: (Int, Int, Int) -> Unit // month, year도 포함
) : RecyclerView.Adapter<Calendar1Adapter.ViewHolder>() {

    private var selectedDay: Int? = null
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    private val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    // 데이터를 업데이트하는 메서드 (선택된 날짜 초기화하지 않음)
    fun updateData(newDays: List<Pair<Int?, Boolean>>, newPainsPerDayList: List<PainPerDay>) {
        days = newDays
        painsPerDayList = newPainsPerDayList
        notifyDataSetChanged()

    }

    init {
        // 어댑터 초기화 시 painsPerDayList 로그 출력
        Log.d("Calendar1Adapter", "painsPerDayList 데이터: $painsPerDayList")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = Calendar1DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    // 선택된 날짜 업데이트 메서드
    fun setSelectedDate(day: Int, month: Int, year: Int) {
        selectedDay = day
        selectedMonth = month
        selectedYear = year
        notifyDataSetChanged()
    }

    // 선택된 날짜 가져오는 메서드
    fun getSelectedDate(): Triple<Int?, Int, Int> {
        return Triple(selectedDay, selectedMonth, selectedYear)
    }

    inner class ViewHolder(private val binding: Calendar1DayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dayData: Pair<Int?, Boolean>) {
            val (day, isCurrentMonth) = dayData

            if (day != null) {
                binding.dateRecy.text = day.toString()
                binding.dateRecy.visibility = View.VISIBLE
                binding.dateRecy.setTextColor(if (isCurrentMonth) Color.BLACK else Color.parseColor("#D9D9D9"))
                // painsPerDayList의 내용 출력
                painsPerDayList.forEach { pain ->
                    Log.d("Calendar1Adapter", "PainPerDay - date: ${pain.date}, level: ${pain.level}")
                }

                // 현재 날짜 표시
                //val isToday = (day == currentDay && selectedMonth == currentMonth && selectedYear == currentYear)
                //val isSelected = (day == selectedDay && selectedMonth == currentMonth && selectedYear == currentYear)
                val isToday = (day == currentDay && selectedMonth == currentMonth && selectedYear == currentYear)
                val isSelected = (day == selectedDay && selectedMonth == selectedMonth && selectedYear == selectedYear)

                if (isToday) {
                    binding.dayRecy.text = "오늘"
                    binding.dayRecy.setTextColor(Color.parseColor("#FF453A"))
                    binding.dayRecy.visibility = View.VISIBLE
                } else {
                    binding.dayRecy.text = ""
                    binding.dayRecy.visibility = View.INVISIBLE
                }

                // painsPerDay 데이터를 사용한 today_view 배경 설정
                val matchingPain = painsPerDayList.find { pain ->
                    // "yyyy-MM-dd" 형식의 date 필드 파싱
                    val parts = pain.date.split("-")
                    val year = parts[0].toInt()
                    val month = parts[1].toInt()
                    val day = parts[2].toInt()
                    // 비교 전 로그 출력
                    Log.d("Calendar1Adapter", "비교 중 - pain day: $day, pain month: $month, pain year: $year")

                    // 현재 바인딩 중인 날짜와 비교
                    day == this@ViewHolder.binding.dateRecy.text.toString().toInt() &&
                            month == selectedMonth &&
                            year == selectedYear
                }

                if (matchingPain != null) {
                    // level 값에 따라 배경 설정 (1 ~ 10)
                    Log.d("Calendar1Adapter", "matchingPain 찾음 - level: ${matchingPain.level}")
                    val levelDrawable = when (matchingPain.level) {
                        1 -> R.drawable.level1
                        2 -> R.drawable.level2
                        3 -> R.drawable.level3
                        4 -> R.drawable.level4
                        5 -> R.drawable.level5
                        6 -> R.drawable.level6
                        7 -> R.drawable.level7
                        8 -> R.drawable.level8
                        9 -> R.drawable.level9
                        10 -> R.drawable.level10
                        else -> R.drawable.circle_white
                    }
                    binding.todayView.setBackgroundResource(levelDrawable)
                } else {
                    Log.d("Calendar1Adapter", "matchingPain 찾지 못함")
                    // 데이터가 없는 날짜 처리
                    val currentDate = Calendar.getInstance()
                    val selectedDate = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth - 1, day)
                    }

                    when {
                        selectedDate.before(currentDate) -> {
                            binding.todayView.setBackgroundResource(R.drawable.circle_nod)
                        }
                        isToday -> {
                            binding.todayView.setBackgroundResource(R.drawable.circle_white)
                        }
                        else -> {
                            binding.todayView.setBackgroundResource(R.drawable.circle_white)
                        }
                    }
                }
                // 회색 stroke 처리
                if (isToday && selectedDay == null) {
                    if (matchingPain == null) {
                        // 오늘 날짜인데 API 데이터가 없는 경우
                        binding.todayView.setBackgroundResource(R.drawable.circle_today)
                        binding.pillProgressBar.setIndicatorColor(Color.parseColor("#ffffff"))
                    } else {
                        // 오늘 날짜이고 API 데이터가 있는 경우
                        binding.pillProgressBar.setIndicatorColor(Color.parseColor("#898989"))
                    }
                } else if (isSelected && isCurrentMonth) {
                    // 선택된 날짜이고 현재 달인 경우
                    if (isToday) {
                        // 오늘 날짜가 선택된 경우
                        if (matchingPain == null) {
                            binding.todayView.setBackgroundResource(R.drawable.circle_today)
                            binding.pillProgressBar.setIndicatorColor(Color.parseColor("#ffffff"))
                        } else {
                            binding.pillProgressBar.setIndicatorColor(Color.parseColor("#898989"))
                        }
                    } else {
                        // 오늘 날짜가 아닌 다른 날짜가 선택된 경우
                        binding.pillProgressBar.setIndicatorColor(Color.parseColor("#898989"))
                    }
                } else {
                    // 기본 색상
                    binding.pillProgressBar.setIndicatorColor(Color.parseColor("#ffffff"))
                }

                // 클릭 이벤트 처리
                itemView.setOnClickListener {
                    if (isCurrentMonth) {
                        selectedDay = day
                        selectedMonth = this@Calendar1Adapter.selectedMonth
                        selectedYear = this@Calendar1Adapter.selectedYear
                        notifyDataSetChanged()
                        onDayClickListener(day, selectedMonth, selectedYear)
                    }
                }
                /*itemView.setOnClickListener {
                    if (isCurrentMonth) {
                        selectedDay = day
                        notifyDataSetChanged()
                        // month, year도 함께 넘기기
                        onDayClickListener(day, selectedMonth, selectedYear)
                    }
                }*/
            } else {
                binding.dateRecy.text = ""
                binding.dateRecy.visibility = View.INVISIBLE
                binding.dayRecy.visibility = View.INVISIBLE
                binding.todayView.visibility = View.INVISIBLE
            }
        }
    }

    // 날짜 변경 시 호출되는 함수
    fun updateSelectedDate(day: Int, month: Int, year: Int) {
        selectedDay = day
        selectedMonth = month
        selectedYear = year
        notifyDataSetChanged()
    }
}
