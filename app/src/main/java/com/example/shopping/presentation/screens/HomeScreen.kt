package com.example.shopping.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.presentation.utils.Banner
import com.example.shopping.presentation.utils.ProductCardDark
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextDim
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun HomeScreen(
    viewModel: ShoppingAppViewModel,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSeeAllCategoriesClick: () -> Unit,
    onSeeAllProductsClick: () -> Unit,
    onNotificationClick: () -> Unit = {}
) {
    val bannerState by viewModel.bannerState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()
    val productsState by viewModel.productsState.collectAsState()

    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showNotificationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    val categoriesList = categoriesState.data?.filterNotNull() ?: emptyList()
    val productsList = productsState.data ?: emptyList()

    // ⚡ Random Flash Sale Offers on Firebase Products (Max 5 products with ₹200 to ₹700 off)
    val flashSaleProducts = remember(productsList) {
        if (productsList.isEmpty()) emptyList()
        else {
            val possibleDiscounts = listOf(200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700)
            productsList.shuffled().take(5).map { product ->
                val basePrice = product.price.toDoubleOrNull() ?: 1499.0
                val discount = possibleDiscounts.random()
                val discountedPrice = maxOf(99.0, basePrice - discount).toInt().toString()
                product.copy(
                    finalPrice = discountedPrice
                )
            }
        }
    }

    // 🌟 Randomize "Suggested For You" separately (Max 5 products with fresh selection each time)
    val suggestedForYouProducts = remember(productsList) {
        if (productsList.isEmpty()) emptyList()
        else productsList.shuffled().take(5)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Nexora Brand Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.example.shopping.R.drawable.app_logo),
                contentDescription = "Nexora Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "NEXORA",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SHOP MORE. LIVE BETTER.",
                    fontSize = 9.sp,
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Search & Notification Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                placeholder = { Text("Search products", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkInputBg,
                    unfocusedContainerColor = DarkInputBg,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = DarkInputBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = OrangePrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(12.dp),
                color = DarkCardSecondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    IconButton(onClick = { showNotificationDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Notification Modal Dialog
        if (showNotificationDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showNotificationDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Notifications", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 18.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkCardSecondary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "🎉 Welcome Offer!", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Get up to 50% discount on all new season clothing.", color = TextMuted, fontSize = 13.sp)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkCardSecondary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "📦 Fast Delivery Active", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Express courier delivery is now available for your address.", color = TextMuted, fontSize = 13.sp)
                            }
                        }
                    }
                },
                containerColor = DarkCard,
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { showNotificationDialog = false }) {
                        Text(text = "Close", color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }




        if (searchQuery.isNotBlank()) {
            val query = searchQuery.trim()
            val matchingProducts = productsList.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                product.category.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)
            }
            val matchingCategories = categoriesList.filter { cat ->
                cat.name.contains(query, ignoreCase = true)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Category recommendation chips
                if (matchingCategories.isNotEmpty()) {
                    Text(
                        text = "Suggested Categories",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(matchingCategories) { category ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = DarkCardSecondary,
                                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { onCategoryClick(category.name) }
                            ) {
                                Text(
                                    text = "📁 ${category.name}",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Matching Products (${matchingProducts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (matchingProducts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No items matching \"$searchQuery\"",
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Only products present in your Firebase catalog are shown.",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    val chunked = matchingProducts.chunked(2)
                    chunked.forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowItems.forEach { product ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCardDark(
                                        product = product,
                                        modifier = Modifier.fillMaxWidth(),
                                        onProductClick = { onProductClick(product.productId) }
                                    )
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        } else {
            // Normal Home Layout
            // Categories Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Categories", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(
                    text = "See more",
                    fontSize = 13.sp,
                    color = OrangePrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onSeeAllCategoriesClick() }
                )
            }

            // Categories Circular Row
            if (categoriesList.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCardSecondary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Nothing to display", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categoriesList) { category ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onCategoryClick(category.name) }
                        ) {
                            Surface(
                                modifier = Modifier.size(68.dp),
                                shape = CircleShape,
                                color = DarkCardSecondary
                            ) {
                                if (category.categoryImage.isNotEmpty()) {
                                    com.example.shopping.presentation.utils.SmartAsyncImage(
                                        imageUrl = category.categoryImage,
                                        contentDescription = category.name,
                                        shape = CircleShape,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = category.name.take(1).uppercase(),
                                            color = OrangePrimary,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = category.name,
                                fontSize = 12.sp,
                                color = TextWhite,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Auto-Sliding Promo Banner
            Banner(banners = bannerState.data ?: emptyList())

            // Flash Sale Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Flash Sale", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(
                    text = "See more",
                    fontSize = 13.sp,
                    color = OrangePrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onSeeAllProductsClick() }
                )
            }

            // Flash Sale Horizontal List (Random Products with ₹200 to ₹700 dynamic discounts)
            if (flashSaleProducts.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(flashSaleProducts) { product ->
                        ProductCardDark(
                            product = product,
                            onProductClick = { onProductClick(product.productId) }
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCardSecondary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Nothing to display", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Suggested For You Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Suggested For You", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(
                    text = "See more",
                    fontSize = 13.sp,
                    color = OrangePrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onSeeAllProductsClick() }
                )
            }

            // Suggested For You Horizontal List (Distinctly randomized collection)
            if (suggestedForYouProducts.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(suggestedForYouProducts) { product ->
                        ProductCardDark(
                            product = product,
                            onProductClick = { onProductClick(product.productId) }
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCardSecondary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Nothing to display", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
