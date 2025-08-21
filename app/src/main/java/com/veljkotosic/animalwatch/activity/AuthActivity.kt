package com.veljkotosic.animalwatch.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.firebase.FirebaseApp
import com.veljkotosic.animalwatch.data.auth.repository.FireBaseAuthRepository
import com.veljkotosic.animalwatch.data.storage.CloudinaryStorageRepository
import com.veljkotosic.animalwatch.data.user.repository.FirestoreUserRepository
import com.veljkotosic.animalwatch.navigation.auth.AuthNavHost
import com.veljkotosic.animalwatch.ui.theme.AnimalWatchTheme
import com.veljkotosic.animalwatch.viewmodel.auth.AuthViewModel
import com.veljkotosic.animalwatch.viewmodel.auth.AuthViewModelFactory
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModelFactory
import kotlin.getValue

class AuthActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(FireBaseAuthRepository())
    }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(FirestoreUserRepository(), CloudinaryStorageRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        setContent {
            AnimalWatchTheme {
                AuthNavHost(authViewModel = authViewModel, userViewModel = userViewModel)
            }
        }
    }
}