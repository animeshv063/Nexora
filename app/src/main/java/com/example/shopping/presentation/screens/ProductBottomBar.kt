package com.example.shopping.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted

@Composable
internal fun ProductBottomBar(
    modifier: Modifier = Modifier,
    product: ProductDataModels,
    totalSelectedUnits: Int,
    activeSelections: Map<String, Int>,
    viewModel: ShoppingAppViewModel,
    onBuyNowClick: (String, Int, String) -> Unit
) {
    val context = LocalContext.current
    val isOutOfStock = product.availableUnits <= 0

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkCard,
        shadowElevation = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (isOutOfStock) {
                            Toast.makeText(context, "This product is currently out of stock!", Toast.LENGTH_SHORT).show()
                        } else if (totalSelectedUnits <= 0) {
                            Toast.makeText(context, "Please select at least 1 item to add to cart!", Toast.LENGTH_SHORT).show()
                        } else {
                            activeSelections.forEach { (sz, qty) ->
                                val cartItem = CartDataModels(
                                    productId = product.productId,
                                    name = product.name,
                                    image = product.image,
                                    quantity = qty.toString(),
                                    size = sz,
                                    description = product.description,
                                    category = product.category,
                                    price = product.price,
                                    finalPrice = product.finalPrice
                                )
                                viewModel.addToCart(cartItem)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isOutOfStock || totalSelectedUnits <= 0) DarkInputBorder else OrangePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkCard)
                ) {
                    Text(
                        text = if (isOutOfStock) "Out of Stock" else "Add to Cart (${if (totalSelectedUnits > 0) totalSelectedUnits else 0})",
                        color = if (isOutOfStock || totalSelectedUnits <= 0) TextMuted else OrangePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        if (isOutOfStock) {
                            Toast.makeText(context, "This product is currently out of stock!", Toast.LENGTH_SHORT).show()
                        } else if (totalSelectedUnits <= 0) {
                            Toast.makeText(context, "Please select at least 1 item to proceed to checkout!", Toast.LENGTH_SHORT).show()
                        } else {
                            val sizesSummary = activeSelections.entries.joinToString(", ") { "${it.key}(x${it.value})" }
                            onBuyNowClick(product.productId, totalSelectedUnits, sizesSummary)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOutOfStock || totalSelectedUnits <= 0) DarkCardSecondary else OrangePrimary
                    )
                ) {
                    Text(
                        text = if (isOutOfStock) "Unavailable" else "Buy Now (${if (totalSelectedUnits > 0) totalSelectedUnits else 0})",
                        color = if (isOutOfStock || totalSelectedUnits <= 0) TextMuted else ButtonTextColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
