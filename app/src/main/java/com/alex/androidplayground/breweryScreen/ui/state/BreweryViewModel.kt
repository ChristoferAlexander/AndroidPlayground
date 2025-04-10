package com.alex.androidplayground.breweryScreen.ui.state

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.alex.androidplayground.breweryScreen.data.repository.BreweryRepositoryImpl
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryDao
import com.alex.androidplayground.breweryScreen.ui.state.BreweryScreenAction.*
import com.alex.androidplayground.breweryScreen.ui.state.BreweryScreenEvent.*
import com.alex.androidplayground.core.ui.state.BaseViewModel
import com.alex.androidplayground.core.utils.connectivity.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@HiltViewModel
class BreweryViewModel @Inject constructor(
    breweryDao: BreweryDao,
    breweryRepositoryImpl: BreweryRepositoryImpl,
    connectivityObserver: ConnectivityObserver
) : BaseViewModel<BreweryScreenState, BreweryScreenAction, BreweryScreenEvent>(
    initialState = { BreweryScreenState() }
) {

    @OptIn(ExperimentalAtomicApi::class)
    private val wasDisconnected = AtomicBoolean(false)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagingDataFlow = state
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { state ->
            // Pager does not scroll to the top when we refresh the list so we scroll to the top manually
            // Trying to solve in Compose level will result in the list losing the scroll position if we navigate to another screen and back
            sendEvent(ScrollToTop)
            // Delete local data when query changes to make sure pager data clears even if an error happens while fetching new data
            breweryDao.deleteAll()
            breweryRepositoryImpl.getBreweriesPagingData(query = state.query)
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            connectivityObserver.isConnected.collect { isConnected ->
                when (isConnected) {
                    true -> if (wasDisconnected.exchange(false)) sendEvent(OnConnectionRestored)
                    false -> wasDisconnected.store(true)
                }
            }
        }
    }

    override fun onAction(action: BreweryScreenAction) {
        when (action) {
            is UpdateQuery -> setState { it.copy(query = action.query) }
            is BrewerySelected -> viewModelScope.launch {
                sendEvent(OnBrewerySelected(action.id))
            }
        }
    }
}
