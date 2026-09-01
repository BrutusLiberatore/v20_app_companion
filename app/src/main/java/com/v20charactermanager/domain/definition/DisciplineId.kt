package com.v20charactermanager.domain.definition

enum class DisciplineId(
    val id: String,
    val nameIt: String,
    val nameEn: String
) {
    ANIMALISM("discipline.animalism", "Animalità", "Animalism"),
    AUSPEX("discipline.auspex", "Auspex", "Auspex"),
    CELERITY("discipline.celerity", "Velocità", "Celerity"),
    CHIMESTRY("discipline.chimerstry", "Chimerismo", "Chimerstry"),
    DEMENTATION("discipline.dementation", "Demenza", "Dementation"),
    DOMINATE("discipline.dominate", "Dominazione", "Dominate"),
    FORTITUDE("discipline.fortitude", "Fortitudine", "Fortitude"),
    NECROMANCY("discipline.necromancy", "Necromanzia", "Necromancy"),
    OBFUSCATE("discipline.obfuscate", "Oscurazione", "Obfuscate"),
    OBTENEBRATION("discipline.obtenebration", "Ottenebramento", "Obtenebration"),
    POTENCE("discipline.potence", "Potenza", "Potence"),
    PRESENCE("discipline.presence", "Ascendente", "Presence"),
    PROTEAN("discipline.protean", "Proteide", "Protean"),
    QUIETUS("discipline.quietus", "Quietus", "Quietus"),
    RESILIENCE("discipline.resilience", "Robustezza", "Resilience"),
    SERPENTIS("discipline.serpentis", "Serpentis", "Serpentis"),
    THAUMATURGY("discipline.thaumaturgy", "Taumaturgia", "Thaumaturgy"),
    VICISSITUDE("discipline.vicissitude", "Vicissitudine", "Vicissitude");

    companion object {
        fun fromId(id: String): DisciplineId? =
            entries.find { it.id == id }
    }
}
