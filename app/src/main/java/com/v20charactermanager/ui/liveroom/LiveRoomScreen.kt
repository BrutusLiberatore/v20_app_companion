package com.v20charactermanager.ui.liveroom

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.VideoView
import android.widget.MediaController
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.viewinterop.AndroidView
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
    chronicleId: String = "",
    audioViewModel: com.v20charactermanager.ui.chronicle.AudioViewModel? = null,
    chronicleRepository: com.v20charactermanager.domain.repository.ChronicleRepository? = null,
    autoHost: String = "",
    autoPort: Int = 0,
    autoPlayerName: String = "",
    autoCharacterId: String = "",
    onCreateRoom: (String, String, String) -> Unit,
    onJoinRoom: (String, Int, String, String?) -> Unit,
    onRetryJoin: () -> Unit = {},
    onPresentAsset: (String, String, String) -> Unit,
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
        BackHandler { onToggleFullscreen() }
        FullscreenPresentation(
            file = uiState.presentedFile,
            isMaster = uiState.isMaster,
            onDismiss = { if (uiState.isMaster) onDismissFile() else onToggleFullscreen() },
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
                    chronicleId = chronicleId,
                    audioViewModel = audioViewModel,
                    chronicleRepository = chronicleRepository,
                    onPresentAsset = onPresentAsset,
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
                    text = if (uiState.connectionStatus.isNotBlank()) uiState.connectionStatus else stringResource(R.string.live_connecting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = alpha),
                    textAlign = TextAlign.Center
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
    chronicleId: String = "",
    audioViewModel: com.v20charactermanager.ui.chronicle.AudioViewModel? = null,
    chronicleRepository: com.v20charactermanager.domain.repository.ChronicleRepository? = null,
    onPresentAsset: (String, String, String) -> Unit,
    onDismissFile: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onSendStatUpdate: (String, String, Int?) -> Unit,
    modifier: Modifier = Modifier
) {

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
        // IP banner for master - outside table area so it's always readable
        if (uiState.isMaster && uiState.room != null && uiState.room.host.isNotBlank()) {
            var showIpBanner by remember { mutableStateOf(true) }
            if (showIpBanner) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
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

        // Table area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .onGloballyPositioned { tableBoxSize = it.size },
            contentAlignment = Alignment.Center
        ) {
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
                    chronicleId = chronicleId,
                    audioViewModel = audioViewModel,
                    chronicleRepository = chronicleRepository,
                    onPresentAsset = onPresentAsset,
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
    chronicleId: String = "",
    audioViewModel: com.v20charactermanager.ui.chronicle.AudioViewModel? = null,
    chronicleRepository: com.v20charactermanager.domain.repository.ChronicleRepository? = null,
    onPresentAsset: (String, String, String) -> Unit,
    onDismissFile: () -> Unit,
    onToggleFullscreen: () -> Unit
) {
    var showFileSelector by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showAudioMixer by remember { mutableStateOf(false) }
    var showChronicleInfo by remember { mutableStateOf(false) }
    val audioUiState = audioViewModel?.uiState?.collectAsState()
    val audioState = audioUiState?.value ?: com.v20charactermanager.ui.chronicle.AudioMixUiState()

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
            Spacer(modifier = Modifier.width(4.dp))
            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Audio Mixer") },
                        leadingIcon = { Icon(Icons.Default.MusicNote, contentDescription = null) },
                        onClick = { showMenu = false; showAudioMixer = true }
                    )
                    DropdownMenuItem(
                        text = { Text("Cronaca") },
                        leadingIcon = { Icon(Icons.Default.Book, contentDescription = null) },
                        onClick = { showMenu = false; showChronicleInfo = true }
                    )
                }
            }
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
                Text("Presenta dalla Cronaca", color = Color(0xFF1A1A2E), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showFileSelector) {
        ChronicleFileSelector(
            assets = uiState.chronicleAssets,
            onSelectAsset = { asset ->
                showFileSelector = false
                val mimeType = when {
                    asset.type == MediaAssetType.DOCUMENT -> "application/pdf"
                    asset.type == MediaAssetType.VIDEO -> "video/*"
                    asset.originalFilePath.endsWith(".pdf") -> "application/pdf"
                    asset.originalFilePath.endsWith(".gif") -> "image/gif"
                    asset.originalFilePath.endsWith(".svg") -> "image/svg+xml"
                    else -> "image/*"
                }
                onPresentAsset(asset.id, asset.title, mimeType)
            },
            onDismiss = { showFileSelector = false }
        )
    }

    if (showAudioMixer && audioViewModel != null) {
        AudioMixerPopup(
            audioState = audioState,
            chronicleId = chronicleId,
            onTogglePlay = { audioViewModel.togglePlay(it) },
            onStop = { audioViewModel.stopTrack(it) },
            onStopAll = { audioViewModel.stopAll() },
            onSetVolume = { id, vol -> audioViewModel.setVolume(id, vol) },
            onSetLooping = { id, loop -> audioViewModel.setLooping(id, loop) },
            onActivatePreset = { audioViewModel.activatePreset(it) },
            onSavePreset = { name -> audioViewModel.savePreset(chronicleId, name) },
            onDeletePreset = { id -> audioViewModel.deletePreset(id, chronicleId) },
            onDismiss = { showAudioMixer = false }
        )
    }

    if (showChronicleInfo && chronicleRepository != null) {
        ChronicleDeepBrowserPopup(
            chronicleRepo = chronicleRepository,
            chronicleId = chronicleId,
            chronicleName = uiState.room?.name ?: "",
            onDismiss = { showChronicleInfo = false }
        )
    }
}

@Composable
private fun ChronicleFileSelector(
    assets: List<MediaAsset>,
    onSelectAsset: (MediaAsset) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona File dalla Cronaca") },
        text = {
            Column {
                if (assets.isEmpty()) {
                    Text(
                        text = "Nessun file nella cronaca.\nImporta file nella sezione Media della cronaca.",
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
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        dismissButton = null
    )
}

@Composable
private fun AudioMixerPopup(
    audioState: com.v20charactermanager.ui.chronicle.AudioMixUiState,
    chronicleId: String = "",
    onTogglePlay: (String) -> Unit,
    onStop: (String) -> Unit,
    onStopAll: () -> Unit,
    onSetVolume: (String, Float) -> Unit,
    onSetLooping: (String, Boolean) -> Unit,
    onActivatePreset: (com.v20charactermanager.domain.model.AudioPreset) -> Unit,
    onSavePreset: (String) -> Unit,
    onDeletePreset: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFFE91E63))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Audio Mixer")
            }
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 450.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1A1A2E),
                    contentColor = Color(0xFFE91E63)
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Tracce", modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Preset", modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (selectedTab) {
                    0 -> {
                        if (audioState.tracks.isEmpty()) {
                            Text(
                                text = "Nessuna traccia audio.\nImporta audio nella sezione Audio della cronaca.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(audioState.tracks) { track ->
                                    AudioTrackRow(
                                        track = track,
                                        onTogglePlay = { onTogglePlay(track.id) },
                                        onStop = { onStop(track.id) },
                                        onSetVolume = { vol -> onSetVolume(track.id, vol) },
                                        onSetLooping = { loop -> onSetLooping(track.id, loop) }
                                    )
                                }
                            }
                        }
                        if (audioState.tracks.any { it.isActive }) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { showSaveDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE91E63))
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salva Preset", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = onStopAll,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ferma Tutto", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    1 -> {
                        if (audioState.presets.isEmpty()) {
                            Text(
                                text = "Nessun preset salvato.\nSeleziona delle tracce attive e salva un preset.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(audioState.presets) { preset ->
                                    PresetRow(
                                        preset = preset,
                                        onActivate = { onActivatePreset(preset) },
                                        onDelete = { onDeletePreset(preset.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Chiudi") }
        },
        dismissButton = null
    )

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Salva Preset Audio") },
            text = {
                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    label = { Text("Nome Preset") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (presetName.isNotBlank()) {
                        onSavePreset(presetName)
                        presetName = ""
                        showSaveDialog = false
                    }
                }) { Text("Salva") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false; presetName = "" }) { Text("Annulla") }
            }
        )
    }
}

@Composable
private fun PresetRow(
    preset: com.v20charactermanager.domain.model.AudioPreset,
    onActivate: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A3E)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(preset.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${preset.tracks.size} tracce", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }
            IconButton(onClick = onActivate, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Attiva", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = Color(0xFFFF5722), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun AudioTrackRow(
    track: com.v20charactermanager.domain.model.AudioTrack,
    onTogglePlay: () -> Unit,
    onStop: () -> Unit,
    onSetVolume: (Float) -> Unit,
    onSetLooping: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (track.isActive) Color(0xFF1B5E20).copy(alpha = 0.4f) else Color(0xFF2A2A3E)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onTogglePlay, modifier = Modifier.size(32.dp)) {
                    Icon(
                        if (track.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (track.isActive) "Pausa" else "Play",
                        tint = if (track.isActive) Color(0xFF4CAF50) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(track.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        track.category.name,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
                if (track.isActive) {
                    IconButton(onClick = onStop, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color(0xFFFF5722), modifier = Modifier.size(16.dp))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                Icon(Icons.Default.VolumeDown, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                Slider(
                    value = track.volume,
                    onValueChange = onSetVolume,
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f).height(20.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFE91E63),
                        activeTrackColor = Color(0xFFE91E63)
                    )
                )
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                Checkbox(
                    checked = track.isLooping,
                    onCheckedChange = onSetLooping,
                    modifier = Modifier.size(16.dp),
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFE91E63))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Loop", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ChronicleDeepBrowserPopup(
    chronicleRepo: com.v20charactermanager.domain.repository.ChronicleRepository,
    chronicleId: String,
    chronicleName: String,
    onDismiss: () -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf("NPC", "Luoghi", "Note", "Scena", "Eventi", "Segreti")

    val npcs by chronicleRepo.getNpcs(chronicleId).collectAsState(initial = emptyList())
    val locations by chronicleRepo.getLocations(chronicleId).collectAsState(initial = emptyList())
    val notes by chronicleRepo.getChronicleNotes(chronicleId).collectAsState(initial = emptyList())
    val scenes by chronicleRepo.getScenes(chronicleId).collectAsState(initial = emptyList())
    val events by chronicleRepo.getEvents(chronicleId).collectAsState(initial = emptyList())
    val secrets by chronicleRepo.getSecrets(chronicleId).collectAsState(initial = emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Book, contentDescription = null, tint = Gold)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Cronaca", color = Gold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (chronicleName.isNotBlank()) {
                        Text(chronicleName, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 450.dp)) {
                ScrollableTabRow(
                    selectedTabIndex = selectedSection,
                    containerColor = Color(0xFF1A1A2E),
                    contentColor = Gold,
                    edgePadding = 4.dp
                ) {
                    sections.forEachIndexed { index, label ->
                        Tab(
                            selected = selectedSection == index,
                            onClick = { selectedSection = index },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(label, modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (selectedSection) {
                    0 -> {
                        if (npcs.isEmpty()) {
                            EmptySection("Nessun NPC presente")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(npcs) { npc ->
                                    SimpleListItem(
                                        title = npc.name,
                                        subtitle = npc.clanId ?: npc.role,
                                        icon = Icons.Default.Person,
                                        iconTint = Color(0xFF9C27B0)
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        if (locations.isEmpty()) {
                            EmptySection("Nessun luogo presente")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(locations) { loc ->
                                    SimpleListItem(
                                        title = loc.name,
                                        subtitle = loc.description ?: "",
                                        icon = Icons.Default.LocationOn,
                                        iconTint = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        if (notes.isEmpty()) {
                            EmptySection("Nessuna nota presente")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(notes) { note ->
                                    SimpleListItem(
                                        title = note.text.take(40),
                                        subtitle = note.text.drop(40).take(80),
                                        icon = Icons.Default.StickyNote2,
                                        iconTint = Color(0xFFFFC107)
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        if (scenes.isEmpty()) {
                            EmptySection("Nessuna scena presente")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(scenes) { scene ->
                                    SimpleListItem(
                                        title = scene.title,
                                        subtitle = scene.description?.take(80) ?: "",
                                        icon = Icons.Default.Theaters,
                                        iconTint = Color(0xFFFF5722)
                                    )
                                }
                            }
                        }
                    }
                    4 -> {
                        if (events.isEmpty()) {
                            EmptySection("Nessun evento presente")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(events) { event ->
                                    SimpleListItem(
                                        title = event.title,
                                        subtitle = event.description?.take(80) ?: "",
                                        icon = Icons.Default.Event,
                                        iconTint = Color(0xFF2196F3)
                                    )
                                }
                            }
                        }
                    }
                    5 -> {
                        if (secrets.isEmpty()) {
                            EmptySection("Nessun segreto presente")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(secrets) { secret ->
                                    SimpleListItem(
                                        title = secret.title,
                                        subtitle = secret.content.take(80),
                                        icon = Icons.Default.VisibilityOff,
                                        iconTint = Color(0xFF607D8B)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Chiudi") }
        },
        dismissButton = null
    )
}

@Composable
private fun EmptySection(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
    }
}

@Composable
private fun SimpleListItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A3E)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
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
    isMaster: Boolean = false,
    onDismiss: () -> Unit,
    onToggleMinimize: () -> Unit
) {
    val context = LocalContext.current

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
                val bitmap = remember(file.data) {
                    renderPdfFirstPage(file.data)
                }
                if (bitmap != null) {
                    AsyncImage(
                        model = bitmap,
                        contentDescription = file.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(file.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Text("Impossibile visualizzare il PDF", color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }
            file.mimeType.startsWith("video/") -> {
                val tempFile = remember(file.data) {
                    try {
                        val tmp = java.io.File.createTempFile("video_", "_${file.name}", context.cacheDir)
                        tmp.writeBytes(file.data)
                        tmp.deleteOnExit()
                        tmp
                    } catch (_: Exception) { null }
                }
                if (tempFile != null) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(Uri.fromFile(tempFile))
                                setMediaController(MediaController(ctx).apply { setAnchorView(this@apply) })
                                setOnPreparedListener { it.isLooping = true; start() }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(file.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.InsertDriveFile, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(file.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text("Formato non visualizzabile direttamente", color = Color.White.copy(alpha = 0.5f))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isMaster) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Chiudi presentazione", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onToggleMinimize) {
                Icon(Icons.Default.FullscreenExit, contentDescription = "Riduci", tint = Color.White)
            }
        }
    }
}

private fun renderPdfFirstPage(data: ByteArray): Bitmap? {
    return try {
        val tmpFile = java.io.File.createTempFile("pdf_preview", ".pdf")
        tmpFile.writeBytes(data)
        val pfd = ParcelFileDescriptor.open(tmpFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(pfd)
        if (renderer.pageCount > 0) {
            val page = renderer.openPage(0)
            val scale = 2f
            val bitmap = Bitmap.createBitmap((page.width * scale).toInt(), (page.height * scale).toInt(), Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(android.graphics.Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            renderer.close()
            pfd.close()
            tmpFile.delete()
            bitmap
        } else {
            renderer.close()
            pfd.close()
            tmpFile.delete()
            null
        }
    } catch (_: Exception) { null }
}
