package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionType;
import com.corrodinggames.rts.game.units.actions.NoneAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.ui.GameUI;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/m.class */
class ChangeEditorTabAction extends NoneAction {
    boolean a;
    boolean b;

    public ChangeEditorTabAction(boolean z, boolean z2) {
        super("changeUnitTab" + z + "d:" + z2);
        this.a = z;
        this.b = z2;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        return d();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public String d() {
        EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
        if (editorOrBuilderL == null) {
            return "<NULL>";
        }
        if (this.b) {
            return editorOrBuilderL.G.a();
        }
        String str = VariableScope.nullOrMissingString;
        if (this.a) {
            str = str + "<- ";
        }
        if (!this.a) {
            str = str + " ->";
        }
        return str;
    }

    public void n() {
        EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
        if (editorOrBuilderL == null) {
            GameEngine.logColored("Editor not active");
        } else {
            if (this.b) {
                return;
            }
            editorOrBuilderL.G = editorOrBuilderL.G.a(this.a);
        }
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return "Change unit tab in editor";
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
    public ActionDisplayType getActionDisplayType() {
        if (this.b) {
            return ActionDisplayType.infoOnly;
        }
        return super.getActionDisplayType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.NoneAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType getActionType() {
        if (this.b) {
            return ActionType.infoOnly;
        }
        return super.getActionType();
    }
}
