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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.shopping.presentation.utils.SmartAsyncImage
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.SuccessGreen
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    onBuyNowClick: (String, Int, String) -> Unit
) {
    val context = LocalContext.current
    val productDetailState by viewModel.productDetailState.collectAsState()
    val addToCartState by viewModel.addToCartState.collectAsState()
    val wishlistState by viewModel.wishlistState.collectAsState()

    var selectedSize by remember { mutableStateOf("M") }
    var sizeQuantities by remember { mutableStateOf(mapOf<String, Int>()) }
    val currentQuantity = sizeQuantities[selectedSize] ?: 0
    val activeSelections = sizeQuantities.filter { it.value > 0 }
    val totalSelectedUnits = activeSelections.values.sum()

    val sizes = listOf("S", "M", "L", "XL", "XXL")

    LaunchedEffect(productId) {
        viewModel.fetchWishlist()
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

    val rawPrice = if (product.finalPrice.isNotBlank() && product.finalPrice != "0") product.finalPrice else product.price
    val unitPrice = rawPrice.toDoubleOrNull() ?: 0.0

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
                    .height(310.dp)
                    .background(DarkCard)
            ) {
                if (product.image.isNotEmpty()) {
                    SmartAsyncImage(
                        imageUrl = product.image,
                        contentDescription = product.name,
                        shape = androidx.compose.ui.graphics.RectangleShape,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        errorPlaceholderText = "${product.name}\n(Unable to load image link)"
                    )
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
                        color = DarkCardSecondary.copy(alpha = 0.85f),
                        modifier = Modifier.size(40.dp).clickable { onBackClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextWhite
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = DarkCardSecondary.copy(alpha = 0.85f),
                            modifier = Modifier.size(40.dp).clickable {
                                onNavigateToCart()
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = TextWhite
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = DarkCardSecondary.copy(alpha = 0.85f),
                            modifier = Modifier.size(40.dp).clickable {
                                viewModel.toggleFavorite(product)
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) OrangePrimary else TextWhite
                                )
                            }
                        }
                    }
                }
            }

            // Product Details Content
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = DarkCard
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Category Badge & Available Units Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = OrangePrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = product.category.ifEmpty { "General" },
                                    color = OrangePrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            // Dynamic Rating Chip
                            val prodRating = remember(product.productId) { com.example.shopping.presentation.utils.RatingHelper.getRatingForProduct(product.productId) }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFBBF24).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$prodRating ★",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Stock Availability Indicator
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (product.availableUnits > 0) SuccessGreen.copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (product.availableUnits > 0) "In Stock: ${product.availableUnits} units" else "Out of Stock",
                                color = if (product.availableUnits > 0) SuccessGreen else Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = product.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price Section with Discount
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val hasDiscount = product.finalPrice.isNotBlank() && product.finalPrice != product.price && product.finalPrice != "0"
                        if (hasDiscount) {
                            Text(
                                text = "₹${product.finalPrice}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OrangePrimary
                            )
                            Text(
                                text = "₹${product.price}",
                                fontSize = 16.sp,
                                color = TextMuted,
                                textDecoration = TextDecoration.LineThrough
                            )
                            val discountPercent = try {
                                val orig = product.price.toDouble()
                                val finalP = product.finalPrice.toDouble()
                                (((orig - finalP) / orig) * 100).toInt()
                            } catch (e: Exception) { 0 }
                            if (discountPercent > 0) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = OrangePrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "$discountPercent% OFF",
                                        color = OrangePrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "₹${product.price.ifEmpty { "0" }}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OrangePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkInputBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Size Selector
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
                            val sizeQty = sizeQuantities[size] ?: 0
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) OrangePrimary else DarkCardSecondary,
                                border = if (sizeQty > 0 && !isSelected) BorderStroke(1.dp, OrangePrimary) else null,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clickable {
                                        selectedSize = size
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = size,
                                            color = if (isSelected) ButtonTextColor else TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
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
                                        sizeQuantities = sizeQuantities + (selectedSize to currentQuantity - 1)
                                    } else if (currentQuantity == 1) {
                                        sizeQuantities = sizeQuantities - selectedSize
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
                                            sizeQuantities = sizeQuantities + (selectedSize to currentQuantity + 1)
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

                    // Real-time Selected Variations List (Appears when quantity >= 1 and disappears when 0)
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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = OrangePrimary.copy(alpha = 0.2f),
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(text = sz, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OrangePrimary)
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${product.name} (Size: $sz)",
                                                color = TextWhite,
                                                fontSize = 13.sp,
                                                maxLines = 1
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "x$qty  (₹${(unitPrice * qty).toInt()})",
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextMuted,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            IconButton(
                                                onClick = { sizeQuantities = sizeQuantities - sz },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Remove",
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
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

        val isOutOfStock = product.availableUnits <= 0

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
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkBg)
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
