package com.veljkotosic.animalwatch.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import com.veljkotosic.animalwatch.data.storage.CloudinaryStorageRepository
import com.veljkotosic.animalwatch.data.user.repository.FirestoreUserRepository
import com.veljkotosic.animalwatch.navigation.home.HomeNavHost
import com.veljkotosic.animalwatch.ui.theme.AnimalWatchTheme
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModelFactory
import kotlin.getValue

class HomeActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(
            FirestoreUserRepository(),
            CloudinaryStorageRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimalWatchTheme {
                HomeNavHost(
                    userViewModel = userViewModel,
                    onSignOut = {
                        val intent = Intent(this, AuthActivity::class.java)
                        intent.putExtra("signedOut", true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    })
            }
        }
    }
}