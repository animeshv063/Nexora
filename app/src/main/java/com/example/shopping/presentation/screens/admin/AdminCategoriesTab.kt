package com.example.shopping.presentation.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.utils.sanitizeImageUrl
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun AdminCategoriesTab(
    categoriesList: List<CategoryDataModels>,
    catImage: String,
    onCatImageChange: (String) -> Unit,
    onRequestCrop: (url: String) -> Unit,
    onRequestPickAndCrop: () -> Unit,
    onAddCategory: (category: CategoryDataModels, onSuccess: () -> Unit) -> Unit,
    onEditCategory: (CategoryDataModels) -> Unit
) {
    val context = LocalContext.current
    var newCatName by rememberSaveable { mutableStateOf("") }
    var newCatCreateBy by rememberSaveable { mutableStateOf("Animesh") }

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
            text = "Firestore Collection: categories",
            fontSize = 12.sp,
            color = OrangePrimary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Admin: animeshv063@gmail.com",
            fontSize = 11.sp,
            color = TextMuted
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

                KeyValRow(key = "categoryImage", value = catImage, placeholder = "Paste category image link https://...", onValueChange = onCatImageChange)

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkInputBg,
                    border = BorderStroke(1.dp, if (catImage.isNotBlank()) OrangePrimary.copy(alpha = 0.6f) else DarkInputBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (catImage.isNotBlank()) {
                                    onRequestCrop(catImage)
                                } else {
                                    Toast.makeText(context, "Please paste an image URL or pick from gallery first", Toast.LENGTH_SHORT).show()
                                }
                            },
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

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkInputBg,
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onCatImageChange("https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500&q=80")
                            }
                    ) {
                        Text(
                            text = "💡 Sample Shoe",
                            fontSize = 11.sp,
                            color = OrangePrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkInputBg,
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onCatImageChange("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500&q=80")
                            }
                    ) {
                        Text(
                            text = "💡 Sample Shirt",
                            fontSize = 11.sp,
                            color = OrangePrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (catImage.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SmartAsyncImage(
                    imageUrl = catImage,
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
                val img = sanitizeImageUrl(catImage)
                if (name.isEmpty() || img.isEmpty()) {
                    Toast.makeText(context, "Please enter Category Name and Image link", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val alreadyExists = categoriesList.any { it.name.trim().equals(name, ignoreCase = true) }
                if (alreadyExists) {
                    Toast.makeText(context, "⚠️ A category named '$name' already exists!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val category = CategoryDataModels(
                    name = name,
                    categoryImage = img,
                    createBy = newCatCreateBy.ifEmpty { "Animesh" }
                )

                onAddCategory(category) {
                    newCatName = ""
                    onCatImageChange("")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Text("Add Category to Firestore", fontWeight = FontWeight.Bold, color = ButtonTextColor, fontSize = 15.sp)
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
                            onClick = { onEditCategory(category) }
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
