package com.example.pillmate

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class PillListAdapter (private val pillListItem : ArrayList<PillListItem>) :
    RecyclerView.Adapter<PillListAdapter.PillListViewHolder>(){

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

            // 시간 추출
            val hour = item.extractHour()

            // 현재 시간과 항목의 시간을 비교하여 배경 색상 결정
            if (hour != null) {
                if (currentHour >= hour - 1) {
                    // 현재 시간이 항목의 시간보다 한 시간 전이거나 같은 경우
                    itemView.setBackgroundColor(Color.parseColor("#E6EBFA")) // 배경 색상 변경
                    pill_done.visibility = View.INVISIBLE
                    pill_before.visibility = View.INVISIBLE
                    pill_list_go.visibility = View.VISIBLE
                    pill_right.visibility = View.VISIBLE
                } else {
                    // 그 외의 경우 기본 배경 색상 사용
                    itemView.setBackgroundColor(Color.parseColor("#F5F6F8")) // 기본 색상
                    pill_done.visibility = View.INVISIBLE
                    pill_list_go.visibility = View.INVISIBLE
                    pill_right.visibility = View.INVISIBLE
                    pill_before.visibility = View.VISIBLE
                }
            }
        }
    }
}