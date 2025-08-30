package com.veljkotosic.animalwatch.secret

import android.content.Context
import android.content.pm.PackageManager

fun GetSecret(context: Context, name: String) : String? {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        appInfo.metaData?.getString(name)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}

sealed class AppSecret {
    object Cloudinary {
        const val cloudName: String = "com.veljkotosic.animalwatch.cloudinary.CLOUD_NAME"
        const val avatarUploadPreset: String = "com.veljkotosic.animalwatch.cloudinary.AVATAR_UPLOAD_PRESET"
        const val markerUploadPreset: String = "com.veljkotosic.animalwatch.cloudinary.MARKER_UPLOAD_PRESET"
    }
}