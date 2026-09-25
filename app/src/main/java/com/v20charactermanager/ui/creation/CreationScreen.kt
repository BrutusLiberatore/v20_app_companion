package com.v20charactermanager.ui.creation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    onMeritAdd: (com.v20charactermanager.domain.model.MeritValue) -> Unit,
    onMeritRemove: (String) -> Unit,
    onFlawAdd: (com.v20charactermanager.domain.model.FlawValue) -> Unit,
    onFlawRemove: (String) -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onConfirmWarnings: () -> Unit,
    onDismissWarnings: () -> Unit
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
        // Warning dialog for non-standard point allocation (outside scrollable content)
        uiState.pendingWarnings?.let { warnings ->
            Dialog(onDismissRequest = onDismissWarnings) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = stringResource(R.string.creation_warning_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.creation_warning_message),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        warnings.forEach { warning ->
                            Text(
                                text = "• $warning",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismissWarnings) {
                                Text(stringResource(R.string.action_cancel))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = onConfirmWarnings) {
                                Text(stringResource(R.string.creation_warning_continue))
                            }
                        }
                    }
                }
            }
        }

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
                    onIdentityChange = onIdentityChange,
                    onAttributesChange = onAttributeChange
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
                    onVirtueChange = onVirtueChange,
                    onMeritAdd = onMeritAdd,
                    onMeritRemove = onMeritRemove,
                    onFlawAdd = onFlawAdd,
                    onFlawRemove = onFlawRemove
                )
                5 -> FinalizationStep(
                    character = uiState.character,
                    freebieReport = uiState.freebieReport
                )
            }

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.currentStep > 1) {
                    V20IvoryButton(
                        text = stringResource(R.string.action_previous),
                        onClick = onPreviousStep,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (uiState.currentStep < 5) {
                    V20BloodButton(
                        text = stringResource(R.string.action_next),
                        onClick = onNextStep,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    V20BloodButton(
                        text = if (uiState.isSaving) stringResource(R.string.creation_saving) else stringResource(R.string.creation_save),
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isSaving
                    )
                }
            }
        }
    }
}
