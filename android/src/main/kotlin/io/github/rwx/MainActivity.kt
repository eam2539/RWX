package io.github.rwx

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.DocumentsContract
import android.view.*
import android.widget.FrameLayout
import de.fabmax.kool.KoolConfigAndroid
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.createKoolContext
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.platform.KoolContextAndroid
import de.fabmax.kool.platform.KoolSurfaceView
import de.fabmax.kool.util.FrontendScope
import de.fabmax.kool.util.MsdfFont
import io.github.rwx.app.AppOptions
import io.github.rwx.app.AppSession
import io.github.rwx.app.installApp
import io.github.rwx.di.AndroidGameRenderBackend
import io.github.rwx.di.selectedAndroidGameRenderBackend
import io.github.rwx.p2p.P2PLobbyService
import io.github.rwx.render.canvas.KoolCanvasContextResourceInvalidator
import io.github.rwx.session.GameLoadingStatus
import io.github.rwx.session.GameSession
import io.github.rwx.settings.KEY_ANDROID_OPENGL_RENDERER
import io.github.rwx.ui.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get

class MainActivity : Activity(), PlatformStoragePickerController {

    private var koolContext: KoolContextAndroid? = null
    private var appSession: AppSession? = null
    private var nativeGameSession: AndroidGameSession? = null
    private var rendererPreferenceListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var textInputController: AndroidTextInputController? = null
    private var pendingStoragePickerResult: ((ExternalStorageSelection?) -> Unit)? = null
    private var applicationExitRequested: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformStoragePickerBridge.install(this)
        UiScale.uiScale.value = ANDROID_UI_SCALE
        val selectedGameSession = get<GameSession>()
        nativeGameSession = selectedGameSession as? AndroidGameSession
        observeNativeRendererPreference()
        val koolSurface = createKoolSurface(transparentOverlay = nativeGameSession != null)
        val ctx = createKoolContext(
            KoolConfigAndroid(
                appContext = applicationContext,
                surfaceView = koolSurface,
            )
        )
        ctx.surfaceView.setPreserveEGLContextOnPause(true)
        requestFrameRate(ctx.surfaceView)
        koolContext = ctx
        val root = FrameLayout(this)
        nativeGameSession?.attach(this, root, ctx.surfaceView)
        root.addView(
            ctx.surfaceView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
        textInputController = AndroidTextInputController(this, root, ctx.surfaceView).also {
            PlatformTextInputBridge.install(it)
        }
        setContentView(root)
        enterImmersiveMode()

        val loadingHost = LoadingSceneHost()
        val loadingScene = loadingHost.createScene()
        ctx.addScene(loadingScene)
        FrontendScope.launch {
            loadingHost.update(GameLoadingStatus("Loading fonts", 0.05f))
            installMsdfFonts()
            loadingHost.update(GameLoadingStatus("Preparing UI icons", UI_ICON_PROGRESS_START))
            preloadUiIconTextures { completed, total ->
                loadingHost.update(
                    GameLoadingStatus(
                        "Preparing UI icons ($completed/$total)",
                        progressInRange(completed, total, UI_ICON_PROGRESS_START, UI_ICON_PROGRESS_END),
                    )
                )
            }
            loadingHost.update(GameLoadingStatus("Preparing map catalog", MAP_CATALOG_PROGRESS_START))
            val previewPaths = initialMapPreviewPaths(get()) { completed, total ->
                loadingHost.update(
                    GameLoadingStatus(
                        "Preparing map catalog ($completed/$total)",
                        progressInRange(
                            completed,
                            total,
                            MAP_CATALOG_PROGRESS_START,
                            MAP_CATALOG_PROGRESS_END,
                        ),
                    )
                )
            }
            loadingHost.update(GameLoadingStatus("Preparing map previews", MAP_PREVIEW_PROGRESS_START))
            preloadMapPreviewTextures(previewPaths) { completed, total ->
                loadingHost.update(
                    GameLoadingStatus(
                        "Preparing map previews ($completed/$total)",
                        progressInRange(completed, total, MAP_PREVIEW_PROGRESS_START, MAP_PREVIEW_PROGRESS_END),
                    )
                )
            }
            loadingHost.update(GameLoadingStatus("Preparing multiplayer", MULTIPLAYER_PROGRESS))
            withContext(Dispatchers.IO) {
                runCatching {
                    P2PLobbyService.getInstance().startIfNeeded()
                }.onFailure { error ->
                    logger.warn(error) { "Unable to prewarm RWX P2P during startup" }
                }
            }
            loadingHost.update(GameLoadingStatus("Preparing UI renderer", UI_RENDERER_PROGRESS))
            loadingHost.showUiTextureWarmup()
            delay(UI_RENDERER_WARMUP_MILLIS)
            loadingHost.hideUiTextureWarmup()
            loadingHost.update(GameLoadingStatus("Preparing game session", 0.95f))
            delay(STARTING_GAME_STATUS_FRAME_MILLIS)
            appSession = installApp(
                context = ctx,
                options = AppOptions(isDesktop = false),
                onQuit = ::exitApplication,
            )
            ctx.removeScene(loadingScene)
        }
    }

    private fun exitApplication() {
        runOnUiThread {
            if (applicationExitRequested) return@runOnUiThread
            applicationExitRequested = true
            finishAndRemoveTask()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            appSession?.navigateBack() ?: return super.onKeyDown(keyCode, event)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        nativeGameSession?.onPause()
        super.onPause()
        koolContext?.onPause()
    }

    override fun onResume() {
        super.onResume()
        enterImmersiveMode()
        koolContext?.onResume()
        nativeGameSession?.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enterImmersiveMode()
        }
    }

    override fun onDestroy() {
        val terminateProcess = applicationExitRequested
        PlatformStoragePickerBridge.uninstall(this)
        pendingStoragePickerResult?.invoke(null)
        pendingStoragePickerResult = null
        textInputController?.let {
            PlatformTextInputBridge.uninstall(it)
            it.dispose()
        }
        textInputController = null
        nativeGameSession?.detach()
        rendererPreferenceListener?.let { listener ->
            getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(listener)
        }
        rendererPreferenceListener = null
        invalidateUiTextureCaches()
        get<KoolCanvasContextResourceInvalidator>().invalidateContextResources()
        koolContext?.releaseFromKoolSystem()
        koolContext = null
        appSession = null
        nativeGameSession = null
        super.onDestroy()
        if (terminateProcess) {
            Process.killProcess(Process.myPid())
        }
    }

    private fun observeNativeRendererPreference() {
        if (nativeGameSession == null) return
        val preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key != KEY_ANDROID_OPENGL_RENDERER) return@OnSharedPreferenceChangeListener
            val backend = selectedAndroidGameRenderBackend(
                useOpenGlPreference = prefs.getBoolean(KEY_ANDROID_OPENGL_RENDERER, false),
                incompleteLoadAttempts = prefs.getInt("numIncompleteLoadAttempts", 0),
                loadsSinceNormalExit = prefs.getInt("numLoadsSinceRunningGameOrNormalExit", 0),
            )
            val rendererMode = when (backend) {
                AndroidGameRenderBackend.CANVAS -> AndroidRendererMode.CANVAS
                AndroidGameRenderBackend.OPENGL -> AndroidRendererMode.OPENGL
            }
            runOnUiThread {
                nativeGameSession?.switchRendererMode(rendererMode)
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        rendererPreferenceListener = listener
    }

    override fun requestExternalStorage(onResult: (ExternalStorageSelection?) -> Unit) {
        runOnUiThread {
            pendingStoragePickerResult?.invoke(null)
            pendingStoragePickerResult = onResult
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                )
            }
            startActivityForResult(intent, EXTERNAL_STORAGE_TREE_REQUEST_CODE)
        }
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != EXTERNAL_STORAGE_TREE_REQUEST_CODE) return
        val callback = pendingStoragePickerResult ?: return
        pendingStoragePickerResult = null
        val uri = data?.data?.takeIf { resultCode == RESULT_OK }
        val selection = uri?.let(::persistExternalStorageSelection)
        FrontendScope.launch { callback(selection) }
    }

    private fun persistExternalStorageSelection(uri: Uri): ExternalStorageSelection? = runCatching {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
        )
        ExternalStorageSelection(
            uri = uri.toString(),
            displayPath = DocumentsContract.getTreeDocumentId(uri),
        )
    }.getOrNull()

    private suspend fun installMsdfFonts() {
        UiTheme.Fonts.installBaseFont(loadMsdfFont(UiTheme.Fonts.CJK_MSDF_FONT_PATH))
        UiTheme.Fonts.installTitleFont(loadMsdfFont(UiTheme.Fonts.TITLE_MSDF_FONT_PATH))
    }

    private fun createKoolSurface(transparentOverlay: Boolean): KoolSurfaceView =
        KoolSurfaceView(this).apply {
            if (transparentOverlay) {
                // Native Canvas/OpenGL is below this view, so only that backend needs an alpha
                // surface and media-overlay composition.
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
                setZOrderMediaOverlay(true)
            } else {
                // Kool owns the only game surface. An opaque buffer avoids transparent-surface
                // composition and the stale/black buffer flashes it can introduce on Android.
                setEGLConfigChooser(8, 8, 8, 0, 16, 0)
                holder.setFormat(PixelFormat.OPAQUE)
            }
        }

    private suspend fun loadMsdfFont(path: String): MsdfFont =
        MsdfFont(path).getOrElse { error ->
            throw IllegalStateException("Failed to load required MSDF font: $path", error)
        }

    private fun enterImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.decorView.windowInsetsController?.apply {
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
}

private const val ANDROID_UI_SCALE: Float = 0.8f
private const val ANDROID_TARGET_FRAME_RATE: Float = 60f
private const val UI_ICON_PROGRESS_START: Float = 0.1f
private const val UI_ICON_PROGRESS_END: Float = 0.42f
private const val MAP_CATALOG_PROGRESS_START: Float = 0.44f
private const val MAP_CATALOG_PROGRESS_END: Float = 0.54f
private const val MAP_PREVIEW_PROGRESS_START: Float = 0.56f
private const val MAP_PREVIEW_PROGRESS_END: Float = 0.84f
private const val MULTIPLAYER_PROGRESS: Float = 0.88f
private const val UI_RENDERER_PROGRESS: Float = 0.93f
private const val UI_RENDERER_WARMUP_MILLIS: Long = 100L
private const val STARTING_GAME_STATUS_FRAME_MILLIS: Long = 50L
private const val PREWARM_MAP_PREVIEWS_PER_MODE: Int = 12
private const val EXTERNAL_STORAGE_TREE_REQUEST_CODE: Int = 9124

private suspend fun initialMapPreviewPaths(
    viewModelFactory: LevelSelectViewModelFactory,
    onProgress: (Int, Int) -> Unit,
): List<String> {
    val modes = LevelSelectMode.entries.filter { it.assetSubdir != null }
    val paths = mutableListOf<String>()
    modes.forEachIndexed { index, mode ->
        val entries = withContext(Dispatchers.Default) {
            viewModelFactory.create(mode).items()
        }
        paths += entries.asSequence()
            .mapNotNull { it.previewAssetPath }
            .take(PREWARM_MAP_PREVIEWS_PER_MODE)
        onProgress(index + 1, modes.size)
    }
    return paths.distinct()
}

private fun progressInRange(
    completed: Int,
    total: Int,
    start: Float,
    end: Float,
): Float {
    if (total <= 0) return end
    return start + (end - start) * (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
}

private fun requestFrameRate(surfaceView: GLSurfaceView) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
    surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            holder.surface.setFrameRate(
                ANDROID_TARGET_FRAME_RATE,
                Surface.FRAME_RATE_COMPATIBILITY_FIXED_SOURCE,
            )
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

        override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
    })
}

private fun KoolContextAndroid.releaseFromKoolSystem() {
    onDestroy()
    val contextField = KoolSystem::class.java.getDeclaredField("defaultContext").apply {
        isAccessible = true
    }
    if (contextField.get(null) === this) {
        contextField.set(null, null)
    }
}
