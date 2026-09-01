package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewerScreen(
    mediaAsset: MediaAsset,
    annotations: List<ImageAnnotation>,
    layers: List<ImageLayer>,
    toolState: DrawToolState,
    canUndo: Boolean,
    canRedo: Boolean,
    isDrawingEnabled: Boolean,
    activeLayerId: String?,
    onBack: () -> Unit,
    onToggleLayers: () -> Unit,
    onToggleDrawing: () -> Unit,
    onTogglePresentation: () -> Unit,
    onToolChange: (DrawTool) -> Unit,
    onColorChange: (Long) -> Unit,
    onStrokeWidthChange: (Float) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit,
    onClearLayer: () -> Unit,
    onStrokeComplete: (ImageAnnotation) -> Unit,
    onLayerTap: (String) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showToolbar by remember { mutableStateOf(true) }
    var showLayersPanel by remember { mutableStateOf(false) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        if (!isDrawingEnabled) {
            scale = (scale * zoomChange).coerceIn(0.5f, 5f)
            offsetX += panChange.x
            offsetY += panChange.y
        }
    }

    Scaffold(
        topBar = {
            if (showToolbar) {
                TopAppBar(
                    title = { Text(text = mediaAsset.title, color = V20Ink, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = V20Ink)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showLayersPanel = !showLayersPanel }) {
                            Icon(Icons.Default.Layers, contentDescription = stringResource(R.string.viewer_layers), tint = V20Ink)
                        }
                        IconButton(onClick = onToggleDrawing) {
                            Icon(
                                if (isDrawingEnabled) Icons.Default.EditOff else Icons.Default.Edit,
                                contentDescription = if (isDrawingEnabled) stringResource(R.string.draw_undo) else stringResource(R.string.draw_tool_pen),
                                tint = if (isDrawingEnabled) V20GreenBright else V20Ink
                            )
                        }
                        IconButton(onClick = onTogglePresentation) {
                            Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.viewer_presentation), tint = V20GreenBright)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = V20Surface2)
                )
            }
        },
        bottomBar = {
            if (showToolbar && isDrawingEnabled) {
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
                    onClearLayer = onClearLayer
                )
            } else if (showToolbar) {
                Surface(color = V20Surface2, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.viewer_dimensions, mediaAsset.width, mediaAsset.height),
                            color = V20InkDim,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${String.format("%.0f", scale * 100)}%",
                            color = V20InkDim,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { if (!isDrawingEnabled) showToolbar = !showToolbar },
                        onDoubleTap = {
                            if (!isDrawingEnabled) {
                                if (scale > 1.5f) {
                                    scale = 1f; offsetX = 0f; offsetY = 0f
                                } else {
                                    scale = 2.5f
                                }
                            }
                        }
                    )
                }
                .then(
                    if (isDrawingEnabled) Modifier else Modifier.transformable(state = transformableState)
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(mediaAsset.originalFilePath))
                    .crossfade(true)
                    .build(),
                contentDescription = mediaAsset.title,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentScale = ContentScale.Fit
            )

            AnnotationCanvas(
                annotations = annotations.filter { layer -> layers.any { it.id == layer.layerId && it.visible } },
                toolState = toolState,
                activeLayerId = activeLayerId,
                isDrawingEnabled = isDrawingEnabled,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                onStrokeComplete = onStrokeComplete,
                onPinTap = { }
            )

            if (showLayersPanel) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(240.dp)
                        .align(Alignment.CenterEnd),
                    color = V20Surface.copy(alpha = 0.95f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.viewer_layers),
                            color = V20GoldBright,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        layers.forEach { layer ->
                            val isActive = layer.id == activeLayerId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(
                                        if (isActive) V20GreenBright.copy(alpha = 0.15f) else Color.Transparent,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .pointerInput(layer.id) {
                                        detectTapGestures(onTap = { onLayerTap(layer.id) })
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    if (layer.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = if (layer.visible) V20GreenBright else V20InkFaint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = layer.name,
                                        color = if (layer.visible) V20Ink else V20InkFaint,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (isActive) {
                                        Text(
                                            text = "● Active",
                                            color = V20GreenBright,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                        if (layers.isEmpty()) {
                            Text(
                                text = stringResource(R.string.media_empty),
                                color = V20InkFaint,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
