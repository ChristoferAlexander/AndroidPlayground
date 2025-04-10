package com.alex.androidplayground.core.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.ObserveFlowWithLifecycle(onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = this@ObserveFlowWithLifecycle, key2 = lifecycleOwner) {
        this@ObserveFlowWithLifecycle.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { onEvent(it) }
    }
}

@Composable
fun rememberPermissionsResultLauncher(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: (shouldShowRationale: Boolean) -> Unit
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    val activity = LocalActivity.current
    return rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            onPermissionsGranted()
        } else {
            val shouldShowRationale = permissions.any {
                shouldShowRequestPermissionRationale(activity!!, it.key)
            }
            if (shouldShowRationale) {
                onPermissionsDenied(true)
            } else {
                onPermissionsDenied(false)
            }
        }
    }
}

fun Context.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}


