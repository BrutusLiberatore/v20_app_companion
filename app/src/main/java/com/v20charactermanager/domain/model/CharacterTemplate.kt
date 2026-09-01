package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.*

enum class CharacterTemplate(
    val nameEn: String,
    val nameIt: String,
    val descriptionEn: String,
    val descriptionIt: String,
    val clan: ClanId,
    val archetype: String
) {
    BRUAH_WARRIOR(
        nameEn = "Brujah Warrior", nameIt = "Guerriero Brujah",
        descriptionEn = "A fierce fighter driven by passion and rage",
        descriptionIt = "Un feroce combattente spinto da passione e rabbia",
        clan = ClanId.BRUAH,
        archetype = "warrior"
    ),
    VENTRUE_POLITICIAN(
        nameEn = "Ventrue Politician", nameIt = "Politico Ventrue",
        descriptionEn = "A natural leader who commands through presence and authority",
        descriptionIt = "Un leader naturale che comanda con presenza e autorità",
        clan = ClanId.VENTRUE,
        archetype = "social"
    ),
    TOREADOR_ARTIST(
        nameEn = "Toreador Artist", nameIt = "Artista Toreador",
        descriptionEn = "An aesthete haunted by beauty and artistic obsession",
        descriptionIt = "Un esteta tormentato da bellezza e ossessione artistica",
        clan = ClanId.TOREADOR,
        archetype = "social"
    ),
    MALKAVIAN_ORACLE(
        nameEn = "Malkavian Oracle", nameIt = "Oracolo Malkavian",
        descriptionEn = "A seer whose madness reveals hidden truths",
        descriptionIt = "Un veggente la cui follia rivela verità nascoste",
        clan = ClanId.MALKAVIAN,
        archetype = "mental"
    ),
    NOSFERATU_SPY(
        nameEn = "Nosferatu Spy", nameIt = "Spia Nosferatu",
        descriptionEn = "A deformed information broker hiding in the shadows",
        descriptionIt = "Un informatore deformato che si nasconde nell'ombra",
        clan = ClanId.NOSFERATU,
        archetype = "stealth"
    ),
    TREMERE_SORCERER(
        nameEn = "Tremere Sorcerer", nameIt = "Stregone Tremere",
        descriptionEn = "A methodical blood sorcerer with arcane knowledge",
        descriptionIt = "Uno stregone del sangue metodico con conoscenze arcaniche",
        clan = ClanId.TREMERE,
        archetype = "mental"
    ),
    GANGREL_WILD(
        nameEn = "Gangrel Wildling", nameIt = "Selvaggio Gangrel",
        descriptionEn = "A feral vampire close to nature and animal instincts",
        descriptionIt = "Un vampiro ferino vicino alla natura e agli istinti animali",
        clan = ClanId.GANGREL,
        archetype = "physical"
    ),
    LASOMBRA_SHADOW(
        nameEn = "Lasombra Shadow", nameIt = "Ombra Lasombra",
        descriptionEn = "A manipulator who thrives in darkness and control",
        descriptionIt = "Un manipolatore che prospera nell'oscurità e nel controllo",
        clan = ClanId.LASOMBRA,
        archetype = "social"
    ),
    GIOVANNI_MERCHANT(
        nameEn = "Giovanni Merchant", nameIt = "Mercante Giovanni",
        descriptionEn = "A wealthy merchant dealing in death and commerce",
        descriptionIt = "Un ricco mercante che traffica in morte e commerci",
        clan = ClanId.GIOVANNI,
        archetype = "social"
    ),
    ASSAMITE_JUDGE(
        nameEn = "Assamite Judge", nameIt = "Giudice Assamita",
        descriptionEn = "A deadly executioner serving clan justice",
        descriptionIt = "Un letale esecutore che serve la giustizia del clan",
        clan = ClanId.ASSAMITE,
        archetype = "physical"
    ),
    RAVNOS_TRICKSTER(
        nameEn = "Ravnos Trickster", nameIt = "Ingannatore Ravnos",
        descriptionEn = "A charming deceiver who lives by illusions",
        descriptionIt = "Un affascinante ingannatore che vive di illusioni",
        clan = ClanId.RAVNOS,
        archetype = "social"
    ),
    SETHITE_SERPENT(
        nameEn = "Followers of Set Serpent", nameIt = "Serpente dei Seguaci di Set",
        descriptionEn = "A corruptor who tempts mortals and immortals alike",
        descriptionIt = "Un corruttore che tenta mortali e immortali",
        clan = ClanId.FOLLOWERS_OF_SET,
        archetype = "social"
    ),
    TZIMISCE_SHAPER(
        nameEn = "Tzimisce Shaper", nameIt = "Plasmatore Tzimisce",
        descriptionEn = "A flesh-shaper with terrifying discipline mastery",
        descriptionIt = "Un plasmatore di carne con terrificante padronanza delle discipline",
        clan = ClanId.TZIMISCE,
        archetype = "physical"
    ),
    CAITIFF_OUTCAST(
        nameEn = "Caitiff Outcast", nameIt = "Emarginato Caitiff",
        descriptionEn = "A clanless survivor who forges their own path",
        descriptionIt = "Un sopravvissuto senza clan che forga la propria strada",
        clan = ClanId.CAITIFF,
        archetype = "any"
    ),
    STREET_SCHOLAR(
        nameEn = "Street Scholar", nameIt = "Scholar di Strada",
        descriptionEn = "A knowledgeable vampire who learned from the streets",
        descriptionIt = "Un vampiro colto che ha imparato per le strade",
        clan = ClanId.MALKAVIAN,
        archetype = "mental"
    ),
    COMBAT_SPECIALIST(
        nameEn = "Combat Specialist", nameIt = "Specialista di Combattimento",
        descriptionEn = "A vampire optimized for physical confrontation",
        descriptionIt = "Un vampiro ottimizzato per il confronto fisico",
        clan = ClanId.BRUAH,
        archetype = "physical"
    ),
    INVESTIGATOR(
        nameEn = "Investigator", nameIt = "Investigatore",
        descriptionEn = "A meticulous investigator who uncovers hidden truths",
        descriptionIt = "Un meticoloso investigatore che scopre verità nascoste",
        clan = ClanId.VENTRUE,
        archetype = "mental"
    ),
    NIGHT_PATROL(
        nameEn = "Night Patrol", nameIt = "Pattuglia Notturna",
        descriptionEn = "A street-level enforcer who keeps the peace",
        descriptionIt = "Un applicatore di strada che mantiene la pace",
        clan = ClanId.NOSFERATU,
        archetype = "physical"
    );

    fun applyTemplate(base: Character): Character {
        return when (this) {
            BRUAH_WARRIOR -> base.copy(
                attributes = base.attributes.map {
                    when (it.id) {
                        AttributeId.STRENGTH -> it.copy(value = 4)
                        AttributeId.DEXTERITY -> it.copy(value = 3)
                        AttributeId.STAMINA -> it.copy(value = 3)
                        AttributeId.CHARISMA -> it.copy(value = 2)
                        AttributeId.MANIPULATION -> it.copy(value = 2)
                        AttributeId.APPEARANCE -> it.copy(value = 2)
                        AttributeId.PERCEPTION -> it.copy(value = 2)
                        AttributeId.INTELLIGENCE -> it.copy(value = 2)
                        AttributeId.WITS -> it.copy(value = 3)
                        else -> it
                    }
                },
                abilities = base.abilities.map {
                    when (it.id) {
                        AbilityId.ATHLETICS -> it.copy(value = 3)
                        AbilityId.INTIMIDATE -> it.copy(value = 3)
                        AbilityId.STREETWISE -> it.copy(value = 2)
                        AbilityId.MELEE -> it.copy(value = 3)
                        AbilityId.FIREARMS -> it.copy(value = 2)
                        AbilityId.MELEE -> it.copy(value = 3)
                        else -> it
                    }
                },
                disciplines = listOf(
                    DisciplineValue(DisciplineId.POTENCE, 2),
                    DisciplineValue(DisciplineId.CELERITY, 1)
                ),
                virtues = listOf(
                    VirtueValue(VirtueId.CONSCIENCE, 1),
                    VirtueValue(VirtueId.SELF_CONTROL, 2),
                    VirtueValue(VirtueId.COURAGE, 4)
                )
            )

            VENTRUE_POLITICIAN -> base.copy(
                attributes = base.attributes.map {
                    when (it.id) {
                        AttributeId.STRENGTH -> it.copy(value = 2)
                        AttributeId.DEXTERITY -> it.copy(value = 2)
                        AttributeId.STAMINA -> it.copy(value = 2)
                        AttributeId.CHARISMA -> it.copy(value = 4)
                        AttributeId.MANIPULATION -> it.copy(value = 4)
                        AttributeId.APPEARANCE -> it.copy(value = 3)
                        AttributeId.PERCEPTION -> it.copy(value = 2)
                        AttributeId.INTELLIGENCE -> it.copy(value = 3)
                        AttributeId.WITS -> it.copy(value = 3)
                        else -> it
                    }
                },
                abilities = base.abilities.map {
                    when (it.id) {
                        AbilityId.LEADERSHIP -> it.copy(value = 3)
                        AbilityId.LEADERSHIP -> it.copy(value = 3)
                        AbilityId.STREETWISE -> it.copy(value = 2)
                        AbilityId.STREETWISE -> it.copy(value = 3)
                        AbilityId.SUBTERFUGE -> it.copy(value = 2)
                        AbilityId.ACADEMICS -> it.copy(value = 2)
                        else -> it
                    }
                },
                disciplines = listOf(
                    DisciplineValue(DisciplineId.PRESENCE, 2),
                    DisciplineValue(DisciplineId.DOMINATE, 1)
                ),
                virtues = listOf(
                    VirtueValue(VirtueId.CONSCIENCE, 2),
                    VirtueValue(VirtueId.SELF_CONTROL, 3),
                    VirtueValue(VirtueId.COURAGE, 2)
                )
            )

            TOREADOR_ARTIST -> base.copy(
                attributes = base.attributes.map {
                    when (it.id) {
                        AttributeId.STRENGTH -> it.copy(value = 1)
                        AttributeId.DEXTERITY -> it.copy(value = 2)
                        AttributeId.STAMINA -> it.copy(value = 2)
                        AttributeId.CHARISMA -> it.copy(value = 4)
                        AttributeId.MANIPULATION -> it.copy(value = 2)
                        AttributeId.APPEARANCE -> it.copy(value = 4)
                        AttributeId.PERCEPTION -> it.copy(value = 3)
                        AttributeId.INTELLIGENCE -> it.copy(value = 2)
                        AttributeId.WITS -> it.copy(value = 3)
                        else -> it
                    }
                },
                abilities = base.abilities.map {
                    when (it.id) {
                        AbilityId.EXPRESSION -> it.copy(value = 4)
                        AbilityId.EMPATHY -> it.copy(value = 3)
                        AbilityId.PERFORMANCE -> it.copy(value = 3)
                        AbilityId.CRAFTS -> it.copy(value = 2)
                        AbilityId.STREETWISE -> it.copy(value = 2)
                        else -> it
                    }
                },
                disciplines = listOf(
                    DisciplineValue(DisciplineId.PRESENCE, 2),
                    DisciplineValue(DisciplineId.CELERITY, 1)
                ),
                virtues = listOf(
                    VirtueValue(VirtueId.CONSCIENCE, 3),
                    VirtueValue(VirtueId.SELF_CONTROL, 2),
                    VirtueValue(VirtueId.COURAGE, 2)
                )
            )

            else -> base
        }
    }
}
