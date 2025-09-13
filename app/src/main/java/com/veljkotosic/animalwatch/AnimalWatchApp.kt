package com.veljkotosic.animalwatch

import android.app.Application
import com.google.firebase.FirebaseApp

class AnimalWatchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}