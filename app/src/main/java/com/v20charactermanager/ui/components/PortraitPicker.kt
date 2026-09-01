package com.v20charactermanager.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.v20charactermanager.R
import java.io.File

@Composable
fun PortraitPicker(
    portraitPath: String?,
    onGalleryPick: () -> Unit,
    onCameraPick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 120
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        if (portraitPath != null) {
            val file = File(portraitPath)
            if (file.exists()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(file)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.portrait_pick),
                    modifier = Modifier
                        .size(size.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { showMenu = true },
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = stringResource(R.string.portrait_pick),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                PortraitPlaceholder(
                    size = size,
                    onClick = { showMenu = true }
                )
            }
        } else {
            PortraitPlaceholder(
                size = size,
                onClick = { showMenu = true }
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.portrait_gallery)) },
                onClick = {
                    showMenu = false
                    onGalleryPick()
                },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.portrait_camera)) },
                onClick = {
                    showMenu = false
                    onCameraPick()
                },
                leadingIcon = {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                }
            )
            if (portraitPath != null) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.portrait_remove)) },
                    onClick = {
                        showMenu = false
                        onRemove()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun PortraitPlaceholder(
    size: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = stringResource(R.string.portrait_pick),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size((size / 3).dp)
        )
    }
}
