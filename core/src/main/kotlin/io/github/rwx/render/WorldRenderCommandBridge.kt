package io.github.rwx.render

data class WorldSceneUnitAnchor(
    val unitId: Int,
    val x: Float,
    val y: Float,
    val isSelected: Boolean,
)

class WorldRenderCommandBridge {
    var unitAnchors: List<WorldSceneUnitAnchor> = emptyList()
        private set

    fun applyCommands(commands: List<WorldRenderCommand>): List<WorldSceneUnitAnchor> {
        unitAnchors = commands.mapNotNull { command ->
            when (command) {
                is WorldRenderCommand.DrawUnit -> WorldSceneUnitAnchor(
                    unitId = command.unitId,
                    x = command.x,
                    y = command.y,
                    isSelected = command.isSelected,
                )
            }
        }
        return unitAnchors
    }
}
