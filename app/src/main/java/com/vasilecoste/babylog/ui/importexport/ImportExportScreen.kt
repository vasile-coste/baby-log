package com.vasilecoste.babylog.ui.importexport

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.ui.components.SimpleTopBar
import kotlinx.coroutines.launch

private sealed class StatusMessage {
    data class ImportSuccess(val babyName: String, val entryCount: Int) : StatusMessage()
    data class ImportError(val reason: String) : StatusMessage()
    object ImportReadError : StatusMessage()
    object ExportSuccess : StatusMessage()
    data class ExportError(val reason: String) : StatusMessage()
    object ExportWriteError : StatusMessage()
}

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
    if (selectedBaby == null && babies.isNotEmpty()) {
        selectedBaby = babies.first()
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
            viewModel.importJson(text).fold(
                onSuccess = { (name, count) -> status = StatusMessage.ImportSuccess(name, count) },
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
                    status = if (written) StatusMessage.ExportSuccess else StatusMessage.ExportWriteError
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
                BabyDropdown(
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
}

@Composable
private fun StatusText(status: StatusMessage) {
    val text = when (status) {
        is StatusMessage.ImportSuccess -> stringResource(
            R.string.import_success,
            status.babyName,
            pluralStringResource(R.plurals.plural_entries, status.entryCount, status.entryCount),
        )
        is StatusMessage.ImportError -> stringResource(R.string.import_error, status.reason)
        StatusMessage.ImportReadError -> stringResource(R.string.import_error, stringResource(R.string.error_could_not_read_file))
        StatusMessage.ExportSuccess -> stringResource(R.string.export_success)
        is StatusMessage.ExportError -> stringResource(R.string.export_error, status.reason)
        StatusMessage.ExportWriteError -> stringResource(R.string.export_error, stringResource(R.string.error_could_not_write_file))
    }
    Text(text)
}

@Composable
private fun BabyDropdown(
    label: String,
    babies: List<BabyProfile>,
    selected: BabyProfile?,
    onSelect: (BabyProfile) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Row {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.name ?: label)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                babies.forEach { baby ->
                    DropdownMenuItem(
                        text = { Text(baby.name) },
                        onClick = {
                            onSelect(baby)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
