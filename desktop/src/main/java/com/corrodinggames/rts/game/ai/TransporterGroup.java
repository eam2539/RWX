package com.corrodinggames.rts.game.ai;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.TransportUnitInterface;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.gameFramework.Command;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/n.class */
public class TransporterGroup extends AIUnitGroupBase {
    boolean a;
    int b;
    int c;
    BaseZone d;
    float e;
    float f;
    float g;
    float h;
    float i;
    float j;
    float k;
    int l;
    AIUnitGroupBase m;
    OrderableUnit n;
    float o;
    boolean p;
    boolean q;
    float r;
    float s;

    @Override // com.corrodinggames.rts.game.ai.AIStrategyNode, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.a);
        gameOutputStream.writeInt(this.b);
        gameOutputStream.writeInt(this.c);
        gameOutputStream.writeInt(this.F.size());
        Iterator it = this.F.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeUnitIdOrNullUnitEntity((OrderableUnit) it.next());
        }
        gameOutputStream.writeByte(5);
        gameOutputStream.writeInt(this.aiController.filterUnitAndCommand(this.m));
        gameOutputStream.writeBoolean(this.q);
        gameOutputStream.writeUnitIdOrNullUnitEntity(this.n);
        gameOutputStream.writeFloat(this.o);
        gameOutputStream.writeBoolean(this.p);
        gameOutputStream.writeFloat(this.r);
        gameOutputStream.writeFloat(this.s);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.ai.AIStrategyNode
    /* JADX INFO: renamed from: a */
    public void readFromInputStream(GameInputStream gameInputStream) throws IOException {
        this.a = gameInputStream.readBoolean();
        this.b = gameInputStream.readInt();
        this.c = gameInputStream.readInt();
        q();
        int i = gameInputStream.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            OrderableUnit unitEntity = gameInputStream.readUnitEntity();
            if (unitEntity != null) {
                if (!this.aiController.isNonCombatCustomUnit(unitEntity)) {
                    GameEngine.updatePaintTextSizeIfNeeded("TransporterGroup:readIn: Unit is not transporterUnit");
                } else {
                    a(unitEntity);
                }
            }
        }
        byte b = gameInputStream.readByte();
        if (b >= 1) {
            this.m = (AIUnitGroupBase) this.aiController.findNodeById(gameInputStream.readInt());
        }
        if (b >= 2) {
            this.q = gameInputStream.readBoolean();
        }
        if (b >= 3) {
            this.n = gameInputStream.readUnitEntity();
        }
        if (b >= 4) {
            this.o = gameInputStream.readFloat();
            this.p = gameInputStream.readBoolean();
        }
        if (b >= 5) {
            this.r = gameInputStream.readFloat();
            this.s = gameInputStream.readFloat();
        }
        super.readFromInputStream(gameInputStream);
    }

    public TransporterGroup(AIController aIController) {
        super(aIController);
        this.e = 100.0f;
        this.f = 4000.0f;
        this.g = 100.0f;
        this.o = 0.0f;
        this.p = false;
    }

    public void c() {
        for (BaseUnit baseUnit : BaseUnit.bE) {
            if (!baseUnit.isDestroyed && baseUnit.team == this.aiController && this.l > this.F.size() && (baseUnit instanceof OrderableUnit)) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if (!orderableUnit.isAIUnit && orderableUnit.aB == null && this.aiController.isNonCombatCustomUnit(orderableUnit) && this.aiController.isEligibleUnitForRandomSelection(orderableUnit)) {
                    a(orderableUnit);
                }
            }
        }
    }

    public boolean d() {
        return this.m != null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.corrodinggames.rts.game.ai.AIUnitGroupBase
    public void c(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        this.h += f;
        n();
        if (this.l <= this.F.size()) {
        }
        this.i = Utility.moveTowardsZero(this.i, f);
        this.j = Utility.moveTowardsZero(this.j, f);
        this.k = Utility.moveTowardsZero(this.k, f);
        if (!d() && !this.q && this.l > this.F.size() && this.i == 0.0f) {
            this.i = 300.0f;
            c();
        }
        if (!d() && this.F.size() != 0) {
            if (!d()) {
                this.f = Utility.moveTowardsZero(this.f, f);
                if (this.f == 0.0f) {
                    this.f = 4000.0f;
                    if (this.d != null) {
                        PointF pointFW = this.d.getRandomPointInside();
                        this.posX = pointFW.x;
                        this.posY = pointFW.y;
                    }
                }
            }
            if (this.j == 0.0f) {
                this.j = 400.0f;
                Command commandNewCommandForTeam = gameEngine.commandController.newCommandForTeam(this.aiController);
                for (OrderableUnit orderableUnit : this.F) {
                    if (getDistanceSqToUnit(orderableUnit) > 28900.0f && !orderableUnit.isAttackCommandActive()) {
                        commandNewCommandForTeam.setTargetUnit(orderableUnit);
                    } else if (((TransportUnitInterface) orderableUnit).getTransportedUnitCount() != 0) {
                        ActionId actionIdCp = orderableUnit.getUnitAIPathfindPath();
                        Command commandNewCommandForTeam2 = gameEngine.commandController.newCommandForTeam(this.aiController);
                        commandNewCommandForTeam2.setTargetUnit(orderableUnit);
                        commandNewCommandForTeam2.setActionId(actionIdCp);
                    }
                }
                commandNewCommandForTeam.setMoveTarget(this.posX, this.posY);
            }
            if (this.m == null) {
                this.g = Utility.moveTowardsZero(this.g, f);
                if (this.g == 0.0f) {
                    this.g = 100.0f;
                    if (Utility.getRandomIntInRange(0, 100) < 80) {
                        a(f, true);
                    }
                    if (this.m == null) {
                        a(f, false);
                    }
                }
            }
        }
        if (this.m != null && this.m.isDestroyed) {
            this.m = null;
        }
        if (!this.q) {
            if (this.m != null) {
                ArrayList<OrderableUnit> arrayList = this.m.G;
                if (this.n != null && (this.n.isDestroyed || this.n.unitTransportTarget != null || this.n.parentEntity != null)) {
                    arrayList.remove(this.n);
                    this.n = null;
                }
                if (this.n == null) {
                    for (OrderableUnit orderableUnit2 : arrayList) {
                        if (orderableUnit2.unitTransportTarget == null) {
                            Iterator it = this.F.iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    if (((OrderableUnit) it.next()).d((BaseUnit) orderableUnit2, false)) {
                                        this.n = orderableUnit2;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    if (this.n == null) {
                        this.q = true;
                        this.j = 0.0f;
                        this.k = 0.0f;
                        this.r = this.m.posX;
                        this.s = this.m.posY;
                    }
                }
                if (this.n != null) {
                    if (this.j == 0.0f) {
                        this.j = 400.0f;
                        Command commandNewCommandForTeam3 = gameEngine.commandController.newCommandForTeam(this.aiController);
                        Iterator it2 = this.F.iterator();
                        while (it2.hasNext()) {
                            commandNewCommandForTeam3.setTargetUnit((OrderableUnit) it2.next());
                        }
                        commandNewCommandForTeam3.setMoveTarget(this.n.posX, this.n.posY);
                    }
                    if (this.k == 0.0f) {
                        this.k = 80.0f;
                        for (OrderableUnit orderableUnit3 : arrayList) {
                            Iterator it3 = this.F.iterator();
                            while (true) {
                                if (it3.hasNext()) {
                                    OrderableUnit orderableUnit4 = (OrderableUnit) it3.next();
                                    if (orderableUnit4.d((BaseUnit) orderableUnit3, false) && Utility.distanceSq(orderableUnit4.posX, orderableUnit4.posY, orderableUnit3.posX, orderableUnit3.posY) < 14400.0f) {
                                        Command commandNewCommandForTeam4 = gameEngine.commandController.newCommandForTeam(this.aiController);
                                        commandNewCommandForTeam4.setTargetUnit(orderableUnit3);
                                        commandNewCommandForTeam4.setLoadIntoTarget(orderableUnit4);
                                        break;
                                    }
                                }
                            }
                        }
                        boolean z = false;
                        Iterator it4 = this.F.iterator();
                        while (it4.hasNext()) {
                            if (((OrderableUnit) it4.next()).d((BaseUnit) this.n, false)) {
                                z = true;
                            }
                        }
                        if (!z) {
                            this.n = null;
                        }
                    }
                }
            }
        } else if (this.m == null) {
            e();
        } else {
            if (this.j == 0.0f) {
                this.j = 400.0f;
                float fRandomFloatInRange = this.m.posX + Utility.randomFloatInRange(-40.0f, 40.0f);
                float fRandomFloatInRange2 = this.m.posY + Utility.randomFloatInRange(-40.0f, 40.0f);
                if (this.o > 600.0f) {
                    fRandomFloatInRange += Utility.randomFloatInRange(-300.0f, 300.0f);
                    fRandomFloatInRange2 += Utility.randomFloatInRange(-300.0f, 300.0f);
                }
                if (this.o > 1200.0f) {
                    fRandomFloatInRange += Utility.randomFloatInRange(-300.0f, 300.0f);
                    fRandomFloatInRange2 += Utility.randomFloatInRange(-300.0f, 300.0f);
                }
                if (GameViewUtils.a(fRandomFloatInRange, fRandomFloatInRange2, UnitMovementType.LAND)) {
                    fRandomFloatInRange += Utility.randomFloatInRange(-100.0f, 100.0f);
                    fRandomFloatInRange2 += Utility.randomFloatInRange(-100.0f, 100.0f);
                }
                if (GameViewUtils.a(fRandomFloatInRange, fRandomFloatInRange2, UnitMovementType.LAND)) {
                    fRandomFloatInRange += Utility.randomFloatInRange(-200.0f, 200.0f);
                    fRandomFloatInRange2 += Utility.randomFloatInRange(-200.0f, 200.0f);
                }
                if (GameViewUtils.a(fRandomFloatInRange, fRandomFloatInRange2, UnitMovementType.LAND)) {
                    fRandomFloatInRange += Utility.randomFloatInRange(-200.0f, 200.0f);
                    fRandomFloatInRange2 += Utility.randomFloatInRange(-200.0f, 200.0f);
                }
                if (GameViewUtils.a(fRandomFloatInRange, fRandomFloatInRange2, UnitMovementType.LAND)) {
                    this.j = 30.0f;
                } else {
                    this.r = fRandomFloatInRange;
                    this.s = fRandomFloatInRange2;
                    Command commandNewCommandForTeam5 = gameEngine.commandController.newCommandForTeam(this.aiController);
                    for (OrderableUnit orderableUnit5 : this.F) {
                        if (((TransportUnitInterface) orderableUnit5).getTransportedUnitCount() == 0) {
                            Command commandNewCommandForTeam6 = gameEngine.commandController.newCommandForTeam(this.aiController);
                            commandNewCommandForTeam6.setTargetUnit(orderableUnit5);
                            commandNewCommandForTeam6.setMoveTarget(this.posX, this.posY);
                        } else if (Utility.distanceSq(orderableUnit5.posX, orderableUnit5.posY, this.r, this.s) > 1600.0f) {
                            commandNewCommandForTeam5.setTargetUnit(orderableUnit5);
                        }
                    }
                    commandNewCommandForTeam5.setMoveTarget(this.r, this.s);
                }
            }
            if (this.k == 0.0f) {
                this.k = 100.0f;
                for (OrderableUnit orderableUnit6 : this.F) {
                    if (Utility.distanceSq(orderableUnit6.posX, orderableUnit6.posY, this.r, this.s) < 6400.0f) {
                        this.p = true;
                        ActionId actionIdCp2 = orderableUnit6.getUnitAIPathfindPath();
                        Command commandNewCommandForTeam7 = gameEngine.commandController.newCommandForTeam(this.aiController);
                        commandNewCommandForTeam7.setTargetUnit(orderableUnit6);
                        commandNewCommandForTeam7.setActionId(actionIdCp2);
                    }
                }
            }
            if (this.p) {
                this.m.o();
                this.o += f;
            }
            boolean z2 = false;
            for (OrderableUnit orderableUnit7 : this.F) {
                if (!orderableUnit7.isDestroyed && ((TransportUnitInterface) orderableUnit7).getTransportedUnitCount() != 0) {
                    z2 = true;
                }
            }
            if (!z2 || this.o > 1700.0f) {
                e();
            }
        }
        if (this.h > 1500.0f && this.F.size() == 0) {
            destroy();
        }
    }

    public void e() {
        this.q = false;
        this.m = null;
        this.o = 0.0f;
        this.j = 0.0f;
        this.k = 0.0f;
        this.p = false;
        f();
    }

    public void a(float f, boolean z) {
        for (AIStrategyNode aIStrategyNode : this.aiController.activeStrategies) {
            if ((aIStrategyNode instanceof AIUnitGroupBase) && !(aIStrategyNode instanceof TransporterGroup) && (!z || (aIStrategyNode instanceof RallyGroup))) {
                AIUnitGroupBase aIUnitGroupBase = (AIUnitGroupBase) aIStrategyNode;
                if (aIUnitGroupBase.G.size() != 0 && !aIUnitGroupBase.m()) {
                    this.m = aIUnitGroupBase;
                    this.n = null;
                    return;
                }
            }
        }
    }

    public BaseZone a(boolean z) {
        BaseZone baseZone = null;
        for (AIStrategyNode aIStrategyNode : this.aiController.activeStrategies) {
            if (aIStrategyNode instanceof BaseZone) {
                BaseZone baseZone2 = (BaseZone) aIStrategyNode;
                if (!baseZone2.isUnderAttack || !z) {
                    if (baseZone2.stage == BaseZoneStage.Active) {
                        baseZone = baseZone2;
                        if (Utility.getRandomInt(3) == 0) {
                            return baseZone;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return baseZone;
    }

    public void f() {
        PointF randomTilePosition = null;
        if (1 != 0) {
            this.d = a(true);
            if (this.d == null) {
                this.d = a(false);
            }
            if (this.d != null) {
                randomTilePosition = this.d.getRandomPointInside();
            }
        }
        if (randomTilePosition == null) {
            randomTilePosition = this.aiController.getRandomTilePosition();
            this.d = null;
        }
        this.posX = randomTilePosition.x;
        this.posY = randomTilePosition.y;
    }
}
