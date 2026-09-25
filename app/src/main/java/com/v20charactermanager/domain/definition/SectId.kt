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

        fun defaultForClan(clan: ClanId): SectId = clan.defaultSect
    }
}
