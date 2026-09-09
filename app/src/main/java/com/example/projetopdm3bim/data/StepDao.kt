package com.example.projetopdm3bim.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {
    @Insert
    suspend fun insert(record: StepRecord)

    @Query("SELECT * FROM steps ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestRecord(): StepRecord?

    @Query("SELECT SUM(stepsTaken) FROM steps WHERE timestamp >= :startTime")
    suspend fun getStepsSince(startTime: Long): Int?

    @Query("SELECT * FROM steps WHERE timestamp >= :startTime ORDER BY timestamp ASC")
    fun getRecordsSince(startTime: Long): Flow<List<StepRecord>>

    @Query("SELECT strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch', 'localtime') as day, SUM(stepsTaken) as totalSteps FROM steps WHERE timestamp >= :startTime GROUP BY day ORDER BY day ASC")
    fun getStepsGroupedByDay(startTime: Long): Flow<List<DayStepCount>>
}

data class DayStepCount(
    val day: String,
    val totalSteps: Int
)
