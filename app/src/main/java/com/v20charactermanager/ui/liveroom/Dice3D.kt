package com.v20charactermanager.ui.liveroom

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.v20charactermanager.domain.model.LiveRoomMessage
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    fun dot(o: Vec3) = x * o.x + y * o.y + z * o.z
    fun cross(o: Vec3) = Vec3(
        y * o.z - z * o.y,
        z * o.x - x * o.z,
        x * o.y - y * o.x
    )

    fun normalized(): Vec3 {
        val len = sqrt(x * x + y * y + z * z)
        return if (len < 1e-6f) Vec3(0f, 0f, 1f) else Vec3(x / len, y / len, z / len)
    }
}

private data class Quat(val x: Float, val y: Float, val z: Float, val w: Float) {
    fun mul(o: Quat) = Quat(
        w * o.x + x * o.w + y * o.z - z * o.y,
        w * o.y - x * o.z + y * o.w + z * o.x,
        w * o.z + x * o.y - y * o.x + z * o.w,
        w * o.w - x * o.x - y * o.y - z * o.z
    )

    fun normalized(): Quat {
        val len = sqrt(x * x + y * y + z * z + w * w)
        return if (len < 1e-6f) Quat(0f, 0f, 0f, 1f) else Quat(x / len, y / len, z / len, w / len)
    }

    private fun conjugate() = Quat(-x, -y, -z, w)

    fun rotate(v: Vec3): Vec3 {
        val res = mul(Quat(v.x, v.y, v.z, 0f)).mul(conjugate())
        return Vec3(res.x, res.y, res.z)
    }
}

private fun axisAngle(axis: Vec3, angle: Float): Quat {
    val a = axis.normalized()
    val h = angle * 0.5f
    val s = sin(h)
    return Quat(a.x * s, a.y * s, a.z * s, cos(h))
}

private fun slerp(a: Quat, bIn: Quat, t: Float): Quat {
    var b = bIn
    var dot = a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w
    if (dot < 0f) {
        b = Quat(-b.x, -b.y, -b.z, -b.w)
        dot = -dot
    }
    if (dot > 0.9995f) {
        return Quat(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t,
            a.z + (b.z - a.z) * t,
            a.w + (b.w - a.w) * t
        ).normalized()
    }
    val theta = acos(dot.coerceIn(-1f, 1f))
    val sinTheta = sin(theta)
    val wa = sin((1f - t) * theta) / sinTheta
    val wb = sin(t * theta) / sinTheta
    return Quat(
        a.x * wa + b.x * wb,
        a.y * wa + b.y * wb,
        a.z * wa + b.z * wb,
        a.w * wa + b.w * wb
    )
}

private fun alignToCamera(n: Vec3): Quat {
    val d = n.dot(Vec3(0f, 0f, 1f)).coerceIn(-1f, 1f)
    return when {
        d > 0.9999f -> Quat(0f, 0f, 0f, 1f)
        d < -0.9999f -> axisAngle(Vec3(0f, 1f, 0f), PI.toFloat())
        else -> axisAngle(n.cross(Vec3(0f, 0f, 1f)), acos(d))
    }
}

private data class DieFace(val indices: IntArray, val normal: Vec3, val value: Int)

private class Die10(val vertices: FloatArray, val faces: List<DieFace>) {
    fun faceFor(value: Int) = faces[(value - 1).coerceIn(0, faces.size - 1)]
}

private val die10: Die10 by lazy {
    val ringR = 0.88f
    val ringY = 0.30f
    val poleY = 1.0f
    val verts = FloatArray(12 * 3)
    verts[0] = 0f; verts[1] = poleY; verts[2] = 0f
    verts[3] = 0f; verts[4] = -poleY; verts[5] = 0f
    for (i in 0 until 5) {
        val a = Math.toRadians((i * 72).toDouble())
        verts[(2 + i) * 3] = (cos(a) * ringR).toFloat()
        verts[(2 + i) * 3 + 1] = ringY
        verts[(2 + i) * 3 + 2] = (sin(a) * ringR).toFloat()
        val b = Math.toRadians((i * 72 + 36).toDouble())
        verts[(7 + i) * 3] = (cos(b) * ringR).toFloat()
        verts[(7 + i) * 3 + 1] = -ringY
        verts[(7 + i) * 3 + 2] = (sin(b) * ringR).toFloat()
    }
    fun vAt(idx: Int) = Vec3(verts[idx * 3], verts[idx * 3 + 1], verts[idx * 3 + 2])
    fun makeFace(idx: IntArray, value: Int): DieFace {
        val a = vAt(idx[0]); val b = vAt(idx[1]); val c = vAt(idx[2])
        var n = (b - a).cross(c - a).normalized()
        val centroid = Vec3(
            idx.map { vAt(it).x }.average().toFloat(),
            idx.map { vAt(it).y }.average().toFloat(),
            idx.map { vAt(it).z }.average().toFloat()
        )
        if (n.dot(centroid) < 0f) n = Vec3(-n.x, -n.y, -n.z)
        return DieFace(idx, n, value)
    }
    val faces = buildList {
        for (i in 0 until 5) {
            add(makeFace(intArrayOf(0, 2 + i, 7 + i, 2 + ((i + 1) % 5)), i + 1))
            add(makeFace(intArrayOf(1, 7 + i, 2 + ((i + 1) % 5), 7 + ((i + 1) % 5)), i + 6))
        }
    }
    Die10(verts, faces)
}

private val lightDir = Vec3(-0.35f, 0.72f, 0.60f).normalized()

private fun DrawScope.drawDie(
    center: Offset,
    radiusPx: Float,
    q: Quat,
    value: Int,
    difficulty: Int,
    settle: Float,
    alpha: Float,
    numberPaint: Paint
) {
    val g = die10
    val camDist = 3.6f
    val focal = 2.4f
    val vertCount = g.vertices.size / 3
    val rot = FloatArray(g.vertices.size)
    for (vi in 0 until vertCount) {
        val r = q.rotate(Vec3(g.vertices[vi * 3], g.vertices[vi * 3 + 1], g.vertices[vi * 3 + 2]))
        rot[vi * 3] = r.x
        rot[vi * 3 + 1] = r.y
        rot[vi * 3 + 2] = r.z
    }
    val tintTarget = when {
        value == 1 -> Color(0xFFCF6679)
        value >= difficulty -> Color(0xFF4CAF50)
        else -> Color(0xFF3A3226)
    }
    val strokeColor = lerp(Color(0xFF3A3226), tintTarget, settle)
    val order = g.faces.indices.sortedBy { fi ->
        g.faces[fi].indices.map { rot[it * 3 + 2] }.average().toFloat()
    }
    for (fi in order) {
        val face = g.faces[fi]
        val n = q.rotate(face.normal)
        if (n.z <= 0.02f) continue
        val path = Path()
        var centroidX = 0f
        var centroidY = 0f
        var centroidZ = 0f
        face.indices.forEachIndexed { k, vi ->
            val z = rot[vi * 3 + 2]
            val proj = focal / (camDist - z)
            val sx = center.x + rot[vi * 3] * proj * radiusPx
            val sy = center.y - rot[vi * 3 + 1] * proj * radiusPx
            if (k == 0) path.moveTo(sx, sy) else path.lineTo(sx, sy)
            centroidX += rot[vi * 3]
            centroidY += rot[vi * 3 + 1]
            centroidZ += z
        }
        path.close()
        val lam = n.dot(lightDir).coerceIn(0f, 1f)
        val shade = 0.45f + 0.55f * lam
        val base = Color(0xFFF4ECDC)
        drawPath(
            path = path,
            color = Color(base.red * shade, base.green * shade, base.blue * shade, alpha),
            style = Fill
        )
        drawPath(
            path = path,
            color = strokeColor.copy(alpha = alpha * 0.9f),
            style = Stroke(width = radiusPx * 0.05f)
        )
        if (n.z > 0.32f) {
            val cx = center.x + (centroidX / face.indices.size) * (focal / (camDist - centroidZ / face.indices.size)) * radiusPx
            val cy = center.y - (centroidY / face.indices.size) * (focal / (camDist - centroidZ / face.indices.size)) * radiusPx
            numberPaint.textSize = radiusPx * 0.55f
            numberPaint.color = android.graphics.Color.argb(
                (alpha * 255f).toInt().coerceIn(0, 255),
                if (face.value == 1) 179 else 26,
                if (face.value == 1) 38 else 22,
                if (face.value == 1) 30 else 18
            )
            val fm = numberPaint.fontMetrics
            val baseline = cy - (fm.ascent + fm.descent) / 2f
            drawContext.canvas.nativeCanvas.drawText(face.value.toString(), cx, baseline, numberPaint)
        }
    }
}

@Composable
fun Dice3DOverlay(
    roll: LiveRoomMessage.DiceRoll,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    val dice = roll.dice
    if (dice.isEmpty()) return
    val numberPaint = remember(roll) {
        Paint().apply {
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD
            )
            setShadowLayer(4f, 0f, 2f, 0x66000000)
        }
    }
    val progress = remember(roll) { Animatable(0f) }
    val fade = remember(roll) { Animatable(1f) }
    val transforms = remember(roll) {
        val seedBase = roll.playerName.hashCode().toLong() * 31L +
            dice.fold(0L) { acc, d -> acc * 31L + d }
        dice.mapIndexed { i, value ->
            val rng = Random(seedBase + i * 977L)
            val axis = Vec3(
                rng.nextFloat() * 2f - 1f,
                rng.nextFloat() * 2f - 1f,
                rng.nextFloat() * 2f - 1f
            )
            val axisSafe = if (abs(axis.x) + abs(axis.y) + abs(axis.z) < 0.05f) {
                Vec3(0.7f, 0.3f, 0.9f)
            } else {
                axis
            }
            val spins = (4.0 + rng.nextDouble() * 5.0) * PI
            val start = axisAngle(axisSafe, spins.toFloat())
            val end = alignToCamera(die10.faceFor(value).normal)
            start to end
        }
    }
    LaunchedEffect(roll) {
        progress.animateTo(1f, tween(durationMillis = 1100, easing = FastOutSlowInEasing))
        delay(700)
        fade.animateTo(0f, tween(durationMillis = 320))
        onFinished()
    }
    Canvas(modifier = modifier) {
        val n = dice.size
        val cols = ceil(sqrt(n.toDouble())).toInt().coerceIn(1, 5)
        val rows = (n + cols - 1) / cols
        val cellW = size.width / cols
        val cellH = size.height / rows
        val cell = min(cellW, cellH)
        val radiusPx = cell * 0.34f
        val t = progress.value
        val alpha = fade.value * (t / 0.10f).coerceIn(0f, 1f)
        val settle = ((t - 0.90f) / 0.10f).coerceIn(0f, 1f)
        for (i in 0 until n) {
            val col = i % cols
            val row = i / cols
            val cx = size.width / 2f + (col - (cols - 1) / 2f) * cellW
            val cy = size.height / 2f + (row - (rows - 1) / 2f) * cellH
            val (startQ, endQ) = transforms[i]
            val q = slerp(startQ, endQ, t)
            drawOval(
                color = Color.Black.copy(alpha = 0.38f * alpha),
                topLeft = Offset(cx - radiusPx * 0.5f, cy + radiusPx * 0.62f),
                size = Size(radiusPx, radiusPx * 0.3f)
            )
            drawDie(
                center = Offset(cx, cy),
                radiusPx = radiusPx,
                q = q,
                value = dice[i],
                difficulty = roll.difficulty,
                settle = settle,
                alpha = alpha,
                numberPaint = numberPaint
            )
        }
    }
}
