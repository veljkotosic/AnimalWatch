package com.veljkotosic.animalwatch.viewmodel.auth.uistate

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
    val processing: AuthUiState = AuthUiState()
)
