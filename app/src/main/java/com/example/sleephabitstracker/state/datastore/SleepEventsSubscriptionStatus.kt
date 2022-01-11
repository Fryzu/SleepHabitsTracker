package com.example.sleephabitstracker.state.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PREFERENCE_NAME = "SLEEP_HABIT_TRACKER_PREFERENCES"

/**
 * Przechowuje informacje o subskrybcji do obserwowania zdarzen snu
 */
class SleepEventsSubscriptionStatus(context: Context) {

    private object PreferencesKeys {
        val SUBSCRIBED_TO_SLEEP_EVENTS = booleanPreferencesKey("subscribed_to_sleep_events")
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    val subscribedToSleepEvents: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SUBSCRIBED_TO_SLEEP_EVENTS] ?: false
    }

    suspend fun updateSubscribedToSleepEvents(subscribedToSleep: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUBSCRIBED_TO_SLEEP_EVENTS] = subscribedToSleep
        }
    }

}