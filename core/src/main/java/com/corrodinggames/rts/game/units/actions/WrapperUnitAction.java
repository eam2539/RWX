package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.ui.TextRenderQueue;
import com.corrodinggames.rts.gameFramework.utility.UnitList;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolPaint;

import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/g.class */
public class WrapperUnitAction extends AbstractUnitAction {

    /* JADX INFO: renamed from: a */
    public AbstractUnitAction wrappedAction;

    /* JADX INFO: renamed from: b */
    public OrderableUnit unit;

    /* JADX INFO: renamed from: c */
    public ActionFilter actionFilter;
    static UnitList d;
    static final UnitList e = new UnitList();

    private void K() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (d != null) {
            throw new RuntimeException("savedSelectedUnitsCache!=null");
        }
        d = gameEngine.gameUI.selectedUnitsList;
        e.clear();
        e.add(this.unit);
        gameEngine.gameUI.selectedUnitsList = e;
    }

    private void L() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (d == null) {
            throw new RuntimeException("savedSelectedUnitsCache==null");
        }
        gameEngine.gameUI.selectedUnitsList = d;
        d = null;
        e.clear();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m_ */
    public float getSortOrder() {
        return super.getSortOrder();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction, java.lang.Comparable
    /* JADX INFO: renamed from: a */
    public int compareTo(AbstractUnitAction abstractUnitAction) {
        return super.compareTo(abstractUnitAction);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        return this.wrappedAction.getDisplayName();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: d */
    public String getDisplayName(BaseUnit baseUnit) {
        return this.wrappedAction.getDisplayName(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return this.wrappedAction.getDescription();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: e */
    public String getDescriptionForUnit(BaseUnit baseUnit) {
        return this.wrappedAction.getDescriptionForUnit(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int getCostAmount() {
        return this.wrappedAction.getCostAmount();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int getActiveCount(BaseUnit baseUnit, boolean z) {
        return this.wrappedAction.getActiveCount(this.unit, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n_ */
    public boolean isQueuable() {
        return this.wrappedAction.isQueuable();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean canAfford(BaseUnit baseUnit, boolean z) {
        return this.wrappedAction.canAfford(this.unit, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: t */
    public int getQueueSize() {
        return this.wrappedAction.getQueueSize();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public void onConfirmed(BaseUnit baseUnit) {
        this.wrappedAction.onConfirmed(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isNotAvailable(BaseUnit baseUnit) {
        return this.wrappedAction.isNotAvailable(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: u */
    public boolean isGuiBlinking() {
        return this.wrappedAction.isGuiBlinking();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public boolean alwaysShowsTooltip() {
        return this.wrappedAction.alwaysShowsTooltip();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public UnitType getUnitType() {
        return this.wrappedAction.getUnitType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return this.wrappedAction.isHighPriority();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType getActionType() {
        return this.wrappedAction.getActionType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType getActionDisplayType() {
        return this.wrappedAction.getActionDisplayType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public String d() {
        K();
        String strD = this.wrappedAction.d();
        L();
        return strD;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h_ */
    public boolean shouldShowDisplayText() {
        return this.wrappedAction.shouldShowDisplayText();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public void renderDisplayText(BaseUnit baseUnit, TextRenderQueue textRenderQueue, KoolPaint paint, KoolPaint paint2) {
        K();
        this.wrappedAction.renderDisplayText(this.unit, textRenderQueue, paint, paint2);
        L();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public void onPurchase(BaseUnit baseUnit, TextRenderQueue textRenderQueue) {
        K();
        this.wrappedAction.onPurchase(this.unit, textRenderQueue);
        L();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: j */
    public Texture getIconTexture() {
        return this.wrappedAction.getIconTexture();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public Texture getExtraIconTexture(BaseUnit baseUnit) {
        return this.wrappedAction.getExtraIconTexture(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: v */
    public Rect getIconRect() {
        return this.wrappedAction.getIconRect();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public BaseUnit getUnitShownInUI(BaseUnit baseUnit) {
        return this.wrappedAction.getUnitShownInUI(this.unit);
    }

    public int hashCode() {
        return this.wrappedAction.hashCode();
    }

    public String toString() {
        return this.wrappedAction.toString();
    }

    public WrapperUnitAction(AbstractUnitAction abstractUnitAction, OrderableUnit orderableUnit, ActionId actionId) {
        super(actionId);
        this.actionFilter = ActionFilter.emptyActionFilter;
        this.wrappedAction = abstractUnitAction;
        this.unit = orderableUnit;
        this.sortOrder = this.wrappedAction.sortOrder;
    }

    public AbstractUnitAction p_() {
        return this.wrappedAction;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: x */
    public boolean isRightClickAction() {
        return this.wrappedAction.isRightClickAction();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: s */
    public boolean isWaitingForTarget() {
        return this.wrappedAction.isWaitingForTarget();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: y */
    public UnitType getAttachedUnitType() {
        return this.wrappedAction.getAttachedUnitType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: z */
    public ActionId getQueueId() {
        return this.wrappedAction.getActionId();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public void onTargetSelected(BaseUnit baseUnit, BaseUnit baseUnit2) {
        super.onTargetSelected(baseUnit, baseUnit2);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean appendTooltip(BaseUnit baseUnit, PlayerTeam playerTeam) {
        return this.wrappedAction.appendTooltip(this.unit, playerTeam);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: A */
    public boolean usesActionTarget() {
        return this.wrappedAction.usesActionTarget();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean isTargetingGround(BaseUnit baseUnit) {
        return this.wrappedAction.isTargetingGround((BaseUnit) this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: B */
    public UnitPrice getPrice() {
        return this.wrappedAction.getPrice();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: j */
    public String getIcon(BaseUnit baseUnit) {
        return this.wrappedAction.getIcon(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: d */
    public boolean canPlayerCancel(BaseUnit baseUnit, boolean z) {
        return this.wrappedAction.canPlayerCancel(this.unit, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: k */
    public boolean isAlwaysSinglePress(BaseUnit baseUnit) {
        return this.wrappedAction.isAlwaysSinglePress(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: l */
    public boolean shouldHideQueueInterface(BaseUnit baseUnit) {
        return this.wrappedAction.shouldHideQueueInterface(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: C */
    public boolean selectsUnitOnClick() {
        return this.wrappedAction.selectsUnitOnClick();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: D */
    public boolean shouldShowUnitPreview() {
        return this.wrappedAction.shouldShowUnitPreview();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: E */
    public UnitType getAiConsiderSameAsBuildingUnitType() {
        return this.wrappedAction.getAiConsiderSameAsBuildingUnitType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: F */
    public boolean isDisplayOnly() {
        return this.wrappedAction.isDisplayOnly();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m */
    public boolean isAiDisabled(BaseUnit baseUnit) {
        return this.wrappedAction.isAiDisabled(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n */
    public boolean isAiHighPriority(BaseUnit baseUnit) {
        return this.wrappedAction.isAiHighPriority(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public boolean onClicked(BaseUnit baseUnit, boolean z) {
        return this.wrappedAction.onClicked(this.unit, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: o */
    public boolean isAvailableAndVisible(BaseUnit baseUnit) {
        return this.wrappedAction.isAvailableAndVisible(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: G */
    public boolean isBuildOption() {
        return this.wrappedAction.isBuildOption();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public void c(BaseUnit baseUnit) {
        this.wrappedAction.c(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: l */
    public float getBuildSpeed() {
        return this.wrappedAction.getBuildSpeed();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m */
    public int getKeyBinding() {
        return this.wrappedAction.getKeyBinding();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: H */
    public boolean isHighPriorityQueue() {
        return this.wrappedAction.isHighPriorityQueue();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: I */
    public boolean isOnlyOneUnitAtATime() {
        return this.wrappedAction.isOnlyOneUnitAtATime();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: p */
    public float getProgress(BaseUnit baseUnit) {
        return this.wrappedAction.getProgress(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: q */
    public ArrayList getCandidateActionList(BaseUnit baseUnit) {
        return this.wrappedAction.getCandidateActionList(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: r */
    public boolean isAvailable(BaseUnit baseUnit) {
        if (!this.actionFilter.isAvailable(this, baseUnit)) {
            return false;
        }
        return this.wrappedAction.isAvailable(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        if (!this.actionFilter.isAvailable(this, baseUnit)) {
            return false;
        }
        return this.wrappedAction.b(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: J */
    public int getExtraIconColor() {
        return this.wrappedAction.getExtraIconColor();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: s */
    public boolean shouldShowUnitHealthBar(BaseUnit baseUnit) {
        return this.wrappedAction.shouldShowUnitHealthBar(this.unit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: t */
    public boolean shouldShowUnitProgressBar(BaseUnit baseUnit) {
        return this.wrappedAction.shouldShowUnitProgressBar(this.unit);
    }

    public boolean a(WrapperUnitAction wrapperUnitAction) {
        return this.wrappedAction == wrapperUnitAction.wrappedAction && this.unit == wrapperUnitAction.unit && getActionId() == wrapperUnitAction.getActionId() && this.actionFilter == wrapperUnitAction.actionFilter;
    }
}
