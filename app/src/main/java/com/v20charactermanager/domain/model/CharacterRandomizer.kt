package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.engine.CreationProfile
import com.v20charactermanager.domain.engine.GenerationRules
import java.util.UUID
import kotlin.random.Random

object CharacterRandomizer {

    fun randomize(
        template: CharacterTemplate? = null,
        gender: Gender? = null,
        preferredClan: ClanId? = null,
        preferredSect: SectId? = null,
        generation: Int = 13
    ): Character {
        val clan = preferredClan ?: randomClan()
        val sect = preferredSect ?: SectId.defaultForClan(clan)
        val charGender = gender ?: Gender.entries.random()
        val (firstName, lastName) = NameDatabase.getRandomFullName(charGender)
        val nature = NatureId.entries.random()
        val demeanor = DemeanorId.entries.random()
        val profile = listOf("profile.criminal", "profile.outsider", "profile.child",
            "profile.intellectual", "profile.entertainer", "profile.investigator",
            "profile.worker", "profile.politician", "profile.professional",
            "profile.reporter", "profile.soldier", "profile.bon_vivant",
            "profile.vagabond", "profile.viveur").random()

        val attributeDistribution = randomDistribution(7, 5, 3)
        val abilityDistribution = randomDistribution(13, 9, 5)

        val creationProfile = CreationProfile.forSect(sect)

        val attributes = randomAttributes(attributeDistribution)
        val abilities = randomAbilities(abilityDistribution)
        val disciplines = randomDisciplines(clan, creationProfile.disciplinePoints)
        val backgrounds = randomBackgrounds(creationProfile.backgroundPoints)
        val virtues = randomVirtues(creationProfile.virtuePoints, sect)

        val virtueMap = virtues.associate { it.id to it.value }
        val conscience = virtueMap[VirtueId.CONSCIENCE] ?: 0
        val selfControl = virtueMap[VirtueId.SELF_CONTROL] ?: 0
        val courage = virtueMap[VirtueId.COURAGE] ?: 1
        val conviction = virtueMap[VirtueId.CONVICTION] ?: 0
        val instinct = virtueMap[VirtueId.INSTINCT] ?: 0

        val humanity = conscience + selfControl
        val permanentWillpower = courage
        val maxBloodPool = GenerationRules.getBloodPoolMax(generation)
        val initialBloodPool = rollInitialBloodPool(maxBloodPool)

        val merits = randomMerits()
        val maxFlawValue = merits.sumOf { it.cost }
        val flaws = randomFlaws(maxFlawValue)

        val character = Character(
            id = UUID.randomUUID().toString(),
            identity = CharacterIdentity(
                name = "$firstName $lastName",
                player = "",
                chronicle = "",
                profile = profile,
                clan = clan,
                sect = sect,
                generation = generation,
                nature = nature,
                demeanor = demeanor,
                sire = "",
                haven = "",
                concept = generateConcept(clan, attributes, abilities)
            ),
            attributes = attributes,
            abilities = abilities,
            disciplines = disciplines,
            backgrounds = backgrounds,
            virtues = virtues,
            moralPath = MoralPath(
                pathId = if (sect == SectId.SABBAT) "path.bestia" else "humanity",
                pathNameId = if (sect == SectId.SABBAT) "path.bestia" else "path.humanity",
                conscienceValue = conscience,
                selfControlValue = selfControl,
                courageValue = courage,
                convictionValue = conviction,
                instinctValue = instinct
            ),
            merits = merits,
            flaws = flaws,
            health = HealthState(),
            bloodPool = BloodPoolState(maximum = maxBloodPool, current = initialBloodPool),
            willpower = WillpowerState(permanent = permanentWillpower, current = permanentWillpower),
            experience = ExperienceState(),
            equipment = randomEquipment(),
            notes = "",
            creationStep = 0,
            isComplete = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        return template?.applyTemplate(character) ?: character
    }

    private fun randomClan(): ClanId {
        val weights = mapOf(
            ClanId.BRUAH to 12,
            ClanId.VENTRUE to 10,
            ClanId.TOREADOR to 10,
            ClanId.MALKAVIAN to 10,
            ClanId.NOSFERATU to 10,
            ClanId.TREMERE to 10,
            ClanId.GANGREL to 8,
            ClanId.LASOMBRA to 8,
            ClanId.GIOVANNI to 7,
            ClanId.ASSAMITE to 5,
            ClanId.RAVNOS to 5,
            ClanId.FOLLOWERS_OF_SET to 5,
            ClanId.TZIMISCE to 5,
            ClanId.CAITIFF to 5
        )
        val total = weights.values.sum()
        var roll = Random.nextInt(total)
        for ((clan, weight) in weights) {
            roll -= weight
            if (roll < 0) return clan
        }
        return ClanId.CAITIFF
    }

    private fun randomDistribution(primary: Int, secondary: Int, tertiary: Int): Triple<Int, Int, Int> {
        val order = listOf(primary, secondary, tertiary).shuffled()
        return Triple(order[0], order[1], order[2])
    }

    private fun randomAttributes(distribution: Triple<Int, Int, Int>): List<AttributeValue> {
        val categories = AttributeCategory.entries.shuffled()
        val points = listOf(distribution.first, distribution.second, distribution.third)

        val result = mutableListOf<AttributeValue>()
        categories.forEachIndexed { index, category ->
            val categoryAttrs = AttributeId.entries.filter { it.category == category }
            val dotsToSpend = points[index]
            val values = distributeDots(dotsToSpend, categoryAttrs.size, 1, 5)
            categoryAttrs.forEachIndexed { attrIndex, attrId ->
                result.add(AttributeValue(attrId, values[attrIndex]))
            }
        }
        return result
    }

    private fun randomAbilities(distribution: Triple<Int, Int, Int>): List<AbilityValue> {
        val categories = AbilityCategory.entries.shuffled()
        val points = listOf(distribution.first, distribution.second, distribution.third)

        val result = mutableListOf<AbilityValue>()
        categories.forEachIndexed { index, category ->
            val categoryAbilities = AbilityId.entries.filter { it.category == category }
            val dotsToSpend = points[index]
            val values = distributeDots(dotsToSpend, categoryAbilities.size, 0, 3)
            categoryAbilities.forEachIndexed { abilityIndex, abilityId ->
                result.add(AbilityValue(abilityId, values[abilityIndex]))
            }
        }
        return result
    }

    private fun distributeDots(totalDots: Int, slots: Int, minPerSlot: Int, maxPerSlot: Int): List<Int> {
        var remaining = totalDots
        val values = MutableList(slots) { minPerSlot }
        remaining -= slots * minPerSlot

        while (remaining > 0) {
            val idx = Random.nextInt(slots)
            if (values[idx] < maxPerSlot) {
                values[idx]++
                remaining--
            }
        }
        return values
    }

    private fun randomDisciplines(clan: ClanId, pointsToSpend: Int): List<DisciplineValue> {
        val clanDisciplines = clan.clanDisciplines
        val values = mutableMapOf<DisciplineId, Int>()

        val availableDisciplines = if (clanDisciplines.isEmpty()) {
            DisciplineId.entries.shuffled()
        } else {
            clanDisciplines.shuffled()
        }

        var remaining = pointsToSpend
        for (disc in availableDisciplines) {
            if (remaining <= 0) break
            val dots = minOf(remaining, 3)
            values[disc] = dots
            remaining -= dots
        }

        return values.map { (id, value) -> DisciplineValue(id, value) }
    }

    private fun randomBackgrounds(pointsToSpend: Int): List<BackgroundValue> {
        val allBackgrounds = BackgroundId.entries.shuffled()
        val values = mutableMapOf<BackgroundId, Int>()

        var remaining = pointsToSpend
        for (bg in allBackgrounds) {
            if (remaining <= 0) break
            val dots = minOf(remaining, Random.nextInt(1, 4))
            values[bg] = dots
            remaining -= dots
        }

        return values.map { (id, value) -> BackgroundValue(id, value) }
    }

    private fun randomVirtues(totalPoints: Int, sect: SectId): List<VirtueValue> {
        var remaining = totalPoints - 3
        val virtueIds = if (sect == SectId.SABBAT) {
            VirtueId.sabbatVirtues()
        } else {
            VirtueId.defaultVirtues()
        }

        val values = mutableMapOf<VirtueId, Int>()
        virtueIds.forEach { values[it] = 1 }

        while (remaining > 0) {
            val randomVirtue = virtueIds.random()
            if ((values[randomVirtue] ?: 1) < 5) {
                values[randomVirtue] = (values[randomVirtue] ?: 1) + 1
                remaining--
            }
        }

        return values.map { (id, value) -> VirtueValue(id, value) }
    }

    private fun rollInitialBloodPool(max: Int): Int {
        val roll = Random.nextInt(1, 11)
        return minOf(roll, max)
    }

    private fun randomMerits(): List<MeritValue> {
        val possibleMerits = listOf(
            MeritValue(id = "merit.ambidextrous", name = "Ambidextrous", cost = 2, description = "Use both hands equally well"),
            MeritValue(id = "merit.blush.of.life", name = "Blush of Life", cost = 1, description = "Look alive in daylight"),
            MeritValue(id = "merit.danger.sense", name = "Danger Sense", cost =2, description = "Preternatural awareness of threats"),
            MeritValue(id = "merit.eidetic.memory", name = "Eidetic Memory", cost = 1, description = "Perfect recall"),
            MeritValue(id = "merit.empathy", name = "Empathy", cost = 1, description = "Read emotions easily"),
            MeritValue(id = "merit.harmless", name = "Harmless", cost = 1, description = "People underestimate you"),
            MeritValue(id = "merit.iron.will", name = "Iron Will", cost = 2, description = "Resist mental influence"),
            MeritValue(id = "merit.light sleeper", name = "Light Sleeper", cost = 1, description = "Wake easily"),
            MeritValue(id = "merit.medium", name = "Medium", cost = 1, description = "Sense ghosts and spirits"),
            MeritValue(id = "merit.restaurant", name = "Restaurant", cost = 2, description = "Own a functioning restaurant")
        )
        val count = Random.nextInt(0, 3)
        return possibleMerits.shuffled().take(count)
    }

    private fun randomFlaws(maxValue: Int): List<FlawValue> {
        val possibleFlaws = listOf(
            FlawValue(id = "flaw.addiction", name = "Addiction", value = 1, description = "Cannot resist a substance"),
            FlawValue(id = "flaw.amnesia", name = "Amnesia", value = 1, description = "Frequent memory gaps"),
            FlawValue(id = "flaw.bestial temperament", name = "Bestial Temperament", value = 2, description = "Prone to animalistic rages"),
            FlawValue(id = "flaw.blood dependency", name = "Blood Dependency", value = 2, description = "Cannot survive without regular blood"),
            FlawValue(id = "flaw.bully", name = "Bully", value = 1, description = "Dominate those weaker"),
            FlawValue(id = "flaw.conscience", name = "Conscience", value = 1, description = "Strong moral compass"),
            FlawValue(id = "flaw.dark secret", name = "Dark Secret", value = 1, description = "A dangerous secret"),
            FlawValue(id = "flaw.deformity", name = "Deformity", value = 2, description = "Visible monstrous feature"),
            FlawValue(id = "flaw.doomed", name = "Doomed", value = 3, description = "Death or worse is inevitable"),
            FlawValue(id = "flaw.fool", name = "Fool", value = 1, description = "Make unwise decisions"),
            FlawValue(id = "flaw.greed", name = "Greed", value = 1, description = "Cannot resist material wealth"),
            FlawValue(id = "flaw.introvert", name = "Introvert", value = 1, description = "Avoid social situations"),
            FlawValue(id = "flaw.lame", name = "Lame", value = 2, description = "Cannot walk normally"),
            FlawValue(id = "flaw.one.eye", name = "One Eye", value = 1, description = "Missing an eye"),
            FlawValue(id = "flaw.plancks accelerator", name = "Pacifist", value = 2, description = "Cannot harm others"),
            FlawValue(id = "flaw.prince of lies", name = "Prince of Lies", value = 1, description = "Compulsive liar"),
            FlawValue(id = "flaw.short fuse", name = "Short Fuse", value = 1, description = "Quick to anger"),
            FlawValue(id = "flaw.softhearted", name = "Softhearted", value = 1, description = "Cannot bear suffering"),
            FlawValue(id = "flaw.stumbling", name = "Stumbling", value = 1, description = "Move awkwardly"),
            FlawValue(id = "flaw.vow", name = "Vow", value = 1, description = "Bound by a solemn oath"),
            FlawValue(id = "flaw.warm daylight", name = "Warm Daylight", value = 2, description = "Suffer more in sunlight")
        )

        var total = 0
        val selected = mutableListOf<FlawValue>()
        for (flaw in possibleFlaws.shuffled()) {
            if (total + flaw.value > maxValue) break
            selected.add(flaw)
            total += flaw.value
            if (selected.size >= 3) break
        }
        return selected
    }

    private fun randomEquipment(): List<EquipmentItem> {
        val items = listOf(
            "Cell phone", "Wallet", "Keys", "Leather jacket", "Pocket knife",
            "Sunglasses", "Credit card", "现金", "Lighter", "Watch",
            "Leather bag", "Business cards", "Umbrella", "Flashlight", "First aid kit"
        )
        val count = Random.nextInt(2, 6)
        return items.shuffled().take(count).mapIndexed { index, item ->
            EquipmentItem(
                id = "equip_$index",
                name = item,
                description = "",
                quantity = 1
            )
        }
    }

    private fun generateConcept(clan: ClanId, attributes: List<AttributeValue>, abilities: List<AbilityValue>): String {
        val maxAttr = attributes.maxByOrNull { it.value }
        val maxAbility = abilities.filter { it.value > 0 }.maxByOrNull { it.value }

        val attrWord = when (maxAttr?.id) {
            AttributeId.STRENGTH -> "Strong"
            AttributeId.DEXTERITY -> "Agile"
            AttributeId.STAMINA -> "Tough"
            AttributeId.CHARISMA -> "Charming"
            AttributeId.MANIPULATION -> "Cunning"
            AttributeId.APPEARANCE -> "Beautiful"
            AttributeId.PERCEPTION -> "Perceptive"
            AttributeId.INTELLIGENCE -> "Brilliant"
            AttributeId.WITS -> "Quick"
            else -> "Mysterious"
        }

        val clanWord = when (clan) {
            ClanId.BRUAH -> "Rebel"
            ClanId.VENTRUE -> "Leader"
            ClanId.TOREADOR -> "Artist"
            ClanId.MALKAVIAN -> "Oracle"
            ClanId.NOSFERATU -> "Spy"
            ClanId.TREMERE -> "Sorcerer"
            ClanId.GANGREL -> "Wild"
            ClanId.LASOMBRA -> "Shade"
            ClanId.GIOVANNI -> "Merchant"
            ClanId.ASSAMITE -> "Judge"
            ClanId.RAVNOS -> "Trickster"
            ClanId.FOLLOWERS_OF_SET -> "Serpent"
            ClanId.TZIMISCE -> "Shaper"
            ClanId.CAITIFF -> "Outcast"
        }

        return "$attrWord $clanWord"
    }
}
