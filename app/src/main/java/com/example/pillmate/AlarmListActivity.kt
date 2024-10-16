package com.example.pillmate

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.ActivityAlarmListBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlarmListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmListBinding
    private lateinit var alarmListAdapter: AlarmListAdapter
    private val alarmItems = mutableListOf<AlarmListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        alarmListAdapter = AlarmListAdapter(alarmItems as ArrayList<AlarmListItem>)
        binding.alarmList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.alarmList.adapter = alarmListAdapter

        // 알림 히스토리 불러오기
        loadAlarmHistory()

        binding.btnBefore.setOnClickListener {
            finish()
        }
    }


    private fun loadAlarmHistory() {
        val sharedPreferences = getSharedPreferences("alarmHistory", MODE_PRIVATE)
        val savedAlarms = sharedPreferences.getStringSet("alarms", mutableSetOf())

        // savedAlarms 로그로 출력
        Log.d("AlarmListActivity", "Saved Alarms: $savedAlarms")

        // Create a new list for alarm items
        val newAlarms = mutableListOf<AlarmListItem>()

        savedAlarms?.forEach { alarmData ->
            val alarmDetails = alarmData.split(", ")
            if (alarmDetails.size == 2) {
                val alarmMessage = alarmDetails[0]
                val alarmTime = alarmDetails[1]
                val timeAgo = getTimeAgo(alarmTime)

                // 알람 항목을 로그로 출력
                Log.d("AlarmListActivity", "Alarm Message: $alarmMessage, Alarm Time: $alarmTime")

                // 새로운 알람 항목 추가
                newAlarms.add(AlarmListItem("복약알림", alarmMessage, timeAgo))
            }
        }

        // 어댑터에 데이터를 전달하고 업데이트
        alarmListAdapter.updateAlarmList(newAlarms)

        // "알림이 없습니다" 메시지를 처리
        if (newAlarms.isEmpty()) {
            binding.tvNoAlarm.visibility = View.VISIBLE // Show "No alarms" message
        } else {
            binding.tvNoAlarm.visibility = View.GONE
        }
    }



    fun getTimeAgo(time: String): String {
        // time이 "yyyy-MM-dd HH:mm:ss" 형식으로 넘어올 것으로 가정
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val alarmTime = formatter.parse(time)

        val currentTime = Calendar.getInstance().time
        val diffInMillis = currentTime.time - alarmTime.time

        // 차이를 계산하여 시간 단위로 변환
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return when {
            diffInMinutes < 1 -> "방금 전"
            diffInMinutes < 60 -> "${diffInMinutes}분 전"
            diffInHours < 24 -> "${diffInHours}시간 전"
            diffInDays == 1L -> "어제"
            else -> "${diffInDays}일 전"
        }
    }
}
