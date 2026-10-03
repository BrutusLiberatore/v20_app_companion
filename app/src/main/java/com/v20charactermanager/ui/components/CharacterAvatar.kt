package com.v20charactermanager.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.v20charactermanager.R
import com.v20charactermanager.domain.definition.ClanId
import com.v20charactermanager.domain.model.Character

@Composable
fun CharacterAvatar(
    character: Character,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val portraitUri = character.portraitUri
    val clanSymbol = character.identity.clan.avatarSymbolRes()
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (portraitUri != null) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            portraitUri != null -> AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(portraitUri)
                    .crossfade(true)
                    .build(),
                contentDescription = character.identity.name,
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop
            )
            clanSymbol != null -> Image(
                painter = painterResource(id = clanSymbol),
                contentDescription = null,
                modifier = Modifier.size(size * 0.62f),
                contentScale = ContentScale.Fit
            )
            else -> Text(
                text = character.identity.name.take(1).uppercase().ifEmpty { "?" },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun ClanId.avatarSymbolRes(): Int? = when (this) {
    ClanId.ASSAMITE -> R.drawable.clan_assamite
    ClanId.BRUAH -> R.drawable.clan_brujah
    ClanId.GANGREL -> R.drawable.clan_gangrel
    ClanId.GIOVANNI -> R.drawable.clan_giovanni
    ClanId.LASOMBRA -> R.drawable.clan_lasombra
    ClanId.MALKAVIAN -> R.drawable.clan_malkavian
    ClanId.NOSFERATU -> R.drawable.clan_nosferatu
    ClanId.RAVNOS -> R.drawable.clan_ravnos
    ClanId.FOLLOWERS_OF_SET -> R.drawable.clan_setite
    ClanId.TOREADOR -> R.drawable.clan_toreador
    ClanId.TREMERE -> R.drawable.clan_tremere
    ClanId.TZIMISCE -> R.drawable.clan_tzimisce
    ClanId.VENTRUE -> R.drawable.clan_ventrue
    ClanId.CAITIFF -> R.drawable.clan_caitiff
}
