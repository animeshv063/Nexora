package com.example.shopping.presentation.screens

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import com.example.shopping.presentation.utils.LocationHelper
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import org.json.JSONObject

@Composable
fun CheckoutScreen(
    productId: String,
    quantity: Int = 1,
    size: String = "M",
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val productDetailState by viewModel.productDetailState.collectAsState()
    val userProfileState by viewModel.userProfileState.collectAsState()
    val placeOrderState by viewModel.placeOrderState.collectAsState()

    var selectedPaymentMethod by remember { mutableStateOf("Razorpay") }

    // Delivery Address States
    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var isFetchingLocation by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }

    // Dialog temporary edit states
    var tempName by remember { mutableStateOf("") }
    var tempPhone by remember { mutableStateOf("") }
    var tempAddress by remember { mutableStateOf("") }
    var showOrderConfirmDialog by remember { mutableStateOf(false) }

    val product = productDetailState.data ?: ProductDataModels(
        name = "Fashion Product",
        price = "0",
        finalPrice = "0",
        image = "",
        productId = productId
    )

    val rawPrice = if (product.finalPrice.isNotBlank() && product.finalPrice != "0") product.finalPrice else product.price
    val unitPrice = rawPrice.toDoubleOrNull() ?: 0.0
    val itemTotal = unitPrice * maxOf(1, quantity)
    val shipping = if (itemTotal > 0) 50.0 else 0.0
    val total = itemTotal + shipping
    val isAddressValid = deliveryAddress.isNotBlank() && !deliveryAddress.contains("Please set", ignoreCase = true)

    fun handlePlaceOrder() {
        if (!isAddressValid) {
            Toast.makeText(context, "⚠️ Please enter your delivery address to place an order", Toast.LENGTH_LONG).show()
            tempName = recipientName
            tempPhone = recipientPhone
            tempAddress = ""
            showAddressDialog = true
            return
        }

        viewModel.placeOrder(
            productId = product.productId.ifEmpty { productId },
            quantity = maxOf(1, quantity),
            address = deliveryAddress.trim(),
            paymentMethod = selectedPaymentMethod,
            onSuccess = {
                Toast.makeText(context, "🎉 Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                onOrderSuccess()
            }
        )
    }

    fun proceedToPayOrOrder() {
        if (!isAddressValid) {
            Toast.makeText(context, "⚠️ Please enter your delivery address to proceed", Toast.LENGTH_LONG).show()
            tempName = recipientName
            tempPhone = recipientPhone
            tempAddress = ""
            showAddressDialog = true
            return
        }
        showOrderConfirmDialog = true
    }

    LaunchedEffect(Unit) {
        com.example.shopping.presentation.utils.PaymentEventManager.paymentResultFlow.collectLatest { event ->
            when (event) {
                is com.example.shopping.presentation.utils.PaymentEventManager.PaymentEvent.Success -> {
                    handlePlaceOrder()
                }
                is com.example.shopping.presentation.utils.PaymentEventManager.PaymentEvent.Error -> {
                    Toast.makeText(context, "Payment status: ${event.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            viewModel.fetchUserProfile(uid)
        }
    }

    LaunchedEffect(userProfileState.data) {
        val user = userProfileState.data?.userData
        if (user != null) {
            val fullName = "${user.firstName} ${user.lastName}".trim()
            recipientName = if (fullName.isNotEmpty()) fullName else (currentUser?.displayName ?: "User")
            recipientPhone = if (user.phoneNumber.isNotEmpty()) user.phoneNumber else "9876543210"
            deliveryAddress = user.address.trim()
        } else if (currentUser != null) {
            recipientName = currentUser.displayName ?: "User"
            recipientPhone = "9876543210"
            deliveryAddress = ""
        }
    }

    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) {
            viewModel.fetchProductById(productId)
        }
    }

    // Google Location Services Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            isFetchingLocation = true
            LocationHelper.fetchCurrentAddress(
                context = context,
                onAddressFound = { address ->
                    isFetchingLocation = false
                    deliveryAddress = address
                    Toast.makeText(context, "📍 Location updated via GPS!", Toast.LENGTH_SHORT).show()

                    // Sync updated address to Firestore
                    if (currentUser != null) {
                        val currentData = userProfileState.data?.userData ?: UserData()
                        val updated = currentData.copy(address = address)
                        viewModel.updateProfile(UserDataParent(nodeId = currentUser.uid, userData = updated))
                    }
                },
                onError = { error ->
                    isFetchingLocation = false
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            Toast.makeText(context, "Location permission is required to detect your location automatically.", Toast.LENGTH_SHORT).show()
        }
    }

    fun startRazorpayPayment() {
        if (!isAddressValid) {
            Toast.makeText(context, "⚠️ Please enter your delivery address before proceeding to payment", Toast.LENGTH_LONG).show()
            tempName = recipientName
            tempPhone = recipientPhone
            tempAddress = ""
            showAddressDialog = true
            return
        }

        val activity = context as? Activity
        if (activity != null) {
            val checkout = Checkout()
            val razorpayKey = context.getString(com.example.shopping.R.string.razorpay_key_id)
            checkout.setKeyID(razorpayKey)

            try {
                val cleanDigits = recipientPhone.filter { it.isDigit() }
                val validContact = if (cleanDigits.length >= 10) cleanDigits.takeLast(10) else "9876543210"
                val validEmail = currentUser?.email?.trim().takeIf { !it.isNullOrBlank() } ?: "customer@nexora.com"
                val amountInPaise = (maxOf(1.0, total) * 100).toLong()

                val options = JSONObject().apply {
                    put("name", "Nexora")
                    put("description", "Order Payment")
                    put("currency", "INR")
                    put("amount", amountInPaise)
                    val theme = JSONObject().apply {
                        put("color", "#FF6D00")
                    }
                    put("theme", theme)
                    val prefill = JSONObject().apply {
                        if (validEmail.isNotBlank()) put("email", validEmail)
                        if (validContact.isNotBlank()) put("contact", validContact)
                    }
                    put("prefill", prefill)
                    val retry = JSONObject().apply {
                        put("enabled", true)
                        put("max_count", 4)
                    }
                    put("retry", retry)
                }
                checkout.open(activity, options)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to launch Razorpay: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Cannot launch Razorpay: Activity context unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    // Manual Address Edit Modal Dialog
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = {
                Text(text = "Edit Delivery Details", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Full Name", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Phone Number", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = tempAddress,
                        onValueChange = { tempAddress = it },
                        label = { Text("Delivery Address", color = TextMuted) },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }
            },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        if (tempAddress.isNotBlank()) {
                            recipientName = tempName
                            recipientPhone = tempPhone
                            deliveryAddress = tempAddress
                            showAddressDialog = false

                            // Sync to Firestore
                            if (currentUser != null) {
                                val currentData = userProfileState.data?.userData ?: UserData()
                                val nameParts = tempName.trim().split(" ")
                                val fName = nameParts.firstOrNull() ?: currentData.firstName
                                val lName = if (nameParts.size > 1) nameParts.subList(1, nameParts.size).joinToString(" ") else currentData.lastName

                                val updated = currentData.copy(
                                    firstName = fName,
                                    lastName = lName,
                                    phoneNumber = tempPhone,
                                    address = tempAddress
                                )
                                viewModel.updateProfile(UserDataParent(nodeId = currentUser.uid, userData = updated))
                            }
                            Toast.makeText(context, "Address updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Address cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(text = "Save Address", color = ButtonTextColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text(text = "Cancel", color = TextWhite)
                }
            }
        )
    }

    val statusBarsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isThreeButton = navBarsBottom >= 30.dp
    val safeTop: Dp = if (statusBarsTop > 0.dp) statusBarsTop + 6.dp else 44.dp
    val safeBottom: Dp = if (isThreeButton) navBarsBottom + 16.dp else navBarsBottom + 8.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(top = safeTop, bottom = safeBottom)
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

        // Delivery Address Card with GPS + Manual Edit
        Card(
            modifier = Modifier
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
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "GPS Location", fontSize = 12.sp, color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            tempName = recipientName
                            tempPhone = recipientPhone
                            tempAddress = if (deliveryAddress.contains("Please set")) "" else deliveryAddress
                            showAddressDialog = true
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Edit Manually", fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.SemiBold)
                    }
                }
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
                com.example.shopping.presentation.utils.SmartAsyncImage(
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
                Spacer(modifier = Modifier.height(10.dp))

                listOf(
                    "Razorpay",
                    "Cash on Delivery (COD)"
                ).forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPaymentMethod = method }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPaymentMethod == method,
                            onClick = { selectedPaymentMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary, unselectedColor = TextMuted)
                        )
                        Text(
                            text = if (method == "Razorpay") "Razorpay Gateway (Cards / UPI / Netbanking) 💳" else "Cash on Delivery (COD) 💵",
                            fontSize = 14.sp,
                            fontWeight = if (selectedPaymentMethod == method) FontWeight.Bold else FontWeight.Normal,
                            color = TextWhite
                        )
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

        Spacer(modifier = Modifier.height(24.dp))

        // Pay & Place Order Button
        Button(
            onClick = { proceedToPayOrOrder() },
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

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Place Order Confirmation Modal Dialog (2-Step Safety Verification)
    if (showOrderConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showOrderConfirmDialog = false },
            title = {
                Text(text = "Confirm Order", fontWeight = FontWeight.Bold, color = TextWhite)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to confirm and place this order?",
                        color = TextWhite,
                        fontSize = 14.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkCardSecondary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Item: ${product.name}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Quantity: $quantity", color = TextMuted, fontSize = 12.sp)
                            Text(text = "Total Payable: ₹${total.toInt()}", color = OrangePrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Payment: $selectedPaymentMethod", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        showOrderConfirmDialog = false
                        if (selectedPaymentMethod.startsWith("Razorpay")) {
                            startRazorpayPayment()
                        } else {
                            handlePlaceOrder()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(
                        text = if (selectedPaymentMethod.startsWith("Razorpay")) "Proceed to Pay" else "Confirm & Place Order",
                        color = ButtonTextColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showOrderConfirmDialog = false }) {
                    Text(text = "Cancel", color = TextMuted)
                }
            }
        )
    }
}
