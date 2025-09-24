package com.veljkotosic.animalwatch.uistate.map

import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    val userLoaded: Boolean = false,
    val defaultLocation: LatLng = LatLng(44.226737, 20.7953),
    val creatingMarker: Boolean = false,
    val markerTableOpen: Boolean = false,
    val filtersOpen: Boolean = false,
    val searchingMarkers: Boolean = false,
    val markerCreatorOpen: Boolean = false,
    val markerSearchOpen: Boolean = false,
    val searchRadiusMeters: Double = 3500.0,
    val confirmDeleteAlertOpen: Boolean = false
)
