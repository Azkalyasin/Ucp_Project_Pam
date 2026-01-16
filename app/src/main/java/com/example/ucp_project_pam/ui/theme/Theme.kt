package com.example.ucp_project_pam.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
private val LightColorScheme = lightColorScheme(
    // Hardcode langsung tanpa variable
    primary = Color(0xFF2196F3),              // Biru
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),     // Biru terang
    onPrimaryContainer = Color(0xFF1976D2),   // Biru tua

    secondary = Color(0xFF2196F3),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBBDEFB),   // Biru terang
    onSecondaryContainer = Color(0xFF1976D2),

    tertiary = Color(0xFF42A5F5),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF90CAF9),
    onTertiaryContainer = Color(0xFF1976D2),

    error = Color(0xFFF44336),                // Merah
    onError = Color.White,
    errorContainer = Color(0xFFEF5350),
    onErrorContainer = Color(0xFFD32F2F),

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF212121),

    surface = Color.White,
    onSurface = Color(0xFF212121),

    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF616161),

    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFEEEEEE)
)

// ✅ HARDCODE DARK THEME JUGA
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1E88E5),              // Biru dark
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1565C0),     // Biru gelap
    onPrimaryContainer = Color(0xFF2196F3),

    secondary = Color(0xFF1E88E5),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1976D2),
    onSecondaryContainer = Color(0xFF2196F3),

    error = Color(0xFFEF5350),

    background = Color(0xFF212121),
    onBackground = Color(0xFFFAFAFA),

    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFFAFAFA),

    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = Color(0xFFE0E0E0)
)


@Composable
fun ucp_project_pamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}