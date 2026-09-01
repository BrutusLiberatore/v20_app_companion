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
// CRIMSON SELECTION — For clan, sect, nature, categories
// ═══════════════════════════════════════════════════════════
@Composable
fun V20SelectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp
) {
    val borderColor = if (selected) SelectBtnBorderActive else SelectBtnBorder

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(V20Surface2)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                ),
                color = if (selected) V20Ink else V20InkDim
            )
        }
        // Red diamond at bottom center when selected
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
                    .size(6.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(SelectBtnDiamond)
                    .drawBehind {
                        // Draw diamond shape
                        drawLine(
                            color = SelectBtnDiamond,
                            start = Offset(size.width / 2, 0f),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 1.5.dp.toPx()
                        )
                        drawLine(
                            color = SelectBtnDiamond,
                            start = Offset(size.width, size.height / 2),
                            end = Offset(size.width / 2, size.height),
                            strokeWidth = 1.5.dp.toPx()
                        )
                        drawLine(
                            color = SelectBtnDiamond,
                            start = Offset(size.width / 2, size.height),
                            end = Offset(0f, size.height / 2),
                            strokeWidth = 1.5.dp.toPx()
                        )
                        drawLine(
                            color = SelectBtnDiamond,
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width / 2, 0f),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
            )
        }
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
// DESTRUCTIVE BUTTON — Black, dark red border, pale red text
// ═══════════════════════════════════════════════════════════
@Composable
fun V20DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bgColor by animateColorAsState(
        if (isPressed) DestructBtnPressed else DestructBtnBg,
        label = "destructBg"
    )
    val borderColor by animateColorAsState(
        if (isPressed) V20ErrorBright else DestructBtnBorder,
        label = "destructBorder"
    )
    val textColor by animateColorAsState(
        if (isPressed) Color(0xFFFFCCCC) else DestructBtnText,
        label = "destructText"
    )

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
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
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            ),
            color = textColor
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
            val isActive = index <= currentStep
            val isCurrent = index == currentStep
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

// ═══════════════════════════════════════════════════════════
// WIZARD BUTTONS — Back (secondary) + Continue (primary)
// ═══════════════════════════════════════════════════════════
@Composable
fun V20WizardButtons(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    backText: String,
    continueText: String,
    continueEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        V20IvoryButton(
            text = backText,
            onClick = onBack,
            modifier = Modifier.weight(1f),
            height = 48.dp
        )
        V20BloodButton(
            text = continueText,
            onClick = onContinue,
            modifier = Modifier.weight(1f),
            enabled = continueEnabled,
            height = 48.dp
        )
    }
}
