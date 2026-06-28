package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.GameEngine
import io.github.rwx.logger
import io.github.rwx.session.BattleRoomLaunchConfig
import io.github.rwx.session.BattleRoomSnapshot
import io.github.rwx.session.GameSession
import io.github.rwx.ui.AppScreen
import io.github.rwx.ui.BattleRoomChatLine
import io.github.rwx.ui.BattleRoomSceneHost
import io.github.rwx.ui.LevelSelectMode
import io.github.rwx.ui.LevelSelectViewModelFactory
import io.github.rwx.ui.MapEntry

internal class BattleRoomController(
    private val gameSession: GameSession,
    private val levelSelectViewModelFactory: LevelSelectViewModelFactory,
    private val sceneHost: BattleRoomSceneHost,
    initialMode: LevelSelectMode,
    private val showUnavailableDialog: (String) -> Unit,
) {
    var selectedMode: LevelSelectMode = initialMode
    var selectedMap: MapEntry? = null
        private set
    var isSelectingMapForBattleRoom: Boolean = false
        private set
    private var returnScreen: AppScreen = AppScreen.LevelSelect
    private var draft: BattleRoomDraft? = null
    private val chatLines = mutableListOf<BattleRoomChatLine>()

    val hasDraft: Boolean
        get() = draft != null

    fun selectedOrDefaultMap(): MapEntry? =
        selectedMap ?: selectDefaultMap()

    fun selectDefaultMap(): MapEntry? {
        selectedMap?.let { return it }
        return runCatching {
            levelSelectViewModelFactory.create(selectedMode).items().firstOrNull()
        }.onFailure { error ->
            logger.warn(error) { "Unable to select default RW map for ${selectedMode.label}" }
        }.getOrNull()?.also { map ->
            selectedMap = map
            logger.info { "Selected default RW map: ${map.mapAssetPath}" }
        }
    }

    fun currentSnapshot(refreshNetworkStatus: Boolean = true): BattleRoomSnapshot? =
        gameSession.currentBattleRoom(refreshNetworkStatus)

    fun draftLaunchConfig(): BattleRoomLaunchConfig? =
        draft?.toLaunchConfig()

    fun draftMapPath(): String? =
        draft?.map?.let { it.saveName ?: it.mapAssetPath }

    fun prepareForMap(
        map: MapEntry,
        sandbox: Boolean = false,
        returnScreen: AppScreen = AppScreen.LevelSelect,
    ) {
        selectedMap = map
        this.returnScreen = returnScreen
        val nextDraft = newBattleRoomDraft(
            map = map,
            sandbox = sandbox,
            aiDifficulty = GameEngine.getInstance()?.settingsEngine?.aiDifficulty ?: 1,
        )
        chatLines.clear()
        if (prepareLocalSinglePlayerRoom(nextDraft)) {
            return
        }
        draft = null
        showUnavailableDialog("Unable to prepare battle room")
    }

    fun prepareSandboxGame(): Boolean {
        val map = selectSandboxMap() ?: selectDefaultMap()
        if (map == null) {
            showUnavailableDialog("No sandbox map is available")
            return false
        }
        selectedMode = LevelSelectMode.Skirmish
        prepareForMap(
            map = map,
            sandbox = true,
            returnScreen = AppScreen.MainMenu,
        )
        return true
    }

    fun beginMapSelection() {
        isSelectingMapForBattleRoom = true
    }

    fun cancelMapSelection() {
        isSelectingMapForBattleRoom = false
    }

    fun closeRoom(): AppScreen {
        isSelectingMapForBattleRoom = false
        gameSession.leaveBattleRoom()
        return returnScreen
    }

    fun selectBattleRoomMap(map: MapEntry) {
        selectedMap = map
        val currentDraft = draft
        if (currentDraft != null) {
            val previousDefaultAiCount = defaultBattleRoomAiPlayerCount(currentDraft.map.playerCount)
            val nextDefaultAiCount = defaultBattleRoomAiPlayerCount(map.playerCount)
            currentDraft.settings.mapPath = map.saveName ?: map.fileName
            draft = currentDraft.copy(
                map = map,
                aiPlayerCount = if (currentDraft.aiPlayerCount == previousDefaultAiCount) {
                    nextDefaultAiCount
                } else {
                    currentDraft.aiPlayerCount
                },
            )
            updateFromDraft()
        } else {
            runCatching {
                check(gameSession.setBattleRoomMap(map.saveName ?: map.mapAssetPath, map.saveName != null)) {
                    "Game session rejected map change"
                }
            }.onFailure { error ->
                logger.warn(error) { "Unable to set battle room map" }
                showUnavailableDialog("Unable to set map: ${error.message ?: error.javaClass.simpleName}")
            }
            updateFromNetwork()
        }
        isSelectingMapForBattleRoom = false
    }

    fun prepareHostRoom(map: MapEntry) {
        draft = null
        chatLines.clear()
        returnScreen = AppScreen.Multiplayer
        selectedMap = map
    }

    fun markJoinedRoomStarted() {
        draft = null
        chatLines.clear()
        returnScreen = AppScreen.Multiplayer
    }

    fun updateConnectedRoom(snapshot: BattleRoomSnapshot?) {
        snapshot
            ?.let { sceneHost.updateRoom(it.toBattleRoomModel(previewFor(it), chatLines)) }
            ?: updateFromNetwork()
    }

    fun updateFromNetwork(refreshNetworkStatus: Boolean = true) {
        val snapshot = currentSnapshot(refreshNetworkStatus) ?: return
        draft = null
        sceneHost.updateRoom(snapshot.toBattleRoomModel(previewFor(snapshot), chatLines))
    }

    private fun previewFor(snapshot: BattleRoomSnapshot): String? =
        selectedMap?.previewAssetPath
            ?: snapshot.mapPath
                ?.takeIf { it.isNotBlank() }
                ?.let { path ->
                    runCatching {
                        levelSelectViewModelFactory.create(selectedMode).resolveMapEntry(path).previewAssetPath
                    }.getOrNull()
                }

    fun updateFromDraft() {
        val currentDraft = draft ?: return
        sceneHost.updateRoom(
            currentDraft.toBattleRoomModel(
                mapTypeLabel = if (currentDraft.sandbox) "Sandbox" else selectedMode.label,
                chatLines = chatLines,
            ),
        )
    }

    fun appendChat(text: String, teamColorIndex: Int = -1) {
        val line = BattleRoomChatLine(text, teamColorIndex)
        chatLines += line
        sceneHost.appendChat(line)
    }

    private fun prepareLocalSinglePlayerRoom(draft: BattleRoomDraft): Boolean {
        val launchConfig = draft.toLaunchConfig()
        if (!gameSession.enterLocalBattleRoomLive(launchConfig) &&
            !gameSession.prepareLocalBattleRoom(launchConfig)
        ) {
            return false
        }
        this.draft = null
        gameSession.currentBattleRoom()?.let { snapshot ->
            sceneHost.updateRoom(
                snapshot.toBattleRoomModel(previewFor(snapshot), chatLines),
            )
        }
        return true
    }

    private fun selectSandboxMap(): MapEntry? =
        runCatching {
            levelSelectViewModelFactory.create(LevelSelectMode.Skirmish).items()
                .firstOrNull { it.fileName.contains("Crossing Large", ignoreCase = true) }
                ?: levelSelectViewModelFactory.create(LevelSelectMode.Skirmish).items().firstOrNull()
        }.onFailure { error ->
            logger.warn(error) { "Unable to select sandbox map" }
        }.getOrNull()
}
