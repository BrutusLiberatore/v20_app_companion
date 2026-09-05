package com.v20charactermanager.ui.liveroom

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.*
import java.io.File
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private val FeltGreen = Color(0xFF1B5E20)
private val FeltGreenDark = Color(0xFF0D3B12)
private val FeltBorder = Color(0xFF8D6E3F)
private val Gold = Color(0xFFD4A847)
private val GoldDark = Color(0xFFA67C2E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRoomScreen(
    uiState: LiveRoomState,
    startAsMaster: Boolean,
    chronicleName: String,
    autoHost: String = "",
    autoPort: Int = 0,
    autoPlayerName: String = "",
    autoCharacterId: String = "",
    onCreateRoom: (String, String, String) -> Unit,
    onJoinRoom: (String, Int, String, String?) -> Unit,
    onRetryJoin: () -> Unit = {},
    onPresentFile: (String, String, ByteArray) -> Unit,
    onDismissFile: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onDisconnect: () -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onSendStatUpdate: (String, String, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var autoCreated by remember { mutableStateOf(false) }

    LaunchedEffect(startAsMaster, chronicleName, autoCreated) {
        if (startAsMaster && chronicleName.isNotBlank() && !autoCreated && !uiState.isConnected) {
            autoCreated = true
            onCreateRoom(chronicleName, "Master", "")
        }
    }

    // Auto-join when coming from SelectCharacterScreen
    LaunchedEffect(autoHost, autoPort, autoPlayerName, autoCharacterId) {
        if (autoHost.isNotBlank() && autoPort > 0 && !uiState.isConnected && !autoCreated) {
            autoCreated = true
            val playerName = autoPlayerName.ifBlank { "Giocatore" }
            val charId = autoCharacterId.ifBlank { null }
            onJoinRoom(autoHost, autoPort, playerName, charId)
        }
    }
    if (uiState.isFileFullscreen && uiState.presentedFile != null) {
        FullscreenPresentation(
            file = uiState.presentedFile,
            onDismiss = onDismissFile,
            onToggleMinimize = onToggleFullscreen
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            uiState.room != null -> uiState.room.name
                            else -> stringResource(R.string.live_room)
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (uiState.isConnected) Color(0xFF1A1A2E) else MaterialTheme.colorScheme.surface
                ),
                actions = {
                    if (uiState.isConnected && uiState.presentedFile != null) {
                        IconButton(onClick = onToggleFullscreen) {
                            Icon(
                                if (uiState.isFileFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = stringResource(R.string.live_fullscreen),
                                tint = Color.White
                            )
                        }
                    }
                    if (uiState.isConnected) {
                        IconButton(onClick = onDisconnect) {
                            Icon(Icons.Default.LinkOff, contentDescription = stringResource(R.string.live_disconnect), tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            !uiState.isConnected -> {
                ConnectingOverlay(
                    uiState = uiState,
                    onClearError = onClearError,
                    onBack = onBack,
                    onRetry = onRetryJoin,
                    onManualJoin = { host, port, name, charId ->
                        onJoinRoom(host, port, name, charId)
                    },
                    playerName = uiState.localPlayer?.name ?: "",
                    modifier = modifier.padding(padding)
                )
            }
            else -> {
                VirtualTableView(
                    uiState = uiState,
                    onPresentFile = onPresentFile,
                    onDismissFile = onDismissFile,
                    onToggleFullscreen = onToggleFullscreen,
                    onSendStatUpdate = onSendStatUpdate,
                    modifier = modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ConnectingOverlay(
    uiState: LiveRoomState,
    onClearError: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit = {},
    onManualJoin: (String, Int, String, String?) -> Unit = { _, _, _, _ -> },
    playerName: String = "",
    modifier: Modifier = Modifier
) {
    var showManualDialog by remember { mutableStateOf(false) }
    var manualHost by remember { mutableStateOf("") }
    var manualPort by remember { mutableStateOf("39641") }

    val isEmulatorIp = remember(uiState.error) {
        val err = uiState.error ?: ""
        err.contains("10.0.2.") || err.contains("10.0.3.")
    }

    val infiniteTransition = rememberInfiniteTransition(label = "connecting")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Casino,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Gold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.live_room),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.error != null) {
                Card(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            uiState.error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onClearError, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                if (isEmulatorIp) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.padding(horizontal = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A4A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.live_emulator_warning),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.action_back))
                        }
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = Gold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.live_retry), color = Color(0xFF1A1A2E), fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedButton(
                        onClick = { showManualDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.live_room_manual_connect))
                    }
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = Gold,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.live_connecting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = alpha)
                )
            }
        }
    }

    if (showManualDialog) {
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text(stringResource(R.string.live_room_manual_connect)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.live_room_manual_desc),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = manualHost,
                        onValueChange = { manualHost = it },
                        label = { Text("IP") },
                        placeholder = { Text("192.168.1.100") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualPort,
                        onValueChange = { manualPort = it },
                        label = { Text(stringResource(R.string.live_room_port)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val port = manualPort.toIntOrNull() ?: 39641
                        if (manualHost.isNotBlank()) {
                            onClearError()
                            onManualJoin(manualHost, port, playerName.ifBlank { "Giocatore" }, null)
                            showManualDialog = false
                        }
                    }
                ) { Text(stringResource(R.string.action_connect)) }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun VirtualTableView(
    uiState: LiveRoomState,
    onPresentFile: (String, String, ByteArray) -> Unit,
    onDismissFile: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onSendStatUpdate: (String, String, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
            val fileName = it.lastPathSegment ?: "file"
            val bytes = context.contentResolver.openInputStream(it)?.readBytes() ?: return@rememberLauncherForActivityResult
            onPresentFile(fileName, mimeType, bytes)
        }
    }

    val totalSeats = 8
    val masterAngle = -90f
    val seatDataList = remember(uiState, totalSeats) {
        buildCircularSeats(uiState, totalSeats, masterAngle)
    }

    var tableBoxSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        // Table area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .onGloballyPositioned { tableBoxSize = it.size },
            contentAlignment = Alignment.Center
        ) {
            // IP banner for master - shows connection info for players
            if (uiState.isMaster && uiState.room != null && uiState.room.host.isNotBlank()) {
                var showIpBanner by remember { mutableStateOf(true) }
                if (showIpBanner) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A4A)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = Gold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.live_room_share_ip),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "${uiState.room.host}:${uiState.room.port}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = { showIpBanner = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.action_close),
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Medieval round table asset
            Image(
                painter = painterResource(id = R.drawable.assets_tavolo),
                contentDescription = "Tavolo",
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.Fit
            )

            // Center info
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.room != null) {
                    Text(
                        text = uiState.room.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.isMaster) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Porta: ${uiState.room.port}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold
                        )
                    }
                }
                if (uiState.presentedFile != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = uiState.presentedFile.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            // Chairs with player icons positioned in a circle
            seatDataList.forEach { seat ->
                val angleRad = Math.toRadians(seat.angleDeg.toDouble())

                ChairWithPlayer(
                    seat = seat,
                    modifier = Modifier.offset {
                        val radiusX = tableBoxSize.width.toFloat() * 0.40f
                        val radiusY = tableBoxSize.height.toFloat() * 0.40f
                        IntOffset(
                            x = (radiusX * cos(angleRad)).toInt(),
                            y = (radiusY * sin(angleRad)).toInt()
                        )
                    }
                )
            }
        }

        // Bottom panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A4A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (uiState.isMaster) {
                MasterBottomPanel(
                    uiState = uiState,
                    onPresentFile = onPresentFile,
                    filePickerLauncher = filePickerLauncher,
                    onDismissFile = onDismissFile,
                    onToggleFullscreen = onToggleFullscreen
                )
            } else {
                PlayerBottomPanel(
                    uiState = uiState,
                    onToggleFullscreen = onToggleFullscreen
                )
            }
        }
    }
}

@Composable
private fun ChairWithPlayer(
    seat: SeatData,
    modifier: Modifier = Modifier
) {
    val chairPx = with(LocalDensity.current) { seat.chairSize.toPx() }
    val avatarPx = with(LocalDensity.current) { seat.avatarSize.toPx() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Chair image, rotated to face center
        Image(
            painter = painterResource(id = R.drawable.assets_sedia),
            contentDescription = null,
            modifier = Modifier
                .size(seat.chairSize)
                .graphicsLayer {
                    rotationZ = seat.rotation
                },
            contentScale = ContentScale.Fit
        )

        // Player avatar on top of chair
        if (seat.isOccupied) {
            Box(
                modifier = Modifier
                    .offset(y = with(LocalDensity.current) { -(chairPx * 0.15f).toDp() })
                    .size(seat.avatarSize)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        when {
                            seat.isMaster -> Gold
                            seat.isLocal -> Color(0xFF4CAF50)
                            else -> Color(0xFF5C6BC0)
                        }
                    )
                    .border(
                        2.dp,
                        when {
                            seat.isMaster -> GoldDark
                            seat.isLocal -> Color(0xFF2E7D32)
                            else -> Color(0xFF3949AB)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (seat.isMaster) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFF1A1A2E),
                        modifier = Modifier.size(with(LocalDensity.current) { (avatarPx * 0.55f).toDp() })
                    )
                } else {
                    AsyncImage(
                        model = seat.portraitUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    if (seat.portraitUri == null) {
                        Text(
                            text = seat.initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = with(LocalDensity.current) { (avatarPx * 0.4f).toSp() }
                        )
                    }
                }
            }

            // Name tag below chair
            Surface(
                modifier = Modifier.offset(y = with(LocalDensity.current) { (chairPx * 0.55f).toDp() }),
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.7f)
            ) {
                Text(
                    text = seat.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        seat.isMaster -> Gold
                        seat.isLocal -> Color(0xFF4CAF50)
                        seat.isOccupied -> Color.White
                        else -> Color.White.copy(alpha = 0.4f)
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            // Empty seat indicator
            Icon(
                Icons.Default.PersonAdd,
                contentDescription = "Posto libero",
                tint = Color.White.copy(alpha = 0.25f),
                modifier = Modifier.size(with(LocalDensity.current) { (chairPx * 0.35f).toDp() })
            )
        }
    }
}

@Composable
private fun FeltTable(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padding = 8.dp.toPx()
        val cornerRadius = CornerRadius(40.dp.toPx())

        drawRoundRect(
            color = FeltBorder,
            cornerRadius = cornerRadius,
            size = Size(w, h)
        )

        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(FeltGreen, FeltGreenDark),
                center = Offset(w / 2, h / 2),
                radius = w / 2
            ),
            topLeft = Offset(padding, padding),
            size = Size(w - padding * 2, h - padding * 2),
            cornerRadius = CornerRadius(36.dp.toPx())
        )

        drawRoundRect(
            color = Gold.copy(alpha = 0.3f),
            topLeft = Offset(padding + 3.dp.toPx(), padding + 3.dp.toPx()),
            size = Size(w - (padding + 3.dp.toPx()) * 2, h - (padding + 3.dp.toPx()) * 2),
            cornerRadius = CornerRadius(32.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
private fun MasterBottomPanel(
    uiState: LiveRoomState,
    onPresentFile: (String, String, ByteArray) -> Unit,
    filePickerLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    onDismissFile: () -> Unit,
    onToggleFullscreen: () -> Unit
) {
    var showFileSelector by remember { mutableStateOf(false) }
    val localContext = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Master",
                color = Gold,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${uiState.connectedPlayers.size} giocatori",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.presentedFile != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(uiState.presentedFile.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("In presentazione", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onToggleFullscreen) {
                    Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = Color.White)
                }
                IconButton(onClick = onDismissFile) {
                    Icon(Icons.Default.Close, contentDescription = "Chiudi", tint = Color.White)
                }
            }
        } else {
            Button(
                onClick = { showFileSelector = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PresentToAll, contentDescription = null, tint = Color(0xFF1A1A2E))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Presenta File", color = Color(0xFF1A1A2E), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showFileSelector) {
        ChronicleFileSelector(
            assets = uiState.chronicleAssets,
            onSelectAsset = { asset ->
                showFileSelector = false
                try {
                    val file = java.io.File(asset.originalFilePath)
                    if (file.exists()) {
                        val bytes = file.readBytes()
                        val mimeType = when {
                            asset.type == MediaAssetType.DOCUMENT -> "application/pdf"
                            asset.type == MediaAssetType.VIDEO -> "video/*"
                            asset.originalFilePath.endsWith(".pdf") -> "application/pdf"
                            asset.originalFilePath.endsWith(".gif") -> "image/gif"
                            asset.originalFilePath.endsWith(".svg") -> "image/svg+xml"
                            else -> "image/*"
                        }
                        onPresentFile(asset.title, mimeType, bytes)
                    }
                } catch (_: Exception) {}
            },
            onImportFromDevice = {
                showFileSelector = false
                filePickerLauncher.launch(arrayOf("image/*", "application/pdf", "video/*", "*/*"))
            },
            onDismiss = { showFileSelector = false }
        )
    }
}

@Composable
private fun ChronicleFileSelector(
    assets: List<MediaAsset>,
    onSelectAsset: (MediaAsset) -> Unit,
    onImportFromDevice: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona File dalla Cronaca") },
        text = {
            Column {
                if (assets.isEmpty()) {
                    Text(
                        text = "Nessun file nella cronaca.\nImporta dalla memoria del dispositivo.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(assets) { asset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelectAsset(asset) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    when (asset.type) {
                                        MediaAssetType.DOCUMENT -> Icons.Default.PictureAsPdf
                                        MediaAssetType.VIDEO -> Icons.Default.Videocam
                                        MediaAssetType.MAP, MediaAssetType.LOCATION_MAP -> Icons.Default.Map
                                        else -> Icons.Default.Image
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(asset.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    if (asset.description.isNotBlank()) {
                                        Text(
                                            asset.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                if (asset.tags.isNotEmpty()) {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(asset.tags.first(), style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onImportFromDevice) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Importa da Dispositivo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

@Composable
private fun PlayerBottomPanel(
    uiState: LiveRoomState,
    onToggleFullscreen: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = uiState.localPlayer?.name ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                uiState.localPlayer?.characterName?.let {
                    Text(it, color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                text = "connesso",
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (uiState.presentedFile != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onToggleFullscreen,
                colors = CardDefaults.cardColors(containerColor = Color(0xFF37474F))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        uiState.presentedFile.mimeType.startsWith("image/") -> {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = uiState.presentedFile.data,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        uiState.presentedFile.mimeType == "application/pdf" -> {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                        }
                        else -> {
                            Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = Gold, modifier = Modifier.size(48.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(uiState.presentedFile.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Tocca per ingrandire", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.Fullscreen, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

private data class SeatData(
    val initials: String,
    val displayName: String,
    val isMaster: Boolean,
    val isLocal: Boolean,
    val isOccupied: Boolean,
    val angleDeg: Float,
    val rotation: Float = 0f,
    val chairSize: Dp = 60.dp,
    val avatarSize: Dp = 36.dp,
    val portraitUri: String? = null
)

private fun buildCircularSeats(
    uiState: LiveRoomState,
    totalSeats: Int = 8,
    masterAngle: Float = -90f
): List<SeatData> {
    val seats = mutableListOf<SeatData>()

    val angleStep = 360f / totalSeats

    for (i in 0 until totalSeats) {
        val angleDeg = masterAngle + (i * angleStep)

        val isMasterSeat = i == 0
        val playerIndex = if (isMasterSeat) -1 else i - 1
        val player = if (playerIndex >= 0 && playerIndex < uiState.connectedPlayers.size) {
            uiState.connectedPlayers[playerIndex]
        } else null

        seats.add(
            SeatData(
                initials = when {
                    isMasterSeat -> uiState.room?.masterName?.take(2)?.uppercase() ?: "M"
                    player != null -> player.name.take(2).uppercase()
                    else -> "?"
                },
                displayName = when {
                    isMasterSeat -> uiState.room?.masterName ?: "Master"
                    player != null -> player.characterName ?: player.name
                    else -> "Libero"
                },
                isMaster = isMasterSeat,
                isLocal = isMasterSeat && uiState.isMaster,
                isOccupied = isMasterSeat || player != null,
                angleDeg = angleDeg,
                rotation = angleDeg + 90f,
                chairSize = 60.dp,
                avatarSize = if (isMasterSeat) 40.dp else 34.dp,
                portraitUri = player?.characterId
            )
        )
    }

    return seats
}

@Composable
private fun FullscreenPresentation(
    file: PresentedFile,
    onDismiss: () -> Unit,
    onToggleMinimize: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            file.mimeType.startsWith("image/") -> {
                AsyncImage(
                    model = file.data,
                    contentDescription = file.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            file.mimeType == "application/pdf" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(file.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.InsertDriveFile,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(file.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        // Controls overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Chiudi", tint = Color.White)
            }
            IconButton(onClick = onToggleMinimize) {
                Icon(Icons.Default.FullscreenExit, contentDescription = "Riduci", tint = Color.White)
            }
        }
    }
}
