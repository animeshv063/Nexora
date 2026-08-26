package com.example.shopping.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.util.Base64
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

object ImageCropUtils {

    /**
     * Loads a Bitmap from a Web URL, Base64 Data URI, or Firebase Storage URL using Coil / decode.
     */
    suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = sanitizeImageUrl(url)
            if (cleanUrl.startsWith("data:image", ignoreCase = true)) {
                return@withContext base64ToBitmap(cleanUrl)
            }
            val loader = context.imageLoader
            val request = ImageRequest.Builder(context)
                .data(cleanUrl)
                .allowHardware(false) // Must be software bitmap for pixel operations
                .build()

            val result = (loader.execute(request) as? SuccessResult)?.drawable
            (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Reads the image from the given Uri, crops it to an exact circle,
     * compresses it, saves to cache, and returns the new local file Uri.
     */
    fun createCircularCroppedImage(context: Context, sourceUri: Uri): Uri? {
        return try {
            val bmp = loadBitmap(context, sourceUri) ?: return null
            val minEdge = min(bmp.width, bmp.height)
            val defaultRect = Rect((bmp.width - minEdge) / 2, (bmp.height - minEdge) / 2, (bmp.width + minEdge) / 2, (bmp.height + minEdge) / 2)
            val circular = cropCircularBitmap(bmp, defaultRect)
            saveBitmapToCache(context, circular, "avatar_circular")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Loads the original bitmap from the source URI safely.
     */
    fun loadBitmap(context: Context, sourceUri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Crops a user-selected sub-rectangle of the original bitmap into a rectangular image (for products & banners).
     */
    fun cropRectangularBitmap(
        originalBitmap: Bitmap,
        cropRect: Rect,
        targetWidth: Int = 600,
        targetHeight: Int = 600
    ): Bitmap {
        val safeLeft = cropRect.left.coerceIn(0, originalBitmap.width - 1)
        val safeTop = cropRect.top.coerceIn(0, originalBitmap.height - 1)
        val safeWidth = cropRect.width().coerceIn(1, originalBitmap.width - safeLeft)
        val safeHeight = cropRect.height().coerceIn(1, originalBitmap.height - safeTop)

        val subBitmap = Bitmap.createBitmap(originalBitmap, safeLeft, safeTop, safeWidth, safeHeight)

        return if (targetWidth > 0 && targetHeight > 0) {
            Bitmap.createScaledBitmap(subBitmap, targetWidth, targetHeight, true)
        } else {
            subBitmap
        }
    }

    /**
     * Crops a user-selected sub-rectangle of the original bitmap into an exact circular avatar.
     */
    fun cropCircularBitmap(
        originalBitmap: Bitmap,
        cropRect: Rect
    ): Bitmap {
        val safeLeft = cropRect.left.coerceIn(0, originalBitmap.width - 1)
        val safeTop = cropRect.top.coerceIn(0, originalBitmap.height - 1)
        val safeWidth = cropRect.width().coerceIn(1, originalBitmap.width - safeLeft)
        val safeHeight = cropRect.height().coerceIn(1, originalBitmap.height - safeTop)
        val size = min(safeWidth, safeHeight)

        val subBitmap = Bitmap.createBitmap(originalBitmap, safeLeft, safeTop, size, size)

        val targetSize = min(size, 400)
        val scaled = if (size != targetSize) {
            Bitmap.createScaledBitmap(subBitmap, targetSize, targetSize, true)
        } else {
            subBitmap
        }

        val output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
            color = 0xff424242.toInt()
        }
        val destRect = Rect(0, 0, targetSize, targetSize)
        val destRectF = RectF(destRect)

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(destRectF, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(scaled, destRect, destRect, paint)

        return output
    }

    /**
     * Saves a cropped bitmap into the app's cache directory and returns a file URI.
     */
    fun saveBitmapToCache(context: Context, bitmap: Bitmap, prefix: String = "product_cropped"): Uri? {
        return try {
            val cacheFile = File(context.cacheDir, "${prefix}_${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(cacheFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, outputStream)
            outputStream.flush()
            outputStream.close()
            Uri.fromFile(cacheFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Converts a Bitmap directly into a Base64 data URI for Firestore storage.
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteStream = ByteArrayOutputStream()
        // Compress with JPEG 80% quality to keep size compact and fast for Firestore
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteStream)
        val byteArray = byteStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return "data:image/jpeg;base64," + base64String
    }

    /**
     * Converts a base64 string back into a Bitmap safely.
     */
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val clean = if (base64String.contains(",")) base64String.substringAfter(",") else base64String
            val decodedBytes = Base64.decode(clean.trim(), Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}