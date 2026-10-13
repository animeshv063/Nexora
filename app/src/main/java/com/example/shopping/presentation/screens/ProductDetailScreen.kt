package com.example.shopping.presentation.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.RatingHelper
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
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

    val product = productDetailState.data ?: ProductDataModels(
        name = "Product",
        price = "0",
        finalPrice = "0",
        category = "General",
        description = "Product details and specifications will appear here.",
        image = "",
        productId = productId
    )

    val sizes = remember(product.category) {
        val cat = product.category.trim().lowercase()
        when {
            cat.contains("kid") || cat.contains("child") -> listOf("XS", "S", "M")
            cat.contains("footwear") || cat.contains("shoe") || cat.contains("sneaker") || cat.contains("boot") || cat.contains("sandal") || cat.contains("slipper") -> {
                listOf("UK 6", "UK 7", "UK 8", "UK 9", "UK 10", "UK 11")
            }
            cat.contains("accessor") || cat.contains("belt") || cat.contains("wallet") || cat.contains("watch") || cat.contains("sunglass") || cat.contains("cap") || cat.contains("hat") || cat.contains("bag") -> {
                listOf("Free Size")
            }
            else -> listOf("S", "M", "L", "XL", "XXL")
        }
    }

    var selectedSize by remember { mutableStateOf(sizes.firstOrNull() ?: "M") }

    LaunchedEffect(sizes) {
        if (selectedSize !in sizes) {
            selectedSize = sizes.firstOrNull() ?: "M"
        }
    }

    var sizeQuantities by remember { mutableStateOf(mapOf<String, Int>()) }
    val activeSelections = sizeQuantities.filter { it.value > 0 }
    val totalSelectedUnits = activeSelections.values.sum()

    val isFavorite = wishlistState.data?.any { it?.productId == productId } ?: false

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

    val rawPrice = if (product.finalPrice.isNotBlank() && product.finalPrice != "0") product.finalPrice else product.price
    val unitPrice = rawPrice.toDoubleOrNull() ?: 0.0

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Main Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(DarkBg)
            ) {
                if (product.image.isNotEmpty()) {
                    SmartAsyncImage(
                        imageUrl = product.image,
                        contentDescription = product.name,
                        shape = androidx.compose.ui.graphics.RectangleShape,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        errorPlaceholderText = "${product.name}\n(Unable to load image link)"
                    )
                }
            }

            // Product Details Content (Seamless & Uniform Background)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBg)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Category, Gender, Rating & Stock Status Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = OrangePrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = product.category.ifEmpty { "General" },
                                color = OrangePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Gender Badge (Men / Women / Unisex)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF6366F1).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = when (product.gender) {
                                    "Women" -> "👩 Women"
                                    "Unisex" -> "✨ Unisex"
                                    else -> "👨 Men"
                                },
                                color = Color(0xFFA5B4FC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }

                        // Dynamic Rating Chip
                        val prodRating = remember(product.productId) { RatingHelper.getRatingForProduct(product.productId) }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFBBF24).copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$prodRating ★",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Stock Availability Indicator
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.availableUnits > 0) SuccessGreen.copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (product.availableUnits > 0) "In Stock: ${product.availableUnits}" else "Out of Stock",
                            color = if (product.availableUnits > 0) SuccessGreen else Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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

                // Size and Quantity Variations
                ProductVariationsSection(
                    product = product,
                    sizes = sizes,
                    selectedSize = selectedSize,
                    onSizeSelected = { selectedSize = it },
                    sizeQuantities = sizeQuantities,
                    onQuantityChange = { sizeQuantities = it },
                    unitPrice = unitPrice
                )

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

        // Floating Top Bar: Back, Cart, Favorite
        ProductHeaderBar(
            modifier = Modifier.align(Alignment.TopCenter),
            isFavorite = isFavorite,
            product = product,
            viewModel = viewModel,
            onBackClick = onBackClick,
            onNavigateToCart = onNavigateToCart
        )

        // Bottom Action Bar: Add to Cart & Buy Now
        ProductBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            product = product,
            totalSelectedUnits = totalSelectedUnits,
            activeSelections = activeSelections,
            viewModel = viewModel,
            onBuyNowClick = onBuyNowClick
        )
    }
}
