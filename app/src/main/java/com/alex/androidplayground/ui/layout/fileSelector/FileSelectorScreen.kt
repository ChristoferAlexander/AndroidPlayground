package com.alex.androidplayground.ui.layout.fileSelector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.alex.androidplayground.viewModel.FileSelectorScreenAction
import com.alex.androidplayground.viewModel.FileSelectorScreenState
import com.alex.androidplayground.viewModel.FileSelectorViewModel

@Composable
fun FileSelectorScreen() {
    val context = LocalContext.current
    val viewModel = hiltViewModel<FileSelectorViewModel, FileSelectorViewModel.Factory>(
        creationCallback = { factory: FileSelectorViewModel.Factory ->
            factory.create(filesDir = context.filesDir.path)
        }
    )
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    FileSelectorLayout(
        screenState = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun FileSelectorLayout(
    screenState: FileSelectorScreenState,
    onAction: (action: FileSelectorScreenAction) -> Unit
) {
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            onAction(FileSelectorScreenAction.CreateImageFile(it))
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenState.textToSave ?: "",
            placeholder = {
                Text(screenState.placeHolder)
            },
            onValueChange = {
                onAction(FileSelectorScreenAction.UpdateText(it))
            }
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onAction(FileSelectorScreenAction.CreateTxtFile)
            }
        ) {
            Text(text = "Create txt file")
        }
        Text(
            text = "Saved text:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(text = screenState.savedText ?: "No text saved")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                pickImage.launch("image/*")
            }
        ) {
            Text(text = "Select image file")
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(screenState.imageUri)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build(),
            contentDescription = "Picked image"
        )
    }
}

@Preview
@Composable
fun FileSelectorLayoutPreview() {
    FileSelectorLayout(
        screenState = FileSelectorScreenState(),
        onAction = { println("$it") }
    )
}