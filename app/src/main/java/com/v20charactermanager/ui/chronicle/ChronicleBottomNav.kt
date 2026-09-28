package com.v20charactermanager.ui.chronicle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.v20charactermanager.R

enum class ChronicleBottomNavItem(
    val route: String,
    val icon: ImageVector,
    val labelRes: Int
) {
    LIVE("live", Icons.Filled.PlayArrow, R.string.bottom_nav_session),
    PEOPLE("people", Icons.Filled.People, R.string.bottom_nav_people),
    PLOTS("plots", Icons.Filled.AutoStories, R.string.bottom_nav_plots),
    MEDIA("media", Icons.Filled.Map, R.string.bottom_nav_media),
    AUDIO("audio", Icons.Filled.MusicNote, R.string.bottom_nav_audio),
    MORE("more", Icons.Filled.MoreHoriz, R.string.bottom_nav_more)
}
