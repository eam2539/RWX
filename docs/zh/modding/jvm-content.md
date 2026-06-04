# 内容 API

`api.units` 用于注册自定义单位。

## 注册单位

```kotlin
interface Units {
    fun registerUnit(definition: UnitDefinition)
}
```

通常使用 `units.unit(id) { ... }` 简写。`unitTemplate { ... }` 返回可复用的 `UnitTemplate`。

## UnitBuilder 入口

| 入口                      | 对应原生 Section                                                            |
|---------------------------|-----------------------------------------------------------------------------|
| `core {}`                 | `[core]`                                                                    |
| `graphics {}`             | `[graphics]`                                                                |
| `movement {}`             | `[movement]`                                                                |
| `attack {}`               | `[attack]`                                                                  |
| `ai {}`                   | `[ai]`                                                                      |
| `resource(name) {}`       | `[resource_name]`                                                           |
| `globalResource(name) {}` | `[global_resource_name]`                                                    |
| `action(name) {}`         | `[action_name]`                                                             |
| `hiddenAction(name) {}`   | `[hiddenAction_name]`                                                       |
| `turret(name) {}`         | `[turret_name]`                                                             |
| `projectile(name) {}`     | `[projectile_name]`                                                         |
| `effect(name) {}`         | `[effect_name]`                                                             |
| `effect(name, spec)`      | 复用预先构建的 `EffectSpec`（`UnitBuilder` 与 `UnitIniBuilder` 均有此重载） |
| `decal(name) {}`          | `[decal_name]`                                                              |
| `attachment(name) {}`     | `[attachment_name]`                                                         |
| `leg(name) {}`            | `[leg_name]`                                                                |
| `arm(name) {}`            | `[arm_name]`                                                                |
| `placementRule(name) {}`  | `[placementRule_name]`                                                      |
| `animation(name) {}`      | `[animation_name]`                                                          |
| `canBuild(name) {}`       | `[canBuild_name]`                                                           |
| `template(name) {}`       | `[template_name]`                                                           |
| `extension {}`            | 不生成 INI Section                                                          |
| `on(...) {}`              | 声明式 RWX 事件动作绑定                                                     |
| `listen(...)`             | JVM 事件回调绑定                                                            |
| `removeEventHandler(id)`  | 删除先前的事件绑定                                                          |

同名入口重复调用时修改同一个 Spec。

## 声明单位

```kotlin
val scout = UnitId("example:scout")

api.units.unit(scout) {
    core {
        displayText = "Scout"
        displayTextLang = mapOf("zh" to "侦察车")
        maxHp = 180
        mass = 600
        radius = 10
        price = 250
        buildSpeed = "6s"
        tags = listOf("vehicle", "scout")
    }
    graphics {
        image = ResourcePath("assets/units/scout.png").nativePath
        imageSmoothing = true
    }
    movement {
        movementType = MovementType.LAND
        moveSpeed = 2.1f
        maxTurnSpeed = 4f
    }
}
```

## 武器

武器由 Turret 和 Projectile 组合声明：

```kotlin
val shell = WeaponId("example:cannon_shell")

api.units.unit(UnitId("example:artillery")) {
    attack {
        canAttack = true
        canAttackLandUnits = "true"
        maxAttackRange = 260f
    }
    turret("main") {
        x = 0f
        y = 8f
        projectile = shell.nativeName
        delay = 75f
        limitingRange = 260f
        canAttackLandUnits = "true"
    }
    projectile(shell.nativeName) {
        directDamage = 35
        areaDamage = 12
        areaRadius = 28
        life = 120
        speed = 4f
        image = ResourcePath("assets/projectiles/shell.png").nativePath
    }
}
```

`ProjectileSpec.mutator(name) { ... }` 写入原生 Projectile Mutator。名称不能包含 `_`，原生解析器把第一个下划线作为
Mutator/属性分隔符。

## Extension 侧车

RWX 专属的渲染、观察和伤害元数据通过 `extension {}` 绑定，不会序列化成 INI：

```kotlin
extension {
    shaderId = "example:unit_material"
    renderBinding = UnitRenderBinding(
        RendererId("example:unit_overlay"),
        RenderVariantId("default"),
    )
    damageAvoidChance = 0.15f
    exposureOffsetX = 0f
    exposureOffsetY = -2f
    exposureWidth = 24f
    exposureHeight = 18f
    projectile("cannon_shell") {
        renderBinding = ProjectileRenderBinding(
            RendererId("example:projectile"),
            RenderVariantId("shell"),
        )
        observerBinding = ProjectileObserverBinding(
            ProjectileObserverId("example:projectile-audio"),
            RenderVariantId("shell"),
        )
    }
    turret("main") {
        preFireDuration = 8.ticks
        preFireRenderBinding = PreFireRenderBinding(
            RendererId("example:pre-fire"),
            RenderVariantId("default"),
        )
        postFireDuration = 12.ticks
        postFireRenderBinding = PostFireRenderBinding(
            RendererId("example:post-fire"),
            RenderVariantId("default"),
        )
        observerBinding = TurretFireCycleObserverBinding(
            TurretFireCycleObserverId("example:fire-cycle"),
            RenderVariantId("default"),
        )
    }
    effect("native_dust") {
        renderBinding = EffectRenderBinding(
            RendererId("example:effect"),
            RenderVariantId("dust"),
        )
    }
}
```

> **注意**
> `projectile(name)`、`turret(name)`、`effect(name)` 侧车必须引用同一单位上已声明的原生 Section。

## Extension 伤害与命中扩展

`extension {}` 除了渲染绑定，还可以为单位和投射物注入 RWX 运行时伤害元数据，这些字段不会序列化成 INI：

| 字段                             | 作用域 | 说明                                                |
|----------------------------------|--------|-----------------------------------------------------|
| `damageAvoidChance`              | 单位   | `0f..1f`，单位闪避伤害的概率                        |
| `exposureOffsetX/Y`              | 单位   | 受击判定区域相对单位中心的偏移                      |
| `exposureWidth/Height`           | 单位   | 受击判定区域尺寸；必须与 `exposureOffset*` 成对设置 |
| `directDamageAmount`             | 投射物 | 覆盖原生直接伤害                                    |
| `areaDamageAmount`               | 投射物 | 覆盖原生范围伤害                                    |
| `directDamageHitRateBonus`       | 投射物 | 直接命中率加成                                      |
| `areaDamageHitRateBonus`         | 投射物 | 范围命中率加成                                      |
| `areaDamageExcludeDirectHit`     | 投射物 | 范围伤害是否排除直接命中的目标                      |
| `directDamageArmourIgnoreAmount` | 投射物 | 直接伤害忽略护甲量                                  |
| `areaDamageArmourIgnoreAmount`   | 投射物 | 范围伤害忽略护甲量                                  |
| `rayDamage`                      | 投射物 | 是否按射线命中结算（要求投射物为瞬时/Beam 类型）    |
| `rayDamageRange`                 | 投射物 | 射线长度                                            |
| `rayDamageWidth`                 | 投射物 | 射线宽度                                            |
| `rayDamageTargetWidthFactor`     | 投射物 | 目标命中宽度系数                                    |
| `rayDamageHitEffectOffsetFactor` | 投射物 | 命中效果沿射线偏移系数                              |
| `rayDamageSecondaryTargetTags`   | 投射物 | 射线可命中的额外目标 Tag 列表                       |

> **注意**
> `rayDamage` 只对瞬时投射物生效；为延迟弹道投射物开启会在声明验证阶段直接报错。

## Action

`action(name)` 和 `hiddenAction(name)` 直接暴露原生 Spec：

```kotlin
action("activate") {
    id = "activate"
    text = "Activate"
    displayType = listOf("action")
    iconImage = ResourcePath("assets/ui/activate.png").nativePath
    buildSpeed = "0"
    price = "energy=10"
    addActionCooldownTime = "12"
    alsoQueueAction = "activate_apply"
}
hiddenAction("activate_apply") {
    id = "activateApply"
    buildSpeed = "0.2s"
    addResources = "charge=1"
}
```

> **注意**
> Spec 的 `id` 是原生引擎命令 ID 的来源，原生引擎会在显式值前加 `c`，例如 `id = "activate"` 对应运行时 ActionId
> `cactivate`。省略 `id` 时生成依赖解析顺序的 `_N`，不应把 Section 后缀直接用作 `CUSTOM_ACTION` 的 `actionId`。

## 单位事件

`listen()` 注册 `UnitEventHandler`，在确定性模拟线程按声明顺序调用。`on()` 注册声明式 `UnitEventAction`（当前支持
`spawnEffect`）。

支持的 `UnitEvent`：

- `Created`、`CompleteAndActive`、`Destroyed`、`KilledUnit`、`QueuedUnitFinished`
- `QueueItemAdded`、`QueueItemCancelled`、`ActionCompleted`（可选 Action 过滤）
- `Teleported`、`TouchTargetSuccess`、`WaypointGivenByPlayer`、`TeamChanged`
- `TransportingUnit`、`TransportRemovedUnit`、`EnteredTransport`、`LeftTransport`
- `TookDamage`（可选伤害/Projectile Tag 过滤）
- `MessageReceived`（可选消息 Tag 过滤）
- `AttachmentRemoved`

`UnitEventContext` 提供：

- `api`、`tick`
- `self`、可选 `source`/`target`（只暴露 ID，通过 `api.unitWorld` 解析状态）
- `event: UnitEventKind`
- `payload: UnitEventPayload`

| `UnitEventPayload` | 字段                                           |
|--------------------|------------------------------------------------|
| `None`             | 无                                             |
| `Damage`           | `amount`、`damageTags`、`projectileTags`       |
| `Queue`            | `actionTags`                                   |
| `Action`           | `actionId`、可选 `targetPosition`              |
| `Waypoint`         | `commandType`、可选 `targetPosition`、`queued` |
| `Message`          | `tags`                                         |

示例：

```kotlin
val assign = UnitActionId("assign")

listen(
    UnitEventBindingId("assign_completed"),
    UnitEvent.ActionCompleted(setOf(assign)),
) { event ->
    val payload = event.payload as UnitEventPayload.Action
    val point = payload.targetPosition ?: return@listen
    val nearby = event.api.unitWorld.query(UnitQuery(center = point, radius = 80f))
    event.api.game.log(LogLevel.INFO, "Nearby units: ${nearby.size}")
}
```

`unitCondition {}` 构建 `UnitCondition`，支持 `HEALTH`、`HEALTH_RATIO`、`ENERGY`、`X`、`Y` 等数值属性，`DESTROYED`、`MOVING`、
`ATTACKING` 等布尔属性，以及 `and`、`or`、`!`、`allOf`、`anyOf` 组合。`recursionLimit` 默认为 1。
