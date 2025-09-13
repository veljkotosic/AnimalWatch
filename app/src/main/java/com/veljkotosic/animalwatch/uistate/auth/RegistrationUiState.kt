package com.veljkotosic.animalwatch.uistate.auth

import android.net.Uri

data class RegistrationUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val surname: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val avatarUri: Uri? = null,

    val passwordVisible: Boolean = false,
    val userRequestedCamera: Boolean = false
)
