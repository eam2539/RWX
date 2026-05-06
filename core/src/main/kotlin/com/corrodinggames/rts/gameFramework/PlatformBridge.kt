package com.corrodinggames.rts.gameFramework

import java.io.File


interface PlatformBridge {
    fun isMobilePlatform(): Boolean

    /**
     * Create an isolated ClassLoader for a mod JAR file.
     * Desktop: URLClassLoader over JVM .class files.
     * Android: DexClassLoader over classes.dex inside the JAR.
     */
    fun createModClassLoader(modFile: File, parent: ClassLoader): ClassLoader
}