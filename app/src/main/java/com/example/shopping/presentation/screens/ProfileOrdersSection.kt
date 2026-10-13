package com.example.shopping.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.domain.models.OrderDataModel
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.presentation.viewModels.cancelOrder
import com.example.shopping.presentation.viewModels.resetOrderHistory
import com.example.shopping.ui.theme.DangerRed
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.PrimaryAccent
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
internal fun ProfileOrdersCard(
    ordersList: List<OrderDataModel>,
    isOrdersLoading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkInputBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "My Orders",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextWhite
                    )
                    Text(
                        text = if (isOrdersLoading && ordersList.isEmpty()) "Loading orders..." else if (ordersList.isEmpty()) "No active orders" else "${ordersList.size} orders placed",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = PrimaryAccent.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "View",
                    color = PrimaryAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
internal fun OrdersListDialog(
    ordersList: List<OrderDataModel>,
    isOrdersLoading: Boolean,
    address: String,
    onDismissRequest: () -> Unit,
    onRequestReset: () -> Unit,
    onCancelOrderClick: (OrderDataModel) -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order History (${ordersList.size})",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontSize = 17.sp
                )
                val hasActiveOrders = ordersList.any { !it.status.equals("Cancelled", ignoreCase = true) }
                if (ordersList.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            if (hasActiveOrders) {
                                Toast.makeText(context, "⚠️ Please cancel all live orders before resetting order history!", Toast.LENGTH_LONG).show()
                            } else {
                                onRequestReset()
                            }
                        }
                    ) {
                        Text(
                            text = "Reset 🗑️",
                            color = if (hasActiveOrders) TextMuted else DangerRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        text = {
            if (isOrdersLoading && ordersList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryAccent, modifier = Modifier.size(32.dp))
                }
            } else if (ordersList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "You have not placed any orders yet.", color = TextMuted, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(360.dp)
                ) {
                    items(ordersList) { order ->
                        val isCancelled = order.status.equals("Cancelled", ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkCardSecondary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Qty: ${order.quantity} ${if (order.quantity > 1) "units" else "unit"}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextWhite
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isCancelled) DangerRed.copy(alpha = 0.15f) else com.example.shopping.ui.theme.SuccessGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (isCancelled) "Cancelled" else "Order Placed",
                                            color = if (isCancelled) DangerRed else com.example.shopping.ui.theme.SuccessGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Status: ${if (isCancelled) "Cancelled" else "Order Placed"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isCancelled) DangerRed else com.example.shopping.ui.theme.SuccessGreen
                                )
                                val orderAddress = if (order.address.isNotBlank() && !order.address.contains("Please set", ignoreCase = true)) {
                                    order.address
                                } else if (address.isNotBlank() && !address.contains("Please set", ignoreCase = true)) {
                                    address
                                } else {
                                    "Home Delivery"
                                }
                                Text(text = "Delivery Address: $orderAddress", fontSize = 12.sp, color = TextWhite, maxLines = 2)

                                if (!isCancelled) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { onCancelOrderClick(order) },
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed)
                                    ) {
                                        Text(text = "Cancel Order & Restore Stock", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = DarkCard,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Close", color = PrimaryAccent)
            }
        }
    )
}

@Composable
internal fun CancelOrderConfirmDialog(
    targetOrder: OrderDataModel,
    viewModel: ShoppingAppViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Confirm Cancellation", fontWeight = FontWeight.Bold, color = TextWhite) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Are you sure you want to cancel this order?",
                    color = TextWhite,
                    fontSize = 14.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkCardSecondary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Item: ${targetOrder.productName.ifEmpty { "Product" }}",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "Quantity: ${targetOrder.quantity}", color = TextMuted, fontSize = 12.sp)
                        Text(text = "Payment Method: ${targetOrder.paymentMethod}", color = PrimaryAccent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        },
        containerColor = DarkCard,
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                    viewModel.cancelOrder(targetOrder.orderId, targetOrder.productId, targetOrder.quantity) {
                        Toast.makeText(context, "Order Cancelled & Stock Restored! 🔄", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
                Text("Yes, Cancel Order", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("No, Keep Order", color = TextMuted)
            }
        }
    )
}

@Composable
internal fun ResetOrdersConfirmDialog(
    viewModel: ShoppingAppViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Reset Order History", fontWeight = FontWeight.Bold, color = TextWhite) },
        text = { Text("Are you sure you want to clear your entire order history? This will permanently delete your order records.", color = TextMuted) },
        containerColor = DarkCard,
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                    viewModel.resetOrderHistory {
                        Toast.makeText(context, "Order history reset successfully! 🗑️", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
                Text("Clear All", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
