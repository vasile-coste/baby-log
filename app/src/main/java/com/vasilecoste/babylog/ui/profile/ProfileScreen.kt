package com.vasilecoste.babylog.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.repository.DailyAggregate
import com.vasilecoste.babylog.ui.components.DatePickerDialogHost
import com.vasilecoste.babylog.ui.components.SimpleTopBar
import com.vasilecoste.babylog.ui.main.MainViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(onMenuClick: () -> Unit, viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val baby = uiState.selectedBaby

    Scaffold(topBar = { SimpleTopBar(title = stringResource(R.string.profile_title), onMenuClick = onMenuClick) }) { padding ->
        if (baby == null) {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                Text(stringResource(R.string.profile_no_data), modifier = Modifier.padding(16.dp))
            }
            return@Scaffold
        }

        var showDatePicker by remember(baby.id) { mutableStateOf(false) }
        val birthDate = baby.birthDate

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(baby.name, style = MaterialTheme.typography.headlineSmall)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(
                        R.string.profile_birth_date_label,
                        birthDate?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                            ?: stringResource(R.string.profile_birth_date_not_set),
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                TextButton(onClick = { showDatePicker = true }) {
                    Text(
                        stringResource(
                            if (birthDate == null) R.string.profile_set_birth_date else R.string.profile_edit_birth_date,
                        ),
                    )
                }
            }

            if (showDatePicker) {
                DatePickerDialogHost(
                    initialDate = birthDate,
                    onConfirm = { viewModel.updateBabyBirthDate(baby.id, it) },
                    onDismiss = { showDatePicker = false },
                )
            }

            if (birthDate != null) {
                AgeSection(birthDate)
            }

            HorizontalDivider()

            GrowthAndFeedingSection(uiState.chartData)
        }
    }
}

@Composable
private fun AgeSection(birthDate: LocalDate) {
    val today = LocalDate.now()
    val period = Period.between(birthDate, today)
    val totalMonths = ChronoUnit.MONTHS.between(birthDate, today)
    val totalDays = ChronoUnit.DAYS.between(birthDate, today)

    Text(stringResource(R.string.profile_age_label), style = MaterialTheme.typography.titleMedium)
    Text(
        if (period.years > 0) {
            stringResource(R.string.profile_age_years_months, period.years, period.months)
        } else {
            stringResource(R.string.profile_age_months_only, period.months)
        },
    )
    Text(stringResource(R.string.profile_total_months, totalMonths.toInt()))
    Text(stringResource(R.string.profile_total_days, totalDays.toInt()))
}

@Composable
private fun GrowthAndFeedingSection(chartData: List<DailyAggregate>) {
    val latestWeightKg = chartData.lastOrNull { it.weightKg != null }?.weightKg
    val latestHeightCm = chartData.lastOrNull { it.heightCm != null }?.heightCm

    val today = LocalDate.now()
    val rangeStart = today.minusDays(7)
    val avgFoodMl = chartData
        .filter { it.date >= rangeStart && it.date < today }
        .sumOf { it.totalFoodMl } / 7.0

    Text(
        if (latestWeightKg != null) {
            stringResource(R.string.profile_latest_weight, formatDecimal(latestWeightKg, 2))
        } else {
            stringResource(R.string.profile_no_data)
        },
    )
    Text(
        if (latestHeightCm != null) {
            stringResource(R.string.profile_latest_height, formatDecimal(latestHeightCm, 1))
        } else {
            stringResource(R.string.profile_no_data)
        },
    )
    Text(stringResource(R.string.profile_avg_food_7d, avgFoodMl.roundToInt().toString()))
}

private fun formatDecimal(value: Double, decimals: Int): String {
    val factor = Math.pow(10.0, decimals.toDouble())
    val rounded = Math.round(value * factor) / factor
    return if (decimals == 0) rounded.toInt().toString() else rounded.toString()
}
