package com.veljkotosic.animalwatch.data.stat.repository

import com.veljkotosic.animalwatch.data.stat.entity.UserStats

interface UserStatsRepository {
    suspend fun getTopTen(onDone: (List<UserStats>) -> Unit)
    fun observeTopTen(onDone: (List<UserStats>) -> Unit)
    fun stopObservingTopTen()
    suspend fun getStats(uid: String) : UserStats
}