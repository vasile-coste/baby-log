package com.vasilecoste.babylog.ui.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import com.vasilecoste.babylog.ui.components.DatePickerField
import java.time.LocalDate

@Composable
fun AddEditWeightDialog(
    record: WeightRecord?,
    onSave: (date: LocalDate, weightKg: Double?, heightCm: Double?) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    var date by remember { mutableStateOf(record?.date ?: LocalDate.now()) }
    var weightText by remember { mutableStateOf(record?.weightKg?.toString() ?: "") }
    var heightText by remember { mutableStateOf(record?.heightCm?.toString() ?: "") }
    val weight = weightText.toDoubleOrNull()
    val height = heightText.toDoubleOrNull()
    val hasWeight = weight != null && weight > 0
    val hasHeight = height != null && height > 0

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    stringResource(if (record == null) R.string.growth_add_title else R.string.growth_edit_title),
                    style = MaterialTheme.typography.titleLarge,
                )

                DatePickerField(
                    label = stringResource(R.string.growth_date_label),
                    selectedDate = date,
                    onDateSelected = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(stringResource(R.string.weight_kg_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(stringResource(R.string.height_cm_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

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
                            enabled = hasWeight || hasHeight,
                            onClick = { onSave(date, weight.takeIf { hasWeight }, height.takeIf { hasHeight }) },
                        ) { Text(stringResource(R.string.action_save)) }
                    }
                }
            }
        }
    }
}
