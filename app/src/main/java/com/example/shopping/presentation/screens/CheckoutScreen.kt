package com.example.shopping.presentation.screens

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import com.example.shopping.presentation.utils.LocationHelper
import com.example.shopping.presentation.utils.PaymentEventManager
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.presentation.viewModels.placeOrder
import com.example.shopping.presentation.viewModels.updateProfile
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import kotlinx.coroutines.flow.collectLatest
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

    var selectedPaymentMethod by rememberSaveable { mutableStateOf("Razorpay") }

    // Delivery Address States
    var recipientName by rememberSaveable { mutableStateOf("") }
    var recipientPhone by rememberSaveable { mutableStateOf("") }
    var deliveryAddress by rememberSaveable { mutableStateOf("") }
    var isFetchingLocation by rememberSaveable { mutableStateOf(false) }
    var showAddressDialog by rememberSaveable { mutableStateOf(false) }

    // Dialog temporary edit states
    var tempName by rememberSaveable { mutableStateOf("") }
    var tempPhone by rememberSaveable { mutableStateOf("") }
    var tempAddress by rememberSaveable { mutableStateOf("") }
    var showOrderConfirmDialog by rememberSaveable { mutableStateOf(false) }

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
    val isAddressValid = deliveryAddress.isNotBlank() && !deliveryAddress.contains("Please set", ignoreCase = true) && !deliveryAddress.contains("No delivery address", ignoreCase = true)

    fun handlePlaceOrder() {
        val userSavedAddress = userProfileState.data?.userData?.address?.trim().orEmpty()
        val effectiveAddress = when {
            deliveryAddress.isNotBlank() && !deliveryAddress.contains("No delivery address", ignoreCase = true) -> deliveryAddress.trim()
            userSavedAddress.isNotBlank() && !userSavedAddress.contains("No delivery address", ignoreCase = true) -> userSavedAddress
            else -> "Default Address (Saved at Checkout)"
        }

        // Persist the delivery address permanently to user profile if logged in
        if (currentUser != null && effectiveAddress.isNotBlank() && !effectiveAddress.contains("No delivery address", ignoreCase = true)) {
            val currentData = userProfileState.data?.userData ?: UserData()
            val nameParts = recipientName.trim().split(" ")
            val fName = nameParts.firstOrNull()?.ifEmpty { currentData.firstName } ?: currentData.firstName
            val lName = if (nameParts.size > 1) nameParts.subList(1, nameParts.size).joinToString(" ") else currentData.lastName

            val updated = currentData.copy(
                firstName = if (fName.isNotEmpty()) fName else (currentUser.displayName?.split(" ")?.firstOrNull() ?: ""),
                lastName = if (lName.isNotEmpty()) lName else (currentUser.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: ""),
                email = currentData.email.ifEmpty { currentUser.email ?: "" },
                phoneNumber = if (recipientPhone.isNotBlank() && recipientPhone != "9876543210") recipientPhone else currentData.phoneNumber,
                address = effectiveAddress
            )
            viewModel.updateProfile(UserDataParent(nodeId = currentUser.uid, userData = updated))
        }

        viewModel.placeOrder(
            productId = product.productId.ifEmpty { productId },
            quantity = maxOf(1, quantity),
            address = effectiveAddress,
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
        PaymentEventManager.paymentResultFlow.collectLatest { event ->
            when (event) {
                is PaymentEventManager.PaymentEvent.Success -> {
                    handlePlaceOrder()
                }
                is PaymentEventManager.PaymentEvent.Error -> {
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
            if (recipientName.isBlank()) {
                recipientName = if (fullName.isNotEmpty()) fullName else (currentUser?.displayName ?: "User")
            }
            if (recipientPhone.isBlank()) {
                recipientPhone = if (user.phoneNumber.isNotEmpty()) user.phoneNumber else "9876543210"
            }
            if (deliveryAddress.isBlank() && user.address.isNotBlank()) {
                deliveryAddress = user.address.trim()
            }
        } else if (currentUser != null) {
            if (recipientName.isBlank()) recipientName = currentUser.displayName ?: "User"
            if (recipientPhone.isBlank()) recipientPhone = "9876543210"
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

                    if (currentUser != null) {
                        val currentData = userProfileState.data?.userData ?: UserData()
                        val nameParts = recipientName.trim().split(" ")
                        val fName = nameParts.firstOrNull()?.ifEmpty { currentData.firstName } ?: currentData.firstName
                        val lName = if (nameParts.size > 1) nameParts.subList(1, nameParts.size).joinToString(" ") else currentData.lastName

                        val updated = currentData.copy(
                            firstName = if (fName.isNotEmpty()) fName else (currentUser.displayName?.split(" ")?.firstOrNull() ?: ""),
                            lastName = if (lName.isNotEmpty()) lName else (currentUser.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: ""),
                            email = currentData.email.ifEmpty { currentUser.email ?: "" },
                            phoneNumber = if (recipientPhone.isNotBlank() && recipientPhone != "9876543210") recipientPhone else currentData.phoneNumber,
                            address = address
                        )
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
                val validContact = if (cleanDigits.length >= 10) cleanDigits.takeLast(10) else ""
                val validEmail = currentUser?.email?.trim().takeIf { !it.isNullOrBlank() } ?: "customer@nexora.com"
                val amountInPaise = (maxOf(1.0, total) * 100).toLong()

                val options = JSONObject().apply {
                    put("name", "Nexora")
                    put("description", "Order Payment")
                    put("currency", "INR")
                    put("amount", amountInPaise)
                    put("send_sms_hash", true)
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
        CheckoutAddressDialog(
            tempName = tempName,
            onTempNameChange = { tempName = it },
            tempPhone = tempPhone,
            onTempPhoneChange = { tempPhone = it },
            tempAddress = tempAddress,
            onTempAddressChange = { tempAddress = it },
            onDismissRequest = { showAddressDialog = false },
            onSaveAddress = {
                if (tempAddress.isNotBlank()) {
                    recipientName = tempName
                    recipientPhone = tempPhone
                    deliveryAddress = tempAddress
                    showAddressDialog = false

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
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        CheckoutHeader(onBackClick = onBackClick)

        // Delivery Address Card with GPS + Manual Edit
        CheckoutAddressCard(
            recipientName = recipientName,
            recipientPhone = recipientPhone,
            deliveryAddress = deliveryAddress,
            isAddressValid = isAddressValid,
            isFetchingLocation = isFetchingLocation,
            onGpsClick = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onManualEditClick = {
                tempName = recipientName
                tempPhone = recipientPhone
                tempAddress = if (deliveryAddress.contains("Please set")) "" else deliveryAddress
                showAddressDialog = true
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Product Summary Card
        CheckoutProductSummaryCard(
            product = product,
            size = size,
            quantity = quantity,
            itemTotal = itemTotal
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Gateway Selection
        CheckoutPaymentMethodCard(
            selectedPaymentMethod = selectedPaymentMethod,
            onPaymentMethodSelected = { selectedPaymentMethod = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Price Breakdown
        CheckoutPriceDetailsCard(
            quantity = quantity,
            itemTotal = itemTotal,
            shipping = shipping,
            total = total
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Pay & Place Order Button
        CheckoutPayButton(
            selectedPaymentMethod = selectedPaymentMethod,
            total = total,
            onClick = { proceedToPayOrOrder() }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Place Order Confirmation Modal Dialog (2-Step Safety Verification)
    if (showOrderConfirmDialog) {
        CheckoutOrderConfirmDialog(
            product = product,
            quantity = quantity,
            total = total,
            selectedPaymentMethod = selectedPaymentMethod,
            onDismissRequest = { showOrderConfirmDialog = false },
            onConfirm = {
                showOrderConfirmDialog = false
                if (selectedPaymentMethod.startsWith("Razorpay")) {
                    startRazorpayPayment()
                } else {
                    handlePlaceOrder()
                }
            }
        )
    }
}
