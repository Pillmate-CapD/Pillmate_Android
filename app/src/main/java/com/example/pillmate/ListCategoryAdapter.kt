package com.example.pillmate

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListCategoryAdapter(
    private val categories: List<String>,
    private var selectedPosition: Int = 0,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ListCategoryAdapter.ListCategoryViewHolder>() {

    inner class ListCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: TextView = view.findViewById(R.id.categoryButton)

        init {
            button.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 선택된 아이템을 갱신
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(selectedPosition)
                    onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_category, parent, false)
        return ListCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListCategoryViewHolder, position: Int) {
        holder.button.text = categories[position]
        // 선택 여부에 따른 스타일 적용
        if (position == selectedPosition) {
            holder.button.setBackgroundResource(R.drawable.bg_list_category_check)
            holder.button.setTextColor(Color.parseColor("#FFFFFF"))

        } else {
            holder.button.setBackgroundColor(Color.parseColor("#F9F9F9"))
            holder.button.setTextColor(Color.parseColor("#D9D9D9"))
        }
    }

    override fun getItemCount(): Int = categories.size
}
