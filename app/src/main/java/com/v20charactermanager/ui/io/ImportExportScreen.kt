package com.v20charactermanager.ui.io

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.ui.components.V20ErrorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportScreen(
    uiState: ImportExportUiState,
    onBack: () -> Unit,
    onImportUri: (android.net.Uri) -> Unit,
    onExportCharacter: (Character, android.net.Uri) -> Unit,
    onShareCharacter: (Character) -> Unit,
    onSaveAsCopy: () -> Unit,
    onReplaceExisting: () -> Unit,
    onResetState: () -> Unit,
    onImportEquipmentLibrary: (android.net.Uri) -> Unit,
    onImportEquipmentToCharacter: (String) -> Unit,
    onExportEquipmentLibrary: (List<com.v20charactermanager.domain.model.EquipmentItem>, String, android.net.Uri) -> Unit
) {
    val context = LocalContext.current
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                onImportUri(uri)
            }
        }
    }

    val fileSaverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedCharacter?.let { character ->
                    onExportCharacter(character, uri)
                }
            }
        }
    }

    val equipmentLibraryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                onImportEquipmentLibrary(uri)
            }
        }
    }

    val equipmentLibrarySaverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val state = uiState.operationState
                if (state is IoOperationState.EquipmentLibraryImported) {
                    onExportEquipmentLibrary(state.items, state.libraryName, uri)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_import_export),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.import_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.import_v20_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "application/json"
                            }
                            filePickerLauncher.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.FileUpload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.import_title))
                    }
                }
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.export_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.export_select_character),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (uiState.characters.isEmpty()) {
                        Text(
                            text = stringResource(R.string.export_no_characters),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        uiState.characters.forEach { character ->
                            val unnamed = stringResource(R.string.character_unnamed)
                            val fileFormat = stringResource(R.string.export_file_format)
                            val exportFileName = fileFormat.format(character.identity.name.ifEmpty { unnamed })
                            CharacterExportCard(
                                character = character,
                                onExport = {
                                    selectedCharacter = character
                                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "application/json"
                                        putExtra(Intent.EXTRA_TITLE, exportFileName)
                                    }
                                    fileSaverLauncher.launch(intent)
                                },
                                onShare = {
                                    onShareCharacter(character)
                                }
                            )
                        }
                    }
                }
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.equipment_library_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.equipment_library_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "application/json"
                            }
                            equipmentLibraryPickerLauncher.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.FileUpload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.equipment_library_import))
                    }
                }
            }
        }
    }

    when (val state = uiState.operationState) {
        is IoOperationState.Loading -> {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(R.string.equipment_library_processing)) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text(stringResource(R.string.equipment_library_please_wait))
                    }
                },
                confirmButton = { }
            )
        }
        is IoOperationState.Success -> {
            AlertDialog(
                onDismissRequest = onResetState,
                title = { Text(stringResource(R.string.equipment_library_success)) },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = onResetState) {
                        Text(stringResource(R.string.action_close))
                    }
                }
            )
        }
        is IoOperationState.Error -> {
            V20ErrorDialog(
                errorType = state.errorType,
                customMessage = state.message,
                errorDetails = state.details,
                onDismiss = onResetState,
                onRetry = onResetState
            )
        }
        is IoOperationState.DuplicateDetected -> {
            AlertDialog(
                onDismissRequest = onResetState,
                title = { Text(stringResource(R.string.import_duplicate)) },
                text = {
                    Text(
                        stringResource(R.string.import_duplicate_message, state.existingCharacterName)
                    )
                },
                confirmButton = {
                    TextButton(onClick = onReplaceExisting) {
                        Text(stringResource(R.string.import_replace))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onSaveAsCopy) {
                        Text(stringResource(R.string.import_create_copy))
                    }
                }
            )
        }
        is IoOperationState.EquipmentLibraryImported -> {
            AlertDialog(
                onDismissRequest = onResetState,
                title = { Text(stringResource(R.string.equipment_library_loaded)) },
                text = {
                    Column {
                        Text(stringResource(R.string.equipment_library_found_items, state.items.size, state.libraryName))
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            items(state.items) { item ->
                                Text(
                                    text = "${item.name} (${item.category.name})",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.characters.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.equipment_library_select_character),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    if (uiState.characters.isNotEmpty()) {
                        var selectedChar by remember { mutableStateOf(uiState.characters.firstOrNull()) }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = onResetState) {
                                Text(stringResource(R.string.action_cancel))
                            }
                            TextButton(onClick = {
                                selectedChar?.let { onImportEquipmentToCharacter(it.id) }
                            }) {
                                Text(stringResource(R.string.equipment_library_import_to, selectedChar?.identity?.name?.ifEmpty { stringResource(R.string.character_unnamed) } ?: stringResource(R.string.equipment_library_import_none)))
                            }
                        }
                    } else {
                        TextButton(onClick = onResetState) {
                            Text(stringResource(R.string.action_close))
                        }
                    }
                }
            )
        }
        is IoOperationState.Idle -> { }
    }
}

@Composable
fun CharacterExportCard(
    character: Character,
    onExport: () -> Unit,
    onShare: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.identity.name.ifEmpty { stringResource(R.string.character_unnamed) },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.import_clan_gen_format, character.identity.clan.nameEn, character.identity.generation),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onShare) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = stringResource(R.string.share_title),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onExport) {
                    Icon(
                        Icons.Default.FileDownload,
                        contentDescription = stringResource(R.string.export_title),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
