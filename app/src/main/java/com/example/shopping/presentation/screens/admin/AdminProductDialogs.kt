package com.example.shopping.presentation.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.utils.sanitizeImageUrl
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditProductDialog(
    product: ProductDataModels?,
    editImage: String,
    onEditImageChange: (String) -> Unit,
    predefinedCategories: List<String>,
    predefinedGenders: List<String>,
    onRequestCrop: (url: String) -> Unit,
    onRequestPickAndCrop: () -> Unit,
    onDismiss: () -> Unit,
    onSaveProduct: (ProductDataModels) -> Unit
) {
    if (product == null) return

    var editName by rememberSaveable(product.productId) { mutableStateOf(product.name) }
    var editCategory by rememberSaveable(product.productId) { mutableStateOf(product.category) }
    var editCategoryExpanded by rememberSaveable { mutableStateOf(false) }
    var editGender by rememberSaveable(product.productId) { mutableStateOf(product.gender.ifEmpty { "Men" }) }
    var editGenderExpanded by rememberSaveable { mutableStateOf(false) }
    var editPrice by rememberSaveable(product.productId) { mutableStateOf(product.price) }
    var editFinalPrice by rememberSaveable(product.productId) { mutableStateOf(product.finalPrice) }
    var editUnits by rememberSaveable(product.productId) { mutableStateOf(product.availableUnits.toString()) }
    var editDescription by rememberSaveable(product.productId) { mutableStateOf(product.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                    onExpandedChange = { editCategoryExpanded = !editCategoryExpanded }
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
                                text = { Text(cat, color = TextWhite) },
                                onClick = {
                                    editCategory = cat
                                    editCategoryExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = editGenderExpanded,
                    onExpandedChange = { editGenderExpanded = !editGenderExpanded }
                ) {
                    OutlinedTextField(
                        value = editGender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender / Target Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editGenderExpanded) },
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
                        expanded = editGenderExpanded,
                        onDismissRequest = { editGenderExpanded = false },
                        modifier = Modifier.background(DarkCard)
                    ) {
                        predefinedGenders.forEach { gen ->
                            DropdownMenuItem(
                                text = { Text(gen, color = TextWhite) },
                                onClick = {
                                    editGender = gen
                                    editGenderExpanded = false
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
                    onValueChange = onEditImageChange,
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
                            .height(220.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onRequestCrop(editImage) },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text(text = "✂️ Adjust Crop & Zoom", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ButtonTextColor)
                        }
                        OutlinedButton(
                            onClick = onRequestPickAndCrop,
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, DarkInputBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                        ) {
                            Text(text = "🖼️ Pick & Crop", fontSize = 11.sp, color = TextWhite)
                        }
                    }
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
                    val units = editUnits.toIntOrNull() ?: product.availableUnits
                    val updated = product.copy(
                        name = editName.trim(),
                        category = editCategory.trim(),
                        gender = editGender.trim().ifEmpty { "Men" },
                        price = editPrice.trim(),
                        finalPrice = editFinalPrice.trim(),
                        availableUnits = units,
                        initialUnits = units,
                        image = sanitizeImageUrl(editImage),
                        description = editDescription.trim()
                    )
                    onSaveProduct(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Save Changes", color = ButtonTextColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun AdminDeleteProductDialog(
    productId: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (productId != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Product", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("Are you sure you want to permanently delete this product from your Firestore database?", color = TextMuted) },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = { onConfirm(productId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
