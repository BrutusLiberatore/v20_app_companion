package com.v20charactermanager.ui.creation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.ui.components.V20BloodButton
import com.v20charactermanager.ui.components.V20IvoryButton
import com.v20charactermanager.ui.components.V20ProgressLine
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.CharacterIdentity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreationScreen(
    uiState: CreationUiState,
    onIdentityChange: (CharacterIdentity) -> Unit,
    onAttributeChange: (AttributeId, Int) -> Unit,
    onAbilityChange: (AbilityId, Int) -> Unit,
    onDisciplineAdd: (DisciplineId, Int) -> Unit,
    onDisciplineUpdate: (DisciplineId, Int) -> Unit,
    onDisciplineRemove: (DisciplineId) -> Unit,
    onBackgroundAdd: (BackgroundId, Int) -> Unit,
    onBackgroundUpdate: (BackgroundId, Int) -> Unit,
    onBackgroundRemove: (BackgroundId) -> Unit,
    onVirtueChange: (VirtueId, Int) -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.creation_title_step, uiState.currentStep),
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
            // Step indicator
            V20ProgressLine(currentStep = uiState.currentStep, totalSteps = 5)

            // Validation errors
            uiState.validationResult?.let { result ->
                if (!result.isValid) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.creation_validation_errors),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            result.errors.forEach { error ->
                                Text(
                                    text = "• $error",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // Step content
            when (uiState.currentStep) {
                1 -> IdentityStep(
                    identity = uiState.character.identity,
                    onIdentityChange = onIdentityChange
                )
                2 -> AttributesStep(
                    attributes = uiState.character.attributes,
                    onAttributeChange = onAttributeChange
                )
                3 -> AbilitiesStep(
                    abilities = uiState.character.abilities,
                    onAbilityChange = onAbilityChange
                )
                4 -> AdvantagesStep(
                    character = uiState.character,
                    onDisciplineAdd = onDisciplineAdd,
                    onDisciplineUpdate = onDisciplineUpdate,
                    onDisciplineRemove = onDisciplineRemove,
                    onBackgroundAdd = onBackgroundAdd,
                    onBackgroundUpdate = onBackgroundUpdate,
                    onBackgroundRemove = onBackgroundRemove,
                    onVirtueChange = onVirtueChange
                )
                5 -> FinalizationStep(
                    character = uiState.character,
                    freebieReport = uiState.freebieReport
                )
            }

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (uiState.currentStep > 1) {
                    V20IvoryButton(text = stringResource(R.string.action_previous), onClick = onPreviousStep)
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (uiState.currentStep < 5) {
                    V20BloodButton(text = stringResource(R.string.action_next), onClick = onNextStep)
                } else {
                    V20BloodButton(
                        text = if (uiState.isSaving) stringResource(R.string.creation_saving) else stringResource(R.string.creation_save),
                        onClick = onSave,
                        enabled = !uiState.isSaving
                    )
                }
            }
        }
    }
}
