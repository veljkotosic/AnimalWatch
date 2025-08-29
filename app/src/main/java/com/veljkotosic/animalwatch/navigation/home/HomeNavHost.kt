package com.veljkotosic.animalwatch.navigation.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeNavHost(
    userViewModel: UserViewModel,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()

    val startDestination by remember { mutableStateOf(Screens.Home.route) }

    AnimatedNavHost(navController = navController, startDestination = startDestination) {
        HomeNavigation(navController, Screens.Home.route,
            userViewModel, onSignOut, 600, FastOutSlowInEasing)
    }
}