package com.veljkotosic.animalwatch.navigation.auth

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.viewmodel.auth.AuthViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthNavHost(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
    ){
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    var startDestination by remember { mutableStateOf(Screens.Login.route) }
    var user = auth.currentUser

    LaunchedEffect(Unit) {
        if (user != null) {
            try {
                user.reload().await()
                if (Firebase.auth.currentUser != null) {
                    startDestination = Screens.Register.route
                }
            } catch (e: Exception) {

            }
        }
    }

    AnimatedNavHost(navController = navController, startDestination = startDestination) {
        LoginNavigation(navController, Screens.Login.route, authViewModel = authViewModel, userViewModel = userViewModel, 600, FastOutSlowInEasing)
        RegisterNavigation(navController, Screens.Register.route, authViewModel = authViewModel, userViewModel = userViewModel, 600, FastOutSlowInEasing)
        RegistrationDoneNavigation(navController, Screens.RegistrationDone.route, 600, FastOutSlowInEasing)
    }
}