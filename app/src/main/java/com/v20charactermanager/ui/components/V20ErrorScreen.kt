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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class V20ErrorType(
    val icon: ImageVector,
    val titleKey: String,
    val title: String,
    val description: String,
    val color: Color
) {
    IMAGE_IMPORT_FAILED(
        icon = Icons.Default.BrokenImage,
        titleKey = "error_image_import",
        title = "Immagine non leggibile",
        description = "Il file selezionato non è un'immagine valida o è corrotto.\n\nPossibili cause:\n• Formato non supportato\n• File danneggiato\n• File non è un'immagine",
        color = Color(0xFFE57373)
    ),
    IMAGE_SAVE_FAILED(
        icon = Icons.Default.SaveAlt,
        titleKey = "error_image_save",
        title = "Salvataggio immagine fallito",
        description = "Impossibile salvare l'immagine sul dispositivo.\n\nPossibili cause:\n• Spazio su disco esaurito\n• Permessi di scrittura negati\n• File di destinazione protetto",
        color = Color(0xFFFFB74D)
    ),
    DOCUMENT_IMPORT_FAILED(
        icon = Icons.Default.InsertDriveFile,
        titleKey = "error_document_import",
        title = "Documento non leggibile",
        description = "Impossibile leggere il file selezionato.\n\nPossibili cause:\n• Formato non supportato\n• File corrotto o danneggiato\n• Permessi di lettura negati",
        color = Color(0xFF90CAF9)
    ),
    DOCUMENT_RENDER_FAILED(
        icon = Icons.Default.PictureAsPdf,
        titleKey = "error_document_render",
        title = "Errore visualizzazione documento",
        description = "Impossibile visualizzare il documento.\n\nPossibili cause:\n• PDF corrotto o danneggiato\n• Documento troppo grande per la memoria\n• Formato non completamente supportato",
        color = Color(0xFFCE93D8)
    ),
    IMPORT_FORMAT_ERROR(
        icon = Icons.Default.Description,
        titleKey = "error_import_format",
        title = "Formato file non valido",
        description = "Il file non è nel formato corretto per l'importazione.\n\nPossibili cause:\n• File non esportato da questa app\n• Versione del formato incompatibile\n• File corrotto o modificato manualmente",
        color = Color(0xFFFFCC80)
    ),
    EXPORT_FAILED(
        icon = Icons.Default.SaveAlt,
        titleKey = "error_export",
        title = "Esportazione fallita",
        description = "Impossibile esportare i dati.\n\nPossibili cause:\n• Spazio su disco esaurito\n• Permessi di scrittura negati\n• Errore interno durante la creazione del file",
        color = Color(0xFFA5D6A7)
    ),
    DATABASE_ERROR(
        icon = Icons.Default.Storage,
        titleKey = "error_database",
        title = "Errore del database",
        description = "Si è verificato un errore con il database dell'app.\n\nPossibili cause:\n• Database corrotto\n• Aggiornamento non riuscito\n• Spazio su disco esaurito",
        color = Color(0xFFEF9A9A)
    ),
    MEMORY_ERROR(
        icon = Icons.Default.Memory,
        titleKey = "error_memory",
        title = "Memoria esaurita",
        description = "L'operazione ha esaurito la memoria disponibile.\n\nPossibili cause:\n• Immagine o documento troppo grande\n• Troppi elementi aperti contemporaneamente\n• Memoria del dispositivo insufficiente",
        color = Color(0xFFFFAB91)
    ),
    PERMISSION_DENIED(
        icon = Icons.Default.Security,
        titleKey = "error_permission",
        title = "Permesso negato",
        description = "L'app non ha i permessi necessari per completare l'operazione.\n\nSoluzione:\n• Concedi i permessi di accesso ai file nelle Impostazioni del dispositivo\n• Riavvia l'app dopo aver concesso i permessi",
        color = Color(0xFFB0BEC5)
    ),
    FILE_NOT_FOUND(
        icon = Icons.Default.FolderOff,
        titleKey = "error_file_not_found",
        title = "File non trovato",
        description = "Il file richiesto non esiste o è stato spostato.\n\nPossibili cause:\n• File eliminato dal dispositivo\n• File spostato in un'altra cartella\n• File su un'unità esterna non montata",
        color = Color(0xFFB0BEC5)
    ),
    CHARACTER_NOT_FOUND(
        icon = Icons.Default.LinkOff,
        titleKey = "error_character_not_found",
        title = "Personaggio non trovato",
        description = "Il personaggio richiesto non è stato trovato nel database.\n\nPossibili cause:\n• Personaggio eliminato\n• ID non valido\n• Database corrotto",
        color = Color(0xFFFFF176)
    ),
    VALIDATION_ERROR(
        icon = Icons.Default.ErrorOutline,
        titleKey = "error_validation",
        title = "Dati non validi",
        description = "I dati inseriti non sono validi.\n\nPossibili cause:\n• Valori fuori range consentito\n• Campi obbligatori vuoti\n• Formato dati errato",
        color = Color(0xFFE0E0E0)
    ),
    NETWORK_ERROR(
        icon = Icons.Default.CloudOff,
        titleKey = "error_network",
        title = "Connessione non disponibile",
        description = "Impossibile connettersi a Internet.\n\nPossibili cause:\n• Dispositivo non connesso\n• Problemi con il server\n• Firewall o restrizioni di rete",
        color = Color(0xFF90A4AE)
    ),
    UNKNOWN_ERROR(
        icon = Icons.Default.ErrorOutline,
        titleKey = "error_unknown",
        title = "Errore imprevisto",
        description = "Si è verificato un errore imprevisto.\n\nSe il problema persiste, prova a:\n• Riavviare l'app\n• Aggiornare l'app\n• Contattare lo sviluppatore",
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
                contentDescription = errorType.title,
                tint = errorType.color,
                modifier = Modifier
                    .size(48.dp)
                    .alpha(pulseAlpha)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = errorType.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = customMessage ?: errorType.description,
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
                Text(text = "Riprova", modifier = Modifier.padding(vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (onGoBack != null) {
            OutlinedButton(
                onClick = onGoBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Indietro", modifier = Modifier.padding(vertical = 4.dp))
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
                    contentDescription = errorType.title,
                    tint = errorType.color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = errorType.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = customMessage ?: errorType.description,
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
                    Text(text = "Chiudi")
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
                        Text(text = "Riprova")
                    }
                }
            }
        }
    }
}
