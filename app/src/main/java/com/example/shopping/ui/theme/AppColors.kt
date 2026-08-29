package com.example.shopping.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ThemeManager {
    private const val PREFS_NAME = "app_theme_prefs"
    private const val KEY_IS_DARK = "is_dark_mode"
    private var prefs: SharedPreferences? = null

    var isDarkMode by mutableStateOf(true)
        private set

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            isDarkMode = prefs?.getBoolean(KEY_IS_DARK, true) ?: true
        }
    }

    fun toggleTheme(context: Context? = null) {
        setDarkMode(!isDarkMode, context)
    }

    fun setDarkMode(darkMode: Boolean, context: Context? = null) {
        isDarkMode = darkMode
        if (prefs == null && context != null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
        prefs?.edit()?.putBoolean(KEY_IS_DARK, darkMode)?.apply()
    }
}

// ==========================================
// SOFT LUXURY DARK PALETTE (Reduced Black Density - Soft Carbon)
// ==========================================
val SoftDarkBg = Color(0xFF131418)           // Soft Carbon / Slate Dark (Not harsh pure black)
val SoftDarkCard = Color(0xFF1E1F25)         // Refined Charcoal Card
val SoftDarkCardSecondary = Color(0xFF2B2C35)// Lighter Slate Card
val SoftDarkInputBg = Color(0xFF1E1F25)      // Input Field Background
val SoftDarkInputBorder = Color(0xFF3F414E)  // Subtle Input Border

// ==========================================
// WARM LIGHT PALETTE (Off-White with Warm Tint - Not Blinding Pure White)
// ==========================================
val WarmLightBg = Color(0xFFF7F7FA)          // Soft warm pearl off-white
val WarmLightCard = Color(0xFFFFFFFF)        // Clean Card Surface
val WarmLightCardSecondary = Color(0xFFECEEF3)// Warm Muted Card
val WarmLightInputBg = Color(0xFFFFFFFF)     // Input Field Background
val WarmLightInputBorder = Color(0xFFD3D6E0) // Subtle Input Border

// ==========================================
// DYNAMIC COLOR ACCESSORS BASED ON CURRENT THEME
// ==========================================
val DarkBg: Color get() = if (ThemeManager.isDarkMode) SoftDarkBg else WarmLightBg
val DarkCard: Color get() = if (ThemeManager.isDarkMode) SoftDarkCard else WarmLightCard
val DarkCardSecondary: Color get() = if (ThemeManager.isDarkMode) SoftDarkCardSecondary else WarmLightCardSecondary
val DarkInputBg: Color get() = if (ThemeManager.isDarkMode) SoftDarkInputBg else WarmLightInputBg
val DarkInputBorder: Color get() = if (ThemeManager.isDarkMode) SoftDarkInputBorder else WarmLightInputBorder

// ==========================================
// BUTTON & INTERACTION ACCENTS
// ==========================================
// In Dark Mode: Crisp Oyster White button with Carbon text.
// In Light Mode: Sleek Carbon button with Pure White text.
val PrimaryAccent: Color get() = if (ThemeManager.isDarkMode) Color(0xFFF3F4F6) else Color(0xFF181920)
val PrimaryHover: Color get() = if (ThemeManager.isDarkMode) Color(0xFFE5E7EB) else Color(0xFF282932)
val OrangePrimary: Color get() = PrimaryAccent
val OrangeHover: Color get() = PrimaryHover
val ButtonTextColor: Color get() = if (ThemeManager.isDarkMode) Color(0xFF111216) else Color(0xFFFFFFFF)

// ==========================================
// TYPOGRAPHY COLORS
// ==========================================
val TextWhite: Color get() = if (ThemeManager.isDarkMode) Color(0xFFF5F6F8) else Color(0xFF181920)
val TextMuted: Color get() = if (ThemeManager.isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
val TextDim: Color get() = if (ThemeManager.isDarkMode) Color(0xFF6B7280) else Color(0xFF9CA3AF)

// Status Accents
val AccentCoral = Color(0xFFFF5A5F)
val YellowStar = Color(0xFFFBBF24)
val DangerRed = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF10B981)
