package com.example.shopping.presentation.utils

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Clean & normalize image URLs:
 */
fun sanitizeImageUrl(rawUrl: String): String {
    var url = rawUrl.trim()
    if (url.isEmpty()) return ""

    // Preserve base64 data URIs
    if (url.startsWith("data:image", ignoreCase = true)) {
        return url
    }

    url = url.removeSurrounding("\"", "\"")
        .removeSurrounding("'", "'")
        .removeSurrounding("<", ">")
        .removeSurrounding("(", ")")
        .removeSurrounding("[", "]")
        .trim('"', '\'', '<', '>', ',', ';', ' ')

    // Handle Google image search imgurl parameter if present
    if (url.contains("imgurl=", ignoreCase = true)) {
        try {
            val uri = Uri.parse(url)
            val extracted = uri.getQueryParameter("imgurl") ?: uri.getQueryParameter("url")
            if (!extracted.isNullOrBlank() && extracted.startsWith("http", ignoreCase = true)) {
                return URLDecoder.decode(extracted, StandardCharsets.UTF_8.name()).trim()
            }
        } catch (e: Exception) {
            // fallback
        }
    }

    // Handle Google Drive links
    if (url.contains("drive.google.com/file/d/", ignoreCase = true)) {
        val fileId = url.substringAfter("file/d/").substringBefore("/").substringBefore("?")
        if (fileId.isNotBlank()) {
            return "https://lh3.googleusercontent.com/d/"
        }
    }

    // Handle Dropbox links
    if (url.contains("dropbox.com", ignoreCase = true)) {
        return url.replace("dl=0", "raw=1").replace("?dl=1", "?raw=1")
    }

    // Handle GitHub blob links
    if (url.contains("github.com", ignoreCase = true) && url.contains("/blob/")) {
        return url.replace("github.com", "raw.githubusercontent.com").replace("/blob/", "/")
    }

    return url
}

/**
 * High-Performance SmartAsyncImage:
 * - Supports HTTP/HTTPS URLs, File URIs, and Base64 Data URIs (Cropped Bitmaps)
 */
@Composable
fun SmartAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(8.dp),
    errorPlaceholderText: String = "Unable to load image (Use 'Copy image address')"
) {
    val context = LocalContext.current
    val cleanUrl = sanitizeImageUrl(imageUrl)

    // Check if image is base64 data URI
    val isBase64 = cleanUrl.startsWith("data:image", ignoreCase = true)
    val decodedBitmap = remember(cleanUrl) {
        if (isBase64) {
            try {
                val pureBase64 = if (cleanUrl.contains(",")) cleanUrl.substringAfter(",") else cleanUrl
                val bytes = Base64.decode(pureBase64.trim(), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    Surface(
        modifier = modifier.clip(shape),
        shape = shape,
        color = DarkInputBg
    ) {
        if (cleanUrl.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize().background(DarkInputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No image URL",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        } else if (isBase64) {
            if (decodedBitmap != null) {
                Image(
                    bitmap = decodedBitmap.asImageBitmap(),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(DarkCard).padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error decoding cropped image", color = TextMuted, fontSize = 10.sp)
                }
            }
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(cleanUrl)
                    .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .setHeader("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                    .setHeader("Referer", "https://www.google.com/")
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(DarkInputBg),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = OrangePrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkCard)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Error",
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = errorPlaceholderText,
                                color = TextMuted,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            )
        }
    }
}