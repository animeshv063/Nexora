package com.example.shopping.presentation.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.screens.admin.AdminActionFeedbackDialog
import com.example.shopping.presentation.screens.admin.AdminAddProductTab
import com.example.shopping.presentation.screens.admin.AdminBannersTab
import com.example.shopping.presentation.screens.admin.AdminCategoriesTab
import com.example.shopping.presentation.screens.admin.AdminDeleteBannerDialog
import com.example.shopping.presentation.screens.admin.AdminDeleteProductDialog
import com.example.shopping.presentation.screens.admin.AdminDuplicateWarningDialog
import com.example.shopping.presentation.screens.admin.AdminEditBannerDialog
import com.example.shopping.presentation.screens.admin.AdminEditCategoryDialog
import com.example.shopping.presentation.screens.admin.AdminEditProductDialog
import com.example.shopping.presentation.screens.admin.AdminLoginSection
import com.example.shopping.presentation.screens.admin.AdminProductsListTab
import com.example.shopping.presentation.screens.admin.KeyValRow
import com.example.shopping.presentation.utils.CropAspectRatio
import com.example.shopping.presentation.utils.ImageCropUtils
import com.example.shopping.presentation.utils.ProductImageCropDialog
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.SuccessGreen
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ShoppingAppViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val productsState by viewModel.productsState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()
    val bannerState by viewModel.bannerState.collectAsState()
    val adminActionState by viewModel.adminActionState.collectAsState()

    var isAuthenticated by rememberSaveable { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val productsList = productsState.data ?: emptyList()
    val categoriesList = categoriesState.data?.filterNotNull() ?: emptyList()
    val bannerList = bannerState.data ?: emptyList()

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

    val predefinedGenders = listOf(
        "Men",
        "Women",
        "Unisex"
    )

    // Form Images
    var prodImage by rememberSaveable { mutableStateOf("") }
    var newCatImage by rememberSaveable { mutableStateOf("") }
    var newBanImage by rememberSaveable { mutableStateOf("") }

    // Feedback Alert Dialogs
    var actionDialogTitle by rememberSaveable { mutableStateOf<String?>(null) }
    var actionDialogMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var duplicateWarningMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingDuplicateProduct by remember { mutableStateOf<ProductDataModels?>(null) }

    // Dialog Target States
    var editingProduct by remember { mutableStateOf<ProductDataModels?>(null) }
    var editProductImage by rememberSaveable { mutableStateOf("") }
    var deletingProductId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingCategory by remember { mutableStateOf<CategoryDataModels?>(null) }
    var editingBanner by remember { mutableStateOf<BannerDataModels?>(null) }
    var deletingBanner by remember { mutableStateOf<BannerDataModels?>(null) }

    // Crop & Precision Zoom States
    var cropTarget by rememberSaveable { mutableStateOf<String?>(null) }
    var cropImageUrl by rememberSaveable { mutableStateOf("") }
    var cropBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var cropDialogRatio by remember { mutableStateOf(CropAspectRatio.SQUARE_1_1) }
    var cropDialogTitle by rememberSaveable { mutableStateOf("Crop & Precision Zoom") }

    val galleryCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            val bmp = ImageCropUtils.loadBitmap(context, uri)
            if (bmp != null) {
                cropBitmap = bmp
                cropImageUrl = ""
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
        viewModel.fetchProducts()
        viewModel.fetchCategories()
        viewModel.fetchBanners()
    }

    if (!isAuthenticated) {
        AdminLoginSection(
            onBackClick = onBackClick,
            onAuthenticated = { isAuthenticated = true }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .navigationBarsPadding()
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

            // 4 Navigation Tabs
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
                    onClick = { selectedTab = 0 },
                    text = { Text("➕ Add Product", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 0) OrangePrimary else TextMuted) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🛍️ Products (${productsList.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 1) OrangePrimary else TextMuted) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("📁 Categories (${categoriesList.size})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 2) OrangePrimary else TextMuted) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("🖼️ Banners (${bannerList.size})", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 3) OrangePrimary else TextMuted) }
                )
            }

            when (selectedTab) {
                0 -> {
                    AdminAddProductTab(
                        productsList = productsList,
                        predefinedCategories = predefinedCategories,
                        predefinedGenders = predefinedGenders,
                        prodImage = prodImage,
                        onProdImageChange = { prodImage = it },
                        isLoading = adminActionState.isLoading,
                        onRequestCrop = { url ->
                            cropTarget = "add_product"
                            cropImageUrl = url
                            cropBitmap = null
                            cropDialogRatio = CropAspectRatio.SQUARE_1_1
                            cropDialogTitle = "Crop & Precision Zoom - Product Image"
                        },
                        onRequestPickAndCrop = {
                            cropTarget = "add_product"
                            cropDialogRatio = CropAspectRatio.SQUARE_1_1
                            cropDialogTitle = "Crop Product from Gallery"
                            galleryCropLauncher.launch("image/*")
                        },
                        onIncompleteFields = {
                            actionDialogTitle = "Incomplete Fields"
                            actionDialogMessage = "Please provide Name, Price, and a valid Image link."
                            isSuccessDialog = false
                        },
                        onDuplicateImageDetected = { product ->
                            pendingDuplicateProduct = product
                            duplicateWarningMessage = "Warning: A product with this exact image link already exists in Firestore. Do you still want to proceed?"
                        },
                        onSubmitProduct = { product, onSuccess ->
                            viewModel.addProduct(
                                product = product,
                                onSuccess = {
                                    actionDialogTitle = "Product Added! 🎉"
                                    actionDialogMessage = "Successfully created product '${product.name}' in Firestore."
                                    isSuccessDialog = true
                                    onSuccess()
                                },
                                onError = { error ->
                                    actionDialogTitle = "Add Failed"
                                    actionDialogMessage = "Error: $error"
                                    isSuccessDialog = false
                                }
                            )
                        }
                    )
                }
                1 -> {
                    AdminProductsListTab(
                        productsList = productsList,
                        onEditProduct = { product ->
                            editingProduct = product
                            editProductImage = product.image
                        },
                        onDeleteProduct = { productId ->
                            deletingProductId = productId
                        }
                    )
                }
                2 -> {
                    AdminCategoriesTab(
                        categoriesList = categoriesList,
                        catImage = newCatImage,
                        onCatImageChange = { newCatImage = it },
                        onRequestCrop = { url ->
                            cropTarget = "add_category"
                            cropImageUrl = url
                            cropBitmap = null
                            cropDialogRatio = CropAspectRatio.SQUARE_1_1
                            cropDialogTitle = "Crop Category Icon (1:1)"
                        },
                        onRequestPickAndCrop = {
                            cropTarget = "add_category"
                            cropDialogRatio = CropAspectRatio.SQUARE_1_1
                            cropDialogTitle = "Pick & Crop Category Image"
                            galleryCropLauncher.launch("image/*")
                        },
                        onAddCategory = { category, onSuccess ->
                            viewModel.addCategory(
                                category = category,
                                onSuccess = {
                                    Toast.makeText(context, "✅ Category '${category.name}' Added to Firestore!", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                },
                                onError = { error ->
                                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        onEditCategory = { category ->
                            editingCategory = category
                        }
                    )
                }
                3 -> {
                    AdminBannersTab(
                        bannerList = bannerList,
                        banImage = newBanImage,
                        onBanImageChange = { newBanImage = it },
                        onRequestCrop = { url ->
                            cropTarget = "add_banner"
                            cropImageUrl = url
                            cropBitmap = null
                            cropDialogRatio = CropAspectRatio.BANNER_16_9
                            cropDialogTitle = "Crop Banner Image (16:9)"
                        },
                        onRequestPickAndCrop = {
                            cropTarget = "add_banner"
                            cropDialogRatio = CropAspectRatio.BANNER_16_9
                            cropDialogTitle = "Pick & Crop Banner from Gallery"
                            galleryCropLauncher.launch("image/*")
                        },
                        onAddBanner = { banner, onSuccess ->
                            viewModel.addBanner(
                                banner = banner,
                                onSuccess = {
                                    Toast.makeText(context, "✅ Banner Added to Firestore!", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                },
                                onError = { error ->
                                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        onEditBanner = { banner ->
                            editingBanner = banner
                        },
                        onDeleteBanner = { banner ->
                            deletingBanner = banner
                        }
                    )
                }
            }
        }
    }

    // ==========================================
    // ALERT DIALOGS
    // ==========================================

    AdminActionFeedbackDialog(
        title = actionDialogTitle,
        message = actionDialogMessage,
        isSuccess = isSuccessDialog,
        onDismiss = {
            actionDialogTitle = null
            actionDialogMessage = null
        }
    )

    AdminDuplicateWarningDialog(
        message = duplicateWarningMessage,
        onConfirm = {
            duplicateWarningMessage = null
            val prod = pendingDuplicateProduct
            if (prod != null) {
                viewModel.addProduct(
                    product = prod,
                    onSuccess = {
                        actionDialogTitle = "Product Added! 🎉"
                        actionDialogMessage = "Successfully created product in Firestore."
                        isSuccessDialog = true
                        prodImage = ""
                        pendingDuplicateProduct = null
                    },
                    onError = { error ->
                        actionDialogTitle = "Add Failed"
                        actionDialogMessage = "Error: $error"
                        isSuccessDialog = false
                        pendingDuplicateProduct = null
                    }
                )
            }
        },
        onDismiss = {
            duplicateWarningMessage = null
            pendingDuplicateProduct = null
        }
    )

    AdminEditProductDialog(
        product = editingProduct,
        editImage = editProductImage,
        onEditImageChange = { editProductImage = it },
        predefinedCategories = predefinedCategories,
        predefinedGenders = predefinedGenders,
        onRequestCrop = { url ->
            cropTarget = "edit_product"
            cropImageUrl = url
            cropBitmap = null
            cropDialogRatio = CropAspectRatio.SQUARE_1_1
            cropDialogTitle = "Crop & Precision Zoom - Edit Product"
        },
        onRequestPickAndCrop = {
            cropTarget = "edit_product"
            cropDialogRatio = CropAspectRatio.SQUARE_1_1
            cropDialogTitle = "Pick & Crop Product Image"
            galleryCropLauncher.launch("image/*")
        },
        onDismiss = { editingProduct = null },
        onSaveProduct = { updated ->
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
        }
    )

    AdminDeleteProductDialog(
        productId = deletingProductId,
        onDismiss = { deletingProductId = null },
        onConfirm = { id ->
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
        }
    )

    AdminEditCategoryDialog(
        category = editingCategory,
        onDismiss = { editingCategory = null },
        onSaveCategory = { updated ->
            viewModel.updateCategory(
                categoryId = updated.categoryId,
                category = updated,
                onSuccess = {
                    Toast.makeText(context, "✅ Category Updated in Firestore!", Toast.LENGTH_SHORT).show()
                    editingCategory = null
                },
                onError = { error ->
                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    )

    AdminEditBannerDialog(
        banner = editingBanner,
        onDismiss = { editingBanner = null },
        onSaveBanner = { updated ->
            viewModel.updateBanner(
                bannerId = updated.bannerId,
                banner = updated,
                onSuccess = {
                    Toast.makeText(context, "✅ Banner Updated in Firestore!", Toast.LENGTH_SHORT).show()
                    editingBanner = null
                },
                onError = { error ->
                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    )

    AdminDeleteBannerDialog(
        banner = deletingBanner,
        onDismiss = { deletingBanner = null },
        onConfirm = { ban ->
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
        }
    )

    // Render Precision Crop Modal Dialog
    if (cropTarget != null && (cropImageUrl.isNotBlank() || cropBitmap != null)) {
        ProductImageCropDialog(
            initialBitmap = cropBitmap,
            imageUrl = cropImageUrl,
            dialogTitle = cropDialogTitle,
            initialRatio = cropDialogRatio,
            onDismissRequest = {
                cropTarget = null
                cropBitmap = null
                cropImageUrl = ""
            },
            onCropConfirmed = { _, base64Url ->
                when (cropTarget) {
                    "add_product" -> prodImage = base64Url
                    "edit_product" -> editProductImage = base64Url
                    "add_category" -> newCatImage = base64Url
                    "add_banner" -> newBanImage = base64Url
                }
                cropTarget = null
                cropBitmap = null
                cropImageUrl = ""
                Toast.makeText(context, "Framing & Crop Applied! 🎉", Toast.LENGTH_SHORT).show()
            }
        )
    }
}