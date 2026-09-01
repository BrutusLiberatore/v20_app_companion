package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.Character

class CharacterCreationValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )

    fun validateIdentity(character: Character): ValidationResult {
        val errors = mutableListOf<String>()
        if (character.identity.name.isBlank()) errors.add("Name is required")
        if (character.identity.clan == ClanId.CAITIFF && character.identity.sire.isNotBlank()) {
            // Caitiff can have a sire but it's unusual
        }
        if (!GenerationRules.isValidGeneration(character.identity.generation)) {
            errors.add("Generation must be between 3 and 13")
        }
        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateAttributes(character: Character): ValidationResult {
        val errors = mutableListOf<String>()

        val categoryPoints = AttributeCategory.entries.map { category ->
            val attrs = character.attributes.filter { it.id.category == category }
            category to attrs.sumOf { it.value - RuleSet.ATTRIBUTE_BASE }
        }

        val totalPoints = categoryPoints.sumOf { it.second }
        val expectedTotal = RuleSet.ATTRIBUTE_PRIMARY + RuleSet.ATTRIBUTE_SECONDARY + RuleSet.ATTRIBUTE_TERTIARY
        if (totalPoints != expectedTotal) {
            errors.add("Total attribute points: $totalPoints, expected $expectedTotal")
        }

        categoryPoints.forEach { (category, points) ->
            if (points < 1) {
                errors.add("Category $category must have at least 1 point")
            }
            if (points > 13) {
                errors.add("Category $category has too many points: $points (max 13)")
            }
        }

        val sorted = categoryPoints.map { it.second }.sorted()
        if (sorted != listOf(RuleSet.ATTRIBUTE_TERTIARY, RuleSet.ATTRIBUTE_SECONDARY, RuleSet.ATTRIBUTE_PRIMARY)) {
            errors.add("Attribute distribution must be a 7/5/3 split across categories")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateAbilities(character: Character): ValidationResult {
        val errors = mutableListOf<String>()

        character.abilities.forEach { ability ->
            if (ability.value > RuleSet.ABILITY_MAX_CREATION) {
                errors.add("Ability ${ability.id.nameEn} exceeds max creation value of ${RuleSet.ABILITY_MAX_CREATION}")
            }
        }

        val categoryPoints = AbilityCategory.entries.map { category ->
            val abils = character.abilities.filter { it.id.category == category }
            category to abils.sumOf { it.value - RuleSet.ABILITY_BASE }
        }

        val totalPoints = categoryPoints.sumOf { it.second }
        val expectedTotal = RuleSet.ABILITY_PRIMARY + RuleSet.ABILITY_SECONDARY + RuleSet.ABILITY_TERTIARY
        if (totalPoints != expectedTotal) {
            errors.add("Total ability points: $totalPoints, expected $expectedTotal")
        }

        categoryPoints.forEach { (category, points) ->
            if (points < 1) {
                errors.add("Category $category must have at least 1 point")
            }
        }

        val sorted = categoryPoints.map { it.second }.sorted()
        if (sorted != listOf(RuleSet.ABILITY_TERTIARY, RuleSet.ABILITY_SECONDARY, RuleSet.ABILITY_PRIMARY)) {
            errors.add("Ability distribution must be a 13/9/5 split across categories")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateAdvantages(character: Character): ValidationResult {
        val errors = mutableListOf<String>()

        val creationProfile = CreationProfile.forSect(character.identity.sect)

        val disciplinePoints = character.disciplines.sumOf { it.value }
        if (disciplinePoints != creationProfile.disciplinePoints) {
            errors.add("Discipline points: $disciplinePoints, expected ${creationProfile.disciplinePoints}")
        }

        val backgroundPoints = character.backgrounds.sumOf { it.value }
        if (backgroundPoints != creationProfile.backgroundPoints) {
            errors.add("Background points: $backgroundPoints, expected ${creationProfile.backgroundPoints}")
        }

        val virtuePoints = character.virtues.sumOf { it.value - RuleSet.VIRTUE_BASE }
        val expectedVirtueExtra = creationProfile.virtuePoints - (character.virtues.size * RuleSet.VIRTUE_BASE)
        if (virtuePoints != expectedVirtueExtra) {
            errors.add("Virtue points distribution is incorrect (have $virtuePoints extra, need $expectedVirtueExtra)")
        }

        val clanDisciplines = character.identity.clan.clanDisciplines
        if (clanDisciplines.isNotEmpty()) {
            character.disciplines.forEach { disc ->
                if (disc.id !in clanDisciplines) {
                    errors.add("Discipline ${disc.id.nameEn} is not a clan discipline for ${character.identity.clan.nameEn}")
                }
            }
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateFinalization(character: Character): ValidationResult {
        val errors = mutableListOf<String>()

        val expectedHumanity = HumanityCalculator.calculate(character)
        if (character.moralPath.humanity != expectedHumanity) {
            errors.add("Humanity should be $expectedHumanity, is ${character.moralPath.humanity}")
        }

        val expectedWillpower = WillpowerCalculator.calculatePermanent(character)
        if (character.willpower.permanent != expectedWillpower) {
            errors.add("Willpower should be $expectedWillpower, is ${character.willpower.permanent}")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateStep(character: Character, step: Int): ValidationResult {
        return when (step) {
            1 -> validateIdentity(character)
            2 -> validateAttributes(character)
            3 -> validateAbilities(character)
            4 -> validateAdvantages(character)
            5 -> validateFinalization(character)
            else -> ValidationResult(true)
        }
    }
}
