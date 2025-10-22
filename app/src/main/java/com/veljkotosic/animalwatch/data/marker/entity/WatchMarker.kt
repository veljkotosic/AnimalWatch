package com.veljkotosic.animalwatch.data.marker.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class WatchMarker @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val ownerId: String = "",
    val ownerUserName: String = "",
    val position: GeoPoint = GeoPoint(0.0, 0.0),
    val positionHash: String = "",
    val title: String = "",
    val description: String = "",
    val tags: List<String> = listOf(),
    val baseMarkerId: String? = null,
    val positionInThread: Int = 1,
    val hasUpdates: Boolean = false,
    val updateCount: Int = 0,
    val seenCount: Int = 0,
    val appraisalCount: Int = 0,
    val edited: Boolean = false,
    val imageUri: String = "",
    val createdOn: Timestamp = Timestamp.now(),
    val removedOn: Timestamp? = null,
    val expiresOn: Timestamp? = null,
    val updatedOn: Timestamp? = null,
    val visibility: WatchMarkerVisibility = WatchMarkerVisibility.Public,
    val state: WatchMarkerState = WatchMarkerState.Active,
    val severity: WatchMarkerSeverity = WatchMarkerSeverity.Undefined
) {
    override fun equals(other: Any?): Boolean {
        return other is WatchMarker && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}


