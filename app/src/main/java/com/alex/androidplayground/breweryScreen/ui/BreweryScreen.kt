package com.alex.androidplayground.breweryScreen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.alex.androidplayground.R
import com.alex.androidplayground.breweryScreen.domain.model.Brewery
import com.alex.androidplayground.breweryScreen.domain.model.BreweryType
import com.alex.androidplayground.breweryScreen.ui.state.BreweryScreenAction
import com.alex.androidplayground.breweryScreen.ui.state.BreweryScreenEvent
import com.alex.androidplayground.breweryScreen.ui.state.BreweryScreenState
import com.alex.androidplayground.breweryScreen.ui.state.BreweryViewModel
import com.alex.androidplayground.core.ui.ObserveFlowWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun BreweryScreen(
    viewModel: BreweryViewModel,
    onBrewerySelected: (id: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pages = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    viewModel.events.ObserveFlowWithLifecycle { event ->
        when (event) {
            is BreweryScreenEvent.OnBrewerySelected -> onBrewerySelected(event.breweryId)
            BreweryScreenEvent.OnConnectionRestored -> pages.retry()
            is BreweryScreenEvent.ScrollToTop -> scope.launch {
                listState.scrollToItem(0)
            }
        }
    }
    BreweryLayout(
        state = state,
        pages = pages,
        listState = listState,
        onAction = viewModel::onAction
    )
}

@Composable
fun BreweryLayout(
    state: BreweryScreenState,
    pages: LazyPagingItems<Brewery>,
    listState: LazyListState,
    onAction: (action: BreweryScreenAction) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = state.query,
            onValueChange = { onAction(BreweryScreenAction.UpdateQuery(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { stringResource(R.string.search_breweries) },
            placeholder = { stringResource(R.string.search_breweries) },
            singleLine = true
        )
        BreweryList(
            pages = pages,
            listState = listState,
            onAction = onAction
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BreweryList(
    pages: LazyPagingItems<Brewery>,
    listState: LazyListState = rememberLazyListState(),
    onAction: (action: BreweryScreenAction) -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = pages.loadState.refresh == LoadState.Loading,
        onRefresh = { pages.refresh() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pages.itemCount) { index ->
                pages[index]?.let { brewery ->
                    BreweryItem(
                        brewery = brewery, onAction = onAction
                    )
                }
            }
            pages.apply {
                when {
                    loadState.append is LoadState.Loading -> item { Loading() }
                    loadState.refresh is LoadState.Error -> item { ErrorMessage(stringResource(R.string.failed_to_load_breweries)) }
                }
            }
        }
    }
}

@Composable
fun BreweryItem(
    brewery: Brewery, onAction: (action: BreweryScreenAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onAction(BreweryScreenAction.BrewerySelected(brewery.id)) },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = brewery.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row {
                listOfNotNull(brewery.country, brewery.city)
                    .takeIf { it.isNotEmpty() }
                    ?.joinToString(", ")
                    ?.let { location ->
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
            }
            Text(text = "Type: ${brewery.breweryType.type}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Loading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Red)
    }
}

@Preview(showBackground = true)
@Composable
fun BreweryLayoutPreview() {
    val fakeBreweries = List(10) { index ->
        Brewery(
            id = "$index", name = "Brewery $index", city = "City $index", breweryType = BreweryType.Micro
        )
    }
    val pagingData = PagingData.from(fakeBreweries)
    val fakeDataFlow = MutableStateFlow(pagingData)
    val fakeState = BreweryScreenState()
    Column {
        BreweryLayout(state = fakeState, onAction = {}, pages = fakeDataFlow.collectAsLazyPagingItems(), listState = rememberLazyListState())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBreweryItem() {
    BreweryItem(
        brewery = Brewery(
            id = "1", name = "Sample Brewery", city = "New York", breweryType = BreweryType.Micro
        )
    ) { }
}