package com.veljkotosic.animalwatch.navigation.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.ui.theme.AnimalWatchTheme
import com.veljkotosic.animalwatch.ui.theme.Background
import com.veljkotosic.animalwatch.ui.theme.Primary
import com.veljkotosic.animalwatch.ui.theme.Secondary
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeNavHost(
    mapViewModel: MapViewModel,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(
        Screens.Profile,
        Screens.Map,
        Screens.Leaderboard
    )

    val tabIcons = listOf(
        Icons.Filled.Person,
        Icons.Filled.Map,
        Icons.Filled.Leaderboard
    )

    val startDestination by remember { mutableStateOf(Screens.Map.route) }

    AnimalWatchTheme {
        Scaffold(
            bottomBar = {
                NavigationBar (
                    containerColor = Primary,
                    contentColor = Secondary,
                    modifier = Modifier.height(96.dp)
                ) {
                    tabs.forEachIndexed { index, screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(imageVector = tabIcons[index], contentDescription = screen.route)
                            },
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(screen.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.drawWithContent {
                                drawContent()
                                if (selected) {
                                    drawRect(
                                        color = Background,
                                        topLeft = Offset(0f, 0f),
                                        size = Size(size.width, 4.dp.toPx())
                                    )
                                }
                            }
                        )
                    }
                }
            }
        ) {
            paddingValues -> AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
            ) {
                profileNavigation(navController, Screens.Profile.route, onSignOut, 600, FastOutSlowInEasing)
                mapNavigation(navController, mapViewModel, Screens.Map.route, 600, FastOutSlowInEasing)
                leaderboardNavigation(navController, Screens.Leaderboard.route, 600, FastOutSlowInEasing)
            }
        }
    }
}