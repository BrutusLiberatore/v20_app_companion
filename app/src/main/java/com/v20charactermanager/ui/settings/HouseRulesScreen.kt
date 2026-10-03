package com.v20charactermanager.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.definition.ClanId
import com.v20charactermanager.domain.model.HouseRules
import com.v20charactermanager.ui.theme.V20GoldBright
import com.v20charactermanager.ui.components.V20TopBar
import com.v20charactermanager.ui.theme.V20Surface

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HouseRulesScreen(
    uiState: HouseRulesUiState,
    onUpdateRules: (HouseRules) -> Unit,
    onSave: () -> Unit,
    onResetDefaults: () -> Unit,
    onClearMessage: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rules = uiState.rules

    LaunchedEffect(uiState.message) {
        if (uiState.message != null) {
            kotlinx.coroutines.delay(2500)
            onClearMessage()
        }
    }

    Scaffold(
        topBar = {
            V20TopBar(
                title = { Text(stringResource(R.string.house_rules_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    TextButton(onClick = onResetDefaults) {
                        Text(stringResource(R.string.house_rules_reset), color = V20GoldBright)
                    }
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Save, contentDescription = stringResource(R.string.save), tint = V20GoldBright)
                    }
                }
            )
        },
        containerColor = V20Surface
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.message?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFA5D6A7)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.msg_house_rules_saved),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }

            // Creation Section
            SectionHeader(stringResource(R.string.house_rules_creation))

            NumericField(
                label = stringResource(R.string.house_rules_attr_primary),
                value = rules.attributePrimary,
                onValueChange = { onUpdateRules(rules.copy(attributePrimary = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_attr_secondary),
                value = rules.attributeSecondary,
                onValueChange = { onUpdateRules(rules.copy(attributeSecondary = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_attr_tertiary),
                value = rules.attributeTertiary,
                onValueChange = { onUpdateRules(rules.copy(attributeTertiary = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_ability_primary),
                value = rules.abilityPrimary,
                onValueChange = { onUpdateRules(rules.copy(abilityPrimary = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_ability_secondary),
                value = rules.abilitySecondary,
                onValueChange = { onUpdateRules(rules.copy(abilitySecondary = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_ability_tertiary),
                value = rules.abilityTertiary,
                onValueChange = { onUpdateRules(rules.copy(abilityTertiary = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_discipline_initial),
                value = rules.disciplineInitial,
                onValueChange = { onUpdateRules(rules.copy(disciplineInitial = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_background_initial),
                value = rules.backgroundInitial,
                onValueChange = { onUpdateRules(rules.copy(backgroundInitial = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_virtue_initial),
                value = rules.virtueInitial,
                onValueChange = { onUpdateRules(rules.copy(virtueInitial = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_freebie_points),
                value = rules.freebiePoints,
                onValueChange = { onUpdateRules(rules.copy(freebiePoints = it)) }
            )

            HorizontalDivider()

            // Freebie Costs
            SectionHeader(stringResource(R.string.house_rules_freebie_costs))

            NumericField(
                label = stringResource(R.string.house_rules_freebie_attr),
                value = rules.freebieAttributeCost,
                onValueChange = { onUpdateRules(rules.copy(freebieAttributeCost = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_freebie_ability),
                value = rules.freebieAbilityCost,
                onValueChange = { onUpdateRules(rules.copy(freebieAbilityCost = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_freebie_discipline),
                value = rules.freebieDisciplineCost,
                onValueChange = { onUpdateRules(rules.copy(freebieDisciplineCost = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_freebie_background),
                value = rules.freebieBackgroundCost,
                onValueChange = { onUpdateRules(rules.copy(freebieBackgroundCost = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_freebie_virtue),
                value = rules.freebieVirtueCost,
                onValueChange = { onUpdateRules(rules.copy(freebieVirtueCost = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_freebie_humanity),
                value = rules.freebieHumanityCost,
                onValueChange = { onUpdateRules(rules.copy(freebieHumanityCost = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_freebie_willpower),
                value = rules.freebieWillpowerCost,
                onValueChange = { onUpdateRules(rules.copy(freebieWillpowerCost = it)) }
            )

            HorizontalDivider()

            // Starting Values
            SectionHeader(stringResource(R.string.house_rules_starting))

            NumericField(
                label = stringResource(R.string.house_rules_starting_blood),
                value = rules.startingBlood,
                onValueChange = { onUpdateRules(rules.copy(startingBlood = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_starting_willpower),
                value = rules.startingWillpower,
                onValueChange = { onUpdateRules(rules.copy(startingWillpower = it)) }
            )

            HorizontalDivider()

            // XP Costs
            SectionHeader(stringResource(R.string.house_rules_xp))

            NumericField(
                label = stringResource(R.string.house_rules_xp_attr),
                value = rules.xpAttributeCostPerDot,
                onValueChange = { onUpdateRules(rules.copy(xpAttributeCostPerDot = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_attr_new),
                value = rules.xpNewAttributeCost,
                onValueChange = { onUpdateRules(rules.copy(xpNewAttributeCost = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_ability),
                value = rules.xpAbilityCostPerDot,
                onValueChange = { onUpdateRules(rules.copy(xpAbilityCostPerDot = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_ability_new),
                value = rules.xpNewAbilityCost,
                onValueChange = { onUpdateRules(rules.copy(xpNewAbilityCost = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_background),
                value = rules.xpBackgroundCostPerDot,
                onValueChange = { onUpdateRules(rules.copy(xpBackgroundCostPerDot = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_virtue),
                value = rules.xpVirtueCostPerDot,
                onValueChange = { onUpdateRules(rules.copy(xpVirtueCostPerDot = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_humanity),
                value = rules.xpHumanityCostPerDot,
                onValueChange = { onUpdateRules(rules.copy(xpHumanityCostPerDot = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_willpower),
                value = rules.xpWillpowerCostPerDot,
                onValueChange = { onUpdateRules(rules.copy(xpWillpowerCostPerDot = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_disc_in),
                value = rules.xpDisciplineInClanPerLevel,
                onValueChange = { onUpdateRules(rules.copy(xpDisciplineInClanPerLevel = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_disc_new),
                value = rules.xpNewDisciplineInClan,
                onValueChange = { onUpdateRules(rules.copy(xpNewDisciplineInClan = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_disc_out),
                value = rules.xpDisciplineOutOfClanPerLevel,
                onValueChange = { onUpdateRules(rules.copy(xpDisciplineOutOfClanPerLevel = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_disc_new_out),
                value = rules.xpNewDisciplineOutOfClan,
                onValueChange = { onUpdateRules(rules.copy(xpNewDisciplineOutOfClan = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_disc_caitiff),
                value = rules.xpDisciplineCaitiffPerLevel,
                onValueChange = { onUpdateRules(rules.copy(xpDisciplineCaitiffPerLevel = it)) }
            )
            NumericField(
                label = stringResource(R.string.house_rules_xp_disc_new_caitiff),
                value = rules.xpNewDisciplineCaitiff,
                onValueChange = { onUpdateRules(rules.copy(xpNewDisciplineCaitiff = it)) }
            )

            HorizontalDivider()

            // Dice Rules
            SectionHeader(stringResource(R.string.house_rules_dice))

            NumericField(
                label = stringResource(R.string.house_rules_difficulty),
                value = rules.difficultyDefault,
                onValueChange = { onUpdateRules(rules.copy(difficultyDefault = it)) }
            )
            CheckRow(
                label = stringResource(R.string.house_rules_exploding_available),
                checked = rules.explodingTensAvailable,
                onCheckedChange = { onUpdateRules(rules.copy(explodingTensAvailable = it)) }
            )
            CheckRow(
                label = stringResource(R.string.house_rules_exploding_default),
                checked = rules.explodingTensDefault,
                enabled = rules.explodingTensAvailable,
                onCheckedChange = { onUpdateRules(rules.copy(explodingTensDefault = it)) }
            )
            CheckRow(
                label = stringResource(R.string.house_rules_exploding_recursive),
                checked = rules.explodingTensRecursive,
                enabled = rules.explodingTensAvailable,
                onCheckedChange = { onUpdateRules(rules.copy(explodingTensRecursive = it)) }
            )

            HorizontalDivider()

            // Allowed Content
            SectionHeader(stringResource(R.string.house_rules_allowed))

            Text(
                text = stringResource(R.string.house_rules_allowed_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ClanId.entries.forEach { clan ->
                    val allowed = clan.name !in rules.excludedClans
                    FilterChip(
                        selected = allowed,
                        onClick = {
                            val updated = if (allowed) {
                                rules.excludedClans + clan.name
                            } else {
                                rules.excludedClans - clan.name
                            }
                            onUpdateRules(rules.copy(excludedClans = updated))
                        },
                        label = {
                            Text(
                                text = if (clan == ClanId.CAITIFF) {
                                    stringResource(R.string.clan_no_clan)
                                } else {
                                    clan.nameEn
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = V20GoldBright,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun CheckRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Composable
private fun NumericField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(value) { mutableStateOf(value.toString()) }

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            newText.toIntOrNull()?.let { onValueChange(it) }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
