package com.v20charactermanager.ui.xp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XpSpendingScreen(
    viewModel: XpSpendingViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successItemName) {
        if (uiState.successItemName != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.xp_spend_title)) },
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
                .padding(16.dp)
        ) {
            uiState.character?.let { character ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.xp_available, character.experience.available),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.sheet_xp_earned, character.experience.earned),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.sheet_xp_spent, character.experience.spent),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = { Text(stringResource(R.string.xp_all)) }
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "Physical",
                        onClick = { viewModel.selectCategory("Physical") },
                        label = { Text(stringResource(R.string.xp_physical)) }
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "Social",
                        onClick = { viewModel.selectCategory("Social") },
                        label = { Text(stringResource(R.string.xp_social)) }
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "Mental",
                        onClick = { viewModel.selectCategory("Mental") },
                        label = { Text(stringResource(R.string.xp_mental)) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedCategory == "Talents",
                        onClick = { viewModel.selectCategory("Talents") },
                        label = { Text(stringResource(R.string.xp_talents)) }
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "Skills",
                        onClick = { viewModel.selectCategory("Skills") },
                        label = { Text(stringResource(R.string.xp_skills)) }
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "Knowledges",
                        onClick = { viewModel.selectCategory("Knowledges") },
                        label = { Text(stringResource(R.string.xp_knowledges)) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedCategory == "Disciplines",
                        onClick = { viewModel.selectCategory("Disciplines") },
                        label = { Text(stringResource(R.string.xp_disciplines)) }
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "Backgrounds",
                        onClick = { viewModel.selectCategory("Backgrounds") },
                        label = { Text(stringResource(R.string.xp_backgrounds)) }
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "Virtues",
                        onClick = { viewModel.selectCategory("Virtues") },
                        label = { Text(stringResource(R.string.xp_virtues)) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                uiState.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                uiState.successItemName?.let { name ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.xp_success_format, name, uiState.successNewValue, uiState.successCost),
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.traitItems) { item ->
                        XpTraitCard(
                            item = item,
                            availableXp = character.experience.available,
                            onSpend = { viewModel.spendXp(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun XpTraitCard(
    item: XpTraitItem,
    availableXp: Int,
    onSpend: () -> Unit
) {
    val canAfford = availableXp >= item.cost

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${item.category}${stringResource(R.string.xp_arrow)}${item.currentValue}${stringResource(R.string.xp_arrow)}${item.currentValue + 1}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.cost}${stringResource(R.string.xp_suffix)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (canAfford) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onSpend,
                    enabled = canAfford,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.xp_buy),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
