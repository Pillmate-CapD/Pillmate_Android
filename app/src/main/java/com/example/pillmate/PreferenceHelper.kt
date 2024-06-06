package com.example.pillmate

import android.content.Context
import android.content.SharedPreferences


class PreferencesHelper(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("PillPreferences", Context.MODE_PRIVATE)

    fun setPillCompleted(position: Int, isCompleted: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean("pill_completed_$position", isCompleted)
        editor.apply()
    }

    fun isPillCompleted(position: Int): Boolean {
        return preferences.getBoolean("pill_completed_$position", false)
    }
}