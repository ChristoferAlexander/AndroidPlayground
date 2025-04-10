package com.alex.androidplayground.foregroundServiceScreen.ui.state

import com.alex.androidplayground.core.ui.state.Action

sealed class ForegroundServiceScreenAction : Action {
    data object StartServiceScreen : ForegroundServiceScreenAction()
    data object StopServiceScreen : ForegroundServiceScreenAction()
    data object ShowPermissionsRationale : ForegroundServiceScreenAction()
    data object HidePermissionsRationale : ForegroundServiceScreenAction()
}
