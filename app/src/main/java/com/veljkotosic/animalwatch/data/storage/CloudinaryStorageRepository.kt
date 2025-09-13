package com.veljkotosic.animalwatch.data.storage

import android.content.Context
import android.net.Uri
import com.veljkotosic.animalwatch.secret.AppSecret
import com.veljkotosic.animalwatch.secret.GetSecret
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
    private suspend fun uploadImage(
        publicId: String,
        uri: Uri,
        cloudName: String,
        uploadPreset: String?,
        context: Context
    ): String {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Invalid URI")
            val bytes = inputStream.readBytes()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.Companion.FORM)
                .addFormDataPart(
                    "file", "image.jpg", RequestBody.Companion.create(
                        "image/*".toMediaTypeOrNull(), bytes
                    )
                )
                .addFormDataPart("upload_preset", uploadPreset!!)
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

    override suspend fun uploadAvatarImage(
        publicId: String,
        uri: Uri,
        context: Context
    ): String {
        return uploadImage(
            publicId = publicId,
            uri = uri,
            cloudName = GetSecret(context, AppSecret.Cloudinary.cloudName)!!,
            uploadPreset = GetSecret(context, AppSecret.Cloudinary.avatarUploadPreset),
            context = context
        )
    }

    override suspend fun uploadMarkerImage(
        publicId: String,
        uri: Uri,
        context: Context
    ): String {
        return uploadImage(
            publicId = publicId,
            uri = uri,
            cloudName = GetSecret(context, AppSecret.Cloudinary.cloudName)!!,
            uploadPreset = GetSecret(context, AppSecret.Cloudinary.markerUploadPreset),
            context = context
        )
    }
}