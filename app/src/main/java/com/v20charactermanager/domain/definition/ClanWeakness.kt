package com.v20charactermanager.domain.definition

import kotlinx.serialization.Serializable

@Serializable
sealed interface ClanWeakness {
    val type: String
    val descriptionIt: String
    val descriptionEn: String

    @Serializable
    data class KindredBloodCurse(
        val damageType: String = "lethal",
        val automaticLevelsPerBloodPoint: Int = 1,
        val soakable: Boolean = false
    ) : ClanWeakness {
        override val type = "kindred_blood_curse"
        override val descriptionIt = "Se bevi sangue di vampiro sei tentato dalla diablerie"
        override val descriptionEn = "Drinking vampire blood tempts you toward diablerie"
    }

    @Serializable
    data class Frenzy(
        val resistOrRideFrenzyDifficultyModifier: Int = 2,
        val canSpendWillpowerToAvoidFrenzy: Boolean = false,
        val canSpendWillpowerToEndFrenzy: Boolean = true
    ) : ClanWeakness {
        override val type = "frenzy"
        override val descriptionIt = "Impulsivi: difficoltà +2 per resistere alla frenesia"
        override val descriptionEn = "Impulsive: +2 difficulty to resist frenzy"
    }

    @Serializable
    data class LightSensitivity(
        val sunlightAdditionalHealthLevels: Int = 2,
        val brightLightDicePenalty: Int = 1
    ) : ClanWeakness {
        override val type = "light_sensitivity"
        override val descriptionIt = "Doppia vulnerabilità a luce solare e fuoco"
        override val descriptionEn = "Double vulnerability to sunlight and fire"
    }

    @Serializable
    data class FrenzyAnimalTrait(
        val onEachFrenzy: String = "Guadagna un tratto animale temporaneo",
        val mayBecomePermanent: Boolean = true
    ) : ClanWeakness {
        override val type = "frenzy_animal_trait"
        override val descriptionIt = "Ogni frenesia lascia un tratto animale finché non recuperi"
        override val descriptionEn = "Each frenzy leaves an animal trait until recovered"
    }

    @Serializable
    data class PainfulKiss(
        val appliesTo: String = "mortal_vessels",
        val healthDamagePerBloodPointTaken: Int = 2
    ) : ClanWeakness {
        override val type = "painful_kiss"
        override val descriptionIt = "Il morso infligge danni aggravati e non dà piacere alla preda"
        override val descriptionEn = "The bite inflicts aggravated damage and gives no sustenance"
    }

    @Serializable
    data class NoReflection(
        val affectedSurfaces: List<String> = listOf("mirrors", "water", "polished_reflective_surfaces")
    ) : ClanWeakness {
        override val type = "no_reflection"
        override val descriptionIt = "Non produci riflesso in specchi né su superfici"
        override val descriptionEn = "You cast no reflection in mirrors or on surfaces"
    }

    @Serializable
    data class PermanentDerangement(
        val incurableOriginalDerangement: Boolean = true,
        val canSpendWillpowerToAmeliorateForScene: Boolean = true,
        val canAcquireOtherDerangements: Boolean = true
    ) : ClanWeakness {
        override val type = "permanent_derangement"
        override val descriptionIt = "Sono tutti afflitti da una forma di follia incurabile"
        override val descriptionEn = "All are afflicted with an incurable form of madness"
    }

    @Serializable
    data class AppearanceZero(
        val attributeId: String = "appearance",
        val forcedValue: Int = 0,
        val maxValue: Int = 0,
        val canImprove: Boolean = false
    ) : ClanWeakness {
        override val type = "appearance_zero"
        override val descriptionIt = "Deformi: Aspetto 0, nessun tiro sociale basato sulla bellezza"
        override val descriptionEn = "Deformed: Appearance 0, no social rolls based on looks"
    }

    @Serializable
    data class ViceCompulsion(
        val resistTrait: String = "self_control_or_instinct",
        val difficulty: Int = 6
    ) : ClanWeakness {
        override val type = "vice_compulsion"
        override val descriptionIt = "Ognuno è schiavo di un vizio particolare del clan"
        override val descriptionEn = "Each is slave to a particular clan vice"
    }

    @Serializable
    data class AestheticEntrancement(
        val trigger: String = "something genuinely remarkable/beautiful",
        val resistTrait: String = "self_control_or_instinct",
        val difficulty: Int = 6,
        val onFailure: String = "entranced_for_scene_or_until_stimulus_ends",
        val rerollIfWounded: Boolean = true
    ) : ClanWeakness {
        override val type = "aesthetic_entrancement"
        override val descriptionIt = "Estasi: dinanzi alla bellezza rischi di restarne rapito"
        override val descriptionEn = "Ecstasy: in the presence of beauty you risk being enraptured"
    }

    @Serializable
    data class BloodBondSusceptibility(
        val drinksToFullBond: Int = 2,
        val firstDrinkCountsAsStage: Int = 2
    ) : ClanWeakness {
        override val type = "blood_bond_susceptibility"
        override val descriptionIt = "Legati da tre sorsi del sangue degli anziani del clan"
        override val descriptionEn = "Bound by three draughts of blood from clan elders"
    }

    @Serializable
    data class NativeSoilDependency(
        val minimumNativeSoil: String = "at least two handfuls",
        val onNightWithoutSoil: String = "halve all dice pools cumulatively, minimum 1 die",
        val recovery: String = "rest a full day with native soil"
    ) : ClanWeakness {
        override val type = "native_soil_dependency"
        override val descriptionIt = "Devi dormire circondato da terra della tua patria"
        override val descriptionEn = "You must sleep surrounded by soil from your homeland"
    }

    @Serializable
    data class FeedingRestriction(
        val restrictionAppliesTo: String = "mortal_blood",
        val otherMortalOrAnimalBloodGrantsBloodPool: Boolean = false,
        val vampiricBloodException: Boolean = true,
        val choicePermanent: Boolean = true
    ) : ClanWeakness {
        override val type = "feeding_restriction"
        override val descriptionIt = "Palato esigente: puoi nutrirti solo da un tipo di preda"
        override val descriptionEn = "Discerning palate: you can only feed from one type of prey"
    }

    @Serializable
    data class Clanless(
        val coreNote: String = "No inherited Clan Disciplines or Clan weakness"
    ) : ClanWeakness {
        override val type = "clanless"
        override val descriptionIt = "Senza clan: nessuno ti rispetta, costi PE più alti"
        override val descriptionEn = "No clan: no one respects you, higher XP costs"
    }
}

@Serializable
sealed interface RequiredChoice {
    val id: String
    val type: String
    val required: Boolean
    val promptIt: String
    val promptEn: String

    @Serializable
    data class DerangementChoice(
        override val id: String = "malkavian_derangement",
        override val required: Boolean = true,
        val lockedAsClanWeakness: Boolean = true
    ) : RequiredChoice {
        override val type = "derangement"
        override val promptIt = "Scegli la tua derangement (follia) — obbligatoria per il clan Malkavian"
        override val promptEn = "Choose your derangement — mandatory for Clan Malkavian"
    }

    @Serializable
    data class ViceChoice(
        override val id: String = "ravnos_vice",
        override val required: Boolean = true,
        val examples: List<String> = listOf("lying", "cruelty", "theft")
    ) : RequiredChoice {
        override val type = "free_text_or_enum"
        override val promptIt = "Scegli il tuo vizio di clan Ravnos — esempi: bugia, crudeltà, furto"
        override val promptEn = "Choose your Ravnos clan vice — examples: lying, cruelty, theft"
    }

    @Serializable
    data class FeedingRestrictionChoice(
        override val id: String = "ventrue_feeding_restriction",
        override val required: Boolean = true,
        val permanent: Boolean = true
    ) : RequiredChoice {
        override val type = "free_text_or_tagged_rule"
        override val promptIt = "Specifica la tua restrizione alimentare Ventrue — permanente"
        override val promptEn = "Specify your Ventrue feeding restriction — permanent"
    }

    @Serializable
    data class NativeSoilChoice(
        override val id: String = "native_soil_origin",
        override val required: Boolean = true,
        val description: String = "Place important to the character's mortal life/Embrace from which the soil originates."
    ) : RequiredChoice {
        override val type = "free_text"
        override val promptIt = "Descrivi l'origine della terra natia Tzimisce — luogo legato alla vita mortale o all'Embrace"
        override val promptEn = "Describe the origin of your Tzimisce native soil — a place tied to mortal life or the Embrace"
    }
}

@Serializable
data class AutomaticCreationEffect(
    val operation: String,
    val traitId: String,
    val value: Int,
    val refundCreationDots: Boolean = false,
    val note: String = ""
)
