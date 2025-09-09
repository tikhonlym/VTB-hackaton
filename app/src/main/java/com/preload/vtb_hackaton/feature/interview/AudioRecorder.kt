package com.preload.vtb_hackaton.feature.interview

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {
    
    companion object {
        private const val TAG = "AudioRecorder"
    }
    
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFile: File? = null
    
    fun startRecording(): Boolean {
        if (!hasPermission()) {
            Log.e(TAG, "No recording permission")
            return false
        }
        
        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            
            outputFile = createOutputFile()
            
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile?.absolutePath)
                
                prepare()
                start()
            }
            
            isRecording = true
            Log.d(TAG, "Recording started: ${outputFile?.absolutePath}")
            return true
            
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start recording: ${e.message}")
            stopRecording()
            return false
        }
    }
    
    fun stopRecording(): String? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            
            val filePath = outputFile?.absolutePath
            Log.d(TAG, "Recording stopped: $filePath")
            filePath
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording: ${e.message}")
            null
        }
    }
    
    fun isRecording(): Boolean = isRecording
    
    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun createOutputFile(): File {
        val audioDir = File(context.getExternalFilesDir(null), "audio")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }
        
        val timestamp = System.currentTimeMillis()
        return File(audioDir, "answer_$timestamp.3gp")
    }
    
    fun cleanup() {
        if (isRecording) {
            stopRecording()
        }
        outputFile?.delete()
    }
}

