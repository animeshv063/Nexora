package com.example.shopping.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun CheckoutAddressDialog(
    tempName: String,
    onTempNameChange: (String) -> Unit,
    tempPhone: String,
    onTempPhoneChange: (String) -> Unit,
    tempAddress: String,
    onTempAddressChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveAddress: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Edit Delivery Details", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = onTempNameChange,
                    label = { Text("Full Name", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkInputBg,
                        unfocusedContainerColor = DarkInputBg,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkInputBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = tempPhone,
                    onValueChange = onTempPhoneChange,
                    label = { Text("Phone Number", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkInputBg,
                        unfocusedContainerColor = DarkInputBg,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkInputBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = tempAddress,
                    onValueChange = onTempAddressChange,
                    label = { Text("Delivery Address", color = TextMuted) },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkInputBg,
                        unfocusedContainerColor = DarkInputBg,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkInputBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )
            }
        },
        containerColor = DarkCard,
        confirmButton = {
            Button(
                onClick = onSaveAddress,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(text = "Save Address", color = ButtonTextColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel", color = TextWhite)
            }
        }
    )
}

@Composable
internal fun CheckoutOrderConfirmDialog(
    product: ProductDataModels,
    quantity: Int,
    total: Double,
    selectedPaymentMethod: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Confirm Order", fontWeight = FontWeight.Bold, color = TextWhite)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Are you sure you want to confirm and place this order?",
                    color = TextWhite,
                    fontSize = 14.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkCardSecondary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "Item: ${product.name}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Quantity: $quantity", color = TextMuted, fontSize = 12.sp)
                        Text(text = "Total Payable: ₹${total.toInt()}", color = OrangePrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Payment: $selectedPaymentMethod", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        },
        containerColor = DarkCard,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(
                    text = if (selectedPaymentMethod.startsWith("Razorpay")) "Proceed to Pay" else "Confirm & Place Order",
                    color = ButtonTextColor,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel", color = TextMuted)
            }
        }
    )
}
