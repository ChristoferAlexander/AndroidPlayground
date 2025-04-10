package com.alex.androidplayground.foregroundServiceScreen.domain.repository

import kotlinx.coroutines.flow.Flow

interface ForegroundServiceStatusRepository {
    val isRunning: Flow<Boolean>
    suspend fun setRunning(isRunning: Boolean)
}