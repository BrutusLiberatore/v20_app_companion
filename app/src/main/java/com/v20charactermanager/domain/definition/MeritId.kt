package com.v20charactermanager.domain.definition

enum class MeritId(
    val id: String,
    val nameIt: String,
    val nameEn: String,
    val cost: Int,
    val category: MeritCategory,
    val descriptionIt: String,
    val descriptionEn: String
) {
    AMBIDEXTROUS(
        id = "merit.ambidextrous",
        nameIt = "Ambidestro",
        nameEn = "Ambidextrous",
        cost = 1,
        category = MeritCategory.PHYSICAL,
        descriptionIt = "Puoi usare entrambe le mani con la stessa abilità",
        descriptionEn = "You can use both hands with equal skill"
    ),
    BLUSH_OF_LIFE(
        id = "merit.blushOfLife",
        nameIt = "Vergogna della Vita",
        nameEn = "Blush of Life",
        cost = 1,
        category = MeritCategory.PHYSICAL,
        descriptionIt = "Il tuo volto si arrossisce quando bevi, sembrando più vivo",
        descriptionEn = "Your face flushes when you drink, appearing more alive"
    ),
    DANGER_SENSE(
        id = "merit.dangerSense",
        nameIt = "Senso del Pericolo",
        nameEn = "Danger Sense",
        cost = 1,
        category = MeritCategory.MENTAL,
        descriptionIt = "Avverti istintivamente il pericolo imminente",
        descriptionEn = "You instinctively sense imminent danger"
    ),
    EIDETIC_MEMORY(
        id = "merit.eideticMemory",
        nameIt = "Memoria Fotografica",
        nameEn = "Eidetic Memory",
        cost = 1,
        category = MeritCategory.MENTAL,
        descriptionIt = "Ricordi ogni dettaglio di ciò che hai percepito",
        descriptionEn = "You remember every detail of what you have perceived"
    ),
    EMPATHY(
        id = "merit.empathy",
        nameIt = "Empatia",
        nameEn = "Empathy",
        cost = 1,
        category = MeritCategory.SOCIAL,
        descriptionIt = "Hai un'innata capacità di comprendere le emozioni altrui",
        descriptionEn = "You have an innate ability to understand others' emotions"
    ),
    HARMLESS(
        id = "merit.harmless",
        nameIt = "Inoffensivo",
        nameEn = "Harmless",
        cost = 1,
        category = MeritCategory.SOCIAL,
        descriptionIt = "Non sembri una minaccia, gli altri si fidano di te",
        descriptionEn = "You don't appear threatening; others trust you"
    ),
    IRON_WILL(
        id = "merit.ironWill",
        nameIt = "Volontà di Ferro",
        nameEn = "Iron Will",
        cost = 2,
        category = MeritCategory.MENTAL,
        descriptionIt = "Hai una resistenza mentale eccezionale",
        descriptionEn = "You have exceptional mental resistance"
    ),
    LIGHT_SLEEPER(
        id = "merit.lightSleeper",
        nameIt = "Sonno Leggero",
        nameEn = "Light Sleeper",
        cost = 1,
        category = MeritCategory.PHYSICAL,
        descriptionIt = "Ti svegli al minimo rumore o contatto",
        descriptionEn = "You awaken at the slightest noise or touch"
    ),
    MEDIUM(
        id = "merit.medium",
        nameIt = "Medium",
        nameEn = "Medium",
        cost = 1,
        category = MeritCategory.SOCIAL,
        descriptionIt = "Puoi percepire e comunicare con i morti",
        descriptionEn = "You can perceive and communicate with the dead"
    ),
    PATIENT(
        id = "merit.patient",
        nameIt = "Paziente",
        nameEn = "Patient",
        cost = 1,
        category = MeritCategory.MENTAL,
        descriptionIt = "Hai una pazienza straordinaria",
        descriptionEn = "You have extraordinary patience"
    ),
    QUICK_HEALER(
        id = "merit.quickHealer",
        nameIt = "Guarigione Rapida",
        nameEn = "Quick Healer",
        cost = 2,
        category = MeritCategory.PHYSICAL,
        descriptionIt = "Guarisci dalle ferite molto più rapidamente del normale",
        descriptionEn = "You heal from wounds much faster than normal"
    ),
    SEA_LEGS(
        id = "merit.seaLegs",
        nameIt = "Gambe di Mare",
        nameEn = "Sea Legs",
        cost = 1,
        category = MeritCategory.PHYSICAL,
        descriptionIt = "Non soffri di nausea o vertigini in mare",
        descriptionEn = "You never suffer from seasickness or dizziness at sea"
    ),
    TOUGHNESS(
        id = "merit.toughness",
        nameIt = "Resistenza",
        nameEn = "Toughness",
        cost = 2,
        category = MeritCategory.PHYSICAL,
        descriptionIt = "I tuoi tessuti sono più resistenti del normale",
        descriptionEn = "Your tissues are tougher than normal"
    ),
    COMPUTER_HACKING(
        id = "merit.computerHacking",
        nameIt = "Hacking Informatico",
        nameEn = "Computer Hacking",
        cost = 1,
        category = MeritCategory.MENTAL,
        descriptionIt = "Sei un esperto di accesso non autorizzato ai sistemi informatici",
        descriptionEn = "You are an expert at unauthorized computer system access"
    ),
    EOUTSIDER(
        id = "merit.eOutsider",
        nameIt = "Estraneo dell'E",
        nameEn = "E- Outsider",
        cost = 1,
        category = MeritCategory.BACKGROUND,
        descriptionIt = "Hai accesso a tecnologia o informazioni rare",
        descriptionEn = "You have access to rare technology or information"
    ),
    GENIUS(
        id = "merit.genius",
        nameIt = "Genio",
        nameEn = "Genius",
        cost = 3,
        category = MeritCategory.MENTAL,
        descriptionIt = "La tua mente è straordinariamente acuta",
        descriptionEn = "Your mind is extraordinarily keen"
    ),
    FLEET_OF_FOOT(
        id = "merit.fleetOfFoot",
        nameIt = "Piede Veloce",
        nameEn = "Fleet of Foot",
        cost = 1,
        category = MeritCategory.PHYSICAL,
        descriptionIt = "Puoi muoverti più velocemente del normale",
        descriptionEn = "You can move faster than normal"
    ),
    PREMONITION(
        id = "merit.premonition",
        nameIt = "Premonizione",
        nameEn = "Premonition",
        cost = 1,
        category = MeritCategory.MENTAL,
        descriptionIt = "Hai visioni del futuro",
        descriptionEn = "You have visions of the future"
    ),
    DIVINE_STYLING(
        id = "merit.divineStyling",
        nameIt = "Stile Divino",
        nameEn = "Divine Styling",
        cost = 1,
        category = MeritCategory.SOCIAL,
        descriptionIt = "Hai un gusto innato per la moda e lo stile",
        descriptionEn = "You have an innate sense of fashion and style"
    ),
    NATURAL_LEADER(
        id = "merit.naturalLeader",
        nameIt = "Leader Nato",
        nameEn = "Natural Leader",
        cost = 2,
        category = MeritCategory.SOCIAL,
        descriptionIt = "Hai un'influenza naturale sulle persone",
        descriptionEn = "You have a natural influence over people"
    ),
    CODE_OF_HONOR(
        id = "merit.codeOfHonor",
        nameIt = "Codice d'Onore",
        nameEn = "Code of Honor",
        cost = 1,
        category = MeritCategory.BACKGROUND,
        descriptionIt = "Segui un rigido codice personale di condotta",
        descriptionEn = "You follow a strict personal code of conduct"
    ),
    SMILE_OF_THE_PANTHER(
        id = "merit.smileOfThePanther",
        nameIt = "Sorriso della Pantera",
        nameEn = "Smile of the Panther",
        cost = 2,
        category = MeritCategory.SOCIAL,
        descriptionIt = "Sei irresistibilmente attraente",
        descriptionEn = "You are irresistibly attractive"
    );

    companion object {
        fun fromId(id: String): MeritId? =
            entries.find { it.id == id }

        fun getByCategory(category: MeritCategory): List<MeritId> =
            entries.filter { it.category == category }
    }
}

enum class MeritCategory(val nameIt: String, val nameEn: String) {
    PHYSICAL("Fisico", "Physical"),
    MENTAL("Mentale", "Mental"),
    SOCIAL("Sociale", "Social"),
    BACKGROUND("Background", "Background")
}
