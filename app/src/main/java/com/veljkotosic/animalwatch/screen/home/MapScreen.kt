package com.veljkotosic.animalwatch.screen.home

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.veljkotosic.animalwatch.composable.filter.FilterSelector
import com.veljkotosic.animalwatch.composable.marker.AnimalWatchMarker
import com.veljkotosic.animalwatch.composable.marker.WatchMarkerCreator
import com.veljkotosic.animalwatch.composable.marker.WatchMarkerPreview
import com.veljkotosic.animalwatch.composable.marker.WatchMarkerUpdater
import com.veljkotosic.animalwatch.composable.search.MarkerSearch
import com.veljkotosic.animalwatch.composable.table.WatchMarkerTable
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    mapViewModel: MapViewModel
) {
    val context = LocalContext.current

    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val currentLocation by mapViewModel.currentLocation.collectAsState()
    val selectedMarker by mapViewModel.selectedMarker.collectAsState()
    val baseMarker by mapViewModel.baseMarker.collectAsState()
    val mapUiState by mapViewModel.mapUiState.collectAsState()
    val newMarkerUiState by mapViewModel.newMarkerUiState.collectAsState()
    val updateMarkerUiState by mapViewModel.updateMarkerUiState.collectAsState()
    val filteredMarkers by mapViewModel.filteredMarkers.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapUiState.defaultLocation, 7f)
    }
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }

    LaunchedEffect(mapUiState.userLoaded) {
        if (mapUiState.userLoaded && currentLocation != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
        }
    }

    @SuppressLint("MissingPermission")
    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            mapViewModel.getCurrentLocation()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(mapUiState.newMarkerFilteredOut) {
        if (mapUiState.newMarkerFilteredOut) {
            Toast.makeText(context, "Newly created marker may be hidden by existing filters", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        selectedMarker?.let { marker ->
            WatchMarkerPreview(
                watchMarker = marker,
                mapViewModel = mapViewModel,
                onDismiss = {
                    mapViewModel.deselectMarker()
                }
            )
        }

        if (mapUiState.markerCreatorOpen) {
            WatchMarkerCreator(
                mapViewModel = mapViewModel,
                uiState = newMarkerUiState,
                onDismiss = {
                    mapViewModel.closeMarkerCreator()
                },
                onCreate = {
                    mapViewModel.createMarker(context, cameraPositionState)
                }
            )
        }

        baseMarker?.let { marker ->
            WatchMarkerUpdater(
                mapViewModel = mapViewModel,
                uiState = updateMarkerUiState,
                onDismiss = {
                    mapViewModel.closeMarkerUpdater()
                },
                onUpdate = {
                    mapViewModel.updateMarker(context, marker)
                }
            )
        }

        if (mapUiState.markerTableOpen) {
            WatchMarkerTable(
                markers = filteredMarkers,
                onDismissRequest = { mapViewModel.closeMarkerTable() },
                onSelect = {
                    mapViewModel.closeMarkerTable()
                    mapViewModel.selectMarker(it)
                    mapViewModel.moveCameraToMarker(it, cameraPositionState)
                }
            )
        }

        if (mapUiState.filtersOpen) {
            FilterSelector(
                mapViewModel = mapViewModel,
                onDismissRequest = {
                    mapViewModel.closeFilters()
                }
            )
        }

        if (mapUiState.markerSearchOpen) {
            MarkerSearch(
                mapViewModel = mapViewModel,
                onDismiss = {
                    mapViewModel.closeMarkerSearch()
                }
            )
        }

        GoogleMap (
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = MapProperties(isMyLocationEnabled = locationPermission.status.isGranted),
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        mapViewModel.deselectMarker()
                    })
                }
        ) {
            if (cameraPositionState.position.zoom >= 12f) {
                filteredMarkers.forEach { marker ->
                    key(marker.id) {
                        AnimalWatchMarker (
                            watchMarker = marker,
                            onClick = {
                                mapViewModel.selectMarker(marker)
                                mapViewModel.moveCameraToMarker(marker, cameraPositionState)
                                true
                            }
                        )
                    }
                }
            }
        }

        Column (
            modifier = Modifier.fillMaxHeight().padding(bottom = 28.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                modifier = Modifier.size(64.dp).padding(4.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(6.dp),
                onClick = {
                    mapViewModel.openMarkerTable()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.TableRows,
                    contentDescription = "Table of markers",
                    modifier = Modifier.padding(2.dp).fillMaxSize()
                )
            }

            Button(
                modifier = Modifier.size(64.dp).padding(4.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(6.dp),
                onClick = {
                    mapViewModel.openFilters()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterAlt,
                    contentDescription = "Filter markers",
                    modifier = Modifier.padding(2.dp).fillMaxSize()
                )
            }

            Button(
                modifier = Modifier.size(64.dp).padding(4.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(6.dp),
                onClick = {
                    mapViewModel.openMarkerSearch()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search markers",
                    modifier = Modifier.padding(2.dp).fillMaxSize()
                )
            }

            Button(
                modifier = Modifier.size(64.dp).padding(4.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(6.dp),
                onClick = {
                    mapViewModel.openMarkerCreator()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create marker",
                    modifier = Modifier.padding(2.dp).fillMaxSize()
                )
            }
        }
    }
}