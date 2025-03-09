package com.alex.androidplayground.weatherScreen.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alex.androidplayground.core.utils.ui.ObserveEvents
import com.alex.androidplayground.weatherScreen.domain.model.CurrentWeather
import com.alex.androidplayground.weatherScreen.domain.model.DailyWeather
import com.alex.androidplayground.weatherScreen.ui.state.WeatherEvents
import com.alex.androidplayground.weatherScreen.ui.state.WeatherMviAction
import com.alex.androidplayground.weatherScreen.ui.state.WeatherMviState
import com.alex.androidplayground.weatherScreen.ui.state.WeatherViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun WeatherScreen() {
    val viewModel = hiltViewModel<WeatherViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lat by viewModel.lat.collectAsStateWithLifecycle()
    val long by viewModel.long.collectAsStateWithLifecycle()
    WeatherLayout(
        state = state, onAction = viewModel::onAction, events = viewModel.events, lat = lat, long = long
    )
}

@Composable
fun WeatherLayout(
    state: WeatherMviState, onAction: (WeatherMviAction) -> Unit, events: Flow<WeatherEvents>, lat: String, long: String
) {
    val context = LocalContext.current
    ObserveEvents(events) { event ->
        when (event) {
            is WeatherEvents.DisplayErrorToast -> Toast.makeText(context, event.error, Toast.LENGTH_LONG).show()
        }
    }
    when (state) {
        is WeatherMviState.Loading -> LoadingView()
        is WeatherMviState.Weather -> WeatherView(
            state = state, onAction = onAction, lat = lat, long = long
        )

        is WeatherMviState.Error -> ErrorView(onAction)
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()
    }
}

@Composable
fun WeatherView(
    state: WeatherMviState.Weather, onAction: (WeatherMviAction) -> Unit, lat: String, long: String
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(value = lat,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) onAction(WeatherMviAction.UpdateLatLong(it, long)) },
            label = { Text("Enter Latitude") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = long,
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull() != null)
                    onAction(WeatherMviAction.UpdateLatLong(lat, it))
            },
            label = { Text("Enter Longitude") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                if (lat.isNotBlank() && long.isNotBlank()) {
                    onAction(WeatherMviAction.FetchWeather)
                } else {
                    onAction(WeatherMviAction.InvalidLatLong)
                }
                focusManager.clearFocus()
            })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (lat.isNotBlank() && long.isNotBlank()) {
                    onAction(WeatherMviAction.FetchWeather)
                } else {
                    onAction(WeatherMviAction.InvalidLatLong)
                }
            }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Fetch Weather")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Current Weather", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        state.currentWeather?.let { CurrentWeatherCard(it) }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Weekly Forecast", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        WeeklyForecastList(state.weeklyForecast)
    }
}

@Composable
fun CurrentWeatherCard(currentWeather: CurrentWeather) {
    Card(
        modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Temperature: ${currentWeather.temperature}°C")
            Text(text = "Wind Speed: ${currentWeather.windspeed} km/h")
            Text(text = "Time: ${currentWeather.time}")
        }
    }
}

@Composable
fun WeeklyForecastList(weeklyForecast: List<DailyWeather>) {
    LazyColumn {
        items(weeklyForecast) { dailyWeather ->
            WeeklyForecastItem(dailyWeather)
        }
    }
}

@Composable
fun WeeklyForecastItem(dailyWeather: DailyWeather) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Date: ${dailyWeather.date}")
            Text(text = "Max Temp: ${dailyWeather.temperatureMax}°C")
            Text(text = "Min Temp: ${dailyWeather.temperatureMin}°C")
            Text(text = "Precipitation: ${dailyWeather.precipitation} mm")
            Text(text = "Max Wind Speed: ${dailyWeather.windSpeedMax} km/h")
        }
    }
}

@Composable
fun ErrorView(onAction: (WeatherMviAction) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "An error occurred", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onAction(WeatherMviAction.RetryFetchData) }) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingView() {
    LoadingView()
}

@Preview(showBackground = true)
@Composable
fun PreviewSuccessView() {
    WeatherView(
        state = WeatherMviState.Weather(
            currentWeather = CurrentWeather(temperature = 25.0, windspeed = 10.0, time = "12:00 PM"), weeklyForecast = listOf(
                DailyWeather("Monday", 28.0, 18.0, 5.0, 20.0), DailyWeather("Tuesday", 26.0, 17.0, 3.0, 15.0)
            )
        ), onAction = { }, lat = "0", long = "0"
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorView() {
    ErrorView(onAction = {})
}
