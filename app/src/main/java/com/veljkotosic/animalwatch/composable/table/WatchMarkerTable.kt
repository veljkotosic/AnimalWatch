package com.veljkotosic.animalwatch.composable.table

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarker
import com.veljkotosic.animalwatch.ui.theme.Background

@Composable
fun WatchMarkerTable(
    markers: List<WatchMarker>,
    modifier: Modifier = Modifier,
    onSelect: (WatchMarker) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Background,
        title = null,
        text = {
            if (markers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No markers found",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .padding(vertical = 8.dp)
                ) {
                    items(markers) { marker ->
                        WatchMarkerTableItem(
                            watchMarker = marker,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp, vertical = 4.dp),
                            onClick = { onSelect(marker) }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
        modifier = modifier
    )
}