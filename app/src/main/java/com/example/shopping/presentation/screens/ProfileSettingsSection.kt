package com.example.shopping.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ui.theme.DangerRed
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.DarkCardSecondary
import com.example.shopping.ui.theme.DarkInputBorder
import com.example.shopping.ui.theme.PrimaryAccent
import com.example.shopping.ui.theme.SoftDarkCardSecondary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.example.shopping.ui.theme.ThemeManager
import com.example.shopping.ui.theme.WarmLightCardSecondary

@Composable
fun ProfileTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "My Profile",
            color = TextWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileSettingsSection(
    onAdminClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Appearance Theme Mode Switcher Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, DarkInputBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (ThemeManager.isDarkMode) "Dark Mode" else "Light Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextWhite
                    )
                    Text(
                        text = if (ThemeManager.isDarkMode) "Soft carbon black theme" else "Soft warm pearl light theme",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
                Switch(
                    checked = ThemeManager.isDarkMode,
                    onCheckedChange = { ThemeManager.toggleTheme() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SoftDarkCardSecondary,
                        uncheckedThumbColor = Color(0xFF181920),
                        uncheckedTrackColor = WarmLightCardSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Owner Product Portal
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAdminClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "👑 Owner Product Portal (Manage)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Log Out Button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkCardSecondary)
        ) {
            Text(text = "Log Out", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Delete Account Button
        OutlinedButton(
            onClick = onDeleteAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = DangerRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Delete Account", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = DangerRed)
            }
        }
    }
}
