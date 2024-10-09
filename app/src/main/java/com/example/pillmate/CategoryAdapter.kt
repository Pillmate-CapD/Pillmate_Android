package com.example.pillmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val itemCategory: MutableList<CategoryItem>,
                      private val onCategorySelected: (String) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pill_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemCategory.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = itemCategory[position]
        holder.bind(item)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.pill_category_txt)
        private val underlineView: View = itemView.findViewById(R.id.underline_view)

        fun bind(item: CategoryItem) {
            name.text = item.name
            underlineView.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE

            // Change fontFamily based on selection state
            val typeface = if (item.isSelected) {
                ResourcesCompat.getFont(itemView.context, R.font.notosanskrbold)
            } else {
                ResourcesCompat.getFont(itemView.context, R.font.notosanskrmedium)
            }
            name.typeface = typeface

            itemView.setOnClickListener {
                // Reset all items' isSelected to false
                for (categoryItem in itemCategory) {
                    categoryItem.isSelected = false
                }
                // Set the clicked item isSelected to true
                item.isSelected = true

                // Notify the adapter to refresh the list
                notifyDataSetChanged()

                // 선택된 카테고리를 콜백으로 전달
                onCategorySelected(item.name)
            }
        }
    }
}