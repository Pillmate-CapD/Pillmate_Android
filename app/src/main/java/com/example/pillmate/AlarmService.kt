package com.example.pillmate

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlarmService : Service() {
    private val CHANNEL_ID = "AlarmChannel"
    private val NOTIFICATION_ID = 101
    private lateinit var ringtone: Ringtone

    private lateinit var mediaPlayer: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmService", "onStartCommand called")

        // Show notification immediately
        val notification = createNotification(intent)
        startForeground(NOTIFICATION_ID, notification)

        // Start the alarm sound and vibration
        startAlarm(intent)

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
        val pillName = intent?.getStringExtra("pill_name") ?: "Unknown"
        val pillTime = intent?.getStringExtra("pill_time") ?: "Unknown"
        val pillId = intent?.getIntExtra("pill_id",-1)
        val alarmId = intent?.getIntExtra("alarm_id",-1)
        Log.d("pillId_AlarmService", "pillId: ${pillId}")

        val activityIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pill_name", pillName)
            putExtra("pill_time",pillTime)
            putExtra("pill_id",pillId)
            putExtra("alarm_id",alarmId)
        }
        startActivity(activityIntent)
    }


    private fun createNotification(intent: Intent?): Notification {
        Log.d("AlarmService", "createNotification called")
        val pillName = intent?.getStringExtra("pill_name") ?: "Unknown"
        val pillTime = intent?.getStringExtra("pill_time") ?: "Unknown"
        val pillId = intent?.getIntExtra("pill_id",-1)
        val alarmId = intent?.getIntExtra("alarm_id",-1)
        Log.d("pillId_AlarmService2", "pillId: ${pillId}")

        val activityIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pill_name", pillName)
            putExtra("pill_time",pillTime)
            putExtra("pill_id",pillId)
            putExtra("alarm_id",alarmId)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_MUTABLE)
        val fullscreenPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        createNotificationChannel()

        val alarmSound: Uri = Uri.parse("android.resource://${packageName}/raw/alarm1")
        Log.d("AlarmService", "Creating notification with custom sound: $alarmSound")

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

    private fun startAlarm(intent: Intent?) {
        // Intent에서 sound 정보를 가져와서 사운드를 결정
        val soundType = intent?.getStringExtra("sound") ?: "alarm1"
        val pillId = intent?.getIntExtra("pill_id",-1)
        Log.d("pillId_AlarmService3", "pillId: ${pillId}")

        val alarmSound: Uri = Uri.parse("android.resource://${packageName}/raw/$soundType")

        mediaPlayer = MediaPlayer.create(this, alarmSound).apply {
            isLooping = true // 반복 재생 설정
            start()
        }
    }

    private fun stopAlarm() {
        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release() // MediaPlayer 해제
        }
    }

    private fun fetchMediInfo(pillName: String) {
        val service = RetrofitApi.getRetrofitService // Retrofit 인스턴스 가져오기

        // "명칭" 값을 MediInfoRequest 객체로 변환
        val request = MediInfoRequest(pillName)

        // 서버로 MediInfoRequest 리스트를 POST 요청으로 전송
        val call = service.postMediInfo(listOf(request)) // 단일 요청 리스트로 전송
        call.enqueue(object : Callback<List<MediInfoResponse>> {
            override fun onResponse(call: Call<List<MediInfoResponse>>, response: Response<List<MediInfoResponse>>) {
                if (response.isSuccessful) {
                    val mediInfoList = response.body()
                    mediInfoList?.forEach { mediInfo ->
                        Log.d("fetchMediInfo", "약물 정보 수신 성공: ${mediInfo.name}, ${mediInfo.photo}, ${mediInfo.category}")

                        // 받은 약물 정보를 처리 (필요한 곳에 표시하거나 저장)
                        val updatedData = mapOf(
                            "명칭" to mediInfo.name,
                            "photo" to (mediInfo.photo ?: "이미지 없음"),
                            "category" to (mediInfo.category ?: "카테고리 없음")
                        )

                        // 업데이트된 데이터를 로그로 출력
                        Log.d("UpdatedData", updatedData.toString())

                        // 데이터를 사용할 Activity로 전달하고자 하는 경우
                        val intent = Intent(this@AlarmService, PreMediActivity::class.java).apply {
                            putExtra("updatedData", ArrayList(listOf(updatedData))) // ArrayList로 변환하여 전달
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
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