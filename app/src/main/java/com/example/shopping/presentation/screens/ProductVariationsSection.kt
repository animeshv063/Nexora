package com.example.shopping.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun ProductVariationsSection(
    product: ProductDataModels,
    sizes: List<String>,
    selectedSize: String,
    onSizeSelected: (String) -> Unit,
    sizeQuantities: Map<String, Int>,
    onQuantityChange: (Map<String, Int>) -> Unit,
    unitPrice: Double
) {
    val context = LocalContext.current
    val currentQuantity = sizeQuantities[selectedSize] ?: 0
    val activeSelections = sizeQuantities.filter { it.value > 0 }
    val totalSelectedUnits = activeSelections.values.sum()

    // Size Selector
    Text(
        text = if (sizes.size == 1 && sizes.first() == "Free Size") "Size" else "Select Size",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = TextWhite
    )

    Spacer(modifier = Modifier.height(10.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(sizes) { size ->
            val isSelected = selectedSize == size
            val sizeQty = sizeQuantities[size] ?: 0
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) OrangePrimary else DarkCardSecondary,
                border = if (sizeQty > 0 && !isSelected) BorderStroke(1.dp, OrangePrimary) else null,
                modifier = Modifier
                    .height(44.dp)
                    .clickable { onSizeSelected(size) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = if (size.length > 3) 14.dp else 12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = size,
                            color = if (isSelected) ButtonTextColor else TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (size.length > 4) 12.sp else 13.sp
                        )
                        if (sizeQty > 0) {
                            Text(
                                text = "($sizeQty)",
                                color = if (isSelected) ButtonTextColor else OrangePrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Quantity Counter for selected size
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Quantity for Size ($selectedSize)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            if (product.availableUnits > 0) {
                Text(
                    text = "Limit: ${product.availableUnits} total units",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (currentQuantity > 0) DarkCardSecondary else DarkCardSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(34.dp).clickable {
                    if (currentQuantity > 1) {
                        onQuantityChange(sizeQuantities + (selectedSize to currentQuantity - 1))
                    } else if (currentQuantity == 1) {
                        onQuantityChange(sizeQuantities - selectedSize)
                    }
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "-", fontSize = 18.sp, color = if (currentQuantity > 0) TextWhite else TextMuted, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "$currentQuantity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Surface(
                shape = CircleShape,
                color = if (product.availableUnits > 0 && totalSelectedUnits < product.availableUnits) DarkCardSecondary else DarkCardSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(34.dp).clickable {
                    if (product.availableUnits > 0) {
                        if (totalSelectedUnits < product.availableUnits) {
                            onQuantityChange(sizeQuantities + (selectedSize to currentQuantity + 1))
                        } else {
                            Toast.makeText(context, "Cannot add more. Only ${product.availableUnits} units available in stock!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Item is out of stock!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "+", fontSize = 18.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Real-time Selected Variations List
    if (activeSelections.isNotEmpty()) {
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCardSecondary,
            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Added Items ($totalSelectedUnits units)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = OrangePrimary
                    )
                    Text(
                        text = "₹${(unitPrice * totalSelectedUnits).toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                activeSelections.forEach { (sz, qty) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = OrangePrimary.copy(alpha = 0.15f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = sz,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = OrangePrimary,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = product.name,
                                color = TextWhite,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "x$qty (₹${(unitPrice * qty).toInt()})",
                                fontWeight = FontWeight.Medium,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onQuantityChange(sizeQuantities - sz) },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove size selection",
                                    tint = TextMuted.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
