package com.example.pillmate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _medicineList = MutableLiveData<List<MediListResponse>>()
    val medicineList: LiveData<List<MediListResponse>> get() = _medicineList

    fun setMedicineList(newList: List<MediListResponse>) {
        _medicineList.value = newList
    }

    // 약물을 삭제하는 함수
    fun deleteMedicine(id: Int) {
        val currentList = _medicineList.value?.toMutableList() ?: mutableListOf()
        val updatedList = currentList.filterNot { it.id == id }
        _medicineList.value = updatedList // 업데이트된 리스트로 변경
    }

    // 약물을 추가하는 함수 등 추가 가능
    fun addMedicine(medicine: MediListResponse) {
        val currentList = _medicineList.value?.toMutableList() ?: mutableListOf()
        currentList.add(medicine)
        _medicineList.value = currentList
    }
}