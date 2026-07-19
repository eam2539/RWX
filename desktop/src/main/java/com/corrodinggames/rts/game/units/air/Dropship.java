package com.corrodinggames.rts.game.units.air;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.TransportUnitInterface;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.NoneAction;
import com.corrodinggames.rts.game.units.land.HovercraftUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.b.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/b/d.class */
public class Dropship extends AirUnit implements TransportUnitInterface {
    float e;
    float f;
    boolean g;
    FastArrayList<BaseUnit> o;
    Rect p;
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture[] d = new Texture[10];
    public static final AbstractUnitAction q = new NoneAction(109) { // from class: com.corrodinggames.rts.game.units.b.d.1
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

        @Override // com.corrodinggames.rts.game.units.actions.NoneAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public int isActive(BaseUnit baseUnit, boolean z) {
            return ((Dropship) baseUnit).o.size();
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
            return (((Dropship) baseUnit).g || ((OrderableUnit) baseUnit).isMoving() || ((Dropship) baseUnit).o.size() <= 0) ? false : true;
        }
    };
    public static final AbstractUnitAction r = new NoneAction(110) { // from class: com.corrodinggames.rts.game.units.b.d.2
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

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
            return ((Dropship) baseUnit).g;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        public boolean b(BaseUnit baseUnit) {
            return drawTooltip(baseUnit, false);
        }
    };
    static ArrayList s = new ArrayList();

    static {
        s.add(q);
        s.add(r);
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.e);
        gameOutputStream.writeFloat(this.f);
        gameOutputStream.writeBoolean(this.g);
        gameOutputStream.writeInt(this.o.size());
        Iterator it = this.o.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeUnitIdOrNullBaseUnit((BaseUnit) it.next());
        }
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.e = gameInputStream.readFloat();
        this.f = gameInputStream.readFloat();
        this.g = gameInputStream.readBoolean();
        this.o.clear();
        int i = gameInputStream.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            BaseUnit baseUnit = gameInputStream.readBaseUnit();
            if (baseUnit != null) {
                this.o.add(baseUnit);
            }
        }
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bY */
    public int getTransportedUnitsWeight() {
        return HovercraftUnit.a(this.o);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bZ */
    public int getMaxTransportWeight() {
        return 4;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.dropship;
    }

    public static void L() {
        GameEngine gameEngine = GameEngine.getInstance();
        b = gameEngine.renderGraphicsEngine.a(R.drawable.dropship);
        c = gameEngine.renderGraphicsEngine.a(R.drawable.dropship_shadow);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.dropship_dead);
        d = PlayerTeam.getTeamColorTextures(b);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return a;
        }
        return d[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return c;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    public Dropship(boolean z) {
        super(z);
        this.e = 0.0f;
        this.o = new FastArrayList();
        this.p = new Rect();
        T(45);
        U(47);
        this.radius = 20.0f;
        this.displayRadius = this.radius + 0.0f;
        this.maxHealth = 500.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = b;
        this.shadowTexture = c;
        this.posZ = 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        f(true);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.OrderableUnit
    public boolean I() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return this.posZ >= 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ct */
    public boolean getUnitAIPathfindMaxDepth() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (this.isDead) {
            return;
        }
        boolean zCK = isMoving();
        if (this.g && !zCK && !this.isInitialized && this.posZ < 4.0f) {
            this.f = Utility.moveTowardsZero(this.f, f);
            if (this.f == 0.0f) {
                this.f = 30.0f;
                if (this.o.size() == 0) {
                    this.g = false;
                } else {
                    boolean z = this.o.size() % 2 == 0;
                    float fFastCos = this.posX + (Utility.fastCos(this.rotationSpeed) * (-9.0f));
                    float fFastSin = this.posY + (Utility.fastSin(this.rotationSpeed) * (-9.0f));
                    float fFastCos2 = fFastCos + (Utility.fastCos(this.rotationSpeed + 90.0f) * (z ? -7 : 7)) + (Utility.fastSin(this.rotationSpeed + 90.0f) * (z ? -7 : 7));
                    BaseUnit baseUnit = (BaseUnit) this.o.remove(this.o.size() - 1);
                    if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin)) {
                        fFastCos2 += 10.0f;
                    }
                    if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin)) {
                        fFastCos2 -= 20.0f;
                    }
                    if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin)) {
                        fFastCos2 -= 10.0f;
                        fFastSin += 10.0f;
                    }
                    if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin)) {
                        fFastSin -= 20.0f;
                    }
                    if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin)) {
                        this.o.add(baseUnit);
                    } else {
                        baseUnit.unitTransportTarget = null;
                        baseUnit.posX = fFastCos2;
                        baseUnit.posY = fFastSin;
                        baseUnit.worldX += 0.1f;
                        baseUnit.rotationSpeed = this.rotationSpeed + 180.0f;
                        baseUnit.attackTargetUnit = this;
                        baseUnit.bS = 45.0f;
                        if (baseUnit instanceof OrderableUnit) {
                            OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                            orderableUnit.clearAllWaypoints();
                            orderableUnit.appendMoveWaypoint(this.posX + (Utility.fastCos(this.rotationSpeed) * (-66.0f)), this.posY + (Utility.fastSin(this.rotationSpeed) * (-66.0f)));
                        }
                        if (this.o.size() == 0) {
                            this.g = false;
                        }
                    }
                }
            }
        }
        this.e += 2.0f * f;
        if (this.e > 360.0f) {
            this.e -= 360.0f;
        }
        boolean zI = i();
        if (isAlive()) {
            if (hasNoCurrentWaypoint() && !zCK) {
                this.posZ = Utility.distanceSq(this.posZ, 2.0f, 0.4f * f);
            } else {
                this.posZ = Utility.distanceSq(this.posZ, 35.0f + (Utility.fastSin(this.e) * 1.5f), 0.35f * f);
            }
        }
        if (zI != i()) {
            this.movementActiveThisFrame = true;
            if (i()) {
                S(5);
            } else {
                S(2);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF E(int i) {
        float fG = g(i);
        float f = this.rotationSpeed;
        tempPointF2.a(this.posX + (Utility.fastCos(f) * fG), this.posY + (Utility.fastSin(f) * fG));
        return tempPointF2;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y, this.posZ, i);
        projectileA.ar = Color.a(255, 150, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 40);
        projectileA.U = 35.0f;
        projectileA.l = baseUnit;
        projectileA.h = 80.0f;
        projectileA.t = 4.0f;
        projectileA.x = 2.0f;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1127220);
        gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
        gameEngine.soundEngine.playSound(SoundEngine.gunFire4Sound, 0.3f, this.posX, this.posY);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 140.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 40.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 2.3f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.4f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 99.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean E() {
        return false;
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

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 15.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.SizedObject, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void remove() {
        f(true);
        super.remove();
    }

    public void f(boolean z) {
        for (BaseUnit baseUnit : this.o) {
            baseUnit.unitTransportTarget = null;
            baseUnit.posX = this.posX + (Utility.fastCos(this.rotationSpeed) * (-9.0f));
            baseUnit.posY = this.posY + (Utility.fastSin(this.rotationSpeed) * (-9.0f));
            if (z) {
                baseUnit.getUnitAIConditionTime();
            }
        }
        this.o.clear();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.TransportUnitInterface
    /* JADX INFO: renamed from: bA */
    public boolean isTransportUnloadingActive() {
        return this.g;
    }

    public void M() {
        this.g = true;
        this.f = 30.0f;
    }

    public void ds() {
        this.g = false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 16000.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean d(BaseUnit baseUnit, boolean z) {
        if (this.g || !HovercraftUnit.a(this.o, 4, baseUnit) || baseUnit == this) {
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
        this.o.add(baseUnit);
        GameEngine.getInstance().gameUI.deselectUnit(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.TransportUnitInterface
    public void e(BaseUnit baseUnit) {
        if (baseUnit.unitTransportTarget == this) {
            this.o.remove(baseUnit);
            baseUnit.unitTransportTarget = null;
        } else {
            GameEngine.logWarningAndStack("Unit is not being transported");
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void performUnitAction(AbstractUnitAction abstractUnitAction, boolean z) {
        if (abstractUnitAction == q) {
            M();
        }
        if (abstractUnitAction == r) {
            ds();
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.TransportUnitInterface
    /* JADX INFO: renamed from: bB */
    public int getTransportedUnitCount() {
        return this.o.size();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cr */
    public boolean canTransportUnits() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cp */
    public ActionId getUnitAIPathfindPath() {
        return q.getActionId();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return s;
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
        return this.o;
    }
}
