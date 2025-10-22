package com.veljkotosic.animalwatch.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veljkotosic.animalwatch.data.auth.repository.AuthRepository
import com.veljkotosic.animalwatch.data.user.entity.User
import com.veljkotosic.animalwatch.data.user.repository.UserRepository
import com.veljkotosic.animalwatch.utility.toLatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel (
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    fun signOut() = viewModelScope.launch {
        authRepository.signOut()
    }

    fun loadUser() {
        val uid = authRepository.getCurrentUserId()
        if (uid != null) {
            viewModelScope.launch {
                _userState.value = userRepository.getUser(uid)
            }
        }
    }

    init {
        loadUser()
    }
}