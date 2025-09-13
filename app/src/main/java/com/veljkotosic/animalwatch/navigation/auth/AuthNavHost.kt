package com.veljkotosic.animalwatch.navigation.auth

import android.util.Log
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
import com.veljkotosic.animalwatch.log.Tags
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.viewmodel.auth.LoginViewModel
import com.veljkotosic.animalwatch.viewmodel.auth.RegistrationViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthNavHost(
    signedOut: Boolean,
    loginViewModel: LoginViewModel,
    registrationViewModel: RegistrationViewModel,
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit
) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    var startDestination by remember { mutableStateOf(Screens.Login.route) }

    var user = auth.currentUser

    // Provera kesiranog user-a
    LaunchedEffect(Unit) {
        if (!signedOut)
        {
            if (user != null) {
                try {
                    user.reload().await()
                    if (Firebase.auth.currentUser != null) {
                        onLoginSuccess()
                    }
                } catch (e: Exception) {
                    Log.e(Tags.AUTH_LOG_TAG, "Cannot find cached user.")
                }
            }
        }
    }

    AnimatedNavHost(navController = navController, startDestination = startDestination) {
        loginNavigation(
            navController,
            Screens.Login.route,
            loginViewModel,
            onLoginSuccess,
            600,
            FastOutSlowInEasing)
        registrationNavigation(
            navController,
            Screens.Register.route,
            registrationViewModel,
            userViewModel,
            600,
            FastOutSlowInEasing)
        registrationDoneNavigation(
            navController,
            Screens.RegistrationDone.route,
            600,
            FastOutSlowInEasing)
    }
}