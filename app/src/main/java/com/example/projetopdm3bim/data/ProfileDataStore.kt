package com.example.projetopdm3bim.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class ProfileDataStore(private val context: Context) {
    companion object {
        val HEIGHT = floatPreferencesKey("height")
        val WEIGHT = floatPreferencesKey("weight")
        val STRIDE = floatPreferencesKey("stride")
    }

    val heightFlow: Flow<Float> = context.dataStore.data.map { it[HEIGHT] ?: 170f }
    val weightFlow: Flow<Float> = context.dataStore.data.map { it[WEIGHT] ?: 70f }
    val strideFlow: Flow<Float> = context.dataStore.data.map { it[STRIDE] ?: 0.7f }

    suspend fun saveProfile(height: Float, weight: Float, stride: Float) {
        context.dataStore.edit {
            it[HEIGHT] = height
            it[WEIGHT] = weight
            it[STRIDE] = stride
        }
    }
}
