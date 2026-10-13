package com.example.shopping.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.presentation.utils.SmartAsyncImage
import com.example.shopping.presentation.utils.sanitizeImageUrl
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.SuccessGreen
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun AdminActionFeedbackDialog(
    title: String?,
    message: String?,
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    if (title != null && message != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuccess) SuccessGreen else OrangePrimary
                )
            },
            text = { Text(text = message, color = TextWhite) },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("OK", color = ButtonTextColor, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun AdminDuplicateWarningDialog(
    message: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (message != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Duplicate Image Detected", fontWeight = FontWeight.Bold, color = OrangePrimary) },
            text = { Text(message, color = TextWhite) },
            containerColor = DarkCard,
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Proceed Anyway", color = ButtonTextColor, fontWeight = FontWeight.Bold)
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

@Composable
fun AdminEditCategoryDialog(
    category: CategoryDataModels?,
    onDismiss: () -> Unit,
    onSaveCategory: (CategoryDataModels) -> Unit
) {
    if (category == null) return

    var editCatName by rememberSaveable(category.categoryId) { mutableStateOf(category.name) }
    var editCatImage by rememberSaveable(category.categoryId) { mutableStateOf(category.categoryImage) }
    var editCatCreateBy by rememberSaveable(category.categoryId) { mutableStateOf(category.createBy) }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                    val updated = category.copy(
                        name = editCatName.trim(),
                        categoryImage = sanitizeImageUrl(editCatImage),
                        createBy = editCatCreateBy.ifEmpty { "Animesh" }
                    )
                    onSaveCategory(updated)
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
