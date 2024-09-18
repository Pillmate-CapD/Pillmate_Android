package com.example.pillmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MediListAdapter(private var medicines: List<MediListResponse>) : RecyclerView.Adapter<MediListAdapter.MediViewHolder>() {

    inner class MediViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.image_view)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val info: TextView = itemView.findViewById(R.id.medication_info)
        val time: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pill_list, parent, false)
        return MediViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediViewHolder, position: Int) {
        val medi = medicines[position]

        // Title 설정
        holder.title.text = medi.name

        // Info 포맷팅 및 설정
        val formattedInfo = "${medi.amount}정 | 매일 ${medi.timesPerDay}회 | ${medi.day}일"
        holder.info.text = formattedInfo

        // Time 설정 (첫 번째 timeSlot 사용)
        if (medi.timeSlotList.isNotEmpty()) {
            val timeSlot = medi.timeSlotList[0] // 첫 번째 timeSlot 사용
            val formattedTime = "${timeSlot.spinnerTime} - ${timeSlot.pickerTime}"
            holder.time.text = formattedTime
        } else {
            holder.time.text = "시간대 없음"
        }

        // 이미지 로딩 (Glide 사용)
        Glide.with(holder.itemView.context)
            .load(medi.picture)
            .placeholder(R.color.black) // 기본 이미지
            .into(holder.picture)
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    // 데이터 업데이트 메서드 추가
    fun updateData(newMedicines: List<MediListResponse>) {
        this.medicines = newMedicines
        notifyDataSetChanged()
    }
}
