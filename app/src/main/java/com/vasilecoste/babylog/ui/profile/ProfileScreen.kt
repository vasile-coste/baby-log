package com.vasilecoste.babylog.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Profile Header
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.ChildCare,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                baby.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            if (birthDate != null) {
                AgeText(birthDate)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Information Cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Birth Date Row
                    InfoRow(
                        icon = painterResource(R.drawable.id_cake),
                        label = stringResource(R.string.growth_date_label),
                        value = birthDate?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                            ?: stringResource(R.string.profile_birth_date_not_set)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                    // Theme Row
                    ThemeSectionRow(
                        selectedOverride = uiState.themeOverride,
                        onSelect = { override -> viewModel.setThemeOverride(baby.id, override) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GrowthAndFeedingSection(uiState.chartData)

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.action_delete))
            }
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

    if (showDeleteDialog && baby != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.profile_delete_baby_title)) },
            text = { Text(stringResource(R.string.profile_delete_baby_message, baby.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBabyProfile(baby)
                    showDeleteDialog = false
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }
}

@Composable
private fun AgeText(birthDate: LocalDate) {
    val today = LocalDate.now()
    val period = Period.between(birthDate, today)
    val totalDays = ChronoUnit.DAYS.between(birthDate, today)
    val totalDaysText = pluralStringResource(R.plurals.plural_days, totalDays.toInt(), totalDays.toInt())

    val yearsText = pluralStringResource(R.plurals.plural_years, period.years, period.years)
    val monthsText = pluralStringResource(R.plurals.plural_months, period.months, period.months)
    val daysText = pluralStringResource(R.plurals.plural_days, period.days, period.days)

    val ageText = if (period.years > 0) {
        "$yearsText, $monthsText, $daysText"
    } else if (period.months > 0) {
        "$monthsText, $daysText"
    } else {
        daysText
    }

    Text(
        text = "$ageText ($totalDaysText)",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun GrowthAndFeedingSection(chartData: List<DailyAggregate>) {
    val latestWeightKg = chartData.lastOrNull { it.weightKg != null }?.weightKg
    val latestHeightCm = chartData.lastOrNull { it.heightCm != null }?.heightCm

    val today = LocalDate.now()
    val rangeStart = today.minusDays(7)
    val last7Days = chartData.filter { it.date >= rangeStart && it.date < today }
    val avgFoodMl = if (last7Days.isNotEmpty()) last7Days.sumOf { it.totalFoodMl } / 7.0 else 0.0
    val minFoodMl = last7Days.minOfOrNull { it.totalFoodMl }
    val maxFoodMl = last7Days.maxOfOrNull { it.totalFoodMl }

    // Growth Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(painterResource(R.drawable.id_growth_filled), stringResource(R.string.profile_growth))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoItem(
                    modifier = Modifier.weight(1f),
                    icon = painterResource(R.drawable.id_weight),
                    label = stringResource(R.string.weight_kg_label),
                    value = latestWeightKg?.let { formatDecimal(it, 2) + " kg" } ?: stringResource(R.string.profile_no_data)
                )
                InfoItem(
                    modifier = Modifier.weight(1f),
                    icon = painterResource(R.drawable.id_height),
                    label = stringResource(R.string.height_cm_label),
                    value = latestHeightCm?.let { formatDecimal(it, 1) + " cm" } ?: stringResource(R.string.profile_no_data)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Feeding Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(painterResource(R.drawable.id_feed), stringResource(R.string.profile_food))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FeedingStatRow(
                    label = stringResource(R.string.profile_avg_food_7d, "").replace(":", ""),
                    value = "${avgFoodMl.roundToInt()} ml"
                )
                FeedingStatRow(
                    label = stringResource(R.string.profile_min_food_7d, "").replace(":", ""),
                    value = minFoodMl?.let { "$it ml" } ?: "—"
                )
                FeedingStatRow(
                    label = stringResource(R.string.profile_max_food_7d, "").replace(":", ""),
                    value = maxFoodMl?.let { "$it ml" } ?: "—"
                )
            }
        }
    }
}

@Composable
private fun InfoRow(icon: Painter, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun InfoItem(modifier: Modifier = Modifier, icon: Painter, label: String, value: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FeedingStatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(painter: Painter, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(painter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ThemeSectionRow(selectedOverride: AppTheme?, onSelect: (AppTheme?) -> Unit) {
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painterResource(R.drawable.id_theme),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.profile_theme_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(currentLabel, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
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
