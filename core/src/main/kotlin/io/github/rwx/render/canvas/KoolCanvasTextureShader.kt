package io.github.rwx.render.canvas

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.a
import de.fabmax.kool.modules.ksl.lang.rgb
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.pipeline.PipelineConfig
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.scene.vertexAttrib
import de.fabmax.kool.util.Color

class KoolCanvasTextureShader(
    pipelineConfig: PipelineConfig,
    premultipliedAlpha: Boolean = false,
    val multipliesRgbByAlpha: Boolean = false,
) : KslShader(Model(premultipliedAlpha, multipliesRgbByAlpha), pipelineConfig) {
    var colorMap: Texture2d? by texture2d(TEXTURE_UNIFORM, noTexture)

    private class Model(
        premultipliedAlpha: Boolean,
        multipliesRgbByAlpha: Boolean,
    ) : KslProgram("RWX Canvas Texture Shader") {
        init {
            val texCoords = interStageFloat2()
            val vertexColor = interStageFloat4()

            vertexStage {
                main {
                    texCoords.input set vertexAttrib(VertexLayouts.TexCoord.texCoord)
                    vertexColor.input set vertexAttrib(VertexLayouts.Color.color)

                    val vertexPos by float4Value(vertexAttrib(VertexLayouts.Position.position), 1f)
                    outPosition set mvpMatrix().matrix * vertexPos
                }
            }
            fragmentStage {
                main {
                    val texColor = float4Var(sampleTexture(texture2d(TEXTURE_UNIFORM), texCoords.output))
                    val tint = float4Var(vertexColor.output)
                    val alpha = float1Var(texColor.a * tint.a)
                    if (premultipliedAlpha) {
                        colorOutput(texColor.rgb * tint.rgb * tint.a, alpha)
                    } else if (multipliesRgbByAlpha) {
                        colorOutput(texColor.rgb * tint.rgb * alpha, alpha)
                    } else {
                        colorOutput(texColor.rgb * tint.rgb, alpha)
                    }
                }
            }
        }
    }

    companion object {
        private const val TEXTURE_UNIFORM = "uColorMap"
        private val noTexture = SingleColorTexture(Color.WHITE)

        init {
            KoolSystem.getContextOrNull()?.onShutdown += { noTexture.release() }
        }
    }
}
