package com.alex.androidplayground.foregroundServiceScreen.ui.state

import androidx.lifecycle.viewModelScope
import com.alex.androidplayground.core.ui.state.BaseViewModel
import com.alex.androidplayground.foregroundServiceScreen.domain.repository.ForegroundServiceStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForegroundServiceScreenViewModel @Inject constructor(
    private val statusRepository: ForegroundServiceStatusRepository
) : BaseViewModel<ForegroundServiceScreenState, ForegroundServiceScreenAction, ForegroundServiceScreenEvent>(
    initialState = {
        ForegroundServiceScreenState(
            isRunning = false,
            showPermissionsRationale = false
        )
    }
) {

    init {
        viewModelScope.launch {
            statusRepository.isRunning.collect { isRunning ->
                setState { it.copy(isRunning = isRunning) }
            }
        }
    }

    override fun onAction(action: ForegroundServiceScreenAction) {
        when (action) {
            ForegroundServiceScreenAction.StartServiceScreen -> viewModelScope.launch {
                sendEvent(ForegroundServiceScreenEvent.StartServiceScreen)
            }

            ForegroundServiceScreenAction.StopServiceScreen -> viewModelScope.launch {
                sendEvent(ForegroundServiceScreenEvent.StopServiceScreen)
            }

            ForegroundServiceScreenAction.HidePermissionsRationale -> setState { it.copy(showPermissionsRationale = false) }
            ForegroundServiceScreenAction.ShowPermissionsRationale -> setState { it.copy(showPermissionsRationale = true) }
        }
    }
}