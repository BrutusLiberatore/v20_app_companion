package com.v20charactermanager.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v20charactermanager.R
import com.v20charactermanager.domain.definition.DamageType
import com.v20charactermanager.domain.definition.HealthLevel
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.ui.components.V20ControlButton
import com.v20charactermanager.ui.components.V20IconButton
import com.v20charactermanager.ui.theme.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    character: Character,
    onBack: () -> Unit,
    onSpendBlood: (Int) -> Unit,
    onRefillBlood: (Int) -> Unit,
    onSpendWillpower: (Int) -> Unit,
    onRecoverWillpower: (Int) -> Unit,
    onApplyDamage: (Int, DamageType) -> Unit,
    onHealDamage: (Int) -> Unit,
    onEarnExperience: (Int) -> Unit,
    onSpendExperience: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.session_title, character.identity.name),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Blood Pool
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_blood_pool),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = stringResource(R.string.session_blood_pool),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            V20ControlButton(
                                icon = Icons.Default.Remove,
                                contentDescription = stringResource(R.string.session_spend),
                                onClick = { onSpendBlood(1) },
                                isPlus = false,
                                enabled = character.bloodPool.current > 0
                            )
                            V20ControlButton(
                                icon = Icons.Default.Add,
                                contentDescription = stringResource(R.string.session_refill),
                                onClick = { onRefillBlood(1) },
                                isPlus = true,
                                enabled = character.bloodPool.current < character.bloodPool.maximum
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
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
                }
            }

            // Willpower
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_humanity),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = stringResource(R.string.session_willpower),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            V20ControlButton(
                                icon = Icons.Default.Remove,
                                contentDescription = stringResource(R.string.session_spend),
                                onClick = { onSpendWillpower(1) },
                                isPlus = false,
                                enabled = character.willpower.current > 0,
                                accentColor = V20GoldDark
                            )
                            V20ControlButton(
                                icon = Icons.Default.Add,
                                contentDescription = stringResource(R.string.session_recover),
                                onClick = { onRecoverWillpower(1) },
                                isPlus = true,
                                enabled = character.willpower.current < character.willpower.permanent,
                                accentColor = V20GoldBright
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
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
                }
            }

            // Health
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_health),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = stringResource(R.string.session_health),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                                text = level.nameEn,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (level.penalty != 0) "(${level.penalty})" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(0.5f)
                            )
                            Box(
                                modifier = Modifier
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
                                    .border(0.8.dp, when(damage) { DamageType.NONE -> V20Line; DamageType.BASHING -> V20GoldBright; DamageType.LETHAL -> V20ErrorBright; DamageType.AGGRAVATED -> V20InkFaint }, RoundedCornerShape(4.dp))
                                    .clickable {
                                        val nextDamage = when (damage) {
                                            DamageType.NONE -> DamageType.BASHING
                                            DamageType.BASHING -> DamageType.LETHAL
                                            DamageType.LETHAL -> DamageType.AGGRAVATED
                                            DamageType.AGGRAVATED -> DamageType.NONE
                                        }
                                        onApplyDamage(level.index, nextDamage)
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

            // Experience
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.session_experience),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.session_available, character.experience.available),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${stringResource(R.string.session_earned, character.experience.earned)}  ·  ${stringResource(R.string.session_spent, character.experience.spent)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            V20ControlButton(
                                icon = Icons.Default.Remove,
                                contentDescription = stringResource(R.string.session_spend),
                                onClick = { onSpendExperience(1) },
                                isPlus = false,
                                enabled = character.experience.available > 0,
                                accentColor = V20GoldDark
                            )
                            V20ControlButton(
                                icon = Icons.Default.Add,
                                contentDescription = stringResource(R.string.session_earn),
                                onClick = { onEarnExperience(1) },
                                isPlus = true,
                                accentColor = V20GreenBright
                            )
                        }
                    }
                }
            }
        }
    }
}
