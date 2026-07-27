package com.erp.client.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ErpBlue = Color(0xFF1565C0)
private val ErpBlueDark = Color(0xFF0D47A1)
private val ErpAmber = Color(0xFFFFA000)

private val LightColors = lightColorScheme(
    primary = ErpBlue,
    secondary = ErpAmber,
    tertiary = ErpBlueDark
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = ErpAmber,
    tertiary = ErpBlueDark
)

@Composable
fun ERPXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
