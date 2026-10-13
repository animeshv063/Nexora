package com.example.shopping.presentation.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun AdminProductImageActions(
    prodImage: String,
    onProdImageChange: (String) -> Unit,
    onRequestCrop: (url: String) -> Unit,
    onRequestPickAndCrop: () -> Unit
) {
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = DarkInputBg,
            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
            modifier = Modifier.clickable {
                onProdImageChange("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1000&q=80")
            }
        ) {
            Text(text = "👟 Shoes", fontSize = 11.sp, color = OrangePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = DarkInputBg,
            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
            modifier = Modifier.clickable {
                onProdImageChange("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=1000&q=80")
            }
        ) {
            Text(text = "👕 Shirt", fontSize = 11.sp, color = OrangePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = DarkInputBg,
            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
            modifier = Modifier.clickable {
                onProdImageChange("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&q=80")
            }
        ) {
            Text(text = "⌚ Watch", fontSize = 11.sp, color = OrangePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Image Crop & Precision Zoom Action Card
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkCardSecondary,
        border = BorderStroke(1.dp, if (prodImage.isNotBlank()) OrangePrimary.copy(alpha = 0.6f) else DarkInputBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Crop", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Image Crop & Precision Zoom", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextWhite)
                }
                if (prodImage.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = OrangePrimary.copy(alpha = 0.2f)
                    ) {
                        Text(text = "Link Ready", color = OrangePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (prodImage.isNotBlank()) {
                            onRequestCrop(prodImage)
                        } else {
                            Toast.makeText(context, "Please paste an image URL or pick from gallery first", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(text = "✂️ Adjust Crop & Zoom", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ButtonTextColor)
                }

                OutlinedButton(
                    onClick = onRequestPickAndCrop,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DarkInputBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                ) {
                    Text(text = "🖼️ Pick & Crop", fontSize = 11.sp, color = TextWhite)
                }
            }
        }
    }
}

@Composable
internal fun AdminProductImagePreview(
    prodImage: String
) {
    Text(text = "Product Preview", fontWeight = FontWeight.SemiBold, color = TextWhite, fontSize = 13.sp)
    Spacer(modifier = Modifier.height(6.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCardSecondary),
        contentAlignment = Alignment.Center
    ) {
        if (prodImage.isNotBlank()) {
            SmartAsyncImage(
                imageUrl = prodImage,
                contentDescription = "Product Preview",
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = OrangePrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Live Product Image Preview",
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter or paste any product image URL above to preview",
                    color = TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
