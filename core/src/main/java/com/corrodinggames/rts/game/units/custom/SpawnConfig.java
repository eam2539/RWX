package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bq */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bq.class */
public class SpawnConfig {

    /* JADX INFO: renamed from: a */
    UnitTypeReference unitType;

    /* JADX INFO: renamed from: b */
    LogicBoolean spawnSource;

    /* JADX INFO: renamed from: c */
    LogicBoolean copyWaypointsFrom;

    /* JADX INFO: renamed from: e */
    boolean neutralTeam;

    /* JADX INFO: renamed from: f */
    boolean aggressiveTeam;

    /* JADX INFO: renamed from: g */
    boolean setToTeamOfLastAttacker;

    /* JADX INFO: renamed from: k */
    boolean skipIfOverlapping;

    /* JADX INFO: renamed from: l */
    boolean falling;

    /* JADX INFO: renamed from: n */
    boolean alwaysStartDirAtZero;

    /* JADX INFO: renamed from: s */
    float offsetRandomX;

    /* JADX INFO: renamed from: t */
    float offsetRandomY;

    /* JADX INFO: renamed from: u */
    float offsetRandomDir;

    /* JADX INFO: renamed from: v */
    UnitPrice addResources;

    /* JADX INFO: renamed from: w */
    short transportedUnitsToTransfer;

    /* JADX INFO: renamed from: d */
    int spawnCount = 1;

    /* JADX INFO: renamed from: h */
    float spawnChance = 1.0f;

    /* JADX INFO: renamed from: i */
    int maxSpawnLimit = Integer.MAX_VALUE;

    /* JADX INFO: renamed from: j */
    boolean gridAlign = false;

    /* JADX INFO: renamed from: m */
    int techLevel = -1;

    /* JADX INFO: renamed from: o */
    float offsetX = 0.0f;

    /* JADX INFO: renamed from: p */
    float offsetY = 0.0f;

    /* JADX INFO: renamed from: q */
    float offsetHeight = 0.0f;

    /* JADX INFO: renamed from: r */
    float offsetDir = 0.0f;

    public SpawnConfig(UnitTypeReference unitTypeReference) {
        this.unitType = unitTypeReference;
    }
}
