package com.example.shopping.presentation.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun HomeCategorySection(
    categoriesList: List<CategoryDataModels>,
    isLoading: Boolean,
    onCategoryClick: (String) -> Unit,
    onSeeAllCategoriesClick: () -> Unit
) {
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

    if (categoriesList.isEmpty()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(68.dp),
            shape = RoundedCornerShape(12.dp),
            color = DarkCardSecondary
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(color = OrangePrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("No categories found", color = TextMuted, fontSize = 13.sp)
                }
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
                            SmartAsyncImage(
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
}
