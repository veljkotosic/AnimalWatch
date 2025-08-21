package com.veljkotosic.animalwatch.data.storage

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class CloudinaryStorageRepository(
) : StorageRepository {
    override suspend fun uploadImage(publicId: String, uri: Uri, contentResolver: ContentResolver): String {
        return withContext(Dispatchers.IO) {
            val cloudName = "dboputs0e"
            val uploadPreset = "Avatar"

            val client = OkHttpClient()

            val inputStream = contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Invalid URI")
            val bytes = inputStream.readBytes()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "image.jpg", RequestBody.create(
                    "image/*".toMediaTypeOrNull(), bytes))
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val json = JSONObject(response.body?.string() ?: "{}")
            json.getString("secure_url")
        }
    }
}