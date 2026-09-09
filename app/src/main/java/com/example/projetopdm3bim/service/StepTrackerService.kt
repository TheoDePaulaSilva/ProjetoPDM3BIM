package com.example.projetopdm3bim.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import com.example.projetopdm3bim.data.AppDatabase
import com.example.projetopdm3bim.data.StepRepository
import kotlinx.coroutines.*

class StepTrackerService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    
    private var initialSteps = -1
    private var currentSteps = 0
    private var lastSavedSteps = 0

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var repository: StepRepository

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val database = AppDatabase.getDatabase(this)
        repository = StepRepository(database.stepDao())

        registerSensors()
    }

    private fun registerSensors() {
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            if (initialSteps == -1) {
                initialSteps = totalSteps
            }
            val stepsSinceStart = totalSteps - initialSteps
            currentSteps = stepsSinceStart
            
            saveStepsIfNeeded()
        }
    }

    private fun saveStepsIfNeeded() {
        val diff = currentSteps - lastSavedSteps
        if (diff >= 10) {
            serviceScope.launch {
                repository.addSteps(diff)
                lastSavedSteps = currentSteps
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
    }
}
