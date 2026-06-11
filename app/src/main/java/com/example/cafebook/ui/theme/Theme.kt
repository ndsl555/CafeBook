package com.example.cafebook.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
    darkColorScheme(
        primary = Coffee80,
        onPrimary = CoffeeBrown40, // 主色上的文字顏色
        primaryContainer = Coffee40,
        onPrimaryContainer = Coffee80,
        secondary = CoffeeGrey80,
        onSecondary = CoffeeBrown40,
        tertiary = CoffeeLight80,
        onTertiary = CoffeeBrown40,
        surface = Color(0xFF201A19), // 背景色
        onSurface = Coffee80, // 選中的 label 顏色通常與此相關
        onSurfaceVariant = CoffeeGrey80, // 未選中的 label 顏色
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Coffee40,
        onPrimary = Color.White,
        primaryContainer = Coffee80,
        onPrimaryContainer = CoffeeBrown40,
        secondary = CoffeeGrey40,
        onSecondary = Color.White,
        tertiary = CoffeeBrown40,
        onTertiary = Color.White,
        surface = CoffeeLight80, // 底部導覽列背景通常使用 surface 或 surfaceContainer
        onSurface = Coffee40, // 選中時的文字顏色
        onSurfaceVariant = CoffeeGrey40, // 未選中時的文字顏色
    )

@Composable
fun CafeBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
