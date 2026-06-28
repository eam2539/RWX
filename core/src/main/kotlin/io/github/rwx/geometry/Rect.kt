package io.github.rwx.geometry

class Rect {
    @JvmField
    var a: Int = 0

    @JvmField
    var b: Int = 0

    @JvmField
    var c: Int = 0

    @JvmField
    var d: Int = 0

    constructor()

    constructor(i: Int, i2: Int, i3: Int, i4: Int) {
        a = i
        b = i2
        c = i3
        d = i4
    }

    constructor(rect: Rect) {
        a = rect.a
        b = rect.b
        c = rect.c
        d = rect.d
    }

    override fun equals(other: Any?): Boolean {
        val rect = other as? Rect ?: return false
        return a == rect.a && b == rect.b && c == rect.c && d == rect.d
    }

    override fun hashCode(): Int {
        var result = a
        result = 31 * result + b
        result = 31 * result + c
        result = 31 * result + d
        return result
    }

    override fun toString(): String = "Rect($a, $b, $c, $d)"

    fun a(): Boolean = a >= c || b >= d

    fun b(): Int = c - a

    fun c(): Int = d - b

    fun d(): Int = (a + c) shr 1

    fun e(): Int = (b + d) shr 1

    fun f(): Float = (a + c) * 0.5f

    fun g(): Float = (b + d) * 0.5f

    fun h() {
        d = 0
        b = 0
        c = 0
        a = 0
    }

    fun a(i: Int, i2: Int, i3: Int, i4: Int) {
        a = i
        b = i2
        c = i3
        d = i4
    }

    fun a(rect: Rect) {
        a = rect.a
        b = rect.b
        c = rect.c
        d = rect.d
    }

    fun a(i: Int, i2: Int) {
        a += i
        b += i2
        c += i
        d += i2
    }

    fun b(i: Int, i2: Int): Boolean =
        a < c && b < d && i >= a && i < c && i2 >= b && i2 < d

    fun b(rect: Rect): Boolean =
        a < c && b < d && a <= rect.a && b <= rect.b && c >= rect.c && d >= rect.d

    fun b(i: Int, i2: Int, i3: Int, i4: Int): Boolean =
        a < i3 && i < c && b < i4 && i2 < d

    fun isEmpty(): Boolean = a()

    fun width(): Int = b()

    fun height(): Int = c()

    fun centerXInt(): Int = d()

    fun centerYInt(): Int = e()

    fun centerX(): Float = f()

    fun centerY(): Float = g()

    fun clear() = h()

    fun set(left: Int, top: Int, right: Int, bottom: Int) = a(left, top, right, bottom)

    fun set(rect: Rect) = a(rect)

    fun offset(dx: Int, dy: Int) = a(dx, dy)

    fun contains(x: Int, y: Int): Boolean = b(x, y)

    fun contains(rect: Rect): Boolean = b(rect)

    fun intersects(left: Int, top: Int, right: Int, bottom: Int): Boolean = b(left, top, right, bottom)
}
