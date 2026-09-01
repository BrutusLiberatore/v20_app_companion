package com.v20charactermanager.domain.definition

enum class FlawId(
    val id: String,
    val nameIt: String,
    val nameEn: String,
    val cost: Int,
    val category: FlawCategory,
    val descriptionIt: String,
    val descriptionEn: String
) {
    ADDICTION(
        id = "flaw.addiction",
        nameIt = "Dipendenza",
        nameEn = "Addiction",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Sei dipendente da una sostanza (alcol, droga, sangue)",
        descriptionEn = "You are addicted to a substance (alcohol, drugs, blood)"
    ),
    AMNESIA(
        id = "flaw.amnesia",
        nameIt = "Amnesia",
        nameEn = "Amnesia",
        cost = 2,
        category = FlawCategory.MENTAL,
        descriptionIt = "Non ricordi nulla della tua vita precedente",
        descriptionEn = "You remember nothing of your previous life"
    ),
    BLACK_LOTUS_ADDICTION(
        id = "flaw.blackLotusAddiction",
        nameIt = "Dipendenza dal Loto Nero",
        nameEn = "Black Lotus Addiction",
        cost = 2,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Sei dipendente dal Loto Nero, una droga vampirica pericolosa",
        descriptionEn = "You are addicted to Black Lotus, a dangerous vampire drug"
    ),
    BLOOD_DEPENDENCY(
        id = "flaw.bloodDependency",
        nameIt = "Dipendenza dal Sangue",
        nameEn = "Blood Dependency",
        cost = 2,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Hai bisogno di sangue più spesso del normale",
        descriptionEn = "You need blood more often than normal"
    ),
    BRAMBLE(
        id = "flaw.bramble",
        nameIt = "Roveto",
        nameEn = "Bramble",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Hai spine o escrescenze che ti avvolgono",
        descriptionEn = "You have thorns or growths that wrap around you"
    ),
    CLAN_WEAKNESS(
        id = "flaw.clanWeakness",
        nameIt = "Debolezza di Clan",
        nameEn = "Clan Weakness",
        cost = 2,
        category = FlawCategory.BACKGROUND,
        descriptionIt = "La tua debolezza di clan è particolarmente severa",
        descriptionEn = "Your clan weakness is particularly severe"
    ),
    COLD_BREEZE(
        id = "flaw.coldBreeze",
        nameIt = "Brezza Fredda",
        nameEn = "Cold Breeze",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "La temperatura corporea si abbassa visibilmente",
        descriptionEn = "Your body temperature visibly drops"
    ),
    COMPULSIVE_BEHAVIOR(
        id = "flaw.compulsiveBehavior",
        nameIt = "Comportamento Compulsivo",
        nameEn = "Compulsive Behavior",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Sei vittima di un comportamento compulsivo",
        descriptionEn = "You are the victim of a compulsive behavior"
    ),
    COWARDICE(
        id = "flaw.cowardice",
        nameIt = "Vigliaccheria",
        nameEn = "Cowardice",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Hai una paura debilitante del pericolo",
        descriptionEn = "You have a debilitating fear of danger"
    ),
    DARK_SECRET(
        id = "flaw.darkSecret",
        nameIt = "Segreto Oscuro",
        nameEn = "Dark Secret",
        cost = 1,
        category = FlawCategory.SOCIAL,
        descriptionIt = "Hai un segreto che potrebbe distruggerti se rivelato",
        descriptionEn = "You have a secret that could destroy you if revealed"
    ),
    DEAF(
        id = "flaw.deaf",
        nameIt = "Sordo",
        nameEn = "Deaf",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Non puoi sentire",
        descriptionEn = "You cannot hear"
    ),
    DEFORMITY(
        id = "flaw.deformity",
        nameIt = "Deformità",
        nameEn = "Deformity",
        cost = 2,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Hai una deformità visibile",
        descriptionEn = "You have a visible deformity"
    ),
    DERANGED(
        id = "flaw.deranged",
        nameIt = "Squilibriato",
        nameEn = "Deranged",
        cost = 2,
        category = FlawCategory.MENTAL,
        descriptionIt = "La tua mente è profondamente disturbata",
        descriptionEn = "Your mind is deeply disturbed"
    ),
    DISFIGURED(
        id = "flaw.disfigured",
        nameIt = "Sfigurato",
        nameEn = "Disfigured",
        cost = 2,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Il tuo volto è orribilmente sfigurato",
        descriptionEn = "Your face is horribly disfigured"
    ),
    DRIVEN(
        id = "flaw.driven",
        nameIt = "Obsesso",
        nameEn = "Driven",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Sei ossessionato da un obiettivo o un'idea",
        descriptionEn = "You are obsessed with a goal or idea"
    ),
    EXPLOSIVE_TEMPER(
        id = "flaw.explosiveTemper",
        nameIt = "Temperamento Esplosivo",
        nameEn = "Explosive Temper",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Perdi facilmente il controllo della rabbia",
        descriptionEn = "You easily lose control of your anger"
    ),
    FALSE_MEMORIES(
        id = "flaw.falseMemories",
        nameIt = "Falsi Ricordi",
        nameEn = "False Memories",
        cost = 2,
        category = FlawCategory.MENTAL,
        descriptionIt = "Ricordi eventi che non sono mai accaduti",
        descriptionEn = "You remember events that never happened"
    ),
    FORBIDDEN_KNOWLEDGE(
        id = "flaw.forbiddenKnowledge",
        nameIt = "Conoscenza Proibita",
        nameEn = "Forbidden Knowledge",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Conosci segreti che dovresti ignorare",
        descriptionEn = "You know secrets you should not"
    ),
    FOUL_BLOOD(
        id = "flaw.foulBlood",
        nameIt = "Sangue Malvagio",
        nameEn = "Foul Blood",
        cost = 2,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Il tuo sangue è velenoso o disgustoso",
        descriptionEn = "Your blood is poisonous or foul"
    ),
    FRAGILE(
        id = "flaw.fragile",
        nameIt = "Fragile",
        nameEn = "Fragile",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Il tuo corpo è insolitamente fragile",
        descriptionEn = "Your body is unusually fragile"
    ),
    GUILTY_CONSCIENCE(
        id = "flaw.guiltyConscience",
        nameIt = "Coscienza Colpevole",
        nameEn = "Guilty Conscience",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Sei tormentato dalla colpa per le tue azioni",
        descriptionEn = "You are tormented by guilt over your actions"
    ),
    HEARTLESS(
        id = "flaw.heartless",
        nameIt = "Senza Cuore",
        nameEn = "Heartless",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Non provi empatia o compassione",
        descriptionEn = "You feel no empathy or compassion"
    ),
    HUNTED(
        id = "flaw.hunted",
        nameIt = "Braccato",
        nameEn = "Hunted",
        cost = 2,
        category = FlawCategory.BACKGROUND,
        descriptionIt = "Sei inseguito da qualcosa di pericoloso",
        descriptionEn = "You are pursued by something dangerous"
    ),
    INSOMNIA(
        id = "flaw.insomnia",
        nameIt = "Insonnia",
        nameEn = "Insomnia",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Non riesci a dormire regolarmente",
        descriptionEn = "You cannot sleep regularly"
    ),
    LIAR(
        id = "flaw.liar",
        nameIt = "Bugiardo",
        nameEn = "Liar",
        cost = 1,
        category = FlawCategory.SOCIAL,
        descriptionIt = "Menti compulsivamente anche quando non necessario",
        descriptionEn = "You lie compulsively even when unnecessary"
    ),
    LOST_PRIDE(
        id = "flaw.lostPride",
        nameIt = "Orgoglio Perduto",
        nameEn = "Lost Pride",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Hai perso la tua dignità o il tuo orgoglio",
        descriptionEn = "You have lost your dignity or pride"
    ),
    MISSING_LIMB(
        id = "flaw.missingLimb",
        nameIt = "Membro Mancante",
        nameEn = "Missing Limb",
        cost = 2,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Ti manca un arto",
        descriptionEn = "You are missing a limb"
    ),
    NIGHT_TERRORS(
        id = "flaw.nightTerrors",
        nameIt = "Terrore Notturno",
        nameEn = "Night Terrors",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Hai incubi che ti svegliano di soprassalto",
        descriptionEn = "You have nightmares that wake you in a cold sweat"
    ),
    OVERCONFIDENCE(
        id = "flaw.overconfidence",
        nameIt = "Eccessiva Fiducia",
        nameEn = "Overconfidence",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Sopravvaluti le tue capacità",
        descriptionEn = "You overestimate your abilities"
    ),
    PHOBIA(
        id = "flaw.phobia",
        nameIt = "Fobia",
        nameEn = "Phobia",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Hai una paura irrazionale e debilitante",
        descriptionEn = "You have an irrational and debilitating fear"
    ),
    POOR(
        id = "flaw.poor",
        nameIt = "Povero",
        nameEn = "Poor",
        cost = 1,
        category = FlawCategory.BACKGROUND,
        descriptionIt = "Non hai risorse finanziarie",
        descriptionEn = "You have no financial resources"
    ),
    PSEUDOFOTOFOBIA(
        id = "flaw.pseudofotofobia",
        nameIt = "Pseudofotofobia",
        nameEn = "Pseudofotofobia",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "La luce solare ti causa un dolore intenso",
        descriptionEn = "Sunlight causes you intense pain"
    ),
    SHUNNED_BY_ANIMALS(
        id = "flaw.shunnedByAnimals",
        nameIt = "Evitato dagli Animali",
        nameEn = "Shunned by Animals",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Gli animali fuggono dalla tua presenza",
        descriptionEn = "Animals flee from your presence"
    ),
    SHORT_FUSE(
        id = "flaw.shortFuse",
        nameIt = "Meno Paziente",
        nameEn = "Short Fuse",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Perdi facilmente la pazienza",
        descriptionEn = "You lose patience easily"
    ),
    SQUEAMISH(
        id = "flaw.squeamish",
        nameIt = "Schizzinoso",
        nameEn = "Squeamish",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Il sangue e la violenza ti nauseano",
        descriptionEn = "Blood and violence make you nauseous"
    ),
    TROUBLED_FAMILY(
        id = "flaw.troubledFamily",
        nameIt = "Famiglia In Quiete",
        nameEn = "Troubled Family",
        cost = 1,
        category = FlawCategory.BACKGROUND,
        descriptionIt = "La tua famiglia è una fonte costante di problemi",
        descriptionEn = "Your family is a constant source of trouble"
    ),
    TRIVIAL_RESPONSIBILITY(
        id = "flaw.trivialResponsibility",
        nameIt = "Responsabilità Triviale",
        nameEn = "Trivial Responsibility",
        cost = 1,
        category = FlawCategory.BACKGROUND,
        descriptionIt = "Hai una responsabilità che ti trascina verso il basso",
        descriptionEn = "You have a responsibility that drags you down"
    ),
    UNDERWORLD_HOOK(
        id = "flaw.underworldHook",
        nameIt = "Gancio del Sottobosco",
        nameEn = "Underworld Hook",
        cost = 1,
        category = FlawCategory.BACKGROUND,
        descriptionIt = "Sei coinvolto con elementi criminali",
        descriptionEn = "You are involved with criminal elements"
    ),
    VENDETTA(
        id = "flaw.vengeance",
        nameIt = "Vendetta",
        nameEn = "Vengeance",
        cost = 1,
        category = FlawCategory.MENTAL,
        descriptionIt = "Sei ossessionato dalla vendetta contro qualcuno",
        descriptionEn = "You are obsessed with vengeance against someone"
    ),
    VOW(
        id = "flaw.vow",
        nameIt = "Voto",
        nameEn = "Vow",
        cost = 1,
        category = FlawCategory.BACKGROUND,
        descriptionIt = "Hai fatto un voto o una promessa che ti limita",
        descriptionEn = "You have made a vow or promise that limits you"
    ),
    WARM_DAYLIGHT(
        id = "flaw.warmDaylight",
        nameIt = "Luce del Giorno Calda",
        nameEn = "Warm Daylight",
        cost = 1,
        category = FlawCategory.PHYSICAL,
        descriptionIt = "Anche la luce del giorno più debole ti causa sofferenza",
        descriptionEn = "Even the weakest daylight causes you suffering"
    );

    companion object {
        fun fromId(id: String): FlawId? =
            entries.find { it.id == id }

        fun getByCategory(category: FlawCategory): List<FlawId> =
            entries.filter { it.category == category }
    }
}

enum class FlawCategory(val nameIt: String, val nameEn: String) {
    PHYSICAL("Fisico", "Physical"),
    MENTAL("Mentale", "Mental"),
    SOCIAL("Sociale", "Social"),
    BACKGROUND("Background", "Background")
}
