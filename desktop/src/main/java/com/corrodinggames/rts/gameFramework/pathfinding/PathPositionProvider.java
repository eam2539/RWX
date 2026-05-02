package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.PositionData;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/c.class */
public abstract class PathPositionProvider {
    public abstract PositionData a(BaseUnit baseUnit);

    public abstract PositionData b(BaseUnit baseUnit);

    public abstract void c(BaseUnit baseUnit);

    public abstract void d(BaseUnit baseUnit);
}
