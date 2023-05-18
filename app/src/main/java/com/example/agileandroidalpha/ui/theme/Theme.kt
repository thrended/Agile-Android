package com.example.agileandroidalpha.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat

private val DarkColorScheme = darkColorScheme(
    surface = Blue,
    onSurface = Navy,
    primary = Navy,
    onPrimary = Chartreuse,
    //primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val AndroidGreen = Color(0xFF3DDC84)
val AndroidGreenDark = Color(0xFF3DDC84)
val Orange = Color(0xFF3DDC84)
val OrangeDark = Color(0xFF3DDC84)

private val LightColorPalette = lightColors(
    primary = AndroidGreen,
    primaryVariant = AndroidGreenDark,
    secondary = Orange,
    secondaryVariant = OrangeDark
)

private val LightColorScheme = lightColorScheme(
    surface = Blue,
    onSurface = White,
    primary = LightBlue,
    onPrimary = Navy,
    //primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val droid_caption = Color.DarkGray
val droid_divider_color = Color.LightGray
private val droid_red = Color(0xFFE30425)
private val droid_white = Color.White
private val droid_purple_700 = Color(0xFF720D5D)
private val droid_purple_800 = Color(0xFF5D1049)
private val droid_purple_900 = Color(0xFF4E0D3A)

val droidColors = lightColors(
    primary = droid_purple_800,
    secondary = droid_red,
    surface = droid_purple_900,
    onSurface = droid_white,
    primaryVariant = droid_purple_700
)

val BottomSheetShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

@Composable
fun AgileAndroidAlphaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun AGDroidTheme(content: @Composable () -> Unit) {
    androidx.compose.material.MaterialTheme(droidColors, typography = droidTypography) {
        content()
    }
}