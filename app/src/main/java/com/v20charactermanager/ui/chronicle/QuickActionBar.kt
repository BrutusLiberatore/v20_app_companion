package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R

@Composable
fun QuickActionBar(
    onDiceClick: () -> Unit,
    onNoteClick: () -> Unit,
    onEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = onDiceClick,
            label = {
                Text(
                    text = stringResource(R.string.dice_title),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = onNoteClick,
            label = {
                Text(
                    text = stringResource(R.string.storyteller_quick_note),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = onEventClick,
            label = {
                Text(
                    text = stringResource(R.string.storyteller_event),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.weight(1f)
        )
    }
}
