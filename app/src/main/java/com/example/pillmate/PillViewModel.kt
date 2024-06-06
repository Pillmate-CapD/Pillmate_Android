package com.example.pillmate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PillViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesHelper = PreferencesHelper(application)

    private val _pillItems = MutableLiveData<List<PillListItem>>()
    val pillItems: LiveData<List<PillListItem>>
        get() = _pillItems

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
}