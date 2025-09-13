package com.veljkotosic.animalwatch.data.storage

import android.content.Context
import android.net.Uri

interface StorageRepository {
    suspend fun uploadAvatarImage(publicId: String, uri: Uri, context: Context) : String

    suspend fun uploadMarkerImage(publicId: String, uri: Uri, context: Context) : String
}