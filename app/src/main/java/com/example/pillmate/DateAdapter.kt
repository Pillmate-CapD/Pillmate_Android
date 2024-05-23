package com.example.pillmate

import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.time.LocalDate

class DateAdapter(private val itemDate : ArrayList<DateItem>) :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_item, parent, false)
        return DateViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemDate.size
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val item = itemDate[position]
        holder.bind(item)
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val day: TextView = itemView.findViewById(R.id.day_recy)
        private val date: TextView = itemView.findViewById(R.id.date_recy)
        private val progressBar: CircularProgressIndicator = itemView.findViewById(R.id.pill_progressBar)
        private val backgroundView : View = itemView.findViewById(R.id.today_view)

        fun bind(item: DateItem) {
            day.text = item.dayOfWeek
            date.text = item.formattedDate
            progressBar.progress = item.progress
            if (item.isToday) {
                // 오늘 날짜인 경우 배경 색 변경
                backgroundView.visibility = View.VISIBLE
            } else {
                // 오늘 날짜가 아닌 경우 배경 색 초기화
                backgroundView.visibility = View.INVISIBLE
            }

            // 오늘 이후의 날짜인 경우
            if (!item.isToday && item.date.isAfter(LocalDate.now())) {
                // 프로그레스 바의 색상을 흰색의 20% 투명도로 설정
                progressBar.setIndicatorColor(Color.parseColor("#33FFFFFF"))
                // Date 텍스트의 색상을 흰색의 20% 투명도로 설정
                date.setTextColor(Color.parseColor("#33FFFFFF"))
            } else {
                // 오늘 이후의 날짜가 아니라면 원래의 색상으로 설정
                progressBar.setIndicatorColor(ContextCompat.getColor(itemView.context, R.color.white))
                date.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }
        }
    }
}