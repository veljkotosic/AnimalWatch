package com.veljkotosic.animalwatch.composable.marker

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.veljkotosic.animalwatch.composable.SeverityComboBox
import com.veljkotosic.animalwatch.composable.tag.TagSelector
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity
import com.veljkotosic.animalwatch.ui.theme.Background
import com.veljkotosic.animalwatch.uistate.map.WatchMarkerUiState
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WatchMarkerCreatorBase (
    mapViewModel: MapViewModel,
    uiState: WatchMarkerUiState,
    modifier: Modifier = Modifier,
    titleReadOnly: Boolean = false,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSeverityChanged: (WatchMarkerSeverity) -> Unit,
    onImageUriChanged: (Uri?) -> Unit,
    onTagAdded: (String) -> Unit,
    onTagRemoved: (String) -> Unit,
    onClear: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxHeight = screenHeight * 0.3f

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val cameraPermissionCoroutineScope = rememberCoroutineScope()

    val processingUiState by mapViewModel.processingUiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val keyboardController = LocalSoftwareKeyboardController.current

    var showOptions by remember { mutableStateOf(false) }

    val imageFile = remember {
        File(context.cacheDir, "marker_image_.jpg").apply {
            createNewFile()
        }
    }

    val uri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val newUri = uri.buildUpon().appendQueryParameter("ts", System.currentTimeMillis().toString()).build()
            onImageUriChanged(newUri)
        }
    }

    LaunchedEffect(cameraPermission.status, uiState.userRequestedCamera) {
        if (cameraPermission.status.isGranted && uiState.userRequestedCamera) {
            mapViewModel.resetCameraRequest()
            cameraLauncher.launch(uri)
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = Background,
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    keyboardController?.hide()
                }
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = {
                    if (it.length <= 25) {
                        onTitleChanged(it)
                    }
                },
                label = {
                    Text("Title")
                },
                readOnly = titleReadOnly,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { onDescriptionChanged(it) },
                label = {
                    Text("Description")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            SeverityComboBox(
                uiState = uiState,
                onSelectionChanged = {
                    onSeverityChanged(it)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            if (uiState.imageUri === null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .heightIn(max = maxHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .clickable {
                            if (cameraPermission.status.isGranted) {
                                cameraLauncher.launch(uri)
                            } else {
                                cameraPermissionCoroutineScope.launch {
                                    mapViewModel.onCameraRequested()
                                    cameraPermission.launchPermissionRequest()
                                }
                            }
                        }
                ) {
                    Text("Press to open camera", color = Color.Gray)
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.imageUri)
                        .setParameter("ts", System.currentTimeMillis())
                        .build(),
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .height(maxHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .clickable {
                            if (cameraPermission.status.isGranted) {
                                cameraLauncher.launch(uri)
                            } else {
                                cameraPermissionCoroutineScope.launch {
                                    mapViewModel.onCameraRequested()
                                    cameraPermission.launchPermissionRequest()
                                }
                            }
                        }
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            FlowRow (
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.tags.forEach { tag ->
                    AssistChip(
                        onClick = {
                            onTagRemoved(tag)
                        },
                        label = { Text(tag) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Remove, contentDescription = "Remove", tint = Color.Red)
                        }
                    )
                }

                AssistChip(
                    onClick = { showOptions = true },
                    label = { Text("Add") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            if (showOptions) {
                TagSelector(
                    title = "Select tag",
                    onTagSelected = {
                        onTagAdded(it)
                        showOptions = false
                    },
                    onDismissRequest = { showOptions = false },
                    modifier = Modifier
                        .background(Background)
                        .padding(8.dp)
                )
            }

            processingUiState.errorMessage?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Button(
                    onClick = {
                        if (!processingUiState.isLoading) {
                            onConfirm()
                        }
                    }
                ) {
                    if (processingUiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text(confirmButtonText)
                    }
                }

                TextButton(
                    onClick = {
                        onClear()
                    }
                ) {
                    Text("Clear")
                }
            }
        }
    }
}