package com.idat

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.idat.data.local.preferences.UserPreferencesManager

// 🎨 Color definitions
object AppColors {
    // Light theme colors
    val LightPrimary = Color(0xFF6200EE)
    val LightSecondary = Color(0xFF03DAC6)
    val LightBackground = Color(0xFFF5F5F5)
    val LightSurface = Color(0xFFFFFFFF)
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightOnSecondary = Color(0xFF000000)
    val LightOnBackground = Color(0xFF000000)
    val LightOnSurface = Color(0xFF000000)
    
    // Dark theme colors
    val DarkPrimary = Color(0xFFBB86FC)
    val DarkSecondary = Color(0xFF03DAC6)
    val DarkBackground = Color(0xFF121212)
    val DarkSurface = Color(0xFF1E1E1E)
    val DarkOnPrimary = Color(0xFF000000)
    val DarkOnSecondary = Color(0xFF000000)
    val DarkOnBackground = Color(0xFFFFFFFF)
    val DarkOnSurface = Color(0xFFFFFFFF)
    
    // Gradient colors for light theme
    val LightGradientStart = Color(0xFF667EEA)
    val LightGradientEnd = Color(0xFF764BA2)
    
    // Gradient colors for dark theme
    val DarkGradientStart = Color(0xFF434343)
    val DarkGradientEnd = Color(0xFF000000)
}

@Composable
fun getGradientColors(isDarkTheme: Boolean): List<Color> {
    return if (isDarkTheme) {
        listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460)
        )
    } else {
        listOf(
            Color(0xFF7F00FF).copy(alpha = 0.4f),
            Color(0xFFE100FF).copy(alpha = 0.35f),
            Color(0xFF00C6FF).copy(alpha = 0.35f)
        )
    }
}

private val LightColorScheme = lightColorScheme(
    primary = AppColors.LightPrimary,
    secondary = AppColors.LightSecondary,
    background = AppColors.LightBackground,
    surface = AppColors.LightSurface,
    onPrimary = AppColors.LightOnPrimary,
    onSecondary = AppColors.LightOnSecondary,
    onBackground = AppColors.LightOnBackground,
    onSurface = AppColors.LightOnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.DarkPrimary,
    secondary = AppColors.DarkSecondary,
    background = AppColors.DarkBackground,
    surface = AppColors.DarkSurface,
    onPrimary = AppColors.DarkOnPrimary,
    onSecondary = AppColors.DarkOnSecondary,
    onBackground = AppColors.DarkOnBackground,
    onSurface = AppColors.DarkOnSurface
)

@Composable
fun AppTheme(
    userPreferencesManager: UserPreferencesManager,
    content: @Composable () -> Unit
) {
    val isDarkTheme by userPreferencesManager.isDarkTheme.collectAsState(initial = false)
    
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
