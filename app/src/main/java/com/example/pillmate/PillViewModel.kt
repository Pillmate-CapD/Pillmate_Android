package com.example.pillmate

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class PillViewModel : ViewModel() {
    private val _pillItems = MutableLiveData<List<PillListItem>>()
    val pillItems: LiveData<List<PillListItem>> get() = _pillItems

    private var allPillItems: List<PillListItem> = listOf() // 전체 약 리스트를 저장

    fun loadPillItems(items: List<PillListItem>) {
        allPillItems = items // 전체 약 리스트 저장
        _pillItems.value = items // 처음에는 전체 약 리스트 표시
    }

    // 카테고리 선택 시 해당하는 약만 필터링하여 표시
    fun filterPillItemsByCategory(category: String) {
        _pillItems.value = if (category == "전체") {
            allPillItems // 전체 카테고리 선택 시 전체 리스트 표시
        } else {
            allPillItems.filter { it.category == category } // 선택한 카테고리에 해당하는 약만 필터링
        }
    }
}


//class PillViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val preferencesHelper = PreferencesHelper(application)
//    private val handler = Handler(Looper.getMainLooper())
//
//    private val _pillItems = MutableLiveData<List<PillListItem>>()
//    val pillItems: LiveData<List<PillListItem>>
//        get() = _pillItems
//
//    init {
//        //resetPillCompletionStatus()
//        startMidnightResetTimer()
//    }
//
//    fun loadPillItems(items: List<PillListItem>) {
//        items.forEachIndexed { index, item ->
//            item.isEaten = preferencesHelper.isPillCompleted(index)
//        }
//        _pillItems.value = items
//    }
//
//    fun setPillCompleted(position: Int, isCompleted: Boolean) {
//        preferencesHelper.setPillCompleted(position, isCompleted)
//        _pillItems.value?.get(position)?.isEaten = isCompleted
//        _pillItems.value = _pillItems.value // To trigger LiveData update
//    }
//
//    // 00시가 되면 이전에 완료시켰던 내용을 초기화 하도록
//    private fun startMidnightResetTimer() {
//        val now = Calendar.getInstance()
//        val midnight = Calendar.getInstance().apply {
//            timeInMillis = now.timeInMillis
//            set(Calendar.HOUR_OF_DAY, 0)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            add(Calendar.DAY_OF_MONTH, 1)
//        }
//        val delay = midnight.timeInMillis - now.timeInMillis
//
//        handler.postDelayed({
//            resetPillCompletionStatus()
//            startMidnightResetTimer() // Schedule next reset
//        }, delay)
//    }
//
//    private fun resetPillCompletionStatus() {
//        preferencesHelper.clearAllPillCompletion()
//        _pillItems.value?.forEach { it.isEaten = false }
//        _pillItems.value = _pillItems.value // To trigger LiveData update
//    }
//
//}