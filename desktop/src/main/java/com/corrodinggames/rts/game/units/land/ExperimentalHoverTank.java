package com.corrodinggames.rts.game.units.land;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/c.class */
public class ExperimentalHoverTank extends HoverLandUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    public static Texture d = null;
    public static Texture e = null;
    static Texture[] f = new Texture[10];
    int g;
    float h;
    Projectile i;
    Rect j;
    Paint k;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.experimentalHoverTank;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        Texture textureA = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_hovertank);
        f = PlayerTeam.getTeamColorTextures(textureA);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_hovertank_dead);
        b = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_hovertank_turret);
        c = attackUnit(textureA, textureA.m() / 1, textureA.l());
        d = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_hovertank_shield);
        e = gameEngine.renderGraphicsEngine.a(R.drawable.shield_mid);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        if (this.i != null && this.i.isDestroyed) {
            this.i = null;
        }
        gameOutputStream.writeObjectId(this.i);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.i = (Projectile) gameInputStream.readBaseUnitWithStatus(Projectile.class);
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return a;
        }
        return f[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return c;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && this.posZ > -2.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return b;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture T() {
        return d;
    }

    public ExperimentalHoverTank(boolean z) {
        super(z);
        this.h = 0.0f;
        this.j = new Rect();
        this.k = GameViewUtils.a();
        a(f[7], 1);
        this.radius = 30.0f;
        this.displayRadius = this.radius + 1.0f;
        this.maxHealth = 3500.0f;
        this.currentHealth = this.maxHealth;
        this.unitEnergyMax = 5000.0f;
        this.shield = this.unitEnergyMax;
        this.baseTexture = f[7];
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance();
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        a(UnitSize.largeUnit);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bW */
    public float getUnitTeamData() {
        if (this.unitEnergyMax > 0.0f && this.shield < this.unitEnergyMax) {
            return this.shield / this.unitEnergyMax;
        }
        return super.getUnitTeamData();
    }

    @Override // com.corrodinggames.rts.game.units.land.HoverLandUnit, com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f2) {
        super.update(f2);
        if (this.isDead || !isAlive()) {
            return;
        }
        if (!this.isDead) {
            if (this.spawnExitLockTimer != 0.0f) {
                S(2);
            } else {
                S(4);
            }
        }
        if (this.isMoving) {
        }
        this.h += 1.0f * f2;
        if (this.h > 360.0f) {
            this.h -= 360.0f;
        }
        this.posZ = Utility.distanceSq(this.posZ, 4.0f + (Utility.fastSin(this.h) * 2.0f), 0.1f * f2);
        this.shield = Utility.distanceSq(this.shield, this.unitEnergyMax, 0.25f * f2);
        this.unitShieldMax = Utility.distanceSq(this.unitShieldMax, 0.0f, 4.0f * f2);
        if (this.unitShieldMax > 50.0f) {
            this.unitShieldMax = 50.0f;
        }
        if (this.i != null) {
            PointF pointFE = E(0);
            this.i.posX = pointFE.x;
            this.i.posY = pointFE.y;
            this.i.posZ = this.posZ;
            if (this.i.isDestroyed) {
                this.i = null;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 80000.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float L(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: K */
    public PointF getShadowTexture(int i) {
        PointF pointFK = super.getShadowTexture(i);
        if (this.i != null) {
            pointFK.x += this.i.K;
            pointFK.y += this.i.L;
        }
        return pointFK;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float q(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        GameEngine.getInstance();
        PointF pointFE = E(i);
        if (this.i != null) {
            boolean z = false;
            if (this.i.isDestroyed) {
                z = true;
            }
            if (this.i.l != baseUnit) {
                z = true;
            }
            if (z) {
                this.i = null;
            }
        }
        float fB = b(i) + e(i) + 5.0f;
        if (this.i != null) {
            this.i.h = fB;
            return;
        }
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
        projectileA.U = 380.0f;
        projectileA.l = baseUnit;
        projectileA.h = fB;
        projectileA.B = true;
        projectileA.A = true;
        projectileA.aQ = true;
        projectileA.E = true;
        projectileA.J = 70.0f;
        projectileA.F = 230.0f;
        projectileA.ak = 0.75f;
        projectileA.drawLayer = this.drawLayer;
        this.i = projectileA;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 180.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 8.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        return 8.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.6f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float bc() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.1f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.03f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 1.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.02f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.02f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        if (this.isDead && !z) {
            return super.a_(z);
        }
        if (z) {
            return super.a_(z);
        }
        int i = 0 + (this.g * this.es);
        this.j.a(i, 0, i + this.es, 0 + this.et);
        return this.j;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f2) {
        Texture textureT;
        if (!super.c(f2)) {
            return false;
        }
        GameViewUtils.a((OrderableUnit) this);
        if (!this.isDead) {
            float fClamp = 0.0f;
            if (this.i != null) {
                fClamp = Utility.clamp(this.i.e(), 0.25f) * 3.0f;
            }
            GameViewUtils.a(this, MammothTank.e, fClamp, 0);
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (!this.isDead && this.shield > 0.0f && this.energy == 0.0f && (textureT = T()) != null) {
            this.k.a((int) ((0.09f + ((this.shield / this.unitEnergyMax) * 0.4f) + ((Utility.clamp(this.unitShieldMax, 50.0f) / 50.0f) * 0.5f)) * 255.0f), 255, 255, 255);
            gameEngine.renderGraphicsEngine.a(textureT, this.posX - gameEngine.viewpointXSnapped, (this.posY - gameEngine.viewpointYSnapped) - this.posZ, getUnitArmorRating(false) - 90.0f, this.k);
            return true;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 8.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF G(int i) {
        tempPointF3.a(this.posX, this.posY);
        return tempPointF3;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bl */
    public int getTechLevel() {
        return 1;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bi */
    public boolean isSlidingMovement() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bj */
    public boolean isIgnoreMoveOrders() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cw */
    public int getTransportSlotsNeeded() {
        return 5;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: dd */
    public boolean isExperimental() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f2) {
        super.e(f2);
        GameViewUtils.a(this, m());
    }
}
