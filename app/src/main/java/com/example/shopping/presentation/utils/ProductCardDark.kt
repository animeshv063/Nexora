package com.example.shopping.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.PrimaryAccent
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface

@Composable
fun ProductCardDark(
    product: ProductDataModels,
    modifier: Modifier = Modifier,
    onProductClick: () -> Unit,
    onRemoveFavorite: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onProductClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSecondary)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(DarkCard)
            ) {
                if (product.image.isNotEmpty()) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.name.take(1).uppercase(),
                            color = PrimaryAccent,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (onRemoveFavorite != null) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(32.dp)
                            .align(Alignment.TopEnd),
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        IconButton(onClick = onRemoveFavorite, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Remove Favorite",
                                tint = com.example.shopping.ui.theme.AccentCoral,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }



            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSecondary)
                    .padding(10.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "₹${product.finalPrice.ifEmpty { product.price }}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    if (product.price.isNotEmpty() && product.price != product.finalPrice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${product.price}",
                            fontSize = 11.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = TextMuted
                        )
                    }

                    if (product.availableUnits > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${product.availableUnits} left)",
                            fontSize = 10.sp,
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
