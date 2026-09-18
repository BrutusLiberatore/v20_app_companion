package com.v20charactermanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class InspectorContext {
    data class Character(val characterId: String, val name: String) : InspectorContext()
    data class Npc(val npcId: String, val name: String) : InspectorContext()
    data class Scene(val sceneId: String, val title: String) : InspectorContext()
    data class Location(val locationId: String, val name: String) : InspectorContext()
    data class Media(val assetId: String, val title: String) : InspectorContext()
    data object None : InspectorContext()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextInspector(
    context: InspectorContext,
    layoutType: AdaptiveLayoutType,
    onDismiss: () -> Unit,
    onOpenFull: (InspectorContext) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (InspectorContext) -> Unit = {}
) {
    when (layoutType) {
        AdaptiveLayoutType.COMPACT -> {
            if (context !is InspectorContext.None) {
                ModalBottomSheet(
                    onDismissRequest = onDismiss
                ) {
                    InspectorContent(
                        context = context,
                        onOpenFull = { onOpenFull(context) },
                        content = content,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        AdaptiveLayoutType.MEDIUM -> {
            if (context !is InspectorContext.None) {
                Surface(
                    modifier = modifier.width(320.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    InspectorContent(
                        context = context,
                        onOpenFull = { onOpenFull(context) },
                        content = content,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        AdaptiveLayoutType.EXPANDED -> {
            Surface(
                modifier = modifier.width(300.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                if (context !is InspectorContext.None) {
                    InspectorContent(
                        context = context,
                        onOpenFull = { onOpenFull(context) },
                        content = content,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Seleziona un elemento",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectorContent(
    context: InspectorContext,
    onOpenFull: () -> Unit,
    content: @Composable (InspectorContext) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        when (context) {
            is InspectorContext.Character -> {
                Text("PG", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(context.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            is InspectorContext.Npc -> {
                Text("NPC", color = Color(0xFF9C27B0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(context.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            is InspectorContext.Scene -> {
                Text("SCENA", color = Color(0xFFFF5722), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(context.title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            is InspectorContext.Location -> {
                Text("LUOGO", color = Color(0xFF2196F3), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(context.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            is InspectorContext.Media -> {
                Text("MEDIA", color = Color(0xFFFFC107), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(context.title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            is InspectorContext.None -> return
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        content(context)

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onOpenFull,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apri Scheda Completa", fontSize = 13.sp)
        }
    }
}
