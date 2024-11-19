package com.example.capdi_eat_test

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.AlarmListResponse
import com.example.pillmate.AlarmReceiver
import com.example.pillmate.ListAlarmAdapter
import com.example.pillmate.ListAlarmItem
import com.example.pillmate.R
import com.example.pillmate.RetrofitApi
import com.example.pillmate.databinding.FragmentAlarmSetBinding
import com.example.pillmate.databinding.FragmentHomeBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class AlarmSetFragment : Fragment() {
    private lateinit var binding: FragmentAlarmSetBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListAlarmAdapter
    private var listAlarmItems: MutableList<ListAlarmItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlarmSetBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        recyclerView = binding.alarmListRecy
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter 설정
        adapter = ListAlarmAdapter(listAlarmItems, requireContext())
        recyclerView.adapter = adapter

        // 알람 Get API 연결하기
        getAlarmDataFromServer()

        return binding.root
    }

    // API 호출 및 RecyclerView 업데이트 함수
    private fun getAlarmDataFromServer() {
        Log.d("AlarmSetFragment", "Starting API call to get alarm data")
        val service = RetrofitApi.getRetrofitService // Retrofit 인스턴스 가져오기
        val call = service.getAlarm() // Alarm 데이터를 가져오는 API 호출

        call.enqueue(object : Callback<List<AlarmListResponse>> {
            override fun onResponse(
                call: Call<List<AlarmListResponse>>,
                response: Response<List<AlarmListResponse>>
            ) {
                if (response.isSuccessful) {
                    val alarmList = response.body()

                    if (alarmList.isNullOrEmpty()) {
                        // 데이터가 없거나 null인 경우
                        binding.alarmListRecy.visibility = View.GONE
                        binding.nonMediAlarmLayout.visibility = View.VISIBLE
                    } else {
                        // 데이터가 있는 경우
                        binding.alarmListRecy.visibility = View.VISIBLE
                        binding.nonMediAlarmLayout.visibility = View.GONE

                        Log.d("AlarmSetFragment", "API call successful, received ${alarmList.size} alarms")

                        // 데이터를 받아서 listAlarmItems에 추가
                        listAlarmItems.clear() // 기존 데이터를 초기화
                        listAlarmItems.addAll(alarmList.map { alarm ->
                            // 시간 포맷 변경 (24시간제 -> 12시간제)
                            val formattedTime = convert24HourTo12Hour(alarm.timeSlot.pickerTime)

                            Log.d("AlarmSetFragment", "Adding alarm: ${alarm.name} at $formattedTime")

                            ListAlarmItem(
                                alarm.id,
                                if (alarm.timeSlot.pickerTime.split(":")[0].toInt() < 12) "오전" else "오후", // 오전/오후 구분
                                formattedTime, // 포맷팅된 시간
                                alarm.name, // 약품명
                                "${alarm.amount}정",
                                "매일 ${alarm.day}회",
                            "${alarm.timesPerDay}일",
                                alarm.timeSlot.spinnerTime, // 시간대 설명
                                alarm.isAvailable // 사용 가능 여부
                            )
                        })
                        adapter.notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알림
                        Log.d("AlarmSetFragment", "Adapter notified, data updated")
                    }
                } else {
                    Log.e("AlarmSetFragment", "Failed to get alarm data: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "알람 데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()

                    // API 실패 시 레이아웃 처리
                    binding.alarmListRecy.visibility = View.GONE
                    binding.nonMediAlarmLayout.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<AlarmListResponse>>, t: Throwable) {
                Log.e("AlarmSetFragment", "API call failed: ${t.message}")
                //Toast.makeText(requireContext(), "알람 데이터를 가져오는 데 실패했습니다: ${t.message}", Toast.LENGTH_SHORT).show()

                // 네트워크 오류 발생 시 레이아웃 처리
                binding.alarmListRecy.visibility = View.GONE
                binding.nonMediAlarmLayout.visibility = View.VISIBLE
            }
        })
    }


    // 24시간 형식(18:00:00)을 12시간 형식(오후 06:00)으로 변환하는 함수
    private fun convert24HourTo12Hour(time: String): String {
        val hour = time.split(":")[0].toInt()
        val minute = time.split(":")[1]
        val amPm = if (hour < 12) "오전" else "오후"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        Log.d("AlarmSetFragment", "Converted time: $time to $amPm ${String.format("%02d", hour12)}:$minute")
        return "${String.format("%02d", hour12)}:$minute"
    }



    override fun onResume() {
        super.onResume()
        // 화면이 다시 켜질 때마다 데이터 갱신
        Log.d("AlarmSetFragment", "Fragment resumed, fetching alarm data")
        getAlarmDataFromServer()
    }
}