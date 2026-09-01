@file:OptIn(ExperimentalMaterial3Api::class)

package com.v20charactermanager.ui.creation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.v20charactermanager.R
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.CharacterIdentity
import com.v20charactermanager.domain.model.NameDatabase
import com.v20charactermanager.ui.components.V20IntField

@Composable
fun IdentityStep(
    identity: CharacterIdentity,
    onIdentityChange: (CharacterIdentity) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.creation_step_identity),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = identity.name,
                onValueChange = { onIdentityChange(identity.copy(name = it)) },
                label = { Text(stringResource(R.string.field_name)) },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    val (first, last) = NameDatabase.getRandomFullName()
                    onIdentityChange(identity.copy(name = "$first $last"))
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.creation_random_name)
                )
            }
        }

        OutlinedTextField(
            value = identity.player,
            onValueChange = { onIdentityChange(identity.copy(player = it)) },
            label = { Text(stringResource(R.string.field_player)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = identity.chronicle,
            onValueChange = { onIdentityChange(identity.copy(chronicle = it)) },
            label = { Text(stringResource(R.string.field_chronicle)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Clan dropdown
        var clanExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = clanExpanded,
            onExpandedChange = { clanExpanded = it }
        ) {
            OutlinedTextField(
                value = identity.clan.nameEn,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.field_clan)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clanExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = clanExpanded,
                onDismissRequest = { clanExpanded = false }
            ) {
                ClanId.entries.forEach { clan ->
                    val displayName = if (clan == ClanId.CAITIFF) {
                        stringResource(R.string.clan_no_clan)
                    } else {
                        clan.nameEn
                    }
                    DropdownMenuItem(
                        text = { Text(displayName) },
                        onClick = {
                            val defaultSect = SectId.defaultForClan(clan)
                            onIdentityChange(identity.copy(clan = clan, sect = defaultSect))
                            clanExpanded = false
                        }
                    )
                }
            }
        }

        // Sect dropdown
        var sectExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = sectExpanded,
            onExpandedChange = { sectExpanded = it }
        ) {
            OutlinedTextField(
                value = identity.sect.nameEn,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.field_sect)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sectExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = sectExpanded,
                onDismissRequest = { sectExpanded = false }
            ) {
                SectId.entries.forEach { sect ->
                    DropdownMenuItem(
                        text = { Text(sect.nameEn) },
                        onClick = {
                            onIdentityChange(identity.copy(sect = sect))
                            sectExpanded = false
                        }
                    )
                }
            }
        }

        // Generation
        V20IntField(
            value = identity.generation,
            onValueChange = { if (it in 3..13) onIdentityChange(identity.copy(generation = it)) },
            label = stringResource(R.string.field_generation_range),
            modifier = Modifier.fillMaxWidth()
        )

        // Nature dropdown
        var natureExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = natureExpanded,
            onExpandedChange = { natureExpanded = it }
        ) {
            OutlinedTextField(
                value = identity.nature.nameEn,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.field_nature)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = natureExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = natureExpanded,
                onDismissRequest = { natureExpanded = false }
            ) {
                NatureId.entries.forEach { nature ->
                    DropdownMenuItem(
                        text = { Text(nature.nameEn) },
                        onClick = {
                            onIdentityChange(identity.copy(nature = nature))
                            natureExpanded = false
                        }
                    )
                }
            }
        }

        // Demeanor dropdown
        var demeanorExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = demeanorExpanded,
            onExpandedChange = { demeanorExpanded = it }
        ) {
            OutlinedTextField(
                value = identity.demeanor.nameEn,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.field_demeanor)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = demeanorExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = demeanorExpanded,
                onDismissRequest = { demeanorExpanded = false }
            ) {
                DemeanorId.entries.forEach { demeanor ->
                    DropdownMenuItem(
                        text = { Text(demeanor.nameEn) },
                        onClick = {
                            onIdentityChange(identity.copy(demeanor = demeanor))
                            demeanorExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = identity.sire,
            onValueChange = { onIdentityChange(identity.copy(sire = it)) },
            label = { Text(stringResource(R.string.field_sire)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = identity.haven,
            onValueChange = { onIdentityChange(identity.copy(haven = it)) },
            label = { Text(stringResource(R.string.field_haven)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = identity.concept,
            onValueChange = { onIdentityChange(identity.copy(concept = it)) },
            label = { Text(stringResource(R.string.field_concept)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AttributesStep(
    attributes: List<com.v20charactermanager.domain.model.AttributeValue>,
    onAttributeChange: (AttributeId, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.attributes_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Attribute distribution presets
        Text(
            text = stringResource(R.string.creation_preset_attributes),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    AttributeId.entries.forEach { onAttributeChange(it, 1) }
                    listOf(
                        AttributeId.STRENGTH to 4, AttributeId.DEXTERITY to 3, AttributeId.STAMINA to 3,
                        AttributeId.CHARISMA to 3, AttributeId.MANIPULATION to 3, AttributeId.APPEARANCE to 2,
                        AttributeId.PERCEPTION to 2, AttributeId.INTELLIGENCE to 2, AttributeId.WITS to 2
                    ).forEach { (id, value) -> onAttributeChange(id, value) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.creation_category_physical), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
            OutlinedButton(
                onClick = {
                    AttributeId.entries.forEach { onAttributeChange(it, 1) }
                    listOf(
                        AttributeId.STRENGTH to 2, AttributeId.DEXTERITY to 2, AttributeId.STAMINA to 2,
                        AttributeId.CHARISMA to 4, AttributeId.MANIPULATION to 3, AttributeId.APPEARANCE to 3,
                        AttributeId.PERCEPTION to 3, AttributeId.INTELLIGENCE to 3, AttributeId.WITS to 2
                    ).forEach { (id, value) -> onAttributeChange(id, value) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.creation_category_social), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
            OutlinedButton(
                onClick = {
                    AttributeId.entries.forEach { onAttributeChange(it, 1) }
                    listOf(
                        AttributeId.STRENGTH to 2, AttributeId.DEXTERITY to 2, AttributeId.STAMINA to 2,
                        AttributeId.CHARISMA to 2, AttributeId.MANIPULATION to 2, AttributeId.APPEARANCE to 2,
                        AttributeId.PERCEPTION to 3, AttributeId.INTELLIGENCE to 4, AttributeId.WITS to 3
                    ).forEach { (id, value) -> onAttributeChange(id, value) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.creation_category_mental), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }

        AttributeCategory.entries.forEach { category ->
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            AttributeId.byCategory(category).forEach { attributeId ->
                val currentValue = attributes.find { it.id == attributeId }?.value ?: 1
                AttributeRow(
                    name = attributeId.nameEn,
                    value = currentValue,
                    onValueChange = { onAttributeChange(attributeId, it) }
                )
            }
        }
    }
}

@Composable
fun AttributeRow(
    name: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge
        )

        Row {
            (1..5).forEach { dot ->
                RadioButton(
                    selected = dot <= value,
                    onClick = { onValueChange(if (dot == value) dot - 1 else dot) },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AbilitiesStep(
    abilities: List<com.v20charactermanager.domain.model.AbilityValue>,
    onAbilityChange: (AbilityId, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.abilities_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Ability distribution presets (13/9/5)
        Text(
            text = stringResource(R.string.creation_preset_abilities),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    AbilityId.entries.forEach { onAbilityChange(it, 0) }
                    listOf(
                        AbilityId.ALERTNESS to 2, AbilityId.ATHLETICS to 3, AbilityId.EXPRESSION to 1,
                        AbilityId.EMPATHY to 1, AbilityId.INTIMIDATE to 3, AbilityId.INTUITION to 1,
                        AbilityId.LEADERSHIP to 1, AbilityId.SUBTERFUGE to 1, AbilityId.STREETWISE to 0,
                        AbilityId.ANIMAL_KEN to 1, AbilityId.CRAFTS to 0, AbilityId.FIREARMS to 3,
                        AbilityId.MELEE to 3, AbilityId.STEALTH to 1, AbilityId.DRIVE to 0,
                        AbilityId.LARCENY to 0, AbilityId.PERFORMANCE to 0, AbilityId.SECURITY to 1,
                        AbilityId.SURVIVAL to 0,
                        AbilityId.ACADEMICS to 0, AbilityId.COMPUTER to 0, AbilityId.FINANCE to 0,
                        AbilityId.INVESTIGATION to 2, AbilityId.LAW to 0, AbilityId.MEDICINE to 0,
                        AbilityId.OCCULT to 1, AbilityId.POLITICS to 0, AbilityId.SCIENCE to 2
                    ).forEach { (id, value) -> onAbilityChange(id, value) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.creation_preset_combat), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
            OutlinedButton(
                onClick = {
                    AbilityId.entries.forEach { onAbilityChange(it, 0) }
                    listOf(
                        AbilityId.ALERTNESS to 1, AbilityId.ATHLETICS to 1, AbilityId.EXPRESSION to 3,
                        AbilityId.EMPATHY to 3, AbilityId.INTIMIDATE to 1, AbilityId.INTUITION to 2,
                        AbilityId.LEADERSHIP to 2, AbilityId.SUBTERFUGE to 1, AbilityId.STREETWISE to 1,
                        AbilityId.ANIMAL_KEN to 2, AbilityId.CRAFTS to 1, AbilityId.FIREARMS to 0,
                        AbilityId.MELEE to 0, AbilityId.STEALTH to 0, AbilityId.DRIVE to 2,
                        AbilityId.LARCENY to 0, AbilityId.PERFORMANCE to 2, AbilityId.SECURITY to 1,
                        AbilityId.SURVIVAL to 1,
                        AbilityId.ACADEMICS to 1, AbilityId.COMPUTER to 0, AbilityId.FINANCE to 1,
                        AbilityId.INVESTIGATION to 1, AbilityId.LAW to 0, AbilityId.MEDICINE to 0,
                        AbilityId.OCCULT to 0, AbilityId.POLITICS to 1, AbilityId.SCIENCE to 1
                    ).forEach { (id, value) -> onAbilityChange(id, value) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.creation_category_social), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
            OutlinedButton(
                onClick = {
                    AbilityId.entries.forEach { onAbilityChange(it, 0) }
                    listOf(
                        AbilityId.ALERTNESS to 2, AbilityId.ATHLETICS to 1, AbilityId.EXPRESSION to 1,
                        AbilityId.EMPATHY to 1, AbilityId.INTIMIDATE to 0, AbilityId.INTUITION to 2,
                        AbilityId.LEADERSHIP to 0, AbilityId.SUBTERFUGE to 3, AbilityId.STREETWISE to 3,
                        AbilityId.ANIMAL_KEN to 0, AbilityId.CRAFTS to 2, AbilityId.FIREARMS to 0,
                        AbilityId.MELEE to 0, AbilityId.STEALTH to 2, AbilityId.DRIVE to 1,
                        AbilityId.LARCENY to 2, AbilityId.PERFORMANCE to 0, AbilityId.SECURITY to 2,
                        AbilityId.SURVIVAL to 0,
                        AbilityId.ACADEMICS to 0, AbilityId.COMPUTER to 2, AbilityId.FINANCE to 0,
                        AbilityId.INVESTIGATION to 1, AbilityId.LAW to 0, AbilityId.MEDICINE to 0,
                        AbilityId.OCCULT to 1, AbilityId.POLITICS to 0, AbilityId.SCIENCE to 1
                    ).forEach { (id, value) -> onAbilityChange(id, value) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.creation_preset_stealth), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }

        AbilityCategory.entries.forEach { category ->
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            AbilityId.byCategory(category).forEach { abilityId ->
                val currentValue = abilities.find { it.id == abilityId }?.value ?: 0
                AbilityRow(
                    name = abilityId.nameEn,
                    value = currentValue,
                    onValueChange = { onAbilityChange(abilityId, it) }
                )
            }
        }
    }
}

@Composable
fun AbilityRow(
    name: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge
        )

        Row {
            (0..5).forEach { dot ->
                RadioButton(
                    selected = dot <= value && value > 0,
                    onClick = { onValueChange(if (dot == value) dot - 1 else dot) },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvantagesStep(
    character: com.v20charactermanager.domain.model.Character,
    onDisciplineAdd: (DisciplineId, Int) -> Unit,
    onDisciplineUpdate: (DisciplineId, Int) -> Unit,
    onDisciplineRemove: (DisciplineId) -> Unit,
    onBackgroundAdd: (BackgroundId, Int) -> Unit,
    onBackgroundUpdate: (BackgroundId, Int) -> Unit,
    onBackgroundRemove: (BackgroundId) -> Unit,
    onVirtueChange: (VirtueId, Int) -> Unit
) {
    var showDisciplineDropdown by remember { mutableStateOf(false) }
    var showBackgroundDropdown by remember { mutableStateOf(false) }

    val creationProfile = com.v20charactermanager.domain.engine.CreationProfile.forSect(character.identity.sect)

    val clanDisciplines = character.identity.clan.clanDisciplines
    val availableDisciplines = if (clanDisciplines.isEmpty()) {
        DisciplineId.entries.filter { it !in character.disciplines.map { d -> d.id } }
    } else {
        clanDisciplines.filter { it !in character.disciplines.map { d -> d.id } }
    }
    val availableBackgrounds = BackgroundId.entries.filter { it !in character.backgrounds.map { b -> b.id } }

    val disciplinePointsUsed = character.disciplines.sumOf { it.value }
    val backgroundPointsUsed = character.backgrounds.sumOf { it.value }
    val virtuePointsUsed = character.virtues.sumOf { it.value - RuleSet.VIRTUE_BASE }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.advantages_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Disciplines
        Text(
            text = stringResource(R.string.disciplines_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.discipline_points_remaining, disciplinePointsUsed, creationProfile.disciplinePoints),
            style = MaterialTheme.typography.bodySmall,
            color = if (disciplinePointsUsed == creationProfile.disciplinePoints)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error
        )

        character.disciplines.forEach { disc ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = disc.id.nameEn)
                Row {
                    (1..5).forEach { dot ->
                        RadioButton(
                            selected = dot <= disc.value,
                            onClick = { onDisciplineUpdate(disc.id, if (dot == disc.value) dot - 1 else dot) },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    TextButton(onClick = { onDisciplineRemove(disc.id) }) {
                        Text(stringResource(R.string.action_remove))
                    }
                }
            }
        }

        if (availableDisciplines.isNotEmpty()) {
            Box {
                OutlinedButton(onClick = { showDisciplineDropdown = true }) {
                    Text(stringResource(R.string.add_discipline))
                }
                DropdownMenu(
                    expanded = showDisciplineDropdown,
                    onDismissRequest = { showDisciplineDropdown = false }
                ) {
                    availableDisciplines.forEach { discId ->
                        DropdownMenuItem(
                            text = { Text(discId.nameEn) },
                            onClick = {
                                onDisciplineAdd(discId, 1)
                                showDisciplineDropdown = false
                            }
                        )
                    }
                }
            }
        }

        // Backgrounds
        Text(
            text = stringResource(R.string.backgrounds_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.background_points_remaining, backgroundPointsUsed, creationProfile.backgroundPoints),
            style = MaterialTheme.typography.bodySmall,
            color = if (backgroundPointsUsed == creationProfile.backgroundPoints)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error
        )

        character.backgrounds.forEach { bg ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = bg.id.nameEn)
                Row {
                    (1..5).forEach { dot ->
                        RadioButton(
                            selected = dot <= bg.value,
                            onClick = { onBackgroundUpdate(bg.id, if (dot == bg.value) dot - 1 else dot) },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    TextButton(onClick = { onBackgroundRemove(bg.id) }) {
                        Text(stringResource(R.string.action_remove))
                    }
                }
            }
        }

        if (availableBackgrounds.isNotEmpty()) {
            Box {
                OutlinedButton(onClick = { showBackgroundDropdown = true }) {
                    Text(stringResource(R.string.add_background))
                }
                DropdownMenu(
                    expanded = showBackgroundDropdown,
                    onDismissRequest = { showBackgroundDropdown = false }
                ) {
                    availableBackgrounds.forEach { bgId ->
                        DropdownMenuItem(
                            text = { Text(bgId.nameEn) },
                            onClick = {
                                onBackgroundAdd(bgId, 1)
                                showBackgroundDropdown = false
                            }
                        )
                    }
                }
            }
        }

        // Virtues
        Text(
            text = stringResource(R.string.virtues_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.virtue_points_remaining, virtuePointsUsed, creationProfile.virtuePoints - (character.virtues.size * RuleSet.VIRTUE_BASE)),
            style = MaterialTheme.typography.bodySmall,
            color = if (virtuePointsUsed == creationProfile.virtuePoints - (character.virtues.size * RuleSet.VIRTUE_BASE))
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error
        )

        character.virtues.forEach { virtue ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = virtue.id.nameEn)
                Row {
                    (1..5).forEach { dot ->
                        RadioButton(
                            selected = dot <= virtue.value,
                            onClick = { onVirtueChange(virtue.id, if (dot == virtue.value) dot - 1 else dot) },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FinalizationStep(
    character: com.v20charactermanager.domain.model.Character,
    freebieReport: com.v20charactermanager.domain.engine.FreebiePointCalculator.FreebieReport?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.finalization_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.freebie_points),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                freebieReport?.let { report ->
                    Text(stringResource(R.string.freebie_initial, report.initialPoints))
                    Text(stringResource(R.string.freebie_used, report.usedPoints))
                    Text(stringResource(R.string.freebie_remaining, report.remainingPoints))
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.derived_values),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(stringResource(R.string.derived_humanity, character.moralPath.humanity))
                Text(stringResource(R.string.derived_willpower, character.willpower.permanent))
                Text(stringResource(R.string.derived_blood_pool, character.bloodPool.maximum))
            }
        }
    }
}
