package com.example.sleephabitstracker.sleepitems

import android.util.Log
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class SleepItem(val id: Long, val startTimestamp: Long, val endTimestamp: Long) {
    companion object {
        fun getDateObjectFromUnix(ms: Long): LocalDateTime {
            Log.d("SleepItem", ms.toString())
            return Instant.ofEpochSecond(ms / 1000)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }
    }
}