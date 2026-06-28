package io.github.rwx.geometry

class PointF() {
    @JvmField
    var x: Float = 0f

    @JvmField
    var y: Float = 0f

    constructor(x: Float, y: Float) : this() {
        this.x = x
        this.y = y
    }

    fun a(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun a(pointF: PointF) {
        x = pointF.x
        y = pointF.y
    }

    fun b(x: Float, y: Float) {
        this.x += x
        this.y += y
    }
}
