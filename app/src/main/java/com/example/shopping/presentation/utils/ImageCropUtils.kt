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
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

object ImageCropUtils {

    /**
     * Reads the image from the given Uri, crops it to an exact circle,
     * compresses it, saves to cache, and returns the new local file Uri.
     */
    fun createCircularCroppedImage(context: Context, sourceUri: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            // Determine dimensions for a centered square crop
            val minEdge = min(originalBitmap.width, originalBitmap.height)
            val xOffset = (originalBitmap.width - minEdge) / 2
            val yOffset = (originalBitmap.height - minEdge) / 2

            val squareBitmap = Bitmap.createBitmap(originalBitmap, xOffset, yOffset, minEdge, minEdge)

            // Scale down if huge (keep max 600x600 for high resolution without memory issues)
            val targetSize = min(minEdge, 600)
            val scaledBitmap = if (minEdge != targetSize) {
                Bitmap.createScaledBitmap(squareBitmap, targetSize, targetSize, true)
            } else {
                squareBitmap
            }

            // Create circular mask bitmap
            val output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val color = 0xff424242.toInt()
            val paint = Paint().apply {
                isAntiAlias = true
                this.color = color
            }
            val rect = Rect(0, 0, targetSize, targetSize)
            val rectF = RectF(rect)

            canvas.drawARGB(0, 0, 0, 0)
            canvas.drawOval(rectF, paint)

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(scaledBitmap, rect, rect, paint)

            // Save circular bitmap to app cache directory as PNG (preserving circular transparency)
            val cacheFile = File(context.cacheDir, "cropped_avatar_${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(cacheFile)
            output.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            Uri.fromFile(cacheFile)
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
     * Crops a user-selected sub-rectangle of the original bitmap into an exact circular avatar.
     * Takes normalized user offsets (pan/zoom) or default centered region.
     */
    fun cropCircularBitmap(
        originalBitmap: Bitmap,
        cropRect: Rect
    ): Bitmap {
        // Safe bound checks
        val safeLeft = cropRect.left.coerceIn(0, originalBitmap.width - 1)
        val safeTop = cropRect.top.coerceIn(0, originalBitmap.height - 1)
        val safeWidth = cropRect.width().coerceIn(1, originalBitmap.width - safeLeft)
        val safeHeight = cropRect.height().coerceIn(1, originalBitmap.height - safeTop)
        val size = min(safeWidth, safeHeight)

        val subBitmap = Bitmap.createBitmap(originalBitmap, safeLeft, safeTop, size, size)

        // Scale to standard avatar dimension (e.g. 400x400)
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
     * Converts a Bitmap directly into a Base64 string for Firestore storage.
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteStream)
        val byteArray = byteStream.toByteArray()
        val base64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
        return "data:image/png;base64,$base64"
    }

    /**
     * Converts a circular cropped bitmap into a base64 Data URI string.
     */
    fun createCircularCroppedBase64(context: Context, sourceUri: Uri): String? {
        return try {
            val originalBitmap = loadBitmap(context, sourceUri) ?: return null
            val minEdge = min(originalBitmap.width, originalBitmap.height)
            val xOffset = (originalBitmap.width - minEdge) / 2
            val yOffset = (originalBitmap.height - minEdge) / 2
            val rect = Rect(xOffset, yOffset, xOffset + minEdge, yOffset + minEdge)
            val circular = cropCircularBitmap(originalBitmap, rect)
            bitmapToBase64(circular)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


