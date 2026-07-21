package com.vasilecoste.babylog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderBar(
    babyName: String,
    selectedDate: LocalDate,
    onDayClick: () -> Unit,
    onChartClick: () -> Unit,
    onTitleClick: () -> Unit,
) {
    TopAppBar(
        title = {
            TextButton(onClick = onTitleClick) {
                Text(babyName)
            }
        },
        navigationIcon = {
            TextButton(onClick = onDayClick) {
                Text(formatDayLabel(selectedDate))
            }
        },
        actions = {
            IconButton(onClick = onChartClick) {
                Icon(Icons.Filled.ShowChart, contentDescription = "Weight and food chart")
            }
        },
    )
}

private fun formatDayLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when {
        date == today -> "Today"
        date == today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }
}
