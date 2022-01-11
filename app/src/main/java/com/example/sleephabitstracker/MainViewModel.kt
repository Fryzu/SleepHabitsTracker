package com.example.sleephabitstracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.sleephabitstracker.state.SleepHabitsTrackerDatabase
import com.example.sleephabitstracker.state.SleepHabitsTrackerRepository
import com.example.sleephabitstracker.state.datastore.SleepEventsSubscriptionStatus
import com.example.sleephabitstracker.state.sleepevent.SleepEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Zapewnie dostep applikacji dostep do danych oraz persystencje je podczas jej dzialania
 */
class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository: SleepHabitsTrackerRepository

    private val getAllSleepEvents: LiveData<List<SleepEvent>>
    private val subscribedToSleepEvents: LiveData<Boolean>

    init {
        val sleepEventDAO = SleepHabitsTrackerDatabase.getInstance(application).sleepEventDAO()
        val sleepEventsSubscriptionStatus = SleepEventsSubscriptionStatus(application)
        repository = SleepHabitsTrackerRepository(sleepEventDAO, sleepEventsSubscriptionStatus)
        getAllSleepEvents = repository.getAllSleepEvents.asLiveData()
        subscribedToSleepEvents = repository.subscribedToSleepEvents.asLiveData()
    }

    fun updateSubscribedToSleepEvents(subscribedToSleep: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSubscribedToSleepEvents(subscribedToSleep)
        }
    }
}