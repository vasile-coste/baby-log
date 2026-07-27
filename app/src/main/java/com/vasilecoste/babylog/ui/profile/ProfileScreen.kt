package com.vasilecoste.babylog.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.repository.DailyAggregate
import com.vasilecoste.babylog.ui.components.AddEditBabyDialog
import com.vasilecoste.babylog.ui.components.SimpleTopBar
import com.vasilecoste.babylog.ui.main.MainViewModel
import com.vasilecoste.babylog.ui.theme.AppTheme
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
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.profile_title),
                onMenuClick = onMenuClick,
                actions = {
                    if (baby != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (baby == null) {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                Text(stringResource(R.string.profile_no_data), modifier = Modifier.padding(16.dp))
            }
            return@Scaffold
        }

        val birthDate = baby.birthDate

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(baby.name, style = MaterialTheme.typography.headlineSmall)


            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Text(
                    stringResource(
                        R.string.profile_birth_date_label,
                        birthDate?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                            ?: stringResource(R.string.profile_birth_date_not_set),
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )

                if (birthDate != null) {
                    AgeSection(birthDate)
                }
            }

            HorizontalDivider()

            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                GrowthAndFeedingSection(uiState.chartData)
            }

            HorizontalDivider()

            ThemeSection(
                selectedOverride = uiState.themeOverride,
                onSelect = { override -> viewModel.setThemeOverride(baby.id, override) },
            )
        }
    }

    if (showEditDialog && baby != null) {
        AddEditBabyDialog(
            baby = baby,
            onConfirm = { name, birthDate, gender ->
                viewModel.updateBabyProfile(baby.id, name, birthDate, gender)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
private fun AgeSection(birthDate: LocalDate) {
    val today = LocalDate.now()
    val period = Period.between(birthDate, today)
    val totalMonths = ChronoUnit.MONTHS.between(birthDate, today)
    val totalDays = ChronoUnit.DAYS.between(birthDate, today)

    val monthsText = pluralStringResource(R.plurals.plural_months, period.months, period.months)
    val ageText = if (period.years > 0) {
        val yearsText = pluralStringResource(R.plurals.plural_years, period.years, period.years)
        "$yearsText, $monthsText"
    } else {
        monthsText
    }

    Text(stringResource(R.string.profile_age_label), style = MaterialTheme.typography.titleMedium)
    Text(ageText)
    Text(
        stringResource(
            R.string.profile_total_months,
            pluralStringResource(R.plurals.plural_months, totalMonths.toInt(), totalMonths.toInt()),
        ),
    )
    Text(
        stringResource(
            R.string.profile_total_days,
            pluralStringResource(R.plurals.plural_days, totalDays.toInt(), totalDays.toInt()),
        ),
    )
}

@Composable
private fun GrowthAndFeedingSection(chartData: List<DailyAggregate>) {
    val latestWeightKg = chartData.lastOrNull { it.weightKg != null }?.weightKg
    val latestHeightCm = chartData.lastOrNull { it.heightCm != null }?.heightCm

    val today = LocalDate.now()
    val rangeStart = today.minusDays(7)
    val last7Days = chartData.filter { it.date >= rangeStart && it.date < today }
    val avgFoodMl = last7Days.sumOf { it.totalFoodMl } / 7.0
    val minFoodMl = last7Days.minOfOrNull { it.totalFoodMl }
    val maxFoodMl = last7Days.maxOfOrNull { it.totalFoodMl }

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
    Text(
        if (minFoodMl != null) {
            stringResource(R.string.profile_min_food_7d, minFoodMl.toString())
        } else {
            stringResource(R.string.profile_no_data)
        },
    )
    Text(
        if (maxFoodMl != null) {
            stringResource(R.string.profile_max_food_7d, maxFoodMl.toString())
        } else {
            stringResource(R.string.profile_no_data)
        },
    )
}

@Composable
private fun ThemeSection(selectedOverride: AppTheme?, onSelect: (AppTheme?) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val themeOptions = listOf(
        null to stringResource(R.string.profile_theme_automatic),
        AppTheme.DEFAULT to stringResource(R.string.profile_theme_default),
        AppTheme.BLUE to stringResource(R.string.profile_theme_blue),
        AppTheme.PINK to stringResource(R.string.profile_theme_pink),
    )
    val currentLabel = themeOptions.first { it.first == selectedOverride }.second

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.profile_theme_label), style = MaterialTheme.typography.titleMedium)
        Text(currentLabel, style = MaterialTheme.typography.bodyLarge)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.profile_theme_label)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    themeOptions.forEach { (value, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedOverride == value,
                                    onClick = {
                                        onSelect(value)
                                        showDialog = false
                                    },
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = selectedOverride == value, onClick = null)
                            Text(text = label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.action_close)) }
            },
        )
    }
}

private fun formatDecimal(value: Double, decimals: Int): String {
    val factor = Math.pow(10.0, decimals.toDouble())
    val rounded = Math.round(value * factor) / factor
    return if (decimals == 0) rounded.toInt().toString() else rounded.toString()
}
