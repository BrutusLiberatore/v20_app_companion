package com.v20charactermanager.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.ui.chronicle.ChronicleBottomNavItem

@Composable
fun AdaptiveChronicleNavigation(
    selectedItem: ChronicleBottomNavItem,
    onItemSelected: (ChronicleBottomNavItem) -> Unit,
    layoutType: AdaptiveLayoutType,
    modifier: Modifier = Modifier
) {
    when (layoutType) {
        AdaptiveLayoutType.COMPACT -> {
            NavigationBar(modifier = modifier) {
                ChronicleBottomNavItem.entries.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = null)
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
        AdaptiveLayoutType.MEDIUM,
        AdaptiveLayoutType.EXPANDED -> {
            NavigationRail(modifier = modifier) {
                ChronicleBottomNavItem.entries.forEach { item ->
                    NavigationRailItem(
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = null)
                        },
                        label = {
                            Text(
                                text = stringResource(item.labelRes),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = selectedItem == item,
                        onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}
