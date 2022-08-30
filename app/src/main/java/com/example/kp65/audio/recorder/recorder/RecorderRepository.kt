package com.example.kp65.audio.recorder.recorder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.IOException
import java.util.*

class RecorderRepository {


    companion object {
        @Volatile
        private var instance: RecorderRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RecorderRepository().also { instance = it }
            }
    }

    //objects
    private var mediaRecorder: MediaRecorder? = null

    //variables
    private lateinit var outputFile: String
    private var recordingTime: Long = 0
    private var timer = Timer()
    private val recordingTimeString = MutableLiveData<String>()
    private val isAudioPlaying = MutableLiveData<Boolean>()

    init{
        isAudioPlaying.postValue(false)
    }
    fun initDir(dir: File,fileName:String?=null) {
        val fName = "/${(fileName ?: "temp")}.mp3"
        outputFile = dir.path + fName
    }

    @SuppressLint("RestrictedApi")
    fun startRecording() {

        mediaRecorder = MediaRecorder()

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(outputFile)

        try {
            startTimer()
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    fun pauseRecording() {
        stopTimer()
        mediaRecorder?.pause()
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    fun resumeRecording() {
        timer = Timer()
        startTimer()
        mediaRecorder?.resume()
    }


    @SuppressLint("RestrictedApi")
    fun stopRecording() {
        mediaRecorder?.let {
            resetRecording()
        }

        playAudio()
    }

    private fun playAudio() {
        val mp = MediaPlayer()

        try {
            isAudioPlaying.postValue(true)
            mp.setDataSource(outputFile)
            mp.prepare()
            mp.start()

            mp.setOnCompletionListener { isAudioPlaying.postValue(false) }
        } catch (e: Exception) {
            isAudioPlaying.postValue(false)
            e.printStackTrace()
        }
    }


    fun resetRecording() {
        resetTimer()
        mediaRecorder?.stop()
        mediaRecorder?.reset()

        mediaRecorder = null
    }

    private fun startTimer() {
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                recordingTime += 1
                updateDisplay()
            }
        }, 1000, 1000)
    }

    private fun stopTimer() {
        timer.cancel()
    }


    private fun resetTimer() {
        stopTimer()
        recordingTime = 0
        recordingTimeString.postValue("00:00")
    }

    private fun updateDisplay() {
        val minutes = recordingTime / (60)
        val seconds = recordingTime % 60
        val str = String.format("%d:%02d", minutes, seconds)
        recordingTimeString.postValue(str)
    }

    fun getRecordingTime() = recordingTimeString
    fun isAudioPlaying() = isAudioPlaying

    fun getFilePath() = outputFile
}