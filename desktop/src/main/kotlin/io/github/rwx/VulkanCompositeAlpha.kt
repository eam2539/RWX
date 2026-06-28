package io.github.rwx

import org.lwjgl.vulkan.KHRSurface.*

internal object VulkanCompositeAlpha {
    @JvmStatic
    fun select(supportedModes: Int): Int {
        if (!System.getProperty(TRANSPARENT_FRAMEBUFFER_PROPERTY).toBoolean()) {
            return VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
        }
        return TRANSPARENT_MODES.firstOrNull { mode -> supportedModes and mode != 0 }
            ?: VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
    }

    private val TRANSPARENT_MODES = intArrayOf(
        VK_COMPOSITE_ALPHA_PRE_MULTIPLIED_BIT_KHR,
        VK_COMPOSITE_ALPHA_POST_MULTIPLIED_BIT_KHR,
        VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR,
        VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR,
    )
    private const val TRANSPARENT_FRAMEBUFFER_PROPERTY = "kool.transparentFramebuffer"
}
