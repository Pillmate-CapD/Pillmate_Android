package com.example.capdi_eat_test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.MediListAdapter
import com.example.pillmate.MediListResponse
import com.example.pillmate.R
import com.example.pillmate.RetrofitApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MediListAdapter
    private var medicineList: List<MediListResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_allbtn, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // MediListAdapter 초기화 및 RecyclerView에 설정
        adapter = MediListAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 약 리스트 가져오기
        fetchMedicineList()

//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Add dummy data
        //addDummyData()

        return view
    }


//    recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())



    override fun onResume() {
        super.onResume()
        // 프래그먼트가 다시 활성화될 때마다 약 리스트를 가져옵니다.
        fetchMedicineList()
    }

    private fun fetchMedicineList() {
        val service = RetrofitApi.getRetrofitService
        val call = service.getMediAll()

        call.enqueue(object : Callback<List<MediListResponse>> {
            override fun onResponse(
                call: Call<List<MediListResponse>>,
                response: Response<List<MediListResponse>>
            ) {
                if (response.isSuccessful) {
                    val mediList = response.body()
                    mediList?.let {
                        Log.d("AllFragment", "약 리스트: $mediList")
                        medicineList = it

                        // MediListAdapter에 데이터 업데이트
                        adapter.updateData(it)
                    }
                } else {
                    Log.d("AllFragment", "약 리스트 가져오기 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    //showCustomToast("약 리스트 가져오기 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MediListResponse>>, t: Throwable) {
                Log.e("AllFragment", "API 호출 실패", t)
                //showCustomToast("API 호출 실패: ${t.message}")
            }
        })
    }


//    private fun addDummyData() {
//        medicationList.add(MedicationItem("트윈스타정 (고혈압)", "1정 | 매일 1회 | 3개월", "기상 직후"))
//        medicationList.add(MedicationItem("디아미크롱서방정 (당뇨)", "1정 | 매일 1회 | 3개월", "기상 직후"))
//        medicationList.add(MedicationItem("파스틱정 (당뇨)", "1정 | 매일 2회 | 3개월", "점심 식전, 저녁 식전"))
//        medicationList.add(MedicationItem("바이토린 (고지혈증)", "1정 | 매일 1회 | 3개월", "저녁 식후"))
//        medicationList.add(MedicationItem("트윈스타정 (고혈압)", "1정 | 매일 1회 | 3개월", "기상 직후"))
//        medicationList.add(MedicationItem("디아미크롱서방정 (당뇨)", "1정 | 매일 1회 | 3개월", "기상 직후"))
//        medicationList.add(MedicationItem("파스틱정 (당뇨)", "1정 | 매일 2회 | 3개월", "점심 식전, 저녁 식전"))
//        medicationList.add(MedicationItem("바이토린 (고지혈증)", "1정 | 매일 1회 | 3개월", "저녁 식후"))
//
//
//        // Notify adapter about data change
//        adapter.notifyDataSetChanged()
//    }

//    // ViewHolder 클래스 정의
//    private class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val medicationTitle = itemView.findViewById<TextView>(R.id.medication_title)
//        val medicationInfo = itemView.findViewById<TextView>(R.id.medication_info)
//        val medicationTime = itemView.findViewById<TextView>(R.id.medication_time)
//    }
}