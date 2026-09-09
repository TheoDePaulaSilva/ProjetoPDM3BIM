package com.example.projetopdm3bim.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class StepRepository(private val stepDao: StepDao) {

    suspend fun addSteps(steps: Int) {
        val record = StepRecord(
            timestamp = System.currentTimeMillis(),
            stepsTaken = steps
        )
        stepDao.insert(record)
    }

    suspend fun getTodaySteps(): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return stepDao.getStepsSince(calendar.timeInMillis) ?: 0
    }

    fun getWeeklyHistory(): Flow<List<DayStepCount>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return stepDao.getStepsGroupedByDay(calendar.timeInMillis)
    }
}
