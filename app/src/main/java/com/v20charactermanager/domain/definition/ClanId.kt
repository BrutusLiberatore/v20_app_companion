package com.v20charactermanager.domain.definition

enum class ClanId(
    val id: String,
    val nameIt: String,
    val nameEn: String,
    val sect: String,
    val clanDisciplines: List<DisciplineId>,
    val weaknessIt: String,
    val weaknessEn: String
) {
    ASSAMITE(
        id = "clan.assamite",
        nameIt = "Assamiti",
        nameEn = "Assamite",
        sect = "Indipendenti",
        clanDisciplines = listOf(DisciplineId.OBFUSCATE, DisciplineId.QUIETUS, DisciplineId.CELERITY),
        weaknessIt = "Se bevi sangue di vampiro sei tentato dalla diablerie",
        weaknessEn = "Drinking vampire blood tempts you toward diablerie"
    ),
    BRUAH(
        id = "clan.brujah",
        nameIt = "Brujah",
        nameEn = "Brujah",
        sect = "Camarilla",
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.POTENCE, DisciplineId.CELERITY),
        weaknessIt = "Impulsivi: difficoltà +2 per resistere alla frenesia",
        weaknessEn = "Impulsive: +2 difficulty to resist frenzy"
    ),
    GANGREL(
        id = "clan.gangrel",
        nameIt = "Gangrel",
        nameEn = "Gangrel",
        sect = "Camarilla",
        clanDisciplines = listOf(DisciplineId.ANIMALISM, DisciplineId.PROTEAN, DisciplineId.RESILIENCE),
        weaknessIt = "Ogni frenesia lascia un tratto animale finché non recuperi",
        weaknessEn = "Each frenzy leaves an animal trait until recovered"
    ),
    GIOVANNI(
        id = "clan.giovanni",
        nameIt = "Giovanni",
        nameEn = "Giovanni",
        sect = "Indipendenti",
        clanDisciplines = listOf(DisciplineId.DOMINATE, DisciplineId.NECROMANCY, DisciplineId.POTENCE),
        weaknessIt = "Il morso infligge danni aggravati e non dà piacere alla preda",
        weaknessEn = "The bite inflicts aggravated damage and gives no sustenance"
    ),
    LASOMBRA(
        id = "clan.lasombra",
        nameIt = "Lasombra",
        nameEn = "Lasombra",
        sect = "Sabbat",
        clanDisciplines = listOf(DisciplineId.DOMINATE, DisciplineId.OBTENEBRATION, DisciplineId.POTENCE),
        weaknessIt = "Non produci riflesso in specchi né su superfici",
        weaknessEn = "You cast no reflection in mirrors or on surfaces"
    ),
    MALKAVIAN(
        id = "clan.malkavian",
        nameIt = "Malkavian",
        nameEn = "Malkavian",
        sect = "Camarilla",
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.AUSPEX, DisciplineId.DEMENTATION),
        weaknessIt = "Sono tutti afflitti da una forma di follia incurabile",
        weaknessEn = "All are afflicted with an incurable form of madness"
    ),
    NOSFERATU(
        id = "clan.nosferatu",
        nameIt = "Nosferatu",
        nameEn = "Nosferatu",
        sect = "Camarilla",
        clanDisciplines = listOf(DisciplineId.ANIMALISM, DisciplineId.OBFUSCATE, DisciplineId.POTENCE),
        weaknessIt = "Deformi: Aspetto 0, nessun tiro sociale basato sulla bellezza",
        weaknessEn = "Deformed: Appearance 0, no social rolls based on looks"
    ),
    RAVNOS(
        id = "clan.ravnos",
        nameIt = "Ravnos",
        nameEn = "Ravnos",
        sect = "Indipendenti",
        clanDisciplines = listOf(DisciplineId.ANIMALISM, DisciplineId.CHIMESTRY, DisciplineId.FORTITUDE),
        weaknessIt = "Ognuno è schiavo di un vizio particolare del clan",
        weaknessEn = "Each is slave to a particular clan vice"
    ),
    FOLLOWERS_OF_SET(
        id = "clan.followersOfSet",
        nameIt = "Seguaci di Set",
        nameEn = "Followers of Set",
        sect = "Indipendenti",
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.OBFUSCATE, DisciplineId.SERPENTIS),
        weaknessIt = "Doppia vulnerabilità a luce solare e fuoco",
        weaknessEn = "Double vulnerability to sunlight and fire"
    ),
    TOREADOR(
        id = "clan.toreador",
        nameIt = "Toreador",
        nameEn = "Toreador",
        sect = "Camarilla",
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.AUSPEX, DisciplineId.CELERITY),
        weaknessIt = "Estasi: dinanzi alla bellezza rischi di restarne rapito",
        weaknessEn = "Ecstasy: in the presence of beauty you risk being enraptured"
    ),
    TREMERE(
        id = "clan.tremere",
        nameIt = "Tremere",
        nameEn = "Tremere",
        sect = "Camarilla",
        clanDisciplines = listOf(DisciplineId.AUSPEX, DisciplineId.DOMINATE, DisciplineId.THAUMATURGY),
        weaknessIt = "Legati da tre sorsi del sangue degli anziani del clan",
        weaknessEn = "Bound by three draughts of blood from clan elders"
    ),
    TZIMISCE(
        id = "clan.tzimisce",
        nameIt = "Tzimisce",
        nameEn = "Tzimisce",
        sect = "Sabbat",
        clanDisciplines = listOf(DisciplineId.ANIMALISM, DisciplineId.AUSPEX, DisciplineId.VICISSITUDE),
        weaknessIt = "Devi dormire circondato da terra della tua patria",
        weaknessEn = "You must sleep surrounded by soil from your homeland"
    ),
    VENTRUE(
        id = "clan.ventrue",
        nameIt = "Ventrue",
        nameEn = "Ventrue",
        sect = "Camarilla",
        clanDisciplines = listOf(DisciplineId.PRESENCE, DisciplineId.DOMINATE, DisciplineId.RESILIENCE),
        weaknessIt = "Palato esigente: puoi nutrirti solo da un tipo di preda",
        weaknessEn = "Discerning palate: you can only feed from one type of prey"
    ),
    CAITIFF(
        id = "clan.caitiff",
        nameIt = "Caitiff",
        nameEn = "Caitiff",
        sect = "",
        clanDisciplines = emptyList(),
        weaknessIt = "Senza clan: nessuno ti rispetta, costi PE più alti",
        weaknessEn = "No clan: no one respects you, higher XP costs"
    );

    companion object {
        fun fromId(id: String): ClanId? =
            entries.find { it.id == id }
    }
}
