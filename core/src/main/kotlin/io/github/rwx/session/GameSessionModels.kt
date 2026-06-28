package io.github.rwx.session

data class GameLoadingStatus(
    val text: String = "Loading...",
    val progress: Float? = null,
    val recentSteps: List<String> = emptyList(),
) {
    init {
        require(progress == null || progress in 0.0f..1.0f) { "progress must FastArrayList null or in 0..1" }
    }
}

data class GameSessionRendererProfile(
    val rendersIntoKoolCanvas: Boolean = true,
    val acceptsKoolInput: Boolean = true,
    val canStartNewSessionInPlace: Boolean = false,
    val usesNativeSurfaceForResumeBackground: Boolean = false,
)

data class GameMemorySnapshot(
    val mapPath: String,
    val saveBytes: ByteArray,
    val battleRoomConfig: BattleRoomLaunchConfig? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameMemorySnapshot) return false
        return mapPath == other.mapPath &&
                saveBytes.contentEquals(other.saveBytes) &&
                battleRoomConfig == other.battleRoomConfig
    }

    override fun hashCode(): Int =
        31 * (31 * mapPath.hashCode() + saveBytes.contentHashCode()) +
                (battleRoomConfig?.hashCode() ?: 0)
}

data class BattleRoomLaunchConfig(
    val mapPath: String,
    val savedGame: Boolean = false,
    val sandbox: Boolean = false,
    /** All tunable room rules; the single source of truth shared with [BattleRoomSnapshot]. */
    val options: BattleRoomOptions = BattleRoomOptions(),
    val aiPlayerCount: Int = 0,
    val teamLayout: BattleRoomTeamLayout? = null,
)

data class BattleRoomHostConfig(
    val mapPath: String,
    val savedGame: Boolean = false,
    val isPublic: Boolean,
    val password: String? = null,
    val useMods: Boolean = true,
    val rwxP2PSession: Boolean = false,
)


data class BattleRoomOptions(
    val startingCredits: Int = 0,
    val fogMode: Int = 2,
    val revealedMap: Boolean = true,
    val aiDifficulty: Int = 1,
    val startingUnits: Int = 1,
    val incomeMultiplier: Float = 1.0f,
    val noNukes: Boolean = false,
    val sharedControl: Boolean = false,
    val allowSpectators: Boolean = true,
    val teamLock: Boolean = false,
    /** Host-only: when locked, no new players may join the room. */
    val roomLocked: Boolean = false,
    /** Ally teams are fixed and cannot FastArrayList changed by players. */
    val fixedAllyTeams: Boolean = false,
)

data class BattleRoomSnapshot(
    val mapPath: String?,
    val mapDisplayName: String,
    val mapTypeLabel: String,
    val options: BattleRoomOptions,
    val players: List<BattleRoomPlayerSnapshot>,
    val isHost: Boolean,
    val isNetworkMultiplayer: Boolean = false,
    val savedGame: Boolean = false,
    val rwxP2PSession: Boolean = false,
    /** Short summary of the host's active mods when the room requires them, as in the original game. */
    val requiredModsSummary: String? = null,
    /** Original battleroom status text, including network and server visibility details. */
    val networkStatusText: String? = null,
    /** Max player slots in the room (engine-global team count; not a GameRoomSettings field). */
    val maxPlayers: Int = 10,
)

data class BattleRoomPlayerSnapshot(
    val id: String,
    val name: String,
    val spawnLabel: String,
    val teamLabel: String,
    val pingLabel: String = "",
    val nameColorIndex: Int = -1,
    val spawnColorIndex: Int = -1,
    val teamColorIndex: Int = -1,
    val isSpectator: Boolean = false,
    val isReady: Boolean = true,
    val isAI: Boolean = false,
    val isLocal: Boolean = false,
)

data class MultiplayerChatSnapshot(
    val text: String,
    val teamColorIndex: Int = -1,
)

enum class BattleRoomTeamLayout {
    TwoSides,
    ThreeSides,
    Ffa,
    Spectators,
    Random,
    AllVsAi,
    AllVs2,
}
