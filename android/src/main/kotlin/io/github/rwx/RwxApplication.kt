package io.github.rwx

import android.app.Application
import android.content.pm.ApplicationInfo
import com.corrodinggames.rts.gameFramework.MusicManager
import io.github.rwx.audio.AndroidMediaMusicFactory
import io.github.rwx.di.androidModule
import io.github.rwx.di.coreModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.io.File

class RwxApplication : Application() {

    private lateinit var safAccess: AndroidSafAccess

    override fun onCreate() {
        super.onCreate()

        FileCrashReporter.get(
            crashFile = File(filesDir, CRASH_FILE_NAME),
            environment = androidCrashEnvironment(this),
        ).installAsDefaultUncaughtExceptionHandler()

        MusicManager.musicFactory = AndroidMediaMusicFactory(this)
        safAccess = AndroidSafAccess(this)
        SafPlatformBridge.install(safAccess)

        startKoin {
            androidContext(this@RwxApplication)
            modules(coreModule, androidModule(this@RwxApplication))
        }

        val isDebuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        Timber.plant(object : Timber.DebugTree() {
            override fun isLoggable(tag: String?, priority: Int): Boolean =
                isDebuggable || priority >= android.util.Log.WARN
        })
    }
}
