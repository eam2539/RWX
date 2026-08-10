package io.github.rwx.mod

import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.game.units.actions.ActionId
import com.corrodinggames.rts.game.units.custom.AnimationTag
import com.corrodinggames.rts.gameFramework.GameEngine
import io.github.rwx.geometry.PointF
import io.github.rwx.mod.ModTeamActionRegistry.actions
import io.github.rwx.mod.api.*

object ModTeamActionRegistry : ModOwnedRegistry {
    const val SYSTEM_ACTION_TYPE = 300

    /**
     * Shared with [actions] so a lookup and the queue write it implies stay atomic against
     * teardown: otherwise [unregister] could drop an action and cancel its pending commands
     * between [request] resolving an owner and enqueuing for it, stranding a command that
     * belongs to a mod that is already gone.
     */
    private val lock = Any()
    private val actions = ModRegistrationTable<String, TeamActionHandler>("Team action", lock)

    fun register(owner: ApiImpl, id: TeamActionId, handler: TeamActionHandler) {
        actions.register(owner, id.value, handler)
    }

    override fun unregister(owner: ApiImpl) {
        synchronized(lock) {
            actions.removeOwned(owner)
            NativeCommandQueue.cancel(owner)
        }
    }

    fun clear() = actions.clear()

    @JvmStatic
    fun request(team: PlayerTeam?, id: TeamActionId, targetPosition: WorldPosition?): Boolean {
        if (team == null) return false
        synchronized(lock) {
            val owner = actions.owned(id.value)?.owner ?: return false
            NativeCommandQueue.enqueue(owner) {
                val command = GameEngine.getInstance().commandController.newCommandForTeam(team)
                command.isSystemAction = true
                command.systemActionType = SYSTEM_ACTION_TYPE
                command.actionId = ActionId.intern(id.value)
                targetPosition?.let { position ->
                    command.targetPoint = PointF(position.x, position.y)
                    command.systemFloat = position.height
                }
            }
        }
        return true
    }

    @JvmStatic
    fun execute(team: PlayerTeam?, actionId: String?, targetPoint: PointF?, targetHeight: Float): Boolean {
        if (team == null || actionId == null) return false
        val handler = actions[actionId] ?: return false
        val targetPosition = targetPoint?.let { WorldPosition(it.x, it.y, targetHeight) }
        handler.execute(TeamActionContextImpl(team, targetPosition))
        return true
    }
}

internal fun PlayerTeam.toTeamState(): TeamState = TeamStateImpl(this)

internal fun teamFlagTag(id: TeamFlagId): String = id.unitTag.value

private open class TeamStateImpl(protected val team: PlayerTeam) : TeamState {
    override val teamId: Int get() = team.teamId

    override fun resource(id: ResourceId): Double = when (id.value) {
        "credits" -> team.credits
        else -> 0.0
    }

    override fun hasFlag(id: TeamFlagId): Boolean {
        val tags = team.getTeamAnimationSet() ?: return false
        return AnimationTag.a(AnimationTag.c(teamFlagTag(id)), tags)
    }
}

private class TeamActionContextImpl(
    team: PlayerTeam,
    override val targetPosition: WorldPosition?,
) : TeamStateImpl(team), TeamActionContext {
    override fun trySpend(cost: Cost): Boolean {
        if (cost.resources.keys.any { it.value != "credits" }) return false
        val credits = cost.resources.entries.sumOf { it.value }
        if (team.credits < credits) return false
        team.credits -= credits
        return true
    }

    override fun addResource(id: ResourceId, amount: Double) {
        require(id.value == "credits") { "Runtime custom resources are not supported yet: ${id.value}" }
        team.credits += amount
    }

    override fun grantFlag(id: TeamFlagId) {
        team.b(AnimationTag.a(teamFlagTag(id)))
    }

    override fun revokeFlag(id: TeamFlagId) {
        team.c(AnimationTag.a(teamFlagTag(id)))
    }
}
