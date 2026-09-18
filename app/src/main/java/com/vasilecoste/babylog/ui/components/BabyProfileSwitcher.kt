package com.vasilecoste.babylog.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import java.time.LocalDate

@Composable
fun BabyProfileSwitcherDialog(
    babies: List<BabyProfile>,
    selectedBabyId: Long?,
    onSelect: (Long) -> Unit,
    onAddNew: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.switch_baby_title)) },
        text = {
            Column {
                babies.forEach { baby ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = baby.id == selectedBabyId,
                                onClick = {
                                    onSelect(baby.id)
                                    onDismiss()
                                },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = baby.id == selectedBabyId, onClick = null)
                        Text(baby.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onAddNew) { Text(stringResource(R.string.action_add_baby)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
        },
    )
}

@Composable
fun AddEditBabyDialog(
    baby: BabyProfile? = null,
    onConfirm: (name: String, birthDate: LocalDate?, gender: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(baby?.name ?: "") }
    var birthDate by remember { mutableStateOf<LocalDate?>(baby?.birthDate) }
    var gender by remember { mutableStateOf<String?>(baby?.gender) }

    val genderOptions = listOf(
        null to stringResource(R.string.gender_unspecified),
        "male" to stringResource(R.string.gender_male),
        "female" to stringResource(R.string.gender_female)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (baby == null) R.string.add_baby_profile_title else R.string.edit_baby_profile_title
                )
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.baby_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                DatePickerField(
                    label = stringResource(R.string.birth_date_optional_label),
                    selectedDate = birthDate,
                    onDateSelected = { birthDate = it },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.gender_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )

                genderOptions.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = gender == value,
                                onClick = { gender = value }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = gender == value,
                            onClick = { gender = value }
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), birthDate, gender) },
                enabled = name.isNotBlank(),
            ) { Text(stringResource(if (baby == null) R.string.action_add else R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}
