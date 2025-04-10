package com.alex.androidplayground.breweryScreen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alex.androidplayground.R
import com.alex.androidplayground.breweryScreen.domain.model.Brewery
import com.alex.androidplayground.breweryScreen.ui.state.BreweryDetailsScreenState
import com.alex.androidplayground.breweryScreen.ui.state.BreweryDetailsViewModel

@Composable
fun BreweryDetailsScreen(viewModel: BreweryDetailsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BreweryDetailsLayout(state)
}

@Composable
fun BreweryDetailsLayout(state: BreweryDetailsScreenState) {
    val uriHandler = LocalUriHandler.current
    val notAvailable = stringResource(R.string.n_a)
    val brewery = state.brewery
    if (brewery != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = brewery.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.type, brewery.breweryType),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BreweryDetailItem(
                label = stringResource(R.string.address),
                value = brewery.fullAddress()
            )
            BreweryDetailItem(
                label = stringResource(R.string.phone),
                value = brewery.phone ?: notAvailable
            )
            BreweryDetailItem(
                label = stringResource(R.string.website),
                value = brewery.websiteUrl ?: notAvailable,
                onClick = remember(brewery.websiteUrl) {
                    brewery.websiteUrl?.let { { uriHandler.openUri(it) } }
                }
            )

            val latitude = brewery.latitude
            val longitude = brewery.longitude

            BreweryDetailItem(
                label = stringResource(R.string.coordinates),
                value = remember(latitude, longitude) {
                    if (latitude != null && longitude != null) "$latitude, $longitude"
                    else notAvailable
                },
                onClick = remember(latitude, longitude) {
                    brewery.locationUri()?.let { uri -> { uriHandler.openUri(uri) } }
                }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.brewery_details_not_available),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun BreweryDetailItem(label: String, value: String, onClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = if (onClick != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

private fun Brewery.fullAddress(): String {
    return listOfNotNull(street, address1, address2, address3, city, stateProvince, postalCode, country)
        .joinToString(", ")
}

private fun Brewery.locationUri() = if (latitude != null && longitude != null) {
    "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
} else null
