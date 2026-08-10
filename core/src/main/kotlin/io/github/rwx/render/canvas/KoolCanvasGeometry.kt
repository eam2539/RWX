package io.github.rwx.render.canvas

import kotlin.math.cos
import kotlin.math.sin

data class KoolCanvasViewport(
    val width: Int,
    val height: Int,
) {
    init {
        require(width >= 0) { "Viewport width must be non-negative" }
        require(height >= 0) { "Viewport height must be non-negative" }
    }
}

data class KoolCanvasPoint(
    val x: Float,
    val y: Float,
)

data class KoolCanvasRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
    val isEmpty: Boolean get() = width <= 0f || height <= 0f
    val hasZeroArea: Boolean get() = width == 0f || height == 0f

    val boundsLeft: Float get() = minOf(left, right)
    val boundsTop: Float get() = minOf(top, bottom)
    val boundsRight: Float get() = maxOf(left, right)
    val boundsBottom: Float get() = maxOf(top, bottom)

    fun intersect(other: KoolCanvasRect): KoolCanvasRect? {
        val nextLeft = maxOf(left, other.left)
        val nextTop = maxOf(top, other.top)
        val nextRight = minOf(right, other.right)
        val nextBottom = minOf(bottom, other.bottom)
        val intersection = KoolCanvasRect(nextLeft, nextTop, nextRight, nextBottom)
        return intersection.takeUnless { it.isEmpty }
    }

    fun boundsIntersect(other: KoolCanvasRect): KoolCanvasRect? {
        val nextLeft = maxOf(boundsLeft, other.boundsLeft)
        val nextTop = maxOf(boundsTop, other.boundsTop)
        val nextRight = minOf(boundsRight, other.boundsRight)
        val nextBottom = minOf(boundsBottom, other.boundsBottom)
        val intersection = KoolCanvasRect(nextLeft, nextTop, nextRight, nextBottom)
        return intersection.takeUnless { it.isEmpty }
    }

    fun orientBounds(bounds: KoolCanvasRect): KoolCanvasRect =
        KoolCanvasRect(
            left = if (width >= 0f) bounds.left else bounds.right,
            top = if (height >= 0f) bounds.top else bounds.bottom,
            right = if (width >= 0f) bounds.right else bounds.left,
            bottom = if (height >= 0f) bounds.bottom else bounds.top,
        )

    companion object {
        fun fromSize(width: Float, height: Float): KoolCanvasRect = KoolCanvasRect(0f, 0f, width, height)
    }
}

/**
 * A compact 2D affine state stack for the Kool canvas command buffer, independent of platform
 * matrix classes.
 */
data class KoolCanvasTransform(
    val scaleX: Float = 1f,
    val skewY: Float = 0f,
    val skewX: Float = 0f,
    val scaleY: Float = 1f,
    val translateX: Float = 0f,
    val translateY: Float = 0f,
) {
    fun translate(dx: Float, dy: Float): KoolCanvasTransform = multiply(
        KoolCanvasTransform(translateX = dx, translateY = dy),
    )

    fun scale(sx: Float, sy: Float, pivotX: Float = 0f, pivotY: Float = 0f): KoolCanvasTransform =
        translate(pivotX, pivotY)
            .multiply(KoolCanvasTransform(scaleX = sx, scaleY = sy))
            .translate(-pivotX, -pivotY)

    fun rotate(degrees: Float, pivotX: Float = 0f, pivotY: Float = 0f): KoolCanvasTransform {
        if (degrees == 0f) return this
        val radians = Math.toRadians(degrees.toDouble())
        val c = cos(radians).toFloat()
        val s = sin(radians).toFloat()
        return translate(pivotX, pivotY)
            .multiply(KoolCanvasTransform(scaleX = c, skewY = s, skewX = -s, scaleY = c))
            .translate(-pivotX, -pivotY)
    }

    fun multiply(other: KoolCanvasTransform): KoolCanvasTransform = KoolCanvasTransform(
        scaleX = scaleX * other.scaleX + skewX * other.skewY,
        skewY = skewY * other.scaleX + scaleY * other.skewY,
        skewX = scaleX * other.skewX + skewX * other.scaleY,
        scaleY = skewY * other.skewX + scaleY * other.scaleY,
        translateX = scaleX * other.translateX + skewX * other.translateY + translateX,
        translateY = skewY * other.translateX + scaleY * other.translateY + translateY,
    )

    fun map(point: KoolCanvasPoint): KoolCanvasPoint = KoolCanvasPoint(
        x = scaleX * point.x + skewX * point.y + translateX,
        y = skewY * point.x + scaleY * point.y + translateY,
    )

    fun mapRectBounds(rect: KoolCanvasRect): KoolCanvasRect {
        if (this === Identity) return rect
        val p0 = map(KoolCanvasPoint(rect.left, rect.top))
        val p1 = map(KoolCanvasPoint(rect.right, rect.top))
        val p2 = map(KoolCanvasPoint(rect.right, rect.bottom))
        val p3 = map(KoolCanvasPoint(rect.left, rect.bottom))
        return KoolCanvasRect(
            left = minOf(p0.x, p1.x, p2.x, p3.x),
            top = minOf(p0.y, p1.y, p2.y, p3.y),
            right = maxOf(p0.x, p1.x, p2.x, p3.x),
            bottom = maxOf(p0.y, p1.y, p2.y, p3.y),
        )
    }

    companion object {
        val Identity: KoolCanvasTransform = KoolCanvasTransform()
    }
}
