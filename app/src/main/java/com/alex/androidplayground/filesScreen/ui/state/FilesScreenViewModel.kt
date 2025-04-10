package com.alex.androidplayground.filesScreen.ui.state

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.alex.androidplayground.core.ui.state.BaseViewModel
import com.alex.androidplayground.core.ui.state.Event
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(@ApplicationContext val application: Context) : BaseViewModel<FilesScreenState, FilesScreenAction, Nothing>(
    initialState = { FilesScreenState() }
) {

    private val filesDir = application.filesDir.path
    private val screenDir = "fileselectorviewModel"
    private val txtFileName = "text.txt"
    private val imageFileName = "image.jpeg"

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

    override fun onAction(action: FilesScreenAction) {
        when (action) {
            is FilesScreenAction.CreateImageFile -> viewModelScope.launch { createImageFile(action.uri) }
            is FilesScreenAction.CreateTxtFile -> viewModelScope.launch { createTxtFile() }
            is FilesScreenAction.UpdateText -> updateText(action.text)
        }
    }

    private fun updateText(text: String) {
        setState { it.copy(textToSave = text) }
    }

    private fun createDir(): File {
        val folder = File("${filesDir}/$screenDir")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    private suspend fun createTxtFile() = withContext(Dispatchers.IO) {
        val file = File(createDir(), txtFileName)
        try {
            file.createNewFile()
            state.value.textToSave?.let { file.writeText(it) }
            loadTxtFile()
        } catch (e: Exception) {
            e.printStackTrace()
            setState { it.copy(textFileError = e.message) }
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
            setState { it.copy(imageUri = null) }
            delay(100)
            setState { it.copy(imageUri = Uri.fromFile(file)) }
        } catch (e: Exception) {
            e.printStackTrace()
            setState { it.copy(imageFileError = e.message) }
        }
    }

    private suspend fun loadTxtFile() = withContext(Dispatchers.IO) {
        val file = File("$filesDir/$screenDir/$txtFileName")
        if (file.exists()) {
            setState { it.copy(savedText = file.readText()) }
        }
    }

    private suspend fun loadImageFile() = withContext(Dispatchers.IO) {
        val file = File("$filesDir/$screenDir/$imageFileName")
        if (file.exists()) {
            setState { it.copy(imageUri = Uri.fromFile(file)) }
        }
    }
}

class FilesScreenEvent : Event
