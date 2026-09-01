package com.v20charactermanager.domain.definition

enum class NatureId(
    val id: String,
    val nameIt: String,
    val nameEn: String
) {
    ALTRUIST("nature.altruist", "Altruista", "Altruist"),
    ARCHITECT("nature.architect", "Architetto", "Architect"),
    AUTOCRAT("nature.autocrat", "Autocrate", "Autocrat"),
    CHILD("nature.child", "Bambino", "Child"),
    BON_VIVANT("nature.bonVivant", "Buffone", "Bon Vivant"),
    BRAVO("nature.bravo", "Bullo", "Bravo"),
    CURMUDGEON("nature.curmudgeon", "Burbero", "Curmudgeon"),
    COMPETITOR("nature.competitor", "Competitore", "Competitor"),
    CONFORMIST("nature.conformist", "Conformista", "Conformist"),
    DEVIANT("nature.deviant", "Deviato", "Deviant"),
    ENTHUSIAST("nature.enthusiast", "Entusiasta", "Enthusiast"),
    FANATIC("nature.fanatic", "Fanatico", "Fanatic"),
    ROGUE("nature.rogue", "Furfante", "Rogue"),
    GALLANT("nature.gallant", "Galante", "Gallant"),
    JUDGE("nature.judge", "Giudice", "Judge"),
    MARTYR("nature.martyr", "Martire", "Martyr"),
    MASOCHIST("nature.masochist", "Masochista", "Masochist"),
    MONSTER("nature.monster", "Mostro", "Monster"),
    PEDAGOGUE("nature.pedagogue", "Pedagogo", "Pedagogue"),
    PENITENT("nature.penitent", "Penitente", "Penitent"),
    PERFECTIONIST("nature.perfectionist", "Perfezionista", "Perfectionist"),
    PLOTTER("nature.plotter", "Pianificatore", "Plotter"),
    REBEL("nature.rebel", "Ribelle", "Rebel"),
    CONNIVER("nature.conniver", "Sfruttatore", "Conniver"),
    LONER("nature.loner", "Solitario", "Loner"),
    SURVIVOR("nature.survivor", "Sopravvissuto", "Survivor"),
    DAREDEVIL("nature.daredevil", "Temerario", "Daredevil"),
    TRADITIONALIST("nature.traditionalist", "Tradizionalista", "Traditionalist"),
    VISIONARY("nature.visionary", "Visionario", "Visionary");

    companion object {
        fun fromId(id: String): NatureId? =
            entries.find { it.id == id }
    }
}
