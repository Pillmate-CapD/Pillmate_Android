package com.example.pillmate

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class ListAlarmAdapter(
    private val alarmList: MutableList<ListAlarmItem>,
    private val context: Context
) : RecyclerView.Adapter<ListAlarmAdapter.ListAlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_alarm_item, parent, false)
        return ListAlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListAlarmViewHolder, position: Int) {
        val item = alarmList[position]

        // 텍스트 필드 설정
        holder.tvAmPm.text = item.amPm
        holder.tvTime.text = item.time
        holder.medicationTitle.text = item.medicationTitle
        holder.medicationInfo.text = item.medicationInfo
        holder.medicationTime.text = item.medicationTime

        // 스위치 상태 설정 전에 리스너를 제거하여 리사이클링 시 발생하는 문제 방지
        holder.switchAlarm.setOnCheckedChangeListener(null)
        holder.switchAlarm.isChecked = item.isAlarmOn

        // 텍스트 색상 설정
        setTextColors(holder, item.isAlarmOn)

        // 스위치 리스너 설정
        holder.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            // 아이템의 상태 업데이트
            item.isAlarmOn = isChecked

            // 텍스트 색상 변경
            setTextColors(holder, isChecked)

            // 알람 상태를 서버에 업데이트하는 로직 추가 (필요 시)
            // updateAlarmStatus(item)
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

    // 텍스트 색상 설정 메서드
    private fun setTextColors(holder: ListAlarmViewHolder, isAlarmOn: Boolean) {
        if (isAlarmOn) {
            val activeColor = Color.parseColor("#3E3E3E")
            holder.tvAmPm.setTextColor(activeColor)
            holder.tvTime.setTextColor(activeColor)
            holder.medicationTitle.setTextColor(activeColor)
            holder.medicationInfo.setTextColor(activeColor)
            holder.medicationTime.setTextColor(activeColor)
        } else {
            // 기본 색상으로 설정 (예: 검정색)
            val defaultColor = Color.parseColor("#898989")
            holder.tvAmPm.setTextColor(defaultColor)
            holder.tvTime.setTextColor(defaultColor)
            holder.medicationTitle.setTextColor(defaultColor)
            holder.medicationInfo.setTextColor(defaultColor)
            holder.medicationTime.setTextColor(defaultColor)
        }
    }

    // 데이터 업데이트 메서드 추가
    fun updateData(newAlarmList: List<ListAlarmItem>) {
        alarmList.clear()
        alarmList.addAll(newAlarmList)
        notifyDataSetChanged()
    }
}
