package com.veljkotosic.animalwatch.composable.marker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.veljkotosic.animalwatch.uistate.map.WatchMarkerUiState
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel

@Composable
fun WatchMarkerUpdater (
    mapViewModel: MapViewModel,
    uiState: WatchMarkerUiState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit
) {
    WatchMarkerCreatorBase(
        mapViewModel = mapViewModel,
        uiState = uiState,
        modifier = modifier,
        onDismiss = {
            onDismiss()
        },
        onConfirm = {
            onUpdate()
        },
        onTitleChanged = {
            mapViewModel.onUpdateMarkerTitleChanged(it)
        },
        onDescriptionChanged = {
            mapViewModel.onUpdateMarkerDescriptionChanged(it)
        },
        onSeverityChanged = {
            mapViewModel.onUpdateMarkerSeverityChanged(it)
        },
        onImageUriChanged = {
            mapViewModel.onUpdateMarkerImageUriChanged(it)
        },
        onTagAdded = {
            mapViewModel.onUpdateMarkerTagAdded(it)
        },
        onTagRemoved = {
            mapViewModel.onUpdateMarkerTagRemoved(it)
        },
        titleReadOnly = true,
        confirmButtonText = "Update",
        onClear = {
            mapViewModel.resetUpdateMarkerUiState()
        }
    )
}