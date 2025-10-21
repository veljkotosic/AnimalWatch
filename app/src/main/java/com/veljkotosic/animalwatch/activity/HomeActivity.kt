package com.veljkotosic.animalwatch.activity

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.veljkotosic.animalwatch.data.auth.repository.FireBaseAuthRepository
import com.veljkotosic.animalwatch.data.marker.repository.FirestoreWatchMarkerRepository
import com.veljkotosic.animalwatch.data.stat.repository.FirestoreUserStatsRepository
import com.veljkotosic.animalwatch.data.storage.CloudinaryStorageRepository
import com.veljkotosic.animalwatch.data.user.repository.FirestoreUserRepository
import com.veljkotosic.animalwatch.navigation.home.HomeNavHost
import com.veljkotosic.animalwatch.service.NearbyMarkersLookUpService
import com.veljkotosic.animalwatch.ui.theme.AnimalWatchTheme
import com.veljkotosic.animalwatch.utility.service.isInternetConnectionEnabled
import com.veljkotosic.animalwatch.utility.service.isLocationEnabled
import com.veljkotosic.animalwatch.viewmodel.leaderboard.LeaderboardViewModel
import com.veljkotosic.animalwatch.viewmodel.leaderboard.LeaderboardViewModelFactory
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModelFactory
import com.veljkotosic.animalwatch.viewmodel.profile.ProfileViewModel
import com.veljkotosic.animalwatch.viewmodel.profile.ProfileViewModelFactory
import kotlin.system.exitProcess

class HomeActivity : ComponentActivity() {
    private val connectionManager by lazy {
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            showExitDialog("Internet connection is required for the app to function properly")
        }
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                if (isLocationEnabled(this@HomeActivity)) {
                    showExitDialog("Location service must be enabled for the app to function")
                }
            }
        }
    }

    private val mapViewModel: MapViewModel by viewModels {
        MapViewModelFactory(
            FireBaseAuthRepository(),
            FirestoreUserRepository(),
            FirestoreWatchMarkerRepository(),
            CloudinaryStorageRepository(),
            LocationServices.getFusedLocationProviderClient(this)
        )
    }

    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(FireBaseAuthRepository())
    }

    private val leaderboardViewModel: LeaderboardViewModel by viewModels {
        LeaderboardViewModelFactory(
            FirestoreUserStatsRepository(),
            FirestoreUserRepository()
        )
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

    @SuppressWarnings("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.FOREGROUND_SERVICE), 200)
        }

        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)

        enableEdgeToEdge()
        setContent {
            AnimalWatchTheme {
                HomeNavHost(
                    mapViewModel = mapViewModel,
                    profileViewModel = profileViewModel,
                    leaderboardViewModel = leaderboardViewModel,
                    onSignOut = {
                        val intent = Intent(this, AuthActivity::class.java)
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapViewModel.startLocationUpdates()
        }

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(locationReceiver, filter)
    }

    override fun onStop() {
        super.onStop()

        mapViewModel.saveUserLastLocation()

        connectionManager.unregisterNetworkCallback(networkCallback)
        unregisterReceiver(locationReceiver)
    }

    override fun onPause() {
        super.onPause()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                val notificationIntent = Intent(this, NearbyMarkersLookUpService::class.java)
                startForegroundService(notificationIntent)
            }
        } else {
            val notificationIntent = Intent(this, NearbyMarkersLookUpService::class.java)
            startForegroundService(notificationIntent)
        }

        mapViewModel.stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()

        if (!isLocationEnabled(this)) {
            showExitDialog("Location service must be enabled for the app to function")
        }
        if (!isInternetConnectionEnabled(this)) {
            showExitDialog("Internet connection is required for the app to function properly")
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapViewModel.startLocationUpdates()
        }

        stopService(Intent(this, NearbyMarkersLookUpService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, NearbyMarkersLookUpService::class.java))
    }

    @SuppressWarnings("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapViewModel.startLocationUpdates()
        }
    }
}