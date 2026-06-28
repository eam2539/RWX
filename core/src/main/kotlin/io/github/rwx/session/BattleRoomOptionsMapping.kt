package io.github.rwx.session

import com.corrodinggames.rts.gameFramework.network.GameRoomSettings

/**
 * The one and only place that maps between [BattleRoomOptions] (the canonical room-rules model)
 * and the legacy engine's [GameRoomSettings]. Every reader/writer of room settings must go through
 * these two functions so a new option is wired in exactly one edit.
 *
 * Field-name note: the legacy engine spells fog as `fodMode`.
 */
fun GameRoomSettings.toBattleRoomOptions(): BattleRoomOptions =
    BattleRoomOptions(
        startingCredits = startingCredits,
        fogMode = fogMode,
        revealedMap = revealedMap,
        aiDifficulty = aiDifficulty,
        startingUnits = startingUnits,
        incomeMultiplier = incomeMultiplier,
        noNukes = noNukes,
        sharedControl = sharedControl,
        allowSpectators = allowSpectators,
        teamLock = teamLock,
        roomLocked = roomLock,
        fixedAllyTeams = fixedAllyTeams
    )

/**
 * Writes [options] onto this [GameRoomSettings], clamping numeric fields to engine-safe ranges.
 * Sandbox/mode-specific overrides belong on the [BattleRoomOptions] before it reaches here.
 */
fun GameRoomSettings.applyBattleRoomOptions(options: BattleRoomOptions) {
    startingCredits = options.startingCredits.coerceAtLeast(0)
    fogMode = options.fogMode.coerceIn(0, 2)
    revealedMap = options.revealedMap
    aiDifficulty = options.aiDifficulty
    startingUnits = options.startingUnits.coerceAtLeast(1)
    incomeMultiplier = options.incomeMultiplier.coerceIn(0.1f, 100f)
    noNukes = options.noNukes
    sharedControl = options.sharedControl
    allowSpectators = options.allowSpectators
    teamLock = options.teamLock
    roomLock = options.roomLocked
    fixedAllyTeams = options.fixedAllyTeams
}
