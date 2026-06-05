package com.fios.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class FIOSThemeType {
    HACKER_GREEN,
    MATRIX,
    NORD,
    DRACULA,
    KALI_PURPLE,
    CYBER_BLUE
}

private val HackerGreenColorScheme = darkColorScheme(
    primary = HackerGreenPrimary,
    background = HackerGreenBackground,
    surface = HackerGreenSurface,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val MatrixColorScheme = darkColorScheme(
    primary = MatrixPrimary,
    background = MatrixBackground,
    surface = MatrixSurface,
    onPrimary = Color.Black,
    onBackground = MatrixPrimary,
    onSurface = MatrixPrimary
)

private val NordColorScheme = darkColorScheme(
    primary = NordPrimary,
    background = NordBackground,
    surface = NordSurface,
    onPrimary = Color.Black,
    onBackground = Color(0xFFECEFF4),
    onSurface = Color(0xFFECEFF4)
)

private val DraculaColorScheme = darkColorScheme(
    primary = DraculaPrimary,
    background = DraculaBackground,
    surface = DraculaSurface,
    onPrimary = Color.Black,
    onBackground = Color(0xFFF8F8F2),
    onSurface = Color(0xFFF8F8F2)
)

private val KaliPurpleColorScheme = darkColorScheme(
    primary = KaliPurplePrimary,
    background = KaliPurpleBackground,
    surface = KaliPurpleSurface,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val CyberBlueColorScheme = darkColorScheme(
    primary = CyberBluePrimary,
    background = CyberBlueBackground,
    surface = CyberBlueSurface,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun FIOSTheme(
    themeType: FIOSThemeType = FIOSThemeType.HACKER_GREEN,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        FIOSThemeType.HACKER_GREEN -> HackerGreenColorScheme
        FIOSThemeType.MATRIX -> MatrixColorScheme
        FIOSThemeType.NORD -> NordColorScheme
        FIOSThemeType.DRACULA -> DraculaColorScheme
        FIOSThemeType.KALI_PURPLE -> KaliPurpleColorScheme
        FIOSThemeType.CYBER_BLUE -> CyberBlueColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
