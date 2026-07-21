package com.vasilecoste.babylog.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vasilecoste.babylog.data.db.entity.Entry
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryDialog(
    entry: Entry?,
    showVitaminOption: Boolean,
    onSave: (time: LocalTime, foodMl: Int?, poop: Boolean, pee: Boolean, vitamin: Boolean) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val initialTime = entry?.time ?: LocalTime.now()
    val timeState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )
    var foodMlText by remember { mutableStateOf(entry?.foodMl?.toString().orEmpty()) }
    var poop by remember { mutableStateOf(entry?.poop ?: false) }
    var pee by remember { mutableStateOf(entry?.pee ?: false) }
    var vitamin by remember { mutableStateOf(entry?.vitamin ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    if (entry == null) "Add entry" else "Edit entry",
                    style = MaterialTheme.typography.titleLarge,
                )

                TimeInput(state = timeState)

                OutlinedTextField(
                    value = foodMlText,
                    onValueChange = { foodMlText = it.filter { c -> c.isDigit() } },
                    label = { Text("Food amount (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                CheckboxRow(label = "Poop", checked = poop, onCheckedChange = { poop = it })
                CheckboxRow(label = "Pee", checked = pee, onCheckedChange = { pee = it })
                if (showVitaminOption) {
                    CheckboxRow(label = "Vitamins", checked = vitamin, onCheckedChange = { vitamin = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (onDelete != null) {
                        TextButton(onClick = onDelete) { Text("Delete") }
                    } else {
                        Column {}
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        TextButton(
                            onClick = {
                                onSave(
                                    LocalTime.of(timeState.hour, timeState.minute),
                                    foodMlText.toIntOrNull(),
                                    poop,
                                    pee,
                                    vitamin,
                                )
                            },
                        ) { Text("Save") }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label)
    }
}
