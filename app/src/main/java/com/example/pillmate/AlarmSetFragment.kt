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
import com.example.pillmate.AlarmReceiver
import com.example.pillmate.R
import com.example.pillmate.databinding.FragmentAlarmSetBinding
import com.example.pillmate.databinding.FragmentHomeBinding
import java.util.Calendar

class AlarmSetFragment : Fragment() {
    private lateinit var binding: FragmentAlarmSetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlarmSetBinding.inflate(inflater, container, false)

        binding.btnSetAlarm.setOnClickListener {
            setAlarm(10,"파스틱정")
            Toast.makeText(requireContext(), "10초 후에 알람이 울립니다", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun setAlarm(seconds: Int, pillName: String) {
        //val context = itemView.context
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_RESTART_SERVICE // 추가된 부분
            putExtra("pill_name", pillName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.SECOND, seconds)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canScheduleExactAlarms = alarmManager.canScheduleExactAlarms()
            if (!canScheduleExactAlarms) {
                Log.w("PillListAdapter", "Requesting exact alarm permission.")
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
                //fragment.startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM)
                return
            }
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            // Log the alarm time
            val formattedTime = "%02d:%02d:%02d".format(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
            )
            Log.d("PillListAdapter", "Alarm set for: $formattedTime, Pill: $pillName")
        } catch (e: SecurityException) {
            Log.e("PillListAdapter", "SecurityException: ${e.message}")
            // Handle SecurityException gracefully, perhaps by requesting necessary permissions
        }
    }
}