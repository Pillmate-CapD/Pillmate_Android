package com.example.pillmate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.work.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class PillAlarmWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        // 서버에서 약 리스트를 받아오고, 알람을 설정하는 작업을 진행합니다.
        fetchPillDataAndSetAlarms(applicationContext)
        return Result.success()
    }

    private fun fetchPillDataAndSetAlarms(context: Context) {
        val service = RetrofitApi.getRetrofitService
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val call = service.getMain(currentTime)
        //val call = service.getMain()

        call.enqueue(object : Callback<MainPageResponse> {
            override fun onResponse(
                call: Call<MainPageResponse>,
                response: Response<MainPageResponse>
            ) {
                if (response.isSuccessful) {
                    val mainResponse = response.body()
                    mainResponse?.let { response ->
                        val pillList = response.medicineAlarmRecords.map {
                            PillListItem(
                                time = it.time, // 시간 정보는 추가적인 포맷팅이 필요할 수 있음
                                name = it.name,
                                isEaten = it.isEaten,
                                medicineId = it.medicineId
                            )
                        }

                        // 받아온 약 리스트를 통해 알람을 설정
                        pillList.forEach { pill ->
                            setAlarmForPill(context, pill)
                        }

                        Log.d("PillAlarmWorker", "약 리스트를 성공적으로 받아와 알람을 설정했습니다.")
                    }
                }
            }

            override fun onFailure(call: Call<MainPageResponse>, t: Throwable) {
                Log.e("PillAlarmWorker", "서버 요청 실패: ${t.message}")
            }
        })
    }
}

fun setAlarmForPill(context: Context, pill: PillListItem) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // BroadcastReceiver로 전달할 인텐트 생성
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("pill_name", pill.name)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        pill.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )

    // 알람 시간을 설정
    val calendar = Calendar.getInstance().apply {
        val hour = pill.extractHour() // pill의 시간을 기반으로 시간 추출
        val minute = pill.extractMinute() // pill의 시간을 기반으로 분 추출
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    // Android 12 이상에서 정확한 알람을 스케줄링할 수 있는지 확인
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmManager", "정확한 알람을 설정할 수 있는 권한이 없습니다.")
            // 권한 요청을 위한 인텐트 생성 (정확한 알람 권한이 없을 경우)
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            }
            context.startActivity(intent)
            return
        }
    }

    // 알람 설정 (정확한 알람을 설정하고 오류 처리)
    try {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.d("AlarmManager", "알람 설정 완료: ${pill.name} - ${pill.time}")
    } catch (e: SecurityException) {
        Log.e("AlarmManager", "알람 설정 중 오류 발생: ${e.message}")
    }
}
