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
        // 알람 발생 시 서비스에 사운드 정보와 함께 전달
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("pill_name", intent.getStringExtra("pill_name"))
            putExtra("pill_time",intent.getStringExtra("pill_time"))
            putExtra("pill_id",intent.getIntExtra("pill_id", -1))
            putExtra("sound", intent.getStringExtra("sound") ?: "alarm1") // 기본값은 "alarm1"
        }
        context.startService(serviceIntent)
    }
}
