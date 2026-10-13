package com.example.shopping.presentation.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.utils.sanitizeImageUrl
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current

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
                            modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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

                // Gender Dropdown (Men / Women / Unisex)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "gender",
                        color = OrangePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(110.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = isGenderDropdownExpanded,
                        onExpandedChange = { isGenderDropdownExpanded = !isGenderDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = prodGender,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderDropdownExpanded) },
                            modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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
                            expanded = isGenderDropdownExpanded,
                            onDismissRequest = { isGenderDropdownExpanded = false },
                            modifier = Modifier.background(DarkCard)
                        ) {
                            predefinedGenders.forEach { gen ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = when (gen) {
                                                "Men" -> "👨 Men / Male"
                                                "Women" -> "👩 Women / Female"
                                                else -> "✨ Unisex"
                                            },
                                            color = TextWhite,
                                            fontSize = 14.sp
                                        )
                                    },
                                    onClick = {
                                        prodGender = gen
                                        isGenderDropdownExpanded = false
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

                KeyValRow(key = "image", value = prodImage, placeholder = "Paste image link https://...", onValueChange = onProdImageChange)

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
                            onProdImageChange("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1000&q=80")
                        }
                    ) {
                        Text(text = "👟 Shoes", fontSize = 11.sp, color = OrangePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkInputBg,
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                        modifier = Modifier.clickable {
                            onProdImageChange("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=1000&q=80")
                        }
                    ) {
                        Text(text = "👕 Shirt", fontSize = 11.sp, color = OrangePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkInputBg,
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                        modifier = Modifier.clickable {
                            onProdImageChange("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&q=80")
                        }
                    ) {
                        Text(text = "⌚ Watch", fontSize = 11.sp, color = OrangePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Image Crop & Precision Zoom Action Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkCardSecondary,
                    border = BorderStroke(1.dp, if (prodImage.isNotBlank()) OrangePrimary.copy(alpha = 0.6f) else DarkInputBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Crop", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Image Crop & Precision Zoom", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextWhite)
                            }
                            if (prodImage.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = OrangePrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(text = "Link Ready", color = OrangePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (prodImage.isNotBlank()) {
                                        onRequestCrop(prodImage)
                                    } else {
                                        Toast.makeText(context, "Please paste an image URL or pick from gallery first", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                            ) {
                                Text(text = "✂️ Adjust Crop & Zoom", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ButtonTextColor)
                            }

                            OutlinedButton(
                                onClick = onRequestPickAndCrop,
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, DarkInputBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                            ) {
                                Text(text = "🖼️ Pick & Crop", fontSize = 11.sp, color = TextWhite)
                            }
                        }
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

        // Image Preview with exact aspect ratio
        Text(text = "Product Preview", fontWeight = FontWeight.SemiBold, color = TextWhite, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
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
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = OrangePrimary.copy(alpha = 0.7f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Live Product Image Preview",
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter or paste any product image URL above to preview",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
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
