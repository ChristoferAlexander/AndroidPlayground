package com.alex.androidplayground.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ForegroundServiceViewModel @Inject constructor(
    @ApplicationContext val application: Context
) : ViewModel() {

    private val _screenState = MutableStateFlow(FileSelectorScreenState())
    val screenState = _screenState.asStateFlow()

    fun onAction(action: ForegroundServiceAction) {
        when (action) {
            else -> {}
        }
    }
}

data class ForegroundServiceState(
    val textToSave: String? = null,
    val savedText: String? = null,
    val imageUri: Uri? = null,
    val textFileError: String? = null,
    val imageFileError: String? = null,
    val placeHolder: String = "Enter text"
)

sealed interface ForegroundServiceAction {

}