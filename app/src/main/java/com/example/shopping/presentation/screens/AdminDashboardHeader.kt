package com.example.shopping.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.SuccessGreen
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun AdminDashboardHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Owner Console",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = TextWhite
            )
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SuccessGreen.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
        ) {
            Text(
                text = "● Owner Authenticated",
                color = SuccessGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun AdminDashboardTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    productsCount: Int,
    categoriesCount: Int,
    bannersCount: Int
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = DarkCard,
        contentColor = OrangePrimary,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = OrangePrimary,
                height = 3.dp
            )
        }
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = { Text("➕ Add Product", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 0) OrangePrimary else TextMuted) }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = { Text("🛍️ Products ($productsCount)", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 1) OrangePrimary else TextMuted) }
        )
        Tab(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            text = { Text("📁 Categories ($categoriesCount)", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 2) OrangePrimary else TextMuted) }
        )
        Tab(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            text = { Text("🖼️ Banners ($bannersCount)", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 3) OrangePrimary else TextMuted) }
        )
    }
}
