package com.example.shopping.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.ProductCardDark
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun HomeFlashSaleSection(
    flashSaleProducts: List<ProductDataModels>,
    isLoading: Boolean,
    onProductClick: (String) -> Unit,
    onSeeAllProductsClick: () -> Unit
) {
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

    if (flashSaleProducts.isNotEmpty()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(flashSaleProducts) { product ->
                ProductCardDark(
                    product = product,
                    modifier = Modifier.width(160.dp),
                    onProductClick = { onProductClick(product.productId) }
                )
            }
        }
    } else {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(80.dp),
            shape = RoundedCornerShape(12.dp),
            color = DarkCardSecondary
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(color = OrangePrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("No products available", color = TextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
internal fun HomeSuggestedSection(
    suggestedProducts: List<ProductDataModels>,
    isLoading: Boolean,
    onProductClick: (String) -> Unit,
    onSeeAllProductsClick: () -> Unit
) {
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

    if (suggestedProducts.isNotEmpty()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(suggestedProducts) { product ->
                ProductCardDark(
                    product = product,
                    modifier = Modifier.width(160.dp),
                    onProductClick = { onProductClick(product.productId) }
                )
            }
        }
    } else {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(80.dp),
            shape = RoundedCornerShape(12.dp),
            color = DarkCardSecondary
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(color = OrangePrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("No products available", color = TextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}
