package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.af */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/af.class */
public enum UnitEventType {
    created,
    completeAndActive,
    destroyed,
    killedAnyUnit,
    queuedUnitFinished,
    queueItemAdded,
    queueItemCancelled,
    teleported,
    touchTargetSuccess,
    newWaypointGivenByPlayer,
    teamChanged,
    transportingNewUnit,
    transportUnloadedOrRemovedUnit,
    tookDamage,
    enteredTransport,
    leftTransport,
    newMessage,
    attachmentRemoved
}
