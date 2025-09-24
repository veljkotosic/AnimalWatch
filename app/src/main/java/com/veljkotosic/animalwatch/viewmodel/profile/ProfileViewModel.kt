package com.veljkotosic.animalwatch.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veljkotosic.animalwatch.data.auth.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {
    fun signOut() = viewModelScope.launch {
        authRepository.signOut()
    }
}