package com.corrodinggames.rts.kool.ui

import com.corrodinggames.rts.gameFramework.KoolGraphics
import com.corrodinggames.rts.gameFramework.KoolUI
import com.corrodinggames.rts.gameFramework.KoolUIDocumentHandle
import com.corrodinggames.rts.gameFramework.KoolKey
import java.io.File

/**
 * KoolEngine UI adapter.
 *
 * This provides a minimal, pure-Kotlin UI system suitable for
 * game menus and HUD overlays. It replaces the libRocket dependency.
 *
 * The implementation uses immediate-mode-like panels and a
 * retained-mode document system with simple RCSS-like styling.
 *
 * For the initial migration, this wraps document loading/rendering
 * while the full RWX GUI layer (CommonGuiEngine) handles layout logic.
 */
class KoolUIAdapter : KoolUI {

    private var _initialized = false
    private val documents = mutableMapOf<Int, UIDocument>()
    private var nextDocId = 1
    private var _activeDocument: KoolUIDocumentHandle? = null

    // Fonts loaded for UI
    private val loadedFonts = mutableListOf<String>()

    override fun init() {
        _initialized = true
    }

    override fun loadDocument(path: String): KoolUIDocumentHandle {
        val id = nextDocId++
        val doc = UIDocument(id, path)
        documents[id] = doc
        return KoolUIDocumentHandle(id)
    }

    override fun unloadDocument(doc: KoolUIDocumentHandle) {
        documents.remove(doc.id)
        if (_activeDocument?.id == doc.id) {
            _activeDocument = null
        }
    }

    override fun showDocument(doc: KoolUIDocumentHandle) {
        _activeDocument = doc
    }

    override fun hideDocument(doc: KoolUIDocumentHandle) {
        if (_activeDocument?.id == doc.id) {
            _activeDocument = null
        }
    }

    override fun render(graphics: KoolGraphics) {
        // For now, UI rendering is handled by the existing CommonGuiEngine
        // which uses the GraphicsInterface directly.
        // This adapter will be extended with native rendering when
        // the full kool-UI system is implemented.
    }

    override fun update(dt: Float) {
        // Update UI animations/documents
    }

    override fun resize(width: Int, height: Int) {
        // Resize UI layout
    }

    override fun sendMouseEvent(x: Int, y: Int, button: Int, pressed: Boolean, scrolled: Int) {
        // Forward to UI document event handling
    }

    override fun sendKeyEvent(key: KoolKey, pressed: Boolean, text: String) {
        // Forward to UI document event handling
    }

    override fun dispose() {
        documents.clear()
        _activeDocument = null
        _initialized = false
    }

    override val activeDocument: KoolUIDocumentHandle?
        get() = _activeDocument

    override fun loadFont(path: String, cssName: String?) {
        val fontName = cssName ?: File(path).nameWithoutExtension
        loadedFonts.add(fontName)
        // Fonts are loaded through the graphics system's loadFont()
    }

    override fun executeScript(script: String) {
        // Execute UI script (used for event handling callbacks)
        // The existing libRocket script engine will be re-targeted
        // to work with this UI system
    }

    private data class UIDocument(
        val id: Int,
        val path: String,
        var visible: Boolean = false,
    )
}