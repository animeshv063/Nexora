package com.example.shopping.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.SuccessGreen
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessAlertDialog(
    onDismissRequest: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(18.dp),
                color = DarkCard
            )
            .padding(1.dp),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard, RoundedCornerShape(18.dp))
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(
                            color = SuccessGreen.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = SuccessGreen,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Congratulations!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your account has been created successfully. Welcome to the app!",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = TextMuted,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue to Home",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ButtonTextColor
                    )
                }
            }
        }
    )
}