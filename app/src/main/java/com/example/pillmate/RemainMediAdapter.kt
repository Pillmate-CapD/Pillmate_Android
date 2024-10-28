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
            binding.remainItemMedi.text = item.name
            binding.remainTimeMedi.text = "${item.day}일 남음"
        }
    }

    fun updateItems(newItems: List<RemainMediItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

