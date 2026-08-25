package com.example.shopping.presentation.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.razorpay.Checkout
import org.json.JSONObject

@Composable
fun CheckoutScreen(
    productId: String,
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val context = LocalContext.current
    val productDetailState by viewModel.productDetailState.collectAsState()

    var selectedPaymentMethod by remember { mutableStateOf("Razorpay / UPI / Cards") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) {
            viewModel.fetchProductById(productId)
        }
    }

    val product = productDetailState.data ?: ProductDataModels(
        name = "Product Order",
        price = "0",
        finalPrice = "0",
        image = ""
    )


    val price = product.finalPrice.toDoubleOrNull() ?: product.price.toDoubleOrNull() ?: 1000.0
    val shipping = 50.0
    val total = price + shipping

    fun startRazorpayPayment() {
        val activity = context as? Activity
        if (activity != null) {
            val checkout = Checkout()
            val razorpayKey = context.getString(com.example.shopping.R.string.razorpay_key_id)
            checkout.setKeyID(razorpayKey)

            try {
                val options = JSONObject().apply {
                    put("name", "Fashion Shopping")
                    put("description", product.name)
                    put("currency", "INR")
                    put("amount", (total * 100).toInt())
                    put("prefill.email", "customer@example.com")
                    put("prefill.contact", "9876543210")
                }
                checkout.open(activity, options)
            } catch (e: Exception) {
                Toast.makeText(context, "Payment Initiated: ₹${total.toInt()}", Toast.LENGTH_SHORT).show()
                onOrderSuccess()
            }
        } else {
            Toast.makeText(context, "Order Placed Successfully! 🎉", Toast.LENGTH_SHORT).show()
            onOrderSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
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
            Text(text = "Checkout", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }

        // Delivery Address Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSecondary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Delivery Address", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Rohan Sharma • +91 1234567899", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                Text(text = "Flat 402, Sunset Boulevard, Paris", fontSize = 13.sp, color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Product Summary Card
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
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "₹${product.finalPrice.ifEmpty { product.price }}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OrangePrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Gateway Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSecondary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Payment Method", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)

                Spacer(modifier = Modifier.height(8.dp))

                val methods = listOf("Razorpay / UPI / Cards", "Cash On Delivery (COD)")
                methods.forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPaymentMethod = method }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPaymentMethod == method,
                            onClick = { selectedPaymentMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary, unselectedColor = TextMuted)
                        )
                        Text(text = method, fontSize = 14.sp, color = TextWhite)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Price Breakdown
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
                    Text(text = "Item Total", fontSize = 13.sp, color = TextMuted)
                    Text(text = "₹${price.toInt()}", fontSize = 14.sp, color = TextWhite, fontWeight = FontWeight.Medium)
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

        Spacer(modifier = Modifier.height(24.dp))

        // Pay & Place Order Button
        Button(
            onClick = {
                if (selectedPaymentMethod.startsWith("Razorpay")) {
                    startRazorpayPayment()
                } else {
                    Toast.makeText(context, "Order Placed with COD! 🎉", Toast.LENGTH_LONG).show()
                    onOrderSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Text(text = "Pay & Place Order • ₹${total.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
