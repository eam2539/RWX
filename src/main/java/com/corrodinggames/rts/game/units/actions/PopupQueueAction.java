package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface;
import com.corrodinggames.rts.game.units.buildings.Projectile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/w.class */
public abstract class PopupQueueAction extends AbstractUnitAction {
    public PopupQueueAction(int i) {
        super(i);
    }

    public PopupQueueAction(String str) {
        super(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int isActive(BaseUnit baseUnit, boolean z) {
        if (!(baseUnit instanceof FactoryQueueInterface)) {
            return 99;
        }
        return ((FactoryQueueInterface) baseUnit).a(getActionId(), z);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: p */
    public float getProgress(BaseUnit baseUnit) {
        Projectile projectileDw;
        if (!(baseUnit instanceof FactoryQueueInterface) || (projectileDw = ((FactoryQueueInterface) baseUnit).dw()) == null || !isAvailableForUnit(projectileDw.j)) {
            return -1.0f;
        }
        float f = projectileDw.m;
        if (f < 0.0f) {
            return 0.0f;
        }
        if (f > 1.0f) {
            return 1.0f;
        }
        return f;
    }

    public float K() {
        return 0.01f;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: u */
    public boolean isGuiBlinking() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType e() {
        return ActionType.popupQueue;
    }
}
