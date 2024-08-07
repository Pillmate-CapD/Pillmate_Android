package com.example.pillmate

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pillmate.databinding.ActivityAlarmBinding
import com.example.pillmate.databinding.ActivityAlarmListBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AlarmActivity : AppCompatActivity(), BottomSheetFragment.BottomSheetListener {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var calendar: Calendar
    private lateinit var powerManager: PowerManager
    private var flag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // xml에서 텍스트 밑줄
        binding.btnTodayNone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)

        calendar = Calendar.getInstance()

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

        turnScreenOnAndKeyguardOff()

        Thread {
            while (flag) {
                try {
                    calendar = Calendar.getInstance()
                    runOnUiThread {
                        updateTimeText()
                    }
                    Thread.sleep(1000)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
            }
        }.start()

        binding.btnPill.setOnClickListener {
            flag = false
            stopAlarm()
            // EatMediActivity로 이동
            val intent = Intent(this, EatMediActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnTodayNone.setOnClickListener {
            flag = false
            stopAlarm()
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun updateTimeText() {
        val hourOfDay = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val timeString: String

        val formattedMinute = if (minute < 10) {
            "0$minute"
        } else {
            "$minute"
        }

        timeString = when {
            hourOfDay in 1..11 -> "$hourOfDay:$formattedMinute"
            hourOfDay == 12 -> "$hourOfDay:$formattedMinute"
            else -> {
                val formattedHour = if (hourOfDay > 12) {
                    hourOfDay - 12
                } else {
                    hourOfDay
                }
                "$formattedHour:$formattedMinute"
            }
        }

        binding.time.text = timeString

        val dateFormat = SimpleDateFormat("MM월 dd일 EEEE", Locale.getDefault())
        val dateString = dateFormat.format(calendar.time)
        binding.date.text = dateString
    }

    @SuppressLint("NewApi")
    private fun turnScreenOnAndKeyguardOff() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)
    }

    private fun stopAlarm() {
        val serviceIntent = Intent(this, AlarmService::class.java)
        stopService(serviceIntent)
    }

    override fun onAlarmDismiss() {
        finish()
    }
}