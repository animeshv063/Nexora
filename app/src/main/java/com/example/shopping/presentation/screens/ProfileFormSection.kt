package com.example.shopping.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.presentation.utils.CustomTextField
import com.example.shopping.ui.theme.ButtonTextColor
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.PrimaryAccent

@Composable
internal fun ProfileFormSection(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    phone: String,
    onPhoneChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    isEditing: Boolean,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // First Name & Last Name row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    label = "First Name",
                    readOnly = !isEditing
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
                    label = "Last Name",
                    readOnly = !isEditing
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        CustomTextField(
            value = email,
            onValueChange = {},
            label = "Email Address",
            readOnly = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        CustomTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "Phone Number",
            readOnly = !isEditing
        )

        Spacer(modifier = Modifier.height(14.dp))

        CustomTextField(
            value = address,
            onValueChange = onAddressChange,
            label = "Address",
            readOnly = !isEditing
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Edit / Save Profile Button
        if (isEditing) {
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = ButtonTextColor, modifier = Modifier.size(20.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ButtonTextColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ButtonTextColor)
                    }
                }
            }
        } else {
            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, PrimaryAccent),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkBg)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = PrimaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Edit Profile", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = PrimaryAccent)
                }
            }
        }
    }
}
