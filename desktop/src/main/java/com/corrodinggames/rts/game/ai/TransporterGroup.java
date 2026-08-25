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
    /* JADX INFO: renamed from: d */
    BaseZone zone;
    float e;
    /* JADX INFO: renamed from: f */
    float repositionTimer;
    /* JADX INFO: renamed from: g */
    float pickupSearchTimer;
    /* JADX INFO: renamed from: h */
    float updateTimer;
    /* JADX INFO: renamed from: i */
    float requestTimer;
    /* JADX INFO: renamed from: j */
    float commandTimer;
    /* JADX INFO: renamed from: k */
    float retryTimer;
    /* JADX INFO: renamed from: l */
    int capacity;
    /* JADX INFO: renamed from: m */
    AIUnitGroupBase unitGroup;
    /* JADX INFO: renamed from: n */
    OrderableUnit transportUnit;
    float o;
    boolean p;
    /* JADX INFO: renamed from: q */
    boolean isWaitingForUnits;
    /* JADX INFO: renamed from: r */
    float waitPosX;
    /* JADX INFO: renamed from: s */
    float waitPosY;

    public TransporterGroup(AIController aIController) {
        super(aIController);
        this.e = 100.0f;
        this.repositionTimer = 4000.0f;
        this.pickupSearchTimer = 100.0f;
        this.o = 0.0f;
        this.p = false;
    }

    @Override // com.corrodinggames.rts.game.ai.AIStrategyNode, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.a);
        gameOutputStream.writeInt(this.b);
        gameOutputStream.writeInt(this.c);
        gameOutputStream.writeInt(this.F.size());
        Iterator it = this.F.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeOrderableUnit((OrderableUnit) it.next());
        }
        gameOutputStream.writeByte(5);
        gameOutputStream.writeInt(this.aiController.filterUnitAndCommand(this.unitGroup));
        gameOutputStream.writeBoolean(this.isWaitingForUnits);
        gameOutputStream.writeOrderableUnit(this.transportUnit);
        gameOutputStream.writeFloat(this.o);
        gameOutputStream.writeBoolean(this.p);
        gameOutputStream.writeFloat(this.waitPosX);
        gameOutputStream.writeFloat(this.waitPosY);
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
            OrderableUnit unitEntity = gameInputStream.readOrderableUnit();
            if (unitEntity != null) {
                if (!this.aiController.isNonCombatCustomUnit(unitEntity)) {
                    GameEngine.logColored("TransporterGroup:readIn: Unit is not transporterUnit");
                } else {
                    a(unitEntity);
                }
            }
        }
        byte b = gameInputStream.readByte();
        if (b >= 1) {
            this.unitGroup = (AIUnitGroupBase) this.aiController.findNodeById(gameInputStream.readInt());
        }
        if (b >= 2) {
            this.isWaitingForUnits = gameInputStream.readBoolean();
        }
        if (b >= 3) {
            this.transportUnit = gameInputStream.readOrderableUnit();
        }
        if (b >= 4) {
            this.o = gameInputStream.readFloat();
            this.p = gameInputStream.readBoolean();
        }
        if (b >= 5) {
            this.waitPosX = gameInputStream.readFloat();
            this.waitPosY = gameInputStream.readFloat();
        }
        super.readFromInputStream(gameInputStream);
    }

    public void c() {
        for (BaseUnit baseUnit : BaseUnit.bE) {
            if (!baseUnit.isDead && baseUnit.team == this.aiController && this.capacity > this.F.size() && (baseUnit instanceof OrderableUnit)) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if (!orderableUnit.isAIUnit && orderableUnit.aB == null && this.aiController.isNonCombatCustomUnit(orderableUnit) && this.aiController.isEligibleUnitForRandomSelection(orderableUnit)) {
                    a(orderableUnit);
                }
            }
        }
    }

    public boolean d() {
        return this.unitGroup != null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.corrodinggames.rts.game.ai.AIUnitGroupBase
    public void c(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        this.updateTimer += f;
        n();
        if (this.capacity <= this.F.size()) {
        }
        this.requestTimer = Utility.moveTowardsZero(this.requestTimer, f);
        this.commandTimer = Utility.moveTowardsZero(this.commandTimer, f);
        this.retryTimer = Utility.moveTowardsZero(this.retryTimer, f);
        if (!d() && !this.isWaitingForUnits && this.capacity > this.F.size() && this.requestTimer == 0.0f) {
            this.requestTimer = 300.0f;
            c();
        }
        if (!d() && this.F.size() != 0) {
            if (!d()) {
                this.repositionTimer = Utility.moveTowardsZero(this.repositionTimer, f);
                if (this.repositionTimer == 0.0f) {
                    this.repositionTimer = 4000.0f;
                    if (this.zone != null) {
                        PointF pointFW = this.zone.getRandomPointInside();
                        this.posX = pointFW.x;
                        this.posY = pointFW.y;
                    }
                }
            }
            if (this.commandTimer == 0.0f) {
                this.commandTimer = 400.0f;
                Command commandNewCommandForTeam = gameEngine.commandController.newCommandForTeam(this.aiController);
                for (OrderableUnit orderableUnit : this.F) {
                    if (getDistanceSqToUnit(orderableUnit) > 28900.0f && !orderableUnit.isAttackCommandActive()) {
                        commandNewCommandForTeam.addUnitToCommand(orderableUnit);
                    } else if (((TransportUnitInterface) orderableUnit).getTransportedUnitCount() != 0) {
                        ActionId actionIdCp = orderableUnit.getUnloadActionId();
                        Command commandNewCommandForTeam2 = gameEngine.commandController.newCommandForTeam(this.aiController);
                        commandNewCommandForTeam2.addUnitToCommand(orderableUnit);
                        commandNewCommandForTeam2.setActionId(actionIdCp);
                    }
                }
                commandNewCommandForTeam.setMoveTarget(this.posX, this.posY);
            }
            if (this.unitGroup == null) {
                this.pickupSearchTimer = Utility.moveTowardsZero(this.pickupSearchTimer, f);
                if (this.pickupSearchTimer == 0.0f) {
                    this.pickupSearchTimer = 100.0f;
                    if (Utility.getRandomIntInRange(0, 100) < 80) {
                        a(f, true);
                    }
                    if (this.unitGroup == null) {
                        a(f, false);
                    }
                }
            }
        }
        if (this.unitGroup != null && this.unitGroup.isDestroyed) {
            this.unitGroup = null;
        }
        if (!this.isWaitingForUnits) {
            if (this.unitGroup != null) {
                ArrayList<OrderableUnit> arrayList = this.unitGroup.G;
                if (this.transportUnit != null && (this.transportUnit.isDead || this.transportUnit.transportContainer != null || this.transportUnit.parentEntity != null)) {
                    arrayList.remove(this.transportUnit);
                    this.transportUnit = null;
                }
                if (this.transportUnit == null) {
                    for (OrderableUnit orderableUnit2 : arrayList) {
                        if (orderableUnit2.transportContainer == null) {
                            Iterator it = this.F.iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    if (((OrderableUnit) it.next()).d((BaseUnit) orderableUnit2, false)) {
                                        this.transportUnit = orderableUnit2;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    if (this.transportUnit == null) {
                        this.isWaitingForUnits = true;
                        this.commandTimer = 0.0f;
                        this.retryTimer = 0.0f;
                        this.waitPosX = this.unitGroup.posX;
                        this.waitPosY = this.unitGroup.posY;
                    }
                }
                if (this.transportUnit != null) {
                    if (this.commandTimer == 0.0f) {
                        this.commandTimer = 400.0f;
                        Command commandNewCommandForTeam3 = gameEngine.commandController.newCommandForTeam(this.aiController);
                        Iterator it2 = this.F.iterator();
                        while (it2.hasNext()) {
                            commandNewCommandForTeam3.addUnitToCommand((OrderableUnit) it2.next());
                        }
                        commandNewCommandForTeam3.setMoveTarget(this.transportUnit.posX, this.transportUnit.posY);
                    }
                    if (this.retryTimer == 0.0f) {
                        this.retryTimer = 80.0f;
                        for (OrderableUnit orderableUnit3 : arrayList) {
                            Iterator<OrderableUnit> it3 = this.F.iterator();
                            while (true) {
                                if (it3.hasNext()) {
                                    OrderableUnit orderableUnit4 = it3.next();
                                    if (orderableUnit4.d(orderableUnit3, false) && Utility.distanceSq(orderableUnit4.posX, orderableUnit4.posY, orderableUnit3.posX, orderableUnit3.posY) < 14400.0f) {
                                        Command commandNewCommandForTeam4 = gameEngine.commandController.newCommandForTeam(this.aiController);
                                        commandNewCommandForTeam4.addUnitToCommand(orderableUnit3);
                                        commandNewCommandForTeam4.setLoadIntoTarget(orderableUnit4);
                                        break;
                                    }
                                }else break;
                            }
                        }
                        boolean z = false;
                        Iterator it4 = this.F.iterator();
                        while (it4.hasNext()) {
                            if (((OrderableUnit) it4.next()).d((BaseUnit) this.transportUnit, false)) {
                                z = true;
                            }
                        }
                        if (!z) {
                            this.transportUnit = null;
                        }
                    }
                }
            }
        } else if (this.unitGroup == null) {
            e();
        } else {
            if (this.commandTimer == 0.0f) {
                this.commandTimer = 400.0f;
                float fRandomFloatInRange = this.unitGroup.posX + Utility.randomFloatInRange(-40.0f, 40.0f);
                float fRandomFloatInRange2 = this.unitGroup.posY + Utility.randomFloatInRange(-40.0f, 40.0f);
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
                    this.commandTimer = 30.0f;
                } else {
                    this.waitPosX = fRandomFloatInRange;
                    this.waitPosY = fRandomFloatInRange2;
                    Command commandNewCommandForTeam5 = gameEngine.commandController.newCommandForTeam(this.aiController);
                    for (OrderableUnit orderableUnit5 : this.F) {
                        if (((TransportUnitInterface) orderableUnit5).getTransportedUnitCount() == 0) {
                            Command commandNewCommandForTeam6 = gameEngine.commandController.newCommandForTeam(this.aiController);
                            commandNewCommandForTeam6.addUnitToCommand(orderableUnit5);
                            commandNewCommandForTeam6.setMoveTarget(this.posX, this.posY);
                        } else if (Utility.distanceSq(orderableUnit5.posX, orderableUnit5.posY, this.waitPosX, this.waitPosY) > 1600.0f) {
                            commandNewCommandForTeam5.addUnitToCommand(orderableUnit5);
                        }
                    }
                    commandNewCommandForTeam5.setMoveTarget(this.waitPosX, this.waitPosY);
                }
            }
            if (this.retryTimer == 0.0f) {
                this.retryTimer = 100.0f;
                for (OrderableUnit orderableUnit6 : this.F) {
                    if (Utility.distanceSq(orderableUnit6.posX, orderableUnit6.posY, this.waitPosX, this.waitPosY) < 6400.0f) {
                        this.p = true;
                        ActionId actionIdCp2 = orderableUnit6.getUnloadActionId();
                        Command commandNewCommandForTeam7 = gameEngine.commandController.newCommandForTeam(this.aiController);
                        commandNewCommandForTeam7.addUnitToCommand(orderableUnit6);
                        commandNewCommandForTeam7.setActionId(actionIdCp2);
                    }
                }
            }
            if (this.p) {
                this.unitGroup.o();
                this.o += f;
            }
            boolean z2 = false;
            for (OrderableUnit orderableUnit7 : this.F) {
                if (!orderableUnit7.isDead && ((TransportUnitInterface) orderableUnit7).getTransportedUnitCount() != 0) {
                    z2 = true;
                }
            }
            if (!z2 || this.o > 1700.0f) {
                e();
            }
        }
        if (this.updateTimer > 1500.0f && this.F.size() == 0) {
            destroy();
        }
    }

    public void e() {
        this.isWaitingForUnits = false;
        this.unitGroup = null;
        this.o = 0.0f;
        this.commandTimer = 0.0f;
        this.retryTimer = 0.0f;
        this.p = false;
        f();
    }

    public void a(float f, boolean z) {
        for (AIStrategyNode aIStrategyNode : this.aiController.activeStrategies) {
            if ((aIStrategyNode instanceof AIUnitGroupBase) && !(aIStrategyNode instanceof TransporterGroup) && (!z || (aIStrategyNode instanceof RallyGroup))) {
                AIUnitGroupBase aIUnitGroupBase = (AIUnitGroupBase) aIStrategyNode;
                if (aIUnitGroupBase.G.size() != 0 && !aIUnitGroupBase.m()) {
                    this.unitGroup = aIUnitGroupBase;
                    this.transportUnit = null;
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
            this.zone = a(true);
            if (this.zone == null) {
                this.zone = a(false);
            }
            if (this.zone != null) {
                randomTilePosition = this.zone.getRandomPointInside();
            }
        }
        if (randomTilePosition == null) {
            randomTilePosition = this.aiController.getRandomTilePosition();
            this.zone = null;
        }
        this.posX = randomTilePosition.x;
        this.posY = randomTilePosition.y;
    }
}
