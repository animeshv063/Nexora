package com.example.shopping.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.ProductCardDark
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun HomeSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            placeholder = { Text("Search products", color = TextMuted, fontSize = 14.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
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
                IconButton(onClick = onNotificationClick) {
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
}

@Composable
internal fun HomeNotificationDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
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
            TextButton(onClick = onDismissRequest) {
                Text(text = "Close", color = OrangePrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
internal fun HomeSearchResultsView(
    searchQuery: String,
    productsList: List<ProductDataModels>,
    categoriesList: List<CategoryDataModels>,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit
) {
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
}
