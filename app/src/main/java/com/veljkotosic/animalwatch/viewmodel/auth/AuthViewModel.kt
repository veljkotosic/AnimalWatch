package com.veljkotosic.animalwatch.viewmodel.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.veljkotosic.animalwatch.data.auth.repository.AuthRepository
import com.veljkotosic.animalwatch.data.user.entity.User
import com.veljkotosic.animalwatch.viewmodel.auth.uistate.LoginUiState
import com.veljkotosic.animalwatch.viewmodel.auth.uistate.RegistrationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registrationUiState = MutableStateFlow(RegistrationUiState())
    val registrationUiState: StateFlow<RegistrationUiState> = _registrationUiState.asStateFlow()

    fun onLoginEmailChanged(newEmail: String) {
        _loginUiState.update { it.copy(email = newEmail) }
    }

    fun onLoginPasswordChanged(newPassword: String) {
        _loginUiState.update { it.copy(password = newPassword) }
    }

    fun onRegistrationEmailChanged(newEmail: String) {
        _registrationUiState.update { it.copy(email = newEmail) }
    }

    fun onRegistrationPasswordChanged(newPassword: String) {
        _registrationUiState.update { it.copy(password = newPassword) }
    }

    fun onRegistrationConfirmPasswordChanged(newConfirmPassword: String) {
        _registrationUiState.update { it.copy(confirmPassword = newConfirmPassword) }
    }

    fun onRegistrationNameChanged(newName: String) {
        _registrationUiState.update { it.copy(name = newName) }
    }

    fun onRegistrationSurnameChanged(newSurname: String) {
        _registrationUiState.update { it.copy(surname = newSurname) }
    }

    fun onRegistrationDisplayNameChanged(newDisplayName: String) {
        _registrationUiState.update { it.copy(displayName = newDisplayName) }
    }

    fun onRegistrationPhoneNumberChanged(newPhoneNumber: String) {
        _registrationUiState.update { it.copy(phoneNumber = newPhoneNumber) }
    }

    fun onAvatarUriChanged(newAvatarUri: Uri?) {
        _registrationUiState.update { it.copy(avatarUri = newAvatarUri) }
    }

    fun setLoginError(newErrorMessage: String?) {
        _loginUiState.update { it.copy(processing = it.processing.copy(errorMessage = newErrorMessage)) }
    }

    fun setRegistrationError(newErrorMessage: String?) {
        _registrationUiState.update { it.copy(processing = it.processing.copy(errorMessage = newErrorMessage)) }
    }

    fun passwordsMatch() : Boolean {
        return registrationUiState.value.password === registrationUiState.value.confirmPassword
    }

    fun buildUser(registrationUiState: RegistrationUiState) : User {
        return User(
            uid = getCurrentUserId()!!,
            displayName = registrationUiState.displayName,
            email = registrationUiState.email,
            name = registrationUiState.name,
            surname = registrationUiState.surname,
            phoneNumber = registrationUiState.phoneNumber,
            avatarUrl = registrationUiState.avatarUri?.toString() ?: ""
        )
    }

    fun register() = viewModelScope.launch {
        _registrationUiState.update { it.copy(processing = it.processing.copy(isLoading = true)) }
        try {
            val uid = authRepository.register(
                _registrationUiState.value.email,
                _registrationUiState.value.password
            )
            _registrationUiState.update { it.copy(processing = it.processing.copy(isSuccess = true)) }
        } catch (e: Exception) {
            _registrationUiState.update {
                it.copy(processing = it.processing.copy(isSuccess = false, errorMessage = e.message))
            }
        } finally {
            _registrationUiState.update { it.copy(processing = it.processing.copy(isLoading = false)) }
        }
    }

    fun login() = viewModelScope.launch {
        _loginUiState.update { it.copy(processing = it.processing.copy(isLoading = true)) }
        try {
            val uid = authRepository.login(
                _loginUiState.value.email,
                _loginUiState.value.password
                )
            _loginUiState.update {
                it.copy(processing = it.processing.copy(isSuccess = true),
                    )
            }
        } catch (e: Exception) {
            _loginUiState.update {
                it.copy(processing = it.processing.copy(isSuccess = false, errorMessage = e.message))
            }
        } finally {
            _loginUiState.update { it.copy(processing = it.processing.copy(isLoading = false)) }
        }
    }

    fun logout() = authRepository.logout()

    fun getCurrentUserId() = authRepository.getCurrentUserId()
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}