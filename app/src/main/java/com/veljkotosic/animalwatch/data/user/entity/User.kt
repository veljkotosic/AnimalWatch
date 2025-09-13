package com.veljkotosic.animalwatch.data.user.entity

import com.google.firebase.firestore.GeoPoint

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val name: String = "",
    val surname: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val lastKnownLocation: GeoPoint? = null
)