package com.veljkotosic.animalwatch.activity

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.LocationServices
import com.veljkotosic.animalwatch.R
import com.veljkotosic.animalwatch.data.auth.repository.FireBaseAuthRepository
import com.veljkotosic.animalwatch.data.marker.repository.FirestoreWatchMarkerRepository
import com.veljkotosic.animalwatch.data.storage.CloudinaryStorageRepository
import com.veljkotosic.animalwatch.data.user.repository.FirestoreUserRepository
import com.veljkotosic.animalwatch.navigation.home.HomeNavHost
import com.veljkotosic.animalwatch.ui.theme.AnimalWatchTheme
import com.veljkotosic.animalwatch.utility.service.isInternetConnectionEnabled
import com.veljkotosic.animalwatch.utility.service.isLocationEnabled
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModelFactory
import kotlin.system.exitProcess

class HomeActivity : ComponentActivity() {
    private val CHANNEL_ID = "MARKER_UPDATE_CHANNEL_2"
    private val CHANNEL_NAME = "Marker Updates"

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
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 200)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingNotificationIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        mapViewModel.notificationEvent.observe(this) { count ->
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New markers available")
                .setContentText("There are $count new markers near you!")
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingNotificationIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(this).notify(1, notification)
        }

        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)

        enableEdgeToEdge()
        setContent {
            AnimalWatchTheme {
                HomeNavHost(
                    mapViewModel = mapViewModel,
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

    override fun onStart() {
        super.onStart()

        connectionManager.registerDefaultNetworkCallback(networkCallback)

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(locationReceiver, filter)
    }

    override fun onStop() {
        super.onStop()

        mapViewModel.saveUserLastLocation()

        connectionManager.unregisterNetworkCallback(networkCallback)
        unregisterReceiver(locationReceiver)
    }

    override fun onResume() {
        super.onResume()

        if (!isLocationEnabled(this)) {
            showExitDialog("Location service must be enabled for the app to function")
        }
        if (!isInternetConnectionEnabled(this)) {
            showExitDialog("Internet connection is required for the app to function properly")
        }
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