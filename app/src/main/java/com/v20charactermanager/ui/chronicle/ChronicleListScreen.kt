package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.Chronicle
import com.v20charactermanager.domain.model.ChronicleUserRole
import com.v20charactermanager.ui.components.V20BloodButton
import com.v20charactermanager.ui.components.V20GothicFab
import com.v20charactermanager.ui.components.V20IvoryButton
import com.v20charactermanager.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronicleListScreen(
    uiState: ChronicleListUiState,
    onChronicleClick: (String) -> Unit,
    onCreateChronicle: (String, String, String, ChronicleUserRole) -> Unit,
    onDeleteChronicle: (String) -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.chronicle_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = V20Green,
                    titleContentColor = V20Ink,
                    navigationIconContentColor = V20Ink
                )
            )
        },
        floatingActionButton = {
            V20GothicFab(
                icon = Icons.Default.Add,
                contentDescription = stringResource(R.string.chronicle_create),
                onClick = { showCreateDialog = true }
            )
        }
    ) { padding ->
        if (uiState.chronicles.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = V20GreenBright
                    )
                    Text(
                        text = stringResource(R.string.chronicle_empty),
                        style = MaterialTheme.typography.headlineSmall,
                        color = V20InkDim
                    )
                    V20BloodButton(
                        text = stringResource(R.string.chronicle_create_first),
                        onClick = { showCreateDialog = true }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.chronicles) { chronicle ->
                    ChronicleCard(
                        chronicle = chronicle,
                        onClick = { onChronicleClick(chronicle.id) },
                        onDelete = { onDeleteChronicle(chronicle.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateChronicleDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description, storytellerName, role ->
                onCreateChronicle(name, description, storytellerName, role)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun ChronicleCard(
    chronicle: Chronicle,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = V20Surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chronicle.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = V20Ink
                )
                if (chronicle.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chronicle.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = V20InkDim,
                        maxLines = 2
                    )
                }
                if (chronicle.storytellerName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${stringResource(R.string.chronicle_storyteller)}: ${chronicle.storytellerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = V20InkFaint
                    )
                }
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = V20Error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.chronicle_delete_title)) },
            text = { Text(stringResource(R.string.chronicle_delete_message, chronicle.name)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.action_delete), color = V20Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun CreateChronicleDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, ChronicleUserRole) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var storytellerName by remember { mutableStateOf("") }
    var isStoryteller by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.chronicle_create)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.chronicle_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.chronicle_description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = storytellerName,
                    onValueChange = { storytellerName = it },
                    label = { Text(stringResource(R.string.chronicle_storyteller)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isStoryteller,
                        onCheckedChange = { isStoryteller = it }
                    )
                    Text(stringResource(R.string.chronicle_storyteller_role))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(
                            name,
                            description,
                            storytellerName,
                            if (isStoryteller) ChronicleUserRole.STORYTELLER else ChronicleUserRole.PLAYER
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.action_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
