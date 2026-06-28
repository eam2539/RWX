package io.github.rwx.di

import android.content.Context
import io.github.rwx.*
import io.github.rwx.p2p.WebRtcTunnelProxy
import io.github.rwx.render.GameRenderBackend
import io.github.rwx.session.GameSession
import io.github.rwx.settings.KEY_ANDROID_OPENGL_RENDERER
import org.koin.dsl.module

fun androidModule(context: Context) = module {
    WebRtcTunnelProxy.registerFactory { config -> AndroidWebRtcTunnelProxy(context, config) }
    single<PlatformBridge> { AndroidPlatformBridge(context) }
    single<PlatformStorage> { get<PlatformBridge>().storage }
    single<PreferenceStorage> { get<PlatformBridge>().preferenceStorage }
    single<AppMetadata> { get<PlatformBridge>().appMetadata }
    single<AppLogger> { get<PlatformBridge>().logger }
    single<CrashReporter> { get<PlatformBridge>().crashReporter }
    single {
        AndroidGameSession(
            rendererMode = when (selectedAndroidGameRenderBackend()) {
                AndroidGameRenderBackend.OPENGL -> AndroidRendererMode.OPENGL
                else -> AndroidRendererMode.CANVAS
            },
        )
    }
    single { AndroidExternalGameRenderBackend(gameSession = get()) }
    single<GameSession> { get<AndroidGameSession>() }
    single<GameRenderBackend> { get<AndroidExternalGameRenderBackend>() }
}

private fun org.koin.core.scope.Scope.selectedAndroidGameRenderBackend(): AndroidGameRenderBackend {
    val preferences = get<PreferenceStorage>().preference(PREFERENCE_NAME)
    return selectedAndroidGameRenderBackend(
        useOpenGlPreference = preferences.getBoolean(KEY_ANDROID_OPENGL_RENDERER, false),
        incompleteLoadAttempts = preferences.getInt("numIncompleteLoadAttempts", 0),
        loadsSinceNormalExit = preferences.getInt("numLoadsSinceRunningGameOrNormalExit", 0),
    )
}

internal enum class AndroidGameRenderBackend {
    CANVAS,
    OPENGL,
}

internal fun selectedAndroidGameRenderBackend(
    useOpenGlPreference: Boolean,
    incompleteLoadAttempts: Int = 0,
    loadsSinceNormalExit: Int = 0,
): AndroidGameRenderBackend =
    when {
        useOpenGlPreference && incompleteLoadAttempts <= 3 && loadsSinceNormalExit <= 15 ->
            AndroidGameRenderBackend.OPENGL

        else -> AndroidGameRenderBackend.CANVAS
    }
