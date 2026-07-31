package io.github.rwx.slick

import io.github.rwx.session.BattleRoomLaunchConfig
import io.github.rwx.session.GameMemorySnapshot

/**
 * What the Slick renderer should be showing.
 *
 * This is the single encoding of "what was asked for" on the desktop backend: [SlickGameSession]
 * publishes one of these, [SlickGame] holds exactly one as its pending work, and the render thread
 * consumes it. Because the variants are a closed set, the mutually-exclusive states can no longer
 * disagree with each other the way parallel nullable fields could.
 */
sealed interface SlickSessionRequest {
    /** Path reported in loading/error status, or null when the request loads no level of its own. */
    val loadingPath: String?

    /** True when honouring this request loads a level, so the render thread must drive the game loop. */
    val loadsLevel: Boolean
        get() = true

    /** Boot the engine and renderer only; no level is loaded. */
    data object EnginePreparation : SlickSessionRequest {
        override val loadingPath: String? = null
        override val loadsLevel: Boolean = false
    }

    data class Map(
        val mapPath: String,
    ) : SlickSessionRequest {
        override val loadingPath: String = mapPath
    }

    data class Replay(
        val replayName: String,
    ) : SlickSessionRequest {
        override val loadingPath: String = replayName
    }

    data class BattleRoom(
        val config: BattleRoomLaunchConfig,
    ) : SlickSessionRequest {
        override val loadingPath: String = config.mapPath
    }

    data class MemorySnapshot(
        val snapshot: GameMemorySnapshot,
    ) : SlickSessionRequest {
        override val loadingPath: String = snapshot.mapPath
    }

    /** Adopt a battle room the engine has already started (host pressed "Start game"). */
    data class StartedGame(
        val mapPath: String,
    ) : SlickSessionRequest {
        override val loadingPath: String = mapPath
    }

    data object MenuBackground : SlickSessionRequest {
        override val loadingPath: String? = null
    }
}

/**
 * Tracks only the hand-off between GameSession and the Slick render thread.
 * Map ownership and loading status remain in GameSession.
 */
internal class SlickSessionRequestState {
    var desired: SlickSessionRequest? = null
        private set

    var dispatched: SlickSessionRequest? = null
        private set

    fun replace(request: SlickSessionRequest) {
        desired = request
        dispatched = null
    }

    fun synchronize(request: SlickSessionRequest) {
        if (desired == request) return
        replace(request)
    }

    fun nextDispatch(): SlickSessionRequest? {
        val request = desired ?: return null
        if (request == dispatched) return null
        dispatched = request
        return request
    }

    fun clearRequest() {
        desired = null
        dispatched = null
    }

    fun runtimeStopped() {
        dispatched = null
    }
}
