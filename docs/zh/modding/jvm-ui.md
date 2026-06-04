# Kool UI 与 HUD

`api.ui` 提供游戏内菜单、HUD、模组窗口、消息和世界坐标选择。模组内容函数的 receiver 是 `de.fabmax.kool.modules.ui2.UiScope`
，可直接使用 Kool 的布局、控件和状态 API。

## 接口总览

```kotlin
interface Ui {
    fun addInGameMenuItem(menuId: Int? = null, text: LocalizedText, onClick: (id: Int) -> Unit)
    fun removeInGameMenuItem(menuId: Int)
    fun selectedUnits(): List<UnitRuntimeState>
    fun registerHud(id: HudId, order: Int = 0, content: UiScope.() -> Unit)
    fun unregisterHud(id: HudId)
    fun setNativeHudVisible(visible: Boolean)
    fun registerWindow(
        id: ModWindowId,
        title: LocalizedText,
        content: UiScope.(context: ModWindowContext, contentWidth: Dp) -> Unit,
    )
    fun openWindow(id: ModWindowId)
    fun closeWindow()
    fun refreshWindow()
    fun showMessage(message: LocalizedText, durationTicks: Int = 180)
    fun requestWorldPosition(
        onSelected: (WorldPosition) -> Unit,
        onCancelled: () -> Unit = {},
    ): WorldPositionSelection
}
```

`HudId` 和 `ModWindowId` 都要求小写的 `namespace:path`，同一 ID 全局只能注册一次。模组卸载时 RWX
会自动移除其注册的所有菜单项、HUD、窗口和坐标选择。

## 战场 HUD

`registerHud()` 把 Kool 内容函数加入战场 HUD 场景。多个图层先按 `order` 升序、再按 `HudId` 排序，后绘制的图层在上方。

```kotlin
api.ui.registerHud(HudId("example:battle"), order = 10) {
    val viewportWidth = Dp.fromPx(surface.viewportWidth.use())
    Box(width = Grow.Std, height = Grow.Std) {
        Text("Tick: ${api.game.tick}") {
            modifier
                .align(AlignmentX.End, AlignmentY.Top)
                .margin(Dp(16f))
        }
    }
}
```

Kool `MutableState` 被内容读取后，状态变化会触发重组。外部呈现数据变化时调用 `surface.triggerUpdate()`；需要逐帧读取时使用
`surface.onEachFrame { surface.triggerUpdate() }`。

> **注意**
> HUD 内容不能注册游戏内容、分配长期资源或提交模拟操作。

## 隐藏原生 HUD

`setNativeHudVisible(false)` 隐藏 RW 原生资源栏、单位 Action、迷你地图等，但保留世界渲染、镜头、框选和战场命令输入。只要有任意模组请求隐藏，原生
HUD 就保持隐藏。

```kotlin
override fun init() {
    api.ui.setNativeHudVisible(false)
    api.ui.registerHud(HudId("example:battle")) { BattleHud() }
}

override fun dispose() {
    api.ui.unregisterHud(HudId("example:battle"))
    api.ui.setNativeHudVisible(true)
}
```

## 已选中单位

`selectedUnits()` 返回本机玩家当前选中的存活单位快照，顺序与原生选择列表一致。

```kotlin
api.ui.registerHud(HudId("example:selection")) {
    val unit = api.ui.selectedUnits().singleOrNull()
    Text(unit?.let { "HP ${it.health.toInt()} / ${it.maxHealth.toInt()}" } ?: "")
}
```

> **注意**
> `selectedUnits()` 只读取本机 UI 选择，不属于确定性模拟状态。联机中其他客户端可能有不同选择，不能用于确定性决策。

## 模组窗口

`registerWindow()` 注册一个占用 RWX 菜单层的 Kool 页面。`openWindow()` 切换到该页面，`closeWindow()` 返回游戏，
`refreshWindow()` 触发重组。

`ModWindowContext` 提供：

- `api` — 拥有该窗口的 `Api`
- `locale` — 当前 BCP 47 locale tag
- `refresh()` — 重组当前窗口
- `close()` — 关闭窗口并返回游戏

```kotlin
val windowId = ModWindowId("example:status")

api.ui.registerWindow(windowId, LocalizedText.bilingual("Status", "状态")) {
    context, contentWidth ->
    Text("Tick: ${context.api.game.tick}") {
        modifier.width(contentWidth)
    }
}

api.ui.addInGameMenuItem(menuId = 42001, text = LocalizedText.bilingual("Status", "状态")) {
    api.ui.openWindow(windowId)
}
```

## 游戏内菜单

- `addInGameMenuItem(menuId?, text, onClick)` — 向原生游戏内菜单加入一项；省略 `menuId` 时 RWX 从 `26000` 开始分配，并把实际
  ID 传给 `onClick`
- `removeInGameMenuItem(menuId)` — 主动移除菜单项（卸载时自动清理）

## 世界坐标选择

`requestWorldPosition()` 启动一次世界坐标选择。左键确认，右键或 Escape 取消；全局同时只能有一个请求。返回的
`WorldPositionSelection.active` 表示请求是否仍在等待，`cancel()` 可主动取消。

```kotlin
api.ui.requestWorldPosition(
    onSelected = { position ->
        api.game.requestTeamAction(TeamActionId("example:activate_scan"), position)
    },
    onCancelled = {
        api.ui.showMessage(LocalizedText.literal("Selection cancelled"))
    },
)
```

> **注意**
> HUD、窗口、菜单和坐标选择回调不能直接修改模拟。Mod 自定义同步操作应调用 `requestTeamAction()`
> ，再在已注册的队伍动作处理器中验证并执行；原生单位命令通过 UI 事件回调调用 `api.commands` 提交。

## 资源与线程

Kool UI content 运行在呈现线程。`Texture2d` 等 GPU 资源须在 `dispose()` 调用 `release()`，不要在每次重组中重复加载纹理。
