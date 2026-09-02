package com.v20charactermanager.ui.chronicle

import android.graphics.BitmapFactory
import android.os.Bundle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.ui.theme.V20GoldBright
import com.v20charactermanager.ui.theme.V20Ink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImagePresentationActivity : ComponentActivity() {
    companion object {
        const val EXTRA_FILE_PATH = "file_path"
        const val EXTRA_TITLE = "title"
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
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""

        setContent {
            ImagePresentationScreen(
                filePath = filePath,
                title = title,
                onFinish = { finish() }
            )
        }
    }
}

@Composable
private fun ImagePresentationScreen(
    filePath: String,
    title: String,
    onFinish: () -> Unit
) {
    val file = remember(filePath) { File(filePath) }
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(file) {
        isLoading = true
        error = null
        try {
            withContext(Dispatchers.IO) {
                val loaded = BitmapFactory.decodeFile(file.absolutePath)
                if (loaded != null) {
                    bitmap = loaded
                } else {
                    error = "Formato immagine non supportato"
                }
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
        scale = (scale * zoomChange).coerceIn(0.5f, 8f)
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
                    text = "Caricamento immagine...",
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
                Icon(
                    Icons.Filled.BrokenImage,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = error!!, color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onFinish) {
                    Text("Chiudi", color = V20GoldBright)
                }
            }
        } else {
            bitmap?.let { bmp ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformState),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = title,
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
                            text = title,
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

                    if (showControls && scale > 1.1f) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                scale = 1f
                                offset = Offset.Zero
                            }) {
                                Text("Reset zoom", color = V20GoldBright)
                            }
                        }
                    }
                }
            }
        }
    }
}
