package com.example.sleephabitstracker.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.sleephabitstracker.state.SleepHabitsTrackerDatabase
import com.example.sleephabitstracker.state.SleepHabitsTrackerRepository
import com.example.sleephabitstracker.state.datastore.SleepEventsSubscriptionStatus
import com.example.sleephabitstracker.state.sleepevent.SleepEvent
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private const val TAG = "SleepEventsReceiver"

/**
 * Nasłuchuje na zdarzenia wykrycia snu i zapisuje je w bazie danych
 */
class SleepEventsReceiver : BroadcastReceiver() {

    private val scope: CoroutineScope = MainScope()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive(): $intent")

        val sleepEventDAO = SleepHabitsTrackerDatabase.getInstance(context).sleepEventDAO()
        val sleepEventsSubscriptionStatus = SleepEventsSubscriptionStatus(context)
        val repository = SleepHabitsTrackerRepository(sleepEventDAO, sleepEventsSubscriptionStatus)


        if (SleepSegmentEvent.hasEvents(intent)) {
            val sleepSegmentEvents: List<SleepSegmentEvent> =
                SleepSegmentEvent.extractEvents(intent)
            Log.d(TAG, "SleepSegmentEvent List: $sleepSegmentEvents")
            addSleepEventsToDatabase(repository, sleepSegmentEvents)
        }
    }

    private fun addSleepEventsToDatabase(
        repository: SleepHabitsTrackerRepository,
        sleepSegmentEvents: List<SleepSegmentEvent>
    ) {
        if (sleepSegmentEvents.isNotEmpty()) {
            scope.launch {
                val convertedToEntityVersion: List<SleepEvent> =
                    sleepSegmentEvents.map {
                        SleepEvent.fromLocationEvent(it)
                    }
                repository.insertSleepEvents(convertedToEntityVersion)
            }
        }
    }

    companion object {
        fun createSleepReceiverPendingIntent(context: Context): PendingIntent {
            val sleepIntent = Intent(context, SleepEventsReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                0,
                sleepIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }
}