package com.example.shopping.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.utils.sanitizeImageUrl
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun AdminEditBannerDialog(
    banner: BannerDataModels?,
    onDismiss: () -> Unit,
    onSaveBanner: (BannerDataModels) -> Unit
) {
    if (banner == null) return

    var editBanName by rememberSaveable(banner.bannerId) { mutableStateOf(banner.name) }
    var editBanImage by rememberSaveable(banner.bannerId) { mutableStateOf(banner.image) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Promotional Banner", fontWeight = FontWeight.Bold, color = TextWhite) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = editBanName,
                    onValueChange = { editBanName = it },
                    label = { Text("Banner Name / Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkInputBorder,
                        focusedContainerColor = DarkInputBg,
                        unfocusedContainerColor = DarkInputBg
                    )
                )

                OutlinedTextField(
                    value = editBanImage,
                    onValueChange = { editBanImage = it },
                    label = { Text("Banner Image URL") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkInputBorder,
                        focusedContainerColor = DarkInputBg,
                        unfocusedContainerColor = DarkInputBg
                    )
                )

                if (editBanImage.isNotBlank()) {
                    SmartAsyncImage(
                        imageUrl = editBanImage,
                        contentDescription = "Preview",
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            }
        },
        containerColor = DarkCard,
        confirmButton = {
            Button(
                onClick = {
                    val updated = banner.copy(
                        name = editBanName.trim(),
                        image = sanitizeImageUrl(editBanImage)
                    )
                    onSaveBanner(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Save Changes", color = ButtonTextColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun AdminDeleteBannerDialog(
    banner: BannerDataModels?,
    onDismiss: () -> Unit,
    onConfirm: (BannerDataModels) -> Unit
) {
    if (banner != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Banner", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("Are you sure you want to permanently delete '${banner.name}' from your Firestore database?", color = TextMuted) },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = { onConfirm(banner) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
