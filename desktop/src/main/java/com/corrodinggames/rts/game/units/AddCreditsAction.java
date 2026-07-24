package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.actions.NoneAction;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/i.class */
class AddCreditsAction extends NoneAction {
    public AddCreditsAction() {
        super("addCredits");
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        return "Add credits";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return "Add $10000 to this team";
    }

    @Override // com.corrodinggames.rts.game.units.actions.NoneAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public boolean getIconForUnit() {
        return true;
    }
}
