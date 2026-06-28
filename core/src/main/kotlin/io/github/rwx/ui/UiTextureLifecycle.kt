package io.github.rwx.ui

/**
 * Drops process-level UI texture references before an Android Kool context is destroyed.
 *
 * Android can recreate the Activity while keeping the process alive. Without clearing these
 * caches, the next Activity sees Texture2d instances backed by the previous EGL context and skips
 * their preload because they still report isLoaded, leaving SVGs and previews rendered as black.
 */
fun invalidateUiTextureCaches() {
    invalidateUiIconTextureCache()
    invalidateMapPreviewTextureCache()
    invalidateResourceBrowserPreviewTextureCache()
}
