package com.example.shopping.presentation.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import com.example.shopping.presentation.utils.CustomTextField
import com.example.shopping.presentation.utils.ImageCropUtils
import com.example.shopping.presentation.utils.LogOutAlertDialog
import com.example.shopping.presentation.utils.ProfilePhotoStorage
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DangerRed
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.PrimaryAccent
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun ProfileScreen(
    viewModel: ShoppingAppViewModel,
    onLogOutSuccess: () -> Unit,
    onAdminClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userProfileState by viewModel.userProfileState.collectAsState()
    val profileUpdateState by viewModel.profileUpdateState.collectAsState()
    val deleteAccountState by viewModel.deleteAccountState.collectAsState()
    val userOrdersState by viewModel.userOrdersState.collectAsState()

    var isEditing by rememberSaveable { mutableStateOf(false) }
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }
    var showOrdersDialog by rememberSaveable { mutableStateOf(false) }
    var showResetOrdersConfirm by rememberSaveable { mutableStateOf(false) }
    var orderToCancel by    remember { mutableStateOf<com.example.shopping.domain.models.OrderDataModel?>(null) }

    var selectedBitmapForCrop by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Form fields
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var profileImage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            if (ProfilePhotoStorage.isPhotoRemoved(context, uid)) {
                profileImage = ""
            } else {
                val localSaved = ProfilePhotoStorage.getLocalProfileAvatar(context, uid)
                if (localSaved != null) {
                    profileImage = localSaved
                }
            }
            viewModel.fetchUserProfile(uid)
            viewModel.fetchUserOrders()
        }
    }

    LaunchedEffect(userProfileState.data) {
        val uid = currentUser?.uid
        val isRemoved = uid != null && ProfilePhotoStorage.isPhotoRemoved(context, uid)

        val user = userProfileState.data?.userData
        if (user != null) {
            firstName = user.firstName
            lastName = user.lastName
            phone = user.phoneNumber
            address = user.address

            if (isRemoved || user.profileImage.isEmpty()) {
                profileImage = ""
            } else {
                val localSaved = uid?.let { ProfilePhotoStorage.getLocalProfileAvatar(context, it) }
                if (localSaved != null) {
                    profileImage = localSaved
                } else if (user.profileImage.isNotEmpty()) {
                    if (user.profileImage.startsWith("data:image") || user.profileImage.length > 200) {
                        coroutineScope.launch {
                            val bmp = ImageCropUtils.base64ToBitmap(user.profileImage)
                            if (bmp != null && currentUser != null) {
                                val saved = withContext(Dispatchers.IO) {
                                    ProfilePhotoStorage.saveCircularBitmapLocally(context, currentUser.uid, bmp)
                                }
                                if (saved != null) profileImage = saved
                            }
                        }
                    } else {
                        profileImage = user.profileImage
                    }
                }
            }
        } else if (currentUser != null) {
            val names = (currentUser.displayName ?: "").split(" ")
            firstName = names.firstOrNull() ?: ""
            lastName = if (names.size > 1) names.subList(1, names.size).joinToString(" ") else ""

            if (isRemoved) {
                profileImage = ""
            } else {
                val localSaved = ProfilePhotoStorage.getLocalProfileAvatar(context, currentUser.uid)
                profileImage = localSaved ?: ""
            }
        }
    }

    LaunchedEffect(profileUpdateState) {
        profileUpdateState.data?.let {
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            isEditing = false
            viewModel.resetProfileUpdateState()
        }
        profileUpdateState.errorMessage?.let {
            // Silently recover if firestore rules are restrictive; local profile is already safely set
            if (!it.contains("permission", ignoreCase = true)) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
            viewModel.resetProfileUpdateState()
        }
    }

    LaunchedEffect(deleteAccountState) {
        deleteAccountState.data?.let {
            currentUser?.uid?.let { uid -> ProfilePhotoStorage.clearLocalProfileAvatar(context, uid) }
            Toast.makeText(context, "Account successfully deleted", Toast.LENGTH_LONG).show()
            viewModel.resetDeleteAccountState()
            onLogOutSuccess()
        }
        deleteAccountState.errorMessage?.let { error ->
            currentUser?.uid?.let { uid -> ProfilePhotoStorage.clearLocalProfileAvatar(context, uid) }
            auth.signOut()
            Toast.makeText(context, "Account removed. Please log in again.", Toast.LENGTH_SHORT).show()
            viewModel.resetDeleteAccountState()
            onLogOutSuccess()
        }
    }

    // Image Picker Launcher with Interactive Circular Crop Dialog
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    ImageCropUtils.loadBitmap(context, uri)
                }
                if (bitmap != null) {
                    selectedBitmapForCrop = bitmap
                }
            }
        }
    }

    // Circular Crop Interactive Dialog
    selectedBitmapForCrop?.let { bmp ->
        com.example.shopping.presentation.utils.ImageCropDialog(
            bitmap = bmp,
            onDismissRequest = { selectedBitmapForCrop = null },
            onCropConfirmed = { croppedCircularBitmap ->
                selectedBitmapForCrop = null
                if (currentUser != null) {
                    ProfilePhotoStorage.setPhotoRemoved(context, currentUser.uid, false)
                    coroutineScope.launch {
                        val localPath = withContext(Dispatchers.IO) {
                            ProfilePhotoStorage.saveCircularBitmapLocally(context, currentUser.uid, croppedCircularBitmap)
                        }

                        if (localPath != null) {
                            // Update UI state immediately
                            profileImage = localPath
                            Toast.makeText(context, "Profile photo updated", Toast.LENGTH_SHORT).show()

                            // Sync base64 to firestore
                            val base64Avatar = withContext(Dispatchers.IO) {
                                ImageCropUtils.bitmapToBase64(croppedCircularBitmap)
                            }
                            val currentData = userProfileState.data?.userData ?: UserData(
                                firstName = firstName,
                                lastName = lastName,
                                email = currentUser.email ?: "",
                                phoneNumber = phone,
                                address = address
                            )
                            val updated = currentData.copy(profileImage = base64Avatar)
                            viewModel.updateProfile(UserDataParent(nodeId = currentUser.uid, userData = updated))
                        }
                    }
                }
            }
        )
    }





    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogOutAlertDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                auth.signOut()
                showLogoutDialog = false
                onLogOutSuccess()
            }
        )
    }

    // Delete Account Confirmation Dialog
    val isGoogleUser = currentUser?.providerData?.any { it.providerId == "google.com" } == true
    var deleteConfirmPassword by remember { mutableStateOf("") }
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteAccountDialog = false
                deleteConfirmPassword = ""
            },
            title = {
                Text(text = "Delete Account?", fontWeight = FontWeight.Bold, color = TextWhite)
            },
            text = {
                Column {
                    Text(
                        text = if (isGoogleUser) {
                            "Are you sure you want to delete your account? This will permanently erase your profile, shopping cart, and wishlist from this app and unlink your Google account."
                        } else {
                            "This will permanently delete your profile, orders, and authentication records. Please enter your password to confirm."
                        },
                        color = TextMuted,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    if (!isGoogleUser) {
                        Spacer(modifier = Modifier.height(14.dp))
                        CustomTextField(
                            value = deleteConfirmPassword,
                            onValueChange = { deleteConfirmPassword = it },
                            label = "Enter Password to Confirm",
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                        )
                    }
                }
            },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        currentUser?.uid?.let { uid ->
                            viewModel.deleteAccount(uid, if (isGoogleUser) "" else deleteConfirmPassword.trim())
                            showDeleteAccountDialog = false
                            deleteConfirmPassword = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text(text = "Delete Forever", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteAccountDialog = false
                    deleteConfirmPassword = ""
                }) {
                    Text(text = "Cancel", color = TextWhite)
                }
            }
        )
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Text(
            text = "My Profile",
            color = TextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Avatar (Clean initials placeholder or custom picked photo)
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(120.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = DarkCardSecondary,
                modifier = Modifier
                    .size(120.dp)
                    .border(2.5.dp, PrimaryAccent, CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {

                if (profileImage.isNotEmpty()) {
                    com.example.shopping.presentation.utils.SmartAsyncImage(
                        imageUrl = profileImage,
                        contentDescription = "Profile Photo",
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Clean initials avatar
                    val initial = (firstName.firstOrNull() ?: currentUser?.email?.firstOrNull() ?: 'U').uppercaseChar()
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial.toString(),
                            color = PrimaryAccent,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Edit photo camera icon badge
            Surface(
                shape = CircleShape,
                color = PrimaryAccent,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Change Photo",
                        tint = com.example.shopping.ui.theme.ButtonTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Action options for Photo (Change / Remove Photo)
        if (profileImage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remove Photo",
                color = DangerRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable {
                        profileImage = ""
                        if (currentUser != null) {
                            ProfilePhotoStorage.setPhotoRemoved(context, currentUser.uid, true)
                            ProfilePhotoStorage.clearLocalProfileAvatar(context, currentUser.uid)
                            val currentData = userProfileState.data?.userData ?: UserData(
                                firstName = firstName,
                                lastName = lastName,
                                email = currentUser.email ?: "",
                                phoneNumber = phone,
                                address = address
                            )
                            val updated = currentData.copy(profileImage = "")
                            viewModel.updateProfile(UserDataParent(nodeId = currentUser.uid, userData = updated))
                            Toast.makeText(context, "Profile photo removed", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(4.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap circle or badge to choose photo",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        // First Name & Last Name row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "First Name",
                    readOnly = !isEditing
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Last Name",
                    readOnly = !isEditing
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        CustomTextField(
            value = currentUser?.email ?: "",
            onValueChange = {},
            label = "Email Address",
            readOnly = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        CustomTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            readOnly = !isEditing
        )

        Spacer(modifier = Modifier.height(14.dp))

        CustomTextField(
            value = address,
            onValueChange = { address = it },
            label = "Address",
            readOnly = !isEditing
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Edit / Save Profile Button
        if (isEditing) {
            Button(
                onClick = {
                    if (currentUser != null) {
                        val updated = UserData(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            email = currentUser.email ?: "",
                            phoneNumber = phone.trim(),
                            address = address.trim(),
                            profileImage = profileImage
                        )
                        viewModel.updateProfile(UserDataParent(nodeId = currentUser.uid, userData = updated))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
            ) {
                if (profileUpdateState.isLoading) {
                    CircularProgressIndicator(color = com.example.shopping.ui.theme.ButtonTextColor, modifier = Modifier.size(20.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = com.example.shopping.ui.theme.ButtonTextColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = com.example.shopping.ui.theme.ButtonTextColor)
                    }
                }


            }
        } else {
            OutlinedButton(
                onClick = { isEditing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryAccent),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkBg)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = PrimaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Edit Profile", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = PrimaryAccent)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // My Orders Card
        val ordersList = userOrdersState.data ?: emptyList()
        val isOrdersLoading = userOrdersState.isLoading
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkInputBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showOrdersDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = PrimaryAccent, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "My Orders",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextWhite
                        )
                        Text(
                            text = if (isOrdersLoading && ordersList.isEmpty()) "Loading orders..." else if (ordersList.isEmpty()) "No active orders" else "${ordersList.size} orders placed",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "View",
                        color = PrimaryAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Orders List Dialog
        if (showOrdersDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showOrdersDialog = false },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Order History (${ordersList.size})",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 17.sp
                        )
                        val hasActiveOrders = ordersList.any { !it.status.equals("Cancelled", ignoreCase = true) }
                        if (ordersList.isNotEmpty()) {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    if (hasActiveOrders) {
                                        Toast.makeText(context, "⚠️ Please cancel all live orders before resetting order history!", Toast.LENGTH_LONG).show()
                                    } else {
                                        showResetOrdersConfirm = true
                                    }
                                }
                            ) {
                                Text(
                                    text = "Reset 🗑️",
                                    color = if (hasActiveOrders) TextMuted else DangerRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                text = {
                    if (isOrdersLoading && ordersList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryAccent, modifier = Modifier.size(32.dp))
                        }
                    } else if (ordersList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "You have not placed any orders yet.", color = TextMuted, fontSize = 14.sp)
                        }
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.height(360.dp)
                        ) {
                            items(ordersList) { order ->
                                val isCancelled = order.status.equals("Cancelled", ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkCardSecondary,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Qty: ${order.quantity} ${if (order.quantity > 1) "units" else "unit"}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = TextWhite
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (isCancelled) DangerRed.copy(alpha = 0.15f) else com.example.shopping.ui.theme.SuccessGreen.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = if (isCancelled) "Cancelled" else "Order Placed",
                                                    color = if (isCancelled) DangerRed else com.example.shopping.ui.theme.SuccessGreen,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Status: ${if (isCancelled) "Cancelled" else "Order Placed"}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isCancelled) DangerRed else com.example.shopping.ui.theme.SuccessGreen
                                        )
                                        val orderAddress = if (order.address.isNotBlank() && !order.address.contains("Please set", ignoreCase = true)) {
                                            order.address
                                        } else if (address.isNotBlank() && !address.contains("Please set", ignoreCase = true)) {
                                            address
                                        } else {
                                            "Home Delivery"
                                        }
                                        Text(text = "Delivery Address: $orderAddress", fontSize = 12.sp, color = TextWhite, maxLines = 2)

                                        if (!isCancelled) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedButton(
                                                onClick = {
                                                    orderToCancel = order
                                                },
                                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed)
                                            ) {
                                                Text(text = "Cancel Order & Restore Stock", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = DarkCard,
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { showOrdersDialog = false }) {
                        Text(text = "Close", color = PrimaryAccent)
                    }
                }
            )
        }

        // Cancel Order Confirmation Dialog
        orderToCancel?.let { targetOrder ->
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { orderToCancel = null },
                title = { Text("Confirm Cancellation", fontWeight = FontWeight.Bold, color = TextWhite) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Are you sure you want to cancel this order?",
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkCardSecondary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Item: ${targetOrder.productName.ifEmpty { "Product" }}",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(text = "Quantity: ${targetOrder.quantity}", color = TextMuted, fontSize = 12.sp)
                                Text(text = "Payment Method: ${targetOrder.paymentMethod}", color = PrimaryAccent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                },
                containerColor = DarkCard,
                confirmButton = {
                    androidx.compose.material3.Button(
                        onClick = {
                            val ord = targetOrder
                            orderToCancel = null
                            viewModel.cancelOrder(ord.orderId, ord.productId, ord.quantity) {
                                Toast.makeText(context, "Order Cancelled & Stock Restored! 🔄", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text("Yes, Cancel Order", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { orderToCancel = null }) {
                        Text("No, Keep Order", color = TextMuted)
                    }
                }
            )
        }

        // Reset Orders Confirmation Dialog
        if (showResetOrdersConfirm) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showResetOrdersConfirm = false },
                title = { Text("Reset Order History", fontWeight = FontWeight.Bold, color = TextWhite) },
                text = { Text("Are you sure you want to clear your entire order history? This will permanently delete your order records.", color = TextMuted) },
                containerColor = DarkCard,
                confirmButton = {
                    androidx.compose.material3.Button(
                        onClick = {
                            showResetOrdersConfirm = false
                            viewModel.resetOrderHistory {
                                Toast.makeText(context, "Order history reset successfully! 🗑️", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text("Clear All", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showResetOrdersConfirm = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Appearance Theme Mode Switcher Card (Dark / Light Mode)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkInputBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (com.example.shopping.ui.theme.ThemeManager.isDarkMode) "Dark Mode" else "Light Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextWhite
                    )
                    Text(
                        text = if (com.example.shopping.ui.theme.ThemeManager.isDarkMode) "Soft carbon black theme" else "Soft warm pearl light theme",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
                androidx.compose.material3.Switch(
                    checked = com.example.shopping.ui.theme.ThemeManager.isDarkMode,
                    onCheckedChange = { com.example.shopping.ui.theme.ThemeManager.toggleTheme() },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = com.example.shopping.ui.theme.SoftDarkCardSecondary,
                        uncheckedThumbColor = Color(0xFF181920),
                        uncheckedTrackColor = com.example.shopping.ui.theme.WarmLightCardSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 👑 App Owner / Admin Portal (Horizontal, Centered)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAdminClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "👑 Owner Product Portal (Manage)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Log Out Button
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkCardSecondary)
        ) {
            Text(text = "Log Out", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
        }


        Spacer(modifier = Modifier.height(18.dp))

        // Delete Account Button
        OutlinedButton(
            onClick = { showDeleteAccountDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = DangerRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Delete Account", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = DangerRed)
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

