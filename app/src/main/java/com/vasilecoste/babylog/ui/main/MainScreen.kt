package com.vasilecoste.babylog.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.ui.chart.WeightFoodChartSheet
import com.vasilecoste.babylog.ui.components.AddBabyDialog
import com.vasilecoste.babylog.ui.components.BabyProfileSwitcherDialog
import com.vasilecoste.babylog.ui.components.ExpandableFab
import com.vasilecoste.babylog.ui.components.HeaderBar
import com.vasilecoste.babylog.ui.components.QuickInfoCard
import com.vasilecoste.babylog.ui.components.SimpleTopBar
import com.vasilecoste.babylog.ui.components.TimelineItem
import com.vasilecoste.babylog.ui.daypicker.DayPickerSheet
import com.vasilecoste.babylog.ui.entry.AddEditEntryDialog
import com.vasilecoste.babylog.ui.weight.AddWeightDialog

@Composable
fun MainScreen(
    onMenuClick: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsState()

    var fabExpanded by remember { mutableStateOf(false) }
    var showDayPicker by remember { mutableStateOf(false) }
    var showChart by remember { mutableStateOf(false) }
    var showAddEntry by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<Entry?>(null) }
    var showAddWeight by remember { mutableStateOf(false) }
    var showBabySwitcher by remember { mutableStateOf(false) }
    var showAddBaby by remember { mutableStateOf(false) }

    if (!uiState.hasBabies) {
        Scaffold(topBar = { SimpleTopBar(title = stringResource(R.string.app_name), onMenuClick = onMenuClick) }) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                EmptyState(onAddBaby = { showAddBaby = true })
            }
        }
        if (showAddBaby) {
            AddBabyDialog(
                onConfirm = { name, birthDate ->
                    viewModel.addBabyProfile(name, birthDate)
                    showAddBaby = false
                },
                onDismiss = { showAddBaby = false },
            )
        }
        return
    }

    Scaffold(
        topBar = {
            HeaderBar(
                babyName = uiState.selectedBaby?.name ?: "",
                selectedDate = uiState.selectedDate,
                onMenuClick = onMenuClick,
                onDayClick = { showDayPicker = true },
                onChartClick = { showChart = true },
                onTitleClick = { showBabySwitcher = true },
            )
        },
        floatingActionButton = {
            ExpandableFab(
                expanded = fabExpanded,
                onToggle = { fabExpanded = !fabExpanded },
                onAddEntry = {
                    fabExpanded = false
                    showAddEntry = true
                },
                onAddWeight = {
                    fabExpanded = false
                    showAddWeight = true
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            QuickInfoCard(stats = uiState.quickStats)
            if (uiState.entries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_entries_today), style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.entries) { entry ->
                        TimelineItem(entry = entry, onLongPress = { editingEntry = entry })
                    }
                }
            }
        }
    }

    if (showDayPicker) {
        DayPickerSheet(
            dates = uiState.pickerDates,
            selectedDate = uiState.selectedDate,
            onSelect = { viewModel.selectDate(it) },
            onDismiss = { showDayPicker = false },
        )
    }

    if (showChart) {
        WeightFoodChartSheet(data = uiState.chartData, onDismiss = { showChart = false })
    }

    if (showAddEntry) {
        val hasVitaminToday = uiState.entries.any { it.vitamin }
        AddEditEntryDialog(
            entry = null,
            showVitaminOption = !hasVitaminToday,
            onSave = { time, foodMl, poop, pee, puke, vitamin, breastfed ->
                viewModel.addEntry(time, foodMl, poop, pee, puke, vitamin, breastfed)
                showAddEntry = false
            },
            onDismiss = { showAddEntry = false },
        )
    }

    editingEntry?.let { entry ->
        AddEditEntryDialog(
            entry = entry,
            showVitaminOption = true,
            onSave = { time, foodMl, poop, pee, puke, vitamin, breastfed ->
                viewModel.updateEntry(
                    entry.copy(
                        time = time,
                        foodMl = foodMl,
                        poop = poop,
                        pee = pee,
                        puke = puke,
                        vitamin = vitamin,
                        breastfed = breastfed,
                    ),
                )
                editingEntry = null
            },
            onDelete = {
                viewModel.deleteEntry(entry)
                editingEntry = null
            },
            onDismiss = { editingEntry = null },
        )
    }

    if (showAddWeight) {
        AddWeightDialog(
            onSave = { weightKg, heightCm ->
                viewModel.addWeight(weightKg, heightCm)
                showAddWeight = false
            },
            onDismiss = { showAddWeight = false },
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
        AddBabyDialog(
            onConfirm = { name, birthDate ->
                viewModel.addBabyProfile(name, birthDate)
                showAddBaby = false
            },
            onDismiss = { showAddBaby = false },
        )
    }
}

@Composable
private fun EmptyState(onAddBaby: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Text(stringResource(R.string.empty_state_message), style = MaterialTheme.typography.titleMedium)
            Button(onClick = onAddBaby) { Text(stringResource(R.string.action_add_baby)) }
        }
    }
}
