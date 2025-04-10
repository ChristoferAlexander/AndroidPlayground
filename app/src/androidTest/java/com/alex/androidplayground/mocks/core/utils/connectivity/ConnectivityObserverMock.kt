package com.alex.androidplayground.mocks.core.utils.connectivity

import com.alex.androidplayground.core.utils.connectivity.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ConnectivityObserverMock: ConnectivityObserver {

    override val isConnected: Flow<Boolean>
        get() = flowOf(true)
    override val isGpsEnabled: Flow<Boolean>
        get() = flowOf(true)
}