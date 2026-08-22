package com.corrodinggames.rts.game.units.land;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.*;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.PlaceBuildingAction;
import com.corrodinggames.rts.game.units.actions.ReclaimTargetAction;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.Vector3D;
import io.github.rwx.geometry.PointF;
import io.github.rwx.render.canvas.KoolPaint;

import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/b.class */
public class BuilderUnit extends LandUnit implements UnitPathPoints {

    /* JADX INFO: renamed from: i */
    PointF[] targetPriorityPoints;

    /* JADX INFO: renamed from: j */
    PointF[] unitRenderPoints;

    /* JADX INFO: renamed from: k */
    static KoolPaint linePaint;

    /* JADX INFO: renamed from: l */
    static KoolPaint linePaintOver;

    /* JADX INFO: renamed from: m */
    static KoolPaint chargePaint;

    /* JADX INFO: renamed from: a */
    static Texture builderTexture = null;

    /* JADX INFO: renamed from: b */
    public static Texture builderTexture_dead = null;

    /* JADX INFO: renamed from: c */
    static Texture builderShadowTexture = null;

    /* JADX INFO: renamed from: d */
    public static Texture[] builderTexture_teamColors = new Texture[10];

    /* JADX INFO: renamed from: e */
    public static Texture builderChargeTexture = null;

    /* JADX INFO: renamed from: f */
    public static Texture builderDechargeTexture = null;

    /* JADX INFO: renamed from: g */
    static Texture builderIconTexture = null;

    /* JADX INFO: renamed from: h */
    public static Texture[] builderIconTexture_teamColors = new Texture[10];

    /* JADX INFO: renamed from: n */
    static AbstractUnitAction reclaimAction = new ReclaimTargetAction(false);

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: f, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.builder;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] b() {
        return this.targetPriorityPoints;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] e_() {
        return this.unitRenderPoints;
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        return builderIconTexture_teamColors[this.team.getTeamColorIndex()];
    }

    /* JADX INFO: renamed from: K */
    public static void loadTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        builderTexture = gameEngine.renderGraphicsEngine.a(R.drawable.builder);
        builderTexture_dead = gameEngine.renderGraphicsEngine.a(R.drawable.builder_dead);
        builderShadowTexture = attackUnit(builderTexture, builderTexture.m(), builderTexture.l());
        builderTexture_teamColors = PlayerTeam.getTeamColorTextures(builderTexture);
        builderChargeTexture = gameEngine.renderGraphicsEngine.a(R.drawable.builder_charge);
        builderDechargeTexture = gameEngine.renderGraphicsEngine.a(R.drawable.builder_decharge);
        builderIconTexture = gameEngine.renderGraphicsEngine.a(R.drawable.unit_icon_builder);
        builderIconTexture_teamColors = PlayerTeam.getTeamColorTextures(builderIconTexture);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: a */
    public boolean canRepairTarget(BaseUnit baseUnit) {
        if (!baseUnit.q() && baseUnit.bI()) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return builderTexture_dead;
        }
        return builderTexture_teamColors[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return builderShadowTexture;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    public BuilderUnit(boolean z) {
        super(z);
        this.targetPriorityPoints = new PointF[6];
        this.unitRenderPoints = new PointF[this.targetPriorityPoints.length];
        linePaint = new KoolPaint();
        linePaint.a(40, 0, 255, 0);
        linePaint.a(true);
        linePaint.a(2.0f);
        linePaint.a(KoolPaint.Cap.ROUND);
        linePaintOver = new KoolPaint();
        linePaintOver.a(linePaint);
        linePaintOver.a(55, 255, 60, 60);
        chargePaint = new KoolPaint();
        chargePaint.a(60, 255, 255, 255);
        T(20);
        U(20);
        this.radius = 10.0f;
        this.displayRadius = this.radius + 2.0f;
        this.maxHealth = 170.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = builderTexture;
        for (int i = 0; i < this.targetPriorityPoints.length; i++) {
            this.targetPriorityPoints[i] = new PointF();
            this.unitRenderPoints[i] = new PointF();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    public static void updateTargetPriorityPoints(float f, UnitPathPoints unitPathPoints) {
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
            pointF3.x = Utility.distanceSq(pointF3.x, pointF4.x, 0.1f * f);
            pointF3.y = Utility.distanceSq(pointF3.y, pointF4.y, 0.1f * f);
            pointF3.x += (pointF4.x - pointF3.x) * 0.04f * f;
            pointF3.y += (pointF4.y - pointF3.y) * 0.04f * f;
            float f2 = currentRepairOrReclaimTarget.radius * 0.75f;
            if (Utility.abs(pointF3.x - pointF4.x) < 1.0f) {
                pointF4.x = Utility.randomRepairTargetOffset(-f2, f2);
            }
            if (Utility.abs(pointF3.y - pointF4.y) < 1.0f) {
                pointF4.y = Utility.randomRepairTargetOffset(-f2, f2);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance();
        this.baseTexture = builderTexture_dead;
        S(0);
        this.isAlive = false;
        a(UnitSize.small);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: b */
    public static void drawTargetPriorityPoints(float f, UnitPathPoints unitPathPoints) {
        OrderableUnit orderableUnit = (OrderableUnit) unitPathPoints;
        BaseUnit currentRepairOrReclaimTarget = orderableUnit.getCurrentRepairOrReclaimTarget();
        if (currentRepairOrReclaimTarget != null) {
            boolean zIsCurrentCommandReclaim = orderableUnit.isCurrentCommandReclaim();
            if (!zIsCurrentCommandReclaim && orderableUnit.aO) {
                return;
            }
            GameEngine gameEngine = GameEngine.getInstance();
            PointF[] pointFArrB = unitPathPoints.b();
            KoolPaint paint = linePaint;
            if (zIsCurrentCommandReclaim) {
                paint = linePaintOver;
            }
            Vector3D vector3DBn = orderableUnit.bn();
            for (PointF pointF : pointFArrB) {
                float f2 = (currentRepairOrReclaimTarget.posX + pointF.x) - gameEngine.viewpointXSnapped;
                float f3 = ((currentRepairOrReclaimTarget.posY - currentRepairOrReclaimTarget.posZ) + pointF.y) - gameEngine.viewpointYSnapped;
                gameEngine.renderGraphicsEngine.a((vector3DBn.x + (pointF.x * 0.15f)) - gameEngine.viewpointXSnapped, (((vector3DBn.y - vector3DBn.z) + (pointF.y * 0.15f)) - gameEngine.viewpointYSnapped) - orderableUnit.posZ, f2, f3, paint);
                gameEngine.renderGraphicsEngine.k();
                gameEngine.renderGraphicsEngine.b(f2, f3);
                gameEngine.renderGraphicsEngine.a(0.5f, 0.5f);
                if (zIsCurrentCommandReclaim) {
                    gameEngine.renderGraphicsEngine.a(builderDechargeTexture, 0.0f, 0.0f, chargePaint);
                } else {
                    gameEngine.renderGraphicsEngine.a(builderChargeTexture, 0.0f, 0.0f, chargePaint);
                }
                gameEngine.renderGraphicsEngine.l();
            }
        }
    }

    @Override
    // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!this.isDead) {
            updateTargetPriorityPoints(f, this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
        super.a(f, z);
        if (!this.isDead) {
            drawTargetPriorityPoints(f, this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        return 30.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float f(int i) {
        return 1.3f;
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        if (!super.c(f)) {
            return false;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (!this.isDead) {
            float fE = this.movementLevels[0].speed / e(0);
            if (fE != 0.0f) {
                Vector3D vector3DBn = bn();
                gameEngine.renderGraphicsEngine.i();
                gameEngine.renderGraphicsEngine.b(vector3DBn.x - gameEngine.viewpointXSnapped, (vector3DBn.y - vector3DBn.z) - gameEngine.viewpointYSnapped);
                gameEngine.renderGraphicsEngine.a(fE, fE);
                if (isCurrentCommandReclaim()) {
                    gameEngine.renderGraphicsEngine.a(builderDechargeTexture, 0.0f, 0.0f, (KoolPaint) null);
                } else {
                    gameEngine.renderGraphicsEngine.a(builderChargeTexture, 0.0f, 0.0f, (KoolPaint) null);
                }
                gameEngine.renderGraphicsEngine.j();
                return true;
            }
            return true;
        }
        return true;
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
        if (isOverLiquid()) {
            return 0.6f;
        }
        return 0.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        if (isOverLiquid()) {
            return 1.7f;
        }
        return 3.8f;
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

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void performUnitAction(AbstractUnitAction abstractUnitAction, boolean z) {
    }

    /* JADX INFO: renamed from: a */
    public static void addAvailableActions(ArrayList arrayList, int i) {
        arrayList.add(reclaimAction);
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.extractor, 1, 1));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.turret, 1, 2));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.antiAirTurret, 1, 3));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.landFactory, 1, 4));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.airFactory, 1, 5));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.seaFactory, 1, 6));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.laserDefence, 1, 7));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.repairbay, 1, 8));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.fabricator, 1, 9));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.experimentalLandFactory, 1, 10));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.NukeLaucher, 1, 14));
        arrayList.add(new PlaceBuildingAction(UnitTypeEnum.AntiNukeLaucher, 1, 15));
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return r().a(getUpgradeLevel());
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
    public boolean g(BaseUnit baseUnit, boolean z) {
        return true;
    }
}
