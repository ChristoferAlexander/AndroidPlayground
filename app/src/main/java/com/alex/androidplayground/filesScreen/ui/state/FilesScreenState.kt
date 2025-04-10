package com.alex.androidplayground.filesScreen.ui.state

import android.net.Uri
import com.alex.androidplayground.core.ui.state.State

data class FilesScreenState(
    val textToSave: String? = null,
    val savedText: String? = null,
    val imageUri: Uri? = null,
    val textFileError: String? = null,
    val imageFileError: String? = null
) : State