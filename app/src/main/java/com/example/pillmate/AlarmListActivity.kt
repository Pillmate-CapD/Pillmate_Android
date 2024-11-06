package com.example.pillmate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.ActivityAlarmListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class AlarmListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmListBinding
    private lateinit var alarmListAdapter: AlarmListAdapter
    private val alarmItems = mutableListOf<AlarmListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // roomdb에 내용 너무 많아서 일단 삭제할 때 사용함
        //clearAllAlarmLogs()

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
        val alarmDatabase = AlarmDatabase.getInstance(this)

        CoroutineScope(Dispatchers.IO).launch {
            val logs = alarmDatabase.alarmLogDao().getAllLogs()

            val newAlarms = logs.map { log: AlarmLog ->  // 여기에서 타입 명시
                AlarmListItem(
                    type = "복약알림",
                    des = log.message,
                    time = getTimeAgo(log.timestamp)
                )
            }

            withContext(Dispatchers.Main) {
                // 어댑터의 updateAlarmList 메서드를 사용하여 데이터 갱신
                alarmListAdapter.updateAlarmList(newAlarms)

                // "알림이 없습니다" 메시지를 처리
                binding.tvNoAlarm.visibility = if (newAlarms.isEmpty()) View.VISIBLE else View.GONE

                // tv_alarm_bottom 가시성 설정
                binding.tvAlarmBottom.visibility = if (newAlarms.isNotEmpty()) View.VISIBLE else View.GONE
            }

        }
    }



    private fun getTimeAgo(time: Date): String {
        val currentTime = Calendar.getInstance().time
        val diffInMillis = currentTime.time - time.time

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

    private fun clearAllAlarmLogs() {
        val alarmDatabase = AlarmDatabase.getInstance(this)

        CoroutineScope(Dispatchers.IO).launch {
            alarmDatabase.alarmLogDao().deleteAllLogs()
            Log.d("AlarmDatabase", "All logs deleted")
        }
    }
}

