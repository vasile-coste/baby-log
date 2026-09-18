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
    primary = GrayDarkPrimary,
    onPrimary = GrayDarkOnPrimary,
    primaryContainer = GrayDarkPrimaryContainer,
    onPrimaryContainer = GrayDarkOnPrimaryContainer,
    secondary = GrayDarkSecondary,
    secondaryContainer = GrayDarkSecondaryContainer,
    tertiary = GrayDarkTertiary,
    background = GrayDarkBackground,
    surfaceVariant = GrayDarkSurfaceVariant,
    onSurfaceVariant = GrayDarkOnSurfaceVariant,
)

private val LightColorScheme = lightColorScheme(
    primary = GrayLightPrimary,
    onPrimary = GrayLightOnPrimary,
    primaryContainer = GrayLightPrimaryContainer,
    onPrimaryContainer = GrayLightOnPrimaryContainer,
    secondary = GrayLightSecondary,
    secondaryContainer = GrayLightSecondaryContainer,
    tertiary = GrayLightTertiary,
    background = GrayLightBackground,
    surfaceVariant = GrayLightSurfaceVariant,
    onSurfaceVariant = GrayLightOnSurfaceVariant,
)

private val BlueDarkColorScheme = darkColorScheme(
    primary = BlueDarkPrimary,
    onPrimary = BlueDarkOnPrimary,
    primaryContainer = BlueDarkPrimaryContainer,
    onPrimaryContainer = BlueDarkOnPrimaryContainer,
    secondary = BlueDarkSecondary,
    secondaryContainer = BlueDarkSecondaryContainer,
    tertiary = BlueDarkTertiary,
    background = BlueDarkBackground,
    surfaceVariant = BlueDarkSurfaceVariant,
    onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

private val BlueLightColorScheme = lightColorScheme(
    primary = BlueLightPrimary,
    onPrimary = BlueLightOnPrimary,
    primaryContainer = BlueLightPrimaryContainer,
    onPrimaryContainer = BlueLightOnPrimaryContainer,
    secondary = BlueLightSecondary,
    secondaryContainer = BlueLightSecondaryContainer,
    tertiary = BlueLightTertiary,
    background = BlueLightBackground,
    surfaceVariant = BlueLightSurfaceVariant,
    onSurfaceVariant = BlueLightOnSurfaceVariant,
)

private val PinkDarkColorScheme = darkColorScheme(
    primary = PinkDarkPrimary,
    onPrimary = PinkDarkOnPrimary,
    primaryContainer = PinkDarkPrimaryContainer,
    onPrimaryContainer = PinkDarkOnPrimaryContainer,
    secondary = PinkDarkSecondary,
    secondaryContainer = PinkDarkSecondaryContainer,
    tertiary = PinkDarkTertiary,
    background = PinkDarkBackground,
    surfaceVariant = PinkDarkSurfaceVariant,
    onSurfaceVariant = PinkDarkOnSurfaceVariant,
)

private val PinkLightColorScheme = lightColorScheme(
    primary = PinkLightPrimary,
    onPrimary = PinkLightOnPrimary,
    primaryContainer = PinkLightPrimaryContainer,
    onPrimaryContainer = PinkLightOnPrimaryContainer,
    secondary = PinkLightSecondary,
    secondaryContainer = PinkLightSecondaryContainer,
    tertiary = PinkLightTertiary,
    background = PinkLightBackground,
    surfaceVariant = PinkLightSurfaceVariant,
    onSurfaceVariant = PinkLightOnSurfaceVariant,
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
