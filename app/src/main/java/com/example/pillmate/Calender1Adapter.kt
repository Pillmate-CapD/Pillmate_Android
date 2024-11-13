package com.example.pillmate

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.R
import com.example.pillmate.databinding.Calendar1DayItemBinding
import java.util.Calendar

class Calendar1Adapter(
    private val days: List<Pair<Int?, Boolean>>,
    private val onDayClickListener: (Int, Int, Int) -> Unit // month, year도 포함
) : RecyclerView.Adapter<Calendar1Adapter.ViewHolder>() {

    private var selectedDay: Int? = null
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    private val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = Calendar1DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    inner class ViewHolder(private val binding: Calendar1DayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dayData: Pair<Int?, Boolean>) {
            val (day, isCurrentMonth) = dayData

            if (day != null) {
                binding.dateRecy.text = day.toString()
                binding.dateRecy.visibility = View.VISIBLE
                binding.dateRecy.setTextColor(if (isCurrentMonth) Color.BLACK else Color.parseColor("#D9D9D9"))

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

                // 회색 동그라미 처리
                if (isToday && selectedDay == null) {
                    binding.todayView.setBackgroundResource(R.drawable.circle_today)
                } else if (isSelected && isCurrentMonth) {
                    binding.todayView.setBackgroundResource(R.drawable.circle_today)
                } else {
                    binding.todayView.setBackgroundResource(R.drawable.circle_white)
                }

                // 클릭 이벤트 처리
                itemView.setOnClickListener {
                    if (isCurrentMonth) {
                        selectedDay = day
                        notifyDataSetChanged()
                        // month, year도 함께 넘기기
                        onDayClickListener(day, selectedMonth, selectedYear)
                    }
                }
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
