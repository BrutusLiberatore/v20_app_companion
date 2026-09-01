package com.v20charactermanager.ui.chronicle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
    MORE("more", Icons.Filled.MoreHoriz, R.string.bottom_nav_more)
}

@Composable
fun ChronicleBottomNavigation(
    selectedItem: ChronicleBottomNavItem,
    onItemSelected: (ChronicleBottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        ChronicleBottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(item.labelRes))
                },
                selected = selectedItem == item,
                onClick = { onItemSelected(item) }
            )
        }
    }
}
