package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.model.NpcEntry

@Composable
fun CharacterLiveCard(
    character: Character,
    onClick: () -> Unit,
    onBloodChange: (Int) -> Unit,
    onWillpowerChange: (Int) -> Unit,
    onHealthChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val identity = character.identity
    val blood = character.bloodPool
    val willpower = character.willpower
    val health = character.health
    val maxHealth = character.health.levels.size

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = identity.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${identity.clan.nameEn} \u2022 ${identity.generation}${stringResource(R.string.generation_suffix)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Blood Pool
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.session_blood),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onBloodChange(-1) },
                            modifier = Modifier.size(28.dp),
                            enabled = blood.current > 0
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "-", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${blood.current}/${blood.maximum}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { onBloodChange(1) },
                            modifier = Modifier.size(28.dp),
                            enabled = blood.current < blood.maximum
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "+", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Willpower
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.sheet_willpower_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onWillpowerChange(-1) },
                            modifier = Modifier.size(28.dp),
                            enabled = willpower.current > 0
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "-", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${willpower.current}/${willpower.permanent}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { onWillpowerChange(1) },
                            modifier = Modifier.size(28.dp),
                            enabled = willpower.current < willpower.permanent
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "+", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Health summary with controls
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.sheet_health),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onHealthChange(-1) },
                            modifier = Modifier.size(28.dp),
                            enabled = health.totalDamage > 0
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "-", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = getHealthSummary(health, maxHealth),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { onHealthChange(1) },
                            modifier = Modifier.size(28.dp),
                            enabled = health.totalDamage < maxHealth
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "+", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NpcLiveCard(
    npc: NpcEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = npc.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (npc.role.isNotEmpty()) {
                    Text(
                        text = npc.role,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

private fun getHealthSummary(health: com.v20charactermanager.domain.model.HealthState, maxHealth: Int): String {
    val damaged = health.totalDamage
    return if (damaged == 0) "OK" else "$damaged/$maxHealth"
}
