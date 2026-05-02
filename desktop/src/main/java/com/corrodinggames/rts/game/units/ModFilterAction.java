package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionType;
import com.corrodinggames.rts.game.units.actions.NoneAction;
import com.corrodinggames.rts.gameFramework.ui.GameUI;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/j.class */
class ModFilterAction extends NoneAction {
    boolean a;
    boolean b;

    public ModFilterAction(boolean z, boolean z2) {
        super("changeModFilter" + z + "d:" + z2);
        this.a = z;
        this.b = z2;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
        return editorOrBuilderL == null || editorOrBuilderL.G == EditorTab.modded;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getCostForUnit() {
        if (this.b) {
            EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
            if (editorOrBuilderL != null) {
                if (editorOrBuilderL.E != null) {
                    return editorOrBuilderL.E.getDisplayTitle();
                }
                return "All mods";
            }
            return "Mod Filter";
        }
        if (this.a) {
            return "<- Set mod";
        }
        return "Set mod ->";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public String d() {
        if (!this.b) {
            if (this.a) {
                return "<-";
            }
            return "->";
        }
        EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
        if (editorOrBuilderL == null) {
            return "NA";
        }
        if (editorOrBuilderL.E == null) {
            return "All mods";
        }
        return editorOrBuilderL.E.getPaddedTitle();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String isLocked() {
        return "Change filtered mod";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: l */
    public float getBuildSpeed() {
        if (!GameUI.bP) {
            return 0.8f;
        }
        return 0.5f;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m */
    public int getKeyBinding() {
        if (this.b) {
            return 2;
        }
        return 4;
    }

    @Override // com.corrodinggames.rts.game.units.actions.NoneAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType isAlsoSelected() {
        if (this.b) {
            return ActionDisplayType.infoOnly;
        }
        return super.isAlsoSelected();
    }

    @Override // com.corrodinggames.rts.game.units.actions.NoneAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType e() {
        if (this.b) {
            return ActionType.infoOnly;
        }
        return super.e();
    }
}
