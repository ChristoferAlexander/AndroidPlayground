package com.alex.androidplayground.fileSelectorScreen.ui.state

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@HiltViewModel(assistedFactory = FileSelectorViewModel.Factory::class)
class FileSelectorViewModel @AssistedInject constructor(
    @Assisted private val filesDir: String,
    @ApplicationContext val application: Context
) :
    ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(filesDir: String): FileSelectorViewModel
    }

    private val screenDir = "fileselectorviewModel"
    private val txtFileName = "text.txt"
    private val imageFileName = "image.jpeg"

    private val _screenState = MutableStateFlow(FileSelectorScreenState())
    val screenState = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                loadImageFile()
            }
            launch {
                loadTxtFile()
            }
        }
    }

    fun onAction(action: FileSelectorScreenAction) {
        when (action) {
            is FileSelectorScreenAction.CreateImageFile -> viewModelScope.launch { createImageFile(action.uri) }
            is FileSelectorScreenAction.CreateTxtFile -> viewModelScope.launch { createTxtFile() }
            is FileSelectorScreenAction.UpdateText -> updateText(action.text)
        }
    }

    private fun updateText(text: String) {
        _screenState.value = _screenState.value.copy(textToSave = text)
    }

    private fun createDir(): File {
        val folder = File("$filesDir/$screenDir")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    private suspend fun createTxtFile() = withContext(Dispatchers.IO) {
        val file = File(createDir(), txtFileName)
        try {
            file.createNewFile()
            _screenState.value.textToSave?.let { file.writeText(it) }
            loadTxtFile()
        } catch (e: Exception) {
            e.printStackTrace()
            _screenState.value = _screenState.value.copy(textFileError = "Failed to save text: ${e.message}")
        }
    }

    private suspend fun createImageFile(uri: Uri) = withContext(Dispatchers.IO) {
        val file = File(createDir(), imageFileName)
        try {
            application.contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            _screenState.value = _screenState.value.copy(imageUri = null)
            delay(100)
            _screenState.value = _screenState.value.copy(imageUri = Uri.fromFile(file))
        } catch (e: Exception) {
            e.printStackTrace()
            _screenState.value = _screenState.value.copy(imageFileError = "Failed to save image: ${e.message}")
        }
    }

    private suspend fun loadTxtFile() = withContext(Dispatchers.IO) {
        val file = File("$filesDir/$screenDir/$txtFileName")
        if (file.exists()) {
            _screenState.value = _screenState.value.copy(savedText = file.readText())
        }
    }

    private suspend fun loadImageFile() = withContext(Dispatchers.IO) {
        val file = File("$filesDir/$screenDir/$imageFileName")
        if (file.exists()) {
            _screenState.value = _screenState.value.copy(imageUri = Uri.fromFile(file))
        }
    }

}

data class FileSelectorScreenState(
    val textToSave: String? = null,
    val savedText: String? = null,
    val imageUri: Uri? = null,
    val textFileError: String? = null,
    val imageFileError: String? = null,
    val placeHolder: String = "Enter text"
)

sealed interface FileSelectorScreenAction {
    data class UpdateText(val text: String) : FileSelectorScreenAction
    data object CreateTxtFile : FileSelectorScreenAction
    data class CreateImageFile(val uri: Uri) : FileSelectorScreenAction
}