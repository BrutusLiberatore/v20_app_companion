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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.HouseRules
import com.v20charactermanager.ui.theme.V20GoldBright
import com.v20charactermanager.ui.theme.V20Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseRulesScreen(
    uiState: HouseRulesUiState,
    onUpdateRules: (HouseRules) -> Unit,
    onSave: () -> Unit,
    onResetDefaults: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rules = uiState.rules

    Scaffold(
        topBar = {
            TopAppBar(
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

            // Dice Rules
            SectionHeader(stringResource(R.string.house_rules_dice))

            NumericField(
                label = stringResource(R.string.house_rules_difficulty),
                value = rules.difficultyDefault,
                onValueChange = { onUpdateRules(rules.copy(difficultyDefault = it)) }
            )

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
