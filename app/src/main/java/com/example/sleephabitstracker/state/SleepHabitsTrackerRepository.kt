package com.example.sleephabitstracker.state

import com.example.sleephabitstracker.state.sleepevent.SleepEvent
import com.example.sleephabitstracker.state.sleepevent.SleepEventDAO
import kotlinx.coroutines.flow.Flow

/**
 * Zapewnia abstrakcje do obslugi kilku zrodel danych
 */
class SleepHabitsTrackerRepository(
    private val sleepEventDAO: SleepEventDAO
) {

    val getAllSleepEvents: Flow<List<SleepEvent>> = sleepEventDAO.getAll()

    suspend fun insertSleepEvents(sleepEvents: List<SleepEvent>) {
        sleepEventDAO.insertList(sleepEvents)
    }

}