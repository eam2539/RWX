package com.corrodinggames.rts.game.ai;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/h.class */
public abstract class AIUnitGroupBase extends AIStrategyNode {
    ArrayList<OrderableUnit> F;
    ArrayList G;

    public abstract void c(float f);

    public int l() {
        return this.F.size();
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public AIUnitGroupBase(AIController aIController) {
        super(aIController);
        this.F = new ArrayList();
        this.G = new ArrayList();
    }

    public boolean m() {
        for (AIStrategyNode aIStrategyNode : this.aiController.strategyNodes) {
            if ((aIStrategyNode instanceof TransporterGroup) && ((TransporterGroup) aIStrategyNode).m == this) {
                return true;
            }
        }
        return false;
    }

    public void n() {
        Iterator it = this.F.iterator();
        while (it.hasNext()) {
            OrderableUnit orderableUnit = (OrderableUnit) it.next();
            if (orderableUnit == null || orderableUnit.isDestroyed) {
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

    public void o() {
        Iterator it = this.G.iterator();
        while (it.hasNext()) {
            OrderableUnit orderableUnit = (OrderableUnit) it.next();
            if (orderableUnit == null || orderableUnit.isDestroyed || orderableUnit.unitTransportTarget != null || orderableUnit.parentEntity != null) {
                it.remove();
            }
        }
    }

    @Override // com.corrodinggames.rts.game.ai.AIStrategyNode
    /* JADX INFO: renamed from: p */
    public void destroy() {
        q();
        this.G.clear();
        super.destroy();
    }

    protected void a(OrderableUnit orderableUnit) {
        if (orderableUnit.aB != null) {
            orderableUnit.aB.b(orderableUnit);
        }
        if (orderableUnit.team != null && orderableUnit.team != this.aiController) {
            GameEngine.logWarningAndStack("unit.team:" + orderableUnit.team.teamId + ", ai:" + this.aiController.teamId);
        }
        this.F.add(orderableUnit);
        orderableUnit.aB = this;
    }

    public void b(OrderableUnit orderableUnit) {
        this.F.remove(orderableUnit);
        this.G.remove(orderableUnit);
        if (orderableUnit.aB == this) {
            orderableUnit.aB = null;
        }
    }

    public void q() {
        for (OrderableUnit orderableUnit : this.F) {
            if (orderableUnit != null && orderableUnit.aB == this) {
                orderableUnit.aB = null;
            }
        }
        this.F.clear();
    }

    public void b(float f) {
    }
}
