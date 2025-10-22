package com.veljkotosic.animalwatch.data.stat.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.veljkotosic.animalwatch.data.stat.entity.UserStats
import kotlinx.coroutines.tasks.await

class FirestoreUserStatsRepository(
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserStatsRepository {
    private val stats = firestore.collection("stats")

    private var topTenListener: ListenerRegistration? = null

    override suspend fun getTopTen(onDone: (List<UserStats>) -> Unit) {
        val statsSnap = stats.orderBy("total", Query.Direction.DESCENDING).limit(10).get().await()

        val topTen = statsSnap.documents.mapNotNull { document ->
            document.toObject(UserStats::class.java)
        }

        onDone(topTen)
    }

    override fun observeTopTen(onDone: (List<UserStats>) -> Unit) {
        topTenListener?.remove()

        topTenListener = stats.orderBy("total", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val topTen = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(UserStats::class.java)
                    }
                    onDone(topTen)
                }
            }
    }

    override fun stopObservingTopTen() {
        topTenListener?.remove()
    }

    override suspend fun getStats(uid: String) : UserStats {
        val statSnap = stats.document(uid).get().await()
        val stat = statSnap.toObject(UserStats::class.java)
        return stat!!
    }
}