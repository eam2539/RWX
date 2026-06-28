package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import io.github.rwx.geometry.PointF;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/l.class */
public interface FactoryQueueInterface {
    boolean dA();

    void b(Projectile projectile);

    void a(Projectile projectile);

    int h(UnitType unitType);

    int f(boolean z);

    int a(ActionId actionId, boolean z);

    boolean dy();

    void a(PointF pointF);

    void dz();

    Projectile dw();

    FastArrayList dx();

    boolean c(Projectile projectile);
}
