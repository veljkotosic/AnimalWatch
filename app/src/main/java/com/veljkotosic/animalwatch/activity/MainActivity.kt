package com.veljkotosic.animalwatch.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.firebase.auth.FirebaseAuth
import com.veljkotosic.animalwatch.navigation.auth.LoginNavigation
import com.veljkotosic.animalwatch.navigation.auth.RegisterNavigation
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.ui.theme.AnimalWatchTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

        }
    }
}