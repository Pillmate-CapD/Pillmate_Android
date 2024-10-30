package com.example.pillmate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_RESTART_SERVICE = "Restart"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pillName = intent.getStringExtra("pill_name")
        Log.d("AlarmReceiver", "Alarm received for pill: $pillName")

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("pill_name", pillName)
        }
        context.startForegroundService(serviceIntent)
    }
}