package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.Character

class CharacterCreationValidator {

    /**
     * Severity split:
     * - [errors] are hard rules from the V20 manual and block progression/save.
     * - [warnings] are non-standard point allocations and can be overridden by the player.
     */
    data class ValidationResult(
        val errors: List<String> = emptyList(),
        val warnings: List<String> = emptyList()
    ) {
        val isValid: Boolean get() = errors.isEmpty()
    }

    fun validateIdentity(character: Character): ValidationResult {
        val errors = mutableListOf<String>()
        if (character.identity.name.isBlank()) errors.add("Name is required")
        if (!GenerationRules.isValidGeneration(character.identity.generation)) {
            errors.add("Generation must be between 3 and 15")
        }
        character.identity.clan.requiredChoices.forEach { choice ->
            if (choice.required) {
                val answer = character.identity.clanChoices[choice.id]?.trim()
                if (answer.isNullOrEmpty()) {
                    errors.add("Missing required choice: ${choice.promptEn}")
                }
            }
        }
        return ValidationResult(errors = errors)
    }

    fun validateAttributes(character: Character): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val isNosferatu = character.identity.clan == ClanId.NOSFERATU

        if (isNosferatu && character.getAttributeValue(AttributeId.APPEARANCE) != 0) {
            errors.add("Nosferatu must have Appearance 0")
        }

        val categoryPoints = AttributeCategory.entries.map { category ->
            val attrs = character.attributes.filter { it.id.category == category }
            category to attrs.sumOf { attr ->
                val base = if (isNosferatu && attr.id == AttributeId.APPEARANCE) 0 else RuleSet.ATTRIBUTE_BASE
                attr.value - base
            }
        }

        val totalPoints = categoryPoints.sumOf { it.second }
        val expectedTotal = RuleSet.ATTRIBUTE_PRIMARY + RuleSet.ATTRIBUTE_SECONDARY + RuleSet.ATTRIBUTE_TERTIARY
        if (totalPoints != expectedTotal) {
            warnings.add("Total attribute points: $totalPoints, expected $expectedTotal")
        }

        categoryPoints.forEach { (category, points) ->
            if (points < 1) {
                warnings.add("Category $category must have at least 1 point")
            }
            if (points > 13) {
                warnings.add("Category $category has too many points: $points (max 13)")
            }
        }

        val sorted = categoryPoints.map { it.second }.sorted()
        if (sorted != listOf(RuleSet.ATTRIBUTE_TERTIARY, RuleSet.ATTRIBUTE_SECONDARY, RuleSet.ATTRIBUTE_PRIMARY)) {
            warnings.add("Attribute distribution must be a 7/5/3 split across categories")
        }

        return ValidationResult(errors = errors, warnings = warnings)
    }

    fun validateAbilities(character: Character): ValidationResult {
        val warnings = mutableListOf<String>()

        character.abilities.forEach { ability ->
            if (ability.value > RuleSet.ABILITY_MAX_CREATION) {
                warnings.add("Ability ${ability.id.nameEn} exceeds max creation value of ${RuleSet.ABILITY_MAX_CREATION}")
            }
        }

        val categoryPoints = AbilityCategory.entries.map { category ->
            val abils = character.abilities.filter { it.id.category == category }
            category to abils.sumOf { it.value - RuleSet.ABILITY_BASE }
        }

        val totalPoints = categoryPoints.sumOf { it.second }
        val expectedTotal = RuleSet.ABILITY_PRIMARY + RuleSet.ABILITY_SECONDARY + RuleSet.ABILITY_TERTIARY
        if (totalPoints != expectedTotal) {
            warnings.add("Total ability points: $totalPoints, expected $expectedTotal")
        }

        categoryPoints.forEach { (category, points) ->
            if (points < 1) {
                warnings.add("Category $category must have at least 1 point")
            }
        }

        val sorted = categoryPoints.map { it.second }.sorted()
        if (sorted != listOf(RuleSet.ABILITY_TERTIARY, RuleSet.ABILITY_SECONDARY, RuleSet.ABILITY_PRIMARY)) {
            warnings.add("Ability distribution must be a 13/9/5 split across categories")
        }

        return ValidationResult(warnings = warnings)
    }

    fun validateAdvantages(character: Character): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        val creationProfile = CreationProfile.forSect(character.identity.sect)

        val disciplinePoints = character.disciplines.sumOf { it.value }
        if (disciplinePoints != creationProfile.disciplinePoints) {
            warnings.add("Discipline points: $disciplinePoints, expected ${creationProfile.disciplinePoints}")
        }

        val backgroundPoints = character.backgrounds.sumOf { it.value }
        if (backgroundPoints != creationProfile.backgroundPoints) {
            warnings.add("Background points: $backgroundPoints, expected ${creationProfile.backgroundPoints}")
        }

        val virtuePoints = character.virtues.sumOf { it.value - RuleSet.VIRTUE_BASE }
        val expectedVirtueExtra = creationProfile.virtuePoints - (character.virtues.size * RuleSet.VIRTUE_BASE)
        if (virtuePoints != expectedVirtueExtra) {
            warnings.add("Virtue points distribution is incorrect (have $virtuePoints extra, need $expectedVirtueExtra)")
        }

        val clanDisciplines = character.identity.clan.clanDisciplines
        if (clanDisciplines.isNotEmpty()) {
            character.disciplines.forEach { disc ->
                if (disc.id !in clanDisciplines) {
                    errors.add("Discipline ${disc.id.nameEn} is not a clan discipline for ${character.identity.clan.nameEn}")
                }
            }
        }

        val meritPoints = character.merits.sumOf { it.cost }
        if (meritPoints > RuleSet.MERIT_MAX_CREATION) {
            errors.add("Merits exceed the ${RuleSet.MERIT_MAX_CREATION} point limit: $meritPoints")
        }
        val flawPoints = character.flaws.sumOf { it.value }
        if (flawPoints > RuleSet.FLAW_MAX_CREATION) {
            errors.add("Flaws exceed the ${RuleSet.FLAW_MAX_CREATION} point limit: $flawPoints")
        }

        return ValidationResult(errors = errors, warnings = warnings)
    }

    fun validateFinalization(character: Character): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        val expectedHumanity = HumanityCalculator.calculate(character)
        if (character.moralPath.humanity != expectedHumanity) {
            errors.add("Humanity should be $expectedHumanity, is ${character.moralPath.humanity}")
        }

        val expectedWillpower = WillpowerCalculator.calculatePermanent(character)
        if (character.willpower.permanent != expectedWillpower) {
            errors.add("Willpower should be $expectedWillpower, is ${character.willpower.permanent}")
        }

        val freebieReport = FreebiePointCalculator().calculate(character)
        if (freebieReport.remainingPoints < 0) {
            warnings.add("Freebie points exceeded: used ${freebieReport.usedPoints} of ${freebieReport.initialPoints}")
        }

        return ValidationResult(errors = errors, warnings = warnings)
    }

    fun validateStep(character: Character, step: Int): ValidationResult {
        return when (step) {
            1 -> validateIdentity(character)
            2 -> validateAttributes(character)
            3 -> validateAbilities(character)
            4 -> validateAdvantages(character)
            5 -> validateFinalization(character)
            else -> ValidationResult()
        }
    }
}
