package com.v20charactermanager.ui.dice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.ui.components.V20DiceButton
import com.v20charactermanager.ui.components.V20IntField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceScreen(
    viewModel: DiceViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dice_title),
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
            // Base inputs
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.dice_roll_settings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        V20IntField(
                            value = uiState.pool,
                            onValueChange = { viewModel.updatePool(it) },
                            label = stringResource(R.string.dice_pool),
                            modifier = Modifier.weight(1f)
                        )

                        V20IntField(
                            value = uiState.difficulty,
                            onValueChange = { viewModel.updateDifficulty(it) },
                            label = stringResource(R.string.dice_difficulty),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        V20IntField(
                            value = uiState.extraDice,
                            onValueChange = { viewModel.updateExtraDice(it) },
                            label = stringResource(R.string.dice_extra),
                            modifier = Modifier.weight(1f)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = uiState.useWillpower,
                                onCheckedChange = { viewModel.updateUseWillpower(it) }
                            )
                            Text(stringResource(R.string.dice_willpower))
                        }
                    }
                }
            }

            // Modifiers
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.dice_modifier),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        V20IntField(
                            value = uiState.diceModifier,
                            onValueChange = { viewModel.updateDiceModifier(it) },
                            label = stringResource(R.string.dice_modifier),
                            modifier = Modifier.weight(1f)
                        )

                        V20IntField(
                            value = uiState.difficultyModifier,
                            onValueChange = { viewModel.updateDifficultyModifier(it) },
                            label = stringResource(R.string.dice_difficulty_modifier),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.modifierReason,
                        onValueChange = { viewModel.updateModifierReason(it) },
                        label = { Text(stringResource(R.string.dice_modifier_reason)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = uiState.explodingTens,
                            onCheckedChange = { viewModel.updateExplodingTens(it) }
                        )
                        Text(stringResource(R.string.dice_exploding_tens))
                    }
                }
            }

            // Roll summary
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    val finalPool = (uiState.pool + uiState.diceModifier + uiState.extraDice + if (uiState.useWillpower) 1 else 0).coerceAtLeast(1)
                    val finalDifficulty = (uiState.difficulty + uiState.difficultyModifier).coerceIn(2, 10)
                    Text(
                        text = stringResource(R.string.dice_final_pool, finalPool),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.dice_final_difficulty, finalDifficulty),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Roll button
            V20DiceButton(
                text = stringResource(R.string.dice_roll_button),
                onClick = { viewModel.roll() }
            )

            // Results
            uiState.result?.let { diceResult ->
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.dice_results),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Individual dice
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            diceResult.individualResults.forEach { die ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            when {
                                                die >= uiState.difficulty -> Color(0xFFC9A54E)
                                                die == 1 -> Color(0xFF8B1A1A)
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$die",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Verdict
                        Text(
                            text = when {
                                diceResult.isSuccess -> stringResource(R.string.dice_successes, diceResult.netSuccesses)
                                diceResult.isBotch -> stringResource(R.string.dice_botch)
                                else -> stringResource(R.string.dice_failure)
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                diceResult.isSuccess -> Color(0xFFC9A54E)
                                diceResult.isBotch -> Color(0xFF8B1A1A)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.dice_summary, diceResult.successes, diceResult.ones),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        if (diceResult.bonusDiceRolled > 0) {
                            Text(
                                text = stringResource(R.string.dice_bonus_dice, diceResult.bonusDiceRolled),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
