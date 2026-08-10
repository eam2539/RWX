package io.github.rwx.mod

/**
 * Ray-versus-rectangle tests for beam weapons.
 *
 * Beams are checked against each unit's axis-aligned exposure box rather than its collision
 * circle, so long thin ships are hit along their length instead of within a radius.
 */
internal object RayDamageGeometry {
    private const val DIRECTION_EPSILON = 0.000001f

    /**
     * True when [range] reaches the exposure box, measured to the box's nearest edge rather
     * than its centre, so a large target is in range as soon as any part of it is.
     */
    @JvmStatic
    fun isExposureWithinRange(
        sourceX: Float,
        sourceY: Float,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
        range: Float,
    ): Boolean {
        val halfWidth = width * 0.5f
        val halfHeight = height * 0.5f
        val dx = axisDistance(sourceX, centerX - halfWidth, centerX + halfWidth)
        val dy = axisDistance(sourceY, centerY - halfHeight, centerY + halfHeight)
        return (dx * dx) + (dy * dy) < range * range
    }

    /**
     * True when the beam hits the exposure box, either by crossing it or by passing within the
     * weapon's width of its centre.
     *
     * The width test compares a squared perpendicular distance against
     * `rayWidth² + width * targetWidthFactor`, whose second term is a plain length rather than
     * a squared one. That mismatch is deliberate and reproduces the original game's formula
     * exactly; callers compensate through [targetWidthFactor], which carries the world-unit
     * scale (16 for a rectangle authored in the source game's units). Making the units
     * consistent here would silently change every beam's effective width.
     *
     * Like the original, this ignores whether the target lies ahead of or behind the beam
     * origin — the caller has already limited candidates by range.
     */
    @JvmStatic
    fun intersectsRayOrWidth(
        originX: Float,
        originY: Float,
        directionX: Float,
        directionY: Float,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
        rayWidth: Float,
        targetWidthFactor: Float,
    ): Boolean {
        val length = StrictMath.sqrt(
            (directionX * directionX + directionY * directionY).toDouble(),
        ).toFloat()
        if (length <= DIRECTION_EPSILON) return false
        val normalizedX = directionX / length
        val normalizedY = directionY / length
        if (!firstIntersectionDistance(
                originX,
                originY,
                normalizedX,
                normalizedY,
                centerX,
                centerY,
                width,
                height,
            ).isNaN()
        ) {
            return true
        }
        if (rayWidth <= 0f && targetWidthFactor <= 0f) return false
        val cross = (normalizedX * (centerY - originY)) - (normalizedY * (centerX - originX))
        return cross * cross <= (rayWidth * rayWidth) + (width * targetWidthFactor)
    }

    /**
     * Distance from the beam origin to where it first enters the exposure box, `0` when the
     * origin is already inside it, or `NaN` when the beam misses.
     *
     * A zero-sized box stays a point rather than falling back to a radius: that is the source
     * game's runtime behaviour for units that never had an exposure rectangle authored.
     */
    @JvmStatic
    fun firstIntersectionDistance(
        originX: Float,
        originY: Float,
        directionX: Float,
        directionY: Float,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
    ): Float {
        val length = StrictMath.sqrt(
            (directionX * directionX + directionY * directionY).toDouble(),
        ).toFloat()
        if (length <= DIRECTION_EPSILON) return Float.NaN
        val dirX = directionX / length
        val dirY = directionY / length
        val halfWidth = width * 0.5f
        val halfHeight = height * 0.5f
        val minX = centerX - halfWidth
        val maxX = centerX + halfWidth
        val minY = centerY - halfHeight
        val maxY = centerY + halfHeight
        var near = Float.NEGATIVE_INFINITY
        var far = Float.POSITIVE_INFINITY

        // Slab test: clip the ray against each axis' pair of edges in turn.
        if (StrictMath.abs(dirX) <= DIRECTION_EPSILON) {
            if (originX !in minX..maxX) return Float.NaN
        } else {
            val first = (minX - originX) / dirX
            val second = (maxX - originX) / dirX
            near = maxOf(near, minOf(first, second))
            far = minOf(far, maxOf(first, second))
        }
        if (StrictMath.abs(dirY) <= DIRECTION_EPSILON) {
            if (originY !in minY..maxY) return Float.NaN
        } else {
            val first = (minY - originY) / dirY
            val second = (maxY - originY) / dirY
            near = maxOf(near, minOf(first, second))
            far = minOf(far, maxOf(first, second))
        }
        if (far < maxOf(near, 0f)) return Float.NaN
        return maxOf(near, 0f)
    }

    /** Distance from [point] to the `[minimum], [maximum]` interval, or `0` when inside it. */
    private fun axisDistance(point: Float, minimum: Float, maximum: Float): Float = when {
        point < minimum -> minimum - point
        point > maximum -> point - maximum
        else -> 0f
    }
}
