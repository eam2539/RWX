package io.github.rwx.ui.host

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.FrontendScope
import io.github.rwx.ui.component.*
import io.github.rwx.ui.model.*
import kotlinx.coroutines.*

/**
 * Renders the Level Select screen in pure Kool DSL: a map-button list headed by the mode title,
 * with a Back button at the bottom. Cross-platform — uses only kool-core common APIs.
 */
class LevelSelectSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val viewModelFactory: LevelSelectViewModelFactory,
    private val onAction: LevelSelectActionHandler = LevelSelectActionHandler {},
) {
    private val maps = mutableStateListOf<MapEntry>()
    private val modeTitle = mutableStateOf("")
    private val currentMode = mutableStateOf(LevelSelectMode.Skirmish)
    private val isLoading = mutableStateOf(false)
    private var mapLoadJob: Job? = null
    private var mapLoadRevision: Long = 0L

    fun updateMaps(mode: LevelSelectMode) {
        val revision = ++mapLoadRevision
        mapLoadJob?.cancel()
        currentMode.value = mode
        modeTitle.value = mode.label
        isLoading.value = true
        maps.clear()

        val viewModel = viewModelFactory.create(mode)
        mapLoadJob = FrontendScope.launch {
            try {
                val entries = withContext(Dispatchers.IO) { viewModel.items() }
                if (revision != mapLoadRevision) return@launch
                maps.clear()
                maps.addAll(entries)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                GameEngine.log("Failed to load maps for ${mode.name}: ${error.message}")
                if (revision == mapLoadRevision) {
                    maps.clear()
                }
            } finally {
                if (revision == mapLoadRevision) {
                    isLoading.value = false
                }
            }
        }
    }

    fun dispatch(action: LevelSelectAction) = onAction.onAction(action)

    fun createScene(): Scene = UiScene(LEVEL_SELECT_SCENE_NAME) {
        addPanelSurface(PanelStyle.Menu, "level-select-panel", model) { theme ->
            LevelSelectList(
                model = LevelSelectListModel(
                    title = modeTitle.use(),
                    maps = maps.use(),
                    currentMode = currentMode.use(),
                    availableModes = LevelSelectMode.entries,
                    isLoading = isLoading.use(),
                ),
                theme = theme,
                actions = LevelSelectListActions(
                    onMapSelected = { dispatch(LevelSelectAction.SelectMap(it)) },
                    onModeSelected = { dispatch(LevelSelectAction.SelectMode(it)) },
                    onBack = { dispatch(LevelSelectAction.Back) },
                ),
            )
        }
    }

    companion object {
        const val LEVEL_SELECT_SCENE_NAME: String = "level-select"
    }
}
