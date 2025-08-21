package com.veljkotosic.animalwatch.data.user.repository

import android.net.Uri
import com.veljkotosic.animalwatch.data.user.entity.User

interface UserRepository {
    suspend fun createUser(user: User, avatarUri: String)
    suspend fun getUser(uid: String) : User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser(uid: String)
}