package com.example.kp65.audio.recorder.recorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RecorderViewModelProvider(private val recorderRepository: RecorderRepository): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecorderViewModel(recorderRepository) as T
    }
}