package com.v20charactermanager.domain.definition

enum class DemeanorId(
    val id: String,
    val nameIt: String,
    val nameEn: String
) {
    ALTRUIST("demeanor.altruist", "Altruista", "Altruist"),
    ARCHITECT("demeanor.architect", "Architetto", "Architect"),
    AUTOCRAT("demeanor.autocrat", "Autocrate", "Autocrat"),
    CHILD("demeanor.child", "Bambino", "Child"),
    BON_VIVANT("demeanor.bonVivant", "Buffone", "Bon Vivant"),
    BRAVO("demeanor.bravo", "Bullo", "Bravo"),
    CURMUDGEON("demeanor.curmudgeon", "Burbero", "Curmudgeon"),
    COMPETITOR("demeanor.competitor", "Competitore", "Competitor"),
    CONFORMIST("demeanor.conformist", "Conformista", "Conformist"),
    DEVIANT("demeanor.deviant", "Deviato", "Deviant"),
    ENTHUSIAST("demeanor.enthusiast", "Entusiasta", "Enthusiast"),
    FANATIC("demeanor.fanatic", "Fanatico", "Fanatic"),
    ROGUE("demeanor.rogue", "Furfante", "Rogue"),
    GALLANT("demeanor.gallant", "Galante", "Gallant"),
    JUDGE("demeanor.judge", "Giudice", "Judge"),
    MARTYR("demeanor.martyr", "Martire", "Martyr"),
    MASOCHIST("demeanor.masochist", "Masochista", "Masochist"),
    MONSTER("demeanor.monster", "Mostro", "Monster"),
    PEDAGOGUE("demeanor.pedagogue", "Pedagogo", "Pedagogue"),
    PENITENT("demeanor.penitent", "Penitente", "Penitent"),
    PERFECTIONIST("demeanor.perfectionist", "Perfezionista", "Perfectionist"),
    PLOTTER("demeanor.plotter", "Pianificatore", "Plotter"),
    REBEL("demeanor.rebel", "Ribelle", "Rebel"),
    CONNIVER("demeanor.conniver", "Sfruttatore", "Conniver"),
    LONER("demeanor.loner", "Solitario", "Loner"),
    SURVIVOR("demeanor.survivor", "Sopravvissuto", "Survivor"),
    DAREDEVIL("demeanor.daredevil", "Temerario", "Daredevil"),
    TRADITIONALIST("demeanor.traditionalist", "Tradizionalista", "Traditionalist"),
    VISIONARY("demeanor.visionary", "Visionario", "Visionary");

    companion object {
        fun fromId(id: String): DemeanorId? =
            entries.find { it.id == id }
    }
}
