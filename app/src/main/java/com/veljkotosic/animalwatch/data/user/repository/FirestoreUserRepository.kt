package com.veljkotosic.animalwatch.data.user.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.veljkotosic.animalwatch.data.user.entity.User
import kotlinx.coroutines.tasks.await

class FirestoreUserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {
    private val users = firestore.collection("users")
    override suspend fun createUser(user: User, avatarUri: String) {
        val userWithAvatar = user.copy(avatarUrl = avatarUri)
        users.document(user.uid).set(userWithAvatar).await()
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
}