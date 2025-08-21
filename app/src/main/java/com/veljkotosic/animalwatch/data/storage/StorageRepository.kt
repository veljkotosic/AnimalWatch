package com.veljkotosic.animalwatch.data.storage

import android.content.ContentResolver
import android.net.Uri
import java.io.File

interface StorageRepository {
    suspend fun uploadImage(publicId: String, uri: Uri, contentResolver: ContentResolver) : String
}