package com.example.shopping.presentation.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.DarkInputBg
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    leadingIcon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 13.sp) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        readOnly = readOnly,
        leadingIcon = leadingIcon?.let { icon ->
            { Icon(imageVector = icon, contentDescription = null, tint = TextMuted) }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkInputBg,
            unfocusedContainerColor = DarkInputBg,
            disabledContainerColor = DarkInputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = OrangePrimary,
            unfocusedBorderColor = DarkInputBorder,
            focusedLabelColor = OrangePrimary,
            unfocusedLabelColor = TextMuted,
            cursorColor = OrangePrimary
        )
    )
}