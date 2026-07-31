package com.vasilecoste.babylog.ui.sleep

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.SleepEntry
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSleepDialog(
    entry: SleepEntry?,
    onSave: (startTime: LocalTime, durationMinutes: Int) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val initialTime = entry?.startTime ?: LocalTime.now()
    val timeState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )
    var hoursText by remember { mutableStateOf(((entry?.durationMinutes ?: 0) / 60).toString()) }
    var minutesText by remember { mutableStateOf(((entry?.durationMinutes ?: 0) % 60).toString()) }
    val hours = hoursText.toIntOrNull() ?: 0
    val minutes = minutesText.toIntOrNull() ?: 0
    val totalMinutes = hours * 60 + minutes

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
                TimeInput(state = timeState)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = hoursText,
                        onValueChange = { hoursText = it.filter { c -> c.isDigit() } },
                        label = { Text(stringResource(R.string.sleep_duration_hours_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = minutesText,
                        onValueChange = { minutesText = it.filter { c -> c.isDigit() } },
                        label = { Text(stringResource(R.string.sleep_duration_minutes_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
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
                            enabled = totalMinutes > 0,
                            onClick = {
                                onSave(LocalTime.of(timeState.hour, timeState.minute), totalMinutes)
                            },
                        ) { Text(stringResource(R.string.action_save)) }
                    }
                }
            }
        }
    }
}
