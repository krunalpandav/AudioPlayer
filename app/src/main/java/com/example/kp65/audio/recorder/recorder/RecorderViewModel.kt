package com.example.kp65.audio.recorder.recorder

import androidx.lifecycle.ViewModel
import java.io.File

class RecorderViewModel(private val recorderRepository: RecorderRepository): ViewModel() {
    fun initDir(dir : File,fileName:String?=null) = recorderRepository.initDir(dir,fileName)
    fun startRecording() = recorderRepository.startRecording()

    fun stopRecording() = recorderRepository.stopRecording()

    fun pauseRecording() = recorderRepository.pauseRecording()

    fun resumeRecording() = recorderRepository.resumeRecording()
    fun resetRecording() = recorderRepository.resetRecording()

    fun getRecordingTime() = recorderRepository.getRecordingTime()

    fun isAudioPlaying() = recorderRepository.isAudioPlaying()

    fun getFilePath() = recorderRepository.getFilePath()

}