package com.veljkotosic.animalwatch.data.auth.repository

interface AuthRepository {
    suspend fun register(email: String, password: String) : String
    suspend fun login(email: String, password: String) : String
    fun signOut()
    fun getCurrentUserId() : String?
    val errorMessage: String?
}