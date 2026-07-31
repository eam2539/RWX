package io.github.rwx.render.canvas

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.scene.vertexAttrib
import de.fabmax.kool.util.Color

class KoolDisplacementShader(
    pipelineConfig: PipelineConfig,
) : KslShader(Model(), pipelineConfig) {
    var displacementMap: Texture2d? by texture2d(DISPLACEMENT_MAP_UNIFORM, noTexture)
    var screenBase: Texture2d? by texture2d(SCREEN_BASE_UNIFORM, noTexture)
    var viewportSize: Vec2f by uniform2f(VIEWPORT_SIZE_UNIFORM, Vec2f.ONES)
    var offsetBy: Float by uniform1f(OFFSET_BY_UNIFORM, 0f)

    private class Model : KslProgram("Displacement Shader") {
        init {
            val texCoords = interStageFloat2()
            val vertexColor = interStageFloat4()
            val screenPos = interStageFloat2()

            vertexStage {
                main {
                    texCoords.input set vertexAttrib(VertexLayouts.TexCoord.texCoord)
                    vertexColor.input set vertexAttrib(VertexLayouts.Color.color)

                    val vertexPos by float4Value(vertexAttrib(VertexLayouts.Position.position), 1f)
                    val viewport = float2Var(uniformFloat2(VIEWPORT_SIZE_UNIFORM))
                    screenPos.input set float2Value(
                        vertexPos.x + (viewport.x * 0.5f.const),
                        (viewport.y * 0.5f.const) - vertexPos.y,
                    )
                    outPosition set mvpMatrix().matrix * vertexPos
                }
            }
            fragmentStage {
                main {
                    val viewport = float2Var(uniformFloat2(VIEWPORT_SIZE_UNIFORM))
                    val displacement = float4Var(sampleTexture(texture2d(DISPLACEMENT_MAP_UNIFORM), texCoords.output))
                    val screenUv = float2Var(screenPos.output / viewport)
                    val screenOffset = float2Var(
                        uniformFloat1(OFFSET_BY_UNIFORM) *
                                (displacement.xy - float2Value(128f / 255f, 128f / 255f)) *
                                displacement.a *
                                vertexColor.output.a,
                    )
                    screenUv set saturate(screenUv + screenOffset)

                    val screenPixel = float4Var(sampleTexture(texture2d(SCREEN_BASE_UNIFORM), screenUv))
                    colorOutput(screenPixel.rgb, screenPixel.a)
                }
            }
        }
    }

    companion object {
        private const val DISPLACEMENT_MAP_UNIFORM = "uDisplacementMap"
        private const val SCREEN_BASE_UNIFORM = "uScreenBase"
        private const val VIEWPORT_SIZE_UNIFORM = "uViewportSize"
        private const val OFFSET_BY_UNIFORM = "uOffsetBy"
        private val noTexture = SingleColorTexture(Color.WHITE)
        val defaultPipeline = PipelineConfig(
            blendMode = BlendMode.BLEND_MULTIPLY_ALPHA,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS,
            isWriteDepth = false,
        )

        init {
            KoolSystem.getContextOrNull()?.onShutdown += { noTexture.release() }
        }
    }
}
