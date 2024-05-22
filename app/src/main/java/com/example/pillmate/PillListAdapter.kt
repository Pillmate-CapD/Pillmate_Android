package com.example.pillmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PillListAdapter (private val pillListItem : ArrayList<PillListItem>) :
    RecyclerView.Adapter<PillListAdapter.PillListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pill_list, parent, false)
        return PillListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pillListItem.size
    }

    override fun onBindViewHolder(holder: PillListViewHolder, position: Int) {
        val item = pillListItem[position]
        holder.bind(item)
    }

    inner class PillListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val time: TextView = itemView.findViewById(R.id.pill_list_time)
        private val pillName: TextView = itemView.findViewById(R.id.pill_list_name)

        fun bind(item: PillListItem) {
            time.text = item.time
            pillName.text = item.pillName
        }
    }
}