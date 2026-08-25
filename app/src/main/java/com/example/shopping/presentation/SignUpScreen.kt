package com.example.shopping.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.R
import com.example.shopping.domain.models.UserData
import com.example.shopping.presentation.utils.CustomTextField
import com.example.shopping.presentation.utils.GoogleAuthHelper
import com.example.shopping.presentation.utils.SuccessAlertDialog
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(
    viewModel: ShoppingAppViewModel,
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val signUpState by viewModel.signUpState.collectAsState()
    val googleSignInState by viewModel.googleSignInState.collectAsState()

    LaunchedEffect(signUpState) {
        signUpState.data?.let {
            showSuccessDialog = true
        }
        signUpState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(googleSignInState) {
        googleSignInState.data?.let {
            Toast.makeText(context, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show()
            viewModel.resetGoogleSignInState()
            onSignUpSuccess()
        }
        googleSignInState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetGoogleSignInState()
        }
    }

    if (showSuccessDialog) {
        SuccessAlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onSignUpSuccess()
            },
            onClick = {
                showSuccessDialog = false
                onSignUpSuccess()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SignUp",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "First Name",
                    leadingIcon = Icons.Default.Person,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Last Name",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            leadingIcon = Icons.Default.Email,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        CustomTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Phone Number",
            leadingIcon = Icons.Default.Phone,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            modifier = Modifier.padding(vertical = 4.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        CustomTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            leadingIcon = Icons.Default.Lock,
            modifier = Modifier.padding(vertical = 4.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (firstName.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.signUp(
                        UserData(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            email = email.trim(),
                            password = password,
                            phoneNumber = phoneNumber.trim()
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            enabled = !signUpState.isLoading && !googleSignInState.isLoading
        ) {
            if (signUpState.isLoading) {
                CircularProgressIndicator(color = com.example.shopping.ui.theme.ButtonTextColor, modifier = Modifier.size(22.dp))
            } else {
                Text(text = "Signup", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = com.example.shopping.ui.theme.ButtonTextColor)
            }


        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have an account? ", color = TextMuted, fontSize = 14.sp)
            Text(
                text = "Login",
                color = OrangePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = DarkInputBorder)
            Text(
                text = "OR",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = DarkInputBorder)
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    GoogleAuthHelper.initiateGoogleSignIn(
                        context = context,
                        onSuccess = { idToken ->
                            viewModel.loginWithGoogleToken(idToken)
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkInputBorder),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkBg),
            enabled = !signUpState.isLoading && !googleSignInState.isLoading
        ) {
            if (googleSignInState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Log in with Google", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}