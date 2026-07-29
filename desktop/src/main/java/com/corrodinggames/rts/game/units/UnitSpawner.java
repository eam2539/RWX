package com.corrodinggames.rts.game.units;

import android.graphics.Paint;
import android.graphics.PointF;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.land.BuilderUnit;
import com.corrodinggames.rts.game.units.land.LandUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/g.class */
public class UnitSpawner extends LandUnit implements UnitPathPoints {
    public boolean a;
    PointF[] b;
    PointF[] c;
    static Paint d;
    static Paint e;
    static Paint f;
    int g;
    float h;
    float i;
    int j;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: f, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.builder;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] b() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] e_() {
        return this.c;
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        return landUnitIconTextures[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: a */
    public boolean canRepairTarget(BaseUnit baseUnit) {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return BuilderUnit.builderTexture_dead;
        }
        return BuilderUnit.builderTexture_teamColors[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    public UnitSpawner(boolean z) {
        super(z);
        this.b = new PointF[6];
        this.c = new PointF[this.b.length];
        d = new Paint();
        d.a(40, 0, 255, 0);
        d.a(true);
        d.a(2.0f);
        d.a(Paint.Cap.ROUND);
        e = new Paint();
        e.a(d);
        e.a(55, 255, 60, 60);
        f = new Paint();
        f.a(60, 255, 255, 255);
        T(20);
        U(20);
        this.radius = 10.0f;
        this.posX = -1000.0f;
        this.posY = -1000.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 170000.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = BuilderUnit.builderTexture_dead;
        for (int i = 0; i < this.b.length; i++) {
            this.b[i] = new PointF();
            this.c[i] = new PointF();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void a(float f2, UnitPathPoints unitPathPoints) {
        OrderableUnit orderableUnit = (OrderableUnit) unitPathPoints;
        PointF[] pointFArrB = unitPathPoints.b();
        PointF[] pointFArrE_ = unitPathPoints.e_();
        BaseUnit currentRepairOrReclaimTarget = orderableUnit.getCurrentRepairOrReclaimTarget();
        orderableUnit.aN = currentRepairOrReclaimTarget != null;
        if (currentRepairOrReclaimTarget == null) {
            if (pointFArrB[0].x != 0.0f || pointFArrB[0].y != 0.0f) {
                for (int i = 0; i < pointFArrB.length; i++) {
                    PointF pointF = pointFArrB[i];
                    PointF pointF2 = pointFArrE_[i];
                    pointF.x = 0.0f;
                    pointF.y = 0.0f;
                    pointF2.x = 0.0f;
                    pointF2.y = 0.0f;
                }
                return;
            }
            return;
        }
        for (int i2 = 0; i2 < pointFArrB.length; i2++) {
            PointF pointF3 = pointFArrB[i2];
            PointF pointF4 = pointFArrE_[i2];
            pointF3.x = Utility.distanceSq(pointF3.x, pointF4.x, 0.1f * f2);
            pointF3.y = Utility.distanceSq(pointF3.y, pointF4.y, 0.1f * f2);
            pointF3.x += (pointF4.x - pointF3.x) * 0.04f * f2;
            pointF3.y += (pointF4.y - pointF3.y) * 0.04f * f2;
            float f3 = currentRepairOrReclaimTarget.radius * 0.75f;
            if (Utility.abs(pointF3.x - pointF4.x) < 1.0f) {
                pointF4.x = Utility.randomRepairTargetOffset(-f3, f3);
            }
            if (Utility.abs(pointF3.y - pointF4.y) < 1.0f) {
                pointF4.y = Utility.randomRepairTargetOffset(-f3, f3);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = BuilderUnit.builderTexture_dead;
        S(0);
        this.isAlive = false;
        gameEngine.soundEngine.playSound(SoundEngine.unitExplodeSound, 0.8f, this.posX, this.posY);
        bq();
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f2) {
        super.update(f2);
        if (!this.isDead) {
            a(f2, this);
        }
        this.currentHealth = this.maxHealth;
        this.g++;
        this.h += f2;
        this.i += f2;
        GameEngine.getInstance();
        if (!this.a) {
            if (this.i > 3.0f) {
                this.i = 0.0f;
                w();
                return;
            }
            return;
        }
        GameEngine.log("Stress test active");
        for (int i = 0; i < 6000; i++) {
            w();
        }
        removeFromGame();
    }

    public void w() {
        GameEngine gameEngine = GameEngine.getInstance();
        this.j++;
        UnitType unitType = (UnitType) UnitTypeEnum.ae.get(Utility.getDeterministicRandomInt((GameObject) this, 0, UnitTypeEnum.ae.size() - 1, 1 + this.j));
        boolean z = true;
        if (CustomUnitConfig.instance == unitType) {
            z = false;
        }
        if (unitType == UnitTypeEnum.spreadingFire) {
            z = false;
        }
        if (z) {
            BaseUnit baseUnitA = unitType.a();
            baseUnitA.posX = Utility.getDeterministicRandomInt((GameObject) this, 200, ((int) gameEngine.tileMap.getWorldWidth()) - 200, 2 + this.g + this.j);
            baseUnitA.posY = Utility.getDeterministicRandomInt((GameObject) this, 200, ((int) gameEngine.tileMap.getWorldHeight()) - 200, 3 + this.g + this.j + (this.j * 9));
            try {
                baseUnitA.setTeam(Utility.getDeterministicRandomInt((GameObject) this, 0, 3, 4 + this.g + this.j + (this.j * 9)));
                PlayerTeam.c(baseUnitA);
                if (baseUnitA.u()) {
                    baseUnitA.remove();
                }
                if (baseUnitA.bO()) {
                    baseUnitA.remove();
                }
            } catch (MapLoadException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f2, boolean z) {
        if (!this.isDead) {
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float f(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f2) {
        if (!super.c(f2)) {
            return false;
        }
        GameEngine.getInstance();
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: b_ */
    public boolean requiresFacingForActions() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public int y() {
        return 850000;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b */
    public float getDistanceToTarget(BaseUnit baseUnit) {
        return 1.0E7f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float c(BaseUnit baseUnit) {
        return 1.0E7f;
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
        if (isOverLiquid()) {
            return 4.7f;
        }
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
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && !this.isDead;
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

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.OrderableUnit
    public boolean I() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean d(BaseUnit baseUnit) {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: J */
    public boolean isDamageImmune() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public float setTarget(BaseUnit baseUnit, float f2, Projectile projectile) {
        return super.setTarget(baseUnit, 0.0f, projectile);
    }
}
