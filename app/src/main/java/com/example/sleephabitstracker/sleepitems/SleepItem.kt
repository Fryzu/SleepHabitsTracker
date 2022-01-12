package com.example.sleephabitstracker.sleepitems

import java.time.Instant
import java.time.ZoneId

data class SleepItem(val id: Long, val startTimestamp: Long, val endTimestamp: Long) {
    companion object {
        fun getDateStrFromUnix(ms: Long): String {
            var dateTime =  Instant.ofEpochSecond(ms)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            return dateTime.toString()
        }
    }
}