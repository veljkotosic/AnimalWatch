package com.veljkotosic.animalwatch.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity
import com.veljkotosic.animalwatch.ui.theme.Azure

@Composable
fun SeverityLabel(
    modifier: Modifier = Modifier,
    watchMarkerSeverity: WatchMarkerSeverity
) {
    val color: Color
    val text: String

    when (watchMarkerSeverity) {
        WatchMarkerSeverity.Info -> {
            color = Azure
            text = "Info"
        }
        WatchMarkerSeverity.Warning -> {
            color = Color.Yellow
            text = "Warning"
        }
        WatchMarkerSeverity.Danger -> {
            color = Color.Red
            text = "Danger"
        }
        WatchMarkerSeverity.Undefined -> {
            color = Color.Transparent
            text = ""
        }
    }


    Text(
        text = text,
        color = Color.DarkGray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color, RoundedCornerShape(16.dp))
            .border(BorderStroke(2.dp, Color.DarkGray), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}