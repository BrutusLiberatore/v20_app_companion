package com.v20charactermanager.ui.liveroom

// D10 mesh, face geometry and marble face textures come from the free asset pack
// "Low Poly 3D Dice Set" by eddex (https://eddex.itch.io/low-poly-3d-dice-set-game-assets),
// licensed under CC BY-SA 4.0. Face albedo tiles are baked from the original UV texture.

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.ui.platform.LocalContext
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.LiveRoomMessage
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.abs
import kotlin.math.atan2
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
            (b.x - a.x) * t + a.x,
            (b.y - a.y) * t + a.y,
            (b.z - a.z) * t + a.z,
            (b.w - a.w) * t + a.w
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

private data class DieFace(
    val corners: FloatArray,
    val normal: Vec3,
    val fracs: FloatArray,
    val up: Vec3,
    val value: Int
)

private class Die10(val faces: List<DieFace>) {
    fun faceFor(value: Int): DieFace = faces.firstOrNull { it.value == value } ?: faces[0]
}

private val die10: Die10 by lazy {
    val data = floatArrayOf(
            // f0 value=10
    0.45695f, 0.62903f, -0.62890f, 0.00522f, 0.98712f, -0.00719f, 0.84410f, 0.10517f, -0.27980f, 0.52371f, -0.10302f, -0.72083f, 0.00527f, 0.10518f, -0.88926f, 0.06140f, 0.82431f, 0.62286f, 0.17569f, 0.93860f, 0.42342f, 0.91928f, 0.82427f, 0.87598f, -0.44101f, 0.19537f,
    // f1 value=7
    0.00001f, -0.62895f, -0.77744f, -0.00000f, 0.10222f, -0.89197f, 0.51843f, -0.10599f, -0.72352f, -0.00000f, -0.98800f, -0.00998f, -0.51843f, -0.10599f, -0.72353f, 0.06140f, 0.50000f, 0.22893f, 0.17571f, 0.93860f, 0.50000f, 0.22893f, 0.82429f, 0.99619f, 0.06777f, -0.05481f,
    // f2 value=3
    0.73938f, -0.62897f, -0.24025f, 0.52755f, -0.10568f, -0.71652f, 0.84796f, 0.10252f, -0.27552f, 0.84797f, -0.10569f, 0.26959f, 0.00912f, -0.98768f, -0.00296f, 0.15455f, 0.39647f, 0.39229f, 0.06141f, 0.80313f, 0.06140f, 0.84545f, 0.93860f, 0.58309f, 0.77658f, -0.23860f,
    // f3 value=4
    -0.45697f, 0.62896f, -0.62896f, -0.00697f, 0.98953f, -0.00960f, -0.00697f, 0.10752f, -0.89160f, -0.52540f, -0.10069f, -0.72316f, -0.84581f, 0.10752f, -0.28215f, 0.06140f, 0.17571f, 0.91926f, 0.17571f, 0.93860f, 0.57655f, 0.62288f, 0.82429f, 0.88610f, 0.38352f, -0.26027f,
    // f4 value=5
    -0.00000f, 0.62896f, 0.77744f, -0.00000f, -0.10220f, 0.89201f, 0.51843f, 0.10602f, 0.72356f, -0.00000f, 0.98802f, 0.01000f, -0.51843f, 0.10602f, 0.72356f, 0.06140f, 0.50000f, 0.22893f, 0.17571f, 0.93860f, 0.50000f, 0.22893f, 0.82429f, 0.98481f, -0.13500f, 0.10922f,
    // f5 value=2
    0.45697f, -0.62896f, 0.62896f, 0.00518f, -0.98706f, 0.00713f, 0.84402f, -0.10506f, 0.27969f, 0.52361f, 0.10316f, 0.72069f, 0.00518f, -0.10506f, 0.88914f, 0.06140f, 0.82429f, 0.62288f, 0.17571f, 0.93860f, 0.42344f, 0.91926f, 0.82429f, 0.44474f, 0.77394f, 0.45081f,
    // f6 value=9
    -0.45697f, -0.62896f, 0.62896f, -0.00433f, -0.98589f, 0.00596f, -0.00433f, -0.10388f, 0.88796f, -0.52276f, 0.10433f, 0.71951f, -0.84316f, -0.10388f, 0.27851f, 0.06140f, 0.17571f, 0.91926f, 0.17571f, 0.93860f, 0.57656f, 0.62288f, 0.82429f, 0.68138f, -0.70205f, -0.20699f,
    // f7 value=6
    -0.73939f, 0.62896f, 0.24024f, -0.52609f, 0.10444f, 0.71604f, -0.00766f, 0.98644f, 0.00249f, -0.84649f, 0.10444f, -0.27007f, -0.84649f, -0.10378f, 0.27504f, 0.15455f, 0.60354f, 0.84545f, 0.06140f, 0.80313f, 0.93860f, 0.39229f, 0.93860f, 0.61020f, 0.77680f, -0.15569f,
    // f8 value=1
    -0.73939f, -0.62896f, -0.24024f, -0.52741f, -0.10556f, -0.71647f, -0.00898f, -0.98757f, -0.00292f, -0.84782f, -0.10557f, 0.26964f, -0.84782f, 0.10265f, -0.27547f, 0.15455f, 0.60354f, 0.84545f, 0.06140f, 0.80313f, 0.93860f, 0.39229f, 0.93860f, 0.66305f, -0.74218f, -0.09760f,
    // f9 value=8
    0.73257f, 0.62932f, 0.25942f, 0.52063f, 0.09970f, 0.71391f, 0.84889f, -0.10177f, 0.27569f, 0.85649f, 0.11297f, -0.26673f, 0.00941f, 0.98789f, 0.00291f, 0.14902f, 0.40920f, 0.38048f, 0.06928f, 0.79166f, 0.06140f, 0.85098f, 0.93860f, 0.67035f, -0.60081f, -0.43549f
    )
    val values = intArrayOf(10, 7, 3, 4, 5, 2, 9, 6, 1, 8)
    val faces = (0 until 10).map { fi ->
        val o = fi * 26
        DieFace(
            corners = data.copyOfRange(o + 3, o + 15),
            normal = Vec3(data[o], data[o + 1], data[o + 2]),
            fracs = data.copyOfRange(o + 15, o + 23),
            up = Vec3(data[o + 23], data[o + 24], data[o + 25]),
            value = values[fi]
        )
    }
    Die10(faces)
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
    tiles: List<Bitmap>,
    texPaint: Paint
) {
    val g = die10
    val camDist = 3.6f
    val focal = 2.4f
    val tintTarget = when {
        value == 1 -> Color(0xFFCF6679)
        value >= difficulty -> Color(0xFF4CAF50)
        else -> Color(0xFF3A3226)
    }
    val strokeColor = lerp(Color(0xFF3A3226), tintTarget, settle)
    val ptsAll = Array(10) { FloatArray(8) }
    val normals = arrayOfNulls<Vec3>(10)
    val zAvg = FloatArray(10)
    val visible = ArrayList<Int>(10)
    for (fi in 0 until 10) {
        val face = g.faces[fi]
        val n = q.rotate(face.normal)
        normals[fi] = n
        if (n.z <= 0.02f) continue
        val pts = ptsAll[fi]
        var zs = 0f
        for (k in 0 until 4) {
            val r = q.rotate(
                Vec3(face.corners[k * 3], face.corners[k * 3 + 1], face.corners[k * 3 + 2])
            )
            zs += r.z
            val proj = focal / (camDist - r.z)
            pts[k * 2] = center.x + r.x * proj * radiusPx
            pts[k * 2 + 1] = center.y - r.y * proj * radiusPx
        }
        zAvg[fi] = zs / 4f
        visible.add(fi)
    }
    visible.sortBy { zAvg[it] }
    val nativeCanvas = drawContext.canvas.nativeCanvas
    val clipPath = android.graphics.Path()
    val xform = android.graphics.Matrix()
    val srcPts = FloatArray(8)
    for (fi in visible) {
        val face = g.faces[fi]
        val n = normals[fi] ?: continue
        val pts = ptsAll[fi]
        val lam = n.dot(lightDir).coerceIn(0f, 1f)
        val shade = 0.45f + 0.55f * lam
        val path = Path()
        path.moveTo(pts[0], pts[1])
        for (k in 1 until 4) path.lineTo(pts[k * 2], pts[k * 2 + 1])
        path.close()
        clipPath.reset()
        clipPath.moveTo(pts[0], pts[1])
        for (k in 1 until 4) clipPath.lineTo(pts[k * 2], pts[k * 2 + 1])
        clipPath.close()
        val tile = tiles[fi]
        for (k in 0 until 4) {
            srcPts[k * 2] = face.fracs[k * 2] * tile.width
            srcPts[k * 2 + 1] = face.fracs[k * 2 + 1] * tile.height
        }
        xform.reset()
        if (xform.setPolyToPoly(srcPts, 0, pts, 0, 4)) {
            nativeCanvas.save()
            nativeCanvas.clipPath(clipPath)
            val sr = (shade * 255f).toInt().coerceIn(0, 255)
            texPaint.colorFilter = android.graphics.LightingColorFilter(
                (0xFF shl 24) or (sr shl 16) or (sr shl 8) or sr,
                0
            )
            texPaint.alpha = (alpha * 255f).toInt().coerceIn(0, 255)
            nativeCanvas.drawBitmap(tile, xform, texPaint)
            nativeCanvas.restore()
        } else {
            val base = Color(0xFFF4ECDC)
            drawPath(
                path = path,
                color = Color(base.red * shade, base.green * shade, base.blue * shade, alpha),
                style = Fill
            )
        }
        drawPath(
            path = path,
            color = strokeColor.copy(alpha = alpha * 0.9f),
            style = Stroke(width = radiusPx * 0.05f)
        )
        if (settle > 0f) {
            drawPath(
                path = path,
                color = tintTarget.copy(alpha = alpha * 0.16f * settle),
                style = Fill
            )
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
    val context = LocalContext.current
    val tiles = remember {
        val ids = intArrayOf(
            R.drawable.d10_face_0,
            R.drawable.d10_face_1,
            R.drawable.d10_face_2,
            R.drawable.d10_face_3,
            R.drawable.d10_face_4,
            R.drawable.d10_face_5,
            R.drawable.d10_face_6,
            R.drawable.d10_face_7,
            R.drawable.d10_face_8,
            R.drawable.d10_face_9
        )
        val opts = BitmapFactory.Options().apply { inScaled = false }
        ids.map { BitmapFactory.decodeResource(context.resources, it, opts) }
    }
    val texPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }
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
            val face = die10.faceFor(value)
            val base = alignToCamera(face.normal)
            val up = base.rotate(face.up)
            val phi = atan2(up.x, up.y)
            val end = axisAngle(Vec3(0f, 0f, 1f), phi).mul(base)
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
                tiles = tiles,
                texPaint = texPaint
            )
        }
    }
}
