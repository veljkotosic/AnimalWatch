package com.veljkotosic.animalwatch.data.marker.repository

import com.veljkotosic.animalwatch.data.marker.entity.WatchMarker

interface WatchMarkerRepository {
    suspend fun createMarker(marker: WatchMarker)
    suspend fun removeMarker(marker: WatchMarker)
    suspend fun editMarker(marker: WatchMarker)
    suspend fun updateMarker(newMarker: WatchMarker, originalMarker: WatchMarker)
    fun getMarkerCountInArea(centerLatitude: Double, centerLongitude: Double, radiusMeters: Double, onDone: (Result<Int>) -> Unit)
    fun getMarkerLocationsInArea(centerLatitude: Double, centerLongitude: Double, radiusMeters: Double, onDone: (List<WatchMarker>) -> Unit)
    suspend fun getMarker(markerId: String) : WatchMarker
    suspend fun appraiseMarker(marker: WatchMarker, userId: String)

    suspend fun seeMarker(marker: WatchMarker, userId: String)
}