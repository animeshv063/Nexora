package com.example.shopping.presentation.utils

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.PrimaryAccent
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ImageCropDialog(
    bitmap: Bitmap,
    onDismissRequest: () -> Unit,
    onCropConfirmed: (Bitmap) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }

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
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
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
                                text = "Crop Profile Picture",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pinch to zoom • Drag to move",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dedicated Crop Viewport
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F1015))
                            .onSizeChanged { containerSize = it }
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(0.5f, 5.0f)
                                    offset += pan
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (containerSize.width > 0 && containerSize.height > 0) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                val baseScale = max(
                                    canvasWidth / bitmap.width.toFloat(),
                                    canvasHeight / bitmap.height.toFloat()
                                )
                                val totalScale = baseScale * scale
                                val drawnWidth = bitmap.width * totalScale
                                val drawnHeight = bitmap.height * totalScale

                                val imgLeft = (canvasWidth - drawnWidth) / 2f + offset.x
                                val imgTop = (canvasHeight - drawnHeight) / 2f + offset.y

                                // 1. Draw Image
                                drawImage(
                                    image = imageBitmap,
                                    dstOffset = IntOffset(imgLeft.roundToInt(), imgTop.roundToInt()),
                                    dstSize = IntSize(drawnWidth.roundToInt(), drawnHeight.roundToInt())
                                )

                                // 2. Dark Overlay for outside the circle
                                val cropRadius = min(canvasWidth, canvasHeight) * 0.40f
                                val centerOffset = Offset(canvasWidth / 2f, canvasHeight / 2f)

                                // Draw circular viewfinder guide outline
                                drawCircle(
                                    color = PrimaryAccent,
                                    radius = cropRadius,
                                    center = centerOffset,
                                    style = Stroke(width = 3.dp.toPx())
                                )

                                // Subtle inner guide outline
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.4f),
                                    radius = cropRadius - 4.dp.toPx(),
                                    center = centerOffset,
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", color = TextWhite, fontSize = 14.sp)
                        }

                        Button(
                            onClick = {
                                if (containerSize.width > 0 && containerSize.height > 0) {
                                    val canvasWidth = containerSize.width.toFloat()
                                    val canvasHeight = containerSize.height.toFloat()

                                    val baseScale = max(
                                        canvasWidth / bitmap.width.toFloat(),
                                        canvasHeight / bitmap.height.toFloat()
                                    )
                                    val totalScale = baseScale * scale
                                    val drawnWidth = bitmap.width * totalScale
                                    val drawnHeight = bitmap.height * totalScale

                                    val imgLeft = (canvasWidth - drawnWidth) / 2f + offset.x
                                    val imgTop = (canvasHeight - drawnHeight) / 2f + offset.y

                                    val cropRadius = min(canvasWidth, canvasHeight) * 0.40f
                                    val cropDiameter = cropRadius * 2f
                                    val cropLeft = (canvasWidth - cropDiameter) / 2f
                                    val cropTop = (canvasHeight - cropDiameter) / 2f

                                    // Map canvas viewport crop coordinates to original Bitmap pixels
                                    val bmpCropLeft = ((cropLeft - imgLeft) / totalScale).toInt()
                                    val bmpCropTop = ((cropTop - imgTop) / totalScale).toInt()
                                    val bmpCropSize = (cropDiameter / totalScale).toInt()

                                    val sourceRect = Rect(
                                        bmpCropLeft,
                                        bmpCropTop,
                                        bmpCropLeft + bmpCropSize,
                                        bmpCropTop + bmpCropSize
                                    )

                                    val croppedCircular = ImageCropUtils.cropCircularBitmap(bitmap, sourceRect)
                                    onCropConfirmed(croppedCircular)
                                } else {
                                    val minEdge = min(bitmap.width, bitmap.height)
                                    val defaultRect = Rect(0, 0, minEdge, minEdge)
                                    onCropConfirmed(ImageCropUtils.cropCircularBitmap(bitmap, defaultRect))
                                }
                            },
                            modifier = Modifier
                                .weight(1.4f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Apply",
                                tint = ButtonTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Set Avatar", fontWeight = FontWeight.Bold, color = ButtonTextColor, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
