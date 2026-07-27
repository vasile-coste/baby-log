package com.vasilecoste.babylog.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
)

private val BlueDarkColorScheme = darkColorScheme(
    primary = BlueDarkPrimary,
    onPrimary = BlueDarkOnPrimary,
    primaryContainer = BlueDarkPrimaryContainer,
    onPrimaryContainer = BlueDarkOnPrimaryContainer,
    secondary = BlueDarkSecondary,
    secondaryContainer = BlueDarkSecondaryContainer,
    tertiary = BlueDarkTertiary,
)

private val BlueLightColorScheme = lightColorScheme(
    primary = BlueLightPrimary,
    onPrimary = BlueLightOnPrimary,
    primaryContainer = BlueLightPrimaryContainer,
    onPrimaryContainer = BlueLightOnPrimaryContainer,
    secondary = BlueLightSecondary,
    secondaryContainer = BlueLightSecondaryContainer,
    tertiary = BlueLightTertiary,
)

private val PinkDarkColorScheme = darkColorScheme(
    primary = PinkDarkPrimary,
    onPrimary = PinkDarkOnPrimary,
    primaryContainer = PinkDarkPrimaryContainer,
    onPrimaryContainer = PinkDarkOnPrimaryContainer,
    secondary = PinkDarkSecondary,
    secondaryContainer = PinkDarkSecondaryContainer,
    tertiary = PinkDarkTertiary,
)

private val PinkLightColorScheme = lightColorScheme(
    primary = PinkLightPrimary,
    onPrimary = PinkLightOnPrimary,
    primaryContainer = PinkLightPrimaryContainer,
    onPrimaryContainer = PinkLightOnPrimaryContainer,
    secondary = PinkLightSecondary,
    secondaryContainer = PinkLightSecondaryContainer,
    tertiary = PinkLightTertiary,
)

@Composable
fun BabyLogTheme(
    theme: AppTheme = AppTheme.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (theme) {
        AppTheme.BLUE -> if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
        AppTheme.PINK -> if (darkTheme) PinkDarkColorScheme else PinkLightColorScheme
        AppTheme.DEFAULT -> when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
