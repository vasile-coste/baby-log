package com.vasilecoste.babylog.ui.importexport

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.model.ImportedBabyData
import com.vasilecoste.babylog.data.repository.ImportMode
import com.vasilecoste.babylog.ui.components.SimpleTopBar
import kotlinx.coroutines.launch

private sealed class StatusMessage {
    data class ImportError(val reason: String) : StatusMessage()
    object ImportReadError : StatusMessage()
    data class ExportError(val reason: String) : StatusMessage()
    object ExportWriteError : StatusMessage()
}

private data class PendingImport(val data: ImportedBabyData, val existingBaby: BabyProfile)

@Composable
fun ImportExportScreen(
    onMenuClick: () -> Unit,
    viewModel: ImportExportViewModel = viewModel(factory = ImportExportViewModel.Factory),
) {
    val babies by viewModel.babies.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var status by remember { mutableStateOf<StatusMessage?>(null) }
    var selectedBaby by remember { mutableStateOf<BabyProfile?>(null) }
    var pendingImport by remember { mutableStateOf<PendingImport?>(null) }
    if (selectedBaby == null && babies.isNotEmpty()) {
        selectedBaby = babies.first()
    }

    fun runImport(data: ImportedBabyData, existingBabyId: Long?, mode: ImportMode) {
        scope.launch {
            viewModel.importData(data, existingBabyId, mode).fold(
                onSuccess = { (name, count) ->
                    val entriesLabel = context.resources.getQuantityString(R.plurals.plural_entries, count, count)
                    Toast.makeText(context, context.getString(R.string.import_success, name, entriesLabel), Toast.LENGTH_LONG).show()
                },
                onFailure = { e -> status = StatusMessage.ImportError(e.message ?: e.toString()) },
            )
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val text = runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }.getOrNull()
            if (text == null) {
                status = StatusMessage.ImportReadError
                return@launch
            }
            viewModel.parseImport(text).fold(
                onSuccess = { data ->
                    val existing = babies.find { it.name == data.babyName }
                    if (existing != null) {
                        pendingImport = PendingImport(data, existing)
                    } else {
                        runImport(data, existingBabyId = null, mode = ImportMode.MERGE)
                    }
                },
                onFailure = { e -> status = StatusMessage.ImportError(e.message ?: e.toString()) },
            )
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        val babyId = selectedBaby?.id
        if (uri == null || babyId == null) return@rememberLauncherForActivityResult
        scope.launch {
            viewModel.exportJson(babyId).fold(
                onSuccess = { json ->
                    val written = runCatching {
                        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(json) }
                    }.isSuccess
                    if (written) {
                        Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
                    } else {
                        status = StatusMessage.ExportWriteError
                    }
                },
                onFailure = { e -> status = StatusMessage.ExportError(e.message ?: e.toString()) },
            )
        }
    }

    Scaffold(topBar = { SimpleTopBar(title = stringResource(R.string.import_export_title), onMenuClick = onMenuClick) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.import_section_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.import_description))
            Button(onClick = { importLauncher.launch(arrayOf("*/*")) }) {
                Text(stringResource(R.string.action_choose_file))
            }

            HorizontalDivider()

            Text(stringResource(R.string.export_section_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.export_description))

            if (babies.isEmpty()) {
                Text(stringResource(R.string.export_no_baby))
            } else {
                BabyRadioList(
                    label = stringResource(R.string.export_choose_baby_label),
                    babies = babies,
                    selected = selectedBaby,
                    onSelect = { selectedBaby = it },
                )
                Button(
                    onClick = {
                        val suggestedName = "babylog-${selectedBaby?.name.orEmpty()}-export.json"
                        exportLauncher.launch(suggestedName)
                    },
                ) {
                    Text(stringResource(R.string.action_export))
                }
            }

            status?.let { StatusText(it) }
        }
    }

    pendingImport?.let { pending ->
        ReplaceOrMergeDialog(
            babyName = pending.existingBaby.name,
            onReplace = {
                pendingImport = null
                runImport(pending.data, existingBabyId = pending.existingBaby.id, mode = ImportMode.REPLACE)
            },
            onMerge = {
                pendingImport = null
                runImport(pending.data, existingBabyId = pending.existingBaby.id, mode = ImportMode.MERGE)
            },
            onCancel = { pendingImport = null },
        )
    }
}

@Composable
private fun StatusText(status: StatusMessage) {
    val text = when (status) {
        is StatusMessage.ImportError -> stringResource(R.string.import_error, status.reason)
        StatusMessage.ImportReadError -> stringResource(R.string.import_error, stringResource(R.string.error_could_not_read_file))
        is StatusMessage.ExportError -> stringResource(R.string.export_error, status.reason)
        StatusMessage.ExportWriteError -> stringResource(R.string.export_error, stringResource(R.string.error_could_not_write_file))
    }
    Text(text)
}

@Composable
private fun ReplaceOrMergeDialog(
    babyName: String,
    onReplace: () -> Unit,
    onMerge: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.import_existing_baby_title)) },
        text = { Text(stringResource(R.string.import_existing_baby_message, babyName)) },
        confirmButton = {
            TextButton(onClick = onMerge) { Text(stringResource(R.string.action_insert_update)) }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onCancel) { Text(stringResource(R.string.action_cancel)) }
                TextButton(onClick = onReplace) { Text(stringResource(R.string.action_replace)) }
            }
        },
    )
}

@Composable
private fun BabyRadioList(
    label: String,
    babies: List<BabyProfile>,
    selected: BabyProfile?,
    onSelect: (BabyProfile) -> Unit,
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        babies.forEach { baby ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = baby.id == selected?.id, onClick = { onSelect(baby) }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = baby.id == selected?.id, onClick = null)
                Text(baby.name, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
