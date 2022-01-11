package com.example.sleephabitstracker.state

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sleephabitstracker.state.sleepevent.SleepEvent
import com.example.sleephabitstracker.state.sleepevent.SleepEventDAO

/**
 * Przechowuje baze danych i sluzy jako jej glowny element dostepowy
 */
@Database(
    entities = [SleepEvent::class],
    version = 3,
    exportSchema = false
)
abstract class SleepHabitsTrackerDatabase: RoomDatabase() {

    abstract fun sleepEventDAO(): SleepEventDAO

    /**
     * Implementuje wzorzec singletona do inicializacji bazy danych
     */
    companion object {
        @Volatile
        private var INSTANCE: SleepHabitsTrackerDatabase? = null

        fun getInstance(context: Context): SleepHabitsTrackerDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    SleepHabitsTrackerDatabase::class.java,
                    "sleep_habits_tracker_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}