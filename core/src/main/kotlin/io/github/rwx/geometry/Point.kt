package io.github.rwx.geometry

class Point() {
    @JvmField
    var worldX: Int = 0

    @JvmField
    var worldY: Int = 0

    constructor(worldX: Int, worldY: Int) : this() {
        this.worldX = worldX
        this.worldY = worldY
    }

    fun a(worldX: Int, worldY: Int) {
        this.worldX = worldX
        this.worldY = worldY
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Point && worldX == other.worldX && worldY == other.worldY)

    override fun hashCode(): Int = (31 * worldX) + worldY

    override fun toString(): String = "Point($worldX, $worldY)"
}
