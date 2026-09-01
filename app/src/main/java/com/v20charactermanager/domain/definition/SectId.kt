package com.v20charactermanager.domain.definition

enum class SectId(
    val id: String,
    val nameIt: String,
    val nameEn: String
) {
    CAMARILLA("sect.camarilla", "Camarilla", "Camarilla"),
    SABBAT("sect.sabbat", "Sabbat", "Sabbat"),
    ANARCH("sect.anarch", "Anarchici", "Anarch"),
    INDEPENDENT("sect.independent", "Indipendenti", "Independent");

    companion object {
        fun fromId(id: String): SectId? =
            entries.find { it.id == id }

        fun defaultForClan(clan: ClanId): SectId {
            return when (clan) {
                ClanId.LASOMBRA, ClanId.TZIMISCE -> SABBAT
                ClanId.BRUAH, ClanId.MALKAVIAN, ClanId.NOSFERATU,
                ClanId.TOREADOR, ClanId.TREMERE, ClanId.VENTRUE,
                ClanId.GANGREL -> CAMARILLA
                ClanId.ASSAMITE, ClanId.GIOVANNI, ClanId.RAVNOS,
                ClanId.FOLLOWERS_OF_SET -> INDEPENDENT
                ClanId.CAITIFF -> CAMARILLA
            }
        }
    }
}
