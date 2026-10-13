package com.example.shopping.presentation.screens

import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.OrderDataModel
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import com.example.shopping.presentation.utils.ImageCropDialog
import com.example.shopping.presentation.utils.ImageCropUtils
import com.example.shopping.presentation.utils.LogOutAlertDialog
import com.example.shopping.presentation.utils.ProfilePhotoStorage
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.presentation.viewModels.deleteAccount
import com.example.shopping.presentation.viewModels.fetchUserOrders
import com.example.shopping.presentation.viewModels.resetDeleteAccountState
import com.example.shopping.presentation.viewModels.resetProfileUpdateState
import com.example.shopping.presentation.viewModels.updateProfile
import com.example.shopping.ui.theme.DangerRed
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.PrimaryAccent
import com.example.shopping.ui.theme.SoftDarkCardSecondary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.example.shopping.ui.theme.ThemeManager
import com.example.shopping.ui.theme.WarmLightCardSecondary
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit = {},
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
    var orderToCancel by remember { mutableStateOf<OrderDataModel?>(null) }
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
        deleteAccountState.errorMessage?.let {
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
        ImageCropDialog(
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
                            profileImage = localPath
                            Toast.makeText(context, "Profile photo updated", Toast.LENGTH_SHORT).show()

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
    if (showDeleteAccountDialog) {
        ProfileDeleteAccountDialog(
            isGoogleUser = isGoogleUser,
            onDismissRequest = { showDeleteAccountDialog = false },
            onConfirmDelete = { pwd ->
                currentUser?.uid?.let { uid ->
                    viewModel.deleteAccount(uid, pwd)
                    showDeleteAccountDialog = false
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileTopBar(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Avatar
        ProfileAvatarSection(
            profileImage = profileImage,
            firstName = firstName,
            email = currentUser?.email,
            onPickImage = { imagePickerLauncher.launch("image/*") },
            onRemovePhoto = {
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
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields and Edit/Save
        ProfileFormSection(
            firstName = firstName,
            onFirstNameChange = { firstName = it },
            lastName = lastName,
            onLastNameChange = { lastName = it },
            email = currentUser?.email ?: "",
            phone = phone,
            onPhoneChange = { phone = it },
            address = address,
            onAddressChange = { address = it },
            isEditing = isEditing,
            isSaving = profileUpdateState.isLoading,
            onSaveClick = {
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
            onEditClick = { isEditing = true }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // My Orders Card & Dialogs
        val ordersList = userOrdersState.data ?: emptyList()
        val isOrdersLoading = userOrdersState.isLoading
        ProfileOrdersCard(
            ordersList = ordersList,
            isOrdersLoading = isOrdersLoading,
            onClick = { showOrdersDialog = true }
        )

        if (showOrdersDialog) {
            OrdersListDialog(
                ordersList = ordersList,
                isOrdersLoading = isOrdersLoading,
                address = address,
                onDismissRequest = { showOrdersDialog = false },
                onRequestReset = { showResetOrdersConfirm = true },
                onCancelOrderClick = { orderToCancel = it }
            )
        }

        orderToCancel?.let { targetOrder ->
            CancelOrderConfirmDialog(
                targetOrder = targetOrder,
                viewModel = viewModel,
                onDismissRequest = { orderToCancel = null }
            )
        }

        if (showResetOrdersConfirm) {
            ResetOrdersConfirmDialog(
                viewModel = viewModel,
                onDismissRequest = { showResetOrdersConfirm = false }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        ProfileSettingsSection(
            onAdminClick = onAdminClick,
            onLogoutClick = { showLogoutDialog = true },
            onDeleteAccountClick = { showDeleteAccountDialog = true }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}
