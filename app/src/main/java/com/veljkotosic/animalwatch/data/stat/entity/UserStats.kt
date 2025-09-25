package com.veljkotosic.animalwatch.data.stat.entity

data class UserStats(
    val username: String = "",
    val markersCreatedCount: Int = 0,
    val markersUpdatedCount: Int = 0,
    val totalAppraisals: Int = 0,
    val total: Int = 0
)
