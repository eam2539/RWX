package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.land.BuilderUnit;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.x */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/x.class */
public abstract class DummyUnit extends OrderableUnit {
    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        return BuilderUnit.builderTexture_dead;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        return false;
    }

    public DummyUnit(boolean z) {
        super(z);
        T(20);
        U(20);
        this.radius = 1.0f;
        this.displayRadius = this.radius;
        this.isAlive = false;
        this.maxHealth = 100.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = BuilderUnit.builderTexture_dead;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void f_() {
        this.isAlive = false;
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float f(int i) {
        return 0.0f;
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 30.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 100.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 4.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.35f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 99.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.04f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.1f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean E() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 10.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean u() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean d(BaseUnit baseUnit) {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean I() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: J */
    public boolean isDamageImmune() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public float setTarget(BaseUnit baseUnit, float f, Projectile projectile) {
        return super.setTarget(baseUnit, 0.0f, projectile);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean P() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean Q() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType h() {
        return UnitMovementType.AIR;
    }
}
