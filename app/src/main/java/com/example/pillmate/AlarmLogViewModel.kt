package com.example.pillmate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

// AlarmLogViewModel.kt
class AlarmLogViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmLogDao = AlarmDatabase.getInstance(application).alarmLogDao()

    // 사용자별 로그 LiveData 가져오기
    fun getLogsForUser(userId: Int): LiveData<List<AlarmLog>> {
        return alarmLogDao.getAllLogsLiveDataForUser(userId)
    }
}
