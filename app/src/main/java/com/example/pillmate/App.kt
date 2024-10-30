package com.example.pillmate

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class App : Application(){
    companion object{
        lateinit var prefs:Prefs
    }
    override fun onCreate() {
        prefs=Prefs(applicationContext)
        scheduleDailyPillAlarmWorker(this)
        super.onCreate()
    }

    fun scheduleDailyPillAlarmWorker(context: Context) {
        // 자정에 실행되도록 예약 (24시간마다 실행)
        val dailyRequest = PeriodicWorkRequestBuilder<PillAlarmWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS) // 자정까지의 시간 계산
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyPillAlarmWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyRequest
        )
    }

    // 자정까지 남은 시간을 계산하는 함수
    fun calculateInitialDelay(): Long {
        val now = Calendar.getInstance()
        val nextMidnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_MONTH, 1) // 다음날 자정
            }
        }
        return nextMidnight.timeInMillis - now.timeInMillis
    }
}