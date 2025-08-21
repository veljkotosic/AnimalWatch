package com.veljkotosic.animalwatch.viewmodel.auth.uistate

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val processing: AuthUiState = AuthUiState()
)