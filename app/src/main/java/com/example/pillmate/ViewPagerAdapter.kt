package com.example.pillmate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.databinding.FragmentViewPageBinding

class ViewPagerAdapter(
    private val medicineList: List<MedicineInfo>
) : RecyclerView.Adapter<ViewPagerAdapter.MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val binding = FragmentViewPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(medicineList[position])
    }

    override fun getItemCount(): Int = medicineList.size

    inner class MedicineViewHolder(private val binding: FragmentViewPageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(medicineInfo: MedicineInfo) {
            binding.vpMediName.text = medicineInfo.mediName
            binding.tvCategory.text = medicineInfo.category
            binding.vpMediAmount.text = "${medicineInfo.oneEat}회"
            binding.vpMediPerday.text = "${medicineInfo.oneDay}정"
            binding.vpMediDay.text = "${medicineInfo.allDay}일"
            binding.vpMediSlot.text = medicineInfo.timeSlotList.joinToString(separator = "\n") { "${it.spinnerTime} | ${it.pickerTime}" }
        }
    }
}