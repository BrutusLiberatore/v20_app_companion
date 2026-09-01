package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.*
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: String,
    val identity: CharacterIdentity = CharacterIdentity(),
    val attributes: List<AttributeValue> = defaultAttributes(),
    val abilities: List<AbilityValue> = defaultAbilities(),
    val disciplines: List<DisciplineValue> = emptyList(),
    val backgrounds: List<BackgroundValue> = emptyList(),
    val virtues: List<VirtueValue> = defaultVirtues(),
    val moralPath: MoralPath = MoralPath(),
    val merits: List<MeritValue> = emptyList(),
    val flaws: List<FlawValue> = emptyList(),
    val health: HealthState = HealthState(),
    val bloodPool: BloodPoolState = BloodPoolState(),
    val willpower: WillpowerState = WillpowerState(),
    val experience: ExperienceState = ExperienceState(),
    val equipment: List<EquipmentItem> = emptyList(),
    val narrative: CharacterNarrative = CharacterNarrative(),
    val notes: String = "",
    val portraitUri: String? = null,
    val creationStep: Int = 0,
    val isComplete: Boolean = false,
    val importMetadata: ImportMetadata? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val attributeByCategory: Map<AttributeCategory, List<AttributeValue>>
        get() = attributes.groupBy { it.id.category }

    val abilityByCategory: Map<AbilityCategory, List<AbilityValue>>
        get() = abilities.groupBy { it.id.category }

    val isSabbat: Boolean
        get() = identity.sect == SectId.SABBAT

    fun getAttributeValue(attributeId: AttributeId): Int =
        attributes.find { it.id == attributeId }?.value ?: 1

    fun getAbilityValue(abilityId: AbilityId): Int =
        abilities.find { it.id == abilityId }?.value ?: 0

    fun getDisciplineValue(disciplineId: DisciplineId): Int =
        disciplines.find { it.id == disciplineId }?.value ?: 0

    fun getBackgroundValue(backgroundId: BackgroundId): Int =
        backgrounds.find { it.id == backgroundId }?.value ?: 0

    fun getVirtueValue(virtueId: VirtueId): Int =
        virtues.find { it.id == virtueId }?.value ?: 1

    fun setAttributeValue(attributeId: AttributeId, value: Int): Character {
        require(value in 1..5) { "Attribute value must be between 1 and 5" }
        val updated = attributes.map {
            if (it.id == attributeId) it.copy(value = value) else it
        }
        return copy(attributes = updated)
    }

    fun setAbilityValue(abilityId: AbilityId, value: Int): Character {
        require(value in 0..5) { "Ability value must be between 0 and 5" }
        val updated = abilities.map {
            if (it.id == abilityId) it.copy(value = value) else it
        }
        return copy(abilities = updated)
    }

    fun addDiscipline(disciplineId: DisciplineId, value: Int = 1): Character {
        if (disciplines.any { it.id == disciplineId }) return this
        return copy(disciplines = disciplines + DisciplineValue(disciplineId, value))
    }

    fun removeDiscipline(disciplineId: DisciplineId): Character {
        return copy(disciplines = disciplines.filter { it.id != disciplineId })
    }

    fun addBackground(backgroundId: BackgroundId, value: Int = 1, notes: String? = null): Character {
        if (backgrounds.any { it.id == backgroundId }) return this
        return copy(backgrounds = backgrounds + BackgroundValue(backgroundId, value, notes))
    }

    fun removeBackground(backgroundId: BackgroundId): Character {
        return copy(backgrounds = backgrounds.filter { it.id != backgroundId })
    }

    fun setVirtueValue(virtueId: VirtueId, value: Int): Character {
        require(value in 1..5) { "Virtue value must be between 1 and 5" }
        val updated = virtues.map {
            if (it.id == virtueId) it.copy(value = value) else it
        }
        val conscienceValue = updated.find { it.id == VirtueId.CONSCIENCE }?.value ?: 0
        val selfControlValue = updated.find { it.id == VirtueId.SELF_CONTROL }?.value ?: 0
        val courageValue = updated.find { it.id == VirtueId.COURAGE }?.value ?: 1
        val convictionValue = updated.find { it.id == VirtueId.CONVICTION }?.value ?: 0
        val instinctValue = updated.find { it.id == VirtueId.INSTINCT }?.value ?: 0
        return copy(
            virtues = updated,
            moralPath = moralPath.copy(
                conscienceValue = conscienceValue,
                selfControlValue = selfControlValue,
                courageValue = courageValue,
                convictionValue = convictionValue,
                instinctValue = instinctValue
            )
        )
    }

    fun addMerit(merit: MeritValue): Character {
        if (merits.any { it.id == merit.id }) return this
        return copy(merits = merits + merit)
    }

    fun removeMerit(meritId: String): Character {
        return copy(merits = merits.filter { it.id != meritId })
    }

    fun addFlaw(flaw: FlawValue): Character {
        if (flaws.any { it.id == flaw.id }) return this
        return copy(flaws = flaws + flaw)
    }

    fun removeFlaw(flawId: String): Character {
        return copy(flaws = flaws.filter { it.id != flawId })
    }

    fun addEquipment(item: EquipmentItem): Character {
        return copy(equipment = equipment + item)
    }

    fun removeEquipment(itemId: String): Character {
        return copy(equipment = equipment.filter { it.id != itemId })
    }

    fun updateEquipment(item: EquipmentItem): Character {
        return copy(equipment = equipment.map { if (it.id == item.id) item else it })
    }
}

fun defaultAttributes(): List<AttributeValue> =
    AttributeId.entries.map { AttributeValue(it, 1) }

fun defaultAbilities(): List<AbilityValue> =
    AbilityId.entries.map { AbilityValue(it, 0) }

fun defaultVirtues(): List<VirtueValue> =
    VirtueId.defaultVirtues().map { VirtueValue(it, 1) }

fun sabbatVirtues(): List<VirtueValue> =
    VirtueId.sabbatVirtues().map { VirtueValue(it, 1) }
