package com.preload.vtb_hackaton.feature.interview

import android.os.CountDownTimer
import android.util.Log

class QuestionTimer(
    private val onTick: (Long) -> Unit,
    private val onFinish: () -> Unit
) {
    
    companion object {
        private const val TAG = "QuestionTimer"
        private const val DEFAULT_TIME_MS = 3 * 60 * 1000L // 3 минуты
        private const val EXTRA_TIME_MS = 1 * 60 * 1000L // 1 минута
    }
    
    private var timer: CountDownTimer? = null
    private var timeLeft = DEFAULT_TIME_MS
    private var isRunning = false
    
    fun startTimer() {
        if (isRunning) {
            Log.w(TAG, "Timer is already running")
            return
        }
        
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                this@QuestionTimer.onTick(millisUntilFinished)
            }
            
            override fun onFinish() {
                timeLeft = 0
                isRunning = false
                Log.d(TAG, "Timer finished")
                this@QuestionTimer.onFinish()
            }
        }
        
        timer?.start()
        isRunning = true
        Log.d(TAG, "Timer started with ${timeLeft / 1000} seconds")
    }
    
    fun addExtraTime() {
        if (isRunning) {
            timeLeft += EXTRA_TIME_MS
            Log.d(TAG, "Added 1 minute. Time left: ${timeLeft / 1000} seconds")
        }
    }
    
    fun stopTimer() {
        timer?.cancel()
        timer = null
        isRunning = false
        Log.d(TAG, "Timer stopped")
    }
    
    fun isRunning(): Boolean = isRunning
    
    fun getTimeLeft(): Long = timeLeft
    
    fun getFormattedTime(): String {
        val minutes = timeLeft / (1000 * 60)
        val seconds = (timeLeft % (1000 * 60)) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    fun reset() {
        stopTimer()
        timeLeft = DEFAULT_TIME_MS
    }
}

