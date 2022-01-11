package com.example.sleephabitstracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.sleephabitstracker.state.SleepHabitsTrackerDatabase
import com.example.sleephabitstracker.state.SleepHabitsTrackerRepository
import com.example.sleephabitstracker.state.sleepevent.SleepEvent

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val getAllSleepEvents: LiveData<List<SleepEvent>>
    private val repository: SleepHabitsTrackerRepository

    init {
        val sleepEventDAO = SleepHabitsTrackerDatabase.getInstance(application).sleepEventDAO()
        repository = SleepHabitsTrackerRepository(sleepEventDAO)
        getAllSleepEvents = repository.getAllSleepEvents.asLiveData()
    }

}