    package com.alex.androidplayground.weatherScreen.ui

    import android.Manifest
    import android.widget.Toast
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.BoxScope
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.text.KeyboardActions
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.material3.AlertDialog
    import androidx.compose.material3.Button
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.Switch
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.derivedStateOf
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.focus.FocusDirection
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.platform.LocalFocusManager
    import androidx.compose.ui.platform.testTag
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.text.input.ImeAction
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.compose.collectAsStateWithLifecycle
    import com.alex.androidplayground.R
    import com.alex.androidplayground.core.ui.ObserveFlowWithLifecycle
    import com.alex.androidplayground.core.ui.openAppSettings
    import com.alex.androidplayground.core.ui.rememberPermissionsResultLauncher
    import com.alex.androidplayground.core.utils.permissions.checkPermissions
    import com.alex.androidplayground.weatherScreen.domain.model.CurrentWeather
    import com.alex.androidplayground.weatherScreen.domain.model.DailyWeather
    import com.alex.androidplayground.weatherScreen.ui.state.WeatherScreenEvent
    import com.alex.androidplayground.weatherScreen.ui.state.WeatherScreenAction
    import com.alex.androidplayground.weatherScreen.ui.state.WeatherScreenState
    import com.alex.androidplayground.weatherScreen.ui.state.WeatherViewModel
    import kotlinx.collections.immutable.ImmutableList
    import kotlinx.collections.immutable.toImmutableList

    @Composable
    fun WeatherScreen(viewModel: WeatherViewModel) {
        val context = LocalContext.current
        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.events.ObserveFlowWithLifecycle { event ->
            when (event) {
                is WeatherScreenEvent.DisplayErrorToast ->
                    Toast.makeText(context, event.error, Toast.LENGTH_LONG).show()

                WeatherScreenEvent.LaunchAppSettings ->
                    TODO("Handle app settings launch")
            }
        }

        Box(modifier = Modifier.testTag("WeatherScreen_Container")) {
            WeatherLayout(
                state = state,
                onAction = viewModel::onAction
            )
        }
    }

    @Composable
    fun BoxScope.WeatherLayout(
        state: WeatherScreenState,
        onAction: (WeatherScreenAction) -> Unit
    ) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current

        if (state.showPermissionsRationale) {
            LocationPermissionsRationale {
                onAction(WeatherScreenAction.HidePermissionsRationale)
            }
        }

        val permissionsLauncher = rememberPermissionsResultLauncher(
            onPermissionsGranted = { onAction(WeatherScreenAction.ToggleAutoLocation) },
            onPermissionsDenied = { shouldShowRationale ->
                onAction(WeatherScreenAction.ShowPermissionsRationale)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("WeatherLayout_Column")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("LocationSettings_Row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.current_location),
                    modifier = Modifier.testTag("CurrentLocation_Label")
                )
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = state.autoLocation,
                    onCheckedChange = {
                        checkPermissions(
                            context = context,
                            permissions = arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ),
                            onPermissionsGranted = {
                                onAction(WeatherScreenAction.ToggleAutoLocation)
                            },
                            onPermissionsNotGranted = { shouldShowRationale ->
                                if (shouldShowRationale) {
                                    onAction(WeatherScreenAction.ShowPermissionsRationale)
                                } else {
                                    permissionsLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier.testTag("AutoLocation_Switch")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.lat,
                onValueChange = { onAction(WeatherScreenAction.UpdateLatLong(it, state.long)) },
                label = { Text(stringResource(R.string.enter_latitude)) },
                isError = !state.isLatValid,
                supportingText = {
                    if (!state.isLatValid) {
                        Text(
                            text = stringResource(R.string.latitude_must_be_between_90_and_90),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("Latitude_TextField"),
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.autoLocation,
                singleLine = true
            )

            OutlinedTextField(
                value = state.long,
                onValueChange = {
                    onAction(WeatherScreenAction.UpdateLatLong(state.lat, it))
                },
                label = { Text(stringResource(R.string.enter_longitude)) },
                isError = !state.isLongValid,
                supportingText = {
                    if (!state.isLongValid) {
                        Text(
                            text = stringResource(R.string.longitude_must_be_between_180_and_180),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("Longitude_TextField"),
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onAction(WeatherScreenAction.FetchWeatherScreen)
                        focusManager.clearFocus()
                    }
                ),
                enabled = !state.autoLocation,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            val isFetchButtonEnabled by remember(state.autoLocation, state.isLatValid, state.isLongValid) {
                derivedStateOf { !state.autoLocation && state.isLatValid && state.isLongValid }
            }

            Button(
                onClick = { onAction(WeatherScreenAction.FetchWeatherScreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("FetchWeather_Button"),
                shape = MaterialTheme.shapes.medium,
                enabled = isFetchButtonEnabled
            ) {
                Text(text = stringResource(R.string.fetch_weather))
            }

            Spacer(modifier = Modifier.height(16.dp))

            state.currentWeather?.let {
                CurrentWeatherLayout(it)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.weeklyForecast.isNotEmpty()) {
                WeeklyForecastLayout(state.weeklyForecast.toImmutableList())
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .testTag("Loading_Indicator")
            )
        }
    }

    @Composable
    fun CurrentWeatherLayout(currentWeather: CurrentWeather) {
        Column(modifier = Modifier.testTag("CurrentWeather_Container")) {
            Text(
                text = stringResource(R.string.current_weather),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.testTag("CurrentWeather_Title")
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("CurrentWeather_Card"),
                elevation = CardDefaults.cardElevation()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.temperature_c, currentWeather.temperature))
                    Text(text = stringResource(R.string.wind_speed_km_h, currentWeather.windspeed))
                    Text(text = stringResource(R.string.time, currentWeather.time))
                }
            }
        }
    }

    @Composable
    fun WeeklyForecastLayout(weeklyForecast: ImmutableList<DailyWeather>) {
        Column(modifier = Modifier.testTag("WeeklyForecast_Container")) {
            Text(
                text = stringResource(R.string.weekly_forecast),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.testTag("WeeklyForecast_Title")
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.testTag("WeeklyForecast_List")
            ) {
                items(items = weeklyForecast) { dailyWeather ->
                    WeeklyForecastItem(dailyWeather)
                }
            }
        }
    }

    @Composable
    fun WeeklyForecastItem(dailyWeather: DailyWeather) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("WeeklyForecast_Item"),
            elevation = CardDefaults.cardElevation()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.date, dailyWeather.date))
                Text(text = stringResource(R.string.max_temp_c, dailyWeather.temperatureMax))
                Text(text = stringResource(R.string.min_temp_c, dailyWeather.temperatureMin))
                Text(text = stringResource(R.string.precipitation_mm, dailyWeather.precipitation))
                Text(text = stringResource(R.string.max_wind_speed_km_h, dailyWeather.windSpeedMax))
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

    @Preview(showBackground = true)
    @Composable
    fun Preview1() {
        Box {
            WeatherLayout(
                state = WeatherScreenState(
                    lat = "0",
                    long = "0",
                    currentWeather = null,
                    weeklyForecast = listOf(),
                    autoLocation = false
                ),
                onAction = { }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview2() {
        Box {
            WeatherLayout(
                state = WeatherScreenState(
                    lat = "4.3",
                    long = "2",
                    currentWeather = CurrentWeather(
                        temperature = 25.0,
                        windspeed = 10.0,
                        time = "12:00 PM"
                    ),
                    weeklyForecast = listOf(
                        DailyWeather(
                            date = "Monday",
                            temperatureMax = 28.0,
                            temperatureMin = 18.0,
                            precipitation = 5.0,
                            windSpeedMax = 20.0
                        ),
                        DailyWeather(
                            date = "Tuesday",
                            temperatureMax = 26.0,
                            temperatureMin = 17.0,
                            precipitation = 3.0,
                            windSpeedMax = 15.0
                        )
                    ),
                    autoLocation = false
                ),
                onAction = { }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview3() {
        Box {
            WeatherLayout(
                state = WeatherScreenState(
                    lat = "0",
                    long = "0",
                    currentWeather = null,
                    weeklyForecast = emptyList(),
                    autoLocation = false,
                    showPermissionsRationale = true
                ),
                onAction = { }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview4() {
        Box {
            WeatherLayout(
                state = WeatherScreenState(
                    lat = "5.23",
                    long = "2.566",
                    currentWeather = CurrentWeather(
                        temperature = 25.0,
                        windspeed = 10.0,
                        time = "12:00 PM"
                    ),
                    weeklyForecast = listOf(
                        DailyWeather(
                            date = "Monday",
                            temperatureMax = 28.0,
                            temperatureMin = 18.0,
                            precipitation = 5.0,
                            windSpeedMax = 20.0
                        ),
                        DailyWeather(
                            date = "Tuesday",
                            temperatureMax = 26.0,
                            temperatureMin = 17.0,
                            precipitation = 3.0,
                            windSpeedMax = 15.0
                        )
                    ),
                    autoLocation = true
                ),
                onAction = { }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview5() {
        Box {
            WeatherLayout(
                state = WeatherScreenState(
                    lat = "1000",
                    long = "1000",
                    currentWeather = null,
                    weeklyForecast = emptyList(),
                    autoLocation = false,
                    isLatValid = false,
                    isLongValid = false
                ),
                onAction = { }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview6() {
        Box {
            WeatherLayout(
                state = WeatherScreenState(
                    lat = "2",
                    long = "1",
                    currentWeather = null,
                    weeklyForecast = emptyList(),
                    isLoading = true,
                    autoLocation = false,
                    isLatValid = true,
                    isLongValid = true
                ),
                onAction = { }
            )
        }
    }

