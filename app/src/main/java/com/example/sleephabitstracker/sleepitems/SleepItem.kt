package com.example.sleephabitstracker.sleepitems

import android.util.Log
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class SleepItem(val id: Long, val startTimestamp: Long, val endTimestamp: Long) {
    companion object {
        fun getDateStrFromUnix(ms: Long): String {
            Log.d("SleepItem", ms.toString())
            var dateTime =  Instant.ofEpochSecond(ms / 1000)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return formatter.format(dateTime);
        }
    }
}