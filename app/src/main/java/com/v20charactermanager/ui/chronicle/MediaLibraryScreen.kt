package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.ui.theme.*
import com.v20charactermanager.ui.components.V20BloodButton
import com.v20charactermanager.ui.components.V20ErrorDialog
import com.v20charactermanager.ui.components.V20ErrorType
import com.v20charactermanager.ui.components.V20IvoryButton
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MediaLibraryScreen(
    chronicleId: String,
    assets: List<MediaAsset>,
    availableTags: List<String>,
    selectedTag: String? = null,
    onImportImage: () -> Unit,
    onImportDocument: () -> Unit,
    onImportVideo: () -> Unit,
    onAssetClick: (MediaAsset) -> Unit,
    onAssetDelete: (String) -> Unit,
    onAssetRename: (String, String) -> Unit,
    onAssetTagAdd: (String, String) -> Unit,
    onAssetTagRemove: (String, String) -> Unit,
    onFilterByTag: (String?) -> Unit,
    message: String? = null,
    onClearMessage: () -> Unit = {},
    errorType: V20ErrorType? = null,
    errorDetails: String? = null,
    onClearError: () -> Unit = {},
    onBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<MediaAsset?>(null) }
    var showRenameDialog by remember { mutableStateOf<MediaAsset?>(null) }
    var showTagEditorDialog by remember { mutableStateOf<MediaAsset?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    if (errorType != null) {
        V20ErrorDialog(
            errorType = errorType,
            errorDetails = errorDetails,
            onDismiss = onClearError,
            onRetry = onClearError
        )
    }

    val filteredAssets = if (selectedTag != null) {
        assets.filter { asset -> asset.tags.any { it.equals(selectedTag, ignoreCase = true) } }
    } else {
        assets
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.media_library), color = V20GoldBright, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = V20Ink)
                    }
                },
                actions = {
                    IconButton(onClick = onImportDocument) {
                        Icon(Icons.Default.InsertDriveFile, contentDescription = stringResource(R.string.import_document), tint = V20Ink)
                    }
                    IconButton(onClick = onImportVideo) {
                        Icon(Icons.Default.VideoFile, contentDescription = stringResource(R.string.import_video), tint = V20Ink)
                    }
                    V20IvoryButton(text = stringResource(R.string.media_import), onClick = onImportImage)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = V20Surface2)
            )
        },
        containerColor = V20Black
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tag filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTag == null,
                    onClick = { onFilterByTag(null) },
                    label = { Text(stringResource(R.string.media_all)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = V20Green,
                        selectedLabelColor = V20Black,
                        containerColor = V20Surface,
                        labelColor = V20InkDim
                    )
                )
                availableTags.forEach { tag ->
                    FilterChip(
                        selected = selectedTag == tag,
                        onClick = {
                            onFilterByTag(if (selectedTag == tag) null else tag)
                        },
                        label = { Text(tag) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = V20Gold,
                            selectedLabelColor = V20Black,
                            containerColor = V20Surface,
                            labelColor = V20InkDim
                        )
                    )
                }
            }

            if (filteredAssets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = V20InkFaint, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = stringResource(R.string.media_empty), color = V20InkFaint)
                        Text(text = stringResource(R.string.media_import_prompt), color = V20InkFaint, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAssets) { asset ->
                        MediaAssetCard(
                            asset = asset,
                            onClick = { onAssetClick(asset) },
                            onLongClick = { showDeleteDialog = asset },
                            onTagClick = { tag -> onFilterByTag(if (selectedTag == tag) null else tag) },
                            onEditTags = { showTagEditorDialog = asset },
                            onEditTitle = { showRenameDialog = asset }
                        )
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { asset ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.media_delete_confirm)) },
            text = { Text(asset.title) },
            confirmButton = {
                V20BloodButton(
                    text = stringResource(R.string.action_delete),
                    onClick = { onAssetDelete(asset.id); showDeleteDialog = null }
                )
            },
            dismissButton = {
                V20IvoryButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = { showDeleteDialog = null }
                )
            }
        )
    }

    showRenameDialog?.let { asset ->
        var newName by remember { mutableStateOf(asset.title) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text(stringResource(R.string.media_rename)) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text(stringResource(R.string.title_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                V20BloodButton(
                    text = stringResource(R.string.save),
                    onClick = {
                        if (newName.isNotBlank()) {
                            onAssetRename(asset.id, newName)
                            showRenameDialog = null
                        }
                    }
                )
            },
            dismissButton = {
                V20IvoryButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = { showRenameDialog = null }
                )
            }
        )
    }

    showTagEditorDialog?.let { asset ->
        TagEditorDialog(
            asset = asset,
            standardTags = STANDARD_TAGS,
            onAddTag = { tag -> onAssetTagAdd(asset.id, tag) },
            onRemoveTag = { tag -> onAssetTagRemove(asset.id, tag) },
            onDismiss = { showTagEditorDialog = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaAssetCard(
    asset: MediaAsset,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onTagClick: (String) -> Unit,
    onEditTags: () -> Unit,
    onEditTitle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = V20Surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val displayFile = asset.thumbnailFilePath?.let { File(it) }?.takeIf { it.exists() }
                ?: File(asset.originalFilePath).takeIf { it.exists() }
            if (displayFile != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(displayFile)
                        .crossfade(true)
                        .build(),
                    contentDescription = asset.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    tint = V20InkFaint,
                    modifier = Modifier.align(Alignment.Center).size(48.dp)
                )
            }

            // Tag chips at top
            if (asset.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    asset.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = V20Gold.copy(alpha = 0.85f),
                            modifier = Modifier.clickable { onTagClick(tag) }
                        ) {
                            Text(
                                text = tag,
                                color = V20Black,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Play icon for videos
            if (asset.type == MediaAssetType.VIDEO) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = V20GoldBright,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Bottom overlay with title + actions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = asset.title,
                            color = V20Ink,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = onEditTags,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Label,
                            contentDescription = "Tags",
                            tint = V20Gold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onEditTitle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.action_edit),
                            tint = V20Ink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TagEditorDialog(
    asset: MediaAsset,
    standardTags: List<String>,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var customTagInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.tag_manage))
        },
        text = {
            Column {
                Text(
                    text = asset.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.tag_assigned),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (asset.tags.isEmpty()) {
                    Text(
                        text = stringResource(R.string.tag_none),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        asset.tags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = { onRemoveTag(tag) },
                                label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = "Rimuovi", modifier = Modifier.size(14.dp))
                                },
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.tag_add_standard),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    standardTags.filter { it !in asset.tags }.forEach { tag ->
                        AssistChip(
                            onClick = { onAddTag(tag) },
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.tag_custom),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customTagInput,
                        onValueChange = { customTagInput = it },
                        label = { Text(stringResource(R.string.tag_new_hint)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    FilledTonalButton(
                        onClick = {
                            if (customTagInput.isNotBlank()) {
                                onAddTag(customTagInput.trim())
                                customTagInput = ""
                            }
                        },
                        enabled = customTagInput.isNotBlank()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        confirmButton = {
            V20IvoryButton(
                text = stringResource(R.string.action_close),
                onClick = onDismiss
            )
        },
        dismissButton = null
    )
}
