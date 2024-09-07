package com.example.pillmate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class ListAlarmAdapter(
    private val alarmList: List<ListAlarmItem>,
    private val context: Context
) : RecyclerView.Adapter<ListAlarmAdapter.ListAlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_alarm_item, parent, false)
        return ListAlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListAlarmViewHolder, position: Int) {
        val item = alarmList[position]

        // 데이터 바인딩
        holder.tvAmPm.text = item.amPm
        holder.tvTime.text = item.time
        holder.medicationTitle.text = item.medicationTitle
        holder.medicationInfo.text = item.medicationInfo
        holder.medicationTime.text = item.medicationTime
        holder.switchAlarm.isChecked = item.isAlarmOn

        // 스위치 이벤트 핸들링
        holder.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            item.isAlarmOn = isChecked
        }
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    // 뷰 홀더 클래스
    class ListAlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmPm: TextView = itemView.findViewById(R.id.tv_am_pm)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val medicationTitle: TextView = itemView.findViewById(R.id.medication_title)
        val medicationInfo: TextView = itemView.findViewById(R.id.medication_info)
        val medicationTime: TextView = itemView.findViewById(R.id.medication_time)
        val switchAlarm: SwitchCompat = itemView.findViewById(R.id.switch_alarm)
    }
}