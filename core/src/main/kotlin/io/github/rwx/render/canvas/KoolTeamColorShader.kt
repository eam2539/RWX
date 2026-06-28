package io.github.rwx.render.canvas

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.scene.vertexAttrib
import de.fabmax.kool.util.Color

class KoolTeamColorShader(
    mode: KoolCanvasTeamColorMode,
    pipelineConfig: PipelineConfig,
    val multipliesRgbByAlpha: Boolean = false,
) : KslShader(Model(mode, multipliesRgbByAlpha), pipelineConfig) {
    var colorMap: Texture2d? by texture2d(TEXTURE_UNIFORM, noTexture)
    var teamColor: Vec4f by uniform4f(TEAM_COLOR_UNIFORM, Vec4f(1f, 1f, 1f, 1f))
    var teamColorAmount: Float by uniform1f(TEAM_COLOR_AMOUNT_UNIFORM, 1f)

    private class Model(
        mode: KoolCanvasTeamColorMode,
        multipliesRgbByAlpha: Boolean,
    ) : KslProgram("RWX Team Color Shader") {
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
                    val sourceRgb = float3Var(texColor.rgb)
                    val team = float4Var(uniformFloat4(TEAM_COLOR_UNIFORM))
                    val outRgb = float3Var(sourceRgb)

                    when (mode) {
                        KoolCanvasTeamColorMode.PureGreen -> applyPureGreen(sourceRgb, team, outRgb)
                        KoolCanvasTeamColorMode.HueAdd -> applyHueAdd(sourceRgb, team, outRgb)
                        KoolCanvasTeamColorMode.HueShift -> applyHueShift(sourceRgb, team, outRgb)
                    }

                    val alpha = float1Var(texColor.a * vertexColor.output.a)
                    val rgb = outRgb * vertexColor.output.rgb
                    if (multipliesRgbByAlpha) {
                        colorOutput(rgb * alpha, alpha)
                    } else {
                        colorOutput(rgb, alpha)
                    }
                }
            }
        }

        private fun KslScopeBuilder.applyPureGreen(
            sourceRgb: KslExprFloat3,
            team: KslExprFloat4,
            outRgb: KslVarFloat3,
        ) {
            val redBlueDelta = float1Var(abs(sourceRgb.r - sourceRgb.b))
            `if`((sourceRgb.g gt 0f.const) and (redBlueDelta le 0.04f.const)) {
                outRgb set float3Value(sourceRgb.r, sourceRgb.r, sourceRgb.r) + (team.rgb * (sourceRgb.g - sourceRgb.r))
            }
        }

        private fun KslScopeBuilder.applyHueAdd(
            sourceRgb: KslExprFloat3,
            team: KslExprFloat4,
            outRgb: KslVarFloat3,
        ) {
            outRgb set sourceRgb + (team.rgb * uniformFloat1(TEAM_COLOR_AMOUNT_UNIFORM))
        }

        private fun KslScopeBuilder.applyHueShift(
            sourceRgb: KslExprFloat3,
            team: KslExprFloat4,
            outRgb: KslVarFloat3,
        ) {
            val minChannel = float1Var(min(min(sourceRgb.r, sourceRgb.g), sourceRgb.b))
            val maxDelta = float1Var(
                max(
                    max(abs(sourceRgb.r - sourceRgb.g), abs(sourceRgb.g - sourceRgb.b)),
                    abs(sourceRgb.b - sourceRgb.r)
                )
            )
            `if`(maxDelta gt (15f / 256f).const) {
                outRgb set float3Value(minChannel, minChannel, minChannel) + (team.rgb * maxDelta)
            }
        }
    }

    companion object {
        private const val TEXTURE_UNIFORM = "uColorMap"
        private const val TEAM_COLOR_UNIFORM = "uTeamColor"
        private const val TEAM_COLOR_AMOUNT_UNIFORM = "uTeamColorAmount"
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
