package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.GameModeType
import com.corrodinggames.rts.gameFramework.network.GameRoomSettings
import io.github.rwx.logger
import io.github.rwx.session.BattleRoomCoreConfig
import io.github.rwx.session.BattleRoomLaunchConfig
import io.github.rwx.session.BattleRoomSnapshot
import io.github.rwx.session.GameSession
import io.github.rwx.ui.AppScreen
import io.github.rwx.ui.host.BattleRoomSceneHost
import io.github.rwx.ui.model.BattleRoomChatLine
import io.github.rwx.ui.model.LevelSelectMode
import io.github.rwx.ui.model.LevelSelectViewModelFactory
import io.github.rwx.ui.model.MapEntry

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
    private var returnScreen: AppScreen = AppScreen.LevelSelect
    private val chatLines = mutableListOf<BattleRoomChatLine>()

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

    fun prepareForMap(
        map: MapEntry,
        sandbox: Boolean = false,
        returnScreen: AppScreen = AppScreen.LevelSelect,
    ) {
        selectedMap = map
        this.returnScreen = returnScreen
        val newConfig = BattleRoomLaunchConfig(
            sandbox = sandbox,
            aiPlayerCount = defaultBattleRoomAiPlayerCount(map.playerCount),
            room = BattleRoomCoreConfig(
                mapPath = map.mapAssetPath,
                options = GameRoomSettings(),
            ),
        )
        newConfig.room.options.apply {
            aiDifficulty = GameEngine.getInstance()?.settingsEngine?.aiDifficulty ?: 1
            if (map.isSavedGame) gameModeType = GameModeType.savedGame
            if (sandbox) {
                fogMode = 0
                revealedMap = true
            }
        }
        chatLines.clear()
        if (prepareLocalSinglePlayerRoom(newConfig)) {
            return
        }
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

    fun closeRoom(): AppScreen {
        isSelectingMapForBattleRoom = false
        gameSession.leaveBattleRoom()
        return returnScreen
    }

    fun selectBattleRoomMap(map: MapEntry) {
        val previousMap = selectedMap
        selectedMap = map
        val snapshot = currentSnapshot(refreshNetworkStatus = false)
        val currentAiCount = snapshot?.players?.count { it.isAI } ?: 0
        val targetAiCount =
            if (currentAiCount == defaultBattleRoomAiPlayerCount(playerCount = previousMap?.playerCount)) {
                defaultBattleRoomAiPlayerCount(playerCount = map.playerCount)
            } else {
                currentAiCount
            }
        runCatching {
            check(gameSession.setBattleRoomMap(map.mapAssetPath, map.isSavedGame)) {
                "Game session rejected map change"
            }
            val diff = targetAiCount - currentAiCount
            when {
                diff > 0 -> gameSession.addBattleRoomAi(diff)
                diff < 0 -> snapshot?.players?.filter { it.isAI }?.sortedBy { it.spawnColorIndex }?.takeLast(-diff)
                    ?.forEach { gameSession.kickBattleRoomPlayer(it.id) }
            }
        }.onFailure { error ->
            logger.warn(error) { "Unable to set battle room map" }
            showUnavailableDialog("Unable to set map: ${error.message ?: error.javaClass.simpleName}")
        }
        updateFromNetwork()
        isSelectingMapForBattleRoom = false
    }

    fun prepareHostRoom(map: MapEntry) {
        chatLines.clear()
        returnScreen = AppScreen.Multiplayer
        selectedMap = map
    }

    fun markJoinedRoomStarted() {
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
        sceneHost.updateRoom(snapshot.toBattleRoomModel(previewFor(snapshot), chatLines))
    }

    private fun previewFor(snapshot: BattleRoomSnapshot): String? =
        selectedMap?.previewAssetPath
            ?: snapshot.room.mapPath.takeIf { it.isNotBlank() }
                ?.let { path ->
                    runCatching {
                        levelSelectViewModelFactory.create(selectedMode).mapEntry(path).previewAssetPath
                    }.getOrNull()
                }

    fun appendChat(text: String, teamColorIndex: Int = -1) {
        val line = BattleRoomChatLine(text, teamColorIndex)
        chatLines += line
        sceneHost.appendChat(line)
    }

    private fun prepareLocalSinglePlayerRoom(config: BattleRoomLaunchConfig): Boolean {
        if (!gameSession.enterLocalBattleRoomLive(config)) {
            return false
        }
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
