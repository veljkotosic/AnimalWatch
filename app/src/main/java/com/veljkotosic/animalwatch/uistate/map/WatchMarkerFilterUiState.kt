package com.veljkotosic.animalwatch.uistate.map

import com.google.firebase.Timestamp
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity
import com.veljkotosic.animalwatch.utility.takeDays

data class WatchMarkerFilterUiState(
    val severities: List<WatchMarkerSeverity> = listOf(),
    val owner: String = "",
    val tags: List<String> = listOf(),
    val createdAfter: Timestamp = Timestamp.now().takeDays(7),
    val createdBefore: Timestamp = Timestamp.now()
)
