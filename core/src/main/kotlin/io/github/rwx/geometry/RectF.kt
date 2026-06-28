package io.github.rwx.geometry

class RectF {
    @JvmField
    var a: Float = 0f

    @JvmField
    var b: Float = 0f

    @JvmField
    var c: Float = 0f

    @JvmField
    var d: Float = 0f

    constructor()

    constructor(f: Float, f2: Float, f3: Float, f4: Float) {
        a = f
        b = f2
        c = f3
        d = f4
    }

    constructor(rectF: RectF) {
        a = rectF.a
        b = rectF.b
        c = rectF.c
        d = rectF.d
    }

    constructor(rect: Rect) {
        a = rect.a.toFloat()
        b = rect.b.toFloat()
        c = rect.c.toFloat()
        d = rect.d.toFloat()
    }

    override fun toString(): String = "RectF($a, $b, $c, $d)"

    fun a(): Boolean = a >= c || b >= d

    fun b(): Float = c - a

    fun c(): Float = d - b

    fun d(): Float = (a + c) * 0.5f

    fun e(): Float = (b + d) * 0.5f

    fun f() {
        d = 0f
        b = 0f
        c = 0f
        a = 0f
    }

    fun a(f: Float, f2: Float, f3: Float, f4: Float) {
        a = f
        b = f2
        c = f3
        d = f4
    }

    fun a(rectF: RectF) {
        a = rectF.a
        b = rectF.b
        c = rectF.c
        d = rectF.d
    }

    fun a(rect: Rect) {
        a = rect.a.toFloat()
        b = rect.b.toFloat()
        c = rect.c.toFloat()
        d = rect.d.toFloat()
    }

    fun a(f: Float, f2: Float) {
        a += f
        b += f2
        c += f
        d += f2
    }

    fun b(f: Float, f2: Float): Boolean =
        a < c && b < d && f >= a && f < c && f2 >= b && f2 < d

    fun b(f: Float, f2: Float, f3: Float, f4: Float): Boolean {
        if (a >= f3 || f >= c || b >= f4 || f2 >= d) {
            return false
        }
        if (a < f) {
            a = f
        }
        if (b < f2) {
            b = f2
        }
        if (c > f3) {
            c = f3
        }
        if (d > f4) {
            d = f4
            return true
        }
        return true
    }

    fun b(rectF: RectF): Boolean = b(rectF.a, rectF.b, rectF.c, rectF.d)

    fun c(f: Float, f2: Float) {
        if (f < a) {
            a = f
        } else if (f > c) {
            c = f
        }
        if (f2 < b) {
            b = f2
        } else if (f2 > d) {
            d = f2
        }
    }

    fun g() {
        if (a > c) {
            val f = a
            a = c
            c = f
        }
        if (b > d) {
            val f2 = b
            b = d
            d = f2
        }
    }

    fun isEmpty(): Boolean = a()

    fun width(): Float = b()

    fun height(): Float = c()

    fun centerX(): Float = d()

    fun centerY(): Float = e()

    fun clear() = f()

    fun set(left: Float, top: Float, right: Float, bottom: Float) = a(left, top, right, bottom)

    fun set(rectF: RectF) = a(rectF)

    fun set(rect: Rect) = a(rect)

    fun offset(dx: Float, dy: Float) = a(dx, dy)

    fun contains(x: Float, y: Float): Boolean = b(x, y)

    fun intersect(left: Float, top: Float, right: Float, bottom: Float): Boolean = b(left, top, right, bottom)

    fun intersect(rectF: RectF): Boolean = b(rectF)

    fun include(x: Float, y: Float) = c(x, y)

    fun sort() = g()

    companion object {
        @JvmStatic
        fun a(rectF: RectF, rectF2: RectF): Boolean =
            rectF.a < rectF2.c && rectF2.a < rectF.c && rectF.b < rectF2.d && rectF2.b < rectF.d

        @JvmStatic
        fun intersects(rectF: RectF, rectF2: RectF): Boolean = a(rectF, rectF2)
    }
}
