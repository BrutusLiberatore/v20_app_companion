package com.v20charactermanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v20charactermanager.R
import com.v20charactermanager.domain.definition.DamageType
import com.v20charactermanager.domain.definition.HealthLevel
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.ui.theme.V20Error
import com.v20charactermanager.ui.theme.V20ErrorBright
import com.v20charactermanager.ui.theme.V20Gold
import com.v20charactermanager.ui.theme.V20GoldBright
import com.v20charactermanager.ui.theme.V20GoldDark
import com.v20charactermanager.ui.theme.V20InkFaint
import com.v20charactermanager.ui.theme.V20Line
import com.v20charactermanager.ui.theme.V20Surface3
import com.v20charactermanager.util.LocaleHelper

/**
 * Unified quick status panel: blood pool, willpower and health.
 * Naked content (no Card) so callers can wrap it in their own surface.
 * [canEdit] = false hides the increment/decrement controls and makes
 * health boxes read-only (e.g. master viewing a player's character).
 */
@Composable
fun QuickStatusPanel(
    character: Character,
    onSpendBlood: () -> Unit,
    onRefillBlood: () -> Unit,
    onSpendWillpower: () -> Unit,
    onRecoverWillpower: () -> Unit,
    onCycleHealth: (Int, DamageType) -> Unit,
    modifier: Modifier = Modifier,
    canEdit: Boolean = true
) {
    val context = LocalContext.current
    val isItalian = LocaleHelper.isItalian(context)

    Column(modifier = modifier) {
        // --- Blood Pool ---
        StatusSectionHeader(
            iconRes = R.drawable.ic_blood_pool,
            title = stringResource(R.string.session_blood_pool)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${character.bloodPool.current}/${character.bloodPool.maximum}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (canEdit) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    V20ControlButton(
                        icon = Icons.Default.Remove,
                        contentDescription = stringResource(R.string.session_spend),
                        onClick = onSpendBlood,
                        isPlus = false,
                        enabled = character.bloodPool.current > 0
                    )
                    V20ControlButton(
                        icon = Icons.Default.Add,
                        contentDescription = stringResource(R.string.session_refill),
                        onClick = onRefillBlood,
                        isPlus = true,
                        enabled = character.bloodPool.current < character.bloodPool.maximum
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..character.bloodPool.maximum).forEach { box ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (box <= character.bloodPool.current)
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF3AAA5A),
                                        Color(0xFF2D7A45),
                                        Color(0xFF1A5A30)
                                    )
                                )
                            else
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF2A2A2A),
                                        Color(0xFF1A1A1A)
                                    )
                                )
                        )
                        .border(
                            0.5.dp,
                            if (box <= character.bloodPool.current) Color(0xFF4ACC6A) else Color(0xFF333333),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Willpower ---
        StatusSectionHeader(
            iconRes = R.drawable.ic_humanity,
            title = stringResource(R.string.session_willpower)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${character.willpower.current}/${character.willpower.permanent}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC9A54E)
            )
            if (canEdit) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    V20ControlButton(
                        icon = Icons.Default.Remove,
                        contentDescription = stringResource(R.string.session_spend),
                        onClick = onSpendWillpower,
                        isPlus = false,
                        enabled = character.willpower.current > 0,
                        accentColor = V20GoldDark
                    )
                    V20ControlButton(
                        icon = Icons.Default.Add,
                        contentDescription = stringResource(R.string.session_recover),
                        onClick = onRecoverWillpower,
                        isPlus = true,
                        enabled = character.willpower.current < character.willpower.permanent,
                        accentColor = V20GoldBright
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..character.willpower.permanent).forEach { box ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (box <= character.willpower.current)
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFD4A840),
                                        Color(0xFFB89030),
                                        Color(0xFF9A7A20)
                                    )
                                )
                            else
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF2A2A2A),
                                        Color(0xFF1A1A1A)
                                    )
                                )
                        )
                        .border(
                            0.5.dp,
                            if (box <= character.willpower.current) Color(0xFFE8C050) else Color(0xFF333333),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Health ---
        StatusSectionHeader(
            iconRes = R.drawable.ic_health,
            title = stringResource(R.string.session_health)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HealthLevel.entries.forEach { level ->
            val damage = character.health.levels[level.index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isItalian) level.nameIt else level.nameEn,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (level.penalty != 0) "(${level.penalty})" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.5f)
                )
                val healthBoxModifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when (damage) {
                            DamageType.NONE -> SolidColor(V20Surface3)
                            DamageType.BASHING -> Brush.verticalGradient(listOf(V20GoldBright, V20Gold, V20GoldDark))
                            DamageType.LETHAL -> Brush.verticalGradient(listOf(V20ErrorBright, V20Error, Color(0xFF4A0E0E)))
                            DamageType.AGGRAVATED -> Brush.verticalGradient(listOf(Color(0xFF333333), Color(0xFF1A1A1A), Color(0xFF0A0A0A)))
                        }
                    )
                    .border(
                        0.8.dp,
                        when (damage) {
                            DamageType.NONE -> V20Line
                            DamageType.BASHING -> V20GoldBright
                            DamageType.LETHAL -> V20ErrorBright
                            DamageType.AGGRAVATED -> V20InkFaint
                        },
                        RoundedCornerShape(4.dp)
                    )
                Box(
                    modifier = if (canEdit) {
                        healthBoxModifier.clickable {
                            val nextDamage = when (damage) {
                                DamageType.NONE -> DamageType.BASHING
                                DamageType.BASHING -> DamageType.LETHAL
                                DamageType.LETHAL -> DamageType.AGGRAVATED
                                DamageType.AGGRAVATED -> DamageType.NONE
                            }
                            onCycleHealth(level.index, nextDamage)
                        }
                    } else {
                        healthBoxModifier
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (damage) {
                            DamageType.NONE -> ""
                            DamageType.BASHING -> "/"
                            DamageType.LETHAL -> "X"
                            DamageType.AGGRAVATED -> "*"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusSectionHeader(iconRes: Int, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = Color.Unspecified
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
