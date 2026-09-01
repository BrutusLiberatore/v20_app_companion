package com.v20charactermanager.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.v20charactermanager.data.di.AppContainer
import com.v20charactermanager.util.LocaleHelper
import com.v20charactermanager.domain.model.CharacterRandomizer
import com.v20charactermanager.domain.model.CreatureType
import com.v20charactermanager.domain.model.MediaAssetType
import com.v20charactermanager.domain.model.PlotType
import com.v20charactermanager.domain.model.Visibility
import com.v20charactermanager.ui.compendium.CompendiumDetailScreen
import com.v20charactermanager.ui.chronicle.ChronicleDetailScreen
import com.v20charactermanager.ui.chronicle.ChronicleListScreen
import com.v20charactermanager.ui.chronicle.ChronicleViewModel
import com.v20charactermanager.ui.chronicle.ChronicleViewModelFactory
import com.v20charactermanager.ui.chronicle.ImageViewerScreen
import com.v20charactermanager.ui.chronicle.MediaLibraryScreen
import com.v20charactermanager.ui.chronicle.MediaViewModel
import com.v20charactermanager.ui.chronicle.MediaViewModelFactory
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
import com.v20charactermanager.ui.sheet.EditCharacterViewModel
import com.v20charactermanager.ui.sheet.EditCharacterViewModelFactory
import com.v20charactermanager.ui.sheet.SheetScreen
import com.v20charactermanager.ui.xp.XpSpendingScreen
import com.v20charactermanager.ui.xp.XpSpendingViewModel
import com.v20charactermanager.ui.xp.XpSpendingViewModelFactory

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

    fun xpSpending(characterId: String) = "xp_spending/$characterId"

    fun creationStep(step: Int) = "creation/$step"
    fun sheet(characterId: String) = "sheet/$characterId"
    fun session(characterId: String) = "session/$characterId"
    fun dice(pool: Int? = null) = if (pool != null) "dice?pool=$pool" else "dice"
    fun compendiumDetail(itemId: String) = "compendium/detail/$itemId"
    fun chronicleDetail(chronicleId: String) = "chronicle/$chronicleId"
    fun mediaLibrary(chronicleId: String) = "chronicle/$chronicleId/media"
    fun imageViewer(chronicleId: String, assetId: String) = "chronicle/$chronicleId/media/$assetId"
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
                onNextStep = {
                    viewModel.nextStep()
                },
                onPreviousStep = {
                    viewModel.previousStep()
                },
                onSave = {
                    viewModel.saveCharacter()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
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

            LaunchedEffect(characterId) {
                editViewModel.loadCharacter(characterId)
            }

            uiState.character?.let { character ->
                SheetScreen(
                    character = character,
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSession = {
                        navController.navigate(Routes.session(characterId))
                    },
                    onNotesChange = { notes ->
                        editViewModel.updateNotes(notes)
                    },
                    onNavigateToDice = { pool ->
                        navController.navigate(Routes.dice(pool))
                    },
                    isEditing = uiState.isEditing,
                    onToggleEdit = { editViewModel.startEditing() },
                    onSave = { editViewModel.save() },
                    onCancelEdit = { editViewModel.cancelEditing() },
                    onAttributeChange = { attributeId, value ->
                        editViewModel.updateAttribute(attributeId, value)
                    },
                    onAbilityChange = { abilityId, value ->
                        editViewModel.updateAbility(abilityId, value)
                    },
                    onDisciplineValueChange = { disciplineId, value ->
                        editViewModel.updateDisciplineValue(disciplineId, value)
                    },
                    onDisciplineRemove = { disciplineId ->
                        editViewModel.removeDiscipline(disciplineId)
                    },
                    onBackgroundValueChange = { backgroundId, value ->
                        editViewModel.updateBackgroundValue(backgroundId, value)
                    },
                    onBackgroundRemove = { backgroundId ->
                        editViewModel.removeBackground(backgroundId)
                    },
                    onVirtueChange = { virtueId, value ->
                        editViewModel.updateVirtue(virtueId, value)
                    },
                    onPortraitChange = { portraitUri ->
                        editViewModel.updatePortrait(portraitUri)
                    },
                    onMeritAdd = { merit ->
                        editViewModel.addMerit(merit)
                    },
                    onMeritRemove = { meritId ->
                        editViewModel.removeMerit(meritId)
                    },
                    onMeritClone = { merit ->
                        editViewModel.cloneMerit(merit)
                    },
                    onFlawAdd = { flaw ->
                        editViewModel.addFlaw(flaw)
                    },
                    onFlawRemove = { flawId ->
                        editViewModel.removeFlaw(flawId)
                    },
                    onFlawClone = { flaw ->
                        editViewModel.cloneFlaw(flaw)
                    },
                    onEquipmentAdd = { item ->
                        editViewModel.addEquipment(item)
                    },
                    onEquipmentUpdate = { item ->
                        editViewModel.updateEquipment(item)
                    },
                    onEquipmentRemove = { itemId ->
                        editViewModel.removeEquipment(itemId)
                    },
                    onEquipmentClone = { item ->
                        editViewModel.cloneEquipment(item)
                    },
                    onNavigateToXpSpending = {
                        navController.navigate(Routes.xpSpending(characterId))
                    }
                )
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
                factory = SettingsViewModelFactory(appContainer.settingsRepository)
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
                }
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

            LaunchedEffect(chronicleId) {
                viewModel.loadChronicleDetail(chronicleId)
            }

            ChronicleDetailScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onTabSelected = { viewModel.selectTab(it) },
                onAddCharacter = { cId, charId, role ->
                    viewModel.addCharacterToChronicle(cId, charId, role)
                },
                onRemoveCharacter = { cId, charId ->
                    viewModel.removeCharacterFromChronicle(cId, charId)
                },
                onCreateSession = { cId, title ->
                    viewModel.createSession(cId, title)
                },
                onUpdateSession = { session ->
                    viewModel.updateSession(session)
                },
                onDeleteSession = { sessionId ->
                    viewModel.deleteSession(sessionId)
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
                onNavigateToCharacter = { characterId ->
                    navController.navigate(Routes.sheet(characterId))
                },
                onNavigateToDice = {
                    navController.navigate(Routes.dice())
                },
                onCreateNpc = { cId, name, creatureType, role ->
                    viewModel.createNpc(cId, name, creatureType, role)
                },
                onDeleteNpc = { npcId ->
                    viewModel.deleteNpc(npcId)
                },
                onUpdateNpc = { npc ->
                    viewModel.updateNpc(npc)
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
                onCreateFaction = { cId, name ->
                    viewModel.createFaction(cId, name)
                },
                onDeleteFaction = { factionId ->
                    viewModel.deleteFaction(factionId)
                },
                onUpdateFaction = { faction ->
                    viewModel.updateFaction(faction)
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
                onOpenMediaLibrary = { chronicleId ->
                    navController.navigate(Routes.mediaLibrary(chronicleId))
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

            MediaLibraryScreen(
                chronicleId = chronicleId,
                assets = uiState.assets,
                onImportImage = { pickImageLauncher.launch("image/*") },
                onAssetClick = { asset ->
                    navController.navigate(Routes.imageViewer(chronicleId, asset.id))
                },
                onAssetDelete = { mediaViewModel.deleteAsset(it) },
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
                ImageViewerScreen(
                    mediaAsset = asset,
                    annotations = uiState.annotations,
                    layers = uiState.layers,
                    onBack = { navController.popBackStack() },
                    onToggleLayers = { },
                    onTogglePresentation = { }
                )
            }
        }
    }
}
