package com.example.pillmate

import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.time.LocalDate

class DateAdapter(private val itemDate: ArrayList<DateItem>) :
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
        private val backgroundView: View = itemView.findViewById(R.id.today_view)

        fun bind(item: DateItem) {
            day.text = item.dayOfWeek
            date.text = item.formattedDate
            progressBar.progress = item.progress

            val today = LocalDate.now()

            if (item.isToday) {
                backgroundView.visibility = View.VISIBLE
                date.setTextColor(ContextCompat.getColor(itemView.context, R.color.white)) // 오늘 날짜는 흰색
                day.setTextColor(ContextCompat.getColor(itemView.context, R.color.white)) // 오늘 날짜의 요일도 흰색
                progressBar.progress = item.progress
            } else {
                backgroundView.visibility = View.INVISIBLE
                if (item.date.isAfter(today)) { // 오늘 이후의 날짜 여부 확인
                    date.setTextColor(ContextCompat.getColor(itemView.context, R.color.trans_white))  // 오늘 이후의 날짜는 회색
                    //day.setTextColor(ContextCompat.getColor(itemView.context, R.color.trans_white))  // 오늘 이후의 요일도 회색
                    progressBar.progress = 0
                } else {
                    date.setTextColor(ContextCompat.getColor(itemView.context, R.color.white)) // 오늘 이전의 날짜는 흰색
                    day.setTextColor(ContextCompat.getColor(itemView.context, R.color.white)) // 오늘 이전의 요일도 흰색
                    progressBar.progress = item.progress
                }
            }
        }
    }
}