package com.example.pillmate

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.pillmate.databinding.ActivityAlarmBinding
import com.example.pillmate.databinding.ActivityAlarmListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.res.ColorStateList
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout


class AlarmActivity : AppCompatActivity(), BottomSheetFragment.BottomSheetListener {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var calendar: Calendar
    private lateinit var powerManager: PowerManager
    private var flag = true
    private var pillImgUrl: String? = null // 이미지 URL 저장

    private var alarmCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pillName = intent.getStringExtra("pill_name") ?: "Unknown"
        val pillTime = intent?.getStringExtra("pill_time") ?: "Unknown"
        val pillId = intent?.getIntExtra("pill_id", -1)
        val alarmId = intent?.getIntExtra("alarm_id", -1)
        Log.d("pillId", "pillId: ${pillId}")
        //val pillImgUrl = intent.getStringExtra("pill_image_url") // String으로 받기

        //Log.d("AlarmActivity", "pillName: $pillName, pillImgUrl: $pillImgUrl")
        binding.pillName.text = pillName

        fetchMediInfo(pillName)

        // xml에서 텍스트 밑줄
        //binding.btnTodayNone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)

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
                putExtra("pill_time", pillTime)
                putExtra("pill_id", pillId)
                putExtra("alarm_id", alarmId)
                //putExtra("pill_image_url", pillImgUrl) // 이미지 URL 전달
                putExtra("source", "alarm") //하늘 추가
            }

            startActivity(intent)
            finish()
        }

        binding.btnTodayNone.setOnClickListener {
            flag = false
            //stopAlarm()

            // null일 경우 -1을 기본값으로 전달
            showAlarmBottomSheet(alarmId ?: -1)
        }

        binding.btnRealarm.setOnClickListener {
            // 현재 시간 기준으로 5분 후로 알람 시간 설정
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(this, AlarmReceiver::class.java).apply {
                //putExtra("pill_name", "알람 리마인더")
                putExtra("pill_name", pillName)
                putExtra("pill_time", pillTime)
                putExtra("pill_id", pillId)
                putExtra("alarm_id", alarmId)
                putExtra("sound", "alarm2") // 알람 사운드를 구분하기 위한 추가 데이터
            }
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 5분 후의 시간 계산
            val triggerTime = Calendar.getInstance().apply {
                add(Calendar.MINUTE, 5) // 5분 추가
            }.timeInMillis

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )

                // 알람 예약 시간 로그 출력
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val formattedTime = dateFormat.format(triggerTime)
                Log.d("AlarmService", "알람이 예약되었습니다: $formattedTime")

            } catch (e: SecurityException) {
                Log.e("AlarmService", "정확한 알람 예약에 실패했습니다: ${e.message}")
                // 사용자에게 알림이나 다른 예외 처리 로직 추가
            }

            Log.d("AlarmService", "5분 뒤에 알람이 설정되었습니다.")
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

//    // BottomSheetDialog를 열어 시간대를 선택하는 함수
//    private fun showAlarmBottomSheet() {
//        val bottomSheetDialog = BottomSheetDialog(this,R.style.AppBottomSheetDialogTheme)
//        val bottomSheetView = layoutInflater.inflate(R.layout.fragment_bottom_sheet, null)
//
//        // BottomSheetDialog에 레이아웃 설정
//        bottomSheetDialog.setContentView(bottomSheetView)
//
//        // 배경 흐림과 색상 설정
//        bottomSheetDialog.window?.apply {
//            // 다이얼로그 자체 배경을 투명하게 설정
//            setBackgroundDrawableResource(android.R.color.transparent)
//
//            // 배경 흐림 설정
//            setDimAmount(0.3f) // 0.0f ~ 1.0f로 흐림 정도 설정
//
//            // 뒷배경 색상 설정 (검정색에 40% 투명도)
//            decorView.setBackgroundColor(Color.parseColor("#66000000")) // 검정색 + 40% 투명도
//        }
//
//        // BottomSheetDialog 표시
//        bottomSheetDialog.show()
//    }

    private fun showAlarmBottomSheet(alarmId: Int) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val bottomSheetView = layoutInflater.inflate(R.layout.fragment_bottom_sheet, null)

        // BottomSheetDialog에 레이아웃 설정
        bottomSheetDialog.setContentView(bottomSheetView)

        // 얘는 그냥 지금 당장 알람 소리 끄고 알람 화면도 끄기
        bottomSheetView.findViewById<TextView>(R.id.btn_fin_alarm).setOnClickListener {
            bottomSheetDialog.dismiss()
            stopAlarm()
            finish()
        }

        // 그냥 하단 바텀 시트 닫기
        bottomSheetView.findViewById<TextView>(R.id.btn_cancel_alarm).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // TODO - 알람을 on/off 하는 기능 추가하기 => patch해야함. 그러려면, medicine의 id를 알아야 함,
        bottomSheetView.findViewById<View>(R.id.btn_check_alarm).setOnClickListener {
            alarmCheck = !alarmCheck // 클릭할 때마다 alarmCheck 값을 반전시킴

            val alarmButton = bottomSheetView.findViewById<View>(R.id.btn_check_alarm)
            // alarmCheck 상태에 따라 backgroundTint 색상을 변경

            if (alarmCheck) {
                // alarmCheck가 true일 때 색상 1E54DF로 설정
                alarmButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1E54DF"))
            } else {
                // alarmCheck가 false일 때 기본 배경색으로 설정 (null로 설정하여 기본값 적용)
                alarmButton.backgroundTintList = null
            }

            Log.d("alarm", "showAlarm alarmId, ${alarmId}, ${alarmCheck}")
            updateAlarmStatus(alarmId,!alarmCheck)
        }

        // 배경 흐림과 색상 설정
        bottomSheetDialog.window?.apply {
            // 다이얼로그 자체 배경을 투명하게 설정
            setBackgroundDrawableResource(android.R.color.transparent)

            // 배경 흐림 설정
            setDimAmount(0.3f) // 0.0f ~ 1.0f로 흐림 정도 설정

            // 뒷배경 색상 설정 (검정색에 40% 투명도)
            decorView.setBackgroundColor(Color.parseColor("#66000000")) // 검정색 + 40% 투명도
        }

        // BottomSheetDialog 표시
        bottomSheetDialog.show()
    }

    // 알람 상태 업데이트 메서드
    private fun updateAlarmStatus(id: Int, isAlarmOn: Boolean) {
        val service = RetrofitApi.getRetrofitService
        val call = service.patchAlarm(id, isAlarmOn)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("patchAlarm", "알람 on/off 상태가 성공적으로 업데이트됨")

                    // 성공적으로 업데이트되었음을 사용자에게 알림
                    Toast.makeText(this@AlarmActivity, "알람 상태가 업데이트되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.e("patchAlarm", "서버 응답 실패")
                    Toast.makeText(this@AlarmActivity, "알람 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("patchAlarm", "알람 상태 업데이트 실패", t)
                Toast.makeText(
                    this@AlarmActivity,
                    "서버 오류로 인해 알람 상태 업데이트에 실패했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun fetchMediInfo(pillName: String) {
        val service = RetrofitApi.getRetrofitService

        Log.d("pillName", "MediInfo pillName: ${pillName}")

        val request = MediInfoRequest(pillName)
        val call = service.postMediInfo(listOf(request))
        call.enqueue(object : Callback<List<MediInfoResponse>> {
            override fun onResponse(
                call: Call<List<MediInfoResponse>>,
                response: Response<List<MediInfoResponse>>
            ) {
                if (response.isSuccessful) {
                    val mediInfoList = response.body()
                    mediInfoList?.firstOrNull()?.let { mediInfo ->
                        Log.d(
                            "fetchMediInfo",
                            "약물 정보 수신 성공: ${mediInfo.name}, ${mediInfo.photo}, ${mediInfo.category}"
                        )
                        //pillImgUrl = mediInfo.photo // 이미지 URL 저장
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