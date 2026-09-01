package com.v20charactermanager.domain.definition

enum class AbilityCategory {
    TALENTS,
    SKILLS,
    KNOWLEDGES
}

enum class AbilityId(
    val id: String,
    val category: AbilityCategory,
    val nameIt: String,
    val nameEn: String
) {
    // Talents (Attitudini) — DOCX: 9 entries
    ALERTNESS("ability.talents.alertness", AbilityCategory.TALENTS, "Allerta", "Alertness"),
    ATHLETICS("ability.talents.athletics", AbilityCategory.TALENTS, "Atletica", "Athletics"),
    EXPRESSION("ability.talents.expression", AbilityCategory.TALENTS, "Espressività", "Expression"),
    EMPATHY("ability.talents.empathy", AbilityCategory.TALENTS, "Empatia", "Empathy"),
    INTIMIDATE("ability.talents.intimidate", AbilityCategory.TALENTS, "Intimidire", "Intimidate"),
    INTUITION("ability.talents.intuition", AbilityCategory.TALENTS, "Intuizione", "Intuition"),
    LEADERSHIP("ability.talents.leadership", AbilityCategory.TALENTS, "Leadership", "Leadership"),
    SUBTERFUGE("ability.talents.subterfuge", AbilityCategory.TALENTS, "Sotterfugio", "Subterfuge"),
    STREETWISE("ability.talents.streetwise", AbilityCategory.TALENTS, "Conoscenza della Strada", "Streetwise"),

    // Skills (Capacità) — DOCX: 10 entries
    ANIMAL_KEN("ability.skills.animalKen", AbilityCategory.SKILLS, "Addestrare Animali", "Animal Ken"),
    CRAFTS("ability.skills.crafts", AbilityCategory.SKILLS, "Artigianato", "Crafts"),
    FIREARMS("ability.skills.firearms", AbilityCategory.SKILLS, "Armi da Fuoco", "Firearms"),
    MELEE("ability.skills.melee", AbilityCategory.SKILLS, "Armi da Mischia", "Melee"),
    STEALTH("ability.skills.stealth", AbilityCategory.SKILLS, "Furtività", "Stealth"),
    DRIVE("ability.skills.drive", AbilityCategory.SKILLS, "Guidare", "Drive"),
    LARCENY("ability.skills.larceny", AbilityCategory.SKILLS, "Manualità", "Larceny"),
    PERFORMANCE("ability.skills.performance", AbilityCategory.SKILLS, "Musica", "Performance"),
    SECURITY("ability.skills.security", AbilityCategory.SKILLS, "Sicurezza", "Security"),
    SURVIVAL("ability.skills.survival", AbilityCategory.SKILLS, "Sopravvivenza", "Survival"),

    // Knowledges (Conoscenze) — DOCX: 9 entries
    ACADEMICS("ability.knowledges.academics", AbilityCategory.KNOWLEDGES, "Accademiche", "Academics"),
    COMPUTER("ability.knowledges.computer", AbilityCategory.KNOWLEDGES, "Computer", "Computer"),
    FINANCE("ability.knowledges.finance", AbilityCategory.KNOWLEDGES, "Finanza", "Finance"),
    INVESTIGATION("ability.knowledges.investigation", AbilityCategory.KNOWLEDGES, "Investigazione", "Investigation"),
    LAW("ability.knowledges.law", AbilityCategory.KNOWLEDGES, "Legge", "Law"),
    MEDICINE("ability.knowledges.medicine", AbilityCategory.KNOWLEDGES, "Medicina", "Medicine"),
    OCCULT("ability.knowledges.occult", AbilityCategory.KNOWLEDGES, "Occulto", "Occult"),
    POLITICS("ability.knowledges.politics", AbilityCategory.KNOWLEDGES, "Politica", "Politics"),
    SCIENCE("ability.knowledges.science", AbilityCategory.KNOWLEDGES, "Scienze", "Science");

    companion object {
        fun byCategory(category: AbilityCategory): List<AbilityId> =
            entries.filter { it.category == category }

        fun fromId(id: String): AbilityId? =
            entries.find { it.id == id }
    }
}
