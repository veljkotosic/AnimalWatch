package com.veljkotosic.animalwatch.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.veljkotosic.animalwatch.composable.leaderboard.LeaderBoardItem
import com.veljkotosic.animalwatch.viewmodel.leaderboard.LeaderboardViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veljkotosic.animalwatch.ui.theme.Primary

@Composable
fun LeaderboardScreen(
    navController: NavController,
    leaderboardViewModel: LeaderboardViewModel
) {
    val topTen by leaderboardViewModel.topTen.collectAsState()

    val gold = Color(0xFFFFD700)
    val silver = Color(0xFFC0C0C0)
    val bronze = Color(0xFFCD7F32)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Top 10 contributors",
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
            textAlign = TextAlign.Center,
            color = Primary
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(topTen) { index, userStats ->
                val (outlineColor, itemHeight, fontSize) = when (index) {
                    0 -> Triple(gold, 72.dp, 22.sp)
                    1 -> Triple(silver, 64.dp, 20.sp)
                    2 -> Triple(bronze, 56.dp, 18.sp)
                    else -> Triple(Color.Transparent, 48.dp, 16.sp)
                }
                LeaderBoardItem(
                    userStats = userStats,
                    modifier = Modifier.fillMaxWidth(),
                    color = outlineColor,
                    itemHeight = itemHeight,
                    fontSize = fontSize
                )
            }
        }
    }
}