package com.v20charactermanager.ui.liveroom

import com.v20charactermanager.R

/**
 * Table/chair asset packs. Table and chair are chosen independently
 * (state keeps a separate id for each), so styles can be mixed.
 */
enum class TableStylePack(
    val id: String,
    val tableRes: Int,
    val chairRes: Int,
    val labelRes: Int
) {
    MEDIEVALE("medievale", R.drawable.assets_tavolo, R.drawable.assets_sedia_medievale, R.string.style_pack_medievale),
    GIARDINO("giardino", R.drawable.assets_tavolo_giardino, R.drawable.assets_sedia_giardino, R.string.style_pack_giardino),
    MARE("mare", R.drawable.assets_tavolo_mare, R.drawable.assets_sedia_mare, R.string.style_pack_mare),
    NONNA("nonna", R.drawable.assets_tavolo_nonna, R.drawable.assets_sedia_nonna, R.string.style_pack_nonna),
    POKER("poker", R.drawable.assets_tavolo_poker, R.drawable.assets_sedia_poker, R.string.style_pack_poker);

    companion object {
        const val DEFAULT_ID = "medievale"
        fun fromId(id: String?): TableStylePack =
            entries.firstOrNull { it.id == id } ?: MEDIEVALE
    }
}
