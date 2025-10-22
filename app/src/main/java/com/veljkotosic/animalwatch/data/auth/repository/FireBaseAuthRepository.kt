package com.veljkotosic.animalwatch.data.auth.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FireBaseAuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {
    override val errorMessage: String? = ""

    override suspend fun register(email: String, password: String): String {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return result.user!!.uid
    }

    override suspend fun login(email: String, password: String) : String {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user!!.uid
    }

    override fun signOut() = firebaseAuth.signOut()

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

}