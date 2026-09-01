package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.v20charactermanager.domain.model.*
import kotlin.math.atan2
import kotlin.math.sqrt

enum class DrawTool {
    PEN, HIGHLIGHTER, LINE, ARROW, CIRCLE, RECTANGLE, TEXT, PIN, ERASER
}

data class DrawToolState(
    val tool: DrawTool = DrawTool.PEN,
    val color: Long = 0xFFFF0000,
    val strokeWidth: Float = 3f,
    val opacity: Float = 1f
)

class CanvasDrawState {
    val currentPoints: MutableList<Offset> = mutableListOf()
    var startPoint: Offset? = null
    var currentEnd: Offset? = null
}

fun AnnotationType.toDrawTool(): DrawTool = when (this) {
    AnnotationType.PEN_STROKE -> DrawTool.PEN
    AnnotationType.HIGHLIGHTER -> DrawTool.HIGHLIGHTER
    AnnotationType.LINE -> DrawTool.LINE
    AnnotationType.ARROW -> DrawTool.ARROW
    AnnotationType.CIRCLE -> DrawTool.CIRCLE
    AnnotationType.RECTANGLE -> DrawTool.RECTANGLE
    AnnotationType.TEXT -> DrawTool.TEXT
    AnnotationType.PIN -> DrawTool.PIN
    AnnotationType.ERASER -> DrawTool.ERASER
}

fun DrawTool.toAnnotationType(): AnnotationType = when (this) {
    DrawTool.PEN -> AnnotationType.PEN_STROKE
    DrawTool.HIGHLIGHTER -> AnnotationType.HIGHLIGHTER
    DrawTool.LINE -> AnnotationType.LINE
    DrawTool.ARROW -> AnnotationType.ARROW
    DrawTool.CIRCLE -> AnnotationType.CIRCLE
    DrawTool.RECTANGLE -> AnnotationType.RECTANGLE
    DrawTool.TEXT -> AnnotationType.TEXT
    DrawTool.PIN -> AnnotationType.PIN
    DrawTool.ERASER -> AnnotationType.ERASER
}

fun normalizedToCanvas(point: NormalizedPoint, canvasWidth: Float, canvasHeight: Float): Offset {
    return Offset(point.x * canvasWidth, point.y * canvasHeight)
}

fun canvasToNormalized(offset: Offset, canvasWidth: Float, canvasHeight: Float): NormalizedPoint {
    return NormalizedPoint(
        x = (offset.x / canvasWidth).coerceIn(0f, 1f),
        y = (offset.y / canvasHeight).coerceIn(0f, 1f)
    )
}

private fun DrawScope.drawAnnotation(annotation: ImageAnnotation, canvasWidth: Float, canvasHeight: Float) {
    val style = annotation.style
    val color = Color(style.strokeColor)
    val paintOpacity = style.opacity

    when (annotation.type) {
        AnnotationType.PEN_STROKE, AnnotationType.HIGHLIGHTER -> {
            val points = annotation.geometry.points.map { normalizedToCanvas(it, canvasWidth, canvasHeight) }
            if (points.size < 2) return
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            val alpha = if (annotation.type == AnnotationType.HIGHLIGHTER) 0.4f else paintOpacity
            drawPath(
                path = path,
                color = color.copy(alpha = alpha),
                style = Stroke(
                    width = style.strokeWidth * (canvasWidth / 1000f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        AnnotationType.LINE -> {
            val start = annotation.geometry.startPoint?.let { normalizedToCanvas(it, canvasWidth, canvasHeight) } ?: return
            val end = annotation.geometry.endPoint?.let { normalizedToCanvas(it, canvasWidth, canvasHeight) } ?: return
            drawLine(
                color = color.copy(alpha = paintOpacity),
                start = start,
                end = end,
                strokeWidth = style.strokeWidth * (canvasWidth / 1000f),
                cap = StrokeCap.Round
            )
        }
        AnnotationType.ARROW -> {
            val start = annotation.geometry.startPoint?.let { normalizedToCanvas(it, canvasWidth, canvasHeight) } ?: return
            val end = annotation.geometry.endPoint?.let { normalizedToCanvas(it, canvasWidth, canvasHeight) } ?: return
            val sw = style.strokeWidth * (canvasWidth / 1000f)
            drawLine(color = color.copy(alpha = paintOpacity), start = start, end = end, strokeWidth = sw, cap = StrokeCap.Round)
            val angle = atan2(end.y - start.y, end.x - start.x)
            val arrowLen = sw * 4f
            val p1 = Offset(end.x - arrowLen * kotlin.math.cos(angle - 0.4f), end.y - arrowLen * kotlin.math.sin(angle - 0.4f))
            val p2 = Offset(end.x - arrowLen * kotlin.math.cos(angle + 0.4f), end.y - arrowLen * kotlin.math.sin(angle + 0.4f))
            drawLine(color = color.copy(alpha = paintOpacity), start = end, end = p1, strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color = color.copy(alpha = paintOpacity), start = end, end = p2, strokeWidth = sw, cap = StrokeCap.Round)
        }
        AnnotationType.CIRCLE -> {
            val center = annotation.geometry.center?.let { normalizedToCanvas(it, canvasWidth, canvasHeight) } ?: return
            val rx = annotation.geometry.radiusX * canvasWidth
            val ry = annotation.geometry.radiusY * canvasHeight
            drawOval(
                color = color.copy(alpha = paintOpacity),
                topLeft = Offset(center.x - rx, center.y - ry),
                size = Size(rx * 2, ry * 2),
                style = Stroke(width = style.strokeWidth * (canvasWidth / 1000f))
            )
        }
        AnnotationType.RECTANGLE -> {
            val pos = annotation.geometry.position?.let { normalizedToCanvas(it, canvasWidth, canvasHeight) } ?: return
            val w = annotation.geometry.width * canvasWidth
            val h = annotation.geometry.height * canvasHeight
            drawRect(
                color = color.copy(alpha = paintOpacity),
                topLeft = pos,
                size = Size(w, h),
                style = Stroke(width = style.strokeWidth * (canvasWidth / 1000f))
            )
        }
        AnnotationType.TEXT -> {
            val pos = annotation.geometry.position?.let { normalizedToCanvas(it, canvasWidth, canvasHeight) } ?: return
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    this.color = style.fontColor.toInt()
                    textSize = style.fontSize * (canvasWidth / 500f)
                    isAntiAlias = true
                }
                drawText(annotation.text ?: "", pos.x, pos.y, paint)
            }
        }
        AnnotationType.PIN -> {
            val pos = annotation.geometry.position?.let { normalizedToCanvas(it, canvasWidth, canvasHeight) } ?: return
            val pinRadius = 12f * (canvasWidth / 1000f)
            drawCircle(color = Color(0xFFFFD700), radius = pinRadius, center = pos)
            drawCircle(color = Color(0xFF000000), radius = pinRadius * 0.5f, center = pos)
        }
        AnnotationType.ERASER -> {
            val points = annotation.geometry.points.map { normalizedToCanvas(it, canvasWidth, canvasHeight) }
            if (points.size < 2) return
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = style.strokeWidth * (canvasWidth / 300f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Composable
fun AnnotationCanvas(
    annotations: List<ImageAnnotation>,
    toolState: DrawToolState,
    activeLayerId: String?,
    isDrawingEnabled: Boolean,
    modifier: Modifier = Modifier,
    onStrokeComplete: (ImageAnnotation) -> Unit,
    onPinTap: (Offset) -> Unit
) {
    val drawState = remember { CanvasDrawState() }
    var canvasWidth by remember { mutableFloatStateOf(1f) }
    var canvasHeight by remember { mutableFloatStateOf(1f) }
    var isDrawing by remember { mutableStateOf(false) }
    var previewAnnotation by remember { mutableStateOf<ImageAnnotation?>(null) }

    val shapeTools = setOf(DrawTool.LINE, DrawTool.ARROW, DrawTool.CIRCLE, DrawTool.RECTANGLE)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isDrawingEnabled, toolState.tool, activeLayerId) {
                if (!isDrawingEnabled) return@pointerInput
                detectTapGestures(
                    onTap = { offset ->
                        if (toolState.tool == DrawTool.PIN && activeLayerId != null) {
                            onPinTap(offset)
                        }
                        if (toolState.tool == DrawTool.TEXT && activeLayerId != null) {
                            val annotation = ImageAnnotation(
                                id = java.util.UUID.randomUUID().toString(),
                                layerId = activeLayerId,
                                imageDocumentId = "",
                                type = AnnotationType.TEXT,
                                geometry = AnnotationGeometry(
                                    position = canvasToNormalized(offset, canvasWidth, canvasHeight)
                                ),
                                style = AnnotationStyle(
                                    strokeWidth = toolState.strokeWidth,
                                    strokeColor = toolState.color,
                                    fontColor = toolState.color,
                                    opacity = toolState.opacity,
                                    fontSize = 24f
                                ),
                                text = "Note"
                            )
                            onStrokeComplete(annotation)
                        }
                    },
                    onLongPress = { offset ->
                        if (toolState.tool == DrawTool.PIN && activeLayerId != null) {
                            onPinTap(offset)
                        }
                    }
                )
            }
            .pointerInput(isDrawingEnabled, toolState.tool, activeLayerId) {
                if (!isDrawingEnabled) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val down = event.changes.firstOrNull() ?: continue
                        if (!down.pressed) {
                            if (isDrawing && drawState.currentPoints.isNotEmpty()) {
                                if (shapeTools.contains(toolState.tool) && drawState.startPoint != null && drawState.currentEnd != null) {
                                    val annotation = buildShapeAnnotation(
                                        toolState, drawState, canvasWidth, canvasHeight, activeLayerId
                                    )
                                    if (annotation != null) onStrokeComplete(annotation)
                                } else if (toolState.tool == DrawTool.PEN || toolState.tool == DrawTool.HIGHLIGHTER) {
                                    val annotation = buildStrokeAnnotation(
                                        toolState, drawState, canvasWidth, canvasHeight, activeLayerId
                                    )
                                    if (annotation != null) onStrokeComplete(annotation)
                                } else if (toolState.tool == DrawTool.ERASER) {
                                    val annotation = buildEraserAnnotation(
                                        toolState, drawState, canvasWidth, canvasHeight, activeLayerId
                                    )
                                    if (annotation != null) onStrokeComplete(annotation)
                                }
                            }
                            drawState.currentPoints.clear()
                            drawState.startPoint = null
                            drawState.currentEnd = null
                            isDrawing = false
                            previewAnnotation = null
                            continue
                        }
                        val pos = down.position
                        if (!isDrawing) {
                            isDrawing = true
                            drawState.startPoint = pos
                            drawState.currentPoints.add(pos)
                        }
                        drawState.currentEnd = pos
                        if (!shapeTools.contains(toolState.tool)) {
                            drawState.currentPoints.add(pos)
                        }
                        if (isDrawing) {
                            previewAnnotation = when (toolState.tool) {
                                DrawTool.PEN, DrawTool.HIGHLIGHTER -> buildStrokeAnnotation(
                                    toolState, drawState, canvasWidth, canvasHeight, activeLayerId
                                )
                                DrawTool.ERASER -> buildEraserAnnotation(
                                    toolState, drawState, canvasWidth, canvasHeight, activeLayerId
                                )
                                DrawTool.LINE, DrawTool.ARROW, DrawTool.CIRCLE, DrawTool.RECTANGLE -> buildShapeAnnotation(
                                    toolState, drawState, canvasWidth, canvasHeight, activeLayerId
                                )
                                else -> null
                            }
                        }
                    }
                }
            }
    ) {
        canvasWidth = size.width
        canvasHeight = size.height
        annotations.forEach { drawAnnotation(it, size.width, size.height) }
        previewAnnotation?.let { drawAnnotation(it, size.width, size.height) }
    }
}

private fun buildStrokeAnnotation(
    toolState: DrawToolState,
    drawState: CanvasDrawState,
    canvasWidth: Float,
    canvasHeight: Float,
    layerId: String?
): ImageAnnotation? {
    if (drawState.currentPoints.isEmpty() || layerId == null) return null
    val normalizedPoints = drawState.currentPoints.map { canvasToNormalized(it, canvasWidth, canvasHeight) }
    return ImageAnnotation(
        id = java.util.UUID.randomUUID().toString(),
        layerId = layerId,
        imageDocumentId = "",
        type = toolState.tool.toAnnotationType(),
        geometry = AnnotationGeometry(points = normalizedPoints),
        style = AnnotationStyle(
            strokeWidth = toolState.strokeWidth,
            strokeColor = toolState.color,
            opacity = if (toolState.tool == DrawTool.HIGHLIGHTER) 0.4f else toolState.opacity
        )
    )
}

private fun buildEraserAnnotation(
    toolState: DrawToolState,
    drawState: CanvasDrawState,
    canvasWidth: Float,
    canvasHeight: Float,
    layerId: String?
): ImageAnnotation? {
    if (drawState.currentPoints.isEmpty() || layerId == null) return null
    val normalizedPoints = drawState.currentPoints.map { canvasToNormalized(it, canvasWidth, canvasHeight) }
    return ImageAnnotation(
        id = java.util.UUID.randomUUID().toString(),
        layerId = layerId,
        imageDocumentId = "",
        type = AnnotationType.ERASER,
        geometry = AnnotationGeometry(points = normalizedPoints),
        style = AnnotationStyle(
            strokeWidth = toolState.strokeWidth * 3f,
            strokeColor = 0xFF000000,
            opacity = 1f
        )
    )
}

private fun buildShapeAnnotation(
    toolState: DrawToolState,
    drawState: CanvasDrawState,
    canvasWidth: Float,
    canvasHeight: Float,
    layerId: String?
): ImageAnnotation? {
    val start = drawState.startPoint ?: return null
    val end = drawState.currentEnd ?: return null
    if (layerId == null) return null
    val normStart = canvasToNormalized(start, canvasWidth, canvasHeight)
    val normEnd = canvasToNormalized(end, canvasWidth, canvasHeight)

    return when (toolState.tool) {
        DrawTool.LINE -> ImageAnnotation(
            id = java.util.UUID.randomUUID().toString(),
            layerId = layerId, imageDocumentId = "",
            type = AnnotationType.LINE,
            geometry = AnnotationGeometry(startPoint = normStart, endPoint = normEnd),
            style = AnnotationStyle(strokeWidth = toolState.strokeWidth, strokeColor = toolState.color, opacity = toolState.opacity)
        )
        DrawTool.ARROW -> ImageAnnotation(
            id = java.util.UUID.randomUUID().toString(),
            layerId = layerId, imageDocumentId = "",
            type = AnnotationType.ARROW,
            geometry = AnnotationGeometry(startPoint = normStart, endPoint = normEnd),
            style = AnnotationStyle(strokeWidth = toolState.strokeWidth, strokeColor = toolState.color, opacity = toolState.opacity)
        )
        DrawTool.CIRCLE -> {
            val cx = (start.x + end.x) / 2f
            val cy = (start.y + end.y) / 2f
            val rx = kotlin.math.abs(end.x - start.x) / 2f
            val ry = kotlin.math.abs(end.y - start.y) / 2f
            ImageAnnotation(
                id = java.util.UUID.randomUUID().toString(),
                layerId = layerId, imageDocumentId = "",
                type = AnnotationType.CIRCLE,
                geometry = AnnotationGeometry(
                    center = canvasToNormalized(Offset(cx, cy), canvasWidth, canvasHeight),
                    radiusX = rx / canvasWidth,
                    radiusY = ry / canvasHeight
                ),
                style = AnnotationStyle(strokeWidth = toolState.strokeWidth, strokeColor = toolState.color, opacity = toolState.opacity)
            )
        }
        DrawTool.RECTANGLE -> {
            val topLeft = Offset(minOf(start.x, end.x), minOf(start.y, end.y))
            val w = kotlin.math.abs(end.x - start.x)
            val h = kotlin.math.abs(end.y - start.y)
            ImageAnnotation(
                id = java.util.UUID.randomUUID().toString(),
                layerId = layerId, imageDocumentId = "",
                type = AnnotationType.RECTANGLE,
                geometry = AnnotationGeometry(
                    position = canvasToNormalized(topLeft, canvasWidth, canvasHeight),
                    width = w / canvasWidth,
                    height = h / canvasHeight
                ),
                style = AnnotationStyle(strokeWidth = toolState.strokeWidth, strokeColor = toolState.color, opacity = toolState.opacity)
            )
        }
        else -> null
    }
}
