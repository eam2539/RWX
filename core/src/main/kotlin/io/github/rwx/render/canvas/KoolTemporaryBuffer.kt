package io.github.rwx.render.canvas

import com.corrodinggames.rts.gameFramework.utility.OptimizedSizes

object KoolTemporaryBuffer {
    private var buffer: CharArray? = null

    @JvmStatic
    fun a(size: Int): CharArray {
        val result = synchronized(this) {
            buffer.also { buffer = null }
        }
        return if (result == null || result.size < size) {
            CharArray(OptimizedSizes.growSizeByTwo(size))
        } else {
            result
        }
    }

    @JvmStatic
    fun a(chars: CharArray) {
        if (chars.size > 1000) {
            return
        }
        synchronized(this) {
            buffer = chars
        }
    }
}
