package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionType;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.q */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/q.class */
class SetTerrainTypeAction extends AbstractUnitAction {

    /* JADX INFO: renamed from: a */
    EditorTerrainType terrainType;

    public SetTerrainTypeAction(EditorTerrainType editorTerrainType) {
        super("SetTerrainType" + editorTerrainType.ordinal());
        this.terrainType = editorTerrainType;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
        return editorOrBuilderL == null || editorOrBuilderL.G == EditorTab.terrain;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return "Set terrain type to: " + this.terrainType.name();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        return "Set " + this.terrainType.name();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h_ */
    public boolean shouldShowDisplayText() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int getCostAmount() {
        return 0;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int getActiveCount(BaseUnit baseUnit, boolean z) {
        return -1;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum getUnitType() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType getActionType() {
        return ActionType.targetGround;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType getActionDisplayType() {
        return ActionDisplayType.action;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean canAfford(BaseUnit baseUnit, boolean z) {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public boolean alwaysShowsTooltip() {
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
