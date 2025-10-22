package com.veljkotosic.animalwatch.data.marker.repository

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
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
        marker.title.trim()
        marker.description.trim()

        markers.firestore.runTransaction { transaction ->
            transaction.set(markers.document(marker.id), marker)

            transaction.update(stats.document(marker.ownerId), mapOf(
                "markersCreatedCount" to FieldValue.increment(1),
                "total" to FieldValue.increment(1)
            ))
        }.await()
    }

    override suspend fun removeMarker(marker: WatchMarker) {
        markers.firestore.runTransaction { transaction ->
            transaction.update(markers.document(marker.id), mapOf(
                "state" to WatchMarkerState.Removed,
                "removedOn" to Timestamp.now()
            ))

            if (marker.baseMarkerId != null) {
                transaction.update(markers.document(marker.baseMarkerId), mapOf(
                    "hasUpdates" to false,
                    "state" to WatchMarkerState.Active,
                    "updatedOn" to null
                ))

                transaction.update(stats.document(marker.ownerId), mapOf(
                    "markersUpdatedCount" to FieldValue.increment(-1),
                    "total" to FieldValue.increment(-1)
                ))
            } else {
                transaction.update(stats.document(marker.ownerId), mapOf(
                    "markersCreatedCount" to FieldValue.increment(-1),
                    "total" to FieldValue.increment(-1)
                ))
            }
        }.await()
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
        markers.firestore.runTransaction { transaction ->
            transaction.update(markers.document(originalMarker.id), mapOf(
                "hasUpdates" to true,
                "state" to WatchMarkerState.Updated,
                "updatedOn" to Timestamp.now()
            ))

            val newMarkerWithBase = newMarker.copy(baseMarkerId = originalMarker.id)
            transaction.set(markers.document(newMarker.id), newMarkerWithBase)

            transaction.update(stats.document(newMarker.ownerId), mapOf(
                "markersUpdatedCount" to FieldValue.increment(1),
                "total" to FieldValue.increment(1)
            ))
        }.await()
    }

    override fun getMarkerCountInArea(centerLatitude: Double, centerLongitude: Double, radiusMeters: Double, onDone: (Result<Int>) -> Unit) {
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
                try {
                    var markerCount = 0
                    for (task in tasks) {
                        val snapshot = task.result
                        for (doc in snapshot!!.documents) {
                            val foundMarker = doc.toObject(WatchMarker::class.java) ?: continue
                            if (GeoFireUtils.getDistanceBetween(center, foundMarker.position.toGeoLocation()) <= radiusMeters) {
                                markerCount++
                            }
                        }
                    }
                    onDone(Result.success(markerCount))
                } catch (e: Exception) {
                    onDone(Result.failure(e))
                }
            }.addOnFailureListener { e ->
                onDone(Result.failure(e))
            }
    }

    private var listeners = mutableListOf<ListenerRegistration>()

    override fun observeMarkersInArea(centerLatitude: Double, centerLongitude: Double, radiusMeters: Double, onChange: (List<WatchMarker>) -> Unit) {
        listeners.forEach { it.remove() }
        listeners.clear()

        val center = GeoLocation(centerLatitude, centerLongitude)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusMeters)
        val foundMarkers = mutableMapOf<String, WatchMarker>()

        for (b in bounds) {
            val query = markers
                .orderBy("positionHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
                .whereEqualTo("state", WatchMarkerState.Active)
                .whereEqualTo("visibility", WatchMarkerVisibility.Public)

            val listener = query.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (snapshot == null || error != null) {
                    Log.w("Firestore", "Listen failed: ", error)
                    return@addSnapshotListener
                }

                for (change in snapshot.documentChanges) {
                    val doc = change.document
                    val marker = doc.toObject(WatchMarker::class.java)
                    when (change.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            if (marker.state == WatchMarkerState.Active) {
                                val distance = GeoFireUtils.getDistanceBetween(center, marker.position.toGeoLocation())
                                if (distance <= radiusMeters) {
                                    foundMarkers[marker.id] = marker
                                } else {
                                    foundMarkers.remove(marker.id)
                                }
                            } else {
                                foundMarkers.remove(marker.id)
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            foundMarkers.remove(marker.id)
                        }
                    }
                }
                onChange(foundMarkers.values.toList())
            }
            listeners.add(listener)
        }
    }

    override fun stopObservingMarkers() {
        listeners.forEach { it.remove() }
        listeners.clear()
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
                transaction.update(stats.document(marker.ownerId), mapOf(
                    "totalAppraisals" to FieldValue.increment(1),
                    "total" to FieldValue.increment(1)
                ))
            } else {
                transaction.delete(appraisedBy.document(userId))
                transaction.update(markers.document(marker.id), "appraisalCount", FieldValue.increment(-1))
                transaction.update(stats.document(marker.ownerId), mapOf(
                    "totalAppraisals" to FieldValue.increment(-1),
                    "total" to FieldValue.increment(-1)
                ))
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