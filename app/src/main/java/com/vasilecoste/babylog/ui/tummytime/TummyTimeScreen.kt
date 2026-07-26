package com.vasilecoste.babylog.ui.tummytime

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.vasilecoste.babylog.data.db.entity.TummyTimeEntry
import com.vasilecoste.babylog.ui.components.AddEditBabyDialog
import com.vasilecoste.babylog.ui.components.BabyProfileSwitcherDialog
import com.vasilecoste.babylog.ui.components.HeaderBar
import com.vasilecoste.babylog.ui.daypicker.DayPickerSheet
import com.vasilecoste.babylog.ui.main.MainViewModel

@Composable
fun TummyTimeScreen(
    onMenuClick: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDayPicker by remember { mutableStateOf(false) }
    var showBabySwitcher by remember { mutableStateOf(false) }
    var showAddBaby by remember { mutableStateOf(false) }
    var showAddManual by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<TummyTimeEntry?>(null) }
    var sortAscending by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HeaderBar(
                babyName = uiState.selectedBaby?.name ?: "",
                selectedDate = uiState.selectedDate,
                onMenuClick = onMenuClick,
                onDayClick = { showDayPicker = true },
                onTitleClick = { showBabySwitcher = true },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddManual = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.tummy_time_add_manual_cd))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TummyTimeSummaryCard(totalSeconds = uiState.totalTummyTimeSecondsToday)

            TummyTimerButton(
                isRunning = uiState.tummyTimerRunning,
                startEpochMillis = uiState.tummyTimerStartEpochMillis,
                onStart = { viewModel.startTummyTimer() },
                onStop = { viewModel.stopTummyTimer() },
                modifier = Modifier.padding(vertical = 16.dp),
            )

            if (uiState.tummyTimeEntries.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.tummy_time_no_entries), style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                val sortedEntries = if (sortAscending) {
                    uiState.tummyTimeEntries.sortedBy { it.startTime }
                } else {
                    uiState.tummyTimeEntries.sortedByDescending { it.startTime }
                }
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
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
                    items(sortedEntries) { entry ->
                        TummyTimeTimelineItem(entry = entry, onLongPress = { editingEntry = entry })
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

    if (showAddManual) {
        AddEditTummyTimeDialog(
            entry = null,
            onSave = { startTime, durationSeconds ->
                viewModel.addManualTummyTime(uiState.selectedDate, startTime, durationSeconds)
                showAddManual = false
            },
            onDismiss = { showAddManual = false },
        )
    }

    editingEntry?.let { entry ->
        AddEditTummyTimeDialog(
            entry = entry,
            onSave = { startTime, durationSeconds ->
                viewModel.updateTummyTimeEntry(entry.copy(startTime = startTime, durationSeconds = durationSeconds))
                editingEntry = null
            },
            onDelete = {
                viewModel.deleteTummyTimeEntry(entry)
                editingEntry = null
            },
            onDismiss = { editingEntry = null },
        )
    }
}
