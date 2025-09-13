package com.veljkotosic.animalwatch.composable.tag

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.veljkotosic.animalwatch.data.marker.entity.tag.WatchMarkerAnimalBehaviourTags
import com.veljkotosic.animalwatch.data.marker.entity.tag.WatchMarkerAnimalStateTags
import com.veljkotosic.animalwatch.data.marker.entity.tag.WatchMarkerAnimalTags
import com.veljkotosic.animalwatch.data.marker.entity.tag.WatchMarkerAnimalTypeTags
import com.veljkotosic.animalwatch.ui.theme.Background
import com.veljkotosic.animalwatch.ui.theme.Primary

@Composable
fun TagSelector(
    title: String,
    onTagSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        containerColor = Background,
        title = {
            Text(
                text = title,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column (
                modifier = modifier,
            ) {
                TagComboBox(
                    title = "Animal type",
                    tags = WatchMarkerAnimalTypeTags.All,
                    onTagSelected = {
                        onTagSelected(it)
                    }
                )

                TagComboBox(
                    title = "Animal",
                    tags = WatchMarkerAnimalTags.All,
                    onTagSelected = {
                        onTagSelected(it)
                    }
                )

                TagComboBox(
                    title = "Animal state",
                    tags = WatchMarkerAnimalStateTags.All,
                    onTagSelected = {
                        onTagSelected(it)
                    }
                )

                TagComboBox(
                    title = "Animal behaviour",
                    tags = WatchMarkerAnimalBehaviourTags.All,
                    onTagSelected = {
                        onTagSelected(it)
                    }
                )
            }
        },
        confirmButton = {}
    )
}