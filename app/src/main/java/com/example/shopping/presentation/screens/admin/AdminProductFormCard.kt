package com.example.shopping.presentation.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminProductFormCard(
    prodCreateBy: String,
    onProdCreateByChange: (String) -> Unit,
    prodAvailableUnits: String,
    onProdAvailableUnitsChange: (String) -> Unit,
    prodInitialUnits: String,
    onProdInitialUnitsChange: (String) -> Unit,
    prodCategory: String,
    onProdCategoryChange: (String) -> Unit,
    isCategoryDropdownExpanded: Boolean,
    onCategoryDropdownExpandedChange: (Boolean) -> Unit,
    predefinedCategories: List<String>,
    prodGender: String,
    onProdGenderChange: (String) -> Unit,
    isGenderDropdownExpanded: Boolean,
    onGenderDropdownExpandedChange: (Boolean) -> Unit,
    predefinedGenders: List<String>,
    prodName: String,
    onProdNameChange: (String) -> Unit,
    prodPrice: String,
    onProdPriceChange: (String) -> Unit,
    prodFinalPrice: String,
    onProdFinalPriceChange: (String) -> Unit,
    prodImage: String,
    onProdImageChange: (String) -> Unit,
    prodDescription: String,
    onProdDescriptionChange: (String) -> Unit,
    onRequestCrop: (url: String) -> Unit,
    onRequestPickAndCrop: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSecondary),
        border = BorderStroke(1.dp, DarkInputBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            KeyValRow(key = "createBy", value = prodCreateBy, onValueChange = onProdCreateByChange)
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
                    onValueChange = onProdAvailableUnitsChange,
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
                    onValueChange = onProdInitialUnitsChange,
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

            // Category Dropdown
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
                    onExpandedChange = onCategoryDropdownExpandedChange,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = prodCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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
                        onDismissRequest = { onCategoryDropdownExpandedChange(false) },
                        modifier = Modifier.background(DarkCard)
                    ) {
                        predefinedCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = TextWhite, fontSize = 14.sp) },
                                onClick = {
                                    onProdCategoryChange(cat)
                                    onCategoryDropdownExpandedChange(false)
                                }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

            // Gender Dropdown
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
                    onExpandedChange = onGenderDropdownExpandedChange,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = prodGender,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderDropdownExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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
                        onDismissRequest = { onGenderDropdownExpandedChange(false) },
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
                                    onProdGenderChange(gen)
                                    onGenderDropdownExpandedChange(false)
                                }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

            KeyValRow(key = "name", value = prodName, placeholder = "e.g. Classic Oxford Cotton Shirt", onValueChange = onProdNameChange)
            HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

            KeyValRow(key = "price", value = prodPrice, placeholder = "e.g. 1999", isNumber = true, onValueChange = onProdPriceChange)
            HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

            KeyValRow(key = "finalPrice", value = prodFinalPrice, placeholder = "e.g. 1299", isNumber = true, onValueChange = onProdFinalPriceChange)
            HorizontalDivider(color = DarkInputBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

            KeyValRow(key = "image", value = prodImage, placeholder = "Paste image link https://...", onValueChange = onProdImageChange)

            AdminProductImageActions(
                prodImage = prodImage,
                onProdImageChange = onProdImageChange,
                onRequestCrop = onRequestCrop,
                onRequestPickAndCrop = onRequestPickAndCrop
            )

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
                    onValueChange = onProdDescriptionChange,
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
}
