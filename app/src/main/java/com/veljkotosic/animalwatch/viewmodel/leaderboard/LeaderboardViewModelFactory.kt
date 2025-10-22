package com.veljkotosic.animalwatch.viewmodel.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.veljkotosic.animalwatch.data.stat.repository.FirestoreUserStatsRepository
import com.veljkotosic.animalwatch.data.user.repository.FirestoreUserRepository

class LeaderboardViewModelFactory (
    private val userStatsRepository: FirestoreUserStatsRepository,
    private val userRepository: FirestoreUserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            return LeaderboardViewModel(userStatsRepository, userRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}