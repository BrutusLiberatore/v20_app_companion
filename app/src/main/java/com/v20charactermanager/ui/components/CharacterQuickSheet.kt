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
import com.v20charactermanager.domain.model.Character

@Composable
fun CharacterQuickSheet(
    character: Character,
    onBloodChange: (Int) -> Unit,
    onWillpowerChange: (Int) -> Unit,
    onOpenFull: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(character.identity.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "${character.identity.clan.nameIt} • ${character.identity.generation}ª Gen",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        QuickStatRow(
            label = "Sangue",
            current = character.bloodPool.current,
            max = character.bloodPool.maximum,
            color = Color(0xFFD32F2F),
            onDecrement = { onBloodChange(-1) },
            onIncrement = { onBloodChange(1) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        QuickStatRow(
            label = "Volontà",
            current = character.willpower.current,
            max = character.willpower.permanent,
            color = Color(0xFF1976D2),
            onDecrement = { onWillpowerChange(-1) },
            onIncrement = { onWillpowerChange(1) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Salute", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            Spacer(modifier = Modifier.weight(1f))
            val damageCount = character.health.totalDamage
            val healthState = when {
                damageCount >= 6 -> "Incapacitato"
                damageCount >= 5 -> "Grave"
                damageCount >= 3 -> "Ferito"
                else -> "Sano"
            }
            val healthColor = when {
                damageCount >= 6 -> Color(0xFFD32F2F)
                damageCount >= 5 -> Color(0xFFFF9800)
                damageCount >= 3 -> Color(0xFFFFC107)
                else -> Color(0xFF4CAF50)
            }
            Text(healthState, color = healthColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuickStatRow(
    label: String,
    current: Int,
    max: Int,
    color: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, modifier = Modifier.width(60.dp))
        IconButton(onClick = onDecrement, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Remove, contentDescription = "−", tint = color, modifier = Modifier.size(16.dp))
        }
        Text(
            "$current / $max",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.width(50.dp).wrapContentWidth(Alignment.CenterHorizontally)
        )
        IconButton(onClick = onIncrement, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Add, contentDescription = "+", tint = color, modifier = Modifier.size(16.dp))
        }
    }
}
