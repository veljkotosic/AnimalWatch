package com.veljkotosic.animalwatch.viewmodel.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.veljkotosic.animalwatch.data.auth.repository.AuthRepository
import com.veljkotosic.animalwatch.data.marker.repository.WatchMarkerRepository
import com.veljkotosic.animalwatch.data.storage.StorageRepository
import com.veljkotosic.animalwatch.data.user.repository.UserRepository

class MapViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val markerRepository: WatchMarkerRepository,
    private val storageRepository: StorageRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(authRepository, userRepository, markerRepository,
                storageRepository, fusedLocationClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}