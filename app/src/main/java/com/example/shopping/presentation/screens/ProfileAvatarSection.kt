package com.example.shopping.presentation.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DangerRed
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.PrimaryAccent
import com.example.shopping.ui.theme.TextMuted

@Composable
internal fun ProfileAvatarSection(
    profileImage: String,
    firstName: String,
    email: String?,
    onPickImage: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    // Profile Avatar (Clean initials placeholder or custom picked photo)
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(120.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = DarkCardSecondary,
            modifier = Modifier
                .size(120.dp)
                .border(2.5.dp, PrimaryAccent, CircleShape)
                .clickable { onPickImage() }
        ) {
            if (profileImage.isNotEmpty()) {
                SmartAsyncImage(
                    imageUrl = profileImage,
                    contentDescription = "Profile Photo",
                    shape = CircleShape,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Clean initials avatar
                val initial = (firstName.firstOrNull() ?: email?.firstOrNull() ?: 'U').uppercaseChar()
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial.toString(),
                        color = PrimaryAccent,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Edit photo camera icon badge
        Surface(
            shape = CircleShape,
            color = PrimaryAccent,
            modifier = Modifier
                .size(36.dp)
                .clickable { onPickImage() },
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Change Photo",
                    tint = ButtonTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Action options for Photo (Change / Remove Photo)
    if (profileImage.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Remove Photo",
            color = DangerRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { onRemovePhoto() }
                .padding(4.dp)
        )
    } else {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap circle or badge to choose photo",
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}
