package io.github.rwx.di

import io.github.rwx.PlatformStorage
import io.github.rwx.PreferenceStorage
import io.github.rwx.mod.FileSystemModRepository
import io.github.rwx.mod.ModRepository
import io.github.rwx.net.ResourceBrowserRepository
import io.github.rwx.net.UpdateRepository
import io.github.rwx.render.canvas.*
import io.github.rwx.settings.GameSettingsRepository
import io.github.rwx.ui.host.*
import io.github.rwx.ui.model.*
import org.koin.dsl.module

val coreModule = module {
    single {
        GameSettingsRepository(
            preferenceStorage = get(),
        )
    }
    single<ModRepository> {
        FileSystemModRepository(
            storage = get<PlatformStorage>(),
            preferenceStorage = get<PreferenceStorage>(),
        )
    }
    single { ResourceBrowserRepository(storage = get<PlatformStorage>()) }
    single { UpdateRepository() }
    single<KoolCanvasTextureStore> { KoolCanvasTextureRegistry }
    single<KoolCanvasTextureResolver> { get<KoolCanvasTextureStore>() }
    single<KoolCanvasContextResourceInvalidator> { KoolCanvasTextureRegistry }
    factory { parameters ->
        KoolCanvasSceneHost(
            frameRenderer = KoolCanvasFrameRenderer(textureStore = get()),
            sceneName = parameters.getOrNull<String>() ?: KoolCanvasSceneHost.DEFAULT_SCENE_NAME,
        )
    }
    single<LevelSelectViewModelFactory> {
        val storage = get<PlatformStorage>()
        val viewModels = mutableMapOf<LevelSelectMode, LevelSelectViewModel>()
        object : LevelSelectViewModelFactory {
            override fun create(mode: LevelSelectMode): LevelSelectViewModel =
                synchronized(viewModels) {
                    viewModels.getOrPut(mode) { LevelSelectViewModel(mode, storage) }
                }

            override fun invalidateCaches() {
                synchronized(viewModels) {
                    viewModels.values.forEach { it.invalidateCache() }
                }
            }
        }
    }
    single<ReplaySelectViewModelFactory> {
        ReplaySelectViewModelFactory { ReplaySelectViewModel() }
    }

    factory { parameters ->
        MainMenuSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onAction = parameters.getOrNull<(MainMenuAction) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        PauseMenuSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onAction = parameters.getOrNull<(PauseMenuAction) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        LevelSelectSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            viewModelFactory = get(),
            onAction = parameters.getOrNull<LevelSelectActionHandler>() ?: LevelSelectActionHandler {},
        )
    }
    factory { parameters ->
        ReplaySelectSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            viewModelFactory = get(),
            onAction = parameters.getOrNull<ReplaySelectActionHandler>() ?: ReplaySelectActionHandler {},
        )
    }
    factory { parameters ->
        SettingsSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onAction = parameters.getOrNull<(SettingsAction) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        DialogSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onVisibilityChanged = parameters.getOrNull<(Boolean) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        LoadingDialogSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onVisibilityChanged = parameters.getOrNull<(Boolean) -> Unit>() ?: {},
        )
    }
    single { parameters ->
        SnackbarSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onVisibilityChanged = parameters.getOrNull<(Boolean) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        MultiplayerSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onAction = parameters.getOrNull<(MultiplayerAction) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        ModsSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onAction = parameters.getOrNull<(ModsAction) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        ResourceBrowserSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onAction = parameters.getOrNull<(ResourceBrowserAction) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        BattleRoomSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onAction = parameters.getOrNull<(BattleRoomAction) -> Unit>() ?: {},
        )
    }
    factory { parameters ->
        ModWindowSceneHost(
            model = parameters.getOrNull<SettingsModel>() ?: SettingsModel(),
            onBack = parameters.getOrNull<() -> Unit>() ?: {},
        )
    }
    factory { ModHudSceneHost() }
}
