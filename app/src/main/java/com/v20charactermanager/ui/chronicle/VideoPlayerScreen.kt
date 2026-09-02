package com.v20charactermanager.ui.chronicle

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.v20charactermanager.ui.theme.V20GoldBright
import com.v20charactermanager.ui.theme.V20Surface2
import java.io.File

class VideoPlayerActivity : ComponentActivity() {
    companion object {
        const val EXTRA_FILE_PATH = "file_path"
        const val EXTRA_TITLE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val filePath = intent.getStringExtra(EXTRA_FILE_PATH) ?: run {
            finish()
            return
        }
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""

        setContent {
            VideoPlayerScreen(
                filePath = filePath,
                title = title,
                onFinish = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPlayerScreen(
    filePath: String,
    title: String,
    onFinish: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isLooping by remember { mutableStateOf(true) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val file = File(filePath)
            val uri = Uri.fromFile(file)
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        color = V20GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        exoPlayer.release()
                        onFinish()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Indietro", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isLooping = !isLooping
                        exoPlayer.repeatMode = if (isLooping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
                    }) {
                        Icon(
                            if (isLooping) Icons.Default.Repeat else Icons.Default.RepeatOne,
                            contentDescription = if (isLooping) "Loop ON" else "Loop OFF",
                            tint = if (isLooping) V20GoldBright else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = V20Surface2)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
