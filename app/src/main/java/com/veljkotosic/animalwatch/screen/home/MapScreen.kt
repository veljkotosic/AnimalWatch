package com.veljkotosic.animalwatch.screen.home

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.veljkotosic.animalwatch.composable.filter.FilterSelector
import com.veljkotosic.animalwatch.composable.marker.AnimalWatchMarker
import com.veljkotosic.animalwatch.composable.marker.WatchMarkerCreator
import com.veljkotosic.animalwatch.composable.marker.WatchMarkerPreview
import com.veljkotosic.animalwatch.composable.table.WatchMarkerTable
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarker
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity
import com.veljkotosic.animalwatch.data.marker.entity.tag.WatchMarkerAnimalBehaviourTags
import com.veljkotosic.animalwatch.data.marker.entity.tag.WatchMarkerAnimalStateTags
import com.veljkotosic.animalwatch.data.marker.entity.tag.WatchMarkerAnimalTags
import com.veljkotosic.animalwatch.data.marker.entity.tag.WatchMarkerAnimalTypeTags
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
    val mapUiState by mapViewModel.mapUiState.collectAsState()
    val watchMarkerUiState by mapViewModel.newMarkerUiState.collectAsState()
    val filteredMarkers by mapViewModel.filteredMarkers.collectAsState()
    val filterUiState by mapViewModel.filterUiState.collectAsState()

    val srbija = LatLng(44.226737, 20.7953)
    val cameraPositionState = rememberCameraPositionState {
        position = if (currentLocation == null) {
            CameraPosition.fromLatLngZoom(srbija, 7f)
        } else {
            CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
        }
    }
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    //Dozvola za lokaciju
    @SuppressLint("MissingPermission")
    LaunchedEffect(Unit) {
        if (locationPermission.status.isGranted) {
            mapViewModel.getCurrentLocation()
        } else {
            locationPermission.launchPermissionRequest()
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
                uiState = watchMarkerUiState,
                cameraPositionState = cameraPositionState,
                onDismiss = {
                    mapViewModel.closeMarkerCreator()
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

        Column (
            modifier = Modifier.fillMaxHeight()
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(6.dp),
                onClick = {
                    mapViewModel.openMarkerTable()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.TableRows,
                    contentDescription = "Table of markers",
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxSize()
                )
            }

            Button(
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(6.dp),
                onClick = {
                    mapViewModel.openFilters()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterAlt,
                    contentDescription = "Filter markers",
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxSize()
                )
            }

            Button(
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(6.dp),
                onClick = {
                    //TODO pretraga objekata
                    mapViewModel.forceRefreshMarkers()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search markers",
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxSize()
                )
            }

            Button(
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(6.dp),
                onClick = {
                    mapViewModel.openMarkerCreator()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create marker",
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}