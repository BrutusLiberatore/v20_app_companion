package com.v20charactermanager.ui.chronicle

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.ui.theme.V20Black
import com.v20charactermanager.ui.theme.V20GoldBright
import com.v20charactermanager.ui.theme.V20Ink
import com.v20charactermanager.ui.theme.V20Surface2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class PdfPresentationActivity : ComponentActivity() {
    companion object {
        const val EXTRA_FILE_PATH = "file_path"
        const val EXTRA_PAGE = "page"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        val filePath = intent.getStringExtra(EXTRA_FILE_PATH) ?: run {
            finish()
            return
        }
        val startPage = intent.getIntExtra(EXTRA_PAGE, 0)

        setContent {
            PdfPresentationScreen(
                filePath = filePath,
                startPage = startPage,
                onFinish = { finish() }
            )
        }
    }
}

@Composable
private fun PdfPresentationScreen(
    filePath: String,
    startPage: Int,
    onFinish: () -> Unit
) {
    val file = remember(filePath) { java.io.File(filePath) }
    var pages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var currentPage by remember { mutableIntStateOf(startPage.coerceAtLeast(0)) }
    var isLoading by remember { mutableStateOf(true) }
    var totalPages by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(file) {
        isLoading = true
        error = null
        try {
            withContext(Dispatchers.IO) {
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(pfd)
                val count = renderer.pageCount
                totalPages = count
                val bitmaps = mutableListOf<Bitmap>()
                for (i in 0 until count) {
                    if (!isActive) break
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
            }
            isLoading = false
        } catch (e: OutOfMemoryError) {
            isLoading = false
            error = "Memoria esaurita"
        } catch (e: Exception) {
            isLoading = false
            error = e.message ?: "Errore sconosciuto"
        }
    }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
        offset = Offset(
            x = offset.x + panChange.x,
            y = offset.y + panChange.y
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showControls = !showControls },
                    onDoubleTap = { tapOffset ->
                        if (scale > 1.5f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 3f
                            offset = Offset(
                                x = -(tapOffset.x - size.width / 2f) * 2f,
                                y = -(tapOffset.y - size.height / 2f) * 2f
                            )
                        }
                    }
                )
            }
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = V20GoldBright)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Caricamento PDF...",
                    color = V20GoldBright,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (error != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = error!!, color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onFinish) {
                    Text("Chiudi", color = V20GoldBright)
                }
            }
        } else if (pages.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = transformState),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = pages[currentPage].asImageBitmap(),
                    contentDescription = "Page ${currentPage + 1}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        }
                )
            }

            if (showControls) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onFinish) {
                            Icon(Icons.Filled.Close, "Chiudi", tint = Color.White)
                        }
                        Text(
                            text = "${currentPage + 1} / $totalPages",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (scale > 1.1f) "Zoom: ${String.format("%.1f", scale)}x" else "",
                            color = V20GoldBright,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (currentPage > 0) {
                                    currentPage--
                                    scale = 1f
                                    offset = Offset.Zero
                                }
                            },
                            enabled = currentPage > 0
                        ) {
                            Icon(Icons.Filled.ChevronLeft, "Precedente", tint = Color.White, modifier = Modifier.size(36.dp))
                        }

                        Slider(
                            value = currentPage.toFloat(),
                            onValueChange = { currentPage = it.roundToInt() },
                            valueRange = 0f..(totalPages - 1).coerceAtLeast(0).toFloat(),
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = V20GoldBright,
                                activeTrackColor = V20GoldBright,
                                inactiveTrackColor = Color.Gray
                            )
                        )

                        IconButton(
                            onClick = {
                                if (currentPage < pages.size - 1) {
                                    currentPage++
                                    scale = 1f
                                    offset = Offset.Zero
                                }
                            },
                            enabled = currentPage < pages.size - 1
                        ) {
                            Icon(Icons.Filled.ChevronRight, "Successiva", tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        }
    }
}
