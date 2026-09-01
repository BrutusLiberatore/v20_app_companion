package com.v20charactermanager.ui.sheet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.ui.theme.*
import com.v20charactermanager.ui.components.V20BloodButton
import com.v20charactermanager.ui.components.V20IvoryButton
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.AbilityValue
import com.v20charactermanager.domain.model.AttributeValue
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.model.MeritValue
import com.v20charactermanager.domain.model.FlawValue
import com.v20charactermanager.ui.components.V20DotRating as V20DotRatingComponent
import com.v20charactermanager.ui.components.PortraitPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetScreen(
    character: Character,
    onBack: () -> Unit,
    onNavigateToSession: () -> Unit,
    onNotesChange: (String) -> Unit,
    onNavigateToDice: (Int) -> Unit,
    isEditing: Boolean = false,
    onToggleEdit: () -> Unit = {},
    onSave: () -> Unit = {},
    onCancelEdit: () -> Unit = {},
    onAttributeChange: (AttributeId, Int) -> Unit = { _, _ -> },
    onAbilityChange: (AbilityId, Int) -> Unit = { _, _ -> },
    onDisciplineValueChange: (DisciplineId, Int) -> Unit = { _, _ -> },
    onDisciplineRemove: (DisciplineId) -> Unit = {},
    onBackgroundValueChange: (BackgroundId, Int) -> Unit = { _, _ -> },
    onBackgroundRemove: (BackgroundId) -> Unit = {},
    onVirtueChange: (VirtueId, Int) -> Unit = { _, _ -> },
    onPortraitChange: (String?) -> Unit = {},
    onMeritAdd: (MeritValue) -> Unit = {},
    onMeritRemove: (String) -> Unit = {},
    onMeritClone: (MeritValue) -> Unit = {},
    onFlawAdd: (FlawValue) -> Unit = {},
    onFlawRemove: (String) -> Unit = {},
    onFlawClone: (FlawValue) -> Unit = {},
    onEquipmentAdd: (com.v20charactermanager.domain.model.EquipmentItem) -> Unit = {},
    onEquipmentUpdate: (com.v20charactermanager.domain.model.EquipmentItem) -> Unit = {},
    onEquipmentRemove: (String) -> Unit = {},
    onEquipmentClone: (com.v20charactermanager.domain.model.EquipmentItem) -> Unit = {},
    onNavigateToXpSpending: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.sheet_tab_overview),
        stringResource(R.string.sheet_tab_attributes),
        stringResource(R.string.sheet_tab_abilities),
        stringResource(R.string.sheet_tab_advantages),
        stringResource(R.string.sheet_tab_details),
        stringResource(R.string.sheet_merits_flaws),
        stringResource(R.string.sheet_equipment),
        stringResource(R.string.sheet_notes)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = character.identity.name.ifEmpty { stringResource(R.string.character_unnamed) },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = if (isEditing) onCancelEdit else onBack) {
                        Icon(
                            if (isEditing) Icons.Default.Close else Icons.Default.ArrowBack,
                            stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = onSave) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = stringResource(R.string.action_save),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        IconButton(onClick = onToggleEdit) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.action_edit),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        TextButton(onClick = onNavigateToSession) {
                            Text(stringResource(R.string.action_session), color = MaterialTheme.colorScheme.onPrimary)
                        }
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
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = TabBg,
                contentColor = TabActive,
                edgePadding = 0.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                when (index) {
                                    1 -> Icon(painterResource(R.drawable.ic_attributes), null, Modifier.size(16.dp), tint = Color.Unspecified)
                                    2 -> Icon(painterResource(R.drawable.ic_abilities), null, Modifier.size(16.dp), tint = Color.Unspecified)
                                    3 -> Icon(painterResource(R.drawable.ic_merits), null, Modifier.size(16.dp), tint = Color.Unspecified)
                                    4 -> Icon(painterResource(R.drawable.ic_blood_pool), null, Modifier.size(16.dp), tint = Color.Unspecified)
                                    5 -> Icon(painterResource(R.drawable.ic_humanity), null, Modifier.size(16.dp), tint = Color.Unspecified)
                                    6 -> Icon(painterResource(R.drawable.ic_equipment), null, Modifier.size(16.dp), tint = Color.Unspecified)
                                    7 -> Icon(painterResource(R.drawable.ic_notes), null, Modifier.size(16.dp), tint = Color.Unspecified)
                                }
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selectedTab == index) TabActive else TabInactive
                                )
                            }
                        },
                        selectedContentColor = TabActive,
                        unselectedContentColor = TabInactive
                    )
                }
            }

            when (selectedTab) {
                0 -> OverviewTab(
                    character = character,
                    isEditing = isEditing,
                    onPortraitChange = onPortraitChange,
                    onNavigateToDice = onNavigateToDice,
                    onNavigateToSession = onNavigateToSession,
                    onNavigateToXpSpending = onNavigateToXpSpending
                )
                1 -> AttributesTab(
                    character = character,
                    isEditing = isEditing,
                    onAttributeChange = onAttributeChange,
                    onNavigateToDice = onNavigateToDice
                )
                2 -> AbilitiesTab(
                    character = character,
                    isEditing = isEditing,
                    onAbilityChange = onAbilityChange
                )
                3 -> AdvantagesTab(
                    character = character,
                    isEditing = isEditing,
                    onDisciplineValueChange = onDisciplineValueChange,
                    onDisciplineRemove = onDisciplineRemove,
                    onBackgroundValueChange = onBackgroundValueChange,
                    onBackgroundRemove = onBackgroundRemove,
                    onVirtueChange = onVirtueChange
                )
                4 -> DetailsTab(character)
                5 -> MeritsFlawsTab(
                    character = character,
                    isEditing = isEditing,
                    onMeritAdd = onMeritAdd,
                    onMeritRemove = onMeritRemove,
                    onMeritClone = onMeritClone,
                    onFlawAdd = onFlawAdd,
                    onFlawRemove = onFlawRemove,
                    onFlawClone = onFlawClone
                )
                6 -> EquipmentTab(
                    character = character,
                    isEditing = isEditing,
                    onEquipmentAdd = onEquipmentAdd,
                    onEquipmentUpdate = onEquipmentUpdate,
                    onEquipmentRemove = onEquipmentRemove,
                    onEquipmentClone = onEquipmentClone
                )
                7 -> NotesTab(character, onNotesChange)
            }
        }
    }
}

@Composable
fun V20DotRating(
    value: Int,
    maxValue: Int = 5,
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    onValueChange: ((Int) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxValue) { index ->
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .then(
                        if (index < value) {
                            Modifier.background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier
                                .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        }
                    )
                    .then(
                        if (editable && onValueChange != null) {
                            Modifier.clickable {
                                val newValue = if (index + 1 == value) index else index + 1
                                onValueChange(newValue.coerceAtLeast(0))
                            }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

@Composable
fun OverviewTab(
    character: Character,
    isEditing: Boolean = false,
    onPortraitChange: (String?) -> Unit = {},
    onNavigateToDice: (Int) -> Unit,
    onNavigateToSession: () -> Unit,
    onNavigateToXpSpending: () -> Unit = {}
) {
    val context = LocalContext.current
    var showGalleryPicker by remember { mutableStateOf(false) }
    var showCameraPicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = com.v20charactermanager.data.local.PortraitManager.savePortrait(
                context, character.id, it
            )
            onPortraitChange(path)
        }
    }

    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let { uri ->
                val path = com.v20charactermanager.data.local.PortraitManager.savePortrait(
                    context, character.id, uri
                )
                onPortraitChange(path)
            }
        }
        cameraUri = null
    }

    LaunchedEffect(showGalleryPicker) {
        if (showGalleryPicker) {
            galleryLauncher.launch("image/*")
            showGalleryPicker = false
        }
    }

    LaunchedEffect(showCameraPicker) {
        if (showCameraPicker) {
            val file = java.io.File(context.filesDir, "camera_temp_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            cameraUri = uri
            cameraLauncher.launch(uri)
            showCameraPicker = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header: Portrait + Name, Clan, Generation
        Card {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PortraitPicker(
                    portraitPath = character.portraitUri,
                    onGalleryPick = { showGalleryPicker = true },
                    onCameraPick = { showCameraPicker = true },
                    onRemove = { onPortraitChange(null) },
                    size = 100
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = character.identity.name.ifEmpty { stringResource(R.string.character_unnamed) },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${character.identity.clan.nameEn}${stringResource(R.string.overview_generation_dash, character.identity.generation)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (character.identity.chronicle.isNotEmpty()) {
                        Text(
                            text = character.identity.chronicle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Stats: Blood Pool, Willpower, Humanity, Health
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.sheet_quick_stats),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Blood Pool with dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_blood_pool),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(R.string.sheet_blood_pool, character.bloodPool.current, character.bloodPool.maximum),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                V20DotRatingComponent(
                    currentValue = character.bloodPool.current,
                    maxValue = character.bloodPool.maximum.coerceAtMost(15),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Willpower with dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_humanity),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(R.string.sheet_willpower, character.willpower.current, character.willpower.permanent),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                V20DotRatingComponent(
                    currentValue = character.willpower.current,
                    maxValue = character.willpower.permanent,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Humanity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_humanity),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(R.string.sheet_humanity, character.moralPath.humanity),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                V20DotRatingComponent(
                    currentValue = character.moralPath.humanity,
                    maxValue = 10,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Health Status
                val healthyLevels = character.health.levels.count { it == DamageType.NONE }
                val totalLevels = 7
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_health),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "${stringResource(R.string.dashboard_health)}: $healthyLevels/$totalLevels ${stringResource(R.string.dashboard_healthy)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Quick Actions Row
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.sheet_quick_stats),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    V20BloodButton(
                        text = stringResource(R.string.dashboard_roll_dice),
                        onClick = {
                            val totalDice = character.attributes.sumOf { it.value } / 3
                            onNavigateToDice(totalDice.coerceAtLeast(1))
                        },
                        modifier = Modifier.weight(1f),
                        height = 40.dp
                    )
                    V20IvoryButton(
                        text = stringResource(R.string.dashboard_session_mode),
                        onClick = onNavigateToSession,
                        modifier = Modifier.weight(1f),
                        height = 40.dp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    V20IvoryButton(
                        text = stringResource(R.string.sheet_spend_xp),
                        onClick = onNavigateToXpSpending,
                        modifier = Modifier.weight(1f),
                        height = 40.dp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttributesTab(
    character: Character,
    isEditing: Boolean = false,
    onAttributeChange: (AttributeId, Int) -> Unit = { _, _ -> },
    onNavigateToDice: (Int) -> Unit
) {
    var expandedAttribute by remember { mutableStateOf<AttributeId?>(null) }
    var showAbilityDialog by remember { mutableStateOf(false) }
    var selectedAttributeForPool by remember { mutableStateOf<AttributeValue?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AttributeCategory.entries.forEach { category ->
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            character.attributeByCategory[category]?.forEach { attr ->
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!isEditing) {
                                    expandedAttribute = attr.id
                                }
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = attr.id.nameEn)
                        V20DotRating(
                            value = attr.value,
                            maxValue = 5,
                            editable = isEditing,
                            onValueChange = { newValue -> onAttributeChange(attr.id, newValue) }
                        )
                    }

                    if (!isEditing) {
                        DropdownMenu(
                            expanded = expandedAttribute == attr.id,
                            onDismissRequest = { expandedAttribute = null }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.attribute_roll_x_dice, attr.value)) },
                                onClick = {
                                    expandedAttribute = null
                                    onNavigateToDice(attr.value)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.attribute_build_dice_pool)) },
                                onClick = {
                                    expandedAttribute = null
                                    selectedAttributeForPool = attr
                                    showAbilityDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.attribute_view_details)) },
                                onClick = {
                                    expandedAttribute = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Build Dice Pool dialog
    val attrForPool = selectedAttributeForPool
    if (showAbilityDialog && attrForPool != null) {
        AbilitySelectionDialog(
            attribute = attrForPool,
            character = character,
            onSelectAbility = { _, abilityValue ->
                showAbilityDialog = false
                val totalPool = attrForPool.value + abilityValue
                selectedAttributeForPool = null
                onNavigateToDice(totalPool)
            },
            onDismiss = {
                showAbilityDialog = false
                selectedAttributeForPool = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbilitySelectionDialog(
    attribute: AttributeValue,
    character: Character,
    onSelectAbility: (AbilityId, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.attribute_select_ability)) },
        text = {
            Column {
                Text(
                    text = "${attribute.id.nameEn}: ${stringResource(R.string.overview_dice_count, attribute.value)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = stringResource(R.string.advantages_select_ability),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        character.abilities.filter { it.value > 0 }.forEach { ability ->
                            DropdownMenuItem(
                                text = {
                                    Text("${ability.id.nameEn} (${ability.value})")
                                },
                                onClick = {
                                    expanded = false
                                    onSelectAbility(ability.id, ability.value)
                                }
                            )
                        }
                        if (character.abilities.all { it.value == 0 }) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.sheet_no_abilities),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = { expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun AbilitiesTab(
    character: Character,
    isEditing: Boolean = false,
    onAbilityChange: (AbilityId, Int) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AbilityCategory.entries.forEach { category ->
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            character.abilityByCategory[category]?.forEach { ability ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = ability.id.nameEn)
                    V20DotRating(
                        value = ability.value,
                        maxValue = 5,
                        editable = isEditing,
                        onValueChange = { newValue -> onAbilityChange(ability.id, newValue) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvantagesTab(
    character: Character,
    isEditing: Boolean = false,
    onDisciplineValueChange: (DisciplineId, Int) -> Unit = { _, _ -> },
    onDisciplineRemove: (DisciplineId) -> Unit = {},
    onBackgroundValueChange: (BackgroundId, Int) -> Unit = { _, _ -> },
    onBackgroundRemove: (BackgroundId) -> Unit = {},
    onVirtueChange: (VirtueId, Int) -> Unit = { _, _ -> }
) {
    var showAddDisciplineDialog by remember { mutableStateOf(false) }
    var showAddBackgroundDialog by remember { mutableStateOf(false) }
    var viewingDiscipline by remember { mutableStateOf<com.v20charactermanager.domain.definition.DisciplineId?>(null) }
    var viewingDisciplineValue by remember { mutableIntStateOf(0) }
    var viewingBackground by remember { mutableStateOf<com.v20charactermanager.domain.definition.BackgroundId?>(null) }
    var viewingBackgroundValue by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.sheet_disciplines),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (isEditing) {
                IconButton(onClick = { showAddDisciplineDialog = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_discipline),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        character.disciplines.forEach { disc ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewingDiscipline = disc.id; viewingDisciplineValue = disc.value }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = disc.id.nameEn)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V20DotRating(
                        value = disc.value,
                        maxValue = 5,
                        editable = isEditing,
                        onValueChange = { newValue -> onDisciplineValueChange(disc.id, newValue) }
                    )
                    if (isEditing) {
                        IconButton(onClick = { onDisciplineRemove(disc.id) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.action_remove),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.sheet_backgrounds),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (isEditing) {
                IconButton(onClick = { showAddBackgroundDialog = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_background),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        character.backgrounds.forEach { bg ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewingBackground = bg.id; viewingBackgroundValue = bg.value }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = bg.id.nameEn)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V20DotRating(
                        value = bg.value,
                        maxValue = 5,
                        editable = isEditing,
                        onValueChange = { newValue -> onBackgroundValueChange(bg.id, newValue) }
                    )
                    if (isEditing) {
                        IconButton(onClick = { onBackgroundRemove(bg.id) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.action_remove),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = stringResource(R.string.sheet_virtues),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        character.virtues.forEach { virtue ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = virtue.id.nameEn)
                V20DotRating(
                    value = virtue.value,
                    maxValue = 5,
                    editable = isEditing,
                    onValueChange = { newValue -> onVirtueChange(virtue.id, newValue) }
                )
            }
        }
    }

    if (showAddDisciplineDialog) {
        AddItemDialog(
            title = stringResource(R.string.add_discipline),
            items = DisciplineId.entries.filter { disc ->
                character.disciplines.none { it.id == disc }
            },
            itemName = { it.nameEn },
            onDismiss = { showAddDisciplineDialog = false },
            onConfirm = { disciplineId ->
                onDisciplineValueChange(disciplineId, 1)
                showAddDisciplineDialog = false
            }
        )
    }

    if (showAddBackgroundDialog) {
        AddItemDialog(
            title = stringResource(R.string.add_background),
            items = BackgroundId.entries.filter { bg ->
                character.backgrounds.none { it.id == bg }
            },
            itemName = { it.nameEn },
            onDismiss = { showAddBackgroundDialog = false },
            onConfirm = { backgroundId ->
                onBackgroundValueChange(backgroundId, 1)
                showAddBackgroundDialog = false
            }
        )
    }

    viewingDiscipline?.let { discId ->
        ItemDetailPopup(
            title = discId.nameEn,
            subtitle = stringResource(R.string.disciplines_title),
            details = listOf(
                stringResource(R.string.compendium_cost) to viewingDisciplineValue.toString()
            ),
            onDismiss = { viewingDiscipline = null }
        )
    }

    viewingBackground?.let { bgId ->
        ItemDetailPopup(
            title = bgId.nameEn,
            subtitle = stringResource(R.string.sheet_backgrounds),
            details = listOf(
                stringResource(R.string.compendium_cost) to viewingBackgroundValue.toString()
            ),
            onDismiss = { viewingBackground = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AddItemDialog(
    title: String,
    items: List<T>,
    itemName: (T) -> String,
    onDismiss: () -> Unit,
    onConfirm: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<T?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedItem?.let { itemName(it) } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.advantages_select)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        items.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(itemName(item)) },
                                onClick = {
                                    selectedItem = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedItem?.let { onConfirm(it) } },
                enabled = selectedItem != null
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun DetailsTab(character: Character) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.sheet_health),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                HealthLevel.entries.forEach { level ->
                    val damage = character.health.levels[level.index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = level.nameEn)
                        Text(
                            text = when (damage) {
                                DamageType.NONE -> stringResource(R.string.sheet_health_none)
                                DamageType.BASHING -> stringResource(R.string.sheet_damage_bashing)
                                DamageType.LETHAL -> stringResource(R.string.sheet_damage_lethal)
                                DamageType.AGGRAVATED -> stringResource(R.string.sheet_damage_aggravated)
                            }
                        )
                    }
                }
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.sheet_experience),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.sheet_xp_earned, character.experience.earned))
                Text(stringResource(R.string.sheet_xp_spent, character.experience.spent))
                Text(stringResource(R.string.sheet_xp_available, character.experience.available))
            }
        }
    }
}

@Composable
fun MeritsFlawsTab(
    character: Character,
    isEditing: Boolean = false,
    onMeritAdd: (MeritValue) -> Unit = {},
    onMeritRemove: (String) -> Unit = {},
    onMeritClone: (MeritValue) -> Unit = {},
    onFlawAdd: (FlawValue) -> Unit = {},
    onFlawRemove: (String) -> Unit = {},
    onFlawClone: (FlawValue) -> Unit = {}
) {
    var showAddMeritDialog by remember { mutableStateOf(false) }
    var showAddFlawDialog by remember { mutableStateOf(false) }
    var viewingMerit by remember { mutableStateOf<MeritValue?>(null) }
    var viewingFlaw by remember { mutableStateOf<FlawValue?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.sheet_merits_flaws),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sheet_merits),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (isEditing) {
                        IconButton(onClick = { showAddMeritDialog = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.sheet_add_merit),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (character.merits.isEmpty()) {
                    Text(
                        text = stringResource(R.string.sheet_no_merits),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    character.merits.forEach { merit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewingMerit = merit }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = merit.name, fontWeight = FontWeight.Medium)
                                if (merit.description.isNotEmpty()) {
                                    Text(
                                        text = merit.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "(${merit.cost})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (isEditing) {
                                    IconButton(onClick = { onMeritClone(merit) }) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = stringResource(R.string.action_duplicate),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { onMeritRemove(merit.id) }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = stringResource(R.string.action_remove),
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sheet_flaws),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (isEditing) {
                        IconButton(onClick = { showAddFlawDialog = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.sheet_add_flaw),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (character.flaws.isEmpty()) {
                    Text(
                        text = stringResource(R.string.sheet_no_flaws),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    character.flaws.forEach { flaw ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewingFlaw = flaw }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = flaw.name, fontWeight = FontWeight.Medium)
                                if (flaw.description.isNotEmpty()) {
                                    Text(
                                        text = flaw.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "(${flaw.value})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                if (isEditing) {
                                    IconButton(onClick = { onFlawClone(flaw) }) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = stringResource(R.string.action_duplicate),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { onFlawRemove(flaw.id) }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = stringResource(R.string.action_remove),
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddMeritDialog) {
            AddMeritDialog(
                onDismiss = { showAddMeritDialog = false },
                onMeritSelected = { merit ->
                    onMeritAdd(merit)
                    showAddMeritDialog = false
                }
            )
        }

        if (showAddFlawDialog) {
            AddFlawDialog(
                onDismiss = { showAddFlawDialog = false },
                onFlawSelected = { flaw ->
                    onFlawAdd(flaw)
                    showAddFlawDialog = false
                }
            )
        }

        viewingMerit?.let { merit ->
            ItemDetailPopup(
                title = merit.name,
                subtitle = stringResource(R.string.sheet_merits),
                details = listOf(
                    stringResource(R.string.compendium_cost) to "${merit.cost}",
                    stringResource(R.string.compendium_description) to merit.description
                ),
                onDismiss = { viewingMerit = null }
            )
        }

        viewingFlaw?.let { flaw ->
            ItemDetailPopup(
                title = flaw.name,
                subtitle = stringResource(R.string.sheet_flaws),
                details = listOf(
                    stringResource(R.string.compendium_cost) to "${flaw.value}",
                    stringResource(R.string.compendium_description) to flaw.description
                ),
                onDismiss = { viewingFlaw = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeritDialog(
    onDismiss: () -> Unit,
    onMeritSelected: (MeritValue) -> Unit
) {
    var expandedCategory by remember { mutableStateOf<MeritCategory?>(null) }
    val categories = MeritCategory.entries

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sheet_add_merit)) },
        text = {
            Column {
                categories.forEach { category ->
                    Text(
                        text = category.nameEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    MeritId.getByCategory(category).forEach { meritId ->
                        ListItem(
                            headlineContent = { Text(meritId.nameEn) },
                            supportingContent = { Text(meritId.descriptionEn, maxLines = 2) },
                            trailingContent = {
                                Text(
                                    text = "${meritId.cost}",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onMeritSelected(
                                        MeritValue(
                                            id = meritId.id,
                                            name = meritId.nameEn,
                                            cost = meritId.cost,
                                            description = meritId.descriptionEn
                                        )
                                    )
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlawDialog(
    onDismiss: () -> Unit,
    onFlawSelected: (FlawValue) -> Unit
) {
    val categories = FlawCategory.entries

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sheet_add_flaw)) },
        text = {
            Column {
                categories.forEach { category ->
                    Text(
                        text = category.nameEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    FlawId.getByCategory(category).forEach { flawId ->
                        ListItem(
                            headlineContent = { Text(flawId.nameEn) },
                            supportingContent = { Text(flawId.descriptionEn, maxLines = 2) },
                            trailingContent = {
                                Text(
                                    text = "${flawId.cost}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onFlawSelected(
                                        FlawValue(
                                            id = flawId.id,
                                            name = flawId.nameEn,
                                            value = flawId.cost,
                                            description = flawId.descriptionEn
                                        )
                                    )
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun EquipmentTab(
    character: Character,
    isEditing: Boolean,
    onEquipmentAdd: (com.v20charactermanager.domain.model.EquipmentItem) -> Unit,
    onEquipmentUpdate: (com.v20charactermanager.domain.model.EquipmentItem) -> Unit,
    onEquipmentRemove: (String) -> Unit,
    onEquipmentClone: (com.v20charactermanager.domain.model.EquipmentItem) -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<com.v20charactermanager.domain.model.EquipmentItem?>(null) }
    var viewingItem by remember { mutableStateOf<com.v20charactermanager.domain.model.EquipmentItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.sheet_equipment),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (isEditing) {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, stringResource(R.string.action_add))
                }
            }
        }

        if (character.equipment.isEmpty()) {
            Card {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.equipment_no_equipment),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            character.equipment.forEach { item ->
                Card(
                    modifier = Modifier.clickable { viewingItem = item }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.category.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (isEditing) {
                                Row {
                                    IconButton(onClick = { editingItem = item }) {
                                        Icon(Icons.Default.Edit, stringResource(R.string.action_edit), modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onEquipmentClone(item) }) {
                                        Icon(Icons.Default.ContentCopy, stringResource(R.string.action_duplicate), modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onEquipmentRemove(item.id) }) {
                                        Icon(Icons.Default.Close, stringResource(R.string.action_remove), modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            } else {
                                if (item.quantity > 1) {
                                    Text(
                                        text = stringResource(R.string.equipment_quantity_format, item.quantity),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        if (item.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        val details = mutableListOf<String>()
                        if (item.damage.isNotEmpty()) details.add(stringResource(R.string.equipment_dmg_prefix, item.damage))
                        if (item.size > 0) details.add(stringResource(R.string.equipment_size_prefix, item.size))
                        if (item.weight > 0) details.add(stringResource(R.string.equipment_wt_prefix, item.weight))
                        if (item.cost.isNotEmpty()) details.add(stringResource(R.string.equipment_cost_prefix, item.cost))
                        if (details.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = details.joinToString(" · "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (item.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEquipmentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { item ->
                onEquipmentAdd(item)
                showAddDialog = false
            }
        )
    }

    editingItem?.let { item ->
        AddEquipmentDialog(
            existingItem = item,
            onDismiss = { editingItem = null },
            onConfirm = { updated ->
                onEquipmentUpdate(updated)
                editingItem = null
            }
        )
    }

    viewingItem?.let { item ->
        ItemDetailPopup(
            title = item.name,
            subtitle = item.category.name,
            details = listOf(
                stringResource(R.string.equipment_description_label) to item.description,
                stringResource(R.string.equipment_quantity_label) to item.quantity.toString(),
                stringResource(R.string.equipment_damage_label) to item.damage,
                stringResource(R.string.equipment_size_label) to if (item.size > 0) item.size.toString() else "",
                stringResource(R.string.equipment_weight_label) to if (item.weight > 0) item.weight.toString() else "",
                stringResource(R.string.equipment_cost_label) to item.cost,
                stringResource(R.string.equipment_notes_label) to item.notes
            ),
            onDismiss = { viewingItem = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentDialog(
    existingItem: com.v20charactermanager.domain.model.EquipmentItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (com.v20charactermanager.domain.model.EquipmentItem) -> Unit
) {
    val isEdit = existingItem != null
    var name by remember { mutableStateOf(existingItem?.name ?: "") }
    var description by remember { mutableStateOf(existingItem?.description ?: "") }
    var quantity by remember { mutableStateOf((existingItem?.quantity ?: 1).toString()) }
    var category by remember { mutableStateOf(existingItem?.category ?: com.v20charactermanager.domain.model.EquipmentCategory.MISCELLANEOUS) }
    var damage by remember { mutableStateOf(existingItem?.damage ?: "") }
    var size by remember { mutableStateOf((existingItem?.size ?: 0).toString()) }
    var weight by remember { mutableStateOf((existingItem?.weight ?: 0.0).toString()) }
    var cost by remember { mutableStateOf(existingItem?.cost ?: "") }
    var notes by remember { mutableStateOf(existingItem?.notes ?: "") }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (isEdit) R.string.equipment_edit else R.string.equipment_add)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.equipment_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.equipment_description_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                    label = { Text(stringResource(R.string.equipment_quantity_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.equipment_category_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        com.v20charactermanager.domain.model.EquipmentCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = damage,
                    onValueChange = { damage = it },
                    label = { Text(stringResource(R.string.equipment_damage_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(stringResource(R.string.equipment_size_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(stringResource(R.string.equipment_weight_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text(stringResource(R.string.equipment_cost_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.equipment_notes_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            com.v20charactermanager.domain.model.EquipmentItem(
                                id = existingItem?.id ?: java.util.UUID.randomUUID().toString(),
                                name = name.trim(),
                                description = description.trim(),
                                quantity = quantity.toIntOrNull() ?: 1,
                                category = category,
                                damage = damage.trim(),
                                size = size.toIntOrNull() ?: 0,
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                cost = cost.trim(),
                                notes = notes.trim()
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(if (isEdit) stringResource(R.string.action_save) else stringResource(R.string.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun NotesTab(
    character: Character,
    onNotesChange: (String) -> Unit
) {
    var notesText by remember(character.notes) { mutableStateOf(character.notes) }
    var isDirty by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.sheet_notes),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = notesText,
            onValueChange = { newValue ->
                notesText = newValue
                isDirty = newValue != character.notes
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = { Text(stringResource(R.string.notes_character_notes)) },
            placeholder = { Text(stringResource(R.string.notes_placeholder)) },
            singleLine = false
        )

        V20BloodButton(
            text = stringResource(R.string.action_save),
            onClick = {
                onNotesChange(notesText)
                isDirty = false
            },
            enabled = isDirty,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ItemDetailPopup(
    title: String,
    subtitle: String = "",
    details: List<Pair<String, String>>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                details.forEach { (label, value) ->
                    if (value.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "$label: ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        }
    )
}
