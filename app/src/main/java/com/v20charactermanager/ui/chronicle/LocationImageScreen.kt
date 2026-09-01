package com.v20charactermanager.ui.chronicle

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationImageScreen(
    location: ChronicleLocation,
    linkedAsset: MediaAsset?,
    document: ImageDocument?,
    layers: List<ImageLayer>,
    annotations: List<ImageAnnotation>,
    toolState: DrawToolState,
    canUndo: Boolean,
    canRedo: Boolean,
    isDrawingEnabled: Boolean,
    activeLayerId: String?,
    onBack: () -> Unit,
    onImportImage: (Uri) -> Unit,
    onToggleDrawing: () -> Unit,
    onToolChange: (DrawTool) -> Unit,
    onColorChange: (Long) -> Unit,
    onStrokeWidthChange: (Float) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit,
    onClearLayer: () -> Unit,
    onStrokeComplete: (ImageAnnotation) -> Unit,
    onLayerTap: (String) -> Unit,
    onDeleteImage: () -> Unit
) {
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImportImage(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(location.name, fontWeight = FontWeight.Bold)
                        Text(
                            stringResource(R.string.location_image_subtitle),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (linkedAsset != null) {
                        IconButton(onClick = onToggleDrawing) {
                            Icon(
                                if (isDrawingEnabled) Icons.Filled.Close else Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.action_edit)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isDrawingEnabled && linkedAsset != null) {
                AnnotationToolbar(
                    toolState = toolState,
                    canUndo = canUndo,
                    canRedo = canRedo,
                    activeLayerId = activeLayerId,
                    onToolChange = onToolChange,
                    onColorChange = onColorChange,
                    onStrokeWidthChange = onStrokeWidthChange,
                    onUndo = onUndo,
                    onRedo = onRedo,
                    onSave = onSave,
                    onClearLayer = onClearLayer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        if (linkedAsset == null) {
            EmptyLocationImage(
                onImportFromGallery = { pickImageLauncher.launch("image/*") },
                modifier = Modifier.padding(padding)
            )
        } else {
            LocationImageViewer(
                asset = linkedAsset,
                document = document,
                layers = layers,
                annotations = annotations,
                toolState = toolState,
                isDrawingEnabled = isDrawingEnabled,
                activeLayerId = activeLayerId,
                onStrokeComplete = onStrokeComplete,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun EmptyLocationImage(
    onImportFromGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Image,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.location_no_image),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onImportFromGallery) {
            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.location_import_image))
        }
    }
}

@Composable
private fun LocationImageViewer(
    asset: MediaAsset,
    document: ImageDocument?,
    layers: List<ImageLayer>,
    annotations: List<ImageAnnotation>,
    toolState: DrawToolState,
    isDrawingEnabled: Boolean,
    activeLayerId: String?,
    onStrokeComplete: (ImageAnnotation) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(File(asset.originalFilePath))
                .crossfade(true)
                .build(),
            contentDescription = asset.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        if (isDrawingEnabled) {
            val visibleAnnotations = annotations.filter { ann ->
                layers.any { it.id == ann.layerId && it.visible }
            }
            AnnotationCanvas(
                annotations = visibleAnnotations,
                toolState = toolState,
                activeLayerId = activeLayerId,
                isDrawingEnabled = isDrawingEnabled,
                onStrokeComplete = onStrokeComplete,
                onPinTap = { offset ->
                    val annotation = ImageAnnotation(
                        id = java.util.UUID.randomUUID().toString(),
                        layerId = activeLayerId ?: "",
                        imageDocumentId = document?.id ?: "",
                        type = AnnotationType.PIN,
                        geometry = AnnotationGeometry(
                            position = NormalizedPoint(offset.x / 1000f, offset.y / 1000f)
                        ),
                        style = AnnotationStyle(
                            strokeColor = toolState.color.toLong(),
                            strokeWidth = toolState.strokeWidth
                        ),
                        text = "Pin"
                    )
                    onStrokeComplete(annotation)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
