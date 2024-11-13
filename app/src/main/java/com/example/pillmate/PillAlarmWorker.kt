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
                                time = it.time,
                                name = it.name,
                                isEaten = it.isEaten,
                                medicineId = it.medicineId,
                                alarmId = it.alarmId
                            )
                        }

                        Log.d("PillAlarmWorker", "받아온 약 리스트: $pillList")

                        // 받아온 약 리스트를 통해 알람을 설정
                        pillList.forEach { pill ->
                            Log.d(
                                "PillAlarmWorker",
                                "알람 설정 준비 중 - 약 이름: ${pill.name}, 복용 시간: ${pill.time}, 약 id: ${pill.medicineId}"
                            )
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
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("pill_name", pill.name)
        putExtra("pill_time",pill.time)
        putExtra("pill_id",pill.medicineId)
        putExtra("alarm_id",pill.alarmId)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        pill.medicineId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )

    // Calendar 설정 - 현재 날짜에 시와 분을 설정
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, pill.extractHourAlarm()) // pill.time에서 시 추출
        set(Calendar.MINUTE, pill.extractMinuteAlarm()) // pill.time에서 분 추출

        Log.e("PillTime", "pill.time extractHour, minute: ${pill.extractHour()}, ${pill.extractMinute()}")
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // 설정된 시간이 현재 시간보다 이전이라면, 다음 날로 설정
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    Log.d("AlarmManager", "알람 설정 준비 완료 - 약 이름: ${pill.name}, 알람 시간: ${calendar.time}, 약 id: ${pill.medicineId}, 알람 id: ${pill.alarmId}")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmManager", "정확한 알람을 설정할 수 있는 권한이 없습니다.")
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
            return
        }
    }

    try {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.d("AlarmManager", "최종 알람 설정 완료 - 약 이름: ${pill.name}, 알람 시간: ${calendar.time}")
    } catch (e: SecurityException) {
        Log.e("AlarmManager", "알람 설정 중 오류 발생: ${e.message}")
    }
}