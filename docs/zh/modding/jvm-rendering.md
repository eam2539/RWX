# 渲染与效果

## Graphics API

`api.graphics` 提供：

- `registerTexture(id, path, options)` — 注册纹理，`TextureOptions` 支持 `premultiplyAlpha`、
  `TextureFilter.NEAREST/LINEAR`、`TextureWrap.CLAMP/REPEAT`
- `registerProjectileRenderer(id, renderer)` — 注册投射物/Beam 自定义渲染器
- `registerPreFireRenderer(id, renderer)` — 注册开火前摇渲染器（瞄准起手 → 开火）
- `registerPostFireRenderer(id, renderer)` — 注册开火后摇渲染器（开火后的恢复窗口）
- `registerEffectRenderer(id, renderer)` — 注册效果自定义渲染器
- `registerUnitRenderer(id, renderer)` — 注册单位实例渲染器
- `registerShader(definition)` — 注册 Shader
- `setUnitShader(unitId, shaderId?)` — 给单位 ID 绑定或解除 Shader
- `registerAnimation` — 已公开，宿主尚未应用
- `addRenderPass` — 已公开，宿主尚未应用

## 单位实例渲染器

```kotlin
val rendererId = RendererId("example:status-overlay")

api.graphics.registerTexture(
    TextureId("example:status-ring"),
    ResourcePath("assets/status-ring.png"),
    TextureOptions(filter = TextureFilter.LINEAR),
)
api.graphics.registerUnitRenderer(rendererId) { context, canvas ->
    if (context.layer != UnitRenderLayer.UNDER_UNIT) return@registerUnitRenderer
    if (context.destroyed) return@registerUnitRenderer
    canvas.drawTexture(
        TextureId("example:status-ring"),
        context.centerX, context.centerY,
        maxOf(context.drawWidth, context.drawHeight) * 1.25f,
        maxOf(context.drawWidth, context.drawHeight) * 1.25f,
        blendMode = RenderBlendMode.ALPHA,
    )
}

api.units.unit(UnitId("example:scout")) {
    graphics { image = "ROOT:assets/scout.png" }
    extension { renderBinding = UnitRenderBinding(rendererId, RenderVariantId("default")) }
}
```

`UnitRenderContext` 字段：

| 字段                      | 说明                        |
|---------------------------|-----------------------------|
| `instanceId`              | 运行时单位实例 ID           |
| `definitionId`            | 绑定的 Mod 单位声明         |
| `teamId`                  | 原生队伍 ID                 |
| `centerX`、`centerY`      | 单位渲染中心屏幕坐标        |
| `drawWidth`、`drawHeight` | 原生 hull 绘制尺寸          |
| `collisionRadius`         | 原生碰撞半径                |
| `rotationDegrees`         | 单位逻辑朝向角度            |
| `destroyed`               | 当前绘制路径中是否已摧毁    |
| `layer`                   | `UNDER_UNIT` 或 `OVER_UNIT` |
| `variantId`               | 绑定中的样式变体            |

宿主对每个相关单位、每个绘制层各调用一次渲染器。渲染器和绑定必须属于同一个 Mod，Renderer ID 在所有已加载 Mod 之间唯一。

> **注意**
> 渲染回调运行在渲染路径，不能查询或修改 `UnitWorld`、提交命令或创建持久模拟状态。可以读取更新路径发布的不可变快照。

## 投射物渲染器

```kotlin
api.graphics.registerProjectileRenderer(RendererId("example:trail")) { context, canvas ->
    val dx = context.endX - context.startX
    val dy = context.endY - context.startY
    val centerX = (context.startX + context.endX) * 0.5f
    val centerY = (context.startY + context.endY) * 0.5f
    val length = kotlin.math.sqrt(dx * dx + dy * dy)
    val angle = Math.toDegrees(kotlin.math.atan2(dy, dx).toDouble()).toFloat()
    canvas.save()
    try {
        canvas.rotate(angle, centerX, centerY)
        canvas.drawTexture(
            TextureId("example:trail"), centerX, centerY, length, 8f,
            blendMode = RenderBlendMode.ALPHA,
        )
    } finally {
        canvas.restore()
    }
}
```

`ProjectileRenderContext` 提供起终点、`age`、`remaining`、`drawSize` 和 `variant`。`PreFireRenderContext` 与
`PostFireRenderContext` 结构相同，额外提供 `duration`；区别在于 `endX`/`endY` 的语义：前摇期间跟随实时目标，后摇期间固定为
开火瞬间的实际弹着点（目标即使中途死亡也保持不变），后摇的 `startX`/`startY` 则是开火的炮口位置。

## RenderCanvas

- `save()` / `restore()`
- `rotate(degrees, pivotX, pivotY)`
- `scale(scaleX, scaleY, pivotX, pivotY)` — 负值可镜像
- `drawTexture(textureId, centerX, centerY, width, height, tint, opacity, blendMode)`
- `drawTextureRegion(textureId, source: TextureRegion, centerX, centerY, width, height, tint, opacity, blendMode)` —
  只绘制纹理的归一化子区域（`source` 各通道取 `0f..1f`，需满足 `left < right`、`top < bottom`）；省略时回退到
  `drawTexture` 绘制整张纹理

`TextureRegion(left, top, right, bottom)` 的坐标是相对整张纹理的归一化 UV 值，适合从图集中截取贴图。

`RenderBlendMode` 为 `ALPHA` 或 `ADDITIVE`。不要在回调结束后持有 `RenderCanvas`。

## Effect 渲染器

`EffectRenderContext` 提供 `origin`、`age`/`lifetime`、旋转、`alpha`、X/Y 缩放、`shadowPass`、`variant`，以及可空的
`sourceSpeedFraction`（当前速度除以最大移动速度，无可移动源时为 `null`）。

## 投射物与开火周期观察器

观察器用于声音、状态机和一次性副作用，与每帧绘制的 Renderer 分离：

```kotlin
api.game.registerTurretFireCycleObserver(TurretFireCycleObserverId("example:audio")) { context ->
    when (context.phase) {
        TurretFireCyclePhase.PRE_FIRE_STARTED -> { /* 前摇开始 */
        }
        TurretFireCyclePhase.PRE_FIRE_UPDATED -> { /* 前摇逐帧 */
        }
        TurretFireCyclePhase.PRE_FIRE_ENDED -> { /* 前摇结束 */
        }
        TurretFireCyclePhase.POST_FIRE_STARTED -> { /* 后摇开始 */
        }
        TurretFireCyclePhase.POST_FIRE_UPDATED -> { /* 后摇逐帧 */
        }
        TurretFireCyclePhase.POST_FIRE_ENDED -> { /* 后摇结束 */
        }
    }
}
```

开火周期把一次射击分为弹出弹药之前的前摇窗口和之后的恢复（后摇）窗口，两个窗口各自运行
`STARTED / UPDATED / ENDED` 序列，共享同一个 `TurretFireCycleInstanceId`。只关心其中一个窗口的模组可以按相位前缀匹配 （
`phase.isPreFire` / `phase.isPostFire`）。前后摇任一窗口配置为零时长时，对应相位不会到达。

`TurretFireCycleContext` 与投射物观察器一样包含实例 ID、`phase`、起终点、当前/上一帧 `age` 和 `remaining`、`variant`， 另含
`durationTicks`。前摇期间 `endX`/`endY` 跟随实时目标；后摇期间固定为开火瞬间的弹着点。`ageTicks` 和
`remainingTicks` 都在当前窗口内度量，因此每次射击都会各跑一轮 `0..duration`。

> **注意**
> 对象 ID 可能复用，必须连同 `generation` 一起作为实例键。观察器运行在游戏更新路径，应保持确定性、短时执行且不阻塞。

## Shader

`ShaderDefinition` 包含 `id`、可选 vertex、必填 fragment、uniform 列表和 `ShaderTarget` 集合。`ShaderTarget` 包括 `UNIT`、
`PROJECTILE`、`EFFECT`、`TERRAIN`、`UI`、`POST_PROCESS`。

当前已落地单位 Shader 注册和绑定；`addRenderPass` 尚未执行渲染通道。
