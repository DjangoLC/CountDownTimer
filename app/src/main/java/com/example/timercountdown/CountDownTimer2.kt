package com.example.timercountdown

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import java.util.concurrent.TimeUnit

//TODO this class is in progress
class CountDownTimer2(
    private val timeToCountDown: Long,
    private val countDownInterval: Long,
    private val timerPreferences: TimerPreferences,
    private val listener: (String, Boolean) -> Unit
) : CountDownTimer(timeToCountDown, countDownInterval) {

    private val FORMAT = "%02d:%02d"
    private var timeRemaining = 0L

    init {
        setupTimer()
    }

    override fun onFinish() {
        val result = "00:00"
        timeRemaining = 0L
        timerPreferences.saveBoolean(TimerPreferences.HAS_TIME_REMAINING, false)
        timerPreferences.saveLong(TimerPreferences.TIME_REMAINING, 0L)
        timerPreferences.saveLong(TimerPreferences.TIME_TO_COUNT_DOWN, 0L)
        listener.invoke(result, true)
        Log.e("TAG:COUNTDOWN", "finish: ${getRemainingTime()}")
    }

    override fun onTick(millisUntilFinished: Long) {

        val result = String.format(
            FORMAT,
            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
            ),
            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
            )
        )

        timeRemaining = millisUntilFinished
        listener.invoke(result, false)
    }

    fun startCountDown() {
        this.start()
    }

    private fun hasTimeRemaining(): Boolean {
        return timeRemaining > 0
    }

    fun cancelCountDown() {
        this.cancel()
    }

    fun getRemainingTime(): Long {
        return timeRemaining
    }

    fun setupTimer() {
        timerPreferences.saveLong(TimerPreferences.TIME_TO_COUNT_DOWN, timeToCountDown)
        var countDown =
            if (timerPreferences.getBoolean(TimerPreferences.HAS_TIME_REMAINING)) timerPreferences.getLong(
                TimerPreferences.TIME_REMAINING
            ) else timeToCountDown

        if (timerPreferences.getBoolean(TimerPreferences.HAS_TIME_REMAINING)) {

            val timeInBg = timerPreferences.getLong(TimerPreferences.TIME_TO_COUNT_DOWN)
            val diff = System.currentTimeMillis() - timeInBg

            countDown -= diff
        }
    }

    fun saveOnFinish() {
        if (hasTimeRemaining()) {
            Log.e("TAG:COUNTDOWN", "saving time remaining: ${getRemainingTime()}")
            timerPreferences.saveBoolean(TimerPreferences.HAS_TIME_REMAINING, true)
            timerPreferences.saveLong(TimerPreferences.TIME_REMAINING, getRemainingTime())
        } else {
            Log.e("TAG:COUNTDOWN", "not time to save")
            timerPreferences.saveBoolean(TimerPreferences.HAS_TIME_REMAINING, false)
            timerPreferences.saveLong(TimerPreferences.TIME_REMAINING, 0)
            timerPreferences.saveLong(TimerPreferences.TIME_TO_COUNT_DOWN, 0)
        }
    }

    class TimerPreferencesImpl(context: Application) : TimerPreferences {

        private val preferences =
            context.getSharedPreferences(TimerPreferences.TIMER_PREFERENCES, Context.MODE_PRIVATE)

        override fun getLong(key: String): Long {
            return preferences.getLong(key, -1)
        }

        override fun saveLong(key: String, value: Long) {
            with(preferences.edit()) {
                putLong(key, value)
                commit()
            }
        }

        override fun getBoolean(key: String): Boolean {
            return preferences.getBoolean(key, false)
        }

        override fun saveBoolean(key: String, boolean: Boolean) {
            with(preferences.edit()) {
                putBoolean(key, boolean)
                commit()
            }
        }

    }

    interface TimerPreferences {

        companion object {
            const val TIMER_PREFERENCES =
                "mx.frd.donde.movil.presentation.login.loginback.forgottenpass:timerpreferences"
            const val HAS_TIME_REMAINING = "com.example.data:HAS_TIME_REMAINING"
            const val TIME_REMAINING = "com.example.data:TIME_REMAINING"
            const val TIME_TO_COUNT_DOWN = "com.example.data:TIME_TO_COUNT_DOWN"
        }

        fun getLong(key: String): Long
        fun saveLong(key: String, value: Long)
        fun getBoolean(key: String): Boolean
        fun saveBoolean(key: String, boolean: Boolean)
    }


}