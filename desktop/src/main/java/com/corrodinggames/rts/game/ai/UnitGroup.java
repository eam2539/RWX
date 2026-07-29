package com.corrodinggames.rts.game.ai;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.PathfindingUtils;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.sea.WaterUnit;
import com.corrodinggames.rts.gameFramework.Command;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import java.io.IOException;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/g.class */
public class UnitGroup extends AIUnitGroupBase {
    boolean a;
    String b;
    boolean c;
    boolean d;
    boolean e;
    boolean f;
    OrderableUnit g;
    boolean h;
    int i;
    int j;
    BaseZone k;
    float l;
    float m;
    float n;
    float o;
    float p;
    boolean q;
    boolean r;
    boolean s;
    float t;
    float u;
    boolean v;
    BaseUnit w;
    float x;
    float y;
    float z;
    int A;
    boolean B;
    public int C;
    public BaseUnit D;
    UnitMovementType E;

    @Override // com.corrodinggames.rts.game.ai.AIUnitGroupBase
    public boolean a() {
        return this.a;
    }

    @Override // com.corrodinggames.rts.game.ai.AIUnitGroupBase
    public boolean b() {
        if (!this.h) {
            return true;
        }
        return false;
    }

    public static UnitGroup a(AIController aIController, OrderableUnit orderableUnit) {
        UnitGroup unitGroup = new UnitGroup(aIController, false);
        unitGroup.a = true;
        unitGroup.c = true;
        unitGroup.d = true;
        unitGroup.e = true;
        unitGroup.g = orderableUnit;
        unitGroup.a(orderableUnit);
        unitGroup.A = 0;
        unitGroup.k();
        return unitGroup;
    }

    @Override // com.corrodinggames.rts.game.ai.AIStrategyNode, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.h);
        gameOutputStream.writeInt(this.i);
        gameOutputStream.writeInt(this.j);
        gameOutputStream.writeInt(this.F.size());
        Iterator it = this.F.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeOrderableUnit((OrderableUnit) it.next());
        }
        gameOutputStream.writeByte(7);
        gameOutputStream.writeBoolean(false);
        gameOutputStream.writeBoolean(this.s);
        gameOutputStream.writeFloat(this.o);
        gameOutputStream.writeInt(this.G.size());
        Iterator it2 = this.G.iterator();
        while (it2.hasNext()) {
            gameOutputStream.writeOrderableUnit((OrderableUnit) it2.next());
        }
        gameOutputStream.writeBoolean(this.B);
        gameOutputStream.writeBoolean(this.a);
        gameOutputStream.writeBoolean(this.c);
        gameOutputStream.writeBoolean(this.d);
        gameOutputStream.writeBoolean(this.e);
        gameOutputStream.writeBoolean(this.f);
        gameOutputStream.writeOrderableUnit(this.g);
        gameOutputStream.writeInt(this.A);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.ai.AIStrategyNode
    /* JADX INFO: renamed from: a */
    public void readFromInputStream(GameInputStream gameInputStream) throws IOException {
        this.h = gameInputStream.readBoolean();
        this.i = gameInputStream.readInt();
        this.j = gameInputStream.readInt();
        q();
        int i = gameInputStream.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            OrderableUnit unitEntity = gameInputStream.readOrderableUnit();
            if (unitEntity != null) {
                a(unitEntity);
            }
        }
        byte b = gameInputStream.readByte();
        if (b >= 1) {
            gameInputStream.readBoolean();
        }
        if (b >= 2) {
            this.s = gameInputStream.readBoolean();
        }
        if (b >= 3) {
            this.o = gameInputStream.readFloat();
        }
        if (b >= 4) {
            this.G.clear();
            int i3 = gameInputStream.readInt();
            for (int i4 = 0; i4 < i3; i4++) {
                OrderableUnit unitEntity2 = gameInputStream.readOrderableUnit();
                if (unitEntity2 != null) {
                    this.G.add(unitEntity2);
                }
            }
        }
        if (b >= 5) {
            this.B = gameInputStream.readBoolean();
        }
        if (b >= 6) {
            this.a = gameInputStream.readBoolean();
            this.c = gameInputStream.readBoolean();
            this.d = gameInputStream.readBoolean();
            this.e = gameInputStream.readBoolean();
            this.f = gameInputStream.readBoolean();
            this.g = gameInputStream.readOrderableUnit();
        }
        if (b >= 7) {
            this.A = gameInputStream.readInt();
        }
        if (!this.B) {
            Iterator it = this.F.iterator();
            while (it.hasNext()) {
                OrderableUnit orderableUnit = (OrderableUnit) it.next();
                if (orderableUnit instanceof WaterUnit) {
                    if (orderableUnit != null && orderableUnit.aB == this) {
                        orderableUnit.aB = null;
                    }
                    if (orderableUnit != null) {
                        this.G.remove(orderableUnit);
                    }
                    it.remove();
                }
            }
        }
        super.readFromInputStream(gameInputStream);
    }

    public UnitGroup(AIController aIController) {
        super(aIController);
        this.h = true;
        this.l = 1000.0f;
        this.m = 100.0f;
        this.n = 4000.0f;
        this.o = 0.0f;
        this.p = 1000.0f;
        this.q = false;
        this.r = false;
        this.s = false;
        this.t = 0.0f;
        this.u = 0.0f;
        this.C = -9999;
        this.D = null;
        this.E = UnitMovementType.NONE;
    }

    public UnitGroup(AIController aIController, boolean z) {
        this(aIController);
        this.h = z;
    }

    @Override // com.corrodinggames.rts.game.ai.AIUnitGroupBase
    protected void a(OrderableUnit orderableUnit) {
        super.a(orderableUnit);
        this.E = j();
    }

    public void c() {
        for (BaseUnit baseUnit : BaseUnit.bE) {
            if (!baseUnit.isDead && baseUnit.team == this.aiController && this.A > this.F.size() && (baseUnit instanceof OrderableUnit)) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if (!orderableUnit.isActive && !orderableUnit.isAIUnit && orderableUnit.aB == null && this.aiController.isCombatCustomUnit(orderableUnit) && this.aiController.isEligibleUnitForRandomSelection(orderableUnit)) {
                    if (this.B) {
                        if (baseUnit.h() != UnitMovementType.LAND) {
                            if (!this.aiController.isPathPossibleForUnit(orderableUnit, this.posX, this.posY) || (!b() && Utility.getRandomIntInRange(0, 100) <= 2)) {
                                a(orderableUnit);
                            }
                        }
                    } else if (baseUnit.h() != UnitMovementType.WATER) {
                        if (!this.aiController.isPathPossibleForUnit(orderableUnit, this.posX, this.posY)) {
                        }
                        a(orderableUnit);
                    }
                }
            }
        }
    }

    public boolean d() {
        if (this.A <= this.F.size()) {
            return true;
        }
        return false;
    }

    public BaseUnit a(float f) {
        if (GameEngine.getInstance().gameTimeMillis - (f * 1000.0f) < this.C) {
            return this.D;
        }
        return null;
    }

    public BaseUnit e() {
        BaseUnit baseUnitA = a(6.0f);
        if (baseUnitA != null) {
            return baseUnitA;
        }
        return null;
    }

    public BaseUnit f() {
        Iterator it = this.F.iterator();
        while (it.hasNext()) {
            BaseUnit commandOrAttackTarget = ((OrderableUnit) it.next()).getCommandOrAttackTarget();
            if (commandOrAttackTarget != null) {
                return commandOrAttackTarget;
            }
        }
        return null;
    }

    public void a(Command command, boolean z, BaseUnit baseUnit) {
        for (OrderableUnit orderableUnit : this.F) {
            if (!z || orderableUnit.hasNoCurrentWaypoint()) {
                if (baseUnit == null || this.aiController.canUnitReachUnit(orderableUnit, baseUnit)) {
                    command.addUnitToCommand(orderableUnit);
                }
            }
        }
    }

    public void a(String str) {
        this.b = str;
    }

    public PointF a(BaseUnit baseUnit) {
        PointF pointF = new PointF();
        pointF.x = this.posX;
        pointF.y = this.posY;
        float fRandom = (float) (Math.random() * 360.0d);
        float fRandomFloatInRange = Utility.randomFloatInRange(50.0f, 100.0f);
        pointF.x += Utility.fastCos(fRandom) * fRandomFloatInRange;
        pointF.y += Utility.fastSin(fRandom) * fRandomFloatInRange;
        if (baseUnit != null) {
            float angleBetweenPoints = Utility.getAngleBetweenPoints(pointF.x, pointF.y, baseUnit.posX, baseUnit.posY);
            float fRandomFloatInRange2 = Utility.randomFloatInRange(100.0f, 200.0f);
            pointF.x += Utility.fastCos(angleBetweenPoints) * (-fRandomFloatInRange2);
            pointF.y += Utility.fastSin(angleBetweenPoints) * (-fRandomFloatInRange2);
        }
        return pointF;
    }

    @Override // com.corrodinggames.rts.game.ai.AIUnitGroupBase
    public void b(float f) {
        BaseUnit baseUnitE;
        super.b(f);
        n();
        this.E = j();
        if (!this.f && (baseUnitE = e()) != null && f() == null) {
            if (a(baseUnitE, false)) {
                a("fighting attacker");
                Command commandNewCommandForTeam = GameEngine.getInstance().commandController.newCommandForTeam(this.aiController);
                a(commandNewCommandForTeam, true, baseUnitE);
                commandNewCommandForTeam.setAttackMoveTarget(baseUnitE.posX, baseUnitE.posY, false);
                return;
            }
            a("flight from attacker");
            PointF pointFA = a(baseUnitE);
            this.posX = pointFA.x;
            this.posY = pointFA.y;
            if (this.z > 200.0f) {
                this.z = 200.0f;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.ai.AIUnitGroupBase
    public void c(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        this.x += f;
        for (OrderableUnit orderableUnit : this.F) {
            if (orderableUnit != null && this.C < orderableUnit.bs) {
                this.C = orderableUnit.bs;
                this.D = orderableUnit.unitTarget1;
            }
        }
        n();
        if (d()) {
            this.l = Utility.moveTowardsZero(this.l, f);
        } else if (this.v) {
        }
        this.y = Utility.moveTowardsZero(this.y, f);
        this.z = Utility.moveTowardsZero(this.z, f);
        this.p = Utility.moveTowardsZero(this.p, f);
        if (!this.v && !this.r && !d() && this.y == 0.0f) {
            this.y = 200 + Utility.getRandomInt(200);
            c();
        }
        if (!this.v || this.q) {
            if (!this.q) {
                this.n = Utility.moveTowardsZero(this.n, f);
                if (this.n == 0.0f) {
                    if (this.k == null) {
                        this.k = g();
                    }
                    if (this.k != null) {
                        PointF pointFW = this.k.getRandomPointInside();
                        if (!a(pointFW.x, pointFW.y)) {
                            this.n = 100.0f;
                            a("random move: bad target");
                        } else {
                            this.n = 4000.0f;
                            this.posX = pointFW.x;
                            this.posY = pointFW.y;
                            a("random move");
                        }
                    } else {
                        a("random move: no linked base");
                    }
                }
            }
            if (this.z == 0.0f) {
                this.z = 800.0f;
                Command commandNewCommandForTeam = gameEngine.commandController.newCommandForTeam(this.aiController);
                for (OrderableUnit orderableUnit2 : this.F) {
                    boolean z = true;
                    if (getDistanceSqToUnit(orderableUnit2) < 28900.0f) {
                        z = false;
                    }
                    if (!this.f && orderableUnit2.canUnitAttack() && !orderableUnit2.hasNoCurrentWaypoint()) {
                        z = false;
                    }
                    if (z) {
                        commandNewCommandForTeam.addUnitToCommand(orderableUnit2);
                    }
                }
                if (this.f) {
                    commandNewCommandForTeam.setMoveTarget(this.posX, this.posY);
                } else {
                    commandNewCommandForTeam.setAttackMoveTarget(this.posX, this.posY);
                }
            }
        }
        if (this.h) {
            e(f);
        } else {
            d(f);
        }
        if (this.A == 0 && this.F.size() == 0) {
            destroy();
        }
        if (this.c) {
            if (this.g == null || this.g.isDead) {
                destroy();
            }
        }
    }

    BaseZone g() {
        float f = -1.0f;
        BaseZone baseZone = null;
        for (AIStrategyNode aIStrategyNode : this.aiController.activeStrategies) {
            if (aIStrategyNode instanceof BaseZone) {
                BaseZone baseZone2 = (BaseZone) aIStrategyNode;
                if (b(baseZone2.posX, baseZone2.posY)) {
                    float fD = baseZone2.getDistanceSqToPoint(this.posX, this.posY);
                    if (baseZone == null || fD < f) {
                        f = fD;
                        baseZone = baseZone2;
                    }
                }
            }
        }
        return baseZone;
    }

    public void d(float f) {
        if (this.k == null || this.k.isDestroyed) {
            k();
        }
        if (this.c && this.g != null) {
            if (this.e && !this.f) {
                if (this.g.currentHealth / this.g.maxHealth < 0.5d) {
                    this.f = true;
                    if (this.z > 100.0f) {
                        this.z = 100.0f;
                    }
                }
                if (this.w == null) {
                    k();
                }
            } else {
                if (this.g.currentHealth / this.g.maxHealth > 0.6d) {
                    this.f = false;
                }
                boolean z = false;
                if (this.k != null && !this.k.isContested) {
                    z = true;
                }
                if (!z) {
                    BaseZone baseZoneCheckUnitVariableCondition = this.aiController.checkUnitVariableCondition(this.g.h(), this.g.posX, this.g.posY, true);
                    if (baseZoneCheckUnitVariableCondition != null) {
                        this.k = baseZoneCheckUnitVariableCondition;
                    }
                    if (this.k != null) {
                        PointF pointFW = this.k.getRandomPointInside();
                        this.posX = pointFW.x;
                        this.posY = pointFW.y;
                        if (this.z > 100.0f) {
                            this.z = 100.0f;
                        }
                        a("moving to new base");
                    }
                }
            }
        }
        if (this.k != null) {
            for (int i = 0; i < 2; i++) {
                if (this.p == 0.0f) {
                    BaseUnit closestEnemyUnit = this.k.getClosestEnemyUnit();
                    if (closestEnemyUnit == null) {
                        break;
                    }
                    if (a(closestEnemyUnit, false)) {
                        this.w = closestEnemyUnit;
                        this.p = 500.0f;
                        this.n = 2000.0f;
                        if (!this.f) {
                            this.posX = closestEnemyUnit.posX;
                            this.posY = closestEnemyUnit.posY;
                        }
                        if (this.z > 100.0f) {
                            this.z = 100.0f;
                        }
                        a("defending base");
                    }
                }
            }
            if (this.p == 0.0f) {
                this.f = false;
                this.w = null;
            }
        }
    }

    public void e(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.v) {
            if (this.w == null || !this.w.isAlive() || this.w.isDead || !this.r) {
                this.w = this.aiController.getRandomEnemyUnit();
                if (this.w != null && !a(this.w, true)) {
                    this.w = null;
                }
            }
            if (this.w != null) {
                if (this.q) {
                    this.u += f;
                    if (!this.r) {
                        this.t = Utility.moveTowardsZero(this.t, f);
                        if (this.t == 0.0f) {
                            this.t = 20.0f;
                            h();
                        }
                    } else {
                        boolean z = false;
                        Iterator it = this.F.iterator();
                        while (it.hasNext()) {
                            if (getDistanceSqToUnit((OrderableUnit) it.next()) > 28900.0f) {
                                z = true;
                            }
                        }
                        if (!z) {
                            this.q = false;
                        }
                        Iterator it2 = this.F.iterator();
                        while (it2.hasNext()) {
                            if (((OrderableUnit) it2.next()).bs > gameEngine.gameTimeMillis - 1000) {
                                this.q = false;
                                a("Not staging due to damage");
                            }
                        }
                    }
                    if (this.u > 17000.0f) {
                        this.q = false;
                        a("attacking target");
                    }
                } else {
                    this.o += f;
                    if (this.z == 0.0f) {
                        this.z = 800.0f;
                        boolean z2 = false;
                        FastArrayList fastArrayList = new FastArrayList();
                        for (OrderableUnit orderableUnit : this.F) {
                            boolean z3 = true;
                            if (this.w != null) {
                                if (!this.aiController.canUnitReachUnit(orderableUnit, this.w)) {
                                    z3 = false;
                                }
                                if (z3 && !PathfindingUtils.a(orderableUnit, this.w)) {
                                    z3 = false;
                                }
                            }
                            if (z3) {
                                z2 = true;
                                fastArrayList.add(orderableUnit);
                            }
                        }
                        if (!z2) {
                            this.q = false;
                            a("cannot reach main target");
                        } else {
                            Command commandNewCommandForTeam = gameEngine.commandController.newCommandForTeam(this.aiController);
                            commandNewCommandForTeam.addUnitsToCommand(fastArrayList);
                            if (this.w != null && Utility.getRandomIntInRange(0, 100) < 80) {
                                commandNewCommandForTeam.setAttackMoveTarget(this.w.posX, this.w.posY, true);
                            } else {
                                commandNewCommandForTeam.setAttackTarget(this.w, true);
                            }
                            a("attacking main target");
                        }
                    }
                }
            }
        } else if (this.l == 0.0f) {
            this.v = true;
            this.q = true;
        }
        if (this.v) {
            if (this.F.size() == 0) {
                destroy();
            }
            if (this.o > 1000.0f && this.F.size() < 3) {
                destroy();
            }
            if (this.o > 11000.0f) {
                destroy();
            }
        }
    }

    public void h() {
        float f = this.w.posX;
        float f2 = this.w.posY;
        float angleBetweenPoints = Utility.getAngleBetweenPoints(f, f2, this.posX, this.posY);
        float fDistance = Utility.distance(f, f2, this.posX, this.posY);
        if (Utility.getRandomIntInRange(0, 100) < 80) {
            angleBetweenPoints += Utility.getRandomIntInRange(-110, 110);
        }
        int i = (int) (((double) fDistance) * 0.6d);
        if (i < 720) {
            i = 720;
        }
        float randomIntInRange = Utility.getRandomIntInRange(50, i);
        if (Utility.getRandomIntInRange(0, 100) < 80 && randomIntInRange < 450.0f) {
            randomIntInRange = Utility.getRandomIntInRange(450, i);
        }
        float fFastCos = f + (Utility.fastCos(angleBetweenPoints) * randomIntInRange);
        float fFastSin = f2 + (Utility.fastSin(angleBetweenPoints) * randomIntInRange);
        boolean z = true;
        if (!a(fFastCos, fFastSin)) {
            z = false;
        }
        boolean z2 = false;
        boolean z3 = false;
        for (OrderableUnit orderableUnit : this.F) {
            if (orderableUnit.h() == UnitMovementType.LAND) {
                z2 = true;
            }
            if (orderableUnit.h() == UnitMovementType.WATER) {
                z3 = true;
            }
        }
        if (z2) {
            if (this.aiController.activeTransporterGroupCount == 0 && !b(fFastCos, fFastSin)) {
                z = false;
            }
            if (!this.aiController.isPathPossibleBetweenPoints(fFastCos, fFastSin, this.w.posX, this.w.posY, UnitMovementType.LAND) && Utility.getRandomIntInRange(0, 100) < 98) {
                z = false;
            }
        }
        if (z3) {
            if (!b(fFastCos, fFastSin)) {
                z = false;
            }
            if (!this.aiController.isPathPossibleBetweenPoints(fFastCos, fFastSin, this.w.posX, this.w.posY, UnitMovementType.WATER)) {
                z = false;
            }
        }
        if (z) {
            this.posX = fFastCos;
            this.posY = fFastSin;
            this.z = 0.0f;
            this.r = true;
            this.G.clear();
            for (OrderableUnit orderableUnit2 : this.F) {
                if (orderableUnit2.h() != UnitMovementType.WATER && !this.aiController.isPathPossibleForUnit(orderableUnit2, this.posX, this.posY)) {
                    this.G.add(orderableUnit2);
                }
            }
        }
    }

    public UnitMovementType i() {
        return this.E;
    }

    public UnitMovementType j() {
        if (this.F.size() == 0) {
            if (this.B) {
                return UnitMovementType.WATER;
            }
            return UnitMovementType.LAND;
        }
        boolean z = true;
        Iterator it = this.F.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            if (((OrderableUnit) it.next()).h() != UnitMovementType.AIR) {
                z = false;
                break;
            }
        }
        if (z) {
            return UnitMovementType.AIR;
        }
        if (this.B) {
            boolean z2 = true;
            Iterator it2 = this.F.iterator();
            while (it2.hasNext()) {
                if (((OrderableUnit) it2.next()).h() == UnitMovementType.WATER) {
                    z2 = false;
                }
            }
            if (z2) {
                return UnitMovementType.HOVER;
            }
            return UnitMovementType.WATER;
        }
        boolean z3 = true;
        Iterator it3 = this.F.iterator();
        while (it3.hasNext()) {
            UnitMovementType unitMovementTypeH = ((OrderableUnit) it3.next()).h();
            if (unitMovementTypeH == UnitMovementType.LAND || unitMovementTypeH == UnitMovementType.OVER_CLIFF) {
                z3 = false;
            }
        }
        if (z3) {
            return UnitMovementType.HOVER;
        }
        return UnitMovementType.LAND;
    }

    public boolean a(float f, float f2) {
        return !GameViewUtils.a(f, f2, i());
    }

    public boolean b(float f, float f2) {
        Iterator it = this.F.iterator();
        while (it.hasNext()) {
            if (!this.aiController.isPathPossibleForUnit((OrderableUnit) it.next(), f, f2)) {
                return false;
            }
        }
        return true;
    }

    public boolean a(BaseUnit baseUnit, boolean z) {
        for (OrderableUnit orderableUnit : this.F) {
            if (z || this.aiController.isPathPossibleForUnit(orderableUnit, baseUnit.posX, baseUnit.posY)) {
                if (PathfindingUtils.a(orderableUnit, baseUnit)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void k() {
        PointF randomTilePosition = null;
        if (this.c && this.g != null) {
            this.posX = this.g.posX;
            this.posY = this.g.posY;
            this.k = this.aiController.findNearestZone(this.g.posX, this.g.posY);
            return;
        }
        if (1 != 0) {
            int i = 0;
            while (i < 7) {
                boolean z = i > 3;
                if (randomTilePosition == null) {
                    for (AIStrategyNode aIStrategyNode : this.aiController.activeStrategies) {
                        if (aIStrategyNode instanceof BaseZone) {
                            BaseZone baseZone = (BaseZone) aIStrategyNode;
                            if (baseZone.stage == BaseZoneStage.Active && (baseZone.u() > 2 || z)) {
                                if (randomTilePosition == null || Utility.getRandomInt(this.aiController.advancedBaseCount + 2) == 0) {
                                    for (int i2 = 0; i2 < 10; i2++) {
                                        if (randomTilePosition == null) {
                                            PointF pointFW = baseZone.getRandomPointInside();
                                            if (a(pointFW.x, pointFW.y)) {
                                                randomTilePosition = pointFW;
                                            }
                                        }
                                    }
                                    this.k = baseZone;
                                }
                            }
                        }
                    }
                }
                i++;
            }
        }
        if (randomTilePosition == null) {
            randomTilePosition = this.aiController.getRandomTilePosition();
            this.k = null;
        }
        this.posX = randomTilePosition.x;
        this.posY = randomTilePosition.y;
    }
}
