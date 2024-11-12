package com.example.pillmate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Intent에서 모든 데이터를 받아 변수로 저장
        val pillName = intent.getStringExtra("pill_name") ?: "Unknown"
        val pillTime = intent.getStringExtra("pill_time") ?: "Unknown"
        val pillId = intent.getIntExtra("pill_id", -1)
        val sound = intent.getStringExtra("sound") ?: "alarm1" // 기본값은 "alarm1"

        // 모든 데이터를 서비스 Intent에 전달
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("pill_name", pillName)
            putExtra("pill_time", pillTime)
            putExtra("pill_id", pillId)
            putExtra("sound", sound)
        }

        // 로그로 확인
        Log.d("Receiver Data", "Pill Name: $pillName, Pill Time: $pillTime, Pill Id: $pillId, Sound: $sound")

        // 서비스 시작
        context.startService(serviceIntent)
    }
}

