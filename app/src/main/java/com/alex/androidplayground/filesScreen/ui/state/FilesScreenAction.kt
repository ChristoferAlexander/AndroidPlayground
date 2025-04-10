package com.alex.androidplayground.filesScreen.ui.state

import android.net.Uri
import com.alex.androidplayground.core.ui.state.Action

sealed class FilesScreenAction : Action {
    data class UpdateText(val text: String) : FilesScreenAction()
    data object CreateTxtFile : FilesScreenAction()
    data class CreateImageFile(val uri: Uri) : FilesScreenAction()
}
