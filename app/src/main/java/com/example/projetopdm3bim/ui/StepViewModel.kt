package com.example.projetopdm3bim.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetopdm3bim.data.AppDatabase
import com.example.projetopdm3bim.data.ProfileDataStore
import com.example.projetopdm3bim.data.StepRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.projetopdm3bim.data.DayStepCount
import java.util.Calendar

class StepViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val repository: StepRepository
    private val profileDataStore = ProfileDataStore(application)

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    val stepsToday = MutableStateFlow(0)
    val spm = MutableStateFlow(0f)
    val distance = MutableStateFlow(0f)
    val calories = MutableStateFlow(0f)

    private var detectionTimes = mutableListOf<Long>()

    val height = profileDataStore.heightFlow.stateIn(viewModelScope, SharingStarted.Lazily, 170f)
    val weight = profileDataStore.weightFlow.stateIn(viewModelScope, SharingStarted.Lazily, 70f)
    val stride = profileDataStore.strideFlow.stateIn(viewModelScope, SharingStarted.Lazily, 0.7f)

    val weeklyHistory: StateFlow<List<DayStepCount>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StepRepository(database.stepDao())

        weeklyHistory = repository.getWeeklyHistory().map { records ->
            val historyMap = records.associateBy { it.day }
            val fullWeek = mutableListOf<DayStepCount>()

            for (i in -6..0) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, i)
                val dayKey = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE.format(
                    java.time.LocalDateTime.ofInstant(cal.toInstant(), cal.timeZone.toZoneId())
                )
                val record = historyMap[dayKey] ?: DayStepCount(dayKey, 0)
                fullWeek.add(record)
            }
            fullWeek
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        viewModelScope.launch {
            while (true) {
                stepsToday.value = repository.getTodaySteps()
                calculateMetrics()
                kotlinx.coroutines.delay(5000)
            }
        }
        
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI)
    }

    private fun calculateMetrics() {
        distance.value = stepsToday.value * stride.value / 1000f 
        calories.value = stepsToday.value * 0.04f
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            val now = System.currentTimeMillis()
            detectionTimes.add(now)
            
            detectionTimes.removeAll { it < now - 10000 }
            
            if (detectionTimes.size > 1) {
                spm.value = (detectionTimes.size.toFloat() / 10f) * 60f
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun saveProfile(h: Float, w: Float, s: Float) {
        viewModelScope.launch {
            profileDataStore.saveProfile(h, w, s)
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
