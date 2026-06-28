package io.github.rwx.mod

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.UiScope
import io.github.rwx.i18n.I18n
import io.github.rwx.mod.api.*
import io.github.rwx.ui.CoreUiEventQueue

object ModUiRegistry {
    private const val MENU_ID_BASE = 26000

    private val items = linkedMapOf<Int, RegisteredMenuItem>()
    private val callbacks = linkedMapOf<Int, (Int) -> Unit>()
    private val hudLayers = linkedMapOf<String, RegisteredHudLayer>()
    private val nativeHudHiddenOwners = linkedSetOf<ApiImpl>()
    private val windows = linkedMapOf<String, RegisteredWindow>()
    private var activeWindowId: String? = null
    private var worldPositionSelection: RegisteredWorldPositionSelection? = null

    @Volatile
    private var hudRevision = 0L

    @JvmStatic
    fun addInGameMenuItem(owner: ApiImpl, menuId: Int?, label: LocalizedText, callback: (Int) -> Unit): Int {
        val resolvedId = menuId ?: generateSequence(MENU_ID_BASE) { it + 1 }.first { it !in items }
        items[resolvedId] = RegisteredMenuItem(owner, resolvedId, label)
        callbacks[resolvedId] = callback
        return resolvedId
    }

    @JvmStatic
    fun removeInGameMenuItem(menuId: Int) {
        items.remove(menuId)
        callbacks.remove(menuId)
    }

    @Synchronized
    fun registerHud(owner: ApiImpl, id: HudId, order: Int, content: UiScope.() -> Unit) {
        check(id.value !in hudLayers) { "Mod HUD is already registered: ${id.value}" }
        hudLayers[id.value] = RegisteredHudLayer(owner, id, order, content)
        hudRevision++
    }

    @Synchronized
    fun unregisterHud(owner: ApiImpl, id: HudId) {
        val registration = hudLayers[id.value] ?: return
        check(registration.owner === owner) { "Mod HUD is owned by another mod: ${id.value}" }
        hudLayers.remove(id.value)
        hudRevision++
    }

    @Synchronized
    fun setNativeHudVisible(owner: ApiImpl, visible: Boolean) {
        if (visible) {
            nativeHudHiddenOwners.remove(owner)
        } else {
            nativeHudHiddenOwners.add(owner)
        }
    }

    @JvmStatic
    @Synchronized
    fun isNativeHudVisible(): Boolean = nativeHudHiddenOwners.isEmpty()

    @Synchronized
    fun activeHudLayers(): List<ActiveHudLayer> = hudLayers.values
        .sortedWith(compareBy<RegisteredHudLayer> { it.order }.thenBy { it.id.value })
        .map { ActiveHudLayer(it.id, it.order, it.content) }

    @Synchronized
    fun hasActiveHudLayers(): Boolean = hudLayers.isNotEmpty()

    fun hudRevision(): Long = hudRevision

    @JvmStatic
    fun clear() {
        items.clear()
        callbacks.clear()
        if (hudLayers.isNotEmpty()) hudRevision++
        hudLayers.clear()
        nativeHudHiddenOwners.clear()
        windows.clear()
        activeWindowId = null
        worldPositionSelection = null
    }

    @JvmStatic
    fun getInGameMenuItems(): List<InGameMenuItem> = items.values.map { item ->
        InGameMenuItem(
            item.menuId,
            item.label.resolve(I18n.currentLocale.toLanguageTag()),
        )
    }

    @JvmStatic
    fun handleInGameMenuSelection(menuId: Int): Boolean {
        val callback = callbacks[menuId] ?: return false
        callback(menuId)
        return true
    }

    @Synchronized
    fun registerWindow(
        owner: ApiImpl,
        id: ModWindowId,
        title: LocalizedText,
        content: UiScope.(context: ModWindowContext, contentWidth: Dp) -> Unit,
    ) {
        check(id.value !in windows) { "Mod window is already registered: ${id.value}" }
        windows[id.value] = RegisteredWindow(owner, title, content)
    }

    @Synchronized
    fun openWindow(id: ModWindowId) {
        check(id.value in windows) { "Mod window is not registered: ${id.value}" }
        activeWindowId = id.value
        GameEngine.getInstance().gameUI?.isDraggingSelection = false
        CoreUiEventQueue.requestInGameModWindow()
    }

    @Synchronized
    fun closeWindow() {
        deactivateWindow()
        CoreUiEventQueue.requestInGameModWindowBack()
    }

    @Synchronized
    fun deactivateWindow() {
        activeWindowId = null
    }

    fun refreshWindow() {
        CoreUiEventQueue.requestInGameModWindowRefresh()
    }

    @Synchronized
    fun requestWorldPosition(
        owner: ApiImpl,
        onSelected: (WorldPosition) -> Unit,
        onCancelled: () -> Unit,
    ): WorldPositionSelection {
        check(worldPositionSelection == null) { "A world-position selection is already active" }
        val registration = RegisteredWorldPositionSelection(owner, onSelected, onCancelled)
        worldPositionSelection = registration
        return SelectionHandle(registration)
    }

    @JvmStatic
    @Synchronized
    fun hasActiveWorldPositionSelection(): Boolean = worldPositionSelection != null

    @JvmStatic
    fun selectWorldPosition(position: WorldPosition): Boolean {
        val registration = synchronized(this) {
            worldPositionSelection?.also { worldPositionSelection = null }
        } ?: return false
        registration.onSelected(position)
        return true
    }

    @JvmStatic
    fun cancelWorldPositionSelection(): Boolean {
        val registration = synchronized(this) {
            worldPositionSelection?.also { worldPositionSelection = null }
        } ?: return false
        registration.onCancelled()
        return true
    }

    @Synchronized
    fun activeWindow(): ActiveWindow? {
        val registration = activeWindowId?.let(windows::get) ?: return null
        return ActiveWindow(
            title = registration.title,
            content = registration.content,
            context = WindowContext(registration.owner),
        )
    }

    @Synchronized
    fun unregister(owner: ApiImpl) {
        val menuIds = items.filterValues { it.owner === owner }.keys
        menuIds.forEach {
            items.remove(it)
            callbacks.remove(it)
        }
        val windowIds = windows.filterValues { it.owner === owner }.keys
        if (activeWindowId in windowIds) activeWindowId = null
        windowIds.forEach(windows::remove)
        val removedHud = hudLayers.values.removeIf { it.owner === owner }
        if (removedHud) hudRevision++
        nativeHudHiddenOwners.remove(owner)
        if (worldPositionSelection?.owner === owner) worldPositionSelection = null
    }

    private data class RegisteredMenuItem(
        val owner: ApiImpl,
        val menuId: Int,
        val label: LocalizedText,
    )

    private data class RegisteredHudLayer(
        val owner: ApiImpl,
        val id: HudId,
        val order: Int,
        val content: UiScope.() -> Unit,
    )

    private data class RegisteredWindow(
        val owner: ApiImpl,
        val title: LocalizedText,
        val content: UiScope.(context: ModWindowContext, contentWidth: Dp) -> Unit,
    )

    private data class RegisteredWorldPositionSelection(
        val owner: ApiImpl,
        val onSelected: (WorldPosition) -> Unit,
        val onCancelled: () -> Unit,
    )

    private class SelectionHandle(
        private val registration: RegisteredWorldPositionSelection,
    ) : WorldPositionSelection {
        override val active: Boolean
            get() = synchronized(ModUiRegistry) {
                worldPositionSelection === registration
            }

        override fun cancel() {
            val callback = synchronized(ModUiRegistry) {
                if (worldPositionSelection !== registration) return
                worldPositionSelection = null
                registration.onCancelled
            }
            callback()
        }
    }

    data class ActiveWindow(
        val title: LocalizedText,
        val content: UiScope.(context: ModWindowContext, contentWidth: Dp) -> Unit,
        val context: ModWindowContext,
    )

    data class ActiveHudLayer(
        val id: HudId,
        val order: Int,
        val content: UiScope.() -> Unit,
    )

    private class WindowContext(private val owner: ApiImpl) : ModWindowContext {
        override val api = owner
        override val locale: String get() = I18n.currentLocale.toLanguageTag()
        override fun refresh() = refreshWindow()
        override fun close() = closeWindow()
    }

    data class InGameMenuItem(
        val menuId: Int,
        val label: String
    )
}
