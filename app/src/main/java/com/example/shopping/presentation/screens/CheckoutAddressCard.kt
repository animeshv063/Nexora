package com.example.shopping.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun CheckoutAddressCard(
    modifier: Modifier = Modifier,
    recipientName: String,
    recipientPhone: String,
    deliveryAddress: String,
    isAddressValid: Boolean,
    isFetchingLocation: Boolean,
    onGpsClick: () -> Unit,
    onManualEditClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSecondary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Delivery Address", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
                }

                if (isFetchingLocation) {
                    CircularProgressIndicator(color = OrangePrimary, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "$recipientName • $recipientPhone", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAddressValid) deliveryAddress else "⚠️ No delivery address set. Tap 'Edit Manually' or 'GPS Location' below to add your address.",
                fontSize = 13.sp,
                color = if (isAddressValid) TextWhite else OrangePrimary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: GPS Detect vs Manual Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onGpsClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "GPS Location", fontSize = 12.sp, color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onManualEditClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Edit Manually", fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
