package com.veljkotosic.animalwatch.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veljkotosic.animalwatch.data.auth.repository.AuthRepository
import com.veljkotosic.animalwatch.log.Tags
import com.veljkotosic.animalwatch.uistate.auth.LoginUiState
import com.veljkotosic.animalwatch.uistate.processing.ProcessingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _processingUiState = MutableStateFlow(ProcessingUiState())
    val processingUiState: StateFlow<ProcessingUiState> = _processingUiState.asStateFlow()

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _loginUiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChanged(newPassword: String) {
        _loginUiState.update { it.copy(password = newPassword) }
    }

    fun setError(newErrorMessage: String?) {
        _processingUiState.update { it.copy(errorMessage = newErrorMessage) }
    }

    fun login() = viewModelScope.launch {
        _processingUiState.update { it.copy(isLoading = true) }
        try {
            val uid = authRepository.login(
                _loginUiState.value.email,
                _loginUiState.value.password
            )
            _processingUiState.update { it.copy(isSuccess = true) }
        } catch (e: Exception) {
            _processingUiState.update { it.copy(isSuccess = false, errorMessage = e.message) }
            Log.e(Tags.AUTH_LOG_TAG, "Login failed.")
        } finally {
            _processingUiState.update { it.copy(isLoading = false) }
        }
    }
}