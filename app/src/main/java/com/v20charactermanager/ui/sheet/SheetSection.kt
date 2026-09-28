package com.v20charactermanager.ui.sheet

import com.v20charactermanager.R

enum class SheetSection(
    val key: String,
    val labelRes: Int,
    val iconRes: Int?
) {
    OVERVIEW("overview", R.string.sheet_tab_overview, null),
    ATTRIBUTES("attributes", R.string.sheet_tab_attributes, R.drawable.ic_attributes),
    ABILITIES("abilities", R.string.sheet_tab_abilities, R.drawable.ic_abilities),
    ADVANTAGES("advantages", R.string.sheet_tab_advantages, R.drawable.ic_merits),
    DETAILS("details", R.string.sheet_tab_details, R.drawable.ic_blood_pool),
    MERITS_FLAWS("merits_flaws", R.string.sheet_merits_flaws, R.drawable.ic_humanity),
    EQUIPMENT("equipment", R.string.sheet_equipment, R.drawable.ic_equipment),
    NOTES("notes", R.string.sheet_notes, R.drawable.ic_notes);

    companion object {
        val defaultOrder: List<SheetSection> = entries.toList()

        fun parseOrder(raw: String): List<SheetSection> {
            val parsed = raw.split(',')
                .mapNotNull { token -> entries.firstOrNull { it.key == token.trim() } }
            val missing = entries.filter { it !in parsed }
            return parsed + missing
        }

        fun serializeOrder(order: List<SheetSection>): String =
            order.distinct().joinToString(",") { it.key }
    }
}
