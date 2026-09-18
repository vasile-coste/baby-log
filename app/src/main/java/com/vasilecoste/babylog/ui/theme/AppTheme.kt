package com.vasilecoste.babylog.ui.theme

enum class AppTheme {
    DEFAULT,
    BLUE,
    PINK,
}

fun resolveAppTheme(gender: String?, override: AppTheme?): AppTheme = override ?: when (gender) {
    "male" -> AppTheme.BLUE
    "female" -> AppTheme.PINK
    else -> AppTheme.DEFAULT
}
