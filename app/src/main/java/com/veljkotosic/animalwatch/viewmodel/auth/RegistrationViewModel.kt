package com.veljkotosic.animalwatch.viewmodel.auth

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veljkotosic.animalwatch.data.auth.repository.AuthRepository
import com.veljkotosic.animalwatch.data.user.entity.User
import com.veljkotosic.animalwatch.log.Tags
import com.veljkotosic.animalwatch.viewmodel.auth.uistate.RegistrationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _registrationUiState = MutableStateFlow(RegistrationUiState())
    val registrationUiState: StateFlow<RegistrationUiState> = _registrationUiState.asStateFlow()

    private val _newUserUid = MutableStateFlow<String?>(null)
    val newUserUid: StateFlow<String?> = _newUserUid.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _registrationUiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChanged(newPassword: String) {
        _registrationUiState.update { it.copy(password = newPassword) }
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _registrationUiState.update { it.copy(confirmPassword = newConfirmPassword) }
    }

    fun onNameChanged(newName: String) {
        _registrationUiState.update { it.copy(name = newName) }
    }

    fun onSurnameChanged(newSurname: String) {
        _registrationUiState.update { it.copy(surname = newSurname) }
    }

    fun onDisplayNameChanged(newDisplayName: String) {
        _registrationUiState.update { it.copy(displayName = newDisplayName) }
    }

    fun onPhoneNumberChanged(newPhoneNumber: String) {
        _registrationUiState.update { it.copy(phoneNumber = newPhoneNumber) }
    }

    fun onAvatarUriChanged(newAvatarUri: Uri?) {
        _registrationUiState.update { it.copy(avatarUri = newAvatarUri) }
    }

    fun setError(newErrorMessage: String?) {
        _registrationUiState.update { it.copy(processing = it.processing.copy(errorMessage = newErrorMessage)) }
    }

    fun passwordsMatch() : Boolean {
        return registrationUiState.value.password === registrationUiState.value.confirmPassword
    }

    fun buildUser(registrationUiState: RegistrationUiState, uid: String) : User {
        return User(
            uid = uid,
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
            Log.e(Tags.AUTH_LOG_TAG, "Registration failed.")
        } finally {
            _registrationUiState.update { it.copy(processing = it.processing.copy(isLoading = false)) }
        }
    }
}