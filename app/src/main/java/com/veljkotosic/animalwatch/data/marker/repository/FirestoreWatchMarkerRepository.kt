package com.veljkotosic.animalwatch.data.marker.repository

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarker
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerState
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerVisibility
import com.veljkotosic.animalwatch.utility.toGeoLocation
import kotlinx.coroutines.tasks.await

class FirestoreWatchMarkerRepository(
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : WatchMarkerRepository {
    private val markers = firestore.collection("markers")
    private val stats = firestore.collection("stats")

    override suspend fun createMarker(marker: WatchMarker) {
        markers.document(marker.id).set(marker).await()

        stats.document(marker.ownerId).update("markersCreatedCount", FieldValue.increment(1)).await()
    }

    override suspend fun removeMarker(marker: WatchMarker) {
        markers.document(marker.id)
            .update(mapOf(
                "state" to WatchMarkerState.Removed,
                "removedOn" to Timestamp.now()
            ))
    }

    override suspend fun editMarker(marker: WatchMarker) {
        markers.document(marker.id)
            .update(mapOf(
                "title" to marker.title,
                "description" to marker.description,
                "edited" to true,
                "tags" to marker.tags
            ))
            .await()
    }

    override suspend fun updateMarker(newMarker: WatchMarker, originalMarker: WatchMarker) {
        markers.document(originalMarker.id)
            .update(mapOf(
                "state" to WatchMarkerState.Updated,
                "updatedOn" to Timestamp.now()
            ))
            .await()
        val newMarkerWithBase = newMarker.copy(baseMarkerId = originalMarker.id)
        markers.document(newMarker.id).set(newMarkerWithBase).await()

        stats.document(newMarker.ownerId).update("markersUpdatedCount", FieldValue.increment(1)).await()
    }

    override fun getMarkerLocationsInArea(centerLatitude: Double, centerLongitude: Double, radiusMeters: Double, onDone: (List<WatchMarker>) -> Unit) {
        val center = GeoLocation(centerLatitude, centerLongitude)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusMeters)
        val tasks = mutableListOf<Task<QuerySnapshot>>()

        for (b in bounds) {
            val q = markers
                .orderBy("positionHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
                .whereEqualTo("state", WatchMarkerState.Active)
                .whereEqualTo("visibility", WatchMarkerVisibility.Public)
            tasks.add(q.get())
        }

        Tasks.whenAllComplete(tasks)
            .addOnSuccessListener {
            val foundMarkers = mutableListOf<WatchMarker>()
            for (task in tasks) {
                val snapshot = task.result
                for (doc in snapshot!!.documents) {
                    val foundMarker = doc.toObject(WatchMarker::class.java) ?: continue
                    if (GeoFireUtils.getDistanceBetween(center, foundMarker.position.toGeoLocation()) <= radiusMeters) {
                        foundMarkers.add(foundMarker)
                    }
                }
            }
            onDone(foundMarkers)
        }
    }

    override suspend fun getMarker(markerId: String): WatchMarker {
        val markerSnap = markers.document(markerId).get().await()
        val marker = markerSnap.toObject(WatchMarker::class.java)
        return marker!!
    }

    override suspend fun appraiseMarker(marker: WatchMarker, userId: String) {
        markers.firestore.runTransaction { transaction ->
            val appraisedBy = markers.document(marker.id).collection("appraisedBy")
            val doc = transaction.get(appraisedBy.document(userId))

            if (!doc.exists()) {
                transaction.set(appraisedBy.document(userId), mapOf("appraisedOn" to Timestamp.now()))
                transaction.update(markers.document(marker.id), "appraisalCount", FieldValue.increment(1))
                transaction.update(stats.document(marker.ownerId), "totalAppraisals", FieldValue.increment(1))
            } else {
                transaction.delete(appraisedBy.document(userId))
                transaction.update(markers.document(marker.id), "appraisalCount", FieldValue.increment(-1))
                transaction.update(stats.document(marker.ownerId), "totalAppraisals", FieldValue.increment(-1))
            }
        }.await()
    }

    override suspend fun seeMarker(marker: WatchMarker, userId: String) {
        markers.firestore.runTransaction { transaction ->
            val seenBy = markers.document(marker.id).collection("seenBy")
            val doc = transaction.get(seenBy.document(userId))

            if (!doc.exists()) {
                transaction.set(seenBy.document(userId), mapOf("seenOn" to Timestamp.now()))
                transaction.update(markers.document(marker.id), "seenCount", FieldValue.increment(1))
            } else {
                transaction.delete(seenBy.document(userId))
                transaction.update(markers.document(marker.id), "seenCount", FieldValue.increment(-1))
            }
        }
    }


}