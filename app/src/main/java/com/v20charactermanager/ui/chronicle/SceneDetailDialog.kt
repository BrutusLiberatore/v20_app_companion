package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.ChronicleScene

@Composable
fun SceneDetailDialog(
    scene: ChronicleScene,
    isActive: Boolean,
    onActivate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = scene.title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(end = 4.dp)
            ) {
                SceneField(label = stringResource(R.string.scene_hook), value = scene.hook)
                SceneField(label = stringResource(R.string.scene_objective), value = scene.objective)
                SceneField(label = stringResource(R.string.scene_conflict), value = scene.conflict)
                SceneField(label = stringResource(R.string.scene_mood), value = scene.mood)
                SceneField(label = stringResource(R.string.npc_description), value = scene.description)
                SceneField(label = stringResource(R.string.npc_notes), value = scene.notes)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onActivate,
                enabled = !isActive
            ) {
                Text(stringResource(R.string.scene_activate))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        }
    )
}

@Composable
private fun SceneField(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.height(8.dp))
}
