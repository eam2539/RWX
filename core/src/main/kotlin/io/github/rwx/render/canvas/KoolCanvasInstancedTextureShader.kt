package io.github.rwx.render.canvas

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.PipelineConfig
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.scene.instanceAttrib
import de.fabmax.kool.scene.vertexAttrib
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

object KoolCanvasTextureInstanceLayout : Struct("RWXCanvasTextureInstance", MemoryLayout.TightlyPacked) {
    val rect = float4("instattr_rect")
    val uv = float4("instattr_uv")
}

object KoolCanvasAffineTextureInstanceLayout : Struct("RWXCanvasAffineTextureInstance", MemoryLayout.TightlyPacked) {
    val originAxisX = float4("instattr_origin_axis_x")
    val axisY = float4("instattr_axis_y")
    val uv = float4("instattr_uv")
}

class KoolCanvasInstancedTextureShader(
    pipelineConfig: PipelineConfig,
    premultipliedAlpha: Boolean = false,
    val multipliesRgbByAlpha: Boolean = false,
) : KslShader(Model(premultipliedAlpha, multipliesRgbByAlpha), pipelineConfig) {
    var colorMap: Texture2d? by texture2d(TEXTURE_UNIFORM, noTexture)

    private class Model(
        premultipliedAlpha: Boolean,
        multipliesRgbByAlpha: Boolean,
    ) : KslProgram("RWX Canvas Instanced Texture Shader") {
        init {
            val texCoords = interStageFloat2()

            vertexStage {
                main {
                    val corner = vertexAttrib(VertexLayouts.Position.position)
                    val rect = instanceAttrib(KoolCanvasTextureInstanceLayout.rect)
                    val uv = instanceAttrib(KoolCanvasTextureInstanceLayout.uv)

                    val x = rect.x + (rect.z - rect.x) * corner.x
                    val y = rect.y + (rect.w - rect.y) * corner.y
                    texCoords.input set float2Value(
                        uv.x + (uv.z - uv.x) * corner.x,
                        uv.y + (uv.w - uv.y) * corner.y,
                    )

                    outPosition set mvpMatrix().matrix * float4Value(x, y, 0f.const, 1f.const)
                }
            }
            fragmentStage {
                main {
                    val texColor = sampleTexture(texture2d(TEXTURE_UNIFORM), texCoords.output)
                    if (premultipliedAlpha) {
                        colorOutput(texColor.rgb, texColor.a)
                    } else if (multipliesRgbByAlpha) {
                        colorOutput(texColor.rgb * texColor.a, texColor.a)
                    } else {
                        colorOutput(texColor.rgb, texColor.a)
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

class KoolCanvasAffineInstancedTextureShader(
    pipelineConfig: PipelineConfig,
    premultipliedAlpha: Boolean = false,
    val multipliesRgbByAlpha: Boolean = false,
) : KslShader(Model(premultipliedAlpha, multipliesRgbByAlpha), pipelineConfig) {
    var colorMap: Texture2d? by texture2d(TEXTURE_UNIFORM, noTexture)

    private class Model(
        premultipliedAlpha: Boolean,
        multipliesRgbByAlpha: Boolean,
    ) : KslProgram("RWX Canvas Affine Instanced Texture Shader") {
        init {
            val texCoords = interStageFloat2()

            vertexStage {
                main {
                    val corner = vertexAttrib(VertexLayouts.Position.position)
                    val originAxisX = instanceAttrib(KoolCanvasAffineTextureInstanceLayout.originAxisX)
                    val axisY = instanceAttrib(KoolCanvasAffineTextureInstanceLayout.axisY)
                    val uv = instanceAttrib(KoolCanvasAffineTextureInstanceLayout.uv)

                    val x = originAxisX.x + originAxisX.z * corner.x + axisY.x * corner.y
                    val y = originAxisX.y + originAxisX.w * corner.x + axisY.y * corner.y
                    texCoords.input set float2Value(
                        uv.x + (uv.z - uv.x) * corner.x,
                        uv.y + (uv.w - uv.y) * corner.y,
                    )

                    outPosition set mvpMatrix().matrix * float4Value(x, y, 0f.const, 1f.const)
                }
            }
            fragmentStage {
                main {
                    val texColor = sampleTexture(texture2d(TEXTURE_UNIFORM), texCoords.output)
                    if (premultipliedAlpha) {
                        colorOutput(texColor.rgb, texColor.a)
                    } else if (multipliesRgbByAlpha) {
                        colorOutput(texColor.rgb * texColor.a, texColor.a)
                    } else {
                        colorOutput(texColor.rgb, texColor.a)
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
