package io.github.rwx.mod.api.specs


/**
 * Properties for [graphics] section
 *
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class GraphicsSpec(
    // ===== Common Keys =====
    // Example: image: body.png
    /** File path to png image. */
    var image: Texture = "units/missing_unit/missing.png",
    // Example: imageSmoothing: true
    /** Enables linear filtering when loading unit textures. */
    var imageSmoothing: Boolean = false,
    // Example: imageSmoothingWhenZoomedIn: true
    /** Keeps linear filtering enabled while the camera is zoomed in. */
    var imageSmoothingWhenZoomedIn: Boolean = false,
    // Example: image_back: floor.png
    /**
     * An optional image drawn behind other units. Useful for factories that units exit
     */
    var imageBack: Texture? = null,
    // Example: image_shield: forcefield.png
    /** Image to show as a custom shield */
    var imageShield: Texture? = null,
    // Example: image_wreak: ded.png
    /** Image to use when unit dies. Can be NONE to leave no wreak */
    var imageWreak: Texture? = null,
    // Example: image_offsetX: 21
    /** Use this to adjust the graphics of a unit if it is too far off one side */
    var imageOffsetX: Int? = null,
    // Example: image_offsetY: 41
    /** Use this to adjust the graphics of a unit if it is too far off one side */
    var imageOffsetY: Int? = null,
    // Example: image_offsetH: 25
    /** Use this to adjust the height of the graphic, especially when using decals */
    var imageOffsetH: Int? = null,
    // Example: image_floatingPointSize: 10
    /**
     * Fixes of by 1 pixel sizing for images with widths and/or height that has odd value
     */
    var imageFloatingPointSize: Int? = null,
    // Example: isVisible: false
    /** If false will hide the unit. */
    var isVisible: LogicBoolean? = null,
    // Example: isVisibleToEnemies: false
    /**
     * Only visible to player and allies when false. Recommend with showOnMinimapToEnemies. Useful for stealth units.
     */
    var isVisibleToEnemies: Boolean? = null,
    // Example: teamColoringMode: disabled
    /**
     * How pixels are used for team coloring, options: pureGreen (default), hueAdd, hueShift, disabled
     */
    var teamColoringMode: String? = null,
    // Example: scaleImagesTo: 1
    /**
     * Resize image to fit this value in pixels.  Effects leg, and shadow images as well.
     */
    var scaleImagesTo: Float? = null,
    // Example: imageScale: 1
    /** Resize image. Defaults to 1. Effects leg, and shadow images as well. */
    var imageScale: Float? = null,
    // Example: drawLayer: bottom
    /**
     * Land units normally default to ground or ground2 if transport. Options: wreaks, underwater, bottom, ground, ground2, experimentals, air, top
     */
    var drawLayer: String? = null,
    // Example: whenBeingBuiltMakeTransparentTill: 0.4
    /**
     * How long the transparent effect is applied to incomplete units, set to 0 to disable completely. Default: 1
     */
    var whenBeingBuiltMakeTransparentTill: Float? = null,
    // Example: icon_zoomed_out: unitIcon.png
    /** Sets the custom image of the unit icon on the zoomed out battle map */
    var iconZoomedOut: String? = null,
    // Example: icon_zoomed_out_neverShow: false
    /** Sets wheather show the unit icon or not on the zoomed out battle map */
    var iconZoomedOutNeverShow: Boolean? = null,
    // Example: icon_build: iconFish.png
    /** Displays icon of the unit on Build Panel */
    var iconBuild: String? = null,

    // ===== Turrets (images can also be set on each turret) =====
    // Example: image_turret: gun.png
    /** Default image for all turrets, can also be set per turret */
    var imageTurret: Texture? = null,
    // Example: teamColorsOnTurret: true
    /**
     * Defaults false. Apply team colours on turret as well. Also effects pre-turret images
     */
    var teamColorsOnTurret: Boolean? = null,
    // Example: scaleTurretImagesTo: 1.2
    /**
     * Will cause crash if image_turret is not specified, even if image is set per turret
     */
    var scaleTurretImagesTo: Float? = null,
    // Example: lock_body_rotation_with_main_turret: true
    /** Locks body image locked to first turret's direction */
    var lockBodyRotationWithMainTurret: Boolean? = null,
    // Example: lock_leg_rotation_with_main_turret: true
    /** Locks legs and arms to first turret's direction */
    var lockLegRotationWithMainTurret: Boolean? = null,

    // ===== Shadow =====
    // Example: image_shadow: shadow.png
    /**
     * Image file, NONE, AUTO or AUTO_ANIMATED. (AUTO  will use image and make it transparent black only.)
     */
    var imageShadow: String? = null,
    // Example: shadowOffsetX: 10
    /** Adjusts shadow horizontally */
    var shadowOffsetX: Float? = null,
    // Example: shadowOffsetY: 10
    /** Adjusts shadow vertically */
    var shadowOffsetY: Float? = null,
    // Example: image_shadow_frames: true
    /**
     * If shadow image should use frame animation of main image. .Alternative to AUTO_ANIMATED shadow value.
     */
    var imageShadowFrames: Boolean? = null,
    // Example: lock_shadow_rotation_with_main_turret: true
    /** Locks body image shadow locked to first turret's direction */
    var lockShadowRotationWithMainTurret: Boolean? = null,

    // ===== Effects and animation =====
    // Example: total_frames: 3
    /** Defaults to 1. Animations require this. */
    var totalFrames: Int = 1,
    // Example: frame_width: 40
    /** Calculated for you if total frames is set, but can be overridden */
    var frameWidth: Int? = null,
    // Example: frame_height: 60
    /** Defaults to image height */
    var frameHeight: Int? = null,
    // Example: default_frame: 0
    /** Default frame when not playing an animation. First frame is 0 */
    var defaultFrame: Int = 0,
    // Example: splastEffect: true
    /** True to create a water wave effect when over water. Default false */
    var splastEffect: Boolean = false,
    // Example: dustEffect: true
    /** True to create a dust effect when over land. Default false */
    var dustEffect: Boolean = false,
    // Example: splastEffectReverse: true
    /** True to also create effect when unit is reversing */
    var splastEffectReverse: Boolean = false,
    // Example: dustEffectReverse: true
    /** True to also create effect when unit is reversing */
    var dustEffectReverse: Boolean = false,
    // Example: movementEffect: smoke, CUSTOM:fastDust*2, CUSTOM:pop*5
    /** Custom movement effect, can be anything */
    var movementEffect: String? = null,
    // Example: movementEffectReverse: smoke, CUSTOM:fastDust*2, CUSTOM:pop*5
    /** Reversed version of movementEffect: key */
    var movementEffectReverse: String? = null,
    // Example: movementEffectRate: 10
    /** Sets the frequency of effects being spawned while moving */
    var movementEffectRate: Float? = null,
    // Example: movementEffectReverseFlipEffects: true
    /** Create effect as if unit has rotated 180 when reversing */
    var movementEffectReverseFlipEffects: Boolean? = null,
    // Example: repairEffect: CUSTOM:mist*5
    /** Custom repair effect, can be anything. Replaces default effect from builders */
    var repairEffect: String? = null,
    // Example: repairEffectAtTarget: CUSTOM:greensparks*2
    /** Custom repair effect towards the repaired target */
    var repairEffectAtTarget: String? = null,
    // Example: repairEffectRate: 20
    /**
     * Sets the frequency of effects being spawned while repairing. Defaults to 5, affects both sides.
     */
    var repairEffectRate: Int? = null,
    // Example: reclaimEffect: CUSTOM:sap*5
    /** Custom reclaim effect, can be anything. Replaces default effect from builders */
    var reclaimEffect: String? = null,
    // Example: reclaimEffectAtTarget: CUSTOM:redsparks*2
    /** Custom reclaim effect towards the reclaimed target */
    var reclaimEffectAtTarget: String? = null,
    // Example: reclaimEffectRate: 20
    /**
     * Sets the frequency of effects being spawned while reclaiming. Defaults to 5, affects both sides.
     */
    var reclaimEffectRate: Int? = null,
    // Example: rotate_with_direction: false
    /**
     * Defaults to true. Makes unit body image locked to 0 degrees when false. Often used with animation_direction_*
     */
    var rotateWithDirection: Boolean? = null,
    // Example: animation_direction_units: 45
    /**
     * 45 for 8 directions, 90 for 4 direction animation. Used with rotate_with_direction:false
     */
    var animationDirectionUnits: Float? = null,
    // Example: animation_direction_strideX: 40
    /** Animation frames to offset on direction change. */
    var animationDirectionStrideX: Int? = null,
    // Example: animation_direction_strideY: 60
    /** Animation frames to offset on direction change. Used with frame_height. */
    var animationDirectionStrideY: Int? = null,
    // Example: animation_direction_starting: 90
    /** Direction for first frame */
    var animationDirectionStarting: Float? = null,
    // Example: disableLowHpFire: false
    /**
     * Defaults to false. When true, removes the flames on severely damaged unit/structure.
     */
    var disableLowHpFire: Boolean? = null,
    // Example: disableLowHpSmoke: false
    /**
     * Defaults to false. When true, removes the smokes on severely damaged unit/structure.
     */
    var disableLowHpSmoke: Boolean? = null,
    // Example: showTransportBar: false
    /** Defaults to true. Shows the transport bar for transport units. */
    var showTransportBar: Boolean? = null,
    // Example: showHealthBar: false
    /**
     * Defaults to true. Shows the health bar of a unit. (Still hidden even if true when "Always show unit health" setting is disabled.)
     */
    var showHealthBar: Boolean? = null,
    // Example: showEnergyBar: false
    /** Defaults to true. Shows the energy bar of a unit. */
    var showEnergyBar: Boolean? = null,
    // Example: showShieldBar: false
    /**
     * Defaults to true. Shows the shield bar of a unit. Also affects the presence of set custom shield sprite.
     */
    var showShieldBar: Boolean? = null,
    // Example: showQueueBar: false
    /** Defaults to true. Shows the bar for build or action queue of a unit. */
    var showQueueBar: Boolean? = null,
    // Example: showShotDelayBar: false
    /** Defaults to true. Shows the shot delay/warmup of a unit before firing. */
    var showShotDelayBar: Boolean? = null,
    // Example: showSelectionIndicator: false
    /** Defaults to true. Shows select circle/box if true */
    var showSelectionIndicator: Boolean? = null,

    // ===== Deprecated Keys (can be used but there are better, more adaptable ways) =====
    /**
     * False: Green pixels on unit gets converted to team color. True: Whole unit is tinted the team colour. Defaults to false
     */
    //var teamColorsUseHue: Boolean? = null,
    // Example: animation_moving_start: 0
    /**
     * TYPE can be set to: attack, moving, idle. Use [animation] section instead for more control
     */
    var animationMovingStart: Int? = null,
    // Example: animation_moving_end: 3
    /** End frame, must be larger then start */
    var animationMovingEnd: Int? = null,
    // Example: animation_moving_speed: 3
    /** Delay for each frame of animation. Larger values cause slower animation */
    var animationMovingSpeed: Float? = null,
    // Example: animation_moving_pingPong: true
    /** Play animation in reverse before repeating. Useful with scale_start/scale_end */
    var animationMovingPingPong: Boolean? = null,

    // Example: animation_idle_start: 0
    var animationIdleStart: Int? = null,
    var animationIdleEnd: Int? = null,
    var animationIdleSpeed: Float? = null,
    var animationIdlePingPong: Boolean? = null,

    // Example: animation_attack_start: 0
    var animationAttackStart: Int? = null,
    var animationAttackEnd: Int? = null,
    var animationAttackSpeed: Float? = null,
    var animationAttackPingPong: Boolean? = null,

    )
