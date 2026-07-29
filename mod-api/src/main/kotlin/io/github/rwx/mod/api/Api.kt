package io.github.rwx.mod.api

interface Api {
    val game: Game
    val assets: Asset
    val units: Units
    val unitWorld: UnitWorld
    val commands: UnitCommands
    val maps: Map
    val rules: Rule
    val localization: Localization
    val audio: Audio
    val graphics: Graphics
    val effects: Effects
    val ui: Ui
    val ai: AiBehavior
}

interface Game {
    val gameVersion: String
    val tick: Long
    val headless: Boolean

    fun log(level: LogLevel, message: String, cause: Throwable? = null)
    fun schedule(delayTicks: Long, task: ModTask): ScheduledTask
    fun repeat(intervalTicks: Long, task: ModTask): ScheduledTask
    fun localTeam(): TeamState?
    fun team(teamId: Int): TeamState?
    fun registerTeamAction(id: TeamActionId, handler: TeamActionHandler)
    fun requestTeamAction(id: TeamActionId, targetPosition: WorldPosition? = null): Boolean
    fun registerProjectileObserver(id: ProjectileObserverId, observer: ProjectileObserver)
    fun registerPreFireObserver(id: PreFireObserverId, observer: PreFireObserver)
}

@JvmInline
value class TeamActionId(val value: String) {
    init {
        require(value.matches(Regex("[a-z][a-z0-9_.-]*:[a-z0-9][a-z0-9_./-]*"))) {
            "Invalid namespace-qualified team action id: $value"
        }
    }
}

@JvmInline
value class TeamFlagId(val value: String) {
    init {
        require(value.matches(Regex("[a-z][a-z0-9_.-]*:[a-z0-9][a-z0-9_./-]*"))) {
            "Invalid namespace-qualified team flag id: $value"
        }
    }
}

val TeamFlagId.unitTag: Tag
    get() = Tag(
        "mod_flag_" + value
            .lowercase()
            .replace(Regex("[^a-z0-9_]+"), "_")
            .trim('_')
    )

interface TeamState {
    val teamId: Int
    fun resource(id: ResourceId): Double
    fun hasFlag(id: TeamFlagId): Boolean
}

fun interface TeamActionHandler {
    fun execute(context: TeamActionContext)
}

interface TeamActionContext : TeamState {
    val targetPosition: WorldPosition?

    fun trySpend(cost: Cost): Boolean
    fun addResource(id: ResourceId, amount: Double)
    fun grantFlag(id: TeamFlagId)
    fun revokeFlag(id: TeamFlagId)
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

fun interface ModTask {
    fun run(context: Api)
}

interface ScheduledTask {
    val cancelled: Boolean

    fun cancel()
}

interface Asset {
    fun mount(namespace: String, root: ResourcePath)
    fun open(path: ResourcePath): ResourceStream
    fun exists(path: ResourcePath): Boolean
    fun list(path: ResourcePath): List<ResourcePath>
}

data class ResourcePath(val value: String)

interface ResourceStream : AutoCloseable {
    fun readBytes(): ByteArray
    fun readText(charset: String = "UTF-8"): String
}
