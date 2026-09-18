package com.v20charactermanager.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

enum class AdaptiveLayoutType {
    COMPACT,
    MEDIUM,
    EXPANDED
}

@Composable
fun rememberAdaptiveLayout(): AdaptiveLayoutType {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    return remember(widthDp) {
        when {
            widthDp < 600 -> AdaptiveLayoutType.COMPACT
            widthDp < 840 -> AdaptiveLayoutType.MEDIUM
            else -> AdaptiveLayoutType.EXPANDED
        }
    }
}
