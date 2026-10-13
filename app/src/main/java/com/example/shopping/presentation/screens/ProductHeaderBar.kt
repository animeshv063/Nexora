package com.example.shopping.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.AccentCoral
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun ProductHeaderBar(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    product: ProductDataModels,
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
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
                        tint = if (isFavorite) AccentCoral else TextWhite
                    )
                }
            }
        }
    }
}
