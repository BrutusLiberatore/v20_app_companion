package com.v20charactermanager.domain.definition

enum class ClanId(
    val id: String,
    val nameIt: String,
    val nameEn: String,
    val defaultSect: SectId,
    val clanDisciplines: List<DisciplineId>,
    val weakness: ClanWeakness,
    val requiredChoices: List<RequiredChoice>,
    val automaticCreationEffects: List<AutomaticCreationEffect>,
    val creationHints: CreationHints = CreationHints()
) {
    ASSAMITE(
        id = "clan.assamite",
        nameIt = "Assamiti",
        nameEn = "Assamite",
        defaultSect = SectId.INDEPENDENT,
        clanDisciplines = listOf(DisciplineId.OBFUSCATE, DisciplineId.QUIETUS, DisciplineId.CELERITY),
        weakness = ClanWeakness.KindredBloodCurse(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.PHYSICAL, AttributeCategory.SOCIAL),
            recommendedAbilityCategories = listOf(AbilityCategory.TALENTS, AbilityCategory.SKILLS)
        )
    ),
    BRUAH(
        id = "clan.brujah",
        nameIt = "Brujah",
        nameEn = "Brujah",
        defaultSect = SectId.CAMARILLA,
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.POTENCE, DisciplineId.CELERITY),
        weakness = ClanWeakness.Frenzy(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.PHYSICAL),
            recommendedAbilityCategories = listOf(AbilityCategory.SKILLS, AbilityCategory.TALENTS)
        )
    ),
    GANGREL(
        id = "clan.gangrel",
        nameIt = "Gangrel",
        nameEn = "Gangrel",
        defaultSect = SectId.CAMARILLA,
        clanDisciplines = listOf(DisciplineId.ANIMALISM, DisciplineId.PROTEAN, DisciplineId.FORTITUDE),
        weakness = ClanWeakness.FrenzyAnimalTrait(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.PHYSICAL),
            recommendedAbilityCategories = listOf(AbilityCategory.TALENTS, AbilityCategory.SKILLS, AbilityCategory.KNOWLEDGES)
        )
    ),
    GIOVANNI(
        id = "clan.giovanni",
        nameIt = "Giovanni",
        nameEn = "Giovanni",
        defaultSect = SectId.INDEPENDENT,
        clanDisciplines = listOf(DisciplineId.DOMINATE, DisciplineId.NECROMANCY, DisciplineId.POTENCE),
        weakness = ClanWeakness.PainfulKiss(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.SOCIAL, AttributeCategory.MENTAL, AttributeCategory.PHYSICAL),
            recommendedAbilityCategories = listOf(AbilityCategory.KNOWLEDGES, AbilityCategory.TALENTS)
        )
    ),
    LASOMBRA(
        id = "clan.lasombra",
        nameIt = "Lasombra",
        nameEn = "Lasombra",
        defaultSect = SectId.SABBAT,
        clanDisciplines = listOf(DisciplineId.DOMINATE, DisciplineId.OBTENEBRATION, DisciplineId.POTENCE),
        weakness = ClanWeakness.NoReflection(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.MENTAL, AttributeCategory.SOCIAL),
            noteIt = "Nessuna categoria fissa; le build tendono a essere specializzate.",
            noteEn = "No fixed category bonus; builds tend to be specialized."
        )
    ),
    MALKAVIAN(
        id = "clan.malkavian",
        nameIt = "Malkavian",
        nameEn = "Malkavian",
        defaultSect = SectId.CAMARILLA,
        clanDisciplines = listOf(DisciplineId.AUSPEX, DisciplineId.DEMENTATION, DisciplineId.OBFUSCATE),
        weakness = ClanWeakness.PermanentDerangement(),
        requiredChoices = listOf(RequiredChoice.DerangementChoice()),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.MENTAL),
            recommendedAbilityCategories = listOf(AbilityCategory.TALENTS, AbilityCategory.KNOWLEDGES)
        )
    ),
    NOSFERATU(
        id = "clan.nosferatu",
        nameIt = "Nosferatu",
        nameEn = "Nosferatu",
        defaultSect = SectId.CAMARILLA,
        clanDisciplines = listOf(DisciplineId.ANIMALISM, DisciplineId.OBFUSCATE, DisciplineId.POTENCE),
        weakness = ClanWeakness.AppearanceZero(),
        requiredChoices = emptyList(),
        automaticCreationEffects = listOf(
            AutomaticCreationEffect(
                operation = "set_and_lock",
                traitId = "attribute.appearance",
                value = 0,
                refundCreationDots = false,
                note = "Appearance is an exception to the normal rule that Attributes begin at 1."
            )
        ),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.PHYSICAL, AttributeCategory.MENTAL),
            recommendedAbilityCategories = listOf(AbilityCategory.TALENTS, AbilityCategory.SKILLS, AbilityCategory.KNOWLEDGES)
        )
    ),
    RAVNOS(
        id = "clan.ravnos",
        nameIt = "Ravnos",
        nameEn = "Ravnos",
        defaultSect = SectId.INDEPENDENT,
        clanDisciplines = listOf(DisciplineId.ANIMALISM, DisciplineId.CHIMESTRY, DisciplineId.FORTITUDE),
        weakness = ClanWeakness.ViceCompulsion(),
        requiredChoices = listOf(RequiredChoice.ViceChoice()),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.PHYSICAL, AttributeCategory.SOCIAL),
            recommendedAbilityCategories = listOf(AbilityCategory.TALENTS, AbilityCategory.SKILLS)
        )
    ),
    FOLLOWERS_OF_SET(
        id = "clan.followersOfSet",
        nameIt = "Seguaci di Set",
        nameEn = "Followers of Set",
        defaultSect = SectId.INDEPENDENT,
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.OBFUSCATE, DisciplineId.SERPENTIS),
        weakness = ClanWeakness.LightSensitivity(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.SOCIAL, AttributeCategory.MENTAL),
            recommendedAbilityCategories = listOf(AbilityCategory.KNOWLEDGES, AbilityCategory.TALENTS)
        )
    ),
    TOREADOR(
        id = "clan.toreador",
        nameIt = "Toreador",
        nameEn = "Toreador",
        defaultSect = SectId.CAMARILLA,
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.AUSPEX, DisciplineId.CELERITY),
        weakness = ClanWeakness.AestheticEntrancement(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.SOCIAL),
            noteIt = "La priorità delle abilità segue la focus artistica/sociale scelta.",
            noteEn = "Ability priority follows the chosen artistic/social focus."
        )
    ),
    TREMERE(
        id = "clan.tremere",
        nameIt = "Tremere",
        nameEn = "Tremere",
        defaultSect = SectId.CAMARILLA,
        clanDisciplines = listOf(DisciplineId.AUSPEX, DisciplineId.DOMINATE, DisciplineId.THAUMATURGY),
        weakness = ClanWeakness.BloodBondSusceptibility(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.MENTAL),
            recommendedAbilityCategories = listOf(AbilityCategory.KNOWLEDGES)
        )
    ),
    TZIMISCE(
        id = "clan.tzimisce",
        nameIt = "Tzimisce",
        nameEn = "Tzimisce",
        defaultSect = SectId.SABBAT,
        clanDisciplines = listOf(DisciplineId.ANIMALISM, DisciplineId.AUSPEX, DisciplineId.VICISSITUDE),
        weakness = ClanWeakness.NativeSoilDependency(),
        requiredChoices = listOf(RequiredChoice.NativeSoilChoice()),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.PHYSICAL, AttributeCategory.MENTAL),
            recommendedAbilityCategories = listOf(AbilityCategory.KNOWLEDGES, AbilityCategory.SKILLS)
        )
    ),
    VENTRUE(
        id = "clan.ventrue",
        nameIt = "Ventrue",
        nameEn = "Ventrue",
        defaultSect = SectId.CAMARILLA,
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.DOMINATE, DisciplineId.FORTITUDE),
        weakness = ClanWeakness.FeedingRestriction(),
        requiredChoices = listOf(RequiredChoice.FeedingRestrictionChoice()),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            recommendedAttributes = listOf(AttributeCategory.SOCIAL, AttributeCategory.MENTAL),
            noteIt = "Qualsiasi categoria può essere primaria secondo l'expertise.",
            noteEn = "Any category can be primary according to expertise."
        )
    ),
    CAITIFF(
        id = "clan.caitiff",
        nameIt = "Caitiff",
        nameEn = "Caitiff",
        defaultSect = SectId.CAMARILLA,
        clanDisciplines = emptyList(),
        weakness = ClanWeakness.Clanless(),
        requiredChoices = emptyList(),
        automaticCreationEffects = emptyList(),
        creationHints = CreationHints(
            noteIt = "Nessuna preferenza di clan.",
            noteEn = "No clan preference."
        )
    );

    val weaknessDescriptionIt: String get() = weakness.descriptionIt
    val weaknessDescriptionEn: String get() = weakness.descriptionEn

    companion object {
        fun fromId(id: String): ClanId? =
            entries.find { it.id == id }
    }
}
