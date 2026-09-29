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
import com.v20charactermanager.ui.components.QuickStatusPanel
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
            // Quick status panel (blood pool + willpower + health)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    QuickStatusPanel(
                        character = character,
                        onSpendBlood = { onSpendBlood(1) },
                        onRefillBlood = { onRefillBlood(1) },
                        onSpendWillpower = { onSpendWillpower(1) },
                        onRecoverWillpower = { onRecoverWillpower(1) },
                        onCycleHealth = { index, type ->
                            if (type == DamageType.NONE) onHealDamage(index) else onApplyDamage(index, type)
                        }
                    )
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
