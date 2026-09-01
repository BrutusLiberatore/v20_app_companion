package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
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
    onBack: () -> Unit,
    onToggleLayers: () -> Unit,
    onTogglePresentation: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showToolbar by remember { mutableStateOf(true) }
    var showLayersPanel by remember { mutableStateOf(false) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
        offsetX += panChange.x
        offsetY += panChange.y
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
                        IconButton(onClick = onTogglePresentation) {
                            Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.viewer_presentation), tint = V20GreenBright)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = V20Surface2)
                )
            }
        },
        bottomBar = {
            if (showToolbar) {
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
                        onTap = { showToolbar = !showToolbar },
                        onDoubleTap = {
                            if (scale > 1.5f) {
                                scale = 1f; offsetX = 0f; offsetY = 0f
                            } else {
                                scale = 2.5f
                            }
                        }
                    )
                },
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
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    if (layer.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = if (layer.visible) V20GreenBright else V20InkFaint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = layer.name,
                                    color = if (layer.visible) V20Ink else V20InkFaint,
                                    style = MaterialTheme.typography.bodyMedium
                                )
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
