package com.veljkotosic.animalwatch.uistate.map

import android.net.Uri
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity

data class WatchMarkerUiState(
    val title: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val severity: WatchMarkerSeverity = WatchMarkerSeverity.Undefined,
    val imageUri: Uri? = null,

    val userRequestedCamera: Boolean = false
)
