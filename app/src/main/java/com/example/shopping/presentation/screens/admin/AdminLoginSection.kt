package com.example.shopping.presentation.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminLoginSection(
    onBackClick: () -> Unit,
    onAuthenticated: () -> Unit
) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var isVerifyingOwner by rememberSaveable { mutableStateOf(false) }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var ownerUsername by rememberSaveable { mutableStateOf("") }
    var ownerPassword by rememberSaveable { mutableStateOf("") }
    val loginScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(loginScrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.Start)
                .size(40.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = OrangePrimary,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "App Owner Portal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextWhite
                )
                Text(
                    text = "Sign in with your Owner Credentials",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = ownerUsername,
                    onValueChange = { ownerUsername = it },
                    label = { Text("Owner ID") },
                    placeholder = { Text("Enter your Owner ID", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkInputBorder,
                        focusedContainerColor = DarkInputBg,
                        unfocusedContainerColor = DarkInputBg
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = ownerPassword,
                    onValueChange = { ownerPassword = it },
                    label = { Text("Owner Password") },
                    placeholder = { Text("Enter your Password", color = TextMuted) },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isPasswordVisible) KeyboardType.Text else KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                                tint = if (isPasswordVisible) OrangePrimary else TextMuted
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkInputBorder,
                        focusedContainerColor = DarkInputBg,
                        unfocusedContainerColor = DarkInputBg
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val inputUser = ownerUsername.trim()
                        val inputPass = ownerPassword.trim()
                        if (inputUser.isEmpty() || inputPass.isEmpty()) {
                            Toast.makeText(context, "Please enter Owner ID and Password", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isVerifyingOwner = true
                        firestore.collection("Admin").document("credentials").get()
                            .addOnSuccessListener { doc ->
                                isVerifyingOwner = false
                                val storedUser = doc.getString("username")
                                val storedPass = doc.getString("password")

                                val isMatch = if (doc.exists() && storedUser != null && storedPass != null) {
                                    inputUser.equals(storedUser, ignoreCase = true) && inputPass == storedPass
                                } else {
                                    inputUser.equals("AIZUI", ignoreCase = true) && inputPass == "KKKS"
                                }

                                if (isMatch) {
                                    onAuthenticated()
                                    Toast.makeText(context, "Welcome, Owner! 🔑", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "❌ Invalid Owner Credentials", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                isVerifyingOwner = false
                                if (inputUser.equals("AIZUI", ignoreCase = true) && inputPass == "KKKS") {
                                    onAuthenticated()
                                    Toast.makeText(context, "Welcome, Owner! 🔑", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "❌ Invalid Credentials", Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    if (isVerifyingOwner) {
                        CircularProgressIndicator(color = ButtonTextColor, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "Unlock Admin Console", fontWeight = FontWeight.ExtraBold, color = ButtonTextColor, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
