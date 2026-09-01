package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.ImageRevision
import com.v20charactermanager.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionHistoryScreen(
    revisions: List<ImageRevision>,
    onRestore: (ImageRevision) -> Unit,
    onBack: () -> Unit
) {
    var showRestoreDialog by remember { mutableStateOf<ImageRevision?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.version_history),
                        color = V20Ink,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = V20Ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = V20Surface2)
            )
        },
        containerColor = V20Surface
    ) { padding ->
        if (revisions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = V20InkFaint,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.version_no_revisions),
                        color = V20InkFaint,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(revisions.sortedByDescending { it.revisionNumber }) { revision ->
                    RevisionCard(
                        revision = revision,
                        dateFormat = dateFormat,
                        onRestore = { showRestoreDialog = revision }
                    )
                }
            }
        }

        showRestoreDialog?.let { revision ->
            AlertDialog(
                onDismissRequest = { showRestoreDialog = null },
                title = { Text(stringResource(R.string.version_restore), color = V20Ink) },
                text = { Text(stringResource(R.string.version_restore_confirm), color = V20InkDim) },
                confirmButton = {
                    TextButton(onClick = {
                        onRestore(revision)
                        showRestoreDialog = null
                    }) {
                        Text(stringResource(R.string.version_restore), color = V20GreenBright)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestoreDialog = null }) {
                        Text(stringResource(R.string.action_cancel), color = V20InkDim)
                    }
                },
                containerColor = V20Surface2
            )
        }
    }
}

@Composable
private fun RevisionCard(
    revision: ImageRevision,
    dateFormat: SimpleDateFormat,
    onRestore: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = V20Surface2,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Commit,
                contentDescription = null,
                tint = V20GoldBright,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.version_revision, revision.revisionNumber),
                    color = V20Ink,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                revision.description?.let { desc ->
                    Text(
                        text = desc,
                        color = V20InkDim,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = dateFormat.format(Date(revision.createdAt)),
                    color = V20InkFaint,
                    style = MaterialTheme.typography.labelSmall
                )
                revision.sessionId?.let { sessionId ->
                    Text(
                        text = stringResource(R.string.version_session_link, sessionId.take(8)),
                        color = V20InkFaint,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(V20GreenBright.copy(alpha = 0.15f))
                    .clickable { onRestore() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.version_restore),
                    color = V20GreenBright,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
