package com.example.sleephabitstracker.state.sleepevent

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO – Document Access Object zawiera definicje metod oraz kwerend pozwalających na dostęp do bazy danych
 */
@Dao
interface SleepEventDAO {
    @Query("SELECT * FROM sleep_events ORDER BY start_timestamp DESC")
    fun getAll(): Flow<List<SleepEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(sleepEvent: List<SleepEvent>)

    @Delete
    suspend fun delete(sleepEvent: SleepEvent)

    @Query(value = "DELETE FROM sleep_events")
    suspend fun deleteAll()
}