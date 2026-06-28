package io.github.rwx.render

sealed interface WorldRenderCommand {
    data class DrawUnit(
        val unitId: Int,
        val x: Float,
        val y: Float,
        val isSelected: Boolean,
    ) : WorldRenderCommand
}
