package com.veljkotosic.animalwatch.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.veljkotosic.animalwatch.data.storage.StorageRepository
import com.veljkotosic.animalwatch.data.user.repository.UserRepository

class UserViewModelFactory(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userRepository, storageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}