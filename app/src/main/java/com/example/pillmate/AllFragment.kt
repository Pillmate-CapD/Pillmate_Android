package com.example.capdi_eat_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.R

class AllFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private val medicationList = ArrayList<MedicationItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_allbtn, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = object : RecyclerView.Adapter<MedicationViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pill_list, parent, false)
                return MedicationViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
                val item = medicationList[position]
                holder.medicationTitle.text = item.title
                holder.medicationInfo.text = item.info
                holder.medicationTime.text = item.time
            }

            override fun getItemCount(): Int {
                return medicationList.size
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Add dummy data
        addDummyData()

        return view
    }

    private fun addDummyData() {
        medicationList.add(MedicationItem("트윈스타정 (고혈압)", "1정 | 매일 1회 | 3개월", "기상 직후"))
        medicationList.add(MedicationItem("디아미크롱서방정 (제2형당뇨)", "1정 | 매일 1회 | 3개월", "기상 직후"))
        medicationList.add(MedicationItem("파스틱정 (제2형당뇨)", "1정 | 매일 2회 | 3개월", "점심 식전, 저녁 식전"))
        medicationList.add(MedicationItem("바이토린 (고지혈증)", "1정 | 매일 1회 | 3개월", "저녁 식후"))


        // Notify adapter about data change
        adapter.notifyDataSetChanged()
    }

    // ViewHolder 클래스 정의
    private class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicationTitle = itemView.findViewById<TextView>(R.id.medication_title)
        val medicationInfo = itemView.findViewById<TextView>(R.id.medication_info)
        val medicationTime = itemView.findViewById<TextView>(R.id.medication_time)
    }
}