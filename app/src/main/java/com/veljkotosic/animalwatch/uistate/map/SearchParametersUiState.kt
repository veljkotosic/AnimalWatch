package com.veljkotosic.animalwatch.uistate.map

data class SearchParametersUiState(
    val radiusMeters: Double = 3500.0,
    val radiusMetersLowerBound: Double = 200.0,
    val radiusMetersUpperBound: Double = 10000.0
)
