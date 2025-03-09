package com.alex.androidplayground.foregroundServiceScreen.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.checkSelfPermission
import com.alex.androidplayground.foregroundServiceScreen.domain.services.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ForegroundServiceScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                scope.launch(Dispatchers.Main) {
                    Intent(context, LocationService::class.java).also {
                        it.action = LocationService.Actions.START.toString()
                        context.startService(it)
                    }
                }
            } else {
                // TODO
            }
        }
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionCheckResult = checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        Intent(context, LocationService::class.java).also {
                            it.action = LocationService.Actions.START.toString()
                            context.startService(it)
                        }
                    } else {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        ) {
            Text("Start foreground")
        }
    }
}