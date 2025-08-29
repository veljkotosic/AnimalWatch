package com.veljkotosic.animalwatch.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.veljkotosic.animalwatch.data.auth.repository.FireBaseAuthRepository
import com.veljkotosic.animalwatch.data.storage.CloudinaryStorageRepository
import com.veljkotosic.animalwatch.data.user.repository.FirestoreUserRepository
import com.veljkotosic.animalwatch.navigation.auth.AuthNavHost
import com.veljkotosic.animalwatch.ui.theme.AnimalWatchTheme
import com.veljkotosic.animalwatch.viewmodel.auth.AuthViewModelFactory
import com.veljkotosic.animalwatch.viewmodel.auth.LoginViewModel
import com.veljkotosic.animalwatch.viewmodel.auth.RegistrationViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModelFactory
import kotlin.getValue

class AuthActivity : ComponentActivity() {
    private val authViewModelFactory = AuthViewModelFactory(FireBaseAuthRepository())

    private val loginViewModel: LoginViewModel by viewModels { authViewModelFactory }
    private val registrationViewModel: RegistrationViewModel by viewModels { authViewModelFactory }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(
            FirestoreUserRepository(),
            CloudinaryStorageRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signedOut = intent.getBooleanExtra("signedOut", false)

        enableEdgeToEdge()

        setContent {
            AnimalWatchTheme {
                AuthNavHost(
                    signedOut = signedOut,
                    loginViewModel = loginViewModel,
                    registrationViewModel = registrationViewModel,
                    userViewModel = userViewModel,
                    onLoginSuccess = {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    })
            }
        }
    }
}