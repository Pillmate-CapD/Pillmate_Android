package com.example.pillmate

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    private val CHANNEL_ID = "AlarmChannel"
    private val NOTIFICATION_ID = 101
    private lateinit var ringtone: Ringtone

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmService", "onStartCommand called")

        // Show notification immediately
        val notification = createNotification(intent)
        startForeground(NOTIFICATION_ID, notification)

        // Start the alarm sound and vibration
        startAlarm()

        // Start AlarmActivity
        startAlarmActivity(intent)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
        stopForeground(true)
    }

    private fun startAlarmActivity(intent: Intent?) {
        Log.d("AlarmService", "Starting AlarmActivity")
        val activityIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pill_name", intent?.getStringExtra("pill_name"))
        }
        startActivity(activityIntent)
    }

    private fun createNotification(intent: Intent?): Notification {
        Log.d("AlarmService", "createNotification called")
        val pillName = intent?.getStringExtra("pill_name") ?: "Unknown"

        val activityIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pill_name", pillName)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_MUTABLE)
        val fullscreenPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        createNotificationChannel()

        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        Log.d("AlarmService", "Creating notification with sound: $alarmSound")

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("알람")
            .setContentText("약을 복용할 시간입니다: $pillName")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setFullScreenIntent(fullscreenPendingIntent, true)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("AlarmService", "Creating notification channel")
            val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm"
                setSound(alarmSound, attributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("AlarmService", "Notification channel created with sound: $alarmSound")
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            true
        }
    }

    private fun startAlarm() {
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmSound)
        ringtone.play()
    }

    private fun stopAlarm() {
        if (this::ringtone.isInitialized && ringtone.isPlaying) {
            ringtone.stop()
        }
    }
}