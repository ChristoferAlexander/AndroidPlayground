package com.alex.androidplayground.core.utils.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun checkPermissions(
    context: Context,
    permissions: Array<String>,
    onPermissionsGranted: () -> Unit,
    onPermissionsNotGranted: (shouldShowRationale: Boolean) -> Unit
) {
    val allPermissionsAlreadyGranted = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    if (allPermissionsAlreadyGranted) {
        onPermissionsGranted()
    } else {
        val shouldShowRationale = permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(context as android.app.Activity, it)
        }
        if (shouldShowRationale) {
            onPermissionsNotGranted(true)
        } else {
            onPermissionsNotGranted(false)
        }
    }
}


fun Context.hasLocationPermission() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED
