package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionType;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.q */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/q.class */
class SetTerrainTypeAction extends AbstractUnitAction {
    EditorTerrainType a;

    public SetTerrainTypeAction(EditorTerrainType editorTerrainType) {
        super("SetTerrainType" + editorTerrainType.ordinal());
        this.a = editorTerrainType;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
        return editorOrBuilderL == null || editorOrBuilderL.G == EditorTab.terrain;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String isLocked() {
        return "Set terrain type to: " + this.a.name();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getCostForUnit() {
        return "Set " + this.a.name();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h_ */
    public boolean shouldShowDisplayText() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int isConfirmed() {
        return 0;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int isActive(BaseUnit baseUnit, boolean z) {
        return -1;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum getUnitType() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType e() {
        return ActionType.targetGround;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType isAlsoSelected() {
        return ActionDisplayType.action;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public boolean getIconForUnit() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: o */
    public boolean isCancel() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean a(float f, float f2) {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: p */
    public boolean isInstant() {
        return true;
    }
}
