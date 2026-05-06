package com.corrodinggames.rts.gameFramework

class DesktopPlatformBridge : PlatformBridge {
    override fun isMobilePlatform(): Boolean = false

    override fun createModClassLoader(modFile: java.io.File, parent: ClassLoader): ClassLoader {
        return java.net.URLClassLoader(arrayOf(modFile.toURI().toURL()), parent)
    }
}