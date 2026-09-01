package com.v20charactermanager.domain.definition

enum class BackgroundId(
    val id: String,
    val nameIt: String,
    val nameEn: String
) {
    ALLIES("background.allies", "Alleati", "Allies"),
    CONTACTS("background.contacts", "Contatti", "Contacts"),
    FAME("background.fame", "Fama", "Fame"),
    GENERATION("background.generation", "Generazione", "Generation"),
    HERD("background.herd", "Gregge", "Herd"),
    INFLUENCE("background.influence", "Influenza", "Influence"),
    MENTOR("background.mentor", "Mentore", "Mentor"),
    RESOURCES("background.resources", "Risorse", "Resources"),
    RETAINERS("background.retainers", "Seguaci", "Retainers"),
    STATUS("background.status", "Status", "Status");

    companion object {
        fun fromId(id: String): BackgroundId? =
            entries.find { it.id == id }
    }
}
