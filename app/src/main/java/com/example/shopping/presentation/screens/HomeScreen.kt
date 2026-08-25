package com.example.shopping.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
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

    var searchQuery by remember { mutableStateOf("") }
    var showNotificationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    val defaultCategories = listOf(
        CategoryDataModels(name = "Shirts", categoryImage = ""),
        CategoryDataModels(name = "Trousers", categoryImage = ""),
        CategoryDataModels(name = "Shervani", categoryImage = ""),
        CategoryDataModels(name = "Pajamas", categoryImage = ""),
        CategoryDataModels(name = "Kids Wear", categoryImage = "")
    )

    val categoriesList = if (!categoriesState.data.isNullOrEmpty()) categoriesState.data!!.filterNotNull() else defaultCategories
    val productsList = productsState.data ?: emptyList()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Search & Notification Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                placeholder = { Text("Search", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
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
                    // Notification badge
                    Surface(
                        shape = CircleShape,
                        color = com.example.shopping.ui.theme.AccentCoral,
                        modifier = Modifier
                            .size(9.dp)
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                    ) {}
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
                            AsyncImage(
                                model = category.categoryImage,
                                contentDescription = category.name,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
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

        // Flash Sale Horizontal List
        if (productsList.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(productsList) { product ->
                    ProductCardDark(
                        product = product,
                        onProductClick = { onProductClick(product.productId) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = OrangePrimary, modifier = Modifier.size(28.dp))
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

        // Suggested For You Horizontal List
        if (productsList.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(productsList.reversed()) { product ->
                    ProductCardDark(
                        product = product,
                        onProductClick = { onProductClick(product.productId) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
