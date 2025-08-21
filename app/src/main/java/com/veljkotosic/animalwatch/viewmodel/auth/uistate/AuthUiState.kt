package com.veljkotosic.animalwatch.viewmodel.auth.uistate

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)