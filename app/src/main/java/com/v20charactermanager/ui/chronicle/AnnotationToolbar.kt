package com.v20charactermanager.ui.chronicle

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.ui.theme.*

private data class ToolInfo(
    val tool: DrawTool,
    val icon: ImageVector,
    val labelRes: Int,
    val descRes: Int
)

private val toolInfos = listOf(
    ToolInfo(DrawTool.PEN, Icons.Default.Edit, R.string.draw_tool_pen, R.string.draw_tool_pen_desc),
    ToolInfo(DrawTool.HIGHLIGHTER, Icons.Default.Highlight, R.string.draw_tool_highlighter, R.string.draw_tool_highlighter_desc),
    ToolInfo(DrawTool.LINE, Icons.Default.HorizontalRule, R.string.draw_tool_line, R.string.draw_tool_line_desc),
    ToolInfo(DrawTool.ARROW, Icons.Default.ArrowForward, R.string.draw_tool_arrow, R.string.draw_tool_arrow_desc),
    ToolInfo(DrawTool.CIRCLE, Icons.Default.Circle, R.string.draw_tool_circle, R.string.draw_tool_circle_desc),
    ToolInfo(DrawTool.RECTANGLE, Icons.Default.Rectangle, R.string.draw_tool_rectangle, R.string.draw_tool_rectangle_desc),
    ToolInfo(DrawTool.TEXT, Icons.Default.TextFields, R.string.draw_tool_text, R.string.draw_tool_text_desc),
    ToolInfo(DrawTool.PIN, Icons.Default.PushPin, R.string.draw_tool_pin, R.string.draw_tool_pin_desc),
    ToolInfo(DrawTool.ERASER, Icons.Default.AutoFixNormal, R.string.draw_tool_eraser, R.string.draw_tool_eraser_desc),
)

private val presetColors = listOf(
    0xFFFF0000L, 0xFFFF6600L, 0xFFFFFF00L, 0xFF00FF00L,
    0xFF0066FFL, 0xFF9933FFL, 0xFFFFFFFFL, 0xFF000000L,
    0xFFFF3366L, 0xFF33CCFFL
)

@Composable
fun AnnotationToolbar(
    toolState: DrawToolState,
    canUndo: Boolean,
    canRedo: Boolean,
    activeLayerId: String?,
    onToolChange: (DrawTool) -> Unit,
    onColorChange: (Long) -> Unit,
    onStrokeWidthChange: (Float) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit,
    onClearLayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showWidthSlider by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(V20Surface2.copy(alpha = 0.95f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            toolInfos.forEach { info ->
                val isSelected = toolState.tool == info.tool
                val bgColor by animateColorAsState(
                    if (isSelected) V20GreenBright else Color.Transparent, label = "toolBg"
                )
                val iconTint by animateColorAsState(
                    if (isSelected) Color.Black else V20InkDim, label = "toolIcon"
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .clickable { onToolChange(info.tool) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = info.icon,
                        contentDescription = stringResource(info.descRes),
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(toolState.color))
                    .border(2.dp, V20InkDim, CircleShape)
                    .clickable { showColorPicker = !showColorPicker }
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(V20Surface)
                    .clickable { showWidthSlider = !showWidthSlider },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${toolState.strokeWidth.toInt()}",
                    color = V20Ink,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = onUndo, enabled = canUndo, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Undo, contentDescription = stringResource(R.string.draw_undo), tint = if (canUndo) V20Ink else V20InkFaint, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onRedo, enabled = canRedo, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Redo, contentDescription = stringResource(R.string.draw_redo), tint = if (canRedo) V20Ink else V20InkFaint, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onClearLayer, enabled = activeLayerId != null, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = stringResource(R.string.draw_clear_layer), tint = if (activeLayerId != null) V20Error else V20InkFaint, modifier = Modifier.size(18.dp))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(V20GoldBright)
                    .clickable(enabled = activeLayerId != null) { onSave() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.draw_save),
                    color = Color.Black,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showColorPicker) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                presetColors.forEach { color ->
                    val isSelected = toolState.color == color
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(color))
                            .then(
                                if (isSelected) Modifier.border(3.dp, V20GoldBright, CircleShape)
                                else Modifier.border(1.dp, V20InkFaint, CircleShape)
                            )
                            .clickable {
                                onColorChange(color)
                                showColorPicker = false
                            }
                    )
                }
            }
        }

        if (showWidthSlider) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("1", color = V20InkDim, style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = toolState.strokeWidth,
                    onValueChange = { onStrokeWidthChange(it) },
                    valueRange = 1f..20f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = V20GreenBright,
                        activeTrackColor = V20GreenBright
                    )
                )
                Text("20", color = V20InkDim, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
