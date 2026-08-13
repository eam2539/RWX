package io.github.rwx.mod.api.specs


/**
 * Properties for [effect_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class EffectSpec(
    // Example: drawType: normal
    /**
     * Defaults to normal. Changes how effect is rendered.  Options: normal|displacement   (displacement only shows when shader effects are turned on)
     */
    var drawType: String = "normal",
    // Example: life: 70
    /**
     * Defaults 200. Time till effect is removed. Set low as possible to reduce effect overhead.
     */
    var life: Float? = null,
    // Example: lifeRandom: 12
    /** Random offset life by +/- this value */
    var lifeRandom: Float? = null,
    // Example: alsoEmitEffects: CUSTOM:extraSparks*2
    /**
     * Create more effects when created, useful for meta-effects. Note: other 'alsoEmitEffects' on created effects are ignored.
     */
    var alsoEmitEffects: String? = null,
    // Example: alsoEmitEffectsOnDeath: CUSTOM:finalPuff
    /** Create these effects when life runs out. */
    var alsoEmitEffectsOnDeath: String? = null,
    // Example: ifSpawnFailsEmitEffects: CUSTOM:greenSparks*4
    /** If 'spawnChance' for this effects fails then emit these effects instead */
    var ifSpawnFailsEmitEffects: String? = null,
    // Example: alsoPlaySound: meow.wav:0.5
    /**
     * Plays sound upon spawning the effect. Use OGG or WAV audio files. Appending a colon with a float value sets the volume
     */
    var alsoPlaySound: String? = null,
    // Example: createWhenOffscreen: true
    /** Defaults false. When true, effect is created when the unit is offscreen */
    var createWhenOffscreen: Boolean? = null,
    // Example: createWhenZoomedOut: false
    /**
     * Defaults true. Effect is created even when the map is zoomed out. Set to false for improving performance
     */
    var createWhenZoomedOut: Boolean? = null,
    // Example: createWhenOverLiquid: false
    /** Defaults true. Effect is created when the unit is over water */
    var createWhenOverLiquid: Boolean? = null,
    // Example: createWhenOverLand: false
    /** Defaults true. Effect is created when the unit is over land */
    var createWhenOverLand: Boolean? = null,
    // Example: spawnChance: 1
    /** Default 1. If less than 1 effect only has a random chance of being created */
    var spawnChance: Float? = null,
    // Example: showInFog: true
    /** Default false. When true, effect is still visible on fog of war */
    var showInFog: Boolean? = null,
    // Example: delayedStartTimer: 2s
    /** Hide for x time before showing and updating effect. */
    var delayedStartTimer: Float? = null,
    // Example: liveAfterAttachedDies: false
    /** Defaults false when attachedToUnit is being used */
    var liveAfterAttachedDies: Boolean? = null,
    // Example: priority: critical
    /**
     * Defaults to high. verylow/low/high/veryhigh/critical. Takes effect when too many effects are being shown at once.
     */
    var priority: String? = null,

    // ===== Movement =====
    // Example: attachedToUnit: true
    /**
     * Attach to unit or projectile that created this effect. Will move with this object. Useful for thrust effects
     */
    var attachedToUnit: Boolean? = null,
    // Example: alwayStartDirAtZero: true
    /** Ignore source/attached unit dir */
    var alwayStartDirAtZero: Boolean? = null,
    // Example: atmospheric: true
    /** Apply drag to slow this effect down and add small wind effects */
    var atmospheric: Boolean? = null,
    // Example: physics: true
    /** Fall to ground and bounces. Needs height to take effect. */
    var physics: Boolean? = null,
    // Example: physicsGravity: 0.5
    /** Defaults to 1. height speed acceleration when physics: true */
    var physicsGravity: Float? = null,
    // Example: xOffsetRelative: 2
    /**
     * Offset starting effect position. Relative to direction of attached turret, projectile, unit
     */
    var xOffsetRelative: Float? = null,
    // Example: yOffsetRelative: 2
    /**
     * Offset starting effect position. Relative to direction of attached turret, projectile, unit
     */
    var yOffsetRelative: Float? = null,
    // Example: xOffsetRelativeRandom: 4
    /** Random offset by +/- this value */
    var xOffsetRelativeRandom: Float? = null,
    // Example: yOffsetRelativeRandom: 4
    /** Random offset by +/- this value */
    var yOffsetRelativeRandom: Float? = null,
    // Example: xOffsetAbsolute: 2
    /**
     * Offset starting effect by position ignoring direction of attached turret, projectile, unit
     */
    var xOffsetAbsolute: Float? = null,
    // Example: yOffsetAbsolute: 2
    /**
     * Offset starting effect by position ignoring direction of attached turret, projectile, unit
     */
    var yOffsetAbsolute: Float? = null,
    // Example: xOffsetAbsoluteRandom: 5
    /** Random offset by +/- this value */
    var xOffsetAbsoluteRandom: Float? = null,
    // Example: yOffsetAbsoluteRandom: 5
    /** Random offset by +/- this value */
    var yOffsetAbsoluteRandom: Float? = null,
    // Example: xSpeedRelative: 2
    /**
     * Moves the effect sprite on relative horizontal position with specified speed. Negative will move left relative to source, positive will move right relative to source. Useful for thrust effects for moving units
     */
    var xSpeedRelative: Float? = null,
    // Example: ySpeedRelative: 3
    /**
     * Moves the effect sprite on relative vertical position with specified speed. Negative will move down relative to source, positive will move up relative to source. Useful for thrust effects for moving units
     */
    var ySpeedRelative: Float? = null,
    // Example: xSpeedRelativeRandom: 2
    /** Randomly change by -value to value */
    var xSpeedRelativeRandom: Float? = null,
    // Example: ySpeedRelativeRandom: 2
    /** Randomly change by -value to value */
    var ySpeedRelativeRandom: Float? = null,
    // Example: xSpeedAbsolute: 5
    /**
     * Moves the effect sprite on absolute horizontal position with specified speed. Negative will move left relative to map, positive will move right relative to map. Useful for smoke effects on structures
     */
    var xSpeedAbsolute: Float? = null,
    // Example: ySpeedAbsolute: 5
    /**
     * Moves the effect sprite on absolute vertical position with specified speed. Negative will move down relative to map, positive will move up relative to map. Useful for smoke effects on structures
     */
    var ySpeedAbsolute: Float? = null,
    // Example: xSpeedAbsoluteRandom: 2
    /** Randomly change by -value to value */
    var xSpeedAbsoluteRandom: Float? = null,
    // Example: ySpeedAbsoluteRandom: 2
    /** Randomly change by -value to value */
    var ySpeedAbsoluteRandom: Float? = null,
    // Example: hOffset: 4
    /** height offset from source. May be mistakenly confused with yOffsetAbsolute */
    var hOffset: Float? = null,
    // Example: hOffsetRandom: 5
    /** Randomly change by -value to value */
    var hOffsetRandom: Float? = null,
    // Example: hSpeed: 1
    /** Sets the speed to change the height of the effect */
    var hSpeed: Float? = null,
    // Example: hSpeedRandom: 1
    /** Randomly change by -value to value */
    var hSpeedRandom: Float? = null,
    // Example: dirOffset: 43
    /** Sets the static direction of the effect */
    var dirOffset: Float? = null,
    // Example: dirOffsetRandom: 50
    /** Randomly change by -value to value */
    var dirOffsetRandom: Float? = null,
    // Example: dirSpeed: 2
    /** Sets the rotation speed of the effect */
    var dirSpeed: Float? = null,
    // Example: dirSpeedRandom: 3
    /** Randomly change by -value to value */
    var dirSpeedRandom: Float? = null,

    // ===== Graphics =====
    // Example: frameIndex: 0
    /** Use a specific frame from strip index */
    var frameIndex: Int? = null,
    // Example: frameIndexRandom: true
    /** Use random frame from strip index */
    var frameIndexRandom: String? = null,
    // Example: stripIndex: projectiles
    /**
     * A built-in image set to use. Cannot be used with custom image. Options: effects/explode_big/light_50/flame/effects/effects2/projectiles/projectiles2/explode_bits
     */
    var stripIndex: String? = null,
    // Example: image: whitePuff.png
    /** Custom image file to use. Cannot be used with stripIndex. */
    var image: String? = null,
    // Example: imageShadow: AUTO
    /** Custom image file to use for shadows */
    var imageShadow: String? = null,
    // Example: imageAnchor: center
    /** Vertical anchor for the custom image. Options: top|center|bottom */
    var imageAnchor: String? = null,
    // Example: blendMode: additive
    /** Sprite blending mode. Options: alpha|additive */
    var blendMode: String? = null,
    // Example: scaleTo: 2
    /** Defaults to 1. Resizes the unit into the specified scale */
    var scaleTo: Float? = null,
    // Example: scaleFrom: 4
    /** Defaults to 1. Resizes the unit from the specified scale */
    var scaleFrom: Float? = null,
    // Example: scaleXFrom: 1
    /** Horizontal scale at effect start */
    var scaleXFrom: Float? = null,
    // Example: scaleYFrom: 1
    /** Vertical scale at effect start */
    var scaleYFrom: Float? = null,
    // Example: scaleXTo: 1
    /** Horizontal scale at effect end */
    var scaleXTo: Float? = null,
    // Example: scaleYTo: 1
    /** Vertical scale at effect end */
    var scaleYTo: Float? = null,
    // Example: color: #ff00ff
    /**
     * Defaults #FFFFFFFF. Changes the color of the effect sprite. Use pure white sprite for most use cases.
     */
    var color: Color? = null,
    // Example: teamColorRatio: 1
    /** Sets team coloration between 0-1. Team color is dependent from source unit */
    var teamColorRatio: String? = null,
    // Example: drawUnderUnits: false
    /** Renders the effect under all units */
    var drawUnderUnits: Boolean? = null,
    // Example: fadeInTime: 2s
    /** Fade alpha from 0% to 100% for this time at start */
    var fadeInTime: Float? = null,
    // Example: fadeOut: 4s
    /**
     * Fade alpha from 100% to 0% based on life. Set alpha is higher than 1 to delay fade
     */
    var fadeOut: Boolean? = null,
    // Example: alpha: 1
    /** Capped between 0-1. Can be set higher than 1 to delay fadeOut effects */
    var alpha: String? = null,
    // Example: shadow: false
    /** True to draw a shadow. Forced true if imageShadow is used */
    var shadow: String? = null,
    // Example: pivotOffset: 20
    /** Similar to dirOffset, this will also rotate relative keys and child elements */
    var pivotOffset: Float? = null,
    // Example: pivotOffsetRandom: 34
    /** Offsets pivot between +/- of specified value */
    var pivotOffsetRandom: Float? = null,

    // ===== Animation =====
    // Example: total_frames: 10
    /**
     * Total frames of 'image', used with animation or frameIndex. Only needed with custom images
     */
    var totalFrames: Int? = null,
    // Example: frame_width: 40
    /** Width of each frame in a custom image strip */
    var frameWidth: Int? = null,
    // Example: frame_height: 40
    /** Height of each frame in a custom image strip */
    var frameHeight: Int? = null,
    // Example: animateFrameStart: 0
    /** Starting frame for the animation */
    var animateFrameStart: Int? = null,
    // Example: animateFrameStartRandomAdd: 2
    /** Adds a random value from zero through this value to the starting frame */
    var animateFrameStartRandomAdd: Int? = null,
    // Example: animateFrameEnd: 3
    /** Ending frame for the animation */
    var animateFrameEnd: Int? = null,
    // Example: animateFramePingPong: true
    /** If true, animation will go back and forth within the frames */
    var animateFramePingPong: Int? = null,
    // Example: animateFrameSpeed: 10
    /** Sets the animation speed. The lower the value, the slower it plays */
    var animateFrameSpeed: Float? = null,
    // Example: animateFrameSpeedRandom: 20
    /** Randomizes how fast the effect animation plays */
    var animateFrameSpeedRandom: Float? = null,
    // Example: animateFrameLooping: true
    /** Defaults false. When false effect is removed when animation ends */
    var animateFrameLooping: Boolean? = null
)
