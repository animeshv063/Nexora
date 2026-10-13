package com.example.shopping.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.shopping.ui.theme.ButtonTextColor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun CheckoutHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "Checkout", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
    }
}

@Composable
fun CheckoutProductSummaryCard(
    product: ProductDataModels,
    size: String,
    quantity: Int,
    itemTotal: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSecondary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmartAsyncImage(
                imageUrl = product.image,
                contentDescription = product.name,
                modifier = Modifier.size(70.dp),
                shape = RoundedCornerShape(10.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "Size: $size • Qty: $quantity", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "₹${itemTotal.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OrangePrimary)
            }
        }
    }
}

@Composable
fun CheckoutPaymentMethodCard(
    selectedPaymentMethod: String,
    onPaymentMethodSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSecondary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Payment Method", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
            Spacer(modifier = Modifier.height(10.dp))

            listOf(
                "Razorpay",
                "Cash on Delivery (COD)"
            ).forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPaymentMethodSelected(method) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPaymentMethod == method,
                        onClick = { onPaymentMethodSelected(method) },
                        colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary, unselectedColor = TextMuted)
                    )
                    Text(
                        text = if (method == "Razorpay") "Razorpay (Online Payment) 💳" else "Cash on Delivery (COD) 💵",
                        fontSize = 14.sp,
                        fontWeight = if (selectedPaymentMethod == method) FontWeight.Bold else FontWeight.Normal,
                        color = TextWhite
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutPriceDetailsCard(
    quantity: Int,
    itemTotal: Double,
    shipping: Double,
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSecondary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Price Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Item Total ($quantity ${if (quantity > 1) "units" else "unit"})", fontSize = 13.sp, color = TextMuted)
                Text(text = "₹${itemTotal.toInt()}", fontSize = 14.sp, color = TextWhite, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Delivery Charges", fontSize = 13.sp, color = TextMuted)
                Text(text = "₹${shipping.toInt()}", fontSize = 14.sp, color = TextWhite, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkInputBorder)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Payable", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(text = "₹${total.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
            }
        }
    }
}

@Composable
fun CheckoutPayButton(
    selectedPaymentMethod: String,
    total: Double,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
    ) {
        Text(
            text = if (selectedPaymentMethod.startsWith("Razorpay")) "Pay with Razorpay • ₹${total.toInt()}" else "Place Order (Cash on Delivery) • ₹${total.toInt()}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ButtonTextColor
        )
    }
}
