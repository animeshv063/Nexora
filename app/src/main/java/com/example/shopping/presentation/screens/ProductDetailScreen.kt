package com.example.shopping.presentation.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextDim
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onBuyNowClick: (String) -> Unit
) {
    val context = LocalContext.current
    val productDetailState by viewModel.productDetailState.collectAsState()
    val addToCartState by viewModel.addToCartState.collectAsState()
    val wishlistState by viewModel.wishlistState.collectAsState()

    var selectedSize by remember { mutableStateOf("M") }
    var quantity by remember { mutableIntStateOf(1) }

    val sizes = listOf("S", "M", "L", "XL", "XXL")

    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) {
            viewModel.fetchProductById(productId)
        }
    }

    LaunchedEffect(addToCartState) {
        addToCartState.data?.let {
            Toast.makeText(context, "Added to Cart! 🛒", Toast.LENGTH_SHORT).show()
            viewModel.resetAddToCartState()
        }
        addToCartState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetAddToCartState()
        }
    }

    val product = productDetailState.data ?: ProductDataModels(
        name = "Product",
        price = "0",
        finalPrice = "0",
        category = "General",
        description = "Product details and specifications will appear here.",
        image = "",
        productId = productId
    )

    val isFavorite = wishlistState.data?.any { it?.productId == productId } ?: false

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // Main Product Image with Back & Fav buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(DarkCard)
            ) {
                if (product.image.isNotEmpty()) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.name.take(1).uppercase(),
                            color = OrangePrimary,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        shape = CircleShape,
                        color = DarkBg.copy(alpha = 0.8f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = DarkBg.copy(alpha = 0.8f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { viewModel.toggleFavorite(product) }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) OrangePrimary else TextWhite
                            )
                        }
                    }
                }
            }

            // Product Details Sheet
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = (-20).dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = DarkBg
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = product.category.ifEmpty { "Fashion" },
                        color = OrangePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${product.finalPrice.ifEmpty { product.price }}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )

                        if (product.price.isNotEmpty() && product.price != product.finalPrice) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "₹${product.price}",
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkInputBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Size Selection
                    Text(
                        text = "Select Size",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        sizes.forEach { size ->
                            val isSelected = selectedSize == size
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) OrangePrimary else DarkCardSecondary,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { selectedSize = size }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = size,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quantity",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DarkCardSecondary,
                                modifier = Modifier.size(34.dp).clickable { if (quantity > 1) quantity-- }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "-", fontSize = 18.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "$quantity",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            Surface(
                                shape = CircleShape,
                                color = DarkCardSecondary,
                                modifier = Modifier.size(34.dp).clickable { quantity++ }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "+", fontSize = 18.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkInputBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Description",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = product.description.ifEmpty { "Tailored with utmost precision using premium fabric to offer standard fit and all-day comfort." },
                        fontSize = 14.sp,
                        color = TextMuted,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Bottom Action Bar (Add to Cart & Buy Now)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = DarkBg,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val cartItem = CartDataModels(
                            productId = product.productId,
                            name = product.name,
                            image = product.image,
                            quantity = quantity.toString(),
                            size = selectedSize,
                            description = product.description,
                            category = product.category,
                            price = product.price,
                            finalPrice = product.finalPrice
                        )
                        viewModel.addToCart(cartItem)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, OrangePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkBg)
                ) {
                    Text(text = "Add to Cart", color = OrangePrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onBuyNowClick(product.productId) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(text = "Buy Now", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
