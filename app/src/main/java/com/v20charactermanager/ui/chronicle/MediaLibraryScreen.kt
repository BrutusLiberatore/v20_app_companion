package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaLibraryScreen(
    chronicleId: String,
    assets: List<MediaAsset>,
    onImportImage: () -> Unit,
    onImportDocument: () -> Unit,
    onAssetClick: (MediaAsset) -> Unit,
    onAssetDelete: (String) -> Unit,
    onAssetRename: (String, String) -> Unit,
    message: String? = null,
    onClearMessage: () -> Unit = {},
    errorType: V20ErrorType? = null,
    errorDetails: String? = null,
    onClearError: () -> Unit = {},
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(MediaAssetCategory.ALL) }
    var showDeleteDialog by remember { mutableStateOf<MediaAsset?>(null) }
    var showRenameDialog by remember { mutableStateOf<MediaAsset?>(null) }
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

    val filteredAssets = when (selectedCategory) {
        MediaAssetCategory.ALL -> assets
        MediaAssetCategory.MAPS -> assets.filter { it.type == MediaAssetType.MAP || it.type == MediaAssetType.LOCATION_MAP }
        MediaAssetCategory.NPC -> assets.filter { it.type == MediaAssetType.PORTRAIT }
        MediaAssetCategory.LOCATIONS -> assets.filter { it.type == MediaAssetType.LOCATION_MAP }
        MediaAssetCategory.CLUES -> assets.filter { it.type == MediaAssetType.CLUE_VISUAL }
        MediaAssetCategory.DOCUMENTS -> assets.filter { it.type == MediaAssetType.DOCUMENT }
        MediaAssetCategory.OTHER -> assets.filter { it.type == MediaAssetType.OTHER || it.type == MediaAssetType.DIAGRAM || it.type == MediaAssetType.SYMBOL || it.type == MediaAssetType.PHOTO }
    }

    val categories = listOf(
        MediaAssetCategory.ALL to stringResource(R.string.media_all),
        MediaAssetCategory.MAPS to stringResource(R.string.media_maps),
        MediaAssetCategory.NPC to stringResource(R.string.media_npc),
        MediaAssetCategory.LOCATIONS to stringResource(R.string.media_locations),
        MediaAssetCategory.CLUES to stringResource(R.string.media_clues),
        MediaAssetCategory.DOCUMENTS to stringResource(R.string.media_documents),
        MediaAssetCategory.OTHER to stringResource(R.string.media_other)
    )

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
                    V20IvoryButton(text = stringResource(R.string.media_import), onClick = onImportImage)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = V20Surface2)
            )
        },
        containerColor = V20Black
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Category filter chips
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.size) { index ->
                    val (cat, label) = categories[index]
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = V20Green,
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .combinedClickable(
                                    onClick = { onAssetClick(asset) },
                                    onLongClick = { showDeleteDialog = asset }
                                ),
                            colors = CardDefaults.cardColors(containerColor = V20Surface),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                val file = File(asset.originalFilePath)
                                if (file.exists()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(file)
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
                                // Overlay with title + rename button
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .padding(8.dp)
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
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = asset.type.name,
                                                color = V20InkDim,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        IconButton(
                                            onClick = { showRenameDialog = asset },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = stringResource(R.string.action_edit),
                                                tint = V20Ink,
                                                modifier = Modifier.size(18.dp)
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
}
