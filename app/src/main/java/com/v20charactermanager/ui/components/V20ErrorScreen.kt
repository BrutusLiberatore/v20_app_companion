package com.v20charactermanager.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v20charactermanager.R

enum class V20ErrorType(
    val icon: ImageVector,
    val titleRes: Int,
    val descriptionRes: Int,
    val color: Color
) {
    IMAGE_IMPORT_FAILED(
        icon = Icons.Default.BrokenImage,
        titleRes = R.string.error_image_import,
        descriptionRes = R.string.error_image_import_desc,
        color = Color(0xFFE57373)
    ),
    IMAGE_SAVE_FAILED(
        icon = Icons.Default.SaveAlt,
        titleRes = R.string.error_image_save,
        descriptionRes = R.string.error_image_save_desc,
        color = Color(0xFFFFB74D)
    ),
    DOCUMENT_IMPORT_FAILED(
        icon = Icons.Default.InsertDriveFile,
        titleRes = R.string.error_document_import,
        descriptionRes = R.string.error_document_import_desc,
        color = Color(0xFF90CAF9)
    ),
    DOCUMENT_RENDER_FAILED(
        icon = Icons.Default.PictureAsPdf,
        titleRes = R.string.error_document_render,
        descriptionRes = R.string.error_document_render_desc,
        color = Color(0xFFCE93D8)
    ),
    IMPORT_FORMAT_ERROR(
        icon = Icons.Default.Description,
        titleRes = R.string.error_import_format,
        descriptionRes = R.string.error_import_format_desc,
        color = Color(0xFFFFCC80)
    ),
    EXPORT_FAILED(
        icon = Icons.Default.SaveAlt,
        titleRes = R.string.error_export,
        descriptionRes = R.string.error_export_desc,
        color = Color(0xFFA5D6A7)
    ),
    DATABASE_ERROR(
        icon = Icons.Default.Storage,
        titleRes = R.string.error_database,
        descriptionRes = R.string.error_database_desc,
        color = Color(0xFFEF9A9A)
    ),
    MEMORY_ERROR(
        icon = Icons.Default.Memory,
        titleRes = R.string.error_memory,
        descriptionRes = R.string.error_memory_desc,
        color = Color(0xFFFFAB91)
    ),
    PERMISSION_DENIED(
        icon = Icons.Default.Security,
        titleRes = R.string.error_permission,
        descriptionRes = R.string.error_permission_desc,
        color = Color(0xFFB0BEC5)
    ),
    FILE_NOT_FOUND(
        icon = Icons.Default.FolderOff,
        titleRes = R.string.error_file_not_found,
        descriptionRes = R.string.error_file_not_found_desc,
        color = Color(0xFFB0BEC5)
    ),
    CHARACTER_NOT_FOUND(
        icon = Icons.Default.LinkOff,
        titleRes = R.string.error_character_not_found,
        descriptionRes = R.string.error_character_not_found_desc,
        color = Color(0xFFFFF176)
    ),
    VALIDATION_ERROR(
        icon = Icons.Default.ErrorOutline,
        titleRes = R.string.error_validation,
        descriptionRes = R.string.error_validation_desc,
        color = Color(0xFFE0E0E0)
    ),
    NETWORK_ERROR(
        icon = Icons.Default.CloudOff,
        titleRes = R.string.error_network,
        descriptionRes = R.string.error_network_desc,
        color = Color(0xFF90A4AE)
    ),
    UNKNOWN_ERROR(
        icon = Icons.Default.ErrorOutline,
        titleRes = R.string.error_unknown,
        descriptionRes = R.string.error_unknown_desc,
        color = Color(0xFFE0E0E0)
    )
}

@Composable
fun V20ErrorScreen(
    errorType: V20ErrorType,
    customMessage: String? = null,
    errorDetails: String? = null,
    onRetry: (() -> Unit)? = null,
    onGoBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(errorType.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = errorType.icon,
                contentDescription = stringResource(errorType.titleRes),
                tint = errorType.color,
                modifier = Modifier
                    .size(48.dp)
                    .alpha(pulseAlpha)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(errorType.titleRes),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = customMessage ?: stringResource(errorType.descriptionRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        if (errorDetails != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = errorDetails,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (onRetry != null) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(R.string.error_retry), modifier = Modifier.padding(vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (onGoBack != null) {
            OutlinedButton(
                onClick = onGoBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(R.string.error_back), modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
fun V20ErrorDialog(
    errorType: V20ErrorType,
    customMessage: String? = null,
    errorDetails: String? = null,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(errorType.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = errorType.icon,
                    contentDescription = stringResource(errorType.titleRes),
                    tint = errorType.color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(errorType.titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = customMessage ?: stringResource(errorType.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            if (errorDetails != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = errorDetails,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.error_close))
                }

                if (onRetry != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = stringResource(R.string.error_retry))
                    }
                }
            }
        }
    }
}
