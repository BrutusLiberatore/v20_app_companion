package com.v20charactermanager.domain.definition

enum class VirtueId(
    val id: String,
    val nameIt: String,
    val nameEn: String
) {
    CONSCIENCE("virtue.conscience", "Coscienza", "Conscience"),
    SELF_CONTROL("virtue.selfControl", "Autocontrollo", "Self-Control"),
    COURAGE("virtue.courage", "Coraggio", "Courage"),
    CONVICTION("virtue.conviction", "Convinzione", "Conviction"),
    INSTINCT("virtue.instinct", "Istinto", "Instinct");

    companion object {
        fun fromId(id: String): VirtueId? =
            entries.find { it.id == id }

        fun defaultVirtues(): List<VirtueId> =
            listOf(CONSCIENCE, SELF_CONTROL, COURAGE)

        fun sabbatVirtues(): List<VirtueId> =
            listOf(CONVICTION, INSTINCT, COURAGE)
    }
}
