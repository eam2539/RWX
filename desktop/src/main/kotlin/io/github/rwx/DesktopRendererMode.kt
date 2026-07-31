package io.github.rwx

import io.github.rwx.render.RendererMode

internal enum class DesktopRendererMode(
    override val id: String,
) : RendererMode{
    Slick(
        "desktop-slick"
    ),
    //KOOL
}