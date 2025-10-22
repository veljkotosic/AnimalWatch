package com.veljkotosic.animalwatch.service

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.veljkotosic.animalwatch.R
import com.veljkotosic.animalwatch.activity.HomeActivity
import com.veljkotosic.animalwatch.data.marker.repository.FirestoreWatchMarkerRepository
import com.veljkotosic.animalwatch.notification.NotificationChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NearbyMarkersLookUpService : Service() {
    private val notificationId = 123456
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val markerRepository by lazy {
        FirestoreWatchMarkerRepository()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate() {
        super.onCreate()
        startForeground(notificationId, createNotification())
        startLookUp()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLookUp() {
        scope.launch {
            while (isActive) {
                lookUp()
                delay(5 * 60 * 1000)
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun lookUp() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    markerRepository.getMarkerCountInArea(
                        location.latitude,
                        location.longitude,
                        200.0) { result ->
                        result.onSuccess { count ->
                            if (count > 0) {
                                updateNotification("There are $count new markers near you!")
                            } else {
                                updateNotification("No markers nearby.")
                            }
                        }.onFailure {

                        }
                    }
                } else {
                    updateNotification("Cannot get device location")
                }
            }.addOnFailureListener {
                updateNotification("Cannot get device location")
            }
    }

    private fun updateNotification(message: String) {
        val manager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingNotificationIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, NotificationChannels.NEARBY_MARKERS)
            .setContentTitle("Markers in your area")
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingNotificationIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(notificationId, notification)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NotificationChannels.NEARBY_MARKERS)
            .setContentTitle("Markers in your area")
            .setContentText("Scanning for nearby markers...")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
}