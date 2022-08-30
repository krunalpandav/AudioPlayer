package com.example.kp65.audio.recorder.util

import com.example.kp65.audio.recorder.recorder.RecorderRepository
import com.example.kp65.audio.recorder.recorder.RecorderViewModelProvider

object InjectorUtils {
    fun provideRecorderViewModelFactory(): RecorderViewModelProvider {
        val recorderRepository = RecorderRepository.getInstance()
        return RecorderViewModelProvider(recorderRepository)
    }
}