package com.example.pillmate

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class PillListAdapter(private val pillListItem: ArrayList<PillListItem>) :
    RecyclerView.Adapter<PillListAdapter.PillListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pill_list, parent, false)
        return PillListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pillListItem.size
    }

    override fun onBindViewHolder(holder: PillListViewHolder, position: Int) {
        val item = pillListItem[position]
        holder.bind(item)
    }

    inner class PillListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val time: TextView = itemView.findViewById(R.id.pill_list_time)
        private val pillName: TextView = itemView.findViewById(R.id.pill_list_name)

        private val pill_right: View = itemView.findViewById(R.id.pill_right)
        private val pill_list_go: View = itemView.findViewById(R.id.pill_list_go)
        private val pill_done: View = itemView.findViewById(R.id.pill_done)
        private val pill_before: View = itemView.findViewById(R.id.pill_before)

        fun bind(item: PillListItem) {
            // 시간 및 약물 이름 설정
            time.text = item.time
            pillName.text = item.name

            // 현재 시간 설정
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)

            // 시간 추출
            val itemHour = item.extractHour()
            val itemMinute = item.extractMinute()

            // 현재 시간과 항목의 시간을 비교하여 배경 색상 결정
            val itemTimeInMinutes = itemHour * 60 + itemMinute
            val currentTimeInMinutes = currentHour * 60 + currentMinute

            if (currentTimeInMinutes >= itemTimeInMinutes - 60) {
                // 현재 시간이 항목의 시간보다 한 시간 전이거나 같은 경우
                itemView.setBackgroundResource(R.drawable.bg_pill_list_check) // 배경 색상 변경
                pill_done.visibility = View.INVISIBLE
                pill_before.visibility = View.INVISIBLE
                pill_list_go.visibility = View.VISIBLE
                pill_right.visibility = View.VISIBLE

                itemView.setOnClickListener {
                    // 알람 완료 후 상태 변경
                    itemView.setBackgroundResource(R.drawable.custom_pill_background)  // 기본 색상
                    pill_done.visibility = View.VISIBLE
                    pill_list_go.visibility = View.INVISIBLE
                    pill_right.visibility = View.INVISIBLE
                    pill_before.visibility = View.INVISIBLE
                }
            } else {
                // 그 외의 경우 기본 배경 색상 사용
                itemView.setBackgroundResource(R.drawable.custom_pill_background)  // 기본 색상
                pill_done.visibility = View.INVISIBLE
                pill_list_go.visibility = View.INVISIBLE
                pill_right.visibility = View.INVISIBLE
                pill_before.visibility = View.VISIBLE
            }
        }
    }
}