package com.v20charactermanager.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.v20charactermanager.R
import com.v20charactermanager.data.di.AppContainer
import com.v20charactermanager.domain.model.LiveRoomState
import com.v20charactermanager.util.LocaleHelper
import com.v20charactermanager.domain.model.CharacterRandomizer
import com.v20charactermanager.domain.model.ChronicleLocation
import com.v20charactermanager.domain.model.MediaAsset
import com.v20charactermanager.domain.model.MediaAssetType
import com.v20charactermanager.domain.model.Visibility
import com.v20charactermanager.ui.compendium.CompendiumDetailScreen
import com.v20charactermanager.ui.chronicle.ChronicleDetailScreen
import com.v20charactermanager.ui.chronicle.ChronicleStorytellerScreen
import com.v20charactermanager.ui.chronicle.LocationImageScreen
import com.v20charactermanager.ui.chronicle.ChronicleListScreen
import com.v20charactermanager.ui.chronicle.ChronicleViewModel
import com.v20charactermanager.ui.chronicle.ChronicleViewModelFactory
import com.v20charactermanager.ui.chronicle.ImageViewerScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.v20charactermanager.ui.chronicle.DrawToolState
import com.v20charactermanager.ui.chronicle.toAnnotationType
import com.v20charactermanager.ui.chronicle.MediaLibraryScreen
import com.v20charactermanager.ui.chronicle.AudioViewModel
import com.v20charactermanager.ui.chronicle.AudioViewModelFactory
import com.v20charactermanager.ui.chronicle.MediaViewModel
import com.v20charactermanager.ui.chronicle.MediaViewModelFactory
import com.v20charactermanager.ui.chronicle.VersionHistoryScreen
import com.v20charactermanager.ui.chronicle.DocumentViewerScreen
import com.v20charactermanager.ui.chronicle.ChronicleSearchScreen
import com.v20charactermanager.ui.compendium.CompendiumScreen
import com.v20charactermanager.ui.compendium.CompendiumViewModel
import com.v20charactermanager.ui.compendium.CompendiumViewModelFactory
import com.v20charactermanager.ui.creation.CharacterCreationViewModel
import com.v20charactermanager.ui.creation.CharacterCreationViewModelFactory
import com.v20charactermanager.ui.creation.CreationScreen
import com.v20charactermanager.ui.dice.DiceScreen
import com.v20charactermanager.ui.dice.DiceViewModel
import com.v20charactermanager.ui.home.HomeScreen
import com.v20charactermanager.ui.home.HomeViewModel
import com.v20charactermanager.ui.home.HomeViewModelFactory
import com.v20charactermanager.ui.io.ImportExportScreen
import com.v20charactermanager.ui.io.ImportExportViewModel
import com.v20charactermanager.ui.io.ImportExportViewModelFactory
import com.v20charactermanager.ui.session.SessionScreen
import com.v20charactermanager.ui.session.SessionViewModel
import com.v20charactermanager.ui.session.SessionViewModelFactory
import com.v20charactermanager.ui.settings.SettingsScreen
import com.v20charactermanager.ui.settings.SettingsViewModel
import com.v20charactermanager.ui.settings.SettingsViewModelFactory
import com.v20charactermanager.ui.settings.HouseRulesScreen
import com.v20charactermanager.ui.settings.HouseRulesViewModel
import com.v20charactermanager.ui.settings.HouseRulesViewModelFactory
import com.v20charactermanager.ui.sheet.EditCharacterViewModel
import com.v20charactermanager.ui.sheet.EditCharacterViewModelFactory
import com.v20charactermanager.ui.sheet.SheetScreen
import com.v20charactermanager.ui.xp.XpSpendingScreen
import com.v20charactermanager.ui.xp.XpSpendingViewModel
import com.v20charactermanager.ui.xp.XpSpendingViewModelFactory
import kotlin.math.roundToInt

object Routes {
    const val HOME = "home"
    const val CREATION = "creation/{step}"
    const val SHEET = "sheet/{characterId}"
    const val SESSION = "session/{characterId}"
    const val DICE = "dice?pool={pool}"
    const val SETTINGS = "settings"
    const val COMPENDIUM = "compendium"
    const val COMPENDIUM_DETAIL = "compendium/detail/{itemId}"
    const val IMPORT_EXPORT = "import_export"
    const val XP_SPENDING = "xp_spending/{characterId}"
    const val CHRONICLES = "chronicles"
    const val CHRONICLE_DETAIL = "chronicle/{chronicleId}"
    const val MEDIA_LIBRARY = "chronicle/{chronicleId}/media"
    const val IMAGE_VIEWER = "chronicle/{chronicleId}/media/{assetId}"
    const val DOCUMENT_VIEWER = "chronicle/{chronicleId}/document/{assetId}"
    const val VIDEO_VIEWER = "chronicle/{chronicleId}/video/{assetId}"
    const val CHRONICLE_SEARCH = "chronicle/{chronicleId}/search"
    const val VERSION_HISTORY = "chronicle/{chronicleId}/media/{assetId}/versions"
    const val LOCATION_IMAGE = "chronicle/{chronicleId}/location/{locationId}/image"
    const val HOUSE_RULES = "house_rules/{chronicleId}"
    const val SESSION_RECAP = "session_recap/{sessionId}/{chronicleId}"
    const val LIVE_ROOM = "live_room/{chronicleId}/{asMaster}?host={host}&port={port}&playerName={playerName}&characterId={characterId}"
    const val SELECT_CHARACTER = "select_character?host={host}&port={port}&roomName={roomName}&masterName={masterName}"
    const val FIND_TABLE = "find_table"
    const val CRASH_LOGS = "crash_logs"

    fun xpSpending(characterId: String) = "xp_spending/$characterId"

    fun creationStep(step: Int) = "creation/$step"
    fun sheet(characterId: String) = "sheet/$characterId"
    fun session(characterId: String) = "session/$characterId"
    fun dice(pool: Int? = null) = if (pool != null) "dice?pool=$pool" else "dice"
    fun compendiumDetail(itemId: String) = "compendium/detail/$itemId"
    fun chronicleDetail(chronicleId: String) = "chronicle/$chronicleId"
    fun mediaLibrary(chronicleId: String) = "chronicle/$chronicleId/media"
    fun imageViewer(chronicleId: String, assetId: String) = "chronicle/$chronicleId/media/$assetId"
    fun documentViewer(chronicleId: String, assetId: String) = "chronicle/$chronicleId/document/$assetId"
    fun videoViewer(chronicleId: String, assetId: String) = "chronicle/$chronicleId/video/$assetId"
    fun chronicleSearch(chronicleId: String) = "chronicle/$chronicleId/search"
    fun versionHistory(chronicleId: String, assetId: String) = "chronicle/$chronicleId/media/$assetId/versions"
    fun locationImage(chronicleId: String, locationId: String) = "chronicle/$chronicleId/location/$locationId/image"
    fun houseRules(chronicleId: String) = "house_rules/$chronicleId"
    fun sessionRecap(sessionId: String, chronicleId: String) = "session_recap/$sessionId/$chronicleId"
    fun liveRoom(chronicleId: String, asMaster: Boolean = true, host: String = "", port: Int = 0, playerName: String = "", characterId: String = ""): String {
        val cid = chronicleId.ifEmpty { "_join" }
        val safeName = playerName.replace(" ", "%20")
        return "live_room/$cid/$asMaster?host=$host&port=$port&playerName=$safeName&characterId=$characterId"
    }
    fun selectCharacter(host: String, port: Int, roomName: String, masterName: String) =
        "select_character?host=$host&port=$port&roomName=${roomName.replace(" ", "%20")}&masterName=${masterName.replace(" ", "%20")}"
    fun crashLogs() = "crash_logs"
}

@Composable
fun V20NavGraph(
    navController: NavHostController,
    appContainer: AppContainer
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(appContainer.characterRepository)
    )

    // LiveRoomViewModel scoped to entire NavHost (survives navigation)
    val liveRoomViewModel: com.v20charactermanager.ui.liveroom.LiveRoomViewModel = viewModel(
        factory = com.v20charactermanager.ui.liveroom.LiveRoomViewModelFactory(
            context.applicationContext as android.app.Application,
            appContainer.mediaRepository,
            appContainer.characterRepository
        )
    )
    val liveRoomState by liveRoomViewModel.uiState.collectAsState()

    Box {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME
        ) {
            composable(Routes.HOME) {
                val uiState by homeViewModel.uiState.collectAsState()

            HomeScreen(
                uiState = uiState,
                onCharacterClick = { characterId ->
                    navController.navigate(Routes.sheet(characterId))
                },
                onCreateClick = {
                    navController.navigate(Routes.creationStep(1))
                },
                onDeleteClick = { characterId ->
                    homeViewModel.deleteCharacter(characterId)
                },
                onDuplicateClick = { characterId ->
                    homeViewModel.duplicateCharacter(characterId)
                },
                onCompendiumClick = {
                    navController.navigate(Routes.COMPENDIUM)
                },
                onDiceClick = {
                    navController.navigate(Routes.dice())
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onRandomCharacterClick = {
                    val randomCharacter = CharacterRandomizer.randomize()
                    homeViewModel.saveCharacter(randomCharacter)
                    navController.navigate(Routes.sheet(randomCharacter.id))
                },
                onChronicleClick = {
                    navController.navigate(Routes.CHRONICLES)
                }
            )
        }
        composable(
            route = Routes.CREATION,
            arguments = listOf(navArgument("step") { type = NavType.IntType })
        ) { backStackEntry ->
            val step = backStackEntry.arguments?.getInt("step") ?: 1
            val viewModel: CharacterCreationViewModel = viewModel(
                factory = CharacterCreationViewModelFactory(appContainer.characterRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(step) {
                viewModel.goToStep(step)
            }

            LaunchedEffect(uiState.saved) {
                if (uiState.saved) {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            }

            CreationScreen(
                uiState = uiState,
                onIdentityChange = { identity ->
                    viewModel.updateIdentity(identity)
                },
                onAttributeChange = { attributeId, value ->
                    viewModel.updateAttribute(attributeId, value)
                },
                onAbilityChange = { abilityId, value ->
                    viewModel.updateAbility(abilityId, value)
                },
                onDisciplineAdd = { disciplineId, value ->
                    viewModel.addDiscipline(disciplineId, value)
                },
                onDisciplineUpdate = { disciplineId, value ->
                    viewModel.updateDiscipline(disciplineId, value)
                },
                onDisciplineRemove = { disciplineId ->
                    viewModel.removeDiscipline(disciplineId)
                },
                onBackgroundAdd = { backgroundId, value ->
                    viewModel.addBackground(backgroundId, value)
                },
                onBackgroundUpdate = { backgroundId, value ->
                    viewModel.updateBackground(backgroundId, value)
                },
                onBackgroundRemove = { backgroundId ->
                    viewModel.removeBackground(backgroundId)
                },
                onVirtueChange = { virtueId, value ->
                    viewModel.updateVirtue(virtueId, value)
                },
                onMeritAdd = { merit ->
                    viewModel.addMerit(merit)
                },
                onMeritRemove = { meritId ->
                    viewModel.removeMerit(meritId)
                },
                onFlawAdd = { flaw ->
                    viewModel.addFlaw(flaw)
                },
                onFlawRemove = { flawId ->
                    viewModel.removeFlaw(flawId)
                },
                onNextStep = {
                    viewModel.nextStep()
                },
                onPreviousStep = {
                    viewModel.previousStep()
                },
                onSave = {
                    viewModel.saveCharacter()
                },
                onBack = {
                    navController.popBackStack()
                },
                onConfirmWarnings = {
                    viewModel.confirmWarnings()
                },
                onDismissWarnings = {
                    viewModel.dismissWarnings()
                }
            )
        }
        composable(
            route = Routes.SHEET,
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            val editViewModel: EditCharacterViewModel = viewModel(
                factory = EditCharacterViewModelFactory(appContainer.characterRepository)
            )
            val uiState by editViewModel.uiState.collectAsState()
            val sharedCharacters by liveRoomViewModel.sharedCharacters.collectAsState()
            var sheetUnavailable by remember { mutableStateOf(false) }

            LaunchedEffect(characterId) {
                editViewModel.loadCharacter(characterId)
                if (liveRoomViewModel.uiState.value.isMaster) {
                    liveRoomViewModel.requestSharedCharacter(characterId)
                }
                kotlinx.coroutines.delay(5000)
                if (editViewModel.uiState.value.character == null &&
                    liveRoomViewModel.sharedCharacters.value[characterId] == null
                ) {
                    sheetUnavailable = true
                }
            }

            val localCharacter = uiState.character
            val displayCharacter = localCharacter ?: sharedCharacters[characterId]
            val isLocalChar = localCharacter != null

            when {
                displayCharacter != null -> {
                SheetScreen(
                    character = displayCharacter,
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSession = {
                        if (isLocalChar) navController.navigate(Routes.session(characterId))
                    },
                    onNotesChange = { notes ->
                        if (isLocalChar) editViewModel.updateNotes(notes)
                    },
                    onNavigateToDice = { pool ->
                        navController.navigate(Routes.dice(pool))
                    },
                    isEditing = isLocalChar && uiState.isEditing,
                    onToggleEdit = { if (isLocalChar) editViewModel.startEditing() },
                    onSave = { if (isLocalChar) editViewModel.save() },
                    onCancelEdit = { if (isLocalChar) editViewModel.cancelEditing() },
                    onAttributeChange = { attributeId, value ->
                        if (isLocalChar) editViewModel.updateAttribute(attributeId, value)
                    },
                    onAbilityChange = { abilityId, value ->
                        if (isLocalChar) editViewModel.updateAbility(abilityId, value)
                    },
                    onDisciplineValueChange = { disciplineId, value ->
                        if (isLocalChar) editViewModel.updateDisciplineValue(disciplineId, value)
                    },
                    onDisciplineRemove = { disciplineId ->
                        if (isLocalChar) editViewModel.removeDiscipline(disciplineId)
                    },
                    onBackgroundValueChange = { backgroundId, value ->
                        if (isLocalChar) editViewModel.updateBackgroundValue(backgroundId, value)
                    },
                    onBackgroundRemove = { backgroundId ->
                        if (isLocalChar) editViewModel.removeBackground(backgroundId)
                    },
                    onVirtueChange = { virtueId, value ->
                        if (isLocalChar) editViewModel.updateVirtue(virtueId, value)
                    },
                    onPortraitChange = { portraitUri ->
                        if (isLocalChar) editViewModel.updatePortrait(portraitUri)
                    },
                    onMeritAdd = { merit ->
                        if (isLocalChar) editViewModel.addMerit(merit)
                    },
                    onMeritRemove = { meritId ->
                        if (isLocalChar) editViewModel.removeMerit(meritId)
                    },
                    onMeritClone = { merit ->
                        if (isLocalChar) editViewModel.cloneMerit(merit)
                    },
                    onFlawAdd = { flaw ->
                        if (isLocalChar) editViewModel.addFlaw(flaw)
                    },
                    onFlawRemove = { flawId ->
                        if (isLocalChar) editViewModel.removeFlaw(flawId)
                    },
                    onFlawClone = { flaw ->
                        if (isLocalChar) editViewModel.cloneFlaw(flaw)
                    },
                    onEquipmentAdd = { item ->
                        if (isLocalChar) editViewModel.addEquipment(item)
                    },
                    onEquipmentUpdate = { item ->
                        if (isLocalChar) editViewModel.updateEquipment(item)
                    },
                    onEquipmentRemove = { itemId ->
                        if (isLocalChar) editViewModel.removeEquipment(itemId)
                    },
                    onEquipmentClone = { item ->
                        if (isLocalChar) editViewModel.cloneEquipment(item)
                    },
                    onNavigateToXpSpending = {
                        if (isLocalChar) navController.navigate(Routes.xpSpending(characterId))
                    }
                )
                }
                sheetUnavailable -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1A1A2E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.sheet_unavailable),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1A1A2E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFFD4A847))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.sheet_loading),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
        composable(
            route = Routes.SESSION,
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            val viewModel: SessionViewModel = viewModel(
                factory = SessionViewModelFactory(appContainer.characterRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(characterId) {
                viewModel.loadCharacter(characterId)
            }

            uiState.character?.let { character ->
                SessionScreen(
                    character = character,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSpendBlood = { amount ->
                        viewModel.spendBlood(amount)
                    },
                    onRefillBlood = { amount ->
                        viewModel.refillBlood(amount)
                    },
                    onSpendWillpower = { amount ->
                        viewModel.spendWillpower(amount)
                    },
                    onRecoverWillpower = { amount ->
                        viewModel.recoverWillpower(amount)
                    },
                    onApplyDamage = { index, type ->
                        viewModel.applyDamage(index, type)
                    },
                    onHealDamage = { index ->
                        viewModel.healDamage(index)
                    },
                    onEarnExperience = { amount ->
                        viewModel.earnExperience(amount)
                    },
                    onSpendExperience = { amount ->
                        viewModel.spendExperience(amount)
                    }
                )
            }
        }
        composable(
            route = Routes.DICE,
            arguments = listOf(navArgument("pool") { type = NavType.IntType; defaultValue = 5 })
        ) { backStackEntry ->
            val pool = backStackEntry.arguments?.getInt("pool") ?: 5
            val viewModel: DiceViewModel = viewModel()
            LaunchedEffect(pool) {
                viewModel.updatePool(pool)
            }
            DiceScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(appContainer.settingsRepository, appContainer.chronicleRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            SettingsScreen(
                currentLanguage = uiState.language,
                onLanguageChange = { language ->
                    viewModel.setLanguage(language)
                    LocaleHelper.setLanguage(context, language)
                },
                onBack = {
                    navController.popBackStack()
                },
                onImportExportClick = {
                    navController.navigate(Routes.IMPORT_EXPORT)
                },
                onHouseRulesClick = { chronicleId ->
                    navController.navigate(Routes.houseRules(chronicleId))
                },
                onCrashLogsClick = {
                    navController.navigate(Routes.crashLogs())
                },
                chronicles = uiState.chronicles.map { it.id to it.name }
            )
        }
        composable(
            route = Routes.HOUSE_RULES,
            arguments = listOf(navArgument("chronicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: return@composable
            val viewModel: HouseRulesViewModel = viewModel(
                factory = HouseRulesViewModelFactory(appContainer.houseRuleRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(chronicleId) {
                viewModel.loadRules(chronicleId)
            }

            HouseRulesScreen(
                uiState = uiState,
                onUpdateRules = { viewModel.updateRules(it) },
                onSave = { viewModel.save() },
                onResetDefaults = { viewModel.resetToDefaults() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.COMPENDIUM) {
            val viewModel: CompendiumViewModel = viewModel(
                factory = CompendiumViewModelFactory(appContainer.ruleRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            CompendiumScreen(
                uiState = uiState,
                onCategorySelected = { viewModel.selectCategory(it) },
                onSearchQueryChanged = { viewModel.updateSearchQuery(it) },
                onItemClick = { item ->
                    viewModel.selectItem(item)
                    navController.navigate(Routes.compendiumDetail(item.id))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.COMPENDIUM_DETAIL,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            val viewModel: CompendiumViewModel = viewModel(
                factory = CompendiumViewModelFactory(appContainer.ruleRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            val selectedItem = uiState.items.find { it.id == itemId }
            selectedItem?.let { item ->
                CompendiumDetailScreen(
                    item = item,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        }
        composable(Routes.IMPORT_EXPORT) {
            val viewModel: ImportExportViewModel = viewModel(
                factory = ImportExportViewModelFactory(appContainer.characterRepository, context)
            )
            val uiState by viewModel.uiState.collectAsState()

            ImportExportScreen(
                uiState = uiState,
                onBack = {
                    navController.popBackStack()
                },
                onImportUri = { uri ->
                    viewModel.importFromUri(uri)
                },
                onExportCharacter = { character, uri ->
                    viewModel.exportCharacter(character, uri)
                },
                onShareCharacter = { character ->
                    val shareIntent = viewModel.createShareIntent(character)
                    context.startActivity(Intent.createChooser(shareIntent, "Share V20 Character"))
                },
                onSaveAsCopy = {
                    viewModel.saveImportedAsCopy()
                },
                onReplaceExisting = {
                    viewModel.replaceExisting()
                },
                onResetState = {
                    viewModel.resetState()
                },
                onImportEquipmentLibrary = { uri ->
                    viewModel.importEquipmentLibrary(uri)
                },
                onImportEquipmentToCharacter = { characterId ->
                    viewModel.importEquipmentToCharacter(characterId)
                },
                onExportEquipmentLibrary = { items, name, uri ->
                    viewModel.exportEquipmentLibrary(items, name, uri)
                }
            )
        }
        composable(
            route = Routes.XP_SPENDING,
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            val viewModel: XpSpendingViewModel = viewModel(
                factory = XpSpendingViewModelFactory(appContainer.characterRepository)
            )

            LaunchedEffect(characterId) {
                viewModel.loadCharacter(characterId)
            }

            XpSpendingScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.CHRONICLES) {
            val viewModel: ChronicleViewModel = viewModel(
                factory = ChronicleViewModelFactory(appContainer.chronicleRepository, appContainer.characterRepository)
            )
            val uiState by viewModel.listUiState.collectAsState()

            ChronicleListScreen(
                uiState = uiState,
                onChronicleClick = { chronicleId ->
                    navController.navigate(Routes.chronicleDetail(chronicleId))
                },
                onCreateChronicle = { name, description, storytellerName, role ->
                    viewModel.createChronicle(name, description, storytellerName, role)
                },
                onDeleteChronicle = { chronicleId ->
                    viewModel.deleteChronicle(chronicleId)
                },
                onFindTable = {
                    navController.navigate(Routes.FIND_TABLE)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.CHRONICLE_DETAIL,
            arguments = listOf(navArgument("chronicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: ""
            val viewModel: ChronicleViewModel = viewModel(
                factory = ChronicleViewModelFactory(appContainer.chronicleRepository, appContainer.characterRepository)
            )
            val uiState by viewModel.detailUiState.collectAsState()

            val audioViewModel: AudioViewModel = viewModel(
                factory = AudioViewModelFactory(
                    appContainer.audioRepository,
                    context.applicationContext
                )
            )

            LaunchedEffect(chronicleId) {
                viewModel.loadChronicleDetail(chronicleId)
                audioViewModel.loadTracks(chronicleId)
            }

            ChronicleStorytellerScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onStartSession = { session ->
                    viewModel.startSession(session)
                },
                onEndSession = { session ->
                    viewModel.endSession(session)
                },
                onCharacterClick = { characterId ->
                    navController.navigate(Routes.sheet(characterId))
                },
                onCharacterBloodChange = { character, delta ->
                    viewModel.updateCharacterBloodPool(character.id, delta)
                },
                onCharacterWillpowerChange = { character, delta ->
                    viewModel.updateCharacterWillpower(character.id, delta)
                },
                onCharacterHealthChange = { character, delta ->
                    viewModel.updateCharacterHealth(character.id, delta)
                },
                onNpcClick = { npc ->
                    // TODO: Open NPC detail
                },
                onOpenScene = { scene ->
                    // TODO: Open scene detail
                },
                onChangeScene = { sceneId ->
                    uiState.activeSession?.let { session ->
                        viewModel.setActiveScene(session, sceneId)
                    }
                },
                onDiceClick = {
                    navController.navigate(Routes.dice())
                },
                onQuickNote = { text ->
                    uiState.chronicle?.let { chronicle ->
                        chronicle.id.let { cId ->
                            viewModel.createQuickNote(cId, text)
                        }
                    }
                },
                onEventClick = { title, desc ->
                    uiState.activeSession?.let { session ->
                        viewModel.createSessionEvent(session.chronicleId, session.id, title, desc)
                    }
                },
                onMediaClick = {
                    uiState.chronicle?.let { chronicle ->
                        navController.navigate(Routes.mediaLibrary(chronicle.id))
                    }
                },
                onOpenMediaLibrary = { chronicleId ->
                    navController.navigate(Routes.mediaLibrary(chronicleId))
                },
                onTabSelected = { viewModel.selectTab(it) },
                onCreateSession = { cId, title ->
                    viewModel.createSession(cId, title)
                },
                onUpdateSession = { session ->
                    viewModel.updateSession(session)
                },
                onDeleteSession = { sessionId ->
                    viewModel.deleteSession(sessionId)
                },
                onCreateNpc = { cId, name, creatureType, role, charId ->
                    viewModel.createNpc(cId, name, creatureType, role, charId)
                },
                onDeleteNpc = { npcId ->
                    viewModel.deleteNpc(npcId)
                },
                onUpdateNpc = { npc ->
                    viewModel.updateNpc(npc)
                },
                onCreatePlotArc = { cId, title, type ->
                    viewModel.createPlotArc(cId, title, type)
                },
                onDeletePlotArc = { plotId ->
                    viewModel.deletePlotArc(plotId)
                },
                onUpdatePlotArc = { plotArc ->
                    viewModel.updatePlotArc(plotArc)
                },
                onCreateNote = { cId, text ->
                    viewModel.createChronicleNote(cId, text)
                },
                onUpdateNote = { note ->
                    viewModel.updateChronicleNote(note)
                },
                onDeleteNote = { noteId ->
                    viewModel.deleteChronicleNote(noteId)
                },
                onCreateCharacterNote = { cId, charId, text ->
                    viewModel.createCharacterNote(cId, charId, text)
                },
                onUpdateCharacterNote = { note ->
                    viewModel.updateCharacterNote(note)
                },
                onDeleteCharacterNote = { noteId ->
                    viewModel.deleteCharacterNote(noteId)
                },
                onUpdateChronicle = { chronicle ->
                    viewModel.updateChronicle(chronicle)
                },
                onCreateScene = { cId, title ->
                    viewModel.createScene(cId, title)
                },
                onCreateLocation = { cId, name ->
                    viewModel.createLocation(cId, name)
                },
                onDeleteLocation = { locationId ->
                    viewModel.deleteLocation(locationId)
                },
                onUpdateLocation = { location ->
                    viewModel.updateLocation(location)
                },
                onLocationImageClick = { chronicleId, locationId ->
                    navController.navigate(Routes.locationImage(chronicleId, locationId))
                },
                onCreateFaction = { cId, name ->
                    viewModel.createFaction(cId, name)
                },
                onDeleteFaction = { factionId ->
                    viewModel.deleteFaction(factionId)
                },
                onUpdateFaction = { faction ->
                    viewModel.updateFaction(faction)
                },
                onCreateSecret = { cId, title, content ->
                    viewModel.createSecret(cId, title, content)
                },
                onDeleteSecret = { secretId ->
                    viewModel.deleteSecret(secretId)
                },
                onUpdateSecret = { secret ->
                    viewModel.updateSecret(secret)
                },
                onCreateClue = { cId, title, content ->
                    viewModel.createClue(cId, title, content)
                },
                onDeleteClue = { clueId ->
                    viewModel.deleteClue(clueId)
                },
                onUpdateClue = { clue ->
                    viewModel.updateClue(clue)
                },
                onCreateEvent = { cId, title ->
                    viewModel.createEvent(cId, title)
                },
                onDeleteEvent = { eventId ->
                    viewModel.deleteEvent(eventId)
                },
                onUpdateEvent = { event ->
                    viewModel.updateEvent(event)
                },
                onAddCharacter = { cId, charId, role ->
                    viewModel.addCharacterToChronicle(cId, charId, role)
                },
                onRemoveCharacter = { cId, charId ->
                    viewModel.removeCharacterFromChronicle(cId, charId)
                },
                onNavigateToDice = {
                    navController.navigate(Routes.dice())
                },
                onLinkClick = { type, id ->
                    when (type) {
                        "PG" -> navController.navigate(Routes.sheet(id))
                        "NPC" -> { /* NPC detail is opened via onNpcClick in People tab */ }
                        "LUOGHI" -> {
                            uiState.chronicle?.let { chronicle ->
                                navController.navigate(Routes.locationImage(chronicle.id, id))
                            }
                        }
                        "SEGRETI" -> { /* No dedicated screen — user finds in Plots tab */ }
                        "INDIZI" -> { /* No dedicated screen — user finds in Plots tab */ }
                        "NOTE" -> { /* Note stays in place */ }
                        "SESSIONI" -> { /* No dedicated screen — user finds in More > Sessions */ }
                        "FAZIONI" -> { /* No dedicated screen — user finds in More > Factions */ }
                        "EVENTI" -> { /* No dedicated screen — user finds in Plots tab */ }
                        "SCENE" -> { /* No dedicated screen — user finds in Plots tab */ }
                    }
                },
                onSearchClick = {
                    uiState.chronicle?.let { chronicle ->
                        navController.navigate(Routes.chronicleSearch(chronicle.id))
                    }
                },
                audioViewModel = audioViewModel,
                onViewRecap = { sessionId, chronicleId ->
                    navController.navigate(Routes.sessionRecap(sessionId, chronicleId))
                },
                onCloneSession = { session ->
                    viewModel.cloneSession(session)
                },
                onLiveRoom = {
                    navController.navigate(Routes.liveRoom(chronicleId, asMaster = true))
                },
                onJoinLiveRoom = {
                    navController.navigate(Routes.liveRoom(chronicleId, asMaster = false))
                }
            )
        }

        composable(
            route = Routes.MEDIA_LIBRARY,
            arguments = listOf(navArgument("chronicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: return@composable
            val mediaViewModel: MediaViewModel = viewModel(
                factory = MediaViewModelFactory(
                    appContainer.mediaRepository,
                    context.applicationContext
                )
            )
            val uiState by mediaViewModel.libraryUiState.collectAsState()
            LaunchedEffect(chronicleId) { mediaViewModel.loadAssets(chronicleId) }

            val pickImageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    mediaViewModel.importImage(chronicleId, it, "Image", MediaAssetType.OTHER, Visibility.GM_ONLY)
                }
            }

            val pickDocumentLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    val title = uri.lastPathSegment?.substringAfterLast('/') ?: "Document"
                    mediaViewModel.importDocument(chronicleId, it, title)
                }
            }

            val pickVideoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    val title = uri.lastPathSegment?.substringAfterLast('/') ?: "Video"
                    mediaViewModel.importVideo(chronicleId, it, title)
                }
            }

            MediaLibraryScreen(
                chronicleId = chronicleId,
                assets = uiState.assets,
                availableTags = uiState.availableTags,
                selectedTag = uiState.selectedTag,
                onImportImage = { pickImageLauncher.launch("image/*") },
                onImportDocument = { pickDocumentLauncher.launch("application/pdf") },
                onImportVideo = { pickVideoLauncher.launch("video/*") },
                onAssetClick = { asset ->
                    when (asset.type) {
                        MediaAssetType.DOCUMENT -> navController.navigate(Routes.documentViewer(chronicleId, asset.id))
                        MediaAssetType.VIDEO -> {
                            val intent = android.content.Intent(
                                navController.context,
                                com.v20charactermanager.ui.chronicle.VideoPlayerActivity::class.java
                            ).apply {
                                putExtra(com.v20charactermanager.ui.chronicle.VideoPlayerActivity.EXTRA_FILE_PATH, asset.originalFilePath)
                                putExtra(com.v20charactermanager.ui.chronicle.VideoPlayerActivity.EXTRA_TITLE, asset.title)
                            }
                            navController.context.startActivity(intent)
                        }
                        else -> navController.navigate(Routes.imageViewer(chronicleId, asset.id))
                    }
                },
                onAssetDelete = { mediaViewModel.deleteAsset(it) },
                onAssetRename = { assetId, newTitle -> mediaViewModel.renameAsset(assetId, newTitle) },
                onAssetTagAdd = { assetId, tag -> mediaViewModel.addTagToAsset(assetId, tag) },
                onAssetTagRemove = { assetId, tag -> mediaViewModel.removeTagFromAsset(assetId, tag) },
                onFilterByTag = { tag -> mediaViewModel.filterByTag(tag) },
                message = uiState.message,
                onClearMessage = { mediaViewModel.clearMessage() },
                errorType = uiState.errorType,
                errorDetails = uiState.errorDetails,
                onClearError = { mediaViewModel.clearError() },
                onVideoPresent = { asset ->
                    val intent = android.content.Intent(
                        navController.context,
                        com.v20charactermanager.ui.chronicle.VideoPresentationActivity::class.java
                    ).apply {
                        putExtra(com.v20charactermanager.ui.chronicle.VideoPresentationActivity.EXTRA_FILE_PATH, asset.originalFilePath)
                        putExtra(com.v20charactermanager.ui.chronicle.VideoPresentationActivity.EXTRA_TITLE, asset.title)
                    }
                    navController.context.startActivity(intent)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.IMAGE_VIEWER,
            arguments = listOf(
                navArgument("chronicleId") { type = NavType.StringType },
                navArgument("assetId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: return@composable
            val mediaViewModel: MediaViewModel = viewModel(
                factory = MediaViewModelFactory(
                    appContainer.mediaRepository,
                    context.applicationContext
                )
            )
            val uiState by mediaViewModel.viewerUiState.collectAsState()
            LaunchedEffect(assetId) { mediaViewModel.loadAssetForViewing(assetId) }

            uiState.asset?.let { asset ->
                var drawToolState by remember { mutableStateOf(DrawToolState()) }
                val docId = uiState.document?.id ?: ""
                val mediaId = asset.id

                ImageViewerScreen(
                    mediaAsset = asset,
                    annotations = uiState.annotations,
                    layers = uiState.layers,
                    toolState = drawToolState,
                    canUndo = uiState.undoStack.isNotEmpty(),
                    canRedo = uiState.redoStack.isNotEmpty(),
                    isDrawingEnabled = uiState.isDrawingEnabled,
                    activeLayerId = uiState.activeLayerId,
                    onBack = { navController.popBackStack() },
                    onToggleLayers = { },
                    onToggleDrawing = { mediaViewModel.toggleDrawingMode() },
                    onTogglePresentation = { },
                    onToolChange = { tool ->
                        drawToolState = drawToolState.copy(tool = tool)
                        mediaViewModel.selectAnnotationTool(tool.toAnnotationType())
                    },
                    onColorChange = { color -> drawToolState = drawToolState.copy(color = color) },
                    onStrokeWidthChange = { w -> drawToolState = drawToolState.copy(strokeWidth = w) },
                    onUndo = { mediaViewModel.undo() },
                    onRedo = { mediaViewModel.redo() },
                    onSave = {
                        mediaViewModel.saveRevision(
                            imageDocumentId = docId,
                            mediaAssetId = mediaId
                        )
                    },
                    onClearLayer = { mediaViewModel.clearActiveLayerAnnotations() },
                    onStrokeComplete = { annotation ->
                        mediaViewModel.addAnnotation(
                            annotation.copy(
                                imageDocumentId = docId,
                                layerId = uiState.activeLayerId ?: annotation.layerId
                            )
                        )
                    },
                    onLayerTap = { layerId -> mediaViewModel.setActiveLayer(layerId) },
                    onNavigateToHistory = {
                        val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: return@ImageViewerScreen
                        navController.navigate(Routes.versionHistory(chronicleId, assetId))
                    }
                )
            }
        }

        composable(
            route = Routes.VERSION_HISTORY,
            arguments = listOf(
                navArgument("chronicleId") { type = NavType.StringType },
                navArgument("assetId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: return@composable
            val mediaViewModel: MediaViewModel = viewModel(
                factory = MediaViewModelFactory(
                    appContainer.mediaRepository,
                    context.applicationContext
                )
            )
            val uiState by mediaViewModel.viewerUiState.collectAsState()
            LaunchedEffect(assetId) { mediaViewModel.loadAssetForViewing(assetId) }

            VersionHistoryScreen(
                revisions = uiState.revisions,
                onRestore = { revision ->
                    mediaViewModel.restoreRevision(revision)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DOCUMENT_VIEWER,
            arguments = listOf(
                navArgument("chronicleId") { type = NavType.StringType },
                navArgument("assetId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: return@composable
            val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: return@composable
            val mediaViewModel: MediaViewModel = viewModel(
                factory = MediaViewModelFactory(
                    appContainer.mediaRepository,
                    context.applicationContext
                )
            )
            val uiState by mediaViewModel.libraryUiState.collectAsState()
            LaunchedEffect(chronicleId) { mediaViewModel.loadAssets(chronicleId) }
            val asset = uiState.assets.find { it.id == assetId }

            DocumentViewerScreen(
                asset = asset,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.CHRONICLE_SEARCH,
            arguments = listOf(navArgument("chronicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: return@composable
            val viewModel: ChronicleViewModel = viewModel(
                factory = ChronicleViewModelFactory(appContainer.chronicleRepository, appContainer.characterRepository)
            )
            val uiState by viewModel.detailUiState.collectAsState()
            LaunchedEffect(chronicleId) { viewModel.loadChronicleDetail(chronicleId) }

            ChronicleSearchScreen(
                uiState = uiState,
                onEntityClick = { type, id ->
                    when (type) {
                        "PG" -> navController.navigate(Routes.sheet(id))
                        else -> { /* future: open entity detail */ }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.LOCATION_IMAGE,
            arguments = listOf(
                navArgument("chronicleId") { type = NavType.StringType },
                navArgument("locationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: return@composable
            val locationId = backStackEntry.arguments?.getString("locationId") ?: return@composable

            val mediaViewModel: MediaViewModel = viewModel(
                factory = MediaViewModelFactory(
                    appContainer.mediaRepository,
                    context.applicationContext
                )
            )
            val viewerState by mediaViewModel.viewerUiState.collectAsState()

            var location by remember { mutableStateOf<ChronicleLocation?>(null) }
            var linkedAsset by remember { mutableStateOf<MediaAsset?>(null) }

            val chronicleViewModel: ChronicleViewModel = viewModel(
                factory = ChronicleViewModelFactory(appContainer.chronicleRepository, appContainer.characterRepository)
            )
            val chronicleUiState by chronicleViewModel.detailUiState.collectAsState()

            LaunchedEffect(chronicleId) {
                chronicleViewModel.loadChronicleDetail(chronicleId)
            }

            LaunchedEffect(chronicleId, locationId) {
                location = chronicleUiState.locations.find { it.id == locationId }
                mediaViewModel.findAssetForLocation(chronicleId, locationId) { asset ->
                    linkedAsset = asset
                    asset?.let { mediaViewModel.loadAssetForViewing(it.id) }
                }
            }

            var drawToolState by remember { mutableStateOf(DrawToolState()) }

            location?.let { loc ->
                LocationImageScreen(
                    location = loc,
                    linkedAsset = linkedAsset,
                    document = viewerState.document,
                    layers = viewerState.layers,
                    annotations = viewerState.annotations,
                    toolState = drawToolState,
                    canUndo = viewerState.undoStack.isNotEmpty(),
                    canRedo = viewerState.redoStack.isNotEmpty(),
                    isDrawingEnabled = viewerState.isDrawingEnabled,
                    activeLayerId = viewerState.activeLayerId,
                    onBack = { navController.popBackStack() },
                    onImportImage = { uri ->
                        mediaViewModel.importImageForLocation(chronicleId, locationId, uri, loc.name)
                    },
                    onToggleDrawing = { mediaViewModel.toggleDrawingMode() },
                    onToolChange = { tool ->
                        drawToolState = drawToolState.copy(tool = tool)
                        mediaViewModel.selectAnnotationTool(tool.toAnnotationType())
                    },
                    onColorChange = { color -> drawToolState = drawToolState.copy(color = color) },
                    onStrokeWidthChange = { w -> drawToolState = drawToolState.copy(strokeWidth = w) },
                    onUndo = { mediaViewModel.undo() },
                    onRedo = { mediaViewModel.redo() },
                    onSave = {
                        val docId = viewerState.document?.id ?: return@LocationImageScreen
                        val assetId = linkedAsset?.id ?: return@LocationImageScreen
                        mediaViewModel.saveRevision(
                            imageDocumentId = docId,
                            mediaAssetId = assetId
                        )
                    },
                    onClearLayer = { mediaViewModel.clearActiveLayerAnnotations() },
                    onStrokeComplete = { annotation ->
                        val docId = viewerState.document?.id ?: return@LocationImageScreen
                        mediaViewModel.saveAnnotationImmediate(
                            annotation.copy(
                                imageDocumentId = docId,
                                layerId = viewerState.activeLayerId ?: annotation.layerId
                            )
                        )
                    },
                    onLayerTap = { layerId -> mediaViewModel.setActiveLayer(layerId) },
                    onDeleteImage = {
                        linkedAsset?.let { asset ->
                            mediaViewModel.deleteAsset(asset.id)
                            linkedAsset = null
                        }
                    }
                )
            }
        }

        composable(
            route = Routes.SESSION_RECAP,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("chronicleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: return@composable
            val recapViewModel: ChronicleViewModel = viewModel(
                factory = ChronicleViewModelFactory(appContainer.chronicleRepository, appContainer.characterRepository)
            )
            val session by recapViewModel.getSession(sessionId).collectAsState(initial = null)
            val sessionEvents by recapViewModel.getSessionEvents(sessionId).collectAsState(initial = emptyList())
            val detailState by recapViewModel.detailUiState.collectAsState()

            LaunchedEffect(chronicleId) {
                recapViewModel.loadChronicleDetail(chronicleId)
            }

            session?.let { s ->
                com.v20charactermanager.ui.session.SessionRecapScreen(
                    session = s,
                    events = sessionEvents,
                    characters = detailState.availableCharacters,
                    npcs = detailState.npcs,
                    scenes = detailState.scenes,
                    onBack = { navController.popBackStack() },
                    onCloneSession = {
                        recapViewModel.cloneSession(s)
                        navController.popBackStack()
                    },
                    onNavigateToSheet = { charId ->
                        navController.navigate(Routes.sheet(charId))
                    }
                )
            }
        }

        composable(
            route = Routes.LIVE_ROOM,
            arguments = listOf(
                navArgument("chronicleId") { type = NavType.StringType },
                navArgument("asMaster") { type = NavType.StringType },
                navArgument("host") { type = NavType.StringType; defaultValue = "" },
                navArgument("port") { type = NavType.StringType; defaultValue = "0" },
                navArgument("playerName") { type = NavType.StringType; defaultValue = "" },
                navArgument("characterId") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val chronicleId = backStackEntry.arguments?.getString("chronicleId") ?: ""
            val asMaster = backStackEntry.arguments?.getString("asMaster") == "true"
            val autoHost = backStackEntry.arguments?.getString("host") ?: ""
            val autoPort = backStackEntry.arguments?.getString("port")?.toIntOrNull() ?: 0
            val autoPlayerName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("playerName") ?: "", "UTF-8")
            val autoCharacterId = backStackEntry.arguments?.getString("characterId") ?: ""
            
            val liveRoomState by liveRoomViewModel.uiState.collectAsState()

            val chronicleRepo = appContainer.chronicleRepository
            var chronicleName by remember { mutableStateOf("") }
            LaunchedEffect(chronicleId) {
                chronicleRepo.getChronicleById(chronicleId).collect { c ->
                    chronicleName = c?.name ?: ""
                }
            }
            LaunchedEffect(chronicleId) {
                liveRoomViewModel.loadChronicleAssets(chronicleId)
            }

            val audioViewModel: com.v20charactermanager.ui.chronicle.AudioViewModel = viewModel(
                factory = com.v20charactermanager.ui.chronicle.AudioViewModelFactory(
                    appContainer.audioRepository,
                    LocalContext.current.applicationContext
                )
            )
            LaunchedEffect(chronicleId) {
                audioViewModel.loadTracks(chronicleId)
            }

            com.v20charactermanager.ui.liveroom.LiveRoomScreen(
                uiState = liveRoomState,
                startAsMaster = asMaster,
                chronicleName = chronicleName,
                chronicleId = chronicleId,
                audioViewModel = audioViewModel,
                chronicleRepository = appContainer.chronicleRepository,
                autoHost = autoHost,
                autoPort = autoPort,
                autoPlayerName = autoPlayerName,
                autoCharacterId = autoCharacterId,
                onCreateRoom = { name, master, _ ->
                    liveRoomViewModel.createRoom(name, master, chronicleId)
                },
                onJoinRoom = { host, port, name, charId ->
                    liveRoomViewModel.joinRoom(host, port, name, charId)
                },
                onRetryJoin = { liveRoomViewModel.retryJoin() },
                onPresentAsset = { assetId, name, mime ->
                    liveRoomViewModel.presentAsset(assetId, name, mime)
                },
                onDismissFile = { liveRoomViewModel.dismissFile() },
                onToggleFullscreen = { liveRoomViewModel.toggleFullscreen() },
                onDisconnect = { liveRoomViewModel.disconnect() },
                onCloseRoom = {
                    liveRoomViewModel.closeRoom()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onTableStyleChange = { table, chair ->
                    liveRoomViewModel.setTableStyle(table, chair)
                },
                onOpenSheet = { charId ->
                    navController.navigate(Routes.sheet(charId))
                },
                onBack = { navController.popBackStack() },
                onClearError = { liveRoomViewModel.clearError() },
                onSendStatUpdate = { charId, field, value ->
                    liveRoomViewModel.sendStatUpdate(charId, field, value)
                }
            )
        }

        composable(Routes.FIND_TABLE) {
            val context = LocalContext.current
            val findTableViewModel: com.v20charactermanager.ui.liveroom.FindTableViewModel = viewModel(
                factory = com.v20charactermanager.ui.liveroom.FindTableViewModelFactory(
                    context.applicationContext as android.app.Application
                )
            )
            val findTableState by findTableViewModel.uiState.collectAsState()

            com.v20charactermanager.ui.liveroom.FindTableScreen(
                discoveredTables = findTableState.discoveredTables,
                isScanning = findTableState.isScanning,
                onScan = { findTableViewModel.startScan() },
                onStopScan = { findTableViewModel.stopScan() },
                onConnect = { host, port ->
                    val table = findTableState.discoveredTables.find { it.host == host && it.port == port }
                    val roomName = table?.roomName ?: "Tavolo"
                    val masterName = table?.masterName ?: "Master"
                    navController.navigate(Routes.selectCharacter(host, port, roomName, masterName))
                },
                onManualConnect = { host, port ->
                    navController.navigate(Routes.selectCharacter(host, port, "Tavolo", "Master"))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CRASH_LOGS) {
            com.v20charactermanager.ui.settings.CrashLogScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.SELECT_CHARACTER,
            arguments = listOf(
                navArgument("host") { type = NavType.StringType; defaultValue = "" },
                navArgument("port") { type = NavType.StringType; defaultValue = "0" },
                navArgument("roomName") { type = NavType.StringType; defaultValue = "" },
                navArgument("masterName") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val host = backStackEntry.arguments?.getString("host") ?: ""
            val port = backStackEntry.arguments?.getString("port")?.toIntOrNull() ?: 0
            val roomName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("roomName") ?: "", "UTF-8")
            val masterName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("masterName") ?: "", "UTF-8")

            var characters by remember { mutableStateOf<List<com.v20charactermanager.domain.model.Character>>(emptyList()) }
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    appContainer.characterRepository.getAllCharacters().collect { chars ->
                        characters = chars
                    }
                }
            }

            com.v20charactermanager.ui.liveroom.SelectCharacterScreen(
                characters = characters,
                roomName = roomName,
                masterName = masterName,
                onCharacterSelected = { characterId, characterName ->
                    navController.navigate(
                        Routes.liveRoom("_join", asMaster = false, host = host, port = port, playerName = characterName, characterId = characterId)
                    ) {
                        popUpTo(Routes.SELECT_CHARACTER) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }

    // Persistent Live Room re-entry button
    if (liveRoomState.isConnected && !liveRoomState.isFileFullscreen) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp, 80.dp) // Above bottom nav
        ) {
            LiveRoomEntryButton(
                liveRoomState = liveRoomState,
                navController = navController,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
    }
}
}

// Faceted octagon: the quick-return chip looks like a cut gemstone.
private val CrystalShape = GenericShape { size, _ ->
    val cut = minOf(size.width, size.height) * 0.30f
    moveTo(cut, 0f)
    lineTo(size.width - cut, 0f)
    lineTo(size.width, cut)
    lineTo(size.width, size.height - cut)
    lineTo(size.width - cut, size.height)
    lineTo(cut, size.height)
    lineTo(0f, size.height - cut)
    lineTo(0f, cut)
    close()
}

@Composable
private fun LiveRoomEntryButton(
    liveRoomState: LiveRoomState,
    navController: NavHostController,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (liveRoomState.isConnected) 1f else 0f, label = "entryButtonScale")
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current.density
    val configuration = LocalConfiguration.current
    val screenW = configuration.screenWidthDp * density
    val screenH = configuration.screenHeightDp * density

    val isMaster = liveRoomState.isMaster
    val facetTint = if (isMaster) Color(0xFFD4A847) else Color(0xFF66BB6A)

    Box(
        modifier = modifier
            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Row(
            modifier = Modifier
                .pointerInput(screenW, screenH) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val next = dragOffset + dragAmount
                        dragOffset = Offset(
                            next.x.coerceIn(-screenW + 140 * density, 8 * density),
                            next.y.coerceIn(-screenH + 160 * density, 64 * density)
                        )
                    }
                }
                .shadow(6.dp, CrystalShape)
                .clip(CrystalShape)
                .background(Color(0xCC1A1A2E))
                .border(1.dp, facetTint.copy(alpha = 0.6f), CrystalShape)
                .clickable {
                    val chronicleId = liveRoomState.room?.chronicleId ?: ""
                    onNavigate(Routes.liveRoom(chronicleId, isMaster))
                }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TableRestaurant,
                contentDescription = null,
                tint = facetTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.live_room),
                color = Color(0xFFF2EEE1),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFF2EEE1).copy(alpha = 0.7f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
