package com.example.pillmate

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    private val CHANNEL_ID = "AlarmChannel"
    private val NOTIFICATION_ID = 101

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 알림 권한이 있는지 확인하고, 알림을 표시하도록 수정
        if (isNotificationPermissionGranted()) {
            showNotificationPermissionNotification()
        } else {
            // 알림 권한이 없는 경우, 알림 권한 설정 화면으로 이동
            openNotificationSettings()
        }
        return START_STICKY
    }


    private fun openNotificationSettings() {
        val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        settingsIntent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(settingsIntent)
    }

    private fun showNotificationPermissionNotification() {
        val notificationIntent = Intent(this, AlarmActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(notificationIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    override fun onCreate() {
        super.onCreate()
        // Foreground Service로 시작합니다.
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    // 알림을 생성합니다.
    private fun createNotification(): Notification {
        val intent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        // 전체 화면 용 Activity Intent 생성
        val fullscreenIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullscreenPendingIntent = PendingIntent.getActivity(this, 0, fullscreenIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // 알림 채널을 생성합니다.
        createNotificationChannel()

        // 알림을 생성합니다.
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm")
            .setContentText("Alarm is ringing")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX) // 중요도를 MAX로 변경하여 전체 화면 알림으로 설정
            .setAutoCancel(true)
            .setFullScreenIntent(fullscreenPendingIntent, true) // 전체 화면으로 설정
            .build()
    }

    // 알림 채널을 생성합니다.
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH // 중요도를 IMPORTANCE_MAX로 변경하여 전체 화면 알림으로 설정
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        // 알림 권한이 있는지 확인합니다.
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return notificationManager.importance != NotificationManager.IMPORTANCE_NONE
        }
        return true
    }

}