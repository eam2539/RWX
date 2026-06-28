package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/n.class */
enum CustomUnitAction {
    move,
    attack,
    idle,
    created,
    underConstruction,
    underConstructionWithLinkedBuiltTime,
    queuedUnits,
    repair,
    reclaim;

    public static CustomUnitAction a(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
