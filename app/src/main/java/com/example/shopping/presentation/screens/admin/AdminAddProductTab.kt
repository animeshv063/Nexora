package com.example.shopping.presentation.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.sanitizeImageUrl
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextWhite

@Composable
fun AdminAddProductTab(
    productsList: List<ProductDataModels>,
    predefinedCategories: List<String>,
    predefinedGenders: List<String>,
    prodImage: String,
    onProdImageChange: (String) -> Unit,
    isLoading: Boolean,
    onRequestCrop: (url: String) -> Unit,
    onRequestPickAndCrop: () -> Unit,
    onIncompleteFields: () -> Unit,
    onDuplicateImageDetected: (ProductDataModels) -> Unit,
    onSubmitProduct: (product: ProductDataModels, onSuccess: () -> Unit) -> Unit
) {
    var prodName by rememberSaveable { mutableStateOf("") }
    var prodCategory by rememberSaveable { mutableStateOf("Shirts") }
    var isCategoryDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var prodGender by rememberSaveable { mutableStateOf("Men") }
    var isGenderDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var prodPrice by rememberSaveable { mutableStateOf("") }
    var prodFinalPrice by rememberSaveable { mutableStateOf("") }
    var prodAvailableUnits by rememberSaveable { mutableStateOf("30") }
    var prodInitialUnits by rememberSaveable { mutableStateOf("30") }
    var prodCreateBy by rememberSaveable { mutableStateOf("Animesh") }
    var prodDescription by rememberSaveable { mutableStateOf("") }

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

        // Structured Form Card
        AdminProductFormCard(
            prodCreateBy = prodCreateBy,
            onProdCreateByChange = { prodCreateBy = it },
            prodAvailableUnits = prodAvailableUnits,
            onProdAvailableUnitsChange = {
                prodAvailableUnits = it
                prodInitialUnits = it
            },
            prodInitialUnits = prodInitialUnits,
            onProdInitialUnitsChange = { prodInitialUnits = it },
            prodCategory = prodCategory,
            onProdCategoryChange = { prodCategory = it },
            isCategoryDropdownExpanded = isCategoryDropdownExpanded,
            onCategoryDropdownExpandedChange = { isCategoryDropdownExpanded = it },
            predefinedCategories = predefinedCategories,
            prodGender = prodGender,
            onProdGenderChange = { prodGender = it },
            isGenderDropdownExpanded = isGenderDropdownExpanded,
            onGenderDropdownExpandedChange = { isGenderDropdownExpanded = it },
            predefinedGenders = predefinedGenders,
            prodName = prodName,
            onProdNameChange = { prodName = it },
            prodPrice = prodPrice,
            onProdPriceChange = { prodPrice = it },
            prodFinalPrice = prodFinalPrice,
            onProdFinalPriceChange = { prodFinalPrice = it },
            prodImage = prodImage,
            onProdImageChange = onProdImageChange,
            prodDescription = prodDescription,
            onProdDescriptionChange = { prodDescription = it },
            onRequestCrop = onRequestCrop,
            onRequestPickAndCrop = onRequestPickAndCrop
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Image Preview
        AdminProductImagePreview(prodImage = prodImage)

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
                    onIncompleteFields()
                    return@Button
                }

                val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
                val product = ProductDataModels(
                    name = name,
                    price = price,
                    finalPrice = finalPrice,
                    category = prodCategory,
                    gender = prodGender,
                    image = image,
                    description = desc,
                    availableUnits = available,
                    initialUnits = initial,
                    createBy = prodCreateBy.ifEmpty { "Animesh" },
                    lastResetDay = currentDay,
                    date = System.currentTimeMillis()
                )

                val isDuplicateImage = productsList.any { it.image.equals(image, ignoreCase = true) }
                if (isDuplicateImage) {
                    onDuplicateImageDetected(product)
                    return@Button
                }

                onSubmitProduct(product) {
                    prodName = ""
                    prodPrice = ""
                    prodFinalPrice = ""
                    onProdImageChange("")
                    prodDescription = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = ButtonTextColor, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Add Product to Firestore",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = ButtonTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
