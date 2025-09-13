package com.veljkotosic.animalwatch.composable.marker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarker
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity

@Composable
fun AnimalWatchMarker(
    watchMarker: WatchMarker,
    modifier: Modifier = Modifier,
    onClick: (Marker) -> Boolean
) {
    Marker (
        state = MarkerState(position = LatLng(
            watchMarker.position.latitude,
            watchMarker.position.longitude
        )
        ),
        title = watchMarker.title,
        snippet = watchMarker.description,
        icon = BitmapDescriptorFactory.defaultMarker(when (watchMarker.severity) {
            WatchMarkerSeverity.Info -> BitmapDescriptorFactory.HUE_AZURE
            WatchMarkerSeverity.Warning -> BitmapDescriptorFactory.HUE_YELLOW
            WatchMarkerSeverity.Danger -> BitmapDescriptorFactory.HUE_RED
            WatchMarkerSeverity.Undefined -> BitmapDescriptorFactory.HUE_GREEN
        }),
        onClick = onClick
    ) {

    }
}