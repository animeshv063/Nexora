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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.utils.sanitizeImageUrl
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun AdminBannersTab(
    bannerList: List<BannerDataModels>,
    banImage: String,
    onBanImageChange: (String) -> Unit,
    onRequestCrop: (url: String) -> Unit,
    onRequestPickAndCrop: () -> Unit,
    onAddBanner: (banner: BannerDataModels, onSuccess: () -> Unit) -> Unit,
    onEditBanner: (BannerDataModels) -> Unit,
    onDeleteBanner: (BannerDataModels) -> Unit
) {
    val context = LocalContext.current
    var newBanName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Add / Edit Banners",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextWhite
        )
        Text(
            text = "Firestore Collection: banner",
            fontSize = 12.sp,
            color = OrangePrimary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Admin: animeshv063@gmail.com",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Add Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
            border = BorderStroke(1.dp, DarkInputBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                KeyValRow(key = "name", value = newBanName, placeholder = "e.g. Urban Everyday Fashion Collection", onValueChange = { newBanName = it })
                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                KeyValRow(key = "image", value = banImage, placeholder = "Paste banner image link https://...", onValueChange = onBanImageChange)

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkInputBg,
                    border = BorderStroke(1.dp, if (banImage.isNotBlank()) OrangePrimary.copy(alpha = 0.6f) else DarkInputBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (banImage.isNotBlank()) {
                                    onRequestCrop(banImage)
                                } else {
                                    Toast.makeText(context, "Please paste a banner URL or pick from gallery first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text(text = "✂️ Crop (16:9)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ButtonTextColor)
                        }
                        OutlinedButton(
                            onClick = onRequestPickAndCrop,
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, DarkInputBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                        ) {
                            Text(text = "🖼️ Pick & Crop", fontSize = 11.sp, color = TextWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkInputBg,
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onBanImageChange("https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200&q=80")
                            }
                    ) {
                        Text(
                            text = "💡 Sample Store",
                            fontSize = 11.sp,
                            color = OrangePrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkInputBg,
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onBanImageChange("https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200&q=80")
                            }
                    ) {
                        Text(
                            text = "💡 Sample Fashion",
                            fontSize = 11.sp,
                            color = OrangePrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live 16:9 Banner Preview
        Text(text = "Live 16:9 Banner Preview", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkCardSecondary),
            contentAlignment = Alignment.Center
        ) {
            if (banImage.isNotBlank()) {
                SmartAsyncImage(
                    imageUrl = banImage,
                    contentDescription = "Banner Preview",
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
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
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = OrangePrimary.copy(alpha = 0.7f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Live Banner Preview",
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter or paste any banner image URL above to preview in real-time",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val name = newBanName.trim()
                val img = sanitizeImageUrl(banImage)
                if (name.isEmpty() || img.isEmpty()) {
                    Toast.makeText(context, "Please enter Banner Name and Image link", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val banner = BannerDataModels(
                    name = name,
                    image = img
                )

                onAddBanner(banner) {
                    newBanName = ""
                    onBanImageChange("")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Text("Add Banner to Firestore", fontWeight = FontWeight.Bold, color = ButtonTextColor, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Existing Banners (${bannerList.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
        Spacer(modifier = Modifier.height(10.dp))

        if (bannerList.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(80.dp),
                shape = RoundedCornerShape(10.dp),
                color = DarkCardSecondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Nothing to display", color = TextMuted, fontSize = 14.sp)
                }
            }
        } else {
            bannerList.forEach { banner ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
                    border = BorderStroke(1.dp, DarkInputBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .height(130.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        SmartAsyncImage(
                            imageUrl = banner.image,
                            contentDescription = banner.name,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = banner.name,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Row {
                                IconButton(
                                    onClick = { onEditBanner(banner) }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OrangePrimary)
                                }

                                IconButton(
                                    onClick = { onDeleteBanner(banner) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
