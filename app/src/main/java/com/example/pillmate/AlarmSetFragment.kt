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
import com.example.pillmate.AlarmReceiver
import com.example.pillmate.ListAlarmAdapter
import com.example.pillmate.ListAlarmItem
import com.example.pillmate.R
import com.example.pillmate.databinding.FragmentAlarmSetBinding
import com.example.pillmate.databinding.FragmentHomeBinding
import java.util.Calendar

class AlarmSetFragment : Fragment() {
    private lateinit var binding: FragmentAlarmSetBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListAlarmAdapter
    private lateinit var listAlarmItems: MutableList<ListAlarmItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlarmSetBinding.inflate(inflater, container, false)

//        binding.btnSetAlarm.setOnClickListener {
//            //setAlarm(10,"파스틱정")
//            Toast.makeText(requireContext(), "10초 후에 알람이 울립니다", Toast.LENGTH_SHORT).show()
//        }

        // RecyclerView 설정
        recyclerView = binding.alarmListRecy
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 샘플 데이터 생성
        listAlarmItems = mutableListOf(
            ListAlarmItem("오전", "07:00", "트윈스타정 (고혈압)", "1정 | 매일 1회 | 90일", "기상 직후", true),
            ListAlarmItem("오후", "12:00", "다이미크롱서방정 (제2형당뇨)", "1정 | 매일 1회 | 90일", "기상 직후", false),
            ListAlarmItem("오전", "07:00", "트윈스타정 (고혈압)", "1정 | 매일 1회 | 90일", "기상 직후", true),
            ListAlarmItem("오후", "12:00", "다이미크롱서방정 (제2형당뇨)", "1정 | 매일 1회 | 90일", "기상 직후", false),
            ListAlarmItem("오전", "07:00", "트윈스타정 (고혈압)", "1정 | 매일 1회 | 90일", "기상 직후", true),
            ListAlarmItem("오후", "12:00", "다이미크롱서방정 (제2형당뇨)", "1정 | 매일 1회 | 90일", "기상 직후", false),
            ListAlarmItem("오전", "07:00", "트윈스타정 (고혈압)", "1정 | 매일 1회 | 90일", "기상 직후", true),
            ListAlarmItem("오후", "12:00", "다이미크롱서방정 (제2형당뇨)", "1정 | 매일 1회 | 90일", "기상 직후", false)

        )

        // Adapter 설정
        adapter = ListAlarmAdapter(listAlarmItems, requireContext())
        recyclerView.adapter = adapter


        return binding.root
    }

//    private fun setAlarm(seconds: Int, pillName: String) {
//        //val context = itemView.context
//        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, AlarmReceiver::class.java).apply {
//            action = AlarmReceiver.ACTION_RESTART_SERVICE // 추가된 부분
//            putExtra("pill_name", pillName)
//        }
//        val pendingIntent = PendingIntent.getBroadcast(
//            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            add(Calendar.SECOND, seconds)
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val canScheduleExactAlarms = alarmManager.canScheduleExactAlarms()
//            if (!canScheduleExactAlarms) {
//                Log.w("PillListAdapter", "Requesting exact alarm permission.")
//                val intent = Intent().apply {
//                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
//                }
//                //fragment.startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM)
//                return
//            }
//        }
//
//        try {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                pendingIntent
//            )
//            // Log the alarm time
//            val formattedTime = "%02d:%02d:%02d".format(
//                calendar.get(Calendar.HOUR_OF_DAY),
//                calendar.get(Calendar.MINUTE),
//                calendar.get(Calendar.SECOND)
//            )
//            Log.d("PillListAdapter", "Alarm set for: $formattedTime, Pill: $pillName")
//        } catch (e: SecurityException) {
//            Log.e("PillListAdapter", "SecurityException: ${e.message}")
//            // Handle SecurityException gracefully, perhaps by requesting necessary permissions
//        }
//    }
}