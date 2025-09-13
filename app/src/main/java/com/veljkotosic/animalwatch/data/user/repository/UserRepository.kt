package com.veljkotosic.animalwatch.data.user.repository

import com.veljkotosic.animalwatch.data.user.entity.User

interface UserRepository {
    suspend fun createUser(user: User, avatarUri: String)
    suspend fun getUser(uid: String) : User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser(uid: String)
    suspend fun updateLastKnownLocation(uid: String, latitude: Double, longitude: Double)
}