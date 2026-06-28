package io.github.rwx.mod.api.specs


/**
 * Properties for [decal_name] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class DecalSpec(
    // Example: layer: beforeUI
    /**
     * Sets the layer of the decal, values from lowest to highest - shadow,  beforeBody, afterBody, onTop, beforeUI, inactive
     */
    var layer: String? = null,
    // Example: order: 2
    /**
     * Defaults as 0, sets a more discrete layering if 2 or more decals takes same layer type. Otherwise order in INI file will be used.
     */
    var order: Float? = null,
    // Example: onlyWhenSelectedByOwnPlayer : true
    /**
     * Defaults as false, displays the decal only if the owner player clicks on the unit
     */
    var onlyWhenSelectedByOwnPlayer: Boolean? = null,
    // Example: onlyWhenSelectedByEnemyPlayer: true
    /**
     * Defaults as false, displays the decal only if the enemy player clicks on the unit
     */
    var onlyWhenSelectedByEnemyPlayer: Boolean? = null,
    // Example: onlyWhenSelectedByAllyNotOwnPlayer: true
    /**
     * Defaults as false, displays the decal only if the allied player clicks on the unit
     */
    var onlyWhenSelectedByAllyNotOwnPlayer: Boolean? = null,
    // Example: onlyWhenSelectedByAnyPlayer: true
    /** Defaults as false, displays the decal only if any player clicks on the unit */
    var onlyWhenSelectedByAnyPlayer: Boolean? = null,
    // Example: includeParentsSelection: true
    /**
     * onlyWhenSelectedBy* will also checks parent's selection when true. Useful with attachments.
     */
    var includeParentsSelection: Boolean? = null,
    // Example: onlyPlayersWithUnitControl: true
    /**
     * Draws the decal if a player that selects it has control (that includes shared units in multiplayer)
     */
    var onlyPlayersWithUnitControl: Boolean? = null,
    // Example: onlyTeam: any
    /**
     * Draws the decal if value matches the player from the following: own, notOwn, ally, allyNotOwn,  enemy, any
     */
    var onlyTeam: String? = null,
    // Example: onlyWithZoomLevelOrMore: 0.4
    /**
     * Draws the decal only if the zoom level matches or exceeds it, very useful for making 3D units more optimized.
     */
    var onlyWithZoomLevelOrMore: Float? = null,
    // Example: onlyWhileActive: true
    /** Draws the decal only if the unit is fully built */
    var onlyWhileActive: Boolean? = null,
    // Example: onlyWhileAlive: false
    /**
     * Draws the decal only if the unit is alive (If beforeUI layer default true, else default false)
     */
    var onlyWhileAlive: Boolean? = null,
    // Example: onlyInPreview: false
    /** Only show in sidebar, and building placement preview */
    var onlyInPreview: Boolean? = null,
    // Example: onlyOnNonPreview: true
    /**
     * Draws the decal only on the unit itself, not on the preview from building or in sidebar interface
     */
    var onlyOnNonPreview: Boolean? = null,
    // Example: onlyOnBodyFrameOf: 0
    /** Only draw decal when body frame is equal to this */
    var onlyOnBodyFrameOf: Int? = null,
    // Example: isVisible: if self.hp > self.maxHp/2
    /** Dynamic Value, draws the decal on a specific condition */
    var isVisible: LogicBoolean? = null,
    // Example: imageScale: 1 + (self.height * 0.1)
    /** Dynamic value,  scales the decal */
    var imageScale: LogicBoolean? = null,
    // Example: imageScaleX: 1
    /** Dynamic value */
    var imageScaleX: LogicBoolean? = null,
    // Example: imageScaleY:  (self.hp/self.maxHp)*100
    /** Dynamic value */
    var imageScaleY: LogicBoolean? = null,
    // Example: image: fish.png
    /** Takes a single image for the decal - NOT TO BE CONFUSED WITH IMAGE STACK */
    var image: String? = null,
    // Example: Multiple image file stack: imageStack - carFloor.png, carBody.png*3, carRoof.png*2, antenna.png*10
    /**
     * Takes one or more image with occassional multipliers for stacking. Also useful for using image stack from a MagicaVoxel slice export
     */
    var imageStack: String? = null,
    // Example: teamColors: true
    /** When true, automatically reassigns team color to respective teams */
    var teamColors: Boolean? = null,
    // Example: stack_hOffset: 4
    /**
     * Sets the height of every layer in a stack. 1 layer = 1 pixel. Can take negative values.
     */
    var stackHOffset: Float? = null,
    // Example: stack_frameOffset: 1
    /**
     * Useful for making 3D units, frame to offset by in imageStack. Often a value of 1 is useful in a sprite sheet.
     */
    var stackFrameOffset: Int? = null,
    // Example: stack_drawInReverseOrder: true
    /**
     * Renders the stack in the opposite way - last frame goes on bottom, first frame goes to top
     */
    var stackDrawInReverseOrder: Boolean? = null,
    // Example: stack_indexStart: 0
    /** Offset to start drawing images in the image stack */
    var stackIndexStart: LogicBoolean? = null,
    // Example: stack_indexCount: 10
    /** Number of images in the image stack to draw. */
    var stackIndexCount: LogicBoolean? = null,
    // Example: total_frames: 10
    /**
     * Sets the number of frames in an image from an imageStack that uses a spritesheet.
     */
    var totalFrames: Int? = null,
    // Example: frame_width: 20
    /** Sets the frame width in the decal stack */
    var frameWidth: Int? = null,
    // Example: frame_height: 40
    /** Sets the frame height in the decal stack */
    var frameHeight: Int? = null,
    // Example: frame: memory.frame
    /** Dynamic value, useful for animations */
    var frame: LogicBoolean? = null,
    // Example: addBodyFrameMultipliedBy: 2
    /** Add body frame number to this decal when set to 1. */
    var addBodyFrameMultipliedBy: Int? = null,
    // Example: xOffsetRelative: 50
    /** Sets horizontal offset relative to the unit */
    var xOffsetRelative: Int? = null,
    // Example: yOffsetRelative: 50
    /** Sets vertical offset relative to the unit */
    var yOffsetRelative: Int? = null,
    // Example: xOffsetAbsolute: 40
    /** Dynamic value */
    var xOffsetAbsolute: LogicBoolean? = null,
    // Example: yOffsetAbsolute: 40
    /** Dynamic value */
    var yOffsetAbsolute: LogicBoolean? = null,
    // Example: hOffset: 10
    /** Sets the height of the whole decal */
    var hOffset: Int? = null,
    // Example: dirOffset: 45
    /** Sets the direction of the whole decal. */
    var dirOffset: Int? = null,
    // Example: pivotOffset: 45
    /** only affects relative offsets without rotating image */
    var pivotOffset: Int? = null,
    // Example: alwaysStartDirAtZero: true
    /**
     * Keeps the decal on north direction regardless of the unit's direction value,  useful for custom in-unit interfaces
     */
    var alwaysStartDirAtZero: Boolean? = null,
    // Example: image_shadow: AUTO
    /** Sets shadow for the decal */
    var imageShadow: String? = null,
    // Example: shadowOffsetX: 0
    /** Sets the horizontal position of the decal's shadow */
    var shadowOffsetX: Int? = null,
    // Example: shadowOffsetY: 0
    /** Sets the vertical position of the decal's shadow */
    var shadowOffsetY: Int? = null,
    // Example: basePosition: self
    /**
     * Attaches the decal on the specified maker. use "self" if attaching it to the unit itself
     */
    var basePosition: String? = null,
    // Example: basePositionFromLeg: leg_3, basePositionFromLeg: arm_5
    /** Attaches the decal to the specified leg or arm ending. */
    var basePositionFromLegEnd: String? = null,
    // Example: basePositionFromTurret: rocketLauncherBase
    /** Attaches the decal to the specified turret */
    var basePositionFromTurret: String? = null,
    // Example: drawLineTo: attacking.customTarget2
    /** Draws a line from the unit to the specified marker, useful for custom waypoints */
    var drawLineTo: String? = null,
    // Example: color: #ffff00
    /** Sets the color of the drawn line */
    var color: Color? = null,
    // Example: lineWidth: 2
    /** Sets the width of the drawn line */
    var lineWidth: Float? = null,
    // Example: alpha: 0.5
    /** 0-1, sets transparency of decal (images or line) */
    var alpha: LogicBoolean? = null
)
