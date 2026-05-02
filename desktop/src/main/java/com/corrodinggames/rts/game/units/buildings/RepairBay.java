package com.corrodinggames.rts.game.units.buildings;

import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitCommand;
import com.corrodinggames.rts.game.units.UnitPathPoints;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.ReclaimTargetAction;
import com.corrodinggames.rts.game.units.actions.RepairTargetAction;
import com.corrodinggames.rts.game.units.land.BuilderUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.r */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/r.class */
public class RepairBay extends BaseBuilding implements UnitPathPoints {

    /* JADX INFO: renamed from: d */
    float repairTimer;

    /* JADX INFO: renamed from: f */
    Rect drawRect;

    /* JADX INFO: renamed from: g */
    Rect tempRect;

    /* JADX INFO: renamed from: i */
    PointF[] targetPriorityPoints;

    /* JADX INFO: renamed from: j */
    PointF[] hiddenPoints;

    /* JADX INFO: renamed from: a */
    static Texture baseTexture = null;

    /* JADX INFO: renamed from: b */
    static Texture[] teamTextures = new Texture[10];

    /* JADX INFO: renamed from: c */
    static Texture deadTexture = null;
    public static AutoRepairCallback e = new AutoRepairCallback(true);

    /* JADX INFO: renamed from: h */
    static ArrayList availableActions = new ArrayList();

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.repairbay;
    }

    static {
        availableActions.add(new ReclaimTargetAction(true));
        availableActions.add(new RepairTargetAction());
    }

    /* JADX INFO: renamed from: M */
    public static void initializeTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        baseTexture = gameEngine.graphicsEngine2.a(R.drawable.repair_bay);
        deadTexture = gameEngine.graphicsEngine2.a(R.drawable.repair_bay_dead);
        teamTextures = PlayerTeam.getUnitCountByType(baseTexture);
    }

    public RepairBay(boolean z) {
        super(z);
        this.drawRect = new Rect();
        this.tempRect = new Rect();
        this.targetPriorityPoints = new PointF[6];
        this.hiddenPoints = new PointF[this.targetPriorityPoints.length];
        this.baseTexture = baseTexture;
        b(baseTexture);
        this.radius = 30.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 1000.0f;
        this.currentHealth = this.maxHealth;
        this.buildingTargetRect.a(-1, -1, 1, 1);
        this.buildingVelocityRect.a(-1, -1, 1, 1);
        for (int i = 0; i < this.targetPriorityPoints.length; i++) {
            this.targetPriorityPoints[i] = new PointF();
            this.hiddenPoints[i] = new PointF();
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return deadTexture;
        }
        if (this.team == null) {
            return teamTextures[teamTextures.length - 1];
        }
        return teamTextures[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override
    // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding
    /* JADX INFO: renamed from: L */
    public boolean onDeath() {
        this.baseTexture = deadTexture;
        S(0);
        this.isAlive = false;
        a(UnitSize.normal);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public int y() {
        return SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float c(BaseUnit baseUnit) {
        return 0.2f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: a */
    public boolean canRepairTarget(BaseUnit baseUnit) {
        if (baseUnit.q()) {
            return false;
        }
        return true;
    }

    public static UnitCommand a(OrderableUnit orderableUnit, float f, float f2, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        e.configure(orderableUnit.y() + f2, z);
        gameEngine.unitSpatialIndex.a(orderableUnit.posX, orderableUnit.posY, orderableUnit.y() + f2, orderableUnit, f, e);
        BaseUnit baseUnit = e.bestTarget;
        if (baseUnit != null) {
            UnitCommand unitCommandQueueNextWaypoint = orderableUnit.queueNextWaypoint();
            unitCommandQueueNextWaypoint.setRepairCommand(baseUnit);
            if (unitCommandQueueNextWaypoint != null) {
                unitCommandQueueNextWaypoint.attackMoveRange = f2;
                unitCommandQueueNextWaypoint.isRepeating = true;
                return unitCommandQueueNextWaypoint;
            }
            return null;
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!isAlive() || this.isDestroyed) {
            return;
        }
        this.repairTimer += f;
        if (hasNoCurrentWaypoint() && this.repairTimer > 40.0f) {
            this.repairTimer = 0.0f;
            a((OrderableUnit) this, f, 0.0f, false);
        }
        if (!this.isDestroyed) {
            BuilderUnit.updateTargetPriorityPoints(f, this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return super.c(f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
        super.a(f, z);
        if (!this.isDestroyed) {
            BuilderUnit.drawTargetPriorityPoints(f, this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        throw new RuntimeException("Unit cannot shoot");
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF E(int i) {
        PointF pointFG = G(i);
        tempPointF2.a(pointFG.x + 0.0f, pointFG.y - 33.0f);
        return tempPointF2;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return availableActions;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] b() {
        return this.targetPriorityPoints;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] e_() {
        return this.hiddenPoints;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return y();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
        super.e(f);
        GameViewUtils.a(this, y());
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean g(BaseUnit baseUnit, boolean z) {
        return true;
    }
}
