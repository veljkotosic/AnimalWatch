package com.veljkotosic.animalwatch.viewmodel.user

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.veljkotosic.animalwatch.data.storage.StorageRepository
import com.veljkotosic.animalwatch.data.user.entity.User
import com.veljkotosic.animalwatch.data.user.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    fun createUser(user: User, avatarUri: Uri, context: Context) =
        viewModelScope.launch {
            val publicAvatarUri =
                storageRepository.uploadAvatarImage(user.uid, avatarUri, context)
            userRepository.createUser(user, publicAvatarUri)
            _userState.value = user
        }

    fun getUser(uid: String) = viewModelScope.launch {
        _userState.value = userRepository.getUser(uid)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        userRepository.updateUser(user)
        _userState.value = user
    }

    fun deleteUser(uid: String) = viewModelScope.launch {
        userRepository.deleteUser(uid)
        _userState.value = null
    }
}