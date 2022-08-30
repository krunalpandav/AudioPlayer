package com.example.kp65.audio.recorder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kp65.audio.recorder.databinding.ActivityMainBinding
import com.example.kp65.audio.recorder.recorder.RecorderViewModel
import com.example.kp65.audio.recorder.util.InjectorUtils

class MainActivity : AppCompatActivity(), View.OnClickListener  {
    private var viewModel: RecorderViewModel? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        initClickListener()
    }

    private fun initClickListener() {
        binding.btnBack.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

        binding.btnReset.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
        binding.btnPlay.setOnClickListener(this)
        binding.btnPause.setOnClickListener(this)
        binding.btnResume.setOnClickListener(this)
    }


    private fun initUI() {
        //Get the view model factory
        val factory = InjectorUtils.provideRecorderViewModelFactory()

        //Getting the view model
        viewModel = ViewModelProvider(this,factory)[RecorderViewModel::class.java]

        addObserver()
    }

    private fun addObserver() {
        viewModel?.getRecordingTime()?.observe(this) {
            binding.tvRecordingTime.text = it
        }

        viewModel?.isAudioPlaying()?.observe(this) {
            val isPlaying = it == true

            binding.btnPlay.isEnabled = !isPlaying

            binding.btnPlay.isEnabled = !isPlaying
            binding.btnReset.isEnabled = !isPlaying

            binding.btnStart.isEnabled = !isPlaying
            binding.btnPause.isEnabled = !isPlaying
            binding.btnResume.isEnabled = !isPlaying
        }
    }

    override fun onClick(view: View?) {
        view?.id?.let {
            when(it){
                R.id.btnBack -> { gotoBack() }
                R.id.btnSave -> { saveFile() }
                R.id.btnStart ->  { checkPermission() }
                R.id.btnPause -> { pauseRecording() }
                R.id.btnResume -> { resumeRecording() }
                R.id.btnPlay -> { stopRecording() }
                R.id.btnReset -> { resetRecording() }
            }
        }
    }


    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        } else {
            startRecording()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startRecording() {
        externalCacheDir?.let {
            viewModel?.initDir(it)
            viewModel?.startRecording()

            binding.btnSave.visibility = View.INVISIBLE

            binding.btnPlay.visibility = View.INVISIBLE
            binding.btnReset.visibility = View.INVISIBLE

            binding.btnStart.visibility = View.INVISIBLE
            binding.btnPause.visibility = View.VISIBLE
            binding.btnResume.visibility = View.INVISIBLE
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun pauseRecording() {
        viewModel?.pauseRecording()

        binding.btnReset.visibility = View.VISIBLE
        binding.btnPlay.visibility = View.VISIBLE

        binding.btnStart.visibility = View.INVISIBLE
        binding.btnPause.visibility = View.INVISIBLE
        binding.btnResume.visibility = View.VISIBLE
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun resumeRecording() {
        viewModel?.resumeRecording()

        binding.btnReset.visibility = View.INVISIBLE
        binding.btnPlay.visibility = View.INVISIBLE

        binding.btnStart.visibility = View.INVISIBLE
        binding.btnPause.visibility = View.VISIBLE
        binding.btnResume.visibility = View.INVISIBLE
    }



    @SuppressLint("RestrictedApi")
    private fun stopRecording() {
        viewModel?.stopRecording()

        binding.btnSave.visibility = View.VISIBLE
        binding.btnReset.visibility = View.INVISIBLE

        binding.btnStart.visibility = View.VISIBLE
        binding.btnPause.visibility = View.INVISIBLE
        binding.btnResume.visibility = View.INVISIBLE
    }

    private fun resetRecording() {
        viewModel?.resetRecording()

        binding.btnReset.visibility = View.INVISIBLE
        binding.btnPlay.visibility = View.INVISIBLE

        binding.btnStart.visibility = View.VISIBLE
        binding.btnPause.visibility = View.INVISIBLE
        binding.btnResume.visibility = View.INVISIBLE

    }

    private fun gotoBack(){
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun saveFile(){
        val intent = Intent()
        intent.putExtra("filePath", viewModel?.getFilePath().toString())
        setResult(RESULT_OK, intent)
        finish()
    }
}