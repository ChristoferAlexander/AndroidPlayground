package com.alex.androidplayground.core.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : State, A : Action, E : Event>(initialState: () -> S) : ViewModel() {

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState())
    val state = _state.asStateFlow()

    private val _events = Channel<E>()
    val events = _events.receiveAsFlow()

    protected fun setState(update: (S) -> S) {
        _state.update { update(it) }
    }

    protected suspend fun sendEvent(event: E) {
        _events.send(event)
    }

    abstract fun onAction(action: A)
}