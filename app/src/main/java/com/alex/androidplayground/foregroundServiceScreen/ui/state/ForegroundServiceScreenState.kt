package com.alex.androidplayground.foregroundServiceScreen.ui.state

import android.os.Parcelable
import com.alex.androidplayground.core.ui.state.State
import kotlinx.parcelize.Parcelize

@Parcelize
data class ForegroundServiceScreenState(
   val isRunning: Boolean,
   val showPermissionsRationale: Boolean
) : Parcelable, State

