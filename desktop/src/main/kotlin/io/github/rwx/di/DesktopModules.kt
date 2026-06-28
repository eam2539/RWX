package io.github.rwx.di

import io.github.rwx.*
import io.github.rwx.p2p.DesktopWebRtcTunnelProxy
import io.github.rwx.p2p.WebRtcTunnelProxy
import io.github.rwx.render.GameRenderBackend
import io.github.rwx.slick.SlickEmbeddedGameBackend
import io.github.rwx.slick.SlickGameSession
import org.koin.dsl.module

val desktopModule = module {
    WebRtcTunnelProxy.registerFactory { config -> DesktopWebRtcTunnelProxy(config) }
    single<PlatformBridge> { DesktopPlatformBridge() }
    single<PlatformStorage> { get<PlatformBridge>().storage }
    single<PreferenceStorage> { get<PlatformBridge>().preferenceStorage }
    single<AppMetadata> { get<PlatformBridge>().appMetadata }
    single<AppLogger> { get<PlatformBridge>().logger }
    single<CrashReporter> { get<PlatformBridge>().crashReporter }
    single { SlickGameSession(storage = get()) }
    single<GameRenderBackend> { SlickEmbeddedGameBackend(gameSession = get()) }
    single<io.github.rwx.session.GameSession> { get<SlickGameSession>() }
}
