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

class AlarmActivity : AppCompatActivity() {
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

            // EatMediActivity로 이동
            val intent = Intent(this, EatMediActivity::class.java)
            startActivity(intent)

            // 어차피 약 다 먹었으니까 화면 나가도 되지 않나?
            finish()

//            // MainActivity로 이동
//            val mainIntent = Intent(this, MainActivity::class.java)
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(mainIntent)
        }

        binding.btnTodayNone.setOnClickListener {
            val bottomSheetFragment = bottomSheetFragment()
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

        if (hourOfDay in 1..11) {
            timeString = "$hourOfDay:$formattedMinute"
        } else if (hourOfDay == 12) {
            timeString = "$hourOfDay:$formattedMinute"
        } else {
            val formattedHour = if (hourOfDay > 12) {
                hourOfDay - 12
            } else {
                hourOfDay
            }
            timeString = "$formattedHour:$formattedMinute"
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
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        // Android 12 이상에서는 KeyguardManager를 사용하여 잠금 화면을 해제합니다.
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isKeyguardLocked) {
            keyguardManager.requestDismissKeyguard(this, null)
        }
    }
}