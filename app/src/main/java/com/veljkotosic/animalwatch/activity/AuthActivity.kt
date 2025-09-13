package com.veljkotosic.animalwatch.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
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
import com.veljkotosic.animalwatch.utility.service.isInternetConnectionEnabled
import com.veljkotosic.animalwatch.viewmodel.auth.AuthViewModelFactory
import com.veljkotosic.animalwatch.viewmodel.auth.LoginViewModel
import com.veljkotosic.animalwatch.viewmodel.auth.RegistrationViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModelFactory
import kotlin.system.exitProcess

class AuthActivity : ComponentActivity() {
    private val connectionManager by lazy {
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            showExitDialog("Internet connection is required for the app to function properly")
        }
    }

    private fun showExitDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("App closing")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                finishAffinity()
                exitProcess(0)
            }
            .show()
    }

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

    override fun onStart() {
        super.onStart()

        connectionManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onStop() {
        super.onStop()

        connectionManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onResume() {
        super.onResume()

        if (!isInternetConnectionEnabled(this)) {
            showExitDialog("Internet connection is required for the app to function properly")
        }
    }
}