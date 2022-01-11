package com.example.sleephabitstracker

import android.app.PendingIntent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sleephabitstracker.databinding.ActivityMainBinding
import com.example.sleephabitstracker.receivers.SleepEventsReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sleepPendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sleepPendingIntent = SleepEventsReceiver.createSleepReceiverPendingIntent(applicationContext)
    }
}