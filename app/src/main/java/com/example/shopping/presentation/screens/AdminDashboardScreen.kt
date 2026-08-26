package com.example.shopping.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.utils.sanitizeImageUrl
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.SuccessGreen
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    val productsState by viewModel.productsState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()
    val bannerState by viewModel.bannerState.collectAsState()
    val adminActionState by viewModel.adminActionState.collectAsState()

    var isAuthenticated by remember { mutableStateOf(false) }
    var isVerifyingOwner by remember { mutableStateOf(false) }
    var ownerUsername by remember { mutableStateOf("") }
    var ownerPassword by remember { mutableStateOf("") }

    var selectedTab by remember { mutableIntStateOf(0) }

    val productsList = productsState.data ?: emptyList()
    val categoriesList = categoriesState.data?.filterNotNull() ?: emptyList()
    val bannerList = bannerState.data ?: emptyList()

    // 10 Fixed Allowed Categories for Product selector
    val predefinedCategories = listOf(
        "Shirts",
        "T-Shirts",
        "Jeans",
        "Trousers",
        "Jackets",
        "Footwear",
        "Kids Wear",
        "Sherwani",
        "Pajamas",
        "Accessories"
    )

    // ==========================================
    // Product Form States
    // ==========================================
    var prodName by remember { mutableStateOf("") }
    var prodCategory by remember { mutableStateOf("Shirts") }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var prodPrice by remember { mutableStateOf("") }
    var prodFinalPrice by remember { mutableStateOf("") }
    var prodAvailableUnits by remember { mutableStateOf("30") }
    var prodInitialUnits by remember { mutableStateOf("30") }
    var prodCreateBy by remember { mutableStateOf("Animesh") }
    var prodImage by remember { mutableStateOf("") }
    var prodDescription by remember { mutableStateOf("") }

    // Feedback Alert Dialogs
    var actionDialogTitle by remember { mutableStateOf<String?>(null) }
    var actionDialogMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessDialog by remember { mutableStateOf(false) }
    var duplicateWarningMessage by remember { mutableStateOf<String?>(null) }

    // Edit Product Dialog State
    var editingProduct by remember { mutableStateOf<ProductDataModels?>(null) }
    var editName by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("Shirts") }
    var editCategoryExpanded by remember { mutableStateOf(false) }
    var editPrice by remember { mutableStateOf("") }
    var editFinalPrice by remember { mutableStateOf("") }
    var editUnits by remember { mutableStateOf("") }
    var editImage by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var deletingProductId by remember { mutableStateOf<String?>(null) }
    var manageSearchQuery by remember { mutableStateOf("") }

    // ==========================================
    // Category Form & Edit States
    // ==========================================
    var newCatName by remember { mutableStateOf("") }
    var newCatImage by remember { mutableStateOf("") }
    var newCatCreateBy by remember { mutableStateOf("Animesh") }

    var editingCategory by remember { mutableStateOf<CategoryDataModels?>(null) }
    var editCatName by remember { mutableStateOf("") }
    var editCatImage by remember { mutableStateOf("") }
    var editCatCreateBy by remember { mutableStateOf("Animesh") }

    // ==========================================
    // Banner Form & Edit States
    // ==========================================
    var newBanName by remember { mutableStateOf("") }
    var newBanImage by remember { mutableStateOf("") }

    var editingBanner by remember { mutableStateOf<BannerDataModels?>(null) }
    var editBanName by remember { mutableStateOf("") }
    var editBanImage by remember { mutableStateOf("") }
    var deletingBanner by remember { mutableStateOf<BannerDataModels?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
        viewModel.fetchProducts()
        viewModel.fetchCategories()
        viewModel.fetchBanners()
    }

    val statusBarsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isThreeButton = navBarsBottom >= 30.dp
    val safeTop: Dp = if (statusBarsTop > 0.dp) statusBarsTop + 8.dp else 44.dp
    val safeBottom: Dp = if (isThreeButton) navBarsBottom + 16.dp else navBarsBottom + 8.dp

    if (!isAuthenticated) {
        // Secure Owner Login Gate
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(top = safeTop, bottom = safeBottom)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.Start)
                    .size(40.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = OrangePrimary,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "App Owner Portal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Sign in with your Owner Credentials",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = ownerUsername,
                        onValueChange = { ownerUsername = it },
                        label = { Text("Owner ID") },
                        placeholder = { Text("Enter your Owner ID", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = ownerPassword,
                        onValueChange = { ownerPassword = it },
                        label = { Text("Owner Password") },
                        placeholder = { Text("Enter your Password", color = TextMuted) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val inputUser = ownerUsername.trim()
                            val inputPass = ownerPassword.trim()
                            if (inputUser.isEmpty() || inputPass.isEmpty()) {
                                Toast.makeText(context, "Please enter Owner ID and Password", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isVerifyingOwner = true
                            firestore.collection("Admin").document("credentials").get()
                                .addOnSuccessListener { doc ->
                                    isVerifyingOwner = false
                                    val storedUser = doc.getString("username")
                                    val storedPass = doc.getString("password")

                                    val isMatch = if (doc.exists() && storedUser != null && storedPass != null) {
                                        inputUser.equals(storedUser, ignoreCase = true) && inputPass == storedPass
                                    } else {
                                        inputUser.equals("AIZUI", ignoreCase = true) && inputPass == "KKKS"
                                    }

                                    if (isMatch) {
                                        isAuthenticated = true
                                        Toast.makeText(context, "Welcome, Owner! 🔑", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "❌ Invalid Owner Credentials", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    isVerifyingOwner = false
                                    if (inputUser.equals("AIZUI", ignoreCase = true) && inputPass == "KKKS") {
                                        isAuthenticated = true
                                        Toast.makeText(context, "Welcome, Owner! 🔑", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "❌ Invalid Credentials", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        if (isVerifyingOwner) {
                            CircularProgressIndicator(color = Color(0xFF111111), modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = "Unlock Admin Console", fontWeight = FontWeight.ExtraBold, color = Color(0xFF111111), fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    } else {
        // Authenticated Owner Console
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(top = safeTop, bottom = safeBottom)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
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

            // 4 Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkCard,
                contentColor = OrangePrimary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = OrangePrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("➕ Add Product", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🛍️ Products (${productsList.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("📁 Categories (${categoriesList.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("🖼️ Banners (${bannerList.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // ==========================================
                    // TAB 0: ➕ Add Product Form
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "New Product Document",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextWhite
                        )
                        Text(
                            text = "Firestore Collection: Products",
                            fontSize = 12.sp,
                            color = OrangePrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Left-Right Structured Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
                            border = BorderStroke(1.dp, DarkInputBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {

                                KeyValRow(key = "createBy", value = prodCreateBy, onValueChange = { prodCreateBy = it })
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "availableUnits",
                                        color = OrangePrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(110.dp)
                                    )
                                    OutlinedTextField(
                                        value = prodAvailableUnits,
                                        onValueChange = {
                                            prodAvailableUnits = it
                                            prodInitialUnits = it
                                        },
                                        placeholder = { Text("30", color = TextMuted) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextWhite,
                                            unfocusedTextColor = TextWhite,
                                            focusedBorderColor = OrangePrimary,
                                            unfocusedBorderColor = DarkInputBorder,
                                            focusedContainerColor = DarkInputBg,
                                            unfocusedContainerColor = DarkInputBg
                                        )
                                    )
                                }
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "initialUnits",
                                        color = OrangePrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(110.dp)
                                    )
                                    OutlinedTextField(
                                        value = prodInitialUnits,
                                        onValueChange = { prodInitialUnits = it },
                                        placeholder = { Text("30", color = TextMuted) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextWhite,
                                            unfocusedTextColor = TextWhite,
                                            focusedBorderColor = OrangePrimary,
                                            unfocusedBorderColor = DarkInputBorder,
                                            focusedContainerColor = DarkInputBg,
                                            unfocusedContainerColor = DarkInputBg
                                        )
                                    )
                                }
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                // Category Dropdown (Strict selection)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "category",
                                        color = OrangePrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(110.dp)
                                    )

                                    ExposedDropdownMenuBox(
                                        expanded = isCategoryDropdownExpanded,
                                        onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            value = prodCategory,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = TextWhite,
                                                unfocusedTextColor = TextWhite,
                                                focusedBorderColor = OrangePrimary,
                                                unfocusedBorderColor = DarkInputBorder,
                                                focusedContainerColor = DarkInputBg,
                                                unfocusedContainerColor = DarkInputBg
                                            )
                                        )

                                        ExposedDropdownMenu(
                                            expanded = isCategoryDropdownExpanded,
                                            onDismissRequest = { isCategoryDropdownExpanded = false },
                                            modifier = Modifier.background(DarkCard)
                                        ) {
                                            predefinedCategories.forEach { cat ->
                                                DropdownMenuItem(
                                                    text = { Text(cat, color = TextWhite, fontSize = 14.sp) },
                                                    onClick = {
                                                        prodCategory = cat
                                                        isCategoryDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                KeyValRow(key = "name", value = prodName, placeholder = "e.g. Classic Oxford Cotton Shirt", onValueChange = { prodName = it })
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                KeyValRow(key = "price", value = prodPrice, placeholder = "e.g. 1999", isNumber = true, onValueChange = { prodPrice = it })
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                KeyValRow(key = "finalPrice", value = prodFinalPrice, placeholder = "e.g. 1299", isNumber = true, onValueChange = { prodFinalPrice = it })
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                KeyValRow(key = "image", value = prodImage, placeholder = "Paste image link https://...", onValueChange = { prodImage = it })

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkInputBg,
                                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                                        modifier = Modifier.clickable {
                                            prodImage = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1000&q=80"
                                        }
                                    ) {
                                        Text(
                                            text = "💡 Shoes",
                                            fontSize = 11.sp,
                                            color = OrangePrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkInputBg,
                                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                                        modifier = Modifier.clickable {
                                            prodImage = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=1000&q=80"
                                        }
                                    ) {
                                        Text(
                                            text = "💡 Shirt",
                                            fontSize = 11.sp,
                                            color = OrangePrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkInputBg,
                                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                                        modifier = Modifier.clickable {
                                            prodImage = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&q=80"
                                        }
                                    ) {
                                        Text(
                                            text = "💡 Watch",
                                            fontSize = 11.sp,
                                            color = OrangePrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "description",
                                        color = OrangePrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .width(110.dp)
                                            .padding(top = 12.dp)
                                    )
                                    OutlinedTextField(
                                        value = prodDescription,
                                        onValueChange = { prodDescription = it },
                                        placeholder = { Text("Premium 100% breathable cotton...", color = TextMuted) },
                                        minLines = 3,
                                        maxLines = 5,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextWhite,
                                            unfocusedTextColor = TextWhite,
                                            focusedBorderColor = OrangePrimary,
                                            unfocusedBorderColor = DarkInputBorder,
                                            focusedContainerColor = DarkInputBg,
                                            unfocusedContainerColor = DarkInputBg
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Portrait 3:4 Product Preview
                        Text(text = "Live Portrait (3:4) Product Preview", fontWeight = FontWeight.SemiBold, color = TextWhite, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            if (prodImage.isNotBlank()) {
                                SmartAsyncImage(
                                    imageUrl = prodImage,
                                    contentDescription = "Product Preview",
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = OrangePrimary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Live Product Image Preview", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "Enter or paste any product image URL above to preview", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        // Submit Add Product Button
                        Button(
                            onClick = {
                                val name = prodName.trim()
                                val price = prodPrice.trim()
                                val finalPrice = prodFinalPrice.trim().ifEmpty { price }
                                val image = sanitizeImageUrl(prodImage)
                                val desc = prodDescription.trim()
                                val available = prodAvailableUnits.toIntOrNull() ?: 30
                                val initial = prodInitialUnits.toIntOrNull() ?: available

                                if (name.isEmpty() || price.isEmpty() || image.isEmpty()) {
                                    actionDialogTitle = "Incomplete Fields"
                                    actionDialogMessage = "Please provide Name, Price, and a valid Image link."
                                    isSuccessDialog = false
                                    return@Button
                                }

                                val isDuplicateImage = productsList.any { it.image.equals(image, ignoreCase = true) }
                                if (isDuplicateImage) {
                                    duplicateWarningMessage = "Warning: A product with this exact image link already exists in Firestore. Do you still want to proceed?"
                                    return@Button
                                }

                                val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
                                val product = ProductDataModels(
                                    name = name,
                                    price = price,
                                    finalPrice = finalPrice,
                                    category = prodCategory,
                                    image = image,
                                    description = desc,
                                    availableUnits = available,
                                    initialUnits = initial,
                                    createBy = prodCreateBy.ifEmpty { "Animesh" },
                                    lastResetDay = currentDay,
                                    date = System.currentTimeMillis()
                                )

                                viewModel.addProduct(
                                    product = product,
                                    onSuccess = {
                                        actionDialogTitle = "Product Added! 🎉"
                                        actionDialogMessage = "Successfully created product '$name' in Firestore."
                                        isSuccessDialog = true
                                        prodName = ""
                                        prodPrice = ""
                                        prodFinalPrice = ""
                                        prodImage = ""
                                        prodDescription = ""
                                    },
                                    onError = { error ->
                                        actionDialogTitle = "Add Failed"
                                        actionDialogMessage = "Error: $error"
                                        isSuccessDialog = false
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            if (adminActionState.isLoading) {
                                CircularProgressIndicator(color = Color(0xFF111111), modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "Add Product to Firestore",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF111111)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                1 -> {
                    // ==========================================
                    // TAB 1: 🛍️ Products List & Management
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = manageSearchQuery,
                            onValueChange = { manageSearchQuery = it },
                            placeholder = { Text("Search products to edit/delete...", color = TextMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                            trailingIcon = {
                                if (manageSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { manageSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = DarkInputBorder,
                                focusedContainerColor = DarkCardSecondary,
                                unfocusedContainerColor = DarkCardSecondary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val filtered = productsList.filter {
                            it.name.contains(manageSearchQuery, ignoreCase = true) ||
                            it.category.contains(manageSearchQuery, ignoreCase = true)
                        }

                        if (filtered.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No products found in Firestore", color = TextMuted)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filtered) { product ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
                                        border = BorderStroke(1.dp, DarkInputBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            SmartAsyncImage(
                                                imageUrl = product.image,
                                                contentDescription = product.name,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.size(55.dp),
                                                contentScale = ContentScale.Crop
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = product.name,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextWhite,
                                                    fontSize = 14.sp,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "Category: ${product.category}",
                                                    color = TextMuted,
                                                    fontSize = 12.sp
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "₹${product.finalPrice.ifEmpty { product.price }}",
                                                        color = OrangePrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "• Stock: ${product.availableUnits}",
                                                        color = if (product.availableUnits > 0) SuccessGreen else Color(0xFFEF4444),
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = {
                                                    editingProduct = product
                                                    editName = product.name
                                                    editCategory = product.category
                                                    editPrice = product.price
                                                    editFinalPrice = product.finalPrice
                                                    editUnits = product.availableUnits.toString()
                                                    editImage = product.image
                                                    editDescription = product.description
                                                }
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OrangePrimary)
                                            }

                                            IconButton(
                                                onClick = { deletingProductId = product.productId }
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // ==========================================
                    // TAB 2: 📁 Categories Management
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Add / Edit Categories",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextWhite
                        )
                        Text(
                            text = "Firestore Collection: categories (Admin: animeshv063@gmail.com)",
                            fontSize = 12.sp,
                            color = OrangePrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Add Category Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
                            border = BorderStroke(1.dp, DarkInputBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                KeyValRow(key = "createBy", value = newCatCreateBy, onValueChange = { newCatCreateBy = it })
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                KeyValRow(key = "name", value = newCatName, placeholder = "e.g. Footwear, Kids Wear, Shirts", onValueChange = { newCatName = it })
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                KeyValRow(key = "categoryImage", value = newCatImage, placeholder = "Paste category image link https://...", onValueChange = { newCatImage = it })

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkInputBg,
                                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                                        modifier = Modifier.clickable {
                                            newCatImage = "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500&q=80"
                                        }
                                    ) {
                                        Text(
                                            text = "💡 Sample Shoe",
                                            fontSize = 11.sp,
                                            color = OrangePrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkInputBg,
                                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                                        modifier = Modifier.clickable {
                                            newCatImage = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500&q=80"
                                        }
                                    ) {
                                        Text(
                                            text = "💡 Sample Shirt",
                                            fontSize = 11.sp,
                                            color = OrangePrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (newCatImage.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SmartAsyncImage(
                                    imageUrl = newCatImage,
                                    contentDescription = "Category Preview",
                                    shape = CircleShape,
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Live Category Icon Preview", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                    Text(text = "Preview updates in real time", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Button(
                            onClick = {
                                val name = newCatName.trim()
                                val img = sanitizeImageUrl(newCatImage)
                                if (name.isEmpty() || img.isEmpty()) {
                                    Toast.makeText(context, "Please enter Category Name and Image link", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val category = CategoryDataModels(
                                    name = name,
                                    categoryImage = img,
                                    createBy = newCatCreateBy.ifEmpty { "Animesh" }
                                )

                                viewModel.addCategory(
                                    category = category,
                                    onSuccess = {
                                        Toast.makeText(context, "✅ Category '$name' Added to Firestore!", Toast.LENGTH_SHORT).show()
                                        newCatName = ""
                                        newCatImage = ""
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Add Category to Firestore", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Existing Categories (${categoriesList.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
                        Spacer(modifier = Modifier.height(10.dp))

                        if (categoriesList.isEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(80.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = DarkCardSecondary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Nothing to display", color = TextMuted, fontSize = 14.sp)
                                }
                            }
                        } else {
                            categoriesList.forEach { category ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
                                    border = BorderStroke(1.dp, DarkInputBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SmartAsyncImage(
                                            imageUrl = category.categoryImage,
                                            contentDescription = category.name,
                                            shape = CircleShape,
                                            modifier = Modifier.size(50.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = category.name, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                                            Text(text = "By: ${category.createBy}", color = TextMuted, fontSize = 12.sp)
                                        }

                                        IconButton(
                                            onClick = {
                                                editingCategory = category
                                                editCatName = category.name
                                                editCatImage = category.categoryImage
                                                editCatCreateBy = category.createBy
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OrangePrimary)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                3 -> {
                    // ==========================================
                    // TAB 3: 🖼️ Banners Management
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Add / Edit Banners",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextWhite
                        )
                        Text(
                            text = "Firestore Collection: banner (Admin: animeshv063@gmail.com)",
                            fontSize = 12.sp,
                            color = OrangePrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Add Banner Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
                            border = BorderStroke(1.dp, DarkInputBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                KeyValRow(key = "name", value = newBanName, placeholder = "e.g. Urban Everyday Fashion Collection", onValueChange = { newBanName = it })
                                HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                KeyValRow(key = "image", value = newBanImage, placeholder = "Paste banner image link https://...", onValueChange = { newBanImage = it })

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkInputBg,
                                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                                        modifier = Modifier.clickable {
                                            newBanImage = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200&q=80"
                                        }
                                    ) {
                                        Text(
                                            text = "💡 Sample Store Banner",
                                            fontSize = 11.sp,
                                            color = OrangePrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkInputBg,
                                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                                        modifier = Modifier.clickable {
                                            newBanImage = "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200&q=80"
                                        }
                                    ) {
                                        Text(
                                            text = "💡 Sample Fashion Banner",
                                            fontSize = 11.sp,
                                            color = OrangePrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live 16:9 Banner Preview
                        Text(text = "Live 16:9 Banner Preview", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkCardSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            if (newBanImage.isNotBlank()) {
                                SmartAsyncImage(
                                    imageUrl = newBanImage,
                                    contentDescription = "Banner Preview",
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = OrangePrimary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Live Banner Preview", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "Enter or paste any banner image URL above to preview in real-time", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val name = newBanName.trim()
                                val img = sanitizeImageUrl(newBanImage)
                                if (name.isEmpty() || img.isEmpty()) {
                                    Toast.makeText(context, "Please enter Banner Name and Image link", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val banner = BannerDataModels(
                                    name = name,
                                    image = img
                                )

                                viewModel.addBanner(
                                    banner = banner,
                                    onSuccess = {
                                        Toast.makeText(context, "✅ Banner Added to Firestore!", Toast.LENGTH_SHORT).show()
                                        newBanName = ""
                                        newBanImage = ""
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Add Banner to Firestore", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Existing Banners (${bannerList.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
                        Spacer(modifier = Modifier.height(10.dp))

                        if (bannerList.isEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(80.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = DarkCardSecondary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Nothing to display", color = TextMuted, fontSize = 14.sp)
                                }
                            }
                        } else {
                            bannerList.forEach { banner ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
                                    border = BorderStroke(1.dp, DarkInputBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .height(130.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        SmartAsyncImage(
                                            imageUrl = banner.image,
                                            contentDescription = banner.name,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(75.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = banner.name,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(1f)
                                            )

                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        editingBanner = banner
                                                        editBanName = banner.name
                                                        editBanImage = banner.image
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OrangePrimary)
                                                }

                                                IconButton(
                                                    onClick = { deletingBanner = banner }
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // ==========================================
    // ALERT DIALOGS FOR ALL TABS
    // ==========================================

    // Action result feedback dialog
    if (actionDialogTitle != null && actionDialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                actionDialogTitle = null
                actionDialogMessage = null
            },
            title = {
                Text(
                    text = actionDialogTitle!!,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuccessDialog) SuccessGreen else OrangePrimary
                )
            },
            text = { Text(text = actionDialogMessage!!, color = TextWhite) },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        actionDialogTitle = null
                        actionDialogMessage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Duplicate image warning dialog
    if (duplicateWarningMessage != null) {
        AlertDialog(
            onDismissRequest = { duplicateWarningMessage = null },
            title = { Text("Duplicate Image Detected", fontWeight = FontWeight.Bold, color = OrangePrimary) },
            text = { Text(duplicateWarningMessage!!, color = TextWhite) },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        duplicateWarningMessage = null
                        val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
                        val product = ProductDataModels(
                            name = prodName.trim(),
                            price = prodPrice.trim(),
                            finalPrice = prodFinalPrice.trim().ifEmpty { prodPrice.trim() },
                            category = prodCategory,
                            image = prodImage.trim(),
                            description = prodDescription.trim(),
                            availableUnits = prodAvailableUnits.toIntOrNull() ?: 30,
                            initialUnits = prodInitialUnits.toIntOrNull() ?: 30,
                            createBy = prodCreateBy.ifEmpty { "Animesh" },
                            lastResetDay = currentDay,
                            date = System.currentTimeMillis()
                        )
                        viewModel.addProduct(
                            product = product,
                            onSuccess = {
                                actionDialogTitle = "Product Added! 🎉"
                                actionDialogMessage = "Successfully created product in Firestore."
                                isSuccessDialog = true
                                prodName = ""
                                prodPrice = ""
                                prodFinalPrice = ""
                                prodImage = ""
                                prodDescription = ""
                            },
                            onError = { error ->
                                actionDialogTitle = "Add Failed"
                                actionDialogMessage = "Error: $error"
                                isSuccessDialog = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Proceed Anyway", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { duplicateWarningMessage = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // ✏️ Edit Product Dialog
    if (editingProduct != null) {
        val prod = editingProduct!!
        AlertDialog(
            onDismissRequest = { editingProduct = null },
            title = { Text("Edit Product", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Product Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = editCategoryExpanded,
                        onExpandedChange = { editCategoryExpanded = !editCategoryExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = editCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editCategoryExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = DarkInputBorder,
                                focusedContainerColor = DarkInputBg,
                                unfocusedContainerColor = DarkInputBg
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = editCategoryExpanded,
                            onDismissRequest = { editCategoryExpanded = false },
                            modifier = Modifier.background(DarkCard)
                        ) {
                            predefinedCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = TextWhite, fontSize = 14.sp) },
                                    onClick = {
                                        editCategory = cat
                                        editCategoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editPrice,
                        onValueChange = { editPrice = it },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    OutlinedTextField(
                        value = editFinalPrice,
                        onValueChange = { editFinalPrice = it },
                        label = { Text("Final Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    OutlinedTextField(
                        value = editUnits,
                        onValueChange = { editUnits = it },
                        label = { Text("Available Units") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    OutlinedTextField(
                        value = editImage,
                        onValueChange = { editImage = it },
                        label = { Text("Image URL") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    if (editImage.isNotBlank()) {
                        SmartAsyncImage(
                            imageUrl = editImage,
                            contentDescription = "Edit Product Preview",
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") },
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )
                }
            },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        val units = editUnits.toIntOrNull() ?: prod.availableUnits
                        val updated = prod.copy(
                            name = editName.trim(),
                            category = editCategory.trim(),
                            price = editPrice.trim(),
                            finalPrice = editFinalPrice.trim(),
                            availableUnits = units,
                            initialUnits = units,
                            image = sanitizeImageUrl(editImage),
                            description = editDescription.trim()
                        )
                        viewModel.updateProduct(
                            product = updated,
                            onSuccess = {
                                Toast.makeText(context, "✅ Product Updated in Firestore!", Toast.LENGTH_SHORT).show()
                                editingProduct = null
                            },
                            onError = { error ->
                                Toast.makeText(context, "❌ Update Error: $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingProduct = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // 🗑️ Delete Product Confirmation Dialog
    if (deletingProductId != null) {
        AlertDialog(
            onDismissRequest = { deletingProductId = null },
            title = { Text("Delete Product", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("Are you sure you want to permanently delete this product from your Firestore database?", color = TextMuted) },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        val id = deletingProductId!!
                        deletingProductId = null
                        viewModel.deleteProduct(
                            productId = id,
                            onSuccess = {
                                Toast.makeText(context, "🗑️ Product Deleted Successfully!", Toast.LENGTH_SHORT).show()
                            },
                            onError = { error ->
                                Toast.makeText(context, "❌ Delete Error: $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingProductId = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // ✏️ Edit Category Dialog
    if (editingCategory != null) {
        val cat = editingCategory!!
        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("Edit Category", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = editCatName,
                        onValueChange = { editCatName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    OutlinedTextField(
                        value = editCatImage,
                        onValueChange = { editCatImage = it },
                        label = { Text("Category Image URL") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    if (editCatImage.isNotBlank()) {
                        SmartAsyncImage(
                            imageUrl = editCatImage,
                            contentDescription = "Preview",
                            shape = CircleShape,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        val updated = cat.copy(
                            name = editCatName.trim(),
                            categoryImage = sanitizeImageUrl(editCatImage),
                            createBy = editCatCreateBy.ifEmpty { "Animesh" }
                        )
                        viewModel.updateCategory(
                            categoryId = cat.categoryId,
                            category = updated,
                            onSuccess = {
                                Toast.makeText(context, "✅ Category Updated in Firestore!", Toast.LENGTH_SHORT).show()
                                editingCategory = null
                            },
                            onError = { error ->
                                Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // ✏️ Edit Banner Dialog
    if (editingBanner != null) {
        val ban = editingBanner!!
        AlertDialog(
            onDismissRequest = { editingBanner = null },
            title = { Text("Edit Promotional Banner", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = editBanName,
                        onValueChange = { editBanName = it },
                        label = { Text("Banner Name / Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    OutlinedTextField(
                        value = editBanImage,
                        onValueChange = { editBanImage = it },
                        label = { Text("Banner Image URL") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkInputBorder,
                            focusedContainerColor = DarkInputBg,
                            unfocusedContainerColor = DarkInputBg
                        )
                    )

                    if (editBanImage.isNotBlank()) {
                        SmartAsyncImage(
                            imageUrl = editBanImage,
                            contentDescription = "Preview",
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }
                }
            },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        val updated = ban.copy(
                            name = editBanName.trim(),
                            image = sanitizeImageUrl(editBanImage)
                        )
                        viewModel.updateBanner(
                            bannerId = ban.bannerId,
                            banner = updated,
                            onSuccess = {
                                Toast.makeText(context, "✅ Banner Updated in Firestore!", Toast.LENGTH_SHORT).show()
                                editingBanner = null
                            },
                            onError = { error ->
                                Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBanner = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // 🗑️ Delete Banner Confirmation Dialog
    if (deletingBanner != null) {
        val ban = deletingBanner!!
        AlertDialog(
            onDismissRequest = { deletingBanner = null },
            title = { Text("Delete Banner", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("Are you sure you want to permanently delete '${ban.name}' from your Firestore database?", color = TextMuted) },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = {
                        deletingBanner = null
                        viewModel.deleteBanner(
                            bannerId = ban.bannerId,
                            onSuccess = {
                                Toast.makeText(context, "🗑️ Banner Deleted Successfully!", Toast.LENGTH_SHORT).show()
                            },
                            onError = { error ->
                                Toast.makeText(context, "❌ Delete Error: $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingBanner = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun KeyValRow(
    key: String,
    value: String,
    placeholder: String = "",
    isNumber: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = key,
            color = OrangePrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(110.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextMuted, fontSize = 13.sp) },
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = DarkInputBorder,
                focusedContainerColor = DarkInputBg,
                unfocusedContainerColor = DarkInputBg
            )
        )
    }
}
