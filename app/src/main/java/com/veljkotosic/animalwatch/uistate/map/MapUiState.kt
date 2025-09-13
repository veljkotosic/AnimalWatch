package com.veljkotosic.animalwatch.uistate.map

data class MapUiState(
    val creatingMarker: Boolean = false,
    val markerTableOpen: Boolean = false,
    val filtersOpen: Boolean = false,
    val searchingMarkers: Boolean = false,
    val markerPreviewOpen: Boolean = false,
    val markerCreatorOpen: Boolean = false,
    val markerSearchOpen: Boolean = false,
    val searchRadiusMeters: Double = 3500.0,
    val confirmDeleteAlertOpen: Boolean = false
)
