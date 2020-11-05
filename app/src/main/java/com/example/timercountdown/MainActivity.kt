package com.example.timercountdown

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var countDownTimer2: CountDownTimer2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        countDownTimer2 = CountDownTimer2(
            60000L,
            1000,
            CountDownTimer2.TimerPreferencesImpl(application)) { s, b ->
            findViewById<TextView>(R.id.tvRemaining).text = s
            Log.e("MainActivity","time remaining: $s finished: $b")
        }

        countDownTimer2.startCountDown()

    }

    override fun onResume() {
        super.onResume()
        countDownTimer2.saveOnFinish()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}