package io.github.rwx

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.InputController
import com.corrodinggames.rts.gameFramework.SettingsEngine
import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.NativeAssetLoader
import de.fabmax.kool.createContext
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGl
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.util.FrontendScope
import io.github.rwx.app.AppOptions
import io.github.rwx.app.installApp
import io.github.rwx.di.coreModule
import io.github.rwx.di.desktopModule
import io.github.rwx.i18n.LocaleSettings
import io.github.rwx.settings.GameSettingsRepository
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.host.LoadingSceneHost
import io.github.rwx.ui.model.SettingsModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.GlobalContext
import kotlin.time.Duration.Companion.milliseconds

object KoolDesktopMain : KoinComponent {
    @JvmStatic
    fun main(args: Array<String>) {
        configureDesktopLogging()
        System.setProperty(LWJGL_CONTEXT_API_PROPERTY, LWJGL_NATIVE_CONTEXT_API)
        run(args)
    }

    fun run(args: Array<String> = emptyArray()) = runBlocking {
        configureLwjglMemoryStack()
        configureLegacyDesktopPlatform()
        GlobalContext.startKoin {
            modules(coreModule, desktopModule)
        }
        (getKoin().get<CrashReporter>() as? FileCrashReporter)
            ?.installAsDefaultUncaughtExceptionHandler()
        initializeLegacyPreferences()
        LocaleSettings.initialize()

        configureKoolOverlayFramebuffer()
        val options = AppOptions.parseArgs(args, isDesktop = true)
        val renderBackend = selectedRenderBackend()
        val swingHost = SwingKoolHost.create(
            fullscreen = SettingsEngine.getInstance().slick2dFullScreen,
            useOpenGl = renderBackend == RenderBackendGl.Companion,
        )
        val bridge=get<PlatformBridge>()
        bridge.filePickerHost=swingHost
        val context = createContext(createKoolConfig(swingHost, renderBackend))
        val app = KoolApplication(context)
        val loadingScene = LoadingSceneHost.createScene()
        app.ctx.addScene(loadingScene)
        FrontendScope.launch {
            installMsdfFonts()
            val session = installApp(
                context = app.ctx,
                options = options,
                onQuit = swingHost::requestClose,
            )
            app.ctx.removeScene(loadingScene)
            while (!session.isFinishLoading()) {
                delay(50L.milliseconds)
            }
        }

        app.ctx.run()
    }

    internal fun createKoolConfig(
        swingHost: SwingKoolHost,
        renderBackend: BackendProvider,
    ): KoolConfigJvm = KoolConfigJvm(
        defaultAssetLoader = NativeAssetLoader(DesktopPlatformStorage.resolveAssetRoot().absolutePath),
        windowTitle = "RWX Kool",
        windowSize = swingHost.windowSize,
        renderBackend = renderBackend,
        windowSubsystem = swingHost.windowSubsystem,
        asyncSceneUpdate = false,
        useOpenGlFallback = false,
    )

    private fun selectedRenderBackend(): BackendProvider = resolveDesktopRenderBackend(
        System.getProperty(RENDER_BACKEND_PROPERTY) ?: System.getenv(RENDER_BACKEND_ENV),
    )

    internal fun resolveDesktopRenderBackend(requestedBackend: String?): BackendProvider {
        val requested = requestedBackend ?: return RenderBackendVk.Companion
        return when (requested.lowercase()) {
            "vulkan", "vk" -> RenderBackendVk.Companion
            "opengl", "gl" -> RenderBackendGl.Companion
            "webgpu", "wgpu" -> throw IllegalArgumentException(
                "Kool WebGPU backend is not available in kool-core-desktop 0.19.0; " +
                        "available JVM backends are Vulkan and OpenGL.",
            )

            else -> throw IllegalArgumentException(
                "Unsupported Kool render backend '$requested'. Supported values: vulkan, opengl.",
            )
        }.also { backend ->
            logger.info { "Using Kool render backend: ${backend.displayName}" }
        }
    }

    private suspend fun installMsdfFonts() {
        UiTheme.Fonts.install()
    }

    private fun configureLegacyDesktopPlatform() {
        GameEngine.isMenuBackgroundDisabled = true
        GameEngine.isNonAndroidVersion = true
        GameEngine.isDesktopInitialized = true
        GameEngine.isJavaDesktopVersion = true
        GameEngine.isPCOrIOSVersion = true
        InputController.b = DesktopInputHandler()
        ensureDesktopOpenAlMusicFactory()
    }

    private fun initializeLegacyPreferences() {
        SettingsEngine.getInstance().save()
        val model = SettingsModel()
        get<GameSettingsRepository>().loadInto(model)
        get<GameSettingsRepository>().saveFrom(model)
    }

    private fun configureKoolOverlayFramebuffer() {
        System.setProperty(KOOL_TRANSPARENT_FRAMEBUFFER_PROPERTY, "true")
    }

    private const val KOOL_TRANSPARENT_FRAMEBUFFER_PROPERTY: String = "kool.transparentFramebuffer"
    private const val LWJGL_CONTEXT_API_PROPERTY: String = "org.lwjgl.opengl.contextAPI"
    private const val LWJGL_NATIVE_CONTEXT_API: String = "native"
    private const val RENDER_BACKEND_PROPERTY: String = "rwx.kool.backend"
    private const val RENDER_BACKEND_ENV: String = "RWX_KOOL_BACKEND"
}
