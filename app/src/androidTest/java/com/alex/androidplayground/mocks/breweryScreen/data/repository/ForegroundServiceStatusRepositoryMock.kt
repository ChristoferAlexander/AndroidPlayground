package com.alex.androidplayground.mocks.breweryScreen.data.repository

import com.alex.androidplayground.foregroundServiceScreen.domain.repository.ForegroundServiceStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ForegroundServiceStatusRepositoryMock() : ForegroundServiceStatusRepository {

    private val _isRunning = MutableStateFlow(false)

    override val isRunning: Flow<Boolean> = _isRunning

    override suspend fun setRunning(isRunning: Boolean) {
        _isRunning.value = isRunning
    }
}
