package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.custom.condition.StoredResources;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ao */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ao.class */
public class UnitSnapshot {

    /* JADX INFO: renamed from: a */
    long unitId;

    /* JADX INFO: renamed from: b */
    int ammo;

    /* JADX INFO: renamed from: c */
    int unitFlags;

    /* JADX INFO: renamed from: d */
    int blockingFrame;

    /* JADX INFO: renamed from: e */
    StoredResources resources = new StoredResources();
}
