package com.example.shopping.presentation.utils

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

/**
 * 🛠️ Sanitizes user-entered image URLs:
 * - Trims whitespace and trailing newlines.
 * - Extracts real image link from Google Images redirect URLs.
 * - Converts Google Drive share links into direct image streaming links.
 * - Converts Dropbox links into raw image stream links.
 */
fun sanitizeImageUrl(rawUrl: String): String {
    val trimmed = rawUrl.trim()
    if (trimmed.isEmpty()) return ""

    // Handle Google Images search redirect URLs (extract ?imgurl=...)
    if (trimmed.contains("imgurl=", ignoreCase = true)) {
        try {
            val uri = Uri.parse(trimmed)
            val extracted = uri.getQueryParameter("imgurl")
            if (!extracted.isNullOrBlank()) {
                return extracted.trim()
            }
        } catch (e: Exception) {
            // fallback to original trimmed
        }
    }

    // Handle Google Drive share URLs
    if (trimmed.contains("drive.google.com/file/d/", ignoreCase = true)) {
        val fileId = trimmed.substringAfter("file/d/").substringBefore("/").substringBefore("?")
        if (fileId.isNotBlank()) {
            return "https://lh3.googleusercontent.com/d/$fileId"
        }
    }

    // Handle Dropbox share URLs
    if (trimmed.contains("dropbox.com", ignoreCase = true)) {
        return trimmed.replace("dl=0", "raw=1")
    }

    return trimmed
}

/**
 * 🖼️ Robust AsyncImage that displays a smooth spinner on loading and an informative message on error,
 * preventing blank / black boxes when images are loading or URLs are unreachable.
 */
@Composable
fun SmartAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(8.dp),
    errorPlaceholderText: String = "Unable to load image (Check direct image link)"
) {
    val context = LocalContext.current
    val cleanUrl = sanitizeImageUrl(imageUrl)

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
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(cleanUrl)
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
                            horizontalAlignment = Alignment.CenterHorizontally
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
