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
        // Handle the received alarm here

        val inIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pill_name", pillName)
        }
        context.startActivity(inIntent)
    }
}