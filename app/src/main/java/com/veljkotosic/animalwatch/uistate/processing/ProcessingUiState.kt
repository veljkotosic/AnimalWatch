package com.veljkotosic.animalwatch.uistate.processing

data class ProcessingUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)