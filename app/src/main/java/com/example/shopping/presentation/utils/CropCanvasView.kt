package com.example.shopping.presentation.utils

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.shopping.ui.theme.OrangePrimary
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
internal fun CropCanvasView(
    currentBmp: Bitmap,
    selectedRatio: CropAspectRatio,
    scale: Float,
    onScaleChange: (Float) -> Unit,
    offset: Offset,
    onOffsetChange: (Offset) -> Unit,
    onContainerSizeChanged: (IntSize) -> Unit
) {
    val imageBitmap = remember(currentBmp) { currentBmp.asImageBitmap() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { onContainerSizeChanged(it) }
            .pointerInput(selectedRatio) {
                detectTransformGestures { _, pan, zoom, _ ->
                    onScaleChange((scale * zoom).coerceIn(0.5f, 5.0f))
                    onOffsetChange(offset + pan)
                }
            }
    ) {
        val canvasW = size.width
        val canvasH = size.height

        val targetRatio = if (selectedRatio.ratio > 0f) {
            selectedRatio.ratio
        } else {
            currentBmp.width.toFloat() / currentBmp.height.toFloat()
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

        // Base scale to fit image to frame
        val baseScale = max(
            frameW / currentBmp.width.toFloat(),
            frameH / currentBmp.height.toFloat()
        )
        val totalScale = baseScale * scale
        val drawnW = currentBmp.width * totalScale
        val drawnH = currentBmp.height * totalScale

        val imgLeft = (canvasW - drawnW) / 2f + offset.x
        val imgTop = (canvasH - drawnH) / 2f + offset.y

        // 1. Draw Image
        drawImage(
            image = imageBitmap,
            dstOffset = IntOffset(imgLeft.roundToInt(), imgTop.roundToInt()),
            dstSize = IntSize(drawnW.roundToInt(), drawnH.roundToInt())
        )

        // 2. Dark Overlay outside Crop Frame
        val overlayColor = Color.Black.copy(alpha = 0.60f)
        drawRect(color = overlayColor, topLeft = Offset.Zero, size = Size(canvasW, frameTop))
        drawRect(color = overlayColor, topLeft = Offset(0f, frameTop + frameH), size = Size(canvasW, canvasH - (frameTop + frameH)))
        drawRect(color = overlayColor, topLeft = Offset(0f, frameTop), size = Size(frameLeft, frameH))
        drawRect(color = overlayColor, topLeft = Offset(frameLeft + frameW, frameTop), size = Size(canvasW - (frameLeft + frameW), frameH))

        // 3. Frame Border (Orange)
        drawRect(
            color = OrangePrimary,
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameW, frameH),
            style = Stroke(width = 2.5.dp.toPx())
        )

        // 4. Rule of thirds grid lines
        val gridColor = Color.White.copy(alpha = 0.25f)
        val gridStroke = Stroke(width = 1.dp.toPx())
        drawLine(color = gridColor, start = Offset(frameLeft + frameW / 3f, frameTop), end = Offset(frameLeft + frameW / 3f, frameTop + frameH), strokeWidth = gridStroke.width)
        drawLine(color = gridColor, start = Offset(frameLeft + (frameW * 2f) / 3f, frameTop), end = Offset(frameLeft + (frameW * 2f) / 3f, frameTop + frameH), strokeWidth = gridStroke.width)
        drawLine(color = gridColor, start = Offset(frameLeft, frameTop + frameH / 3f), end = Offset(frameLeft + frameW, frameTop + frameH / 3f), strokeWidth = gridStroke.width)
        drawLine(color = gridColor, start = Offset(frameLeft, frameTop + (frameH * 2f) / 3f), end = Offset(frameLeft + frameW, frameTop + (frameH * 2f) / 3f), strokeWidth = gridStroke.width)
    }
}
