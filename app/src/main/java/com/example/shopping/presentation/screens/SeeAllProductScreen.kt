package com.example.shopping.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.presentation.utils.ProductCard
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel

@Composable
fun SeeAllProductScreen(
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val productsState by viewModel.productsState.collectAsState()
    val wishlistState by viewModel.wishlistState.collectAsState()

    val wishlistProductIds = remember(wishlistState.data) {
        wishlistState.data?.mapNotNull { it.productId }?.toSet() ?: emptySet()
    }

    val products = productsState.data ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "All Clothes & Sales",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "${products.size} items in total",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        if (productsState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = wishlistProductIds.contains(product.productId),
                        onProductClick = { onProductClick(product.productId) },
                        onFavoriteClick = { viewModel.toggleFavorite(product) }
                    )
                }
            }
        }
    }
}
