package com.v20charactermanager.ui.creation

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeCharacterRepository : CharacterRepository {
    val inserted = mutableListOf<Character>()
    override fun getAllCharacters(): Flow<List<Character>> = MutableStateFlow(emptyList())
    override fun getCharacterById(id: String): Flow<Character?> = MutableStateFlow(null)
    override suspend fun getCharacterByIdOnce(id: String): Character? = null
    override suspend fun insertCharacter(character: Character) { inserted.add(character) }
    override suspend fun updateCharacter(character: Character) {}
    override suspend fun deleteCharacter(id: String) {}
    override suspend fun duplicateCharacter(id: String): Character? = null
    override suspend fun getCharacterCount(): Int = inserted.size

    fun completedSaves(): List<Character> = inserted.filter { it.isComplete }
}

class CharacterCreationViewModelTest {

    private lateinit var repository: FakeCharacterRepository

    @Before
    fun setUp() {
        repository = FakeCharacterRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun completeCharacter(): Character {
        var c = Character(
            id = "vm-test",
            identity = CharacterIdentity(
                name = "Valid Character",
                clan = ClanId.BRUAH,
                generation = 13
            )
        )
        c = c.setAttributeValue(AttributeId.STRENGTH, 4)
            .setAttributeValue(AttributeId.DEXTERITY, 4)
            .setAttributeValue(AttributeId.STAMINA, 2)
            .setAttributeValue(AttributeId.CHARISMA, 3)
            .setAttributeValue(AttributeId.MANIPULATION, 2)
            .setAttributeValue(AttributeId.APPEARANCE, 3)
            .setAttributeValue(AttributeId.PERCEPTION, 2)
            .setAttributeValue(AttributeId.INTELLIGENCE, 2)
            .setAttributeValue(AttributeId.WITS, 2)

        val targets = mapOf(
            AbilityCategory.TALENTS to 13,
            AbilityCategory.SKILLS to 9,
            AbilityCategory.KNOWLEDGES to 5
        )
        targets.forEach { (category, target) ->
            var placed = 0
            val ids = AbilityId.entries.filter { it.category == category }
            var index = 0
            while (placed < target) {
                val id = ids[index % ids.size]
                val current = c.getAbilityValue(id)
                if (current < 3) {
                    c = c.setAbilityValue(id, current + 1)
                    placed++
                }
                index++
                check(index < 500)
            }
        }
        c = c.addDiscipline(DisciplineId.PRESENCE, 3)
        c = c.addBackground(BackgroundId.entries.first(), 5)
        c = c.setVirtueValue(VirtueId.CONSCIENCE, 3)
            .setVirtueValue(VirtueId.SELF_CONTROL, 2)
            .setVirtueValue(VirtueId.COURAGE, 2)
        return c
    }

    /** Pushes the character into the ViewModel through the same paths the UI uses. */
    private fun CharacterCreationViewModel.loadCharacter(c: Character) {
        updateIdentity(c.identity)
        c.attributes.forEach { updateAttribute(it.id, it.value) }
        c.abilities.forEach { updateAbility(it.id, it.value) }
        c.disciplines.forEach { addDiscipline(it.id, it.value) }
        c.backgrounds.forEach { addBackground(it.id, it.value) }
        c.virtues.forEach { updateVirtue(it.id, it.value) }
    }

    @Test
    fun `save with missing name shows errors and does not save`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CharacterCreationViewModel(repository)
        viewModel.goToStep(5)

        viewModel.saveCharacter()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.saved)
        assertTrue(state.validationResult?.errors?.any { it.contains("Name") } == true)
        assertTrue(repository.completedSaves().isEmpty())
    }

    @Test
    fun `save with valid character completes and applies derived values`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CharacterCreationViewModel(repository)
        viewModel.loadCharacter(completeCharacter())
        viewModel.goToStep(5)

        viewModel.saveCharacter()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.saved)
        assertNull(state.validationResult)
        assertNull(state.pendingWarnings)

        val saved = repository.completedSaves().single()
        assertTrue(saved.isComplete)
        assertEquals(2, saved.willpower.permanent)
        assertEquals(5, saved.moralPath.humanity)
        assertEquals(10, saved.bloodPool.maximum)
        assertEquals(10, saved.bloodPool.current)
    }

    @Test
    fun `save with non-standard points asks confirmation then saves`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CharacterCreationViewModel(repository)
        val character = completeCharacter().setAttributeValue(AttributeId.WITS, 3)
        viewModel.loadCharacter(character)
        viewModel.goToStep(5)

        viewModel.saveCharacter()
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertFalse(state.saved)
        assertTrue(state.pendingSave)
        assertNotNull(state.pendingWarnings)
        assertTrue(repository.completedSaves().isEmpty())

        viewModel.confirmWarnings()
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertTrue(state.saved)
        assertEquals(1, repository.completedSaves().size)
    }

    @Test
    fun `dismiss warnings does not save`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CharacterCreationViewModel(repository)
        val character = completeCharacter().setAttributeValue(AttributeId.WITS, 3)
        viewModel.loadCharacter(character)
        viewModel.goToStep(5)

        viewModel.saveCharacter()
        advanceUntilIdle()
        viewModel.dismissWarnings()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.saved)
        assertFalse(state.pendingSave)
        assertNull(state.pendingWarnings)
        assertTrue(repository.completedSaves().isEmpty())
    }

    @Test
    fun `next step blocks on hard error and advances on warning confirm`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CharacterCreationViewModel(repository)

        // Step 1 with blank name = hard error
        viewModel.nextStep()
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.validationResult)

        // Fill the name: identity passes, wizard moves to step 2
        viewModel.updateIdentity(
            viewModel.uiState.value.character.identity.copy(name = "Named")
        )
        viewModel.nextStep()
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.currentStep)

        // Default attributes = non-standard points: warning, blocked until confirmed
        viewModel.nextStep()
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.pendingWarnings)

        viewModel.confirmWarnings()
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.currentStep)
    }
}
