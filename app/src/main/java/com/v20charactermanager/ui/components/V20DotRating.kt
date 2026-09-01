package com.v20charactermanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun V20DotRating(
    currentValue: Int,
    maxValue: Int = 5,
    editable: Boolean = false,
    enabled: Boolean = true,
    onValueChange: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    dotSize: Dp = 24.dp,
    spacing: Dp = 4.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (i in 1..maxValue) {
            val filled = i <= currentValue
            val index = i

            val backgroundColor = when {
                filled -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            val borderModifier = if (!filled) {
                Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            } else {
                Modifier
            }

            val clickableModifier = if (editable && enabled) {
                Modifier.clickable { onValueChange?.invoke(index) }
            } else {
                Modifier
            }

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .then(borderModifier)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .then(clickableModifier)
                    .semantics {
                        contentDescription = if (filled) {
                            "Dot $i of $maxValue, filled"
                        } else {
                            "Dot $i of $maxValue, empty"
                        }
                    }
            )
        }
    }
}
