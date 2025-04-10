package com.alex.androidplayground.breweryScreen.ui.state

import androidx.lifecycle.viewModelScope
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryDao
import com.alex.androidplayground.breweryScreen.data.source.entity.toBrewery
import com.alex.androidplayground.core.ui.state.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@HiltViewModel(assistedFactory = BreweryDetailsViewModel.Factory::class)
class BreweryDetailsViewModel @AssistedInject constructor(
    @Assisted breweryId: String,
    breweryDao: BreweryDao,
) : BaseViewModel<BreweryDetailsScreenState, Nothing, Nothing>(
    initialState = { BreweryDetailsScreenState() }
) {

    @AssistedFactory
    interface Factory {
        fun create(breweryId: String): BreweryDetailsViewModel
    }

    init {
        viewModelScope.launch {
            val brewery = breweryDao.getById(breweryId)?.toBrewery()
            setState { it.copy(brewery = brewery) }
        }
    }

    override fun onAction(action: Nothing) {
        println(action.toString())
    }
}
