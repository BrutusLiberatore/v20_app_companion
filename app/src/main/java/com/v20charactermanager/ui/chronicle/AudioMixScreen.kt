package com.v20charactermanager.ui.chronicle

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.v20charactermanager.domain.model.AudioTrack
import com.v20charactermanager.domain.model.AudioTrackCategory
import com.v20charactermanager.ui.theme.*

@Composable
fun AudioMixContent(
    chronicleId: String,
    audioViewModel: AudioViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by audioViewModel.uiState.collectAsState()
    var showCategoryDialog by remember { mutableStateOf(false) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var pendingTitle by remember { mutableStateOf("") }

    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pendingUri = it
            pendingTitle = it.lastPathSegment?.substringAfterLast('/') ?: "Audio"
            showCategoryDialog = true
        }
    }

    if (showCategoryDialog && pendingUri != null) {
        CategoryPickerDialog(
            onDismiss = { showCategoryDialog = false; pendingUri = null },
            onConfirm = { category ->
                audioViewModel.importAudio(chronicleId, pendingUri!!, pendingTitle, category)
                showCategoryDialog = false
                pendingUri = null
            }
        )
    }

    if (uiState.tracks.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = V20InkFaint,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nessun audio importato",
                    color = V20InkFaint,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Torna alla tab Visual per importare file audio",
                    color = V20InkFaint,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { audioLauncher.launch("audio/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = V20GoldBright)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = V20Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importa Audio", color = V20Black)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.tracks.size} tracce",
                        color = V20InkFaint,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row {
                        Button(
                            onClick = { audioLauncher.launch("audio/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = V20GoldBright),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = V20Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Importa", color = V20Black, style = MaterialTheme.typography.labelMedium)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { audioViewModel.stopAll() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop All", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
            items(uiState.tracks, key = { it.id }) { track ->
                AudioTrackCard(
                    track = track,
                    onTogglePlay = { audioViewModel.togglePlay(track.id) },
                    onStop = { audioViewModel.stopTrack(track.id) },
                    onVolumeChange = { vol -> audioViewModel.setVolume(track.id, vol) },
                    onLoopToggle = { looping -> audioViewModel.setLooping(track.id, looping) },
                    onDelete = { audioViewModel.deleteTrack(track.id, chronicleId) }
                )
            }
        }
    }
}

@Composable
private fun AudioTrackCard(
    track: AudioTrack,
    onTogglePlay: () -> Unit,
    onStop: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onLoopToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (track.isActive) V20Surface2 else V20Surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(onClick = onTogglePlay, modifier = Modifier.size(40.dp)) {
                        Icon(
                            if (track.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (track.isActive) "Pausa" else "Riproduci",
                            tint = if (track.isActive) V20GoldBright else V20Ink,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = track.title,
                            color = V20Ink,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = when (track.category) {
                                AudioTrackCategory.AMBIENCE -> "Ambience"
                                AudioTrackCategory.MUSIC -> "Musica"
                                AudioTrackCategory.SFX -> "SFX"
                                AudioTrackCategory.CUSTOM -> "Custom"
                            },
                            color = V20InkFaint,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Row {
                    IconButton(onClick = { onLoopToggle(!track.isLooping) }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = "Loop",
                            tint = if (track.isLooping) V20GoldBright else V20InkFaint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onStop, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Elimina",
                            tint = V20InkFaint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.VolumeDown,
                    contentDescription = null,
                    tint = V20InkFaint,
                    modifier = Modifier.size(20.dp)
                )
                Slider(
                    value = track.volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = V20GoldBright,
                        activeTrackColor = V20GoldBright,
                        inactiveTrackColor = V20InkFaint
                    )
                )
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = V20InkFaint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${(track.volume * 100).toInt()}%",
                    color = V20InkFaint,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(32.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryPickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (AudioTrackCategory) -> Unit
) {
    var selected by remember { mutableStateOf(AudioTrackCategory.CUSTOM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Categoria Audio") },
        text = {
            Column {
                Text(
                    text = "Scegli la categoria per questo audio:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                AudioTrackCategory.entries.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = category }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == category,
                            onClick = { selected = category }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (category) {
                                AudioTrackCategory.AMBIENCE -> "Ambience (suoni ambientali)"
                                AudioTrackCategory.MUSIC -> "Musica (colonna sonora)"
                                AudioTrackCategory.SFX -> "SFX (effetti sonori)"
                                AudioTrackCategory.CUSTOM -> "Custom"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
}
