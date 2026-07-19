package com.corrodinggames.rts.game.units.sea;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.*;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.PlaceBuildingAction;
import com.corrodinggames.rts.game.units.actions.ReclaimTargetAction;
import com.corrodinggames.rts.game.units.land.BuilderUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;

import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.h.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/h/b.class */
public class BuilderShip extends WaterUnit implements UnitPathPoints {
    PointF[] f;
    PointF[] g;
    Rect h;
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture d = null;
    static Texture[] e = new Texture[10];
    static AbstractUnitAction i = new ReclaimTargetAction(false);

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit, com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        return BuilderUnit.builderIconTexture_teamColors[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: f, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.builderShip;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] b() {
        return this.f;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] e_() {
        return this.g;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 6000.0f;
    }

    public static void t_() {
        GameEngine gameEngine = GameEngine.getInstance();
        b = gameEngine.renderGraphicsEngine.a(R.drawable.builder_ship);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.builder_ship_dead);
        c = gameEngine.renderGraphicsEngine.a(R.drawable.builder_ship_turret);
        e = PlayerTeam.getTeamColorTextures(b);
        d = attackUnit(b, b.m(), b.l());
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return a;
        }
        return e[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i2) {
        return c;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return d;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && this.posZ > -2.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 3.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 3.0f;
    }

    public BuilderShip(boolean z) {
        super(z);
        this.f = new PointF[6];
        this.g = new PointF[this.f.length];
        this.h = new Rect();
        b(b);
        this.radius = 13.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 500.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = b;
        for (int i2 = 0; i2 < this.f.length; i2++) {
            this.f[i2] = new PointF();
            this.g[i2] = new PointF();
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 240.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.9f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.12f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i2) {
        return 3.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float w(int i2) {
        return 0.25f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.03f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.1f;
    }

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!this.isDead) {
            BuilderUnit.updateTargetPriorityPoints(f, this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
        super.a(f, z);
        if (!this.isDead) {
            BuilderUnit.drawTargetPriorityPoints(f, this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        if (!super.c(f)) {
            return false;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        GameViewUtils.a((OrderableUnit) this);
        if (!this.isDead) {
            float fE = this.movementLevels[0].speed / e(0);
            if (fE != 0.0f) {
                PointF pointFE = E(0);
                gameEngine.renderGraphicsEngine.i();
                gameEngine.renderGraphicsEngine.b(pointFE.x - gameEngine.viewpointXSnapped, (pointFE.y - gameEngine.viewpointYSnapped) - this.posZ);
                gameEngine.renderGraphicsEngine.a(fE, fE);
                if (isCurrentCommandReclaim()) {
                    gameEngine.renderGraphicsEngine.a(BuilderUnit.builderDechargeTexture, 0.0f, 0.0f, (Paint) null);
                } else {
                    gameEngine.renderGraphicsEngine.a(BuilderUnit.builderChargeTexture, 0.0f, 0.0f, (Paint) null);
                }
                gameEngine.renderGraphicsEngine.j();
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i2) {
        return 11.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bl */
    public int getTechLevel() {
        return 1;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF G(int i2) {
        tempPointF3.a(this.posX + (Utility.fastCos(this.rotationSpeed) * 8.0f), this.posY + (Utility.fastSin(this.rotationSpeed) * 8.0f));
        return tempPointF3;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i2) {
        return 120 - (i2 * 28);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i2) {
        return 30.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float f(int i2) {
        return 1.3f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: a */
    public boolean canRepairTarget(BaseUnit baseUnit) {
        if (!baseUnit.q() && baseUnit.bI()) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void performUnitAction(AbstractUnitAction abstractUnitAction, boolean z) {
    }

    public static void a(ArrayList arrayList, int i2) {
        arrayList.add(i);
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.extractor, 1, 1));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.turret, 1, 2));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.antiAirTurret, 1, 3));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.landFactory, 1, 4));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.airFactory, 1, 5));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.seaFactory, 1, 6));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.fabricator, 1, 7));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.laserDefence, 1, 8));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.repairbay, 1, 9));
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return r().a(getUpgradeLevel());
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i2) {
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public int y() {
        return 145;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean g(BaseUnit baseUnit, boolean z) {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float f(UnitType unitType) {
        int iY = y();
        int iA = unitType.a(this);
        if (iA == 0 && unitType.p()) {
            iA = 110;
        }
        return iY + iA;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: u */
    public int setHeight(BaseUnit baseUnit) {
        return (int) f(baseUnit.r());
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public int v(BaseUnit baseUnit) {
        return (int) f(baseUnit.r());
    }
}
