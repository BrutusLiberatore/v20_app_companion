package com.v20charactermanager.ui.chronicle

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.MediaAsset
import com.v20charactermanager.ui.components.V20ErrorScreen
import com.v20charactermanager.ui.components.V20ErrorType
import com.v20charactermanager.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

private const val PREFS_NAME = "v20_pdf_pages"
private const val KEY_PREFIX = "last_page_"

private fun getLastPage(context: Context, filePath: String): Int {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getInt(KEY_PREFIX + filePath.hashCode(), 0)
}

private fun saveLastPage(context: Context, filePath: String, page: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putInt(KEY_PREFIX + filePath.hashCode(), page).apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentViewerScreen(
    asset: MediaAsset?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val pdfPageState = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(asset?.title ?: "Document", color = V20GoldBright) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = V20Ink)
                    }
                },
                actions = {
                    if (asset != null) {
                        IconButton(onClick = {
                            val intent = Intent(context, PdfPresentationActivity::class.java).apply {
                                putExtra(PdfPresentationActivity.EXTRA_FILE_PATH, asset.originalFilePath)
                                putExtra(PdfPresentationActivity.EXTRA_PAGE, pdfPageState.intValue)
                            }
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Filled.Fullscreen, contentDescription = "Presentazione", tint = V20GoldBright)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = V20Surface2)
            )
        },
        containerColor = V20Black
    ) { padding ->
        if (asset == null) {
            V20ErrorScreen(
                errorType = V20ErrorType.FILE_NOT_FOUND,
                onGoBack = onBack,
                modifier = Modifier.padding(padding)
            )
        } else {
            val file = remember(asset.originalFilePath) {
                File(asset.originalFilePath)
            }

            if (file.exists() && file.extension.equals("pdf", ignoreCase = true)) {
                PdfRendererContent(
                    file = file,
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onPageChanged = { pdfPageState.intValue = it },
                    onBack = onBack
                )
            } else {
                V20ErrorScreen(
                    errorType = V20ErrorType.FILE_NOT_FOUND,
                    customMessage = "File non trovato o formato non supportato:\n${file.name}",
                    onGoBack = onBack,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun PdfRendererContent(
    file: File,
    modifier: Modifier = Modifier,
    onPageChanged: (Int) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var totalPages by remember { mutableIntStateOf(0) }
    var renderedPages by remember { mutableStateOf<Map<Int, Bitmap>>(emptyMap()) }
    var currentPage by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var loadingMessage by remember { mutableStateOf("") }
    var loadingProgress by remember { mutableFloatStateOf(0f) }
    var error by remember { mutableStateOf<String?>(null) }
    var renderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var pfd by remember { mutableStateOf<ParcelFileDescriptor?>(null) }
    var isSeeking by remember { mutableStateOf(false) }

    val savedPage = remember(file) { getLastPage(context, file.absolutePath) }

    DisposableEffect(file) {
        onDispose {
            renderer?.close()
            pfd?.close()
        }
    }

    LaunchedEffect(file) {
        isLoading = true
        loadingMessage = "Apertura documento..."
        loadingProgress = 0f
        error = null
        currentPage = savedPage.coerceAtLeast(0)
        try {
            withContext(Dispatchers.IO) {
                val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                pfd = descriptor
                val pdfRenderer = PdfRenderer(descriptor)
                renderer = pdfRenderer
                val count = pdfRenderer.pageCount
                totalPages = count
                loadingMessage = "Caricamento pagina 1 di $count..."

                val bitmaps = mutableMapOf<Int, Bitmap>()
                for (i in 0 until count) {
                    if (!isActive) break
                    loadingMessage = "Caricamento pagina ${i + 1} di $count..."
                    loadingProgress = (i.toFloat() / count)

                    val page = pdfRenderer.openPage(i)
                    val scale = 2
                    val bitmap = Bitmap.createBitmap(
                        page.width * scale,
                        page.height * scale,
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.eraseColor(android.graphics.Color.WHITE)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmaps[i] = bitmap
                    page.close()

                    renderedPages = bitmaps.toMap()
                }
                loadingProgress = 1f
                loadingMessage = "Pronto!"
            }
            isLoading = false
        } catch (e: OutOfMemoryError) {
            isLoading = false
            error = "MEMORY:${e.message}"
        } catch (e: Exception) {
            isLoading = false
            error = "RENDER:${e.message}"
        }
    }

    LaunchedEffect(currentPage, file) {
        saveLastPage(context, file.absolutePath, currentPage)
    }

    if (error != null) {
        val (errorType, details) = when {
            error?.startsWith("MEMORY:") == true ->
                V20ErrorType.MEMORY_ERROR to error!!.removePrefix("MEMORY:")
            else ->
                V20ErrorType.DOCUMENT_RENDER_FAILED to error?.removePrefix("RENDER:")
        }
        V20ErrorScreen(
            errorType = errorType,
            errorDetails = details,
            onRetry = {
                error = null
                renderedPages = emptyMap()
                currentPage = 0
                isLoading = true
            },
            onGoBack = onBack,
            modifier = modifier
        )
    } else if (isLoading && renderedPages.isEmpty()) {
        PdfLoadingScreen(
            message = loadingMessage,
            progress = loadingProgress,
            modifier = modifier
        )
    } else {
        Column(modifier = modifier) {
            if (isLoading) {
                LinearProgressIndicator(
                    progress = { loadingProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = V20GoldBright,
                    trackColor = V20Surface2
                )
            }

            LaunchedEffect(currentPage) {
                onPageChanged(currentPage)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (currentPage > 0) {
                            currentPage--
                        }
                    },
                    enabled = currentPage > 0
                ) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous", tint = V20Ink)
                }

                Text(
                    text = "${currentPage + 1} / $totalPages",
                    style = MaterialTheme.typography.bodyMedium,
                    color = V20Ink
                )

                IconButton(
                    onClick = {
                        if (currentPage < totalPages - 1) {
                            currentPage++
                        }
                    },
                    enabled = currentPage < totalPages - 1
                ) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = V20Ink)
                }
            }

            if (totalPages > 1) {
                Slider(
                    value = currentPage.toFloat(),
                    onValueChange = { newValue ->
                        isSeeking = true
                        currentPage = newValue.roundToInt()
                    },
                    onValueChangeFinished = { isSeeking = false },
                    valueRange = 0f..(totalPages - 1).coerceAtLeast(0).toFloat(),
                    steps = (totalPages - 2).coerceAtLeast(0),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = V20GoldBright,
                        activeTrackColor = V20GoldBright,
                        inactiveTrackColor = V20Surface2
                    )
                )
            }

            if (currentPage in renderedPages) {
                Box(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        bitmap = renderedPages[currentPage]!!.asImageBitmap(),
                        contentDescription = "Page ${currentPage + 1}",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                PdfLoadingScreen(
                    message = "Caricamento pagina ${currentPage + 1} di $totalPages...",
                    progress = if (totalPages > 0) (currentPage.toFloat() / totalPages) else 0f,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PdfLoadingScreen(
    message: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = null,
            tint = V20GoldBright,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Caricamento PDF",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = V20GoldBright,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = V20GoldBright,
            trackColor = V20Surface2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = V20InkDim,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Attendere prego...",
            style = MaterialTheme.typography.bodySmall,
            color = V20InkFaint,
            textAlign = TextAlign.Center
        )
    }
}
