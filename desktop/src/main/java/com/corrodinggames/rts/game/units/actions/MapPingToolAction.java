package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.KeyBinding;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.r */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/r.class */
public class MapPingToolAction extends CustomUnitToolAction {
    public MapPingToolAction() {
        super("c__cut_ping");
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        return "Map Ping";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return "Send a map ping to your allies";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public boolean onClicked(BaseUnit baseUnit, boolean z) {
        GameEngine.getInstance().gameUI.activatePingMapMode();
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: M */
    public KeyBinding isPrimary() {
        return GameEngine.getInstance().inputController.v;
    }
}
