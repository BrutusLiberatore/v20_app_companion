package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.ChronicleScene
import com.v20charactermanager.domain.model.SceneStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneDeckSheet(
    scenes: List<ChronicleScene>,
    activeSceneId: String?,
    onSceneSelect: (ChronicleScene) -> Unit,
    onNewScene: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.storyteller_scene_deck_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (scenes.isEmpty()) {
                Text(
                    text = stringResource(R.string.storyteller_no_scenes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(scenes) { scene ->
                        val isActive = scene.id == activeSceneId
                        val icon = when {
                            isActive -> Icons.Filled.PlayCircle
                            scene.status == SceneStatus.COMPLETED -> Icons.Filled.CheckCircle
                            else -> Icons.Filled.RadioButtonUnchecked
                        }
                        val iconTint = when {
                            isActive -> MaterialTheme.colorScheme.primary
                            scene.status == SceneStatus.COMPLETED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        }

                        ListItem(
                            headlineContent = {
                                Text(
                                    text = scene.title,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconTint
                                )
                            },
                            modifier = Modifier.clickable {
                                onSceneSelect(scene)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    onNewScene()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.storyteller_new_scene))
            }
        }
    }
}
