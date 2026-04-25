package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitCommand;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ab */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ab.class */
public class FormationGroup {

    /* JADX INFO: renamed from: a */
    FastArrayList<OrderableUnit> units = new FastArrayList();

    /* JADX INFO: renamed from: b */
    boolean isActive;

    /* JADX INFO: renamed from: c */
    float targetX;

    /* JADX INFO: renamed from: d */
    float targetY;

    /* JADX INFO: renamed from: e */
    int formationId;

    /* JADX INFO: renamed from: f */
    boolean isQueued;

    /* JADX INFO: renamed from: g */
    public FastArrayList<CommandTarget> commandTargets;

    /* JADX INFO: renamed from: h */
    final /* synthetic */ FormationEngine formationEngine;

    public FormationGroup(FormationEngine formationEngine) {
        this.formationEngine = formationEngine;
    }

    public void a(OrderableUnit orderableUnit, UnitCommand unitCommand) {
        unitCommand.transportTarget = this;
        this.isQueued = unitCommand.isQueued;
    }

    public void a(UnitCommand unitCommand) {
        UnitCommand currentWaypoint;
        for (OrderableUnit orderableUnit : this.units) {
            if (!orderableUnit.isDestroyed && (currentWaypoint = orderableUnit.getCurrentWaypoint()) != null && currentWaypoint.isSameCommand(unitCommand)) {
                orderableUnit.advanceWaypoint();
            }
        }
    }

    public void a() {
        UnitCommand currentWaypoint;
        this.units.clear();
        BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
        int size = BaseUnit.bE.size();
        for (int i = 0; i < size; i++) {
            BaseUnit baseUnit = baseUnitArrA[i];
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if (orderableUnit.I() && (currentWaypoint = orderableUnit.getCurrentWaypoint()) != null && currentWaypoint.transportTarget == this && orderableUnit.bg()) {
                    this.units.add(orderableUnit);
                    this.targetX = currentWaypoint.getTargetX();
                    this.targetY = currentWaypoint.getTargetY();
                }
            }
        }
    }

    public void a(OrderableUnit orderableUnit) {
        orderableUnit.waypointSyncGroupId = this.formationId;
        UnitCommand currentWaypoint = orderableUnit.getCurrentWaypoint();
        if (currentWaypoint != null) {
            currentWaypoint.transportTarget = this;
        }
    }

    public void b() {
        PerformanceProfiler.a();
        c();
    }

    public OrderableUnit a(FastArrayList fastArrayList, float f, float f2, boolean z) {
        float f3 = -1.0f;
        OrderableUnit orderableUnit = null;
        Iterator it = fastArrayList.iterator();
        while (it.hasNext()) {
            OrderableUnit orderableUnit2 = (OrderableUnit) it.next();
            if (z || (orderableUnit2.transportedBy == null && !orderableUnit2.ae)) {
                float fDistance = Utility.distance(f, f2, orderableUnit2.posX, orderableUnit2.posY);
                if (orderableUnit2.af) {
                    fDistance -= 160.0f;
                }
                if (f3 == -1.0f || fDistance < f3) {
                    f3 = fDistance;
                    orderableUnit = orderableUnit2;
                }
            }
        }
        return orderableUnit;
    }

    public FastArrayList<OrderableUnit> a(float f, float f2, boolean z) {
        FastArrayList fastArrayList = new FastArrayList(1);
        FastArrayList fastArrayList2 = new FastArrayList();
        fastArrayList2.clear();
        fastArrayList2.addAll(this.units);
        while (true) {
            OrderableUnit orderableUnitA = a(fastArrayList2, f, f2, true);
            if (orderableUnitA != null) {
                fastArrayList.add(orderableUnitA);
                fastArrayList2.remove(orderableUnitA);
                fastArrayList2.removeAll(a(fastArrayList2, orderableUnitA, true, z));
            } else {
                return fastArrayList;
            }
        }
    }

    public FastArrayList<OrderableUnit> a(FastArrayList fastArrayList, OrderableUnit orderableUnit, boolean z, boolean z2) {
        FastArrayList fastArrayList2 = new FastArrayList(1);
        fastArrayList2.clear();
        int i = 0;
        Object[] objArrA = fastArrayList.a();
        int size = fastArrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            ((OrderableUnit) objArrA[i2]).ap = false;
        }
        for (int i3 = 0; i3 <= 2; i3++) {
            int size2 = fastArrayList.size();
            for (int i4 = 0; i4 < size2; i4++) {
                OrderableUnit orderableUnit2 = (OrderableUnit) objArrA[i4];
                if (!orderableUnit2.ap && orderableUnit2 != orderableUnit && ((z || (orderableUnit2.transportedBy == null && !orderableUnit2.ae)) && orderableUnit2.h() == orderableUnit.h())) {
                    float fDistanceSq = Utility.distanceSq(orderableUnit2.posX, orderableUnit2.posY, orderableUnit.posX, orderableUnit.posY);
                    if ((i3 != 0 || fDistanceSq <= 3600.0f) && ((i3 != 1 || fDistanceSq <= 14400.0f) && (((z2 && fDistanceSq < 160000.0f) || (fDistanceSq < 40000.0f && i < 25)) && (z2 || Utility.abs(orderableUnit2.getMoveSpeed() - orderableUnit.getMoveSpeed()) < 0.4f)))) {
                        orderableUnit2.ap = true;
                        fastArrayList2.add(orderableUnit2);
                        i++;
                    }
                }
            }
        }
        return fastArrayList2;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void c() {
        GameEngine gameEngine = GameEngine.getInstance();
        PerformanceProfiler.a();
        a();
        this.formationEngine.b.a(0.0f, 0.0f);
        for (OrderableUnit orderableUnit : this.units) {
            this.formationEngine.b.b(orderableUnit.posX, orderableUnit.posY);
        }
        this.formationEngine.b.a(this.formationEngine.b.x / this.units.size(), this.formationEngine.b.y / this.units.size());
        float angleBetweenPoints = Utility.getAngleBetweenPoints(this.formationEngine.b.x, this.formationEngine.b.y, this.targetX, this.targetY);
        for (OrderableUnit orderableUnit2 : this.units) {
            if (orderableUnit2.ah > 1) {
                orderableUnit2.af = orderableUnit2.ae;
            } else {
                orderableUnit2.af = false;
            }
            if (orderableUnit2.af && orderableUnit2.ah > 7 && Utility.abs(Utility.endsWith(orderableUnit2.am, angleBetweenPoints, 360.0f)) > 80.0f) {
                orderableUnit2.af = false;
            }
            orderableUnit2.clearTransportState();
            orderableUnit2.ah = (short) 0;
            orderableUnit2.lastTransportPathUpdateTick = gameEngine.lastTick;
            orderableUnit2.waypointSyncGroupId = this.formationId;
        }
        int i = 0;
        while (true) {
            PerformanceProfiler.a();
            OrderableUnit orderableUnitA = a(this.units, this.targetX, this.targetY, false);
            if (orderableUnitA == null) {
                return;
            }
            orderableUnitA.ae = true;
            FormationGroup formationGroupB = null;
            if (i > 0) {
                formationGroupB = this.formationEngine.b();
            }
            if (formationGroupB != null) {
                formationGroupB.commandTargets = this.commandTargets;
                formationGroupB.a(orderableUnitA);
            }
            int i2 = 0;
            float f = 0.0f;
            for (OrderableUnit orderableUnit3 : a(this.units, orderableUnitA, false, this.isQueued)) {
                if (orderableUnit3.speed > f) {
                    f = orderableUnit3.speed;
                }
                orderableUnit3.setTransportParent(orderableUnitA);
                if (formationGroupB != null) {
                    formationGroupB.a(orderableUnit3);
                }
                i2++;
            }
            if (orderableUnitA != null) {
                orderableUnitA.ah = (short) (i2 + 1);
            }
            FastArrayList fastArrayList = new FastArrayList();
            Object[] objArrA = this.units.a();
            int size = this.units.size();
            for (int i3 = 0; i3 < size; i3++) {
                OrderableUnit orderableUnit4 = (OrderableUnit) objArrA[i3];
                if (orderableUnit4.transportedBy == orderableUnitA) {
                    fastArrayList.add(orderableUnit4);
                }
            }
            FastArrayList fastArrayListA = this.formationEngine.a(i2, f, angleBetweenPoints);
            PerformanceProfiler.a();
            this.formationEngine.a(fastArrayList, orderableUnitA, fastArrayListA, angleBetweenPoints, i2);
            PerformanceProfiler.a();
            this.formationEngine.a(fastArrayList, orderableUnitA);
            i++;
        }
    }
}
