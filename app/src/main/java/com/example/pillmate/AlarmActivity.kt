package com.example.pillmate

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.pillmate.databinding.ActivityAlarmBinding
import com.example.pillmate.databinding.ActivityAlarmListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        val pillName = intent.getStringExtra("pill_name") ?: "Unknown"
        //val pillImgUrl = intent.getStringExtra("pill_image_url") // String으로 받기

        //Log.d("AlarmActivity", "pillName: $pillName, pillImgUrl: $pillImgUrl")
        binding.pillName.text = pillName

        fetchMediInfo(pillName)

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
//            val intent = Intent(this, EatMediActivity::class.java)

            // EatMediActivity로 이동하며 pillName 값 전달
            val intent = Intent(this, EatMediActivity::class.java).apply {
                putExtra("pill_name", pillName)
            }

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

    private fun fetchMediInfo(pillName: String) {
        val service = RetrofitApi.getRetrofitService

        Log.d("pillName", "MediInfo pillName: ${pillName}")

        val request = MediInfoRequest(pillName)
        val call = service.postMediInfo(listOf(request))
        call.enqueue(object : Callback<List<MediInfoResponse>> {
            override fun onResponse(call: Call<List<MediInfoResponse>>, response: Response<List<MediInfoResponse>>) {
                if (response.isSuccessful) {
                    val mediInfoList = response.body()
                    mediInfoList?.firstOrNull()?.let { mediInfo ->
                        Log.d("fetchMediInfo", "약물 정보 수신 성공: ${mediInfo.name}, ${mediInfo.photo}, ${mediInfo.category}")

                        // Glide를 사용하여 이미지 로드, 기본 이미지 처리
                        Glide.with(this@AlarmActivity)
                            .load(mediInfo.photo)  // String URL을 직접 사용
                            .placeholder(R.drawable.bg_zoom_null)
                            .error(R.drawable.bg_zoom_null)
                            .into(binding.pillImg)
                        Log.d("mediInfo pillInfo Image", "mediInfo photo: ${mediInfo.photo}")

                    }
                } else {
                    Log.e("fetchMediInfo", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MediInfoResponse>>, t: Throwable) {
                Log.e("fetchMediInfo", "API 호출 실패: ${t.message}")
            }
        })
    }
}