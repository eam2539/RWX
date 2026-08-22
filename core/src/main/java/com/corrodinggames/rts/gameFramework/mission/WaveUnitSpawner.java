package com.corrodinggames.rts.gameFramework.mission;

import com.corrodinggames.rts.game.units.UnitType;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/k.class */
class WaveUnitSpawner {

    /* JADX INFO: renamed from: a */
    UnitType unitType;

    /* JADX INFO: renamed from: b */
    float difficultyMultiplier = 1.0f;
    final /* synthetic */ MissionEngine c;

    WaveUnitSpawner(MissionEngine missionEngine) {
        this.c = missionEngine;
    }
}
