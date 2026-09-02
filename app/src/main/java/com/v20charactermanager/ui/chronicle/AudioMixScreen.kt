package com.v20charactermanager.ui.chronicle

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.AudioTrack
import com.v20charactermanager.domain.model.AudioTrackCategory
import com.v20charactermanager.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioMixScreen(
    chronicleId: String,
    audioViewModel: AudioViewModel,
    onBack: () -> Unit
) {
    val uiState by audioViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
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

    LaunchedEffect(chronicleId) {
        audioViewModel.loadTracks(chronicleId)
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            audioViewModel.clearMessage()
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Audio Mix",
                        color = V20GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        audioViewModel.stopAll()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = V20Ink)
                    }
                },
                actions = {
                    IconButton(onClick = { audioViewModel.stopAll() }) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop All", tint = Color.Red)
                    }
                    IconButton(onClick = { audioLauncher.launch("audio/*") }) {
                        Icon(Icons.Default.Add, contentDescription = "Import Audio", tint = V20Ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = V20Surface2)
            )
        },
        containerColor = V20Black
    ) { padding ->
        if (uiState.tracks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
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
                        text = "Importa file audio per creare la tua colonna sonora",
                        color = V20InkFaint,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                            fontWeight = FontWeight.Bold,
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
