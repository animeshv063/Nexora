package com.example.shopping.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.unit.Dp
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
    var selectedGender by remember { mutableStateOf("All") }

    val genderOptions = listOf("All", "Men", "Women", "Unisex")

    LaunchedEffect(categoryName) {
        viewModel.fetchProductsByCategory(categoryName)
    }

    val products = categoryProductsState.data ?: emptyList()
    val filteredProducts = products.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
        val matchesGender = when (selectedGender) {
            "All" -> true
            "Men" -> product.gender.equals("Men", ignoreCase = true) || product.gender.equals("Unisex", ignoreCase = true) || product.gender.isBlank()
            "Women" -> product.gender.equals("Women", ignoreCase = true) || product.gender.equals("Unisex", ignoreCase = true)
            "Unisex" -> product.gender.equals("Unisex", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesGender
    }


    val statusBarsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isThreeButton = navBarsBottom >= 30.dp
    val safeTop: Dp = if (statusBarsTop > 0.dp) statusBarsTop + 6.dp else 44.dp
    val safeBottom: Dp = if (isThreeButton) navBarsBottom + 12.dp else navBarsBottom + 6.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(top = safeTop, bottom = safeBottom)
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

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Search $categoryName", color = TextMuted, fontSize = 14.sp) },
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

        Spacer(modifier = Modifier.height(10.dp))

        // Gender Filter Chips (All | 👨 Men | 👩 Women | ✨ Unisex)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genderOptions) { gender ->
                val isSelected = selectedGender == gender
                val label = when (gender) {
                    "All" -> "All"
                    "Men" -> "👨 Men"
                    "Women" -> "👩 Women"
                    "Unisex" -> "✨ Unisex"
                    else -> gender
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) OrangePrimary else DarkInputBg,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) OrangePrimary else DarkInputBorder
                    ),
                    modifier = Modifier.clickable { selectedGender = gender }
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.Black else TextWhite,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No products found",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try switching between All / Men / Women filter",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
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
}
