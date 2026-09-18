package com.v20charactermanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v20charactermanager.domain.model.NpcEntry
import com.v20charactermanager.domain.model.NpcStatus

@Composable
fun NpcQuickSheet(
    npc: NpcEntry,
    onOpenFull: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(npc.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    npc.creatureType.name,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }

        if (npc.role.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFFD4A847), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(npc.role, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }

        if (npc.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                npc.description.take(120) + if (npc.description.length > 120) "..." else "",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val statusColor = when (npc.status) {
            NpcStatus.ACTIVE -> Color(0xFF4CAF50)
            NpcStatus.INACTIVE -> Color(0xFF9E9E9E)
            NpcStatus.DEAD -> Color(0xFFD32F2F)
            NpcStatus.UNKNOWN -> Color(0xFFFFC107)
            NpcStatus.ARCHIVED -> Color(0xFF607D8B)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Circle, contentDescription = null, tint = statusColor, modifier = Modifier.size(8.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(npc.status.name, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
