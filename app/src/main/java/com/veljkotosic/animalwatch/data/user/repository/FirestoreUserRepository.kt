package com.veljkotosic.animalwatch.data.user.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.veljkotosic.animalwatch.data.stat.entity.UserStats
import com.veljkotosic.animalwatch.data.user.entity.User
import kotlinx.coroutines.tasks.await

class FirestoreUserRepository(
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {
    private val users = firestore.collection("users")
    private val stats = firestore.collection("stats")

    override suspend fun createUser(user: User, avatarUri: String) {
        users.firestore.runTransaction { transaction ->
            val userWithAvatar = user.copy(avatarUrl = avatarUri)
            transaction.set(users.document(user.uid), userWithAvatar)

            val emptyStats = UserStats(uid = user.uid, username = user.displayName)
            transaction.set(stats.document(user.uid), emptyStats)
        }
    }

    override suspend fun getUser(uid: String) : User? {
        val snapshot = users.document(uid).get().await()
        return snapshot.toObject(User::class.java)
    }

    override suspend fun updateUser(user: User) {
        users.document(user.uid).set(user).await()
    }

    override suspend fun deleteUser(uid: String) {
        users.document(uid).delete().await()
    }

    override suspend fun updateLastKnownLocation(uid: String, latitude: Double, longitude: Double) {
        users.document(uid).update(mapOf("lastKnownLocation" to GeoPoint(latitude, longitude)))
    }
}