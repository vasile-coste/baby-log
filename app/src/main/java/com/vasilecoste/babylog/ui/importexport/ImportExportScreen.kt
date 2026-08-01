package com.vasilecoste.babylog.ui.importexport

import android.content.ClipData
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.model.ImportedBabyData
import com.vasilecoste.babylog.data.repository.ImportMode
import com.vasilecoste.babylog.ui.components.SimpleTopBar
import java.io.File
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
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()

    var status by remember { mutableStateOf<StatusMessage?>(null) }
    var selectedBaby by remember { mutableStateOf<BabyProfile?>(null) }
    var pendingImport by remember { mutableStateOf<PendingImport?>(null) }
    if (selectedBaby == null && babies.isNotEmpty()) {
        selectedBaby = babies.first()
    }

    fun runImport(data: ImportedBabyData, existingBabyId: Long?, mode: ImportMode) {
        viewModel.importData(data, existingBabyId, mode)
    }

    LaunchedEffect(viewModel) {
        viewModel.importResult.collect { result ->
            result.fold(
                onSuccess = { (name, count) ->
                    val entriesLabel = resources.getQuantityString(R.plurals.plural_entries, count, count)
                    Toast.makeText(context, resources.getString(R.string.import_success, name, entriesLabel), Toast.LENGTH_LONG).show()
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
                        Toast.makeText(context, resources.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header Section
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.ImportExport,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.import_export_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Import Card
            SectionCard(
                icon = Icons.Outlined.Download,
                title = stringResource(R.string.import_section_title),
                description = stringResource(R.string.import_description)
            ) {
                Button(
                    onClick = { importLauncher.launch(arrayOf("*/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Download, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.action_choose_file))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Export Card
            SectionCard(
                icon = Icons.Outlined.Upload,
                title = stringResource(R.string.export_section_title),
                description = stringResource(R.string.export_description)
            ) {
                if (babies.isEmpty()) {
                    Text(
                        stringResource(R.string.export_no_baby),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    BabyRadioList(
                        label = stringResource(R.string.export_choose_baby_label),
                        babies = babies,
                        selected = selectedBaby,
                        onSelect = { selectedBaby = it },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val suggestedName = "babylog-${selectedBaby?.name.orEmpty()}-export.json"
                                exportLauncher.launch(suggestedName)
                            },
                        ) {
                            Icon(Icons.Outlined.Upload, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.action_export))
                        }

                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val babyId = selectedBaby?.id ?: return@TextButton
                                scope.launch {
                                    viewModel.exportJson(babyId).fold(
                                        onSuccess = { json ->
                                            shareJson(context, selectedBaby?.name.orEmpty(), json)
                                        },
                                        onFailure = { e ->
                                            status = StatusMessage.ExportError(e.message ?: e.toString())
                                        }
                                    )
                                }
                            },
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.action_share))
                        }
                    }
                }
            }

            status?.let {
                Spacer(modifier = Modifier.height(24.dp))
                StatusText(it)
            }
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
private fun SectionCard(
    icon: ImageVector,
    title: String,
    description: String,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content()
        }
    }
}

@Composable
private fun StatusText(status: StatusMessage, modifier: Modifier = Modifier) {
    val text = when (status) {
        is StatusMessage.ImportError -> stringResource(R.string.import_error, status.reason)
        StatusMessage.ImportReadError -> stringResource(R.string.import_error, stringResource(R.string.error_could_not_read_file))
        is StatusMessage.ExportError -> stringResource(R.string.export_error, status.reason)
        StatusMessage.ExportWriteError -> stringResource(R.string.export_error, stringResource(R.string.error_could_not_write_file))
    }
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(text, color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }
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

private fun shareJson(context: android.content.Context, babyName: String, json: String) {
    try {
        val file = File(context.cacheDir, "babylog-$babyName-export.json")
        file.writeText(json)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newRawUri("", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, context.getString(R.string.action_share))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error sharing: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
