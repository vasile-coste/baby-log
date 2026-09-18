package com.vasilecoste.babylog.ui.sleep

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.SleepEntry
import java.time.Duration
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSleepDialog(
    entry: SleepEntry?,
    onSave: (startTime: LocalTime, endTime: LocalTime?) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val now = LocalTime.now()
    val initialStart = entry?.startTime ?: now
    
    // Logic:
    // Add Mode (entry == null): ended = false (empty end time)
    // Edit Mode (entry != null): ended = true (if it was empty, default to now)
    val initialIsEnded = remember { entry != null }
    var ended by remember { mutableStateOf(initialIsEnded) }

    val initialEnd = entry?.endTime ?: now
    
    val startState = rememberTimePickerState(
        initialHour = initialStart.hour,
        initialMinute = initialStart.minute,
        is24Hour = true,
    )
    val endState = rememberTimePickerState(
        initialHour = initialEnd.hour,
        initialMinute = initialEnd.minute,
        is24Hour = true,
    )
    val durationMinutes = if (ended) durationMinutesBetween(startState, endState) else 0

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    stringResource(
                        if (entry == null) R.string.sleep_add_title else R.string.sleep_edit_title,
                    ),
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(stringResource(R.string.sleep_start_time_label), style = MaterialTheme.typography.labelLarge)
                TimeInput(state = startState)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = ended, onCheckedChange = { ended = it })
                    Text(stringResource(R.string.sleep_ended_label), modifier = Modifier.padding(start = 8.dp))
                }

                if (ended) {
                    Text(stringResource(R.string.sleep_end_time_label), style = MaterialTheme.typography.labelLarge)
                    TimeInput(state = endState)

                    Text(
                        stringResource(R.string.sleep_duration_preview, durationMinutes / 60, durationMinutes % 60),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (onDelete != null) {
                        TextButton(onClick = onDelete) { Text(stringResource(R.string.action_delete)) }
                    } else {
                        Column {}
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
                        TextButton(
                            enabled = !ended || durationMinutes > 0,
                            onClick = {
                                onSave(
                                    LocalTime.of(startState.hour, startState.minute),
                                    if (ended) LocalTime.of(endState.hour, endState.minute) else null,
                                )
                            },
                        ) { Text(stringResource(R.string.action_save)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun durationMinutesBetween(startState: TimePickerState, endState: TimePickerState): Int {
    val start = LocalTime.of(startState.hour, startState.minute)
    val end = LocalTime.of(endState.hour, endState.minute)
    val minutes = Duration.between(start, end).toMinutes()
    return (if (minutes < 0) minutes + 24 * 60 else minutes).toInt()
}
