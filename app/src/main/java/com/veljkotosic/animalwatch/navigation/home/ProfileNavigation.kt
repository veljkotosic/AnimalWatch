package com.veljkotosic.animalwatch.navigation.home

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.veljkotosic.animalwatch.screen.home.ProfileScreen
import com.veljkotosic.animalwatch.viewmodel.profile.ProfileViewModel

fun NavGraphBuilder.profileNavigation (
    navController: NavController,
    profileViewModel: ProfileViewModel,
    route: String,
    onSignOut: () -> Unit,
    duration: Int,
    easing: Easing
) {
    composable(
        route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(duration, easing = easing)
            )},
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(duration, easing = easing)
            )},
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(duration, easing = easing)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(duration, easing = easing)
            )
        }
    ) {
        ProfileScreen(navController, profileViewModel, onSignOut)
    }
}