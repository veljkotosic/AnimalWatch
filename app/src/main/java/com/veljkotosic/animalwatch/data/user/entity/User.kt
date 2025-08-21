package com.veljkotosic.animalwatch.data.user.entity

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val name: String = "",
    val surname: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = ""
)