package com.example.shopping.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.shopping.ui.theme.*
import kotlinx.coroutines.delay
import java.util.UUID

@Composable
fun RazorpaySimulationDialog(
    amount: Double,
    customerName: String,
    onDismiss: () -> Unit,
    onPaymentSuccess: (String) -> Unit
) {
    var state by remember { mutableStateOf("SELECT") } // SELECT, PROCESSING, SUCCESS
    var selectedMethod by remember { mutableStateOf("UPI") }
    var txnId by remember { mutableStateOf("") }

    // Spinning coin animation
    val infiniteTransition = rememberInfiniteTransition(label = "coinSpin")
    val coinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coinRotate"
    )

    LaunchedEffect(state) {
        if (state == "PROCESSING") {
            delay(1200) // 1.2s realistic processing delay
            txnId = "pay_${UUID.randomUUID().toString().take(14).replace("-", "")}"
            state = "SUCCESS"
            delay(1000) // 1s show green tick celebration
            onPaymentSuccess(txnId)
        }
    }

    Dialog(
        onDismissRequest = { if (state == "SELECT") onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = DarkCard
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (state == "SELECT") {
                        // Razorpay Header Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    color = OrangePrimary
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.FlashOn,
                                            contentDescription = "Fast Pay",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Razorpay Gateway",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Instant 1-Click Sandbox Test",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Amount Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = DarkCardSecondary
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Amount to Pay", fontSize = 12.sp, color = TextMuted)
                                    Text(
                                        text = "₹${amount.toInt()}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = OrangePrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Test Mode Active",
                                        color = Color(0xFF10B981),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Method selection
                        Text(
                            text = "Select Payment Option (Auto-Approves)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        listOf(
                            Triple("UPI", "Google Pay / PhonePe / Paytm", Icons.Default.QrCode),
                            Triple("CARD", "Debit / Credit Card (Visa / MC)", Icons.Default.CreditCard),
                            Triple("NETBANKING", "NetBanking / Instant Transfer", Icons.Default.AccountBalance)
                        ).forEach { (id, label, icon) ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clickable { selectedMethod = id },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedMethod == id) OrangePrimary.copy(alpha = 0.12f) else DarkCardSecondary,
                                border = if (selectedMethod == id) androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (selectedMethod == id) OrangePrimary else TextMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = id,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextWhite
                                        )
                                        Text(text = label, fontSize = 11.sp, color = TextMuted)
                                    }
                                    RadioButton(
                                        selected = selectedMethod == id,
                                        onClick = { selectedMethod = id },
                                        colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Pay Button
                        Button(
                            onClick = { state = "PROCESSING" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text(
                                text = "Pay ₹${amount.toInt()} (Instant Success)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = ButtonTextColor
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🔒 No details or OTP required • Automatic validation",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    } else if (state == "PROCESSING") {
                        Spacer(modifier = Modifier.height(20.dp))

                        // Spinning Coin Animation
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .rotate(coinRotation),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.size(76.dp),
                                shape = CircleShape,
                                color = Color(0xFFFFB300),
                                shadowElevation = 10.dp,
                                border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFFFD54F))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "₹",
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF5D4037)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Authorizing with Razorpay...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Simulating secure test transaction",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                    } else if (state == "SUCCESS") {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Green Tick Coin
                        AnimatedVisibility(
                            visible = true,
                            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
                        ) {
                            Surface(
                                modifier = Modifier.size(85.dp),
                                shape = CircleShape,
                                color = Color(0xFF10B981),
                                shadowElevation = 12.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Success",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Payment Successful!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Transaction ID: $txnId",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Confirming your order in Nexora...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
