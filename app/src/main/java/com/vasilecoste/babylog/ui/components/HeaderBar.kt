package com.vasilecoste.babylog.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vasilecoste.babylog.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderBar(
    babyName: String,
    selectedDate: LocalDate,
    onMenuClick: () -> Unit,
    onDayClick: () -> Unit,
    onTitleClick: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.cd_open_menu))
            }
        },
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = onTitleClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
                ) {
                    Text(babyName)
                }
            }
        },
        actions = {
            TextButton(
                onClick = onDayClick,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
            ) {
                Text(formatDayLabel(selectedDate))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}

@Composable
private fun formatDayLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when {
        date == today -> stringResource(R.string.day_today)
        date == today.minusDays(1) -> stringResource(R.string.day_yesterday)
        else -> date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }
}
