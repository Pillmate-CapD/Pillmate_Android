package com.example.pillmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.ActivityAlarmListBinding

class AlarmListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmListBinding
    private lateinit var alarmListAdapter: AlarmListAdapter
    private val alarmItems = mutableListOf<AlarmListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 홈화면 => 주간 달력
        alarmListAdapter = AlarmListAdapter(alarmItems as ArrayList<AlarmListItem>)
        binding.alarmList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.alarmList.adapter = alarmListAdapter

        // 카테고리 아이템 추가
        alarmItems.add(AlarmListItem("복약알림","\'파스틱정\'을 먹을 시간이에요", "방금 전"))
        alarmItems.add(AlarmListItem("복약알림","\'트윈스타정\', \'디아미크롱정\'을 먹을 시간이에요", "5시간 전"))
        alarmItems.add(AlarmListItem("건강일지알림","오늘의 건강일지를 작성해보세요", "1일전"))

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

}