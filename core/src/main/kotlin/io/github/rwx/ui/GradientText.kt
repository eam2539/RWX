package io.github.rwx.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.PipelineConfig
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.scene.vertexAttrib
import de.fabmax.kool.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface GradientTextScope : TextScope {
    override val modifier: GradientTextModifier
}

open class GradientTextModifier(surface: UiSurface) : TextModifier(surface) {
    var gradientStartColor: Color by property(Color.WHITE)
    var gradientEndColor: Color by property(Color.WHITE)
    var gradientStart: Vec2f by property(Vec2f.ZERO)
    var gradientEnd: Vec2f? by property(null)
}

fun <T : GradientTextModifier> T.gradientColors(start: Color, end: Color): T {
    gradientStartColor = start
    gradientEndColor = end
    return this
}

fun <T : GradientTextModifier> T.gradientLine(start: Vec2f, end: Vec2f?): T {
    gradientStart = start
    gradientEnd = end
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun UiScope.GradientText(
    text: String = "",
    scopeName: String? = null,
    block: GradientTextScope.() -> Unit,
): GradientTextScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val textNd = uiNode.createChild(scopeName, GradientTextNode::class, GradientTextNode.factory)
    textNd.modifier.text(text)
    textNd.block()
    return textNd
}

open class GradientTextNode(parent: UiNode?, surface: UiSurface) :
    UiNode(parent, surface),
    GradientTextScope {

    override val modifier = GradientTextModifier(surface)

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = CachedTextGeometry(this)
    private val textBounds = MutableVec4f()
    private val innerWidthPxState = mutableStateOf(0f)
    private var renderTxt = ""
    private val layerKey = UniqueId.nextId("gradient-text")

    private val gradientShader = GradientMsdfUiShader()
    private val mesh = Mesh(
        IndexedVertexList(UiTextVertexLayout, usage = Usage.DYNAMIC),
        name = "GradientTextMesh",
    ).apply {
        isCastingShadow = false
        shader = gradientShader
    }
    private val builder = MeshBuilder(mesh.geometry).apply {
        UiSurface.run { setupUiBuilder() }
    }

    override fun measureContentSize(ctx: KoolContext) {
        surface.applyFontScale(modifier.font, ctx)

        val textMetrics = textCache.getTextMetrics(modifier.text, modifier.font)
        renderTxt = modifier.text

        if (modifier.isWrapText && modifier.width != FitContent) {
            val availableWidth = innerWidthPxState.use()
            if (availableWidth > 0f && availableWidth < textMetrics.width) {
                renderTxt = wrapText(modifier.text, modifier.font, availableWidth)
                textCache.getTextMetrics(renderTxt, modifier.font)
            }
        } else if (modifier.isWrapText && modifier.width == FitContent) {
            logW { "Suspicious gradient text modifier: isWrapText = true and width = FitContent (text: \"${modifier.text}\")" }
        }

        textBounds.x = textMetrics.paddingStart
        textBounds.y = textMetrics.yBaseline
        textBounds.z = textMetrics.width
        textBounds.w = textMetrics.height

        val measuredWidth = when (val modWidth = modifier.width) {
            is Dp -> modWidth.px
            else -> textBounds.z + paddingStartPx + paddingEndPx
        }
        val measuredHeight = when (val modHeight = modifier.height) {
            is Dp -> modHeight.px
            else -> textBounds.w + paddingTopPx + paddingBottomPx
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        super.setBounds(minX, minY, maxX, maxY)
        innerWidthPxState.set(innerWidthPx)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        val font = modifier.font
        require(font is MsdfFont) {
            "GradientText requires an MsdfFont; got ${font::class.simpleName}"
        }
        gradientShader.fontMap = font.data.map
        gradientShader.startColor = modifier.gradientStartColor
        gradientShader.endColor = modifier.gradientEndColor

        val textMetrics = textCache.textMetrics
        textProps.apply {
            this.font = font
            text = renderTxt
            isYAxisUp = false

            val textWidth = textBounds.z
            val textHeight = textBounds.w
            val oriX = textBounds.x + when (modifier.textAlignX) {
                AlignmentX.Start -> paddingStartPx
                AlignmentX.Center -> (widthPx - textWidth) / 2f
                AlignmentX.End -> widthPx - textWidth - paddingEndPx
            }
            val oriY = textBounds.y + when (modifier.textAlignY) {
                AlignmentY.Top -> paddingTopPx
                AlignmentY.Center -> (heightPx - textHeight) / 2f
                AlignmentY.Bottom -> heightPx - textHeight - paddingBottomPx
            }
            origin.set(oriX, oriY, 0f)
        }

        val textLeft = leftPx + textProps.origin.x - textMetrics.paddingStart
        val textTop = topPx + textProps.origin.y - textMetrics.yBaseline
        val textWidth = textMetrics.width.coerceAtLeast(1f)
        val textHeight = textMetrics.height.coerceAtLeast(1f)
        val gradientStart = modifier.gradientStart
        val gradientEnd = modifier.gradientEnd ?: Vec2f(textWidth, textHeight)
        gradientShader.start = Vec2f(textLeft + gradientStart.x, textTop + gradientStart.y)
        gradientShader.end = Vec2f(textLeft + gradientEnd.x, textTop + gradientEnd.y)

        builder.clear()
        val clip = if (modifier.clipToBounds) clipBoundsPx else NO_CLIP
        textCache.addTextGeometry(builder.geometry, textProps, Color.WHITE, textClip = clip)

        val layer = surface.getMeshLayer(modifier.zLayer)
        layer.addCustomLayer(key = layerKey) {
            Node("GradientTextLayer[$nodeIndex]").apply { addNode(mesh) }
        }
    }

    private fun wrapText(text: String, font: Font, availableWidthPx: Float): String {
        val wrappedTxt = StringBuilder()
        var lineWidth = 0f
        var lineStartIdx = 0
        var lastLineBreakIdx = 0
        var lastFallbackLineBreakIdx = 0
        for (i in text.indices) {
            val c = text[i]
            if (c.isWhitespace()) {
                lastLineBreakIdx = i
            } else if (!c.isLetterOrDigit() || (c.isLowerCase() && text.getOrNull(i + 1)?.isUpperCase() == true)) {
                lastFallbackLineBreakIdx = i + 1
            }
            val cw = font.charWidth(c, textProps.enforceSameWidthDigits)
            if (i == lineStartIdx || lineWidth + cw <= availableWidthPx) {
                lineWidth += cw
            } else {
                if (wrappedTxt.isNotEmpty()) {
                    wrappedTxt.append('\n')
                }
                lineStartIdx = when {
                    lastLineBreakIdx > lineStartIdx -> {
                        wrappedTxt.append(text.substring(lineStartIdx, lastLineBreakIdx))
                        lastLineBreakIdx + 1
                    }

                    lastFallbackLineBreakIdx > lineStartIdx -> {
                        wrappedTxt.append(text.substring(lineStartIdx, lastFallbackLineBreakIdx))
                        lastFallbackLineBreakIdx
                    }

                    else -> {
                        wrappedTxt.append(text.substring(lineStartIdx, i))
                        i
                    }
                }
                lineWidth = 0f
                for (j in lineStartIdx..i) {
                    lineWidth += font.charWidth(text[j], textProps.enforceSameWidthDigits)
                }
            }
        }
        if (lineStartIdx < text.length) {
            if (wrappedTxt.isNotEmpty()) {
                wrappedTxt.append('\n')
            }
            wrappedTxt.append(text.substring(lineStartIdx))
        }
        return wrappedTxt.toString()
    }

    companion object {
        val factory: (UiNode, UiSurface) -> GradientTextNode =
            { parent, surface -> GradientTextNode(parent, surface) }
    }
}

private class GradientMsdfUiShader(
    model: Model = Model(),
    pipelineCfg: PipelineConfig = PipelineConfig(
        cullMethod = CullMethod.NO_CULLING,
        blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA,
        depthTest = DepthCompareOp.ALWAYS,
    ),
) : KslShader(model, pipelineCfg) {
    var fontMap by texture2d("tFontMap")
    var pxRangeScale by uniform1f("uPxRange", 1f)
    var startColor: Color by uniformColor("uGradientStartColor", Color.WHITE)
    var endColor: Color by uniformColor("uGradientEndColor", Color.WHITE)
    var start: Vec2f by uniform2f("uGradientStart", Vec2f.ZERO)
    var end: Vec2f by uniform2f("uGradientEnd", Vec2f(1f, 1f))

    class Model : KslProgram("Gradient MSDF UI2 Shader") {
        init {
            val fgColor = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val glowColor = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val msdfProps = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val clipBounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val screenPos = interStageFloat2()
            val uv = interStageFloat2()

            vertexStage {
                main {
                    fgColor.input set vertexAttrib(UiTextVertexLayout.color)
                    glowColor.input set vertexAttrib(UiTextVertexLayout.glowColor)
                    msdfProps.input set vertexAttrib(UiTextVertexLayout.msdfProps)
                    clipBounds.input set vertexAttrib(UiTextVertexLayout.clip)
                    uv.input set vertexAttrib(UiTextVertexLayout.texCoord)

                    val mvp = mat4Var(mvpMatrix().matrix)
                    val vertexPos = float4Var(float4Value(vertexAttrib(UiTextVertexLayout.position), 1f))
                    screenPos.input set vertexPos.xy
                    outPosition set mvp * vertexPos
                }
            }

            fragmentStage {
                val median3 = functionFloat1("median") {
                    val p = paramFloat3("p")
                    body {
                        max(min(p.x, p.y), min(max(p.x, p.y), p.z))
                    }
                }

                val computeOpacity = functionFloat1("computeOpacity") {
                    val msdf = paramFloat3("msdf")
                    val props = paramFloat4("props")

                    body {
                        val sd = float1Var(median3(msdf))
                        val dist = float1Var(sd - 0.5f.const + props.y)
                        val p = step(props.z, dist)
                        dist set dist + p * 2f.const * (props.z - dist)

                        val screenPxDistance = float1Var(props.x * dist)
                        clamp(screenPxDistance + 0.5f.const, 0f.const, 1f.const)
                    }
                }

                main {
                    val fontMap = texture2d("tFontMap")

                    `if`(
                        any(screenPos.output lt clipBounds.output.xy) or
                                any(screenPos.output gt clipBounds.output.zw)
                    ) {
                        discard()
                    }.`else` {
                        val msdfVals = float4Var(sampleTexture(fontMap, uv.output, 0f.const))
                        val color = float4Var(fgColor.output)
                        val pxRange = float1Var(msdfProps.output.x * uniformFloat1("uPxRange"))
                        val weight = msdfProps.output.y

                        val dist = float1Var(msdfVals.a - 0.5f.const + weight)
                        val screenPxDistance = float1Var(pxRange * dist)
                        val sdfOpa = float1Var(clamp(screenPxDistance + 0.5f.const, 0f.const, 1f.const))

                        val msdfOpa = float1Var(computeOpacity(msdfVals.rgb, msdfProps.output))
                        val wMsdf = float1Var(smoothStep(5f.const, 10f.const, pxRange))
                        color.a *= sdfOpa * (1f.const - wMsdf) + msdfOpa * wMsdf

                        val gradientStart = float2Var(uniformFloat2("uGradientStart"))
                        val gradientVector = float2Var(uniformFloat2("uGradientEnd") - gradientStart)
                        val gradientLenSq = float1Var(max(dot(gradientVector, gradientVector), 0.0001f.const))
                        val gradientT = float1Var(
                            clamp(
                                dot(screenPos.output - gradientStart, gradientVector) / gradientLenSq,
                                0f.const,
                                1f.const
                            )
                        )
                        val gradientColor = float4Var(
                            mix(
                                uniformFloat4("uGradientStartColor"),
                                uniformFloat4("uGradientEndColor"),
                                gradientT
                            )
                        )
                        color.a *= gradientColor.a
                        color.rgb set gradientColor.rgb * color.a

                        val glow = float4Var(glowColor.output)
                        glow.a *= smoothStep(0f.const, 1f.const, msdfVals.a * 1.5f.const) * (1f.const - color.a)

                        colorOutput(color.rgb + glow.rgb * glow.a, color.a + glow.a)
                    }
                }
            }
        }
    }
}
