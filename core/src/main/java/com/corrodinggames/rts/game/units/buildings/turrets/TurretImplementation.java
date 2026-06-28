package com.corrodinggames.rts.game.units.buildings.turrets;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.PointF;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/a/c.class */
public abstract class TurretImplementation {
    final /* synthetic */ TurretFactory a;

    /* JADX INFO: renamed from: a */
    abstract float getAttackRange();

    /* JADX INFO: renamed from: a */
    public abstract float getAttackDelay(int i);

    /* JADX INFO: renamed from: b */
    public abstract float getAttackDamage(int i);

    /* JADX INFO: renamed from: a */
    public abstract void fireProjectile(BaseUnit baseUnit, int i);

    /* JADX INFO: renamed from: d */
    public abstract Texture getTurretTopTexture(int i);

    /* JADX INFO: renamed from: b */
    public abstract int getTechLevel();

    public abstract String c();

    /* JADX INFO: renamed from: a */
    public abstract void copyFrom(TurretImplementation turretImplementation);

    public abstract int d();

    TurretImplementation(TurretFactory turretFactory) {
        this.a = turretFactory;
    }

    /* JADX INFO: renamed from: c */
    public PointF getTurretType(final int integer) {
        return this.a.E(integer);
    }

    /* JADX INFO: renamed from: a */
    public boolean isSameType(String str) {
        return c().equals(str);
    }

    /* JADX INFO: renamed from: a */
    public void update(float f) {
    }

    /* JADX INFO: renamed from: e */
    public float getTurretRestingRotationSpeed(int i) {
        return 5.0f;
    }

    /* JADX INFO: renamed from: f */
    public float getTurretTurnSpeed(int i) {
        return 0.5f;
    }

    /* JADX INFO: renamed from: g */
    public float getTurretHeight(int i) {
        return 23.0f;
    }

    /* JADX INFO: renamed from: h */
    public float getTurretOffset(int i) {
        return -1.0f;
    }
}
