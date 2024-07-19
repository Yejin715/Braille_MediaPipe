package com.google.mediapipe.examples.handlandmarker.ble


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class BleViewModelFactory(private val repository: BleRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BleViewModel::class.java)) {
            return BleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}