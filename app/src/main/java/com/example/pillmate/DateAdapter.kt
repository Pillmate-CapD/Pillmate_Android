package com.example.pillmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        init {
//            // 각 아이템을 클릭하는 이벤트를 처리합니다.
//            itemView.setOnClickListener {
//                onItemClick(adapterPosition) // 클릭한 아이템의 위치를 가져와서 전달합니다.
//            }
//        }
        private val day: TextView = itemView.findViewById(R.id.day_recy)
        private val date: TextView = itemView.findViewById(R.id.date_recy)

        fun bind(item: DateItem) {
            day.text = item.day
            date.text = item.date
        }
    }

}