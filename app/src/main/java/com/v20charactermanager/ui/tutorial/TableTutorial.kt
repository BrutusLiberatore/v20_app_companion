package com.v20charactermanager.ui.tutorial

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.v20charactermanager.R
import com.v20charactermanager.ui.theme.V20BloodBg
import com.v20charactermanager.ui.theme.V20SurfaceBlood2
import kotlin.math.roundToInt

private const val TABLE_TUTORIAL_FILE = "v20_table_tutorial"
private const val TABLE_TUTORIAL_KEY = "table_tutorial_done"

object TableTutorialPrefs {
    fun isDone(context: Context): Boolean =
        context.getSharedPreferences(TABLE_TUTORIAL_FILE, Context.MODE_PRIVATE)
            .getBoolean(TABLE_TUTORIAL_KEY, false)

    fun setDone(context: Context) {
        context.getSharedPreferences(TABLE_TUTORIAL_FILE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(TABLE_TUTORIAL_KEY, true)
            .apply()
    }
}

object TableTutorialState {
    var visible by mutableStateOf(false)
}

object TableTutorialAnchors {
    const val IP = "tt_ip"
    const val HEADER = "tt_header"
    const val PRESENT = "tt_present"
    const val MENU = "tt_menu"
    const val FEED = "tt_feed"

    val bounds = mutableStateMapOf<String, Rect>()

    fun clear() {
        bounds.clear()
    }
}

fun Modifier.tableTutorialAnchor(key: String): Modifier =
    onGloballyPositioned { coords ->
        TableTutorialAnchors.bounds[key] = coords.boundsInRoot()
    }

private data class TableTutorialStep(
    val anchorKey: String?,
    val titleRes: Int,
    val bodyRes: Int
)

private val tableTutorialSteps = listOf(
    TableTutorialStep(
        TableTutorialAnchors.IP,
        R.string.tt_master_welcome_title,
        R.string.tt_master_welcome_body
    ),
    TableTutorialStep(
        TableTutorialAnchors.HEADER,
        R.string.tt_master_players_title,
        R.string.tt_master_players_body
    ),
    TableTutorialStep(
        TableTutorialAnchors.PRESENT,
        R.string.tt_master_present_title,
        R.string.tt_master_present_body
    ),
    TableTutorialStep(
        TableTutorialAnchors.PRESENT,
        R.string.tt_master_share_title,
        R.string.tt_master_share_body
    ),
    TableTutorialStep(
        TableTutorialAnchors.MENU,
        R.string.tt_master_combat_title,
        R.string.tt_master_combat_body
    ),
    TableTutorialStep(
        TableTutorialAnchors.MENU,
        R.string.tt_master_dice_title,
        R.string.tt_master_dice_body
    ),
    TableTutorialStep(
        TableTutorialAnchors.FEED,
        R.string.tt_master_feed_title,
        R.string.tt_master_feed_body
    )
)

private val TutorialGold = Color(0xFFD4A847)

@Composable
fun TableTutorialOverlay() {
    val context = LocalContext.current
    val density = LocalDensity.current
    var index by remember { mutableStateOf(0) }
    val step = tableTutorialSteps[index]
    val isLast = index == tableTutorialSteps.lastIndex

    var overlayPos by remember { mutableStateOf(Offset.Zero) }
    var overlaySize by remember { mutableStateOf(IntSize.Zero) }
    var cardSize by remember { mutableStateOf(IntSize.Zero) }

    val finish = {
        TableTutorialPrefs.setDone(context)
        TableTutorialState.visible = false
    }

    BackHandler { finish() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(20f)
            .onGloballyPositioned {
                overlayPos = it.positionInRoot()
                overlaySize = it.size
            }
    ) {
        if (overlaySize.width > 0 && overlaySize.height > 0) {
            val pad = with(density) { 10.dp.toPx() }
            val anchorRoot = step.anchorKey?.let { TableTutorialAnchors.bounds[it] }
            val hole: Rect? = anchorRoot?.let { r ->
                val raw = Rect(
                    r.left - overlayPos.x - pad,
                    r.top - overlayPos.y - pad,
                    r.right - overlayPos.x + pad,
                    r.bottom - overlayPos.y + pad
                )
                val clamped = Rect(
                    raw.left.coerceAtLeast(0f),
                    raw.top.coerceAtLeast(0f),
                    raw.right.coerceAtMost(overlaySize.width.toFloat()),
                    raw.bottom.coerceAtMost(overlaySize.height.toFloat())
                )
                val minSize = with(density) { 48.dp.toPx() }
                if (clamped.width >= minSize && clamped.height >= minSize) clamped else null
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .pointerInput(Unit) { detectTapGestures { } }
            ) {
                drawRect(color = Color.Black.copy(alpha = 0.78f))
                if (hole != null) {
                    val corner = CornerRadius(16.dp.toPx())
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = hole.topLeft,
                        size = Size(hole.width, hole.height),
                        cornerRadius = corner,
                        blendMode = BlendMode.Clear
                    )
                    drawRoundRect(
                        color = TutorialGold,
                        topLeft = hole.topLeft,
                        size = Size(hole.width, hole.height),
                        cornerRadius = corner,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            val cardModifier = if (hole != null) {
                Modifier
                    .widthIn(max = 340.dp)
                    .offset {
                        val m = 16.dp.roundToPx()
                        val gap = 20.dp.roundToPx()
                        val holeCenterX = hole.left.roundToInt() + hole.width.roundToInt() / 2
                        val belowY = hole.bottom.roundToInt() + gap
                        val aboveY = hole.top.roundToInt() - cardSize.height - gap
                        val y = when {
                            cardSize.height > 0 &&
                                belowY + cardSize.height <= overlaySize.height - m -> belowY
                            cardSize.height > 0 && aboveY >= m -> aboveY
                            else -> (overlaySize.height - cardSize.height) / 2
                        }
                        val maxX = (overlaySize.width - cardSize.width - m).coerceAtLeast(m)
                        val x = (holeCenterX - cardSize.width / 2).coerceIn(m, maxX)
                        IntOffset(x, y)
                    }
                    .onSizeChanged { cardSize = it }
            } else {
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .widthIn(max = 340.dp)
                    .onSizeChanged { cardSize = it }
            }

            Card(
                modifier = cardModifier,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = V20SurfaceBlood2)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(step.titleRes),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TutorialGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(step.bodyRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tableTutorialSteps.forEachIndexed { i, _ ->
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(if (i == index) 9.dp else 7.dp)
                                    .background(
                                        color = if (i == index) {
                                            TutorialGold
                                        } else {
                                            Color.White.copy(alpha = 0.3f)
                                        },
                                        shape = CircleShape
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = finish) {
                            Text(
                                text = stringResource(R.string.tutorial_skip),
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = { if (isLast) finish() else index++ },
                            colors = ButtonDefaults.buttonColors(containerColor = TutorialGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    if (isLast) R.string.table_tutorial_done_btn
                                    else R.string.tutorial_next
                                ),
                                color = V20BloodBg,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
