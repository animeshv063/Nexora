package com.example.shopping.presentation.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ProfilePhotoStorage {

    private const val PREFS_NAME = "profile_prefs"
    private const val KEY_PREFIX = "avatar_path_"

    /**
     * Saves the circular cropped bitmap directly into the app's internal private storage directory.
     * Guarantees 100% permission access without requiring any cloud rules, network, or external storage permissions.
     */
    fun saveProfileAvatarLocally(context: Context, uid: String, sourceUri: Uri): String? {
        return try {
            val circularUri = ImageCropUtils.createCircularCroppedImage(context, sourceUri) ?: sourceUri
            val inputStream: InputStream? = context.contentResolver.openInputStream(circularUri)
            if (inputStream == null) return null

            val profileFile = File(context.filesDir, "user_avatar_${uid}_${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(profileFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.flush()
            outputStream.close()

            val localPath = profileFile.absolutePath
            // Persist in SharedPreferences for instantaneous offline loading
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_PREFIX + uid, localPath).apply()

            localPath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves a cropped Bitmap directly to the app's internal private storage directory.
     */
    fun saveCircularBitmapLocally(context: Context, uid: String, bitmap: android.graphics.Bitmap): String? {
        return try {
            val profileFile = File(context.filesDir, "user_avatar_${uid}_${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(profileFile)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val localPath = profileFile.absolutePath
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_PREFIX + uid, localPath).apply()

            localPath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /**
     * Gets the locally saved profile avatar file path for the active user if present.
     */
    fun getLocalProfileAvatar(context: Context, uid: String): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val path = prefs.getString(KEY_PREFIX + uid, null)
        if (path != null && File(path).exists()) {
            return path
        }
        return null
    }

    /**
     * Removes the locally stored profile picture.
     */
    fun clearLocalProfileAvatar(context: Context, uid: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val path = prefs.getString(KEY_PREFIX + uid, null)
        if (path != null) {
            val file = File(path)
            if (file.exists()) file.delete()
        }
        prefs.edit().remove(KEY_PREFIX + uid).apply()
    }
}
