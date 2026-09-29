package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.ChronicleScene
import com.v20charactermanager.domain.model.SceneVariant

@Composable
fun SceneDetailDialog(
    scene: ChronicleScene,
    isActive: Boolean,
    variants: List<SceneVariant> = emptyList(),
    onActivate: () -> Unit,
    onAddVariant: (name: String, notes: String?) -> Unit = { _, _ -> },
    onToggleDefaultVariant: (variantId: String) -> Unit = {},
    onDeleteVariant: (variantId: String) -> Unit = {},
    onDismiss: () -> Unit
) {
    var variantName by remember { mutableStateOf("") }
    var variantNotes by remember { mutableStateOf("") }

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

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.scene_variants),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                variants.forEach { variant ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = variant.isDefault,
                            onClick = { onToggleDefaultVariant(variant.id) }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = variant.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (variant.isDefault) FontWeight.Bold else FontWeight.Normal
                            )
                            if (!variant.notes.isNullOrBlank()) {
                                Text(
                                    text = variant.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        if (variant.isDefault) {
                            Text(
                                text = stringResource(R.string.scene_variant_default),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        IconButton(onClick = { onDeleteVariant(variant.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.action_delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = variantName,
                    onValueChange = { variantName = it },
                    label = { Text(stringResource(R.string.scene_variant_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = variantNotes,
                    onValueChange = { variantNotes = it },
                    label = { Text(stringResource(R.string.npc_notes)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        if (variantName.isNotBlank()) {
                            onAddVariant(variantName, variantNotes.takeIf { it.isNotBlank() })
                            variantName = ""
                            variantNotes = ""
                        }
                    },
                    enabled = variantName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.scene_variant_add))
                }
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
