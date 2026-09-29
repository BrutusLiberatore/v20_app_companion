package com.v20charactermanager.ui.dice

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v20charactermanager.ui.liveroom.Dice3DCanvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Full-screen cinematic dice reveal: dark scrim, tumbling 3D dice, then the
 * verdict slams in (red flash on botch, gold on total success).
 * Auto-dismisses after ~3.3s; tap to skip.
 */
@Composable
fun DiceRevealOverlay(
    data: DiceRevealData,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    if (data.dice.isEmpty()) return
    val scrimIn = remember(data) { Animatable(0f) }
    val verdictIn = remember(data) { Animatable(0f) }
    val flash = remember(data) { Animatable(0f) }
    val outro = remember(data) { Animatable(1f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(data) {
        scrimIn.animateTo(1f, tween(200))
        // Dice tumble for 1100ms; the verdict lands just after they settle
        delay(950)
        verdictIn.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        if (data.isBotch) {
            flash.animateTo(0.5f, tween(90))
            flash.animateTo(0.14f, tween(480))
        }
        delay(1600)
        outro.animateTo(0f, tween(220))
        onFinished()
    }

    val chromeAlpha = scrimIn.value * outro.value
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectTapGestures {
                    scope.launch {
                        outro.animateTo(0f, tween(140))
                        onFinished()
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Black.copy(alpha = 0.75f * chromeAlpha))
            if (flash.value > 0f) {
                drawRect(Color(0xFF8B1A1A).copy(alpha = flash.value * outro.value))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = data.playerName,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer { alpha = chromeAlpha }
            )
            if (data.label.isNotBlank()) {
                Text(
                    text = data.label,
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { alpha = chromeAlpha }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Dice3DCanvas(
                dice = data.dice,
                difficulty = data.difficulty,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            Text(
                text = data.verdict,
                color = when {
                    data.isBotch -> Color(0xFFFF6B6B)
                    data.isCritical -> Color(0xFFC9A54E)
                    else -> Color.White
                },
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = verdictIn.value * outro.value
                    val scale = 0.6f + 0.4f * verdictIn.value
                    scaleX = scale
                    scaleY = scale
                }
            )
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
