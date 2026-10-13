package com.example.shopping.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.presentation.utils.Banner
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.OrangePrimary
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

    val categoriesList = categoriesState.data?.filterNotNull() ?: emptyList()
    val productsList = productsState.data ?: emptyList()

    // ⚡ Flash Sale: Random 6 discounted products on each app open
    val flashSaleProducts = remember(productsList) {
        if (productsList.isEmpty()) emptyList()
        else {
            val discounted = productsList.filter { product ->
                val orig = product.price.toDoubleOrNull() ?: 0.0
                val final = product.finalPrice.toDoubleOrNull() ?: orig
                orig > final && orig > 0
            }
            if (discounted.isNotEmpty()) {
                discounted.shuffled().take(6)
            } else {
                productsList.shuffled().take(6)
            }
        }
    }

    // 🌟 Suggested For You: Random 6 products strictly excluding the 6 selected in Flash Sale
    val suggestedForYouProducts = remember(productsList, flashSaleProducts) {
        if (productsList.isEmpty()) emptyList()
        else {
            val flashSaleIds = flashSaleProducts.map { it.productId }.toSet()
            val remainingProducts = productsList.filter { it.productId !in flashSaleIds }

            if (remainingProducts.isNotEmpty()) {
                remainingProducts.shuffled().take(6)
            } else {
                productsList.shuffled().take(6)
            }
        }
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
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(38.dp)
            ) {
                Image(
                    painter = painterResource(id = com.example.shopping.R.drawable.app_logo_mark),
                    contentDescription = "Nexora Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
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
        HomeSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onNotificationClick = { showNotificationDialog = true }
        )

        // Notification Modal Dialog
        if (showNotificationDialog) {
            HomeNotificationDialog(
                onDismissRequest = { showNotificationDialog = false }
            )
        }

        if (searchQuery.isNotBlank()) {
            HomeSearchResultsView(
                searchQuery = searchQuery,
                productsList = productsList,
                categoriesList = categoriesList,
                onCategoryClick = onCategoryClick,
                onProductClick = onProductClick
            )
        } else {
            // Normal Home Layout
            HomeCategorySection(
                categoriesList = categoriesList,
                isLoading = categoriesState.isLoading,
                onCategoryClick = onCategoryClick,
                onSeeAllCategoriesClick = onSeeAllCategoriesClick
            )

            // Auto-Sliding Promo Banner
            Banner(banners = bannerState.data ?: emptyList())

            // Flash Sale Section
            HomeFlashSaleSection(
                flashSaleProducts = flashSaleProducts,
                isLoading = productsState.isLoading,
                onProductClick = onProductClick,
                onSeeAllProductsClick = onSeeAllProductsClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Suggested For You Section
            HomeSuggestedSection(
                suggestedProducts = suggestedForYouProducts,
                isLoading = productsState.isLoading,
                onProductClick = onProductClick,
                onSeeAllProductsClick = onSeeAllProductsClick
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
