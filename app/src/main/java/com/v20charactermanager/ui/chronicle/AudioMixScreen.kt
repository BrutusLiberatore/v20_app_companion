package com.v20charactermanager.ui.chronicle

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.AudioPreset
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
    var showRenameDialog by remember { mutableStateOf<AudioTrack?>(null) }
    var showSavePresetDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<AudioTrackCategory?>(null) }
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

    showRenameDialog?.let { track ->
        RenameAudioDialog(
            currentTitle = track.title,
            onDismiss = { showRenameDialog = null },
            onConfirm = { newTitle ->
                audioViewModel.renameTrack(track.id, newTitle)
                showRenameDialog = null
            }
        )
    }

    if (showSavePresetDialog) {
        SavePresetDialog(
            onDismiss = { showSavePresetDialog = false },
            onConfirm = { name ->
                audioViewModel.savePreset(chronicleId, name)
                showSavePresetDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Presets section
        if (uiState.presets.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.audio_presets),
                    color = V20GoldBright,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(uiState.presets, key = { it.id }) { preset ->
                PresetCard(
                    preset = preset,
                    trackCount = uiState.tracks.size,
                    onActivate = { audioViewModel.activatePreset(preset) },
                    onDelete = { audioViewModel.deletePreset(preset.id, chronicleId) }
                )
            }
            item {
                HorizontalDivider(color = V20InkFaint.copy(alpha = 0.3f))
            }
        }

        // Category filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text(stringResource(R.string.audio_all)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = V20GoldBright,
                        selectedLabelColor = V20Black
                    )
                )
                AudioTrackCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                        },
                        label = {
                            Text(when (cat) {
                                AudioTrackCategory.AMBIENCE -> stringResource(R.string.audio_ambience)
                                AudioTrackCategory.MUSIC -> stringResource(R.string.audio_music)
                                AudioTrackCategory.SFX -> stringResource(R.string.audio_sfx)
                                AudioTrackCategory.CUSTOM -> stringResource(R.string.audio_custom)
                            })
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = V20GoldBright,
                            selectedLabelColor = V20Black
                        )
                    )
                }
            }
        }

        // Tracks section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.audio_tracks_label, uiState.tracks.size),
                    color = V20GoldBright,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Button(
                        onClick = { showSavePresetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = V20GoldBright),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        enabled = uiState.tracks.any { it.isActive }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = V20Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.audio_save_preset), color = V20Black, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { audioLauncher.launch("audio/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = V20GoldBright),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = V20Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.audio_import), color = V20Black, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { audioViewModel.stopAll() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.audio_stop_all), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        if (uiState.tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = V20InkFaint,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(R.string.audio_empty), color = V20InkFaint)
                    }
                }
            }
        } else {
            val filteredTracks = if (selectedCategoryFilter != null) {
                uiState.tracks.filter { it.category == selectedCategoryFilter }
            } else {
                uiState.tracks
            }
            items(filteredTracks, key = { it.id }) { track ->
                AudioTrackCard(
                    track = track,
                    onTogglePlay = { audioViewModel.togglePlay(track.id) },
                    onStop = { audioViewModel.stopTrack(track.id) },
                    onVolumeChange = { vol -> audioViewModel.setVolume(track.id, vol) },
                    onLoopToggle = { looping -> audioViewModel.setLooping(track.id, looping) },
                    onRename = { showRenameDialog = track },
                    onCategoryChange = { cat -> audioViewModel.updateTrackCategory(track.id, cat) },
                    onDelete = { audioViewModel.deleteTrack(track.id, chronicleId) }
                )
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: AudioPreset,
    trackCount: Int,
    onActivate: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.audio_preset_delete_title)) },
            text = { Text(stringResource(R.string.audio_preset_delete_msg, preset.name)) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text(stringResource(R.string.action_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = V20Surface2),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onActivate, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.audio_activate),
                        tint = V20GoldBright,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = preset.name,
                        color = V20Ink,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.audio_tracks_count, preset.tracks.size),
                        color = V20InkFaint,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                    tint = V20InkFaint,
                    modifier = Modifier.size(20.dp)
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
    onRename: () -> Unit,
    onCategoryChange: (AudioTrackCategory) -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
                            contentDescription = if (track.isActive) stringResource(R.string.audio_pause) else stringResource(R.string.audio_play),
                            tint = if (track.isActive) V20GoldBright else V20Ink,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            color = V20Ink,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                        Text(
                            text = when (track.category) {
                                AudioTrackCategory.AMBIENCE -> stringResource(R.string.audio_ambience)
                                AudioTrackCategory.MUSIC -> stringResource(R.string.audio_music)
                                AudioTrackCategory.SFX -> stringResource(R.string.audio_sfx)
                                AudioTrackCategory.CUSTOM -> stringResource(R.string.audio_custom)
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
                            contentDescription = stringResource(R.string.audio_loop),
                            tint = if (track.isLooping) V20GoldBright else V20InkFaint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onStop, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = stringResource(R.string.audio_stop_all),
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.audio_menu),
                                tint = V20InkFaint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.audio_rename)) },
                                onClick = { showMenu = false; onRename() },
                                leadingIcon = { Icon(Icons.Default.Edit, null) }
                            )
                            AudioTrackCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(when (cat) {
                                            AudioTrackCategory.AMBIENCE -> stringResource(R.string.audio_ambience)
                                            AudioTrackCategory.MUSIC -> stringResource(R.string.audio_music)
                                            AudioTrackCategory.SFX -> stringResource(R.string.audio_sfx)
                                            AudioTrackCategory.CUSTOM -> stringResource(R.string.audio_custom)
                                        })
                                    },
                                    onClick = { showMenu = false; onCategoryChange(cat) },
                                    leadingIcon = {
                                        Icon(
                                            if (track.category == cat) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            null
                                        )
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_delete), color = Color.Red) },
                                onClick = { showMenu = false; onDelete() },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                            )
                        }
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
private fun RenameAudioDialog(
    currentTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf(currentTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.audio_rename_title)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.audio_name)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { if (title.isNotBlank()) onConfirm(title) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun SavePresetDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.audio_preset_save_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.audio_preset_save_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = V20InkFaint
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.audio_preset_name_hint)) },
                    placeholder = { Text(stringResource(R.string.audio_preset_name_example)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun CategoryPickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (AudioTrackCategory) -> Unit
) {
    var selected by remember { mutableStateOf(AudioTrackCategory.CUSTOM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.audio_category_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.audio_category_desc),
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
                                AudioTrackCategory.AMBIENCE -> stringResource(R.string.audio_category_ambience)
                                AudioTrackCategory.MUSIC -> stringResource(R.string.audio_category_music)
                                AudioTrackCategory.SFX -> stringResource(R.string.audio_category_sfx)
                                AudioTrackCategory.CUSTOM -> stringResource(R.string.audio_custom)
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
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
