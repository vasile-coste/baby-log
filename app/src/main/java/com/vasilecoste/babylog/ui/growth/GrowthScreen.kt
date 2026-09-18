package com.vasilecoste.babylog.ui.growth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import com.vasilecoste.babylog.ui.components.AddEditBabyDialog
import com.vasilecoste.babylog.ui.components.BabyProfileSwitcherDialog
import com.vasilecoste.babylog.ui.components.SimpleTopBar
import com.vasilecoste.babylog.ui.main.MainViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val monthYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

@Composable
fun GrowthScreen(
    onMenuClick: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAddRecord by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<WeightRecord?>(null) }
    var sortAscending by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showBabySwitcher by remember { mutableStateOf(false) }
    var showAddBaby by remember { mutableStateOf(false) }

    val monthRecords = uiState.weightRecords.filter { YearMonth.from(it.date) == uiState.selectedMonth }

    Scaffold(
        topBar = {
            SimpleTopBar(
                onMenuClick = onMenuClick,
                titleContent = {
                    TextButton(
                        onClick = { showBabySwitcher = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
                    ) {
                        Text(uiState.selectedBaby?.name ?: "")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showMonthPicker = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
                    ) {
                        Text(uiState.selectedMonth.format(monthYearFormatter))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddRecord = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.growth_add_cd))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GrowthSummaryCard(records = uiState.weightRecords)

            if (monthRecords.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(
                            if (uiState.weightRecords.isEmpty()) R.string.growth_no_records else R.string.growth_no_records_month,
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                val sortedRecords = if (sortAscending) {
                    monthRecords.sortedBy { it.date }
                } else {
                    monthRecords.sortedByDescending { it.date }
                }
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)) {
                            IconButton(
                                onClick = { sortAscending = !sortAscending },
                                modifier = Modifier.align(Alignment.CenterEnd),
                            ) {
                                Icon(
                                    imageVector = if (sortAscending) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                    contentDescription = stringResource(R.string.sort),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    items(sortedRecords) { record ->
                        GrowthTimelineItem(record = record, onLongPress = { editingRecord = record })
                    }
                }
            }
        }
    }

    if (showAddRecord) {
        AddEditWeightDialog(
            record = null,
            onSave = { date, weightKg, heightCm ->
                viewModel.addWeightRecord(date, weightKg, heightCm)
                showAddRecord = false
            },
            onDismiss = { showAddRecord = false },
        )
    }

    editingRecord?.let { record ->
        AddEditWeightDialog(
            record = record,
            onSave = { date, weightKg, heightCm ->
                viewModel.updateWeightRecord(record.copy(date = date, weightKg = weightKg, heightCm = heightCm))
                editingRecord = null
            },
            onDelete = {
                viewModel.deleteWeightRecord(record)
                editingRecord = null
            },
            onDismiss = { editingRecord = null },
        )
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            selected = uiState.selectedMonth,
            onSelect = {
                viewModel.selectMonth(it)
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false },
        )
    }

    if (showBabySwitcher) {
        BabyProfileSwitcherDialog(
            babies = uiState.babies,
            selectedBabyId = uiState.selectedBaby?.id,
            onSelect = { viewModel.selectBaby(it) },
            onAddNew = {
                showBabySwitcher = false
                showAddBaby = true
            },
            onDismiss = { showBabySwitcher = false },
        )
    }

    if (showAddBaby) {
        AddEditBabyDialog(
            onConfirm = { name, birthDate, gender ->
                viewModel.addBabyProfile(name, birthDate, gender)
                showAddBaby = false
            },
            onDismiss = { showAddBaby = false },
        )
    }
}
