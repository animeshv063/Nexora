package com.example.shopping.presentation.utils

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

enum class CropAspectRatio(val label: String, val ratio: Float) {
    ORIGINAL("Original Fit", -1.0f),
    SQUARE_1_1("1:1 Square", 1.0f),
    PORTRAIT_3_4("3:4 Portrait", 3f / 4f),
    PORTRAIT_4_5("4:5 Fashion", 4f / 5f),
    BANNER_16_9("16:9 Banner", 16f / 9f)
}

@Composable
fun ProductImageCropDialog(
    initialBitmap: Bitmap? = null,
    imageUrl: String = "",
    dialogTitle: String = "Image Crop & Precision Zoom",
    initialRatio: CropAspectRatio = CropAspectRatio.ORIGINAL,
    onDismissRequest: () -> Unit,
    onCropConfirmed: (Bitmap, String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activeBitmap by remember { mutableStateOf(initialBitmap) }
    var isLoadingImage by remember { mutableStateOf(initialBitmap == null && imageUrl.isNotBlank()) }
    var loadError by remember { mutableStateOf<String?>(null) }

    var selectedRatio by remember { mutableStateOf(initialRatio) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(imageUrl, initialBitmap) {
        if (initialBitmap != null) {
            activeBitmap = initialBitmap
            isLoadingImage = false
        } else if (imageUrl.isNotBlank()) {
            isLoadingImage = true
            loadError = null
            coroutineScope.launch {
                val loaded = ImageCropUtils.loadBitmapFromUrl(context, imageUrl)
                if (loaded != null) {
                    activeBitmap = loaded
                    isLoadingImage = false
                } else {
                    isLoadingImage = false
                    loadError = "Failed to load image. Please verify the URL."
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.90f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = dialogTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Drag to pan - Pinch or use slider to zoom",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        IconButton(onClick = onDismissRequest, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Aspect Ratio Selector
                    CropAspectRatioSelector(
                        selectedRatio = selectedRatio,
                        onRatioSelected = { ratio ->
                            selectedRatio = ratio
                            scale = 1f
                            offset = Offset.Zero
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Crop Viewport
                    val currentBmp = activeBitmap
                    if (isLoadingImage) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(270.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF111218)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = OrangePrimary, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Loading image...", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    } else if (currentBmp != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(270.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0A0A0E))
                        ) {
                            CropCanvasView(
                                currentBmp = currentBmp,
                                selectedRatio = selectedRatio,
                                scale = scale,
                                onScaleChange = { scale = it },
                                offset = offset,
                                onOffsetChange = { offset = it },
                                onContainerSizeChanged = { containerSize = it }
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF111218)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loadError ?: "No image available to crop",
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Zoom Controls
                    CropZoomControls(
                        scale = scale,
                        onScaleChange = { scale = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons (Reset & Apply)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DarkInputBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = TextWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", fontSize = 13.sp, color = TextWhite)
                        }

                        Button(
                            onClick = {
                                val bmp = activeBitmap
                                if (bmp != null && containerSize.width > 0 && containerSize.height > 0) {
                                    val canvasW = containerSize.width.toFloat()
                                    val canvasH = containerSize.height.toFloat()

                                    val targetRatio = if (selectedRatio.ratio > 0f) {
                                        selectedRatio.ratio
                                    } else {
                                        bmp.width.toFloat() / bmp.height.toFloat()
                                    }

                                    val maxFrameW = canvasW * 0.88f
                                    val maxFrameH = canvasH * 0.88f

                                    var frameW = maxFrameW
                                    var frameH = frameW / targetRatio
                                    if (frameH > maxFrameH) {
                                        frameH = maxFrameH
                                        frameW = frameH * targetRatio
                                    }

                                    val frameLeft = (canvasW - frameW) / 2f
                                    val frameTop = (canvasH - frameH) / 2f

                                    val baseScale = max(
                                        frameW / bmp.width.toFloat(),
                                        frameH / bmp.height.toFloat()
                                    )
                                    val totalScale = baseScale * scale
                                    val drawnW = bmp.width * totalScale
                                    val drawnH = bmp.height * totalScale

                                    val imgLeft = (canvasW - drawnW) / 2f + offset.x
                                    val imgTop = (canvasH - drawnH) / 2f + offset.y

                                    val cropXInBmp = ((frameLeft - imgLeft) / totalScale).toInt().coerceIn(0, bmp.width - 1)
                                    val cropYInBmp = ((frameTop - imgTop) / totalScale).toInt().coerceIn(0, bmp.height - 1)
                                    val cropWInBmp = ((frameW / totalScale).toInt()).coerceIn(1, bmp.width - cropXInBmp)
                                    val cropHInBmp = ((frameH / totalScale).toInt()).coerceIn(1, bmp.height - cropYInBmp)

                                    val cropRect = Rect(
                                        cropXInBmp,
                                        cropYInBmp,
                                        cropXInBmp + cropWInBmp,
                                        cropYInBmp + cropHInBmp
                                    )

                                    val cropped = ImageCropUtils.cropRectangularBitmap(
                                        originalBitmap = bmp,
                                        cropRect = cropRect,
                                        targetWidth = min(cropWInBmp, 800),
                                        targetHeight = min(cropHInBmp, 800)
                                    )

                                    val base64Data = ImageCropUtils.bitmapToBase64(cropped)
                                    onCropConfirmed(cropped, base64Data)
                                } else if (bmp != null) {
                                    val base64Data = ImageCropUtils.bitmapToBase64(bmp)
                                    onCropConfirmed(bmp, base64Data)
                                }
                            },
                            enabled = activeBitmap != null,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Apply", tint = ButtonTextColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Apply Crop & Framing", fontWeight = FontWeight.Bold, color = ButtonTextColor, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
