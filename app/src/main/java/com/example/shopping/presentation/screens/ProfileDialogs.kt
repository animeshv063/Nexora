package com.example.shopping.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.presentation.utils.CustomTextField
import com.example.shopping.ui.theme.DangerRed
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun ProfileDeleteAccountDialog(
    isGoogleUser: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmDelete: (password: String) -> Unit
) {
    var deleteConfirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            deleteConfirmPassword = ""
            onDismissRequest()
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
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }
        },
        containerColor = DarkCard,
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDelete(if (isGoogleUser) "" else deleteConfirmPassword.trim())
                    deleteConfirmPassword = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
                Text(text = "Delete Forever", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                deleteConfirmPassword = ""
                onDismissRequest()
            }) {
                Text(text = "Cancel", color = TextWhite)
            }
        }
    )
}
