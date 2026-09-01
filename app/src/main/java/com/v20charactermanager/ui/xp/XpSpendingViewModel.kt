package com.v20charactermanager.ui.xp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.engine.XpCostCalculator
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class XpTraitItem(
    val name: String,
    val currentValue: Int,
    val cost: Int,
    val category: String,
    val traitType: TraitType
)

enum class TraitType {
    ATTRIBUTE, ABILITY, DISCIPLINE, BACKGROUND, VIRTUE
}

data class XpSpendingUiState(
    val character: Character? = null,
    val allTraitItems: List<XpTraitItem> = emptyList(),
    val traitItems: List<XpTraitItem> = emptyList(),
    val selectedCategory: String? = null,
    val isSpending: Boolean = false,
    val error: String? = null,
    val successItemName: String? = null,
    val successNewValue: Int = 0,
    val successCost: Int = 0
)

class XpSpendingViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(XpSpendingUiState())
    val uiState: StateFlow<XpSpendingUiState> = _uiState.asStateFlow()

    private var characterId: String? = null

    fun loadCharacter(id: String) {
        characterId = id
        viewModelScope.launch {
            characterRepository.getCharacterById(id).collect { character ->
                if (character != null) {
                    val allItems = buildTraitList(character)
                    _uiState.value = _uiState.value.copy(
                        character = character,
                        allTraitItems = allItems,
                        traitItems = applyFilter(allItems, _uiState.value.selectedCategory)
                    )
                }
            }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            traitItems = applyFilter(_uiState.value.allTraitItems, category)
        )
    }

    private fun applyFilter(items: List<XpTraitItem>, category: String?): List<XpTraitItem> {
        if (category == null) return items
        return items.filter { item ->
            when (category) {
                "Physical" -> item.traitType == TraitType.ATTRIBUTE && item.category.equals("Physical", ignoreCase = true)
                "Social" -> item.traitType == TraitType.ATTRIBUTE && item.category.equals("Social", ignoreCase = true)
                "Mental" -> item.traitType == TraitType.ATTRIBUTE && item.category.equals("Mental", ignoreCase = true)
                "Talents" -> item.traitType == TraitType.ABILITY && item.category.equals("Talents", ignoreCase = true)
                "Skills" -> item.traitType == TraitType.ABILITY && item.category.equals("Skills", ignoreCase = true)
                "Knowledges" -> item.traitType == TraitType.ABILITY && item.category.equals("Knowledges", ignoreCase = true)
                "Disciplines" -> item.traitType == TraitType.DISCIPLINE
                "Backgrounds" -> item.traitType == TraitType.BACKGROUND
                "Virtues" -> item.traitType == TraitType.VIRTUE
                else -> item.category.equals(category, ignoreCase = true)
            }
        }
    }

    fun spendXp(item: XpTraitItem) {
        val character = _uiState.value.character ?: return
        if (item.cost > character.experience.available) {
            _uiState.value = _uiState.value.copy(error = "Not enough XP")
            return
        }

        viewModelScope.launch {
            val updatedCharacter = when (item.traitType) {
                TraitType.ATTRIBUTE -> {
                    val attrId = AttributeId.entries.find { it.nameEn == item.name } ?: return@launch
                    character.setAttributeValue(attrId, item.currentValue + 1)
                }
                TraitType.ABILITY -> {
                    val abilId = AbilityId.entries.find { it.nameEn == item.name } ?: return@launch
                    character.setAbilityValue(abilId, item.currentValue + 1)
                }
                TraitType.DISCIPLINE -> {
                    val discId = DisciplineId.entries.find { it.nameEn == item.name } ?: return@launch
                    character.copy(
                        disciplines = character.disciplines.map {
                            if (it.id == discId) it.copy(value = item.currentValue + 1) else it
                        }
                    )
                }
                TraitType.BACKGROUND -> {
                    val bgId = BackgroundId.entries.find { it.nameEn == item.name } ?: return@launch
                    character.copy(
                        backgrounds = character.backgrounds.map {
                            if (it.id == bgId) it.copy(value = item.currentValue + 1) else it
                        }
                    )
                }
                TraitType.VIRTUE -> {
                    val virtueId = VirtueId.entries.find { it.nameEn == item.name } ?: return@launch
                    character.setVirtueValue(virtueId, item.currentValue + 1)
                }
            }

            val spentCharacter = updatedCharacter.copy(
                experience = updatedCharacter.experience.spend(item.cost),
                updatedAt = System.currentTimeMillis()
            )

            characterRepository.updateCharacter(spentCharacter)
            val allItems = buildTraitList(spentCharacter)
            _uiState.value = _uiState.value.copy(
                character = spentCharacter,
                allTraitItems = allItems,
                traitItems = applyFilter(allItems, _uiState.value.selectedCategory),
                successItemName = item.name,
                successNewValue = item.currentValue + 1,
                successCost = item.cost,
                isSpending = false
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successItemName = null)
    }

    private fun buildTraitList(character: Character): List<XpTraitItem> {
        val items = mutableListOf<XpTraitItem>()
        val clan = character.identity.clan

        character.attributes.forEach { attr ->
            if (attr.value < 5) {
                val cost = XpCostCalculator.calculateAttributeCost(attr.value)
                items.add(
                    XpTraitItem(
                        name = attr.id.nameEn,
                        currentValue = attr.value,
                        cost = cost,
                        category = attr.id.category.name.lowercase().replaceFirstChar { it.uppercase() },
                        traitType = TraitType.ATTRIBUTE
                    )
                )
            }
        }

        character.abilities.forEach { abil ->
            if (abil.value < 5) {
                val cost = XpCostCalculator.calculateAbilityCost(abil.value, abil.value == 0)
                items.add(
                    XpTraitItem(
                        name = abil.id.nameEn,
                        currentValue = abil.value,
                        cost = cost,
                        category = abil.id.category.name.lowercase().replaceFirstChar { it.uppercase() },
                        traitType = TraitType.ABILITY
                    )
                )
            }
        }

        character.disciplines.forEach { disc ->
            if (disc.value < 5) {
                val cost = XpCostCalculator.calculateDisciplineCost(clan, disc.value, false)
                items.add(
                    XpTraitItem(
                        name = disc.id.nameEn,
                        currentValue = disc.value,
                        cost = cost,
                        category = "Disciplines",
                        traitType = TraitType.DISCIPLINE
                    )
                )
            }
        }

        character.backgrounds.forEach { bg ->
            if (bg.value < 5) {
                val cost = XpCostCalculator.calculateBackgroundCost(bg.value)
                items.add(
                    XpTraitItem(
                        name = bg.id.nameEn,
                        currentValue = bg.value,
                        cost = cost,
                        category = "Backgrounds",
                        traitType = TraitType.BACKGROUND
                    )
                )
            }
        }

        character.virtues.forEach { virt ->
            if (virt.value < 5) {
                val cost = XpCostCalculator.calculateVirtueCost(virt.value)
                items.add(
                    XpTraitItem(
                        name = virt.id.nameEn,
                        currentValue = virt.value,
                        cost = cost,
                        category = "Virtues",
                        traitType = TraitType.VIRTUE
                    )
                )
            }
        }

        return items.sortedBy { it.category }
    }

    companion object {
        val categories = listOf(
            "Physical", "Social", "Mental",
            "Talents", "Skills", "Knowledges",
            "Disciplines", "Backgrounds", "Virtues"
        )
    }
}

class XpSpendingViewModelFactory(
    private val characterRepository: CharacterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(XpSpendingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return XpSpendingViewModel(characterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
