# AI

`api.ai` 用于配置 AI 玩家、读取观察帧并提交命令。

## 接口总览

```kotlin
interface AiBehavior {
    fun registerAgent(definition: AiAgentDefinition)
    fun unregisterAgent(id: String)
    fun bindAgent(binding: AiAgentBinding)
    fun unbindAgent(scope: AiControlScope)
    fun listPlayers(): List<AiPlayerState>
    fun getPlayerState(teamId: Int): AiPlayerState?
    fun configurePlayer(config: AiPlayerConfig): AiPlayerState?
    fun setPlayerEnabled(teamId: Int, enabled: Boolean): AiPlayerState?
    fun currentObservation(scope: AiObservationScope = AiObservationScope()): AiObservationFrame
    fun submitActions(actions: List<UnitCommandRequest>): List<UnitCommandResult>
}
```

## Agent 定义与绑定

`AiAgentDefinition` 字段：

- `id`、`displayName`
- `driver` — `AiAgentDriver.External(endpoint, protocol)` 或 `AiAgentDriver.Manual`
- `observation`、`actionSpace`、扩展 `properties`

`AiAgentBinding` 通过 `agentId` 和 `AiControlScope` 绑定 Agent。`AiControlScope` 可按 team ID、unit ID、unit type ID
限制控制范围。

> **注意**
> 当前 API 只提供进程内数据和命令契约，不负责启动网络客户端。

## 玩家配置

`AiPlayerConfig` 字段：

- `teamId`、`enabled`
- `mode` — `AiPlayerMode.HUMAN`、`GAME_AI` 或 `EXTERNAL_AGENT`
- `agentId`、`scope`、扩展 `properties`

`AiPlayerState` 返回：名称、启用状态、模式、Agent、原生 AI 控制状态、败北状态、单位数、credits 和 energy。

## 观察帧

`AiObservationScope` 可选择队伍、敌人、中立单位、已摧毁单位、中心/半径和最大单位数。

`currentObservation()` 返回 `AiObservationFrame`，包含：

- `frame`、`tick`、`scope`
- `teams` — `List<AiTeamObservation>`，每项包含 `id`、`name`、`credits`、`energy`、`unitCount`、`defeated`、`controlledByAi`
- `units` — `List<UnitRuntimeState>`，与 `api.unitWorld` 返回的快照相同
- `map` — 可选 `AiMapObservation(width, height, tileWidth, tileHeight)`
- 堆叠的前序帧、terminal team 集合

`AiObservationSpec` 控制地图、资源、订单、隐藏敌人和 frame stack。

`AiActionSpaceDefinition` 控制 `allowedTypes`、`maxUnitsPerAction`、`maxActionsPerTick` 和 `allowQueuedCommands`。

> **注意**
> `includeUnitOrders` 为 `false` 时，AI 视图只会复制快照并把 `currentTargetId` 置空，不会修改底层 `UnitWorld` 状态。

## 提交动作

`submitActions()` 接受与 `api.commands` 相同的 `UnitCommandRequest`，并额外应用 AI 可见性过滤、frame stack、玩家配置、控制范围和
Action Space 校验。

示例：

```kotlin
val obs = api.ai.currentObservation(
    AiObservationScope(teamId = 1, includeEnemies = true)
)
val myUnits = obs.units.filter { it.ref.teamId == 1 && !it.destroyed }

val actions = myUnits.map { unit ->
    UnitCommandRequest(
        teamId = 1,
        unitIds = listOf(unit.ref.instanceId),
        type = UnitCommandType.ATTACK_MOVE,
        target = UnitCommandTarget.Point(WorldPosition(500f, 500f)),
    )
}
api.ai.submitActions(actions)
```

> **注意**
> `api.commands` 不应用 AI 控制范围和 `AiActionSpaceDefinition`，这些限制只属于 `submitActions()`。
