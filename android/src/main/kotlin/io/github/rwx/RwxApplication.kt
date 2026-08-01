package io.github.rwx

import android.app.Application
import android.content.pm.ApplicationInfo
import com.corrodinggames.rts.gameFramework.MusicManager
import io.github.rwx.audio.AndroidMediaMusicFactory
import io.github.rwx.di.androidModule
import io.github.rwx.di.coreModule
import io.github.rwx.mod.api.LogLevel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin
import timber.log.Timber
import java.io.File

class RwxApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FileCrashReporter.get(
            crashFile = File(filesDir, CRASH_FILE_NAME),
            environment = androidCrashEnvironment(this),
        ).installAsDefaultUncaughtExceptionHandler()

        MusicManager.musicFactory = AndroidMediaMusicFactory(this)
        SafPlatformBridge.install(AndroidSafAccess(this))

        startKoin {
            androidContext(this@RwxApplication)
            modules(coreModule, androidModule(this@RwxApplication))
        }

        val storage = getKoin().get<PlatformStorage>()
        storage.createDirectories()

        val isDebuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        GlobalLogger.minimumLevel = if (isDebuggable) LogLevel.DEBUG else LogLevel.INFO
        Timber.plant(object : Timber.DebugTree() {
            override fun isLoggable(tag: String?, priority: Int): Boolean =
                isDebuggable || priority >= android.util.Log.WARN
        })
        Timber.plant(
            FileLoggingTree(
                logFile = storage.rootDir.resolve("logs/rwx.log"),
                minimumPriority = if (isDebuggable) android.util.Log.VERBOSE else android.util.Log.INFO,
            ),
        )
    }
}
