package com.alex.androidplayground.filesScreen.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.alex.androidplayground.R
import com.alex.androidplayground.filesScreen.ui.state.FileViewModel
import com.alex.androidplayground.filesScreen.ui.state.FilesScreenAction
import com.alex.androidplayground.filesScreen.ui.state.FilesScreenState

@Composable
fun FileScreen(viewModel: FileViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FilesLayout(
        screenState = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun FilesLayout(
    screenState: FilesScreenState,
    onAction: (action: FilesScreenAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SaveTextFileLayout(
            textToSave = screenState.textToSave,
            savedText = screenState.savedText,
            onAction = onAction
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        SaveImageFileLayout(
            imageUri = screenState.imageUri,
            onAction = onAction
        )
    }
}

@Composable
fun SaveTextFileLayout(
    textToSave: String?,
    savedText: String?,
    onAction: (action: FilesScreenAction) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = textToSave ?: "",
        placeholder = { Text(stringResource(R.string.enter_text)) },
        onValueChange = { onAction(FilesScreenAction.UpdateText(it)) }
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onAction(FilesScreenAction.CreateTxtFile) }
    ) {
        Text(text = stringResource(R.string.create_txt_file))
    }
    Text(
        text = stringResource(R.string.saved_text),
        style = MaterialTheme.typography.titleMedium
    )
    Text(text = savedText ?: stringResource(R.string.no_text_saved))
}

@Composable
fun SaveImageFileLayout(
    imageUri: Uri?,
    onAction: (action: FilesScreenAction) -> Unit
) {
    val pickImage = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onAction(FilesScreenAction.CreateImageFile(it)) }
    }
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { pickImage.launch("image/*") }
    ) {
        Text(text = stringResource(R.string.select_image_file))
    }
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .build(),
        contentDescription = stringResource(R.string.picked_image)
    )
}

@Preview(showBackground = true)
@Composable
fun FileSelectorLayoutPreview() {
    FilesLayout(
        screenState = FilesScreenState(),
        onAction = { println("$it") }
    )
}