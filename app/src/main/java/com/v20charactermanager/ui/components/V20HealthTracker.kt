package com.v20charactermanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun V20HealthTracker(
    levels: List<com.v20charactermanager.domain.definition.HealthLevel>,
    damages: List<com.v20charactermanager.domain.definition.DamageType>,
    onDamageChange: (Int, com.v20charactermanager.domain.definition.DamageType) -> Unit,
    editable: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        levels.forEachIndexed { index, level ->
            val damage = damages.getOrElse(index) { com.v20charactermanager.domain.definition.DamageType.NONE }

            val damageColor = when (damage) {
                com.v20charactermanager.domain.definition.DamageType.NONE ->
                    MaterialTheme.colorScheme.surfaceVariant
                com.v20charactermanager.domain.definition.DamageType.BASHING ->
                    com.v20charactermanager.ui.theme.V20Warning
                com.v20charactermanager.domain.definition.DamageType.LETHAL ->
                    com.v20charactermanager.ui.theme.V20ErrorBright
                com.v20charactermanager.domain.definition.DamageType.AGGRAVATED ->
                    MaterialTheme.colorScheme.error
            }

            val damageSymbol = when (damage) {
                com.v20charactermanager.domain.definition.DamageType.NONE -> ""
                com.v20charactermanager.domain.definition.DamageType.BASHING -> "/"
                com.v20charactermanager.domain.definition.DamageType.LETHAL -> "X"
                com.v20charactermanager.domain.definition.DamageType.AGGRAVATED -> "*"
            }

            val penaltyText = if (level.penalty == 0) "" else "${level.penalty}"

            val clickableModifier = if (editable) {
                Modifier.clickable {
                    val nextDamage = when (damage) {
                        com.v20charactermanager.domain.definition.DamageType.NONE ->
                            com.v20charactermanager.domain.definition.DamageType.BASHING
                        com.v20charactermanager.domain.definition.DamageType.BASHING ->
                            com.v20charactermanager.domain.definition.DamageType.LETHAL
                        com.v20charactermanager.domain.definition.DamageType.LETHAL ->
                            com.v20charactermanager.domain.definition.DamageType.AGGRAVATED
                        com.v20charactermanager.domain.definition.DamageType.AGGRAVATED ->
                            com.v20charactermanager.domain.definition.DamageType.NONE
                    }
                    onDamageChange(index, nextDamage)
                }
            } else {
                Modifier
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .then(clickableModifier)
                    .semantics {
                        contentDescription = "${level.nameEn}: $damageSymbol $penaltyText"
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = level.nameEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .weight(0.5f)
                        .background(damageColor, MaterialTheme.shapes.small)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = damageSymbol,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = penaltyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
