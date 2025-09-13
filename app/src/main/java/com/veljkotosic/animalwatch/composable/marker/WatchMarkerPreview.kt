package com.veljkotosic.animalwatch.composable.marker

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.veljkotosic.animalwatch.R
import com.veljkotosic.animalwatch.composable.SeverityLabel
import com.veljkotosic.animalwatch.composable.YesNoDialog
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarker
import com.veljkotosic.animalwatch.ui.theme.Background
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun WatchMarkerPreview(
    modifier: Modifier = Modifier,
    watchMarker: WatchMarker,
    mapViewModel: MapViewModel,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxHeight = screenHeight * 0.3f

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val mapUiState by mapViewModel.mapUiState.collectAsState()

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = Background,
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column (
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .background(Background, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Row (
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = watchMarker.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )

                SeverityLabel(watchMarkerSeverity = watchMarker.severity)
            }

            SubcomposeAsyncImage(
                model = watchMarker.imageUri,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                error = {
                    Image(painterResource(R.drawable.no_image_available), contentDescription = "Error")
                },
                loading = {
                    CircularProgressIndicator()
                },
                modifier = Modifier
                    .padding(4.dp)
                    .heightIn(max = maxHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(12.dp))
                    .fillMaxWidth()
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                watchMarker.tags.forEach { tag ->
                    Text(
                        text = tag,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            Text(
                text = watchMarker.description,
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            if (mapViewModel.ownsMarker()) {
                Button(
                    onClick = {
                        mapViewModel.promptMarkerDelete()
                    }
                ) {
                    Text("Delete marker")
                }
            }

            if (mapUiState.confirmDeleteAlertOpen) {
                YesNoDialog(
                    title = {
                        Text("Delete marker")
                    },
                    text = {
                        Text("Are you sure you want to delete this marker?")
                    },
                    yesText = {
                        Text("Delete")
                    },
                    noText = {
                        Text("Cancel")
                    },
                    onYes = {
                        mapViewModel.removeMarker()
                    },
                    onNo = {
                        mapViewModel.closeMarkerDeletePrompt()
                    },
                    onDismiss = {
                        mapViewModel.closeMarkerDeletePrompt()
                    }
                )
            }
        }
    }
}