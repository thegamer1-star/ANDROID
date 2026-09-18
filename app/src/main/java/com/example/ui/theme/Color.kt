package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Accents
val ComoRed = Color(0xFFFF004F)
val ComoCrimson = Color(0xFFFF1744)
val ComoMagenta = Color(0xFFC13584)
val ComoPurple = Color(0xFF833AB4)
val ComoOrange = Color(0xFFFD1D1D)
val ComoAmber = Color(0xFFFCAF45)
val ComoCyan = Color(0xFF00E5FF)

// Dark Theme Colors
val DarkBackground = Color(0xFF0C0C10)
val DarkSurface = Color(0xFF14141A)
val DarkSurfaceVariant = Color(0xFF1E1E26)
val DarkBorder = Color(0xFF2B2B36)
val DarkTextPrimary = Color(0xFFF5F5F7)
val DarkTextSecondary = Color(0xFFA1A1B2)
val DarkTextTertiary = Color(0xFF707080)

// Light Theme Colors
val LightBackground = Color(0xFFFBFBFD)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF2F2F7)
val LightBorder = Color(0xFFE5E5EA)
val LightTextPrimary = Color(0xFF111115)
val LightTextSecondary = Color(0xFF636370)
val LightTextTertiary = Color(0xFF8E8E9F)

// Instagram Story Gradient
val ComoStoryGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFF58529),
        Color(0xFFDD2A7B),
        Color(0xFF8134AF),
        Color(0xFF515BD4)
    )
)

val ComoBrandGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFF004F),
        Color(0xFFD62976),
        Color(0xFF962FBF)
    )
)
