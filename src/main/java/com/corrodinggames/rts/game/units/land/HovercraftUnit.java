package com.corrodinggames.rts.game.units.land;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.TransportUnitInterface;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.NoneAction;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/i.class */
public class HovercraftUnit extends HoverLandUnit implements TransportUnitInterface {

    /* JADX INFO: renamed from: e */
    float animationTimer;

    /* JADX INFO: renamed from: f */
    float unloadTimer;

    /* JADX INFO: renamed from: g */
    boolean isUnloading;

    /* JADX INFO: renamed from: h */
    FastArrayList<BaseUnit> transportedUnits;
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture[] d = new Texture[10];
    public static final AbstractUnitAction i = new NoneAction(109) { // from class: com.corrodinggames.rts.game.units.e.i.1
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public String isLocked() {
            return "-Will unload all units when stopped";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public String getCostForUnit() {
            return Locale.get("gui.actions.unload", new Object[0]);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.corrodinggames.rts.game.units.actions.NoneAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public int isActive(BaseUnit baseUnit, boolean z) {
            return ((TransportUnitInterface) baseUnit).getTransportedUnitCount();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
            return !((TransportUnitInterface) baseUnit).isTransportUnloadingActive() && ((TransportUnitInterface) baseUnit).f() && ((TransportUnitInterface) baseUnit).getTransportedUnitCount() > 0;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        public boolean b(BaseUnit baseUnit) {
            return ((TransportUnitInterface) baseUnit).j();
        }
    };
    public static final AbstractUnitAction j = new NoneAction(110) { // from class: com.corrodinggames.rts.game.units.e.i.2
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public String isLocked() {
            return "-Stop unloading";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public String getCostForUnit() {
            return Locale.get("gui.actions.cancel", new Object[0]);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
            return ((TransportUnitInterface) baseUnit).isTransportUnloadingActive();
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        public boolean b(BaseUnit baseUnit) {
            return drawTooltip(baseUnit, false);
        }
    };
    static ArrayList k = new ArrayList();

    static {
        k.add(i);
        k.add(j);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.animationTimer);
        gameOutputStream.writeFloat(this.unloadTimer);
        gameOutputStream.writeBoolean(this.isUnloading);
        gameOutputStream.writeInt(this.transportedUnits.size());
        Iterator it = this.transportedUnits.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeUnitIdOrNullBaseUnit((BaseUnit) it.next());
        }
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.animationTimer = gameInputStream.readFloat();
        this.unloadTimer = gameInputStream.readFloat();
        this.isUnloading = gameInputStream.readBoolean();
        this.transportedUnits.clear();
        int i2 = gameInputStream.readInt();
        for (int i3 = 0; i3 < i2; i3++) {
            BaseUnit baseUnit = gameInputStream.readBaseUnit();
            if (baseUnit != null) {
                this.transportedUnits.add(baseUnit);
            }
        }
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.hovercraft;
    }

    public static void L() {
        GameEngine gameEngine = GameEngine.getInstance();
        a = gameEngine.graphicsEngine2.a(R.drawable.hovercraft);
        c = gameEngine.graphicsEngine2.a(R.drawable.hovercraft_shadow);
        b = gameEngine.graphicsEngine2.a(R.drawable.hovercraft_dead);
        d = PlayerTeam.getUnitCountByType(a);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return b;
        }
        return d[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return c;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i2) {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        this.baseTexture = b;
        S(0);
        this.isAttacking = false;
        releaseAllUnits(true);
        a(UnitSize.small);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.SizedObject, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void remove() {
        releaseAllUnits(true);
        super.remove();
    }

    /* JADX INFO: renamed from: f */
    public void releaseAllUnits(boolean z) {
        for (BaseUnit baseUnit : this.transportedUnits) {
            baseUnit.unitTransportTarget = null;
            baseUnit.posX = this.posX + (Utility.fastCos(this.rotationSpeed) * (-9.0f));
            baseUnit.posY = this.posY + (Utility.fastSin(this.rotationSpeed) * (-9.0f));
            if (z) {
                baseUnit.getUnitAIConditionTime();
            }
        }
        this.transportedUnits.clear();
    }

    public HovercraftUnit(boolean z) {
        super(z);
        this.animationTimer = 0.0f;
        this.transportedUnits = new FastArrayList();
        T(20);
        U(32);
        this.speed = 15.0f;
        this.maxSpeed = this.speed;
        this.maxHealth = 450.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = a;
        this.shadowTexture = c;
    }

    public static int a(FastArrayList fastArrayList) {
        int unitAIPathfindIterations = 0;
        Iterator it = fastArrayList.iterator();
        while (it.hasNext()) {
            unitAIPathfindIterations += ((BaseUnit) it.next()).getUnitAIPathfindIterations();
        }
        return unitAIPathfindIterations;
    }

    public static boolean a(FastArrayList fastArrayList, int i2, BaseUnit baseUnit) {
        if (a(fastArrayList) + baseUnit.getUnitAIPathfindIterations() <= i2) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bY */
    public int getTransportedUnitsWeight() {
        return a(this.transportedUnits);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bZ */
    public int getMaxTransportWeight() {
        return 4;
    }

    public static boolean a(BaseUnit baseUnit, BaseUnit baseUnit2, boolean z) {
        return a(baseUnit, baseUnit2, z, 9.0f, -180.0f, 70.0f, 0.0f, 7.0f);
    }

    public static boolean a(BaseUnit baseUnit, BaseUnit baseUnit2, boolean z, float f, float f2, float f3, float f4, float f5) {
        float fFastCos = (baseUnit.posX + (Utility.fastCos(baseUnit.rotationSpeed + f2) * f5)) - (Utility.fastSin(baseUnit.rotationSpeed + f2) * f4);
        float fFastSin = baseUnit.posY + (Utility.fastSin(baseUnit.rotationSpeed + f2) * f5) + (Utility.fastCos(baseUnit.rotationSpeed + f2) * f4);
        float fFastCos2 = fFastCos + (Utility.fastCos(baseUnit.rotationSpeed + 90.0f) * (z ? -f : f));
        float fFastSin2 = fFastSin + (Utility.fastSin(baseUnit.rotationSpeed + 90.0f) * (z ? -f : f));
        if (!GameViewUtils.a(baseUnit2, fFastCos2, fFastSin2)) {
            fFastCos2 += 10.0f;
        }
        if (!GameViewUtils.a(baseUnit2, fFastCos2, fFastSin2)) {
            fFastCos2 -= 20.0f;
        }
        if (!GameViewUtils.a(baseUnit2, fFastCos2, fFastSin2)) {
            fFastCos2 -= 10.0f;
            fFastSin2 += 10.0f;
        }
        if (!GameViewUtils.a(baseUnit2, fFastCos2, fFastSin2)) {
            fFastSin2 -= 20.0f;
        }
        if (!GameViewUtils.a(baseUnit2, fFastCos2, fFastSin2)) {
            return false;
        }
        baseUnit2.unitTransportTarget = null;
        baseUnit2.posX = fFastCos2;
        baseUnit2.posY = fFastSin2;
        baseUnit2.worldX += 0.1f;
        baseUnit2.rotationSpeed = baseUnit.rotationSpeed + f2;
        baseUnit2.attackTargetUnit = baseUnit;
        baseUnit2.bS = 45.0f;
        if (baseUnit2 instanceof OrderableUnit) {
            OrderableUnit orderableUnit = (OrderableUnit) baseUnit2;
            orderableUnit.j(baseUnit2.rotationSpeed);
            orderableUnit.clearAllWaypoints();
            orderableUnit.appendMoveWaypoint(baseUnit2.posX + (Utility.fastCos(baseUnit2.rotationSpeed + (z ? -f : f)) * f3), baseUnit2.posY + (Utility.fastSin(baseUnit2.rotationSpeed + (z ? -f : f)) * f3));
            orderableUnit.waypointSyncGroupId = 0;
            return true;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.land.HoverLandUnit, com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (this.isDestroyed || !isAlive()) {
            return;
        }
        if (this.spawnExitLockTimer == 0.0f && this.syncType != 3) {
            S(3);
        }
        if (this.isUnloading && !isMoving() && !this.isRepairing) {
            this.unloadTimer = Utility.moveTowardsZero(this.unloadTimer, f);
            if (this.unloadTimer == 0.0f) {
                this.unloadTimer = 30.0f;
                if (this.transportedUnits.size() == 0) {
                    this.isUnloading = false;
                } else {
                    boolean z = this.transportedUnits.size() % 2 == 0;
                    BaseUnit baseUnit = (BaseUnit) this.transportedUnits.remove(this.transportedUnits.size() - 1);
                    if (!a(this, baseUnit, z)) {
                        this.transportedUnits.add(baseUnit);
                    }
                    if (this.transportedUnits.size() == 0) {
                        this.isUnloading = false;
                    }
                }
            }
        }
        this.animationTimer += 4.0f * f;
        if (this.animationTimer > 360.0f) {
            this.animationTimer -= 360.0f;
        }
        if (!this.isUnloading) {
            this.posZ = Utility.distanceSq(this.posZ, 3.0f + (Utility.fastSin(this.animationTimer) * 1.5f), 0.1f * f);
        } else {
            this.posZ = Utility.distanceSq(this.posZ, 0.0f, 0.1f * f);
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i2) {
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 30.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i2) {
        return 100.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        if (isMoving()) {
            return 1.2f;
        }
        return 0.9f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        if (isMoving()) {
            return 1.8f;
        }
        return 1.4f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.1f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.03f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.05f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i2) {
        return 99.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean d(BaseUnit baseUnit, boolean z) {
        if (this.isUnloading || !a(this.transportedUnits, 4, baseUnit) || baseUnit == this) {
            return false;
        }
        if (this.team != baseUnit.team && !z) {
            return false;
        }
        return GameViewUtils.a(baseUnit, true, true);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e(BaseUnit baseUnit, boolean z) {
        if (!d(baseUnit, z)) {
            return false;
        }
        C(baseUnit);
        return true;
    }

    public void C(BaseUnit baseUnit) {
        baseUnit.unitTransportTarget = this;
        this.transportedUnits.add(baseUnit);
        GameEngine.getInstance().gameUI.deselectUnit(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.TransportUnitInterface
    public void e(BaseUnit baseUnit) {
        if (baseUnit.unitTransportTarget == this) {
            this.transportedUnits.remove(baseUnit);
            baseUnit.unitTransportTarget = null;
        } else {
            GameEngine.logWarningAndStack("Unit is not being transported");
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 12000.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.TransportUnitInterface
    /* JADX INFO: renamed from: bA */
    public boolean isTransportUnloadingActive() {
        return this.isUnloading;
    }

    /* JADX INFO: renamed from: M */
    public void startUnloading() {
        this.isUnloading = true;
        this.unloadTimer = 30.0f;
    }

    /* JADX INFO: renamed from: ds */
    public void stopUnloading() {
        this.isUnloading = false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void performUnitAction(AbstractUnitAction abstractUnitAction, boolean z) {
        if (abstractUnitAction == i) {
            startUnloading();
        }
        if (abstractUnitAction == j) {
            stopUnloading();
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cr */
    public boolean getUnitAIPathfindCost() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.TransportUnitInterface
    /* JADX INFO: renamed from: bB */
    public int getTransportedUnitCount() {
        return this.transportedUnits.size();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cp */
    public ActionId getUnitAIPathfindPath() {
        return i.getActionId();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return k;
    }

    @Override // com.corrodinggames.rts.game.units.TransportUnitInterface
    public boolean f() {
        return !isMoving();
    }

    @Override // com.corrodinggames.rts.game.units.TransportUnitInterface
    public boolean j() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bz */
    public FastArrayList getTransportedUnitList() {
        return this.transportedUnits;
    }
}
