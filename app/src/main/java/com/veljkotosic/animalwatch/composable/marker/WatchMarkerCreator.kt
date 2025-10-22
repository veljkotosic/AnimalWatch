package com.veljkotosic.animalwatch.composable.marker

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import com.veljkotosic.animalwatch.ui.theme.Background
import com.veljkotosic.animalwatch.uistate.map.WatchMarkerUiState
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun WatchMarkerCreator(
    mapViewModel: MapViewModel,
    uiState: WatchMarkerUiState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onCreate: () -> Unit
) {
    WatchMarkerCreatorBase(
        mapViewModel = mapViewModel,
        uiState = uiState,
        modifier = modifier,
        onClear = {
            mapViewModel.resetNewMarkerUiState()
        },
        onTitleChanged = {
            mapViewModel.onNewMarkerTitleChanged(it)
        },
        onDescriptionChanged = {
            mapViewModel.onNewMarkerDescriptionChanged(it)
        },
        onSeverityChanged = {
            mapViewModel.onNewMarkerSeverityChanged(it)
        },
        onImageUriChanged = {
            mapViewModel.onNewMarkerImageUriChanged(it)
        },
        onTagAdded = {
            mapViewModel.onNewMarkerTagAdded(it)
        },
        onTagRemoved = {
            mapViewModel.onNewMarkerTagRemoved(it)
        },
        confirmButtonText = "Create",
        onConfirm = {
            onCreate()
        },
        onDismiss = {
            onDismiss()
        }
    )
}