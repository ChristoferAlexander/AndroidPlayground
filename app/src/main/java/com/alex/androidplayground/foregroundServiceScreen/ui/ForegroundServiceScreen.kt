package com.alex.androidplayground.foregroundServiceScreen.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alex.androidplayground.R
import com.alex.androidplayground.core.ui.ObserveFlowWithLifecycle
import com.alex.androidplayground.core.ui.openAppSettings
import com.alex.androidplayground.core.ui.rememberPermissionsResultLauncher
import com.alex.androidplayground.core.utils.permissions.checkPermissions
import com.alex.androidplayground.foregroundServiceScreen.domain.services.LocationForegroundService
import com.alex.androidplayground.foregroundServiceScreen.ui.state.ForegroundServiceScreenAction
import com.alex.androidplayground.foregroundServiceScreen.ui.state.ForegroundServiceScreenEvent
import com.alex.androidplayground.foregroundServiceScreen.ui.state.ForegroundServiceScreenState
import com.alex.androidplayground.foregroundServiceScreen.ui.state.ForegroundServiceScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ForegroundServiceScreen(viewModel: ForegroundServiceScreenViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    viewModel.events.ObserveFlowWithLifecycle { event ->
        when (event) {
            ForegroundServiceScreenEvent.StartServiceScreen -> scope.launch(Dispatchers.Main) {
                Intent(context, LocationForegroundService::class.java).also {
                    it.action = LocationForegroundService.Actions.START.toString()
                    context.startService(it)
                }
            }

            ForegroundServiceScreenEvent.StopServiceScreen -> scope.launch(Dispatchers.Main) {
                Intent(context, LocationForegroundService::class.java).also {
                    it.action = LocationForegroundService.Actions.STOP.toString()
                    context.startService(it)
                }
            }
        }
    }
    ForegroundServiceLayout(state, viewModel::onAction)
}

@Composable
fun ForegroundServiceLayout(
    state: ForegroundServiceScreenState,
    onAction: (ForegroundServiceScreenAction) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        val launcher = rememberPermissionsResultLauncher(
            onPermissionsGranted = { onAction(ForegroundServiceScreenAction.StartServiceScreen) },
            onPermissionsDenied = { shouldShowRationale -> onAction(ForegroundServiceScreenAction.ShowPermissionsRationale) }
        )
        if (state.showPermissionsRationale) {
            LocationPermissionsRationale {
                onAction(ForegroundServiceScreenAction.HidePermissionsRationale)
            }
        }
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    checkPermissions(
                        context = context,
                        permissions = arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.FOREGROUND_SERVICE_LOCATION
                        ),
                        onPermissionsGranted = {
                            if (state.isRunning) onAction(ForegroundServiceScreenAction.StopServiceScreen)
                            else onAction(ForegroundServiceScreenAction.StartServiceScreen)
                        },
                        onPermissionsNotGranted = { shouldShowRationale ->
                            if (shouldShowRationale) {
                                onAction(ForegroundServiceScreenAction.ShowPermissionsRationale)
                            } else {
                                launcher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                            }
                        }
                    )
                } else {
                    if (state.isRunning) onAction(ForegroundServiceScreenAction.StopServiceScreen)
                    else onAction(ForegroundServiceScreenAction.StartServiceScreen)
                }
            }
        ) {
            Text(if (state.isRunning) "Stop foreground" else "Start foreground")
        }
    }
}

@Composable
fun LocationPermissionsRationale(onDismiss: () -> Unit = {}) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onDismiss()
                context.openAppSettings()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        title = {
            Text(stringResource(R.string.location_permissions))
        },
        text = {
            Text(stringResource(R.string.this_app_needs_access_to_location_permissions))
        }
    )
}