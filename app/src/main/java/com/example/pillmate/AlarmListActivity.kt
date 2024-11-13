package com.example.pillmate

import android.content.SharedPreferences
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

        // SharedPreferences에서 userId를 가져옴
        val sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)

        if (userId != -1) {
            loadAlarmHistory(userId)
        } else {
            Log.e("AlarmListActivity", "userId를 찾을 수 없습니다.")
        }

        binding.btnBefore.setOnClickListener {
            finish()
        }
    }

    private fun loadAlarmHistory(userId: Int) {
        val alarmDatabase = AlarmDatabase.getInstance(this)

        CoroutineScope(Dispatchers.IO).launch {
            val logs = alarmDatabase.alarmLogDao().getAllLogsForUser(userId)

            val newAlarms = logs.map { log ->
                AlarmListItem(
                    type = "복약알림",
                    des = log.message,
                    time = getTimeAgo(log.timestamp)
                )
            }

            withContext(Dispatchers.Main) {
                alarmListAdapter.updateAlarmList(newAlarms)
                binding.tvNoAlarm.visibility = if (newAlarms.isEmpty()) View.VISIBLE else View.GONE
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

