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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.ProductCardDark
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun CategoryProductsScreen(
    categoryName: String,
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val categoryProductsState by viewModel.categoryProductsState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(categoryName) {
        viewModel.fetchProductsByCategory(categoryName)
    }

    val products = categoryProductsState.data ?: emptyList()
    val filteredProducts = products.filter { it.name.contains(searchQuery, ignoreCase = true) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = categoryName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        // Search Bar matching screenshot
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Search", color = TextMuted, fontSize = 14.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkInputBg,
                unfocusedContainerColor = DarkInputBg,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = DarkInputBorder
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts) { product ->
                ProductCardDark(
                    product = product,
                    modifier = Modifier.fillMaxWidth(),
                    onProductClick = { onProductClick(product.productId) }
                )
            }
        }
    }
}
