package com.example.pillmate

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Calendar

class PillViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesHelper = PreferencesHelper(application)
    private val handler = Handler(Looper.getMainLooper())

    private val _pillItems = MutableLiveData<List<PillListItem>>()
    val pillItems: LiveData<List<PillListItem>>
        get() = _pillItems

    init {
        startMidnightResetTimer()
    }

    fun loadPillItems(items: List<PillListItem>) {
        items.forEachIndexed { index, item ->
            item.isCompleted = preferencesHelper.isPillCompleted(index)
        }
        _pillItems.value = items
    }

    fun setPillCompleted(position: Int, isCompleted: Boolean) {
        preferencesHelper.setPillCompleted(position, isCompleted)
        _pillItems.value?.get(position)?.isCompleted = isCompleted
        _pillItems.value = _pillItems.value // To trigger LiveData update
    }

    // 00시가 되면 이전에 완료시켰던 내용을 초기화 하도록
    private fun startMidnightResetTimer() {
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val delay = midnight.timeInMillis - now.timeInMillis

        handler.postDelayed({
            resetPillCompletionStatus()
            startMidnightResetTimer() // Schedule next reset
        }, delay)
    }

    private fun resetPillCompletionStatus() {
        preferencesHelper.clearAllPillCompletion()
        _pillItems.value?.forEach { it.isCompleted = false }
        _pillItems.value = _pillItems.value // To trigger LiveData update
    }
}