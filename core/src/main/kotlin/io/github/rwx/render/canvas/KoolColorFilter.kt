package io.github.rwx.render.canvas

open class KoolColorFilter

class KoolBlendColorFilter(
    @JvmField val color: Int,
    @JvmField val blendMode: KoolCanvasBlendMode,
) : KoolColorFilter()

class KoolMultiplyAddColorFilter(
    @JvmField val multiplyColor: Int,
    @JvmField val addColor: Int,
) : KoolColorFilter() {
    fun usesLegacyAdditiveBlend(): Boolean =
        multiplyColor != 0 && multiplyColor != -1
}
