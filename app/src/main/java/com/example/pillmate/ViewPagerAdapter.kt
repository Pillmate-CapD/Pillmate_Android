package com.example.pillmate

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
            binding.vpMediSlot.text = medicineInfo.timeSlotList.joinToString(separator = "\n") {
                "${it.spinnerTime} | ${it.pickerTime}"
            }

            // 카테고리에 따라 텍스트와 배경색 설정
            val background = binding.tvCategory.background as GradientDrawable
            when (medicineInfo.category) {
                "심혈관질환" -> {
                    background.setColor(Color.parseColor("#40FFCEDF"))
                    binding.tvCategory.setTextColor(Color.parseColor("#FD5592"))
                }
                "고혈압" -> {
                    background.setColor(Color.parseColor("#E6EBFA"))
                    binding.tvCategory.setTextColor(Color.parseColor("#1E54DF"))
                }
                "당뇨" -> {
                    background.setColor(Color.parseColor("#D6F0EF"))
                    binding.tvCategory.setTextColor(Color.parseColor("#0CBBB2"))
                }
                "고지혈증" -> {
                    background.setColor(Color.parseColor("#B0FFBCB8"))
                    binding.tvCategory.setTextColor(Color.parseColor("#FF453A"))
                }
                "호흡기질환" -> {
                    background.setColor(Color.parseColor("#5CEDF2A3"))
                    binding.tvCategory.setTextColor(Color.parseColor("#E2B100"))
                }
                "기타" -> {
                    background.setColor(Color.parseColor("#F0DDF7"))
                    binding.tvCategory.setTextColor(Color.parseColor("#951FC0"))
                }
                else -> {
                    background.setColor(Color.parseColor("#F0DDF7"))
                    binding.tvCategory.setTextColor(Color.parseColor("#951FC0"))
                }
            }
        }
    }
}
