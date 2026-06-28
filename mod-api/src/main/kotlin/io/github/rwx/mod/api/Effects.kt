package io.github.rwx.mod.api

import io.github.rwx.mod.api.specs.EffectSpec

interface Effects {
    fun registerEffect(definition: EffectDefinition)
}

data class RgbaColor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255,
) {
    init {
        require(red in 0..255 && green in 0..255 && blue in 0..255 && alpha in 0..255) {
            "RGBA color channels must be between 0 and 255"
        }
    }

    companion object {
        fun rgb(red: Int, green: Int, blue: Int) = RgbaColor(red, green, blue)
    }
}

enum class EffectPriority {
    VERY_LOW,
    LOW,
    HIGH,
    VERY_HIGH,
    CRITICAL,
}

enum class EffectBlendMode {
    ALPHA,
    ADDITIVE,
}

enum class EffectImageAnchor {
    TOP,
    CENTER,
    BOTTOM,
}

data class EffectScale(
    val xFrom: Float,
    val yFrom: Float,
    val xTo: Float,
    val yTo: Float,
)

data class EffectDefinition(
    val id: EffectId,
    val image: ResourcePath? = null,
    val life: Ticks = Ticks(20),
    val lifeRandom: Ticks = Ticks.ZERO,
    val spawnChance: Float = 1f,
    val color: RgbaColor = RgbaColor(255, 255, 255),
    val scaleFrom: Float = 1f,
    val scaleTo: Float = scaleFrom,
    val scale: EffectScale = EffectScale(scaleFrom, scaleFrom, scaleTo, scaleTo),
    val alpha: Float = 1f,
    val fadeIn: Ticks = Ticks.ZERO,
    val fadeOut: Boolean = true,
    val delay: Ticks = Ticks.ZERO,
    val delayRandom: Ticks = Ticks.ZERO,
    val offset: EffectOffset = EffectOffset(),
    val motion: EffectMotion = EffectMotion(),
    val directionOffsetDegrees: Float = 0f,
    val directionOffsetRandomDegrees: Float = 0f,
    val alwaysStartDirectionAtZero: Boolean = false,
    val animation: EffectAnimation? = null,
    val renderBinding: EffectRenderBinding? = null,
    val attachedToSource: Boolean = false,
    val liveAfterSourceDies: Boolean = true,
    val drawUnderUnits: Boolean = false,
    val blendMode: EffectBlendMode = EffectBlendMode.ALPHA,
    val imageAnchor: EffectImageAnchor = EffectImageAnchor.CENTER,
    val priority: EffectPriority = EffectPriority.HIGH,
    val emittedEffects: List<EffectId> = emptyList(),
    val emittedEffectsOnDeath: List<EffectId> = emptyList(),
    val ini: IniSpecValues = IniSpecValues(emptyMap()),
)

data class EffectOffset(
    val relativeX: Float = 0f,
    val relativeY: Float = 0f,
    val height: Float = 0f,
    val absoluteX: Float = 0f,
    val absoluteY: Float = 0f,
)

data class EffectMotion(
    val relativeXPerTick: Float = 0f,
    val relativeYPerTick: Float = 0f,
    val heightPerTick: Float = 0f,
    val absoluteXPerTick: Float = 0f,
    val absoluteYPerTick: Float = 0f,
    val rotationDegreesPerTick: Float = 0f,
)

data class EffectAnimation(
    val totalFrames: Int,
    val frameWidth: Int? = null,
    val frameHeight: Int? = null,
    val startFrame: Int = 0,
    val endFrame: Int = totalFrames - 1,
    val startFrameRandomAdd: Int = 0,
    val pingPong: Boolean = false,
    val looping: Boolean = false,
    val frameDuration: Ticks = Ticks(1),
    val frameDurationRandom: Ticks = Ticks.ZERO,
)

fun Effects.effect(
    id: EffectId,
    configure: EffectBuilder.() -> Unit,
) {
    val builder = EffectBuilder(id)
    builder.configure()
    registerEffect(builder.buildDefinition())
}

@UnitDsl
class EffectBuilder internal constructor(private val id: EffectId) {
    var image: ResourcePath? = null
    var life: Ticks = Ticks(20)
    var lifeRandom: Ticks = Ticks.ZERO
    var spawnChance: Float = 1f
    var color: RgbaColor = RgbaColor(255, 255, 255)
    var scaleFrom: Float = 1f
    var scaleTo: Float? = null
    var alpha: Float = 1f
    var fadeIn: Ticks = Ticks.ZERO
    var fadeOut: Boolean = true
    var delay: Ticks = Ticks.ZERO
    var delayRandom: Ticks = Ticks.ZERO
    var directionOffsetDegrees: Float = 0f
    var directionOffsetRandomDegrees: Float = 0f
    var alwaysStartDirectionAtZero: Boolean = false
    var attachedToSource: Boolean = false
    var liveAfterSourceDies: Boolean = true
    var drawUnderUnits: Boolean = false
    var blendMode: EffectBlendMode = EffectBlendMode.ALPHA
    var imageAnchor: EffectImageAnchor = EffectImageAnchor.CENTER
    internal var renderBinding: EffectRenderBinding? = null
    var priority: EffectPriority = EffectPriority.HIGH
    private var scaleXFrom: Float? = null
    private var scaleYFrom: Float? = null
    private var scaleXTo: Float? = null
    private var scaleYTo: Float? = null
    private var offset = EffectOffset()
    private var motion = EffectMotion()
    private var animation: EffectAnimation? = null
    private val emittedEffects = mutableListOf<EffectId>()
    private val emittedEffectsOnDeath = mutableListOf<EffectId>()
    private val ini = EffectSpec()

    fun offset(configure: EffectOffsetBuilder.() -> Unit) {
        offset = EffectOffsetBuilder(offset).apply(configure).build()
    }

    fun motion(configure: EffectMotionBuilder.() -> Unit) {
        motion = EffectMotionBuilder(motion).apply(configure).build()
    }

    fun scale(configure: EffectScaleBuilder.() -> Unit) {
        val builder = EffectScaleBuilder(scaleXFrom, scaleYFrom, scaleXTo, scaleYTo).apply(configure)
        scaleXFrom = builder.xFrom
        scaleYFrom = builder.yFrom
        scaleXTo = builder.xTo
        scaleYTo = builder.yTo
    }

    fun animation(configure: EffectAnimationBuilder.() -> Unit) {
        animation = EffectAnimationBuilder().apply(configure).build()
    }

    fun emit(effectId: EffectId) {
        emittedEffects += effectId
    }

    fun emit(effectIds: Iterable<EffectId>) {
        emittedEffects += effectIds
    }

    fun emitOnDeath(effectId: EffectId) {
        emittedEffectsOnDeath += effectId
    }

    fun emitOnDeath(effectIds: Iterable<EffectId>) {
        emittedEffectsOnDeath += effectIds
    }

    fun extension(configure: EffectExtensionBuilder.() -> Unit) {
        EffectExtensionBuilder(this).configure()
    }

    internal fun buildDefinition() = EffectDefinition(
        id = id,
        image = image,
        life = life,
        lifeRandom = lifeRandom,
        spawnChance = spawnChance,
        color = color,
        scaleFrom = scaleFrom,
        scaleTo = scaleTo ?: scaleFrom,
        scale = EffectScale(
            xFrom = scaleXFrom ?: scaleFrom,
            yFrom = scaleYFrom ?: scaleFrom,
            xTo = scaleXTo ?: scaleTo ?: scaleXFrom ?: scaleFrom,
            yTo = scaleYTo ?: scaleTo ?: scaleYFrom ?: scaleFrom,
        ),
        alpha = alpha,
        fadeIn = fadeIn,
        fadeOut = fadeOut,
        delay = delay,
        delayRandom = delayRandom,
        offset = offset,
        motion = motion,
        directionOffsetDegrees = directionOffsetDegrees,
        directionOffsetRandomDegrees = directionOffsetRandomDegrees,
        alwaysStartDirectionAtZero = alwaysStartDirectionAtZero,
        animation = animation,
        renderBinding = renderBinding,
        attachedToSource = attachedToSource,
        liveAfterSourceDies = liveAfterSourceDies,
        drawUnderUnits = drawUnderUnits,
        blendMode = blendMode,
        imageAnchor = imageAnchor,
        priority = priority,
        emittedEffects = emittedEffects.toList(),
        emittedEffectsOnDeath = emittedEffectsOnDeath.toList(),
        ini = IniSpecCodec.encode(ini),
    )
}

@UnitDsl
class EffectExtensionBuilder internal constructor(private val owner: EffectBuilder) {
    var renderBinding: EffectRenderBinding?
        get() = owner.renderBinding
        set(value) {
            owner.renderBinding = value
        }
}

@UnitDsl
class EffectScaleBuilder internal constructor(
    xFrom: Float?,
    yFrom: Float?,
    xTo: Float?,
    yTo: Float?,
) {
    var xFrom: Float? = xFrom
    var yFrom: Float? = yFrom
    var xTo: Float? = xTo
    var yTo: Float? = yTo
}

@UnitDsl
class EffectOffsetBuilder internal constructor(offset: EffectOffset) {
    var relativeX: Float = offset.relativeX
    var relativeY: Float = offset.relativeY
    var height: Float = offset.height
    var absoluteX: Float = offset.absoluteX
    var absoluteY: Float = offset.absoluteY

    internal fun build() = EffectOffset(relativeX, relativeY, height, absoluteX, absoluteY)
}

@UnitDsl
class EffectMotionBuilder internal constructor(motion: EffectMotion) {
    var relativeXPerTick: Float = motion.relativeXPerTick
    var relativeYPerTick: Float = motion.relativeYPerTick
    var heightPerTick: Float = motion.heightPerTick
    var absoluteXPerTick: Float = motion.absoluteXPerTick
    var absoluteYPerTick: Float = motion.absoluteYPerTick
    var rotationDegreesPerTick: Float = motion.rotationDegreesPerTick

    internal fun build() = EffectMotion(
        relativeXPerTick,
        relativeYPerTick,
        heightPerTick,
        absoluteXPerTick,
        absoluteYPerTick,
        rotationDegreesPerTick,
    )
}

@UnitDsl
class EffectAnimationBuilder internal constructor() {
    var totalFrames: Int = 1
    var frameWidth: Int? = null
    var frameHeight: Int? = null
    var startFrame: Int = 0
    var endFrame: Int? = null
    var startFrameRandomAdd: Int = 0
    var pingPong: Boolean = false
    var looping: Boolean = false
    var frameDuration: Ticks = Ticks(1)
    var frameDurationRandom: Ticks = Ticks.ZERO

    internal fun build() = EffectAnimation(
        totalFrames = totalFrames,
        frameWidth = frameWidth,
        frameHeight = frameHeight,
        startFrame = startFrame,
        endFrame = endFrame ?: totalFrames - 1,
        startFrameRandomAdd = startFrameRandomAdd,
        pingPong = pingPong,
        looping = looping,
        frameDuration = frameDuration,
        frameDurationRandom = frameDurationRandom,
    )
}
