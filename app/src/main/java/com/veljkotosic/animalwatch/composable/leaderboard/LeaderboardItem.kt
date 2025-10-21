package com.veljkotosic.animalwatch.composable.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veljkotosic.animalwatch.data.stat.entity.UserStats

@Composable
fun LeaderBoardItem(
    userStats: UserStats,
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent,
    itemHeight: Dp = 48.dp,
    fontSize: TextUnit = 16.sp,
    onClick: (() -> Unit)? = null
) {
    val borderColor = if (color != Color.Transparent) color else MaterialTheme.colorScheme.outline
    Row(
        modifier = modifier
            .height(itemHeight)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = userStats.username,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${userStats.total}",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}