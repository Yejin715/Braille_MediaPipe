package com.google.mediapipe.examples.handlandmarker.ble

import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BleViewModel(get()) }
}

val repositoryModule = module{
    single{
        BleRepository()
    }
}