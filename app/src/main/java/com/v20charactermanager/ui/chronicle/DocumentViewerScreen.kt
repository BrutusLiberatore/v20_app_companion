package com.v20charactermanager.ui.chronicle

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
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.MediaAsset
import com.v20charactermanager.ui.components.V20ErrorScreen
import com.v20charactermanager.ui.components.V20ErrorType
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentViewerScreen(
    asset: MediaAsset?,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(asset?.title ?: "Document") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
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
    onBack: () -> Unit = {}
) {
    var pages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var currentPage by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(file) {
        try {
            val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            val bitmaps = mutableListOf<Bitmap>()
            for (i in 0 until renderer.pageCount) {
                val page = renderer.openPage(i)
                val bitmap = Bitmap.createBitmap(
                    page.width * 2,
                    page.height * 2,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.eraseColor(android.graphics.Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }
            renderer.close()
            pfd.close()
            pages = bitmaps
        } catch (e: OutOfMemoryError) {
            error = "MEMORY:${e.message}"
        } catch (e: Exception) {
            error = "RENDER:${e.message}"
        }
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
            onRetry = { error = null; pages = emptyList() },
            onGoBack = onBack,
            modifier = modifier
        )
    } else if (pages.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = modifier) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (currentPage > 0) currentPage-- },
                    enabled = currentPage > 0
                ) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous")
                }
                Text(
                    text = "${currentPage + 1} / ${pages.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(
                    onClick = { if (currentPage < pages.size - 1) currentPage++ },
                    enabled = currentPage < pages.size - 1
                ) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next")
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    bitmap = pages[currentPage].asImageBitmap(),
                    contentDescription = "Page ${currentPage + 1}",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
