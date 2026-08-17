package io.github.rwx

import org.lwjgl.system.Configuration
import org.lwjgl.system.MemoryStack

/**
 * LWJGL sizes its per-thread [MemoryStack] exactly once, when the class initializes, by reading
 * [Configuration.STACK_SIZE]. Kool raises that to 128 KB from inside `createContext`, but our
 * startup opens the OpenAL device first and `ALC.create()` already pushes a stack frame - so by the
 * time kool asks, the size is locked at LWJGL's 64 KB default and kool's call is a silent no-op.
 *
 * That mattered because `VkInstance.<init>` enumerates a physical device's extensions into a single
 * stack allocation of `count * VkExtensionProperties.SIZEOF` bytes. At 260 bytes per entry, 64 KB
 * holds ~251 extensions; Windows GPU drivers that expose more killed Vulkan backend creation with
 * `OutOfMemoryError: Out of stack space.` before the first frame was ever drawn, leaving the user
 * staring at the black Swing window we had already made visible.
 *
 * So claim the size up front and touch [MemoryStack] while our value is still in effect, which
 * pins it for every thread and makes kool's later 128 KB request harmlessly late.
 */
internal fun configureLwjglMemoryStack(sizeKb: Int = LWJGL_STACK_SIZE_KB) {
    System.setProperty(LWJGL_STACK_SIZE_PROPERTY, sizeKb.toString())
    Configuration.STACK_SIZE.set(sizeKb)
    MemoryStack.stackGet()
}

/** Bytes one `VkExtensionProperties` occupies: `VK_MAX_EXTENSION_NAME_SIZE` + a `uint32` version. */
internal const val VK_EXTENSION_PROPERTIES_SIZEOF: Int = 260

/** Roughly 2000 device extensions of headroom, against the ~251 that 64 KB allowed. */
internal const val LWJGL_STACK_SIZE_KB: Int = 512

private const val LWJGL_STACK_SIZE_PROPERTY: String = "org.lwjgl.system.stackSize"
