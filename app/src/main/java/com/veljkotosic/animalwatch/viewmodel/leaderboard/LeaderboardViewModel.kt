package com.veljkotosic.animalwatch.viewmodel.leaderboard

import androidx.lifecycle.ViewModel
import com.veljkotosic.animalwatch.data.stat.entity.UserStats
import com.veljkotosic.animalwatch.data.stat.repository.FirestoreUserStatsRepository
import com.veljkotosic.animalwatch.data.user.repository.FirestoreUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaderboardViewModel (
    private val userStatsRepository: FirestoreUserStatsRepository,
    private val userRepository: FirestoreUserRepository
) : ViewModel() {
    private val _topTen = MutableStateFlow<List<UserStats>>(emptyList())
    val topTen: StateFlow<List<UserStats>> = _topTen.asStateFlow()

    fun startObservingTopTen() {
        userStatsRepository.observeTopTen { _topTen.value = it }
    }

    fun stopObservingTopTen() {
        userStatsRepository.stopObservingTopTen()
    }

    init {
        startObservingTopTen()
    }

    override fun onCleared() {
        super.onCleared()
        stopObservingTopTen()
    }
}