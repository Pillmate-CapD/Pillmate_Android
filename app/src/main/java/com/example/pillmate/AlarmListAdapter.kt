package com.example.pillmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlarmListAdapter (private val alarmListItem : ArrayList<AlarmListItem>) :
    RecyclerView.Adapter<AlarmListAdapter.AlarmListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_alarm_list, parent, false)
        return AlarmListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return alarmListItem.size
    }

    override fun onBindViewHolder(holder: AlarmListViewHolder, position: Int) {
        val item = alarmListItem[position]
        holder.bind(item)
    }

    inner class AlarmListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val type: TextView = itemView.findViewById(R.id.alarm_type)
        private val des: TextView = itemView.findViewById(R.id.alarm_des)
        private val time: TextView = itemView.findViewById(R.id.alarm_des_time)

        fun bind(item: AlarmListItem) {
            type.text = item.type
            des.text = item.des
            time.text = item.time
        }
    }
}