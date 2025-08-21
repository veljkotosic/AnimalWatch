package com.veljkotosic.animalwatch.navigation.auth

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.veljkotosic.animalwatch.screen.auth.LoginScreen
import com.veljkotosic.animalwatch.viewmodel.auth.AuthViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel

fun NavGraphBuilder.LoginNavigation(
    navController: NavController,
    route: String,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    duration: Int,
    easing: Easing){
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
        LoginScreen(navController, authViewModel = authViewModel, userViewModel = userViewModel)
    }
}