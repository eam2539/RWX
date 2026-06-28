package io.github.rwx.render.canvas

import java.util.*

object KoolArgbColor {
    private val namedColors: Map<String, Int> = mapOf(
        "black" to -0x1000000,
        "darkgray" to -0xbbbbbc,
        "gray" to -0x777778,
        "lightgray" to -0x333334,
        "white" to -0x1,
        "red" to -0x10000,
        "green" to -0xff0100,
        "blue" to -0xffff01,
        "yellow" to -0x100,
        "cyan" to -0xff0001,
        "magenta" to -0xff01,
        "aqua" to -0xff0001,
        "fuchsia" to -0xff01,
        "darkgrey" to -0xbbbbbc,
        "grey" to -0x777778,
        "lightgrey" to -0x333334,
        "lime" to -0xff0100,
        "maroon" to -0x800000,
        "navy" to -0xffff80,
        "olive" to -0x7f8000,
        "purple" to -0x7fff80,
        "silver" to -0x3f3f40,
        "teal" to -0xff7f80,
    )

    @JvmStatic
    fun a(color: Int): Int = color ushr 24

    @JvmStatic
    fun b(color: Int): Int = (color shr 16) and 255

    @JvmStatic
    fun c(color: Int): Int = (color shr 8) and 255

    @JvmStatic
    fun d(color: Int): Int = color and 255

    @JvmStatic
    fun a(red: Int, green: Int, blue: Int): Int =
        -0x1000000 or (red shl 16) or (green shl 8) or blue

    @JvmStatic
    fun a(alpha: Int, red: Int, green: Int, blue: Int): Int =
        (alpha shl 24) or (red shl 16) or (green shl 8) or blue

    @JvmStatic
    fun a(value: String): Int {
        if (value[0] == '#') {
            var parsed = value.substring(1).toLong(radix = 16)
            if (value.length == 7) {
                parsed = parsed or -0x1000000L
            } else if (value.length != 9) {
                throw IllegalArgumentException("Unknown color")
            }
            return parsed.toInt()
        }
        return namedColors[value.lowercase(Locale.ROOT)]
            ?: throw IllegalArgumentException("Unknown color")
    }
}
