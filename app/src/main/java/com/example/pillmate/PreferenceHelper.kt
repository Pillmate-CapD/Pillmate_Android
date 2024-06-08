package com.example.pillmate

import android.content.Context
import android.content.SharedPreferences


class PreferencesHelper(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("pill_prefs", Context.MODE_PRIVATE)

    fun isPillCompleted(index: Int): Boolean {
        return sharedPreferences.getBoolean("pill_completed_$index", false)
    }

    fun setPillCompleted(index: Int, completed: Boolean) {
        sharedPreferences.edit().putBoolean("pill_completed_$index", completed).apply()
    }

    // 상태 초기화 함수 => API 연결하고 나면 없어질 듯
    fun clearAllPillCompletion() {
        sharedPreferences.edit().clear().apply()
    }
}