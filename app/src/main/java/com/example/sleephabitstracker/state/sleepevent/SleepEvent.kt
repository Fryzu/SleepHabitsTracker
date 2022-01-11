package com.example.sleephabitstracker.state.sleepevent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.location.SleepSegmentEvent

/**
 * Reprezentuje tabele z eventami snu w naszej bazie danych
 */
@Entity(tableName = "sleep_events")
data class SleepEvent (
    @PrimaryKey
    @ColumnInfo(name="start_timestamp")
    val startTimestamp: Long,

    @ColumnInfo(name="end_timestamp")
    val endTimestamp: Long,

    @ColumnInfo(name="status")
    val status: Int
) {
    companion object {
        fun fromLocationEvent(locationSleepEvent: SleepSegmentEvent): SleepEvent {
            return SleepEvent(
                locationSleepEvent.startTimeMillis,
                locationSleepEvent.endTimeMillis,
                locationSleepEvent.status
            )
        }
    }
}

