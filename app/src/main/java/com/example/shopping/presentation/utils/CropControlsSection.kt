package com.example.shopping.presentation.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextWhite
import kotlin.math.roundToInt

@Composable
internal fun CropAspectRatioSelector(
    selectedRatio: CropAspectRatio,
    onRatioSelected: (CropAspectRatio) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(CropAspectRatio.values()) { ratio ->
            val isSelected = selectedRatio == ratio
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) OrangePrimary else DarkCardSecondary,
                border = if (!isSelected) BorderStroke(1.dp, DarkInputBorder) else null,
                modifier = Modifier.clickable { onRatioSelected(ratio) }
            ) {
                Text(
                    text = ratio.label,
                    color = if (isSelected) ButtonTextColor else TextWhite,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
internal fun CropZoomControls(
    scale: Float,
    onScaleChange: (Float) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCardSecondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Precision Zoom",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OrangePrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${(scale * 100).roundToInt()}%",
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onScaleChange((scale - 0.15f).coerceIn(0.5f, 5.0f)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(text = "-", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = scale,
                    onValueChange = onScaleChange,
                    valueRange = 0.5f..5.0f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = OrangePrimary,
                        activeTrackColor = OrangePrimary,
                        inactiveTrackColor = DarkInputBorder
                    )
                )

                IconButton(
                    onClick = { onScaleChange((scale + 0.15f).coerceIn(0.5f, 5.0f)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = TextWhite, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
