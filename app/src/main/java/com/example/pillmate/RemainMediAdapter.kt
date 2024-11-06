package com.example.pillmate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.databinding.RemainingItemBinding

class RemainMediAdapter(
    private var items: List<RemainMediItem>
) : RecyclerView.Adapter<RemainMediAdapter.RemainMediViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemainMediViewHolder {
        val binding = RemainingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RemainMediViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RemainMediViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RemainMediViewHolder(private val binding: RemainingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RemainMediItem) {
            // 기본 텍스트 설정
            binding.remainItemMedi.text = item.name
            binding.remainTimeMedi.text = "${item.day}일 남음"

            // 카테고리에 따른 배경 및 이미지 변경
            when (item.category) {
                "고혈압" -> {
                    binding.root.setBackgroundResource(R.drawable.box1)
                    binding.pillView.setBackgroundResource(R.drawable.sample1)
                }

                "심혈관질환" -> {
                    binding.root.setBackgroundResource(R.drawable.box2)
                    binding.pillView.setBackgroundResource(R.drawable.sample2)
                }

                "고지혈증" -> {
                    binding.root.setBackgroundResource(R.drawable.box3)
                    binding.pillView.setBackgroundResource(R.drawable.sample3)
                }

                "당뇨" -> {
                    binding.root.setBackgroundResource(R.drawable.box4)
                    binding.pillView.setBackgroundResource(R.drawable.sample4)
                }

                "호흡기질환" -> {
                    binding.root.setBackgroundResource(R.drawable.box5)
                    binding.pillView.setBackgroundResource(R.drawable.sample5)
                }

                "기타" -> {
                    binding.root.setBackgroundResource(R.drawable.box6)
                    binding.pillView.setBackgroundResource(R.drawable.sample6)
                }

                else -> {
                    // 기본 카테고리 배경 및 이미지 설정
                    binding.root.setBackgroundResource(R.drawable.box6)
                    binding.pillView.setBackgroundResource(R.drawable.sample6)
                }
            }
        }
    }

    fun updateItems(newItems: List<RemainMediItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

