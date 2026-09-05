package com.v20charactermanager.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.util.CrashHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashLogScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var logs by remember {
        mutableStateOf(
            try { CrashHandler.getCrashLogs(context) }
            catch (_: Exception) { emptyList() }
        )
    }
    var expandedIndex by remember { mutableIntStateOf(-1) }
    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.crash_logs)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (logs.isNotEmpty()) {
                        IconButton(onClick = { showClearConfirm = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = stringResource(R.string.crash_clear_all))
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (logs.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.crash_no_logs), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.crash_no_logs_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.crash_count, logs.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(logs.size) { index ->
                        val (date, log) = logs[index]
                        val isExpanded = expandedIndex == index
                        val isError = try { log.contains("Exception") || log.contains("Error") } catch (_: Exception) { false }
                        val firstLine = try {
                            log.lineSequence().firstOrNull { it.contains("Exception") || it.contains("Error") }
                                ?: log.lineSequence().firstOrNull() ?: ""
                        } catch (_: Exception) { "" }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isError)
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(date, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Row {
                                        IconButton(
                                            onClick = {
                                                try {
                                                    val clip = ClipData.newPlainText("Crash Log", log)
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, context.getString(R.string.crash_copied), Toast.LENGTH_SHORT).show()
                                                } catch (_: Exception) {}
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.crash_copy), modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = { expandedIndex = if (isExpanded) -1 else index },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = stringResource(R.string.crash_expand),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                if (!isExpanded) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = firstLine,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.small,
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = try { log.take(10000) } catch (_: Exception) { "Log unreadable" },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .heightIn(max = 300.dp)
                                                .verticalScroll(rememberScrollState()),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(stringResource(R.string.crash_clear_all)) },
            text = { Text(stringResource(R.string.crash_clear_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    try { CrashHandler.clearCrashLogs(context) } catch (_: Exception) {}
                    logs = emptyList()
                    showClearConfirm = false
                }) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}
