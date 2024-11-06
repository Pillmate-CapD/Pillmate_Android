package com.example.pillmate

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

            // 알람 상태를 서버에 업데이트하는 로직 추가
            updateAlarmStatus(item.id, item.isAlarmOn,position)

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

        val defaultTypeface = ResourcesCompat.getFont(holder.itemView.context, R.font.notosanskrlight)
        val activeTypeface = ResourcesCompat.getFont(holder.itemView.context, R.font.notosanskrregular)

        if (isAlarmOn) {
            val activeColor = Color.parseColor("#3E3E3E")
            holder.tvAmPm.typeface = activeTypeface
            holder.tvAmPm.setTextColor(activeColor)
            holder.tvTime.setTextColor(activeColor)
            holder.medicationTitle.setTextColor(activeColor)
            holder.medicationInfo.setTextColor(activeColor)
            holder.medicationTime.setTextColor(activeColor)
        } else {
            // 기본 색상으로 설정 (예: 검정색)
            val defaultColor = Color.parseColor("#898989")
            holder.tvAmPm.typeface = defaultTypeface
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

    // 알람 상태 업데이트 메서드
    private fun updateAlarmStatus(id: Int, isAlarmOn: Boolean, position: Int) {
        val service = RetrofitApi.getRetrofitService
        val call = service.patchAlarm(id, isAlarmOn)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("patchAlarm", "알람 on/off 상태가 성공적으로 업데이트됨")

                    // 성공적으로 업데이트되었음을 사용자에게 알림
                    Toast.makeText(context, "알람 상태가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()

                    // 아이템 업데이트 (필요할 경우, 알람 목록을 다시 불러올 수도 있음)
                    alarmList[position].isAlarmOn = isAlarmOn
                    notifyItemChanged(position)
                } else {
                    Log.e("patchAlarm", "서버 응답 실패")
                    Toast.makeText(context, "알람 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("patchAlarm", "알람 상태 업데이트 실패", t)
                Toast.makeText(context, "서버 오류로 인해 알람 상태 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    // 알람 목록을 서버에서 다시 가져오는 메서드
//    fun fetchAlarmList() {
//        val service = RetrofitApi.getRetrofitService
//        val call = service.getAlarm()
//
//        call.enqueue(object : Callback<List<ListAlarmItem>>{
//            override fun onResponse(call: Call<List<ListAlarmItem>>, response: Response<List<ListAlarmItem>>) {
//                if (response.isSuccessful && response.body() != null) {
//                    val newAlarmList = response.body()!!
//                    updateData(newAlarmList)
//                    Log.d("fetchAlarmList", "알람 목록 불러오기 성공")
//                } else {
//                    Log.e("fetchAlarmList", "서버 응답 실패")
//                }
//            }
//
//            override fun onFailure(call: Call<List<ListAlarmItem>>, t: Throwable) {
//                Log.e("fetchAlarmList", "알람 목록 불러오기 실패", t)
//            }
//        })
//    }
}
