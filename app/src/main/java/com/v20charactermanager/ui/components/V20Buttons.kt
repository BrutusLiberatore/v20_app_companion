package com.v20charactermanager.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v20charactermanager.ui.theme.*

// ═══════════════════════════════════════════════════════════
// BLOOD BUTTON — Primary, burgundy dark with gothic points
// ═══════════════════════════════════════════════════════════
@Composable
fun V20BloodButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 50.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borderColor by animateColorAsState(
        if (isPressed) BloodBtnBorderGlow else BloodBtnBorder,
        label = "bloodBorder"
    )
    val bgColor = if (enabled) BloodBtnBg else BloodBtnBg.copy(alpha = 0.4f)
    val textColor = if (enabled) BloodBtnText else BloodBtnText.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(BloodBtnBgTop, bgColor, BloodBtnBgBot)
                )
            )
            .border(1.2.dp, borderColor, RoundedCornerShape(4.dp))
            .drawBehind {
                // Gothic points at left and right edges of border
                val pointSize = 6.dp.toPx()
                val centerY = size.height / 2
                // Left point
                drawLine(
                    color = borderColor,
                    start = Offset(0f, centerY - pointSize),
                    end = Offset(-pointSize / 2, centerY),
                    strokeWidth = 1.2.dp.toPx()
                )
                drawLine(
                    color = borderColor,
                    start = Offset(-pointSize / 2, centerY),
                    end = Offset(0f, centerY + pointSize),
                    strokeWidth = 1.2.dp.toPx()
                )
                // Right point
                drawLine(
                    color = borderColor,
                    start = Offset(size.width, centerY - pointSize),
                    end = Offset(size.width + pointSize / 2, centerY),
                    strokeWidth = 1.2.dp.toPx()
                )
                drawLine(
                    color = borderColor,
                    start = Offset(size.width + pointSize / 2, centerY),
                    end = Offset(size.width, centerY + pointSize),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            ),
            color = textColor
        )
    }
}

// ═══════════════════════════════════════════════════════════
// IVORY OUTLINE — Secondary, worn ivory border
// ═══════════════════════════════════════════════════════════
@Composable
fun V20IvoryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 50.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borderColor by animateColorAsState(
        if (isPressed) IvoryBtnBorderPressed else IvoryBtnBorder,
        label = "ivoryBorder"
    )

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Transparent)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                letterSpacing = 1.5.sp
            ),
            color = if (enabled) IvoryBtnText else IvoryBtnText.copy(alpha = 0.3f)
        )
    }
}

// ═══════════════════════════════════════════════════════════
// ICON BUTTON — Square/circular, almost black, icon only
// ═══════════════════════════════════════════════════════════
@Composable
fun V20IconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    size: Dp = 44.dp,
    enabled: Boolean = true
) {
    val iconColor = when {
        !enabled -> IconBtnIcon.copy(alpha = 0.3f)
        active -> IconBtnIconActive
        else -> IconBtnIcon
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(IconBtnBg)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size((size.value * 0.5f).dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════
// +/- CONTROL — Small square, metallic border
// ═══════════════════════════════════════════════════════════
@Composable
fun V20ControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPlus: Boolean = true,
    enabled: Boolean = true,
    accentColor: Color = ControlBtnPlus
) {
    val bgColor = if (enabled) ControlBtnBg else ControlBtnDisabled
    val borderColor = if (enabled) ControlBtnBorder else ControlBtnBorder.copy(alpha = 0.3f)
    val iconColor = when {
        !enabled -> V20InkFaint
        isPlus && enabled -> accentColor
        else -> V20InkDim
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(0.8.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════
// FAB — Gothic drop medallion
// ═══════════════════════════════════════════════════════════
@Composable
fun V20GothicFab(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1010), FabBg, Color(0xFF080404))
                )
            )
            .border(1.5.dp, FabBorder, CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = FabIcon,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════
// DICE BUTTON — Dramatic, gothic frame, d10 icon
// ═══════════════════════════════════════════════════════════
@Composable
fun V20DiceButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borderColor by animateColorAsState(
        if (isPressed) DiceBtnGlow else DiceBtnBorder,
        label = "diceBorder"
    )

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2A0E0E), DiceBtnBg, Color(0xFF0A0404))
                )
            )
            .border(1.5.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 3.sp
            ),
            color = DiceBtnIcon
        )
    }
}

// ═══════════════════════════════════════════════════════════
// PROGRESS INDICATOR — Red line with diamonds
// ═══════════════════════════════════════════════════════════
@Composable
fun V20ProgressLine(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val stepNumber = index + 1
            val isActive = stepNumber <= currentStep
            val isCurrent = stepNumber == currentStep
            val color = when {
                isCurrent -> ProgressDiamondActive
                isActive -> ProgressDiamond
                else -> V20InkFaint
            }
            val size = if (isCurrent) 8.dp else 6.dp

            Box(
                modifier = Modifier
                    .size(size)
                    .drawBehind {
                        // Draw diamond shape
                        val cx = size.toPx() / 2
                        val cy = size.toPx() / 2
                        val r = size.toPx() / 2
                        drawLine(color, Offset(cx, cy - r), Offset(cx + r, cy), strokeWidth = 1.5.dp.toPx())
                        drawLine(color, Offset(cx + r, cy), Offset(cx, cy + r), strokeWidth = 1.5.dp.toPx())
                        drawLine(color, Offset(cx, cy + r), Offset(cx - r, cy), strokeWidth = 1.5.dp.toPx())
                        drawLine(color, Offset(cx - r, cy), Offset(cx, cy - r), strokeWidth = 1.5.dp.toPx())
                        if (isCurrent) {
                            drawCircle(color.copy(alpha = 0.3f), radius = r * 1.5f)
                        }
                    }
            )
        }
    }
}
