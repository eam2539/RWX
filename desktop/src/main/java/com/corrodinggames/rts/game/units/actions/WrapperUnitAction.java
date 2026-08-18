package com.corrodinggames.rts.game.units.actions;

import android.graphics.Paint;
import android.graphics.Rect;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.ui.TextRenderQueue;
import com.corrodinggames.rts.gameFramework.utility.UnitList;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/g.class */
public class WrapperUnitAction extends AbstractUnitAction {
    public AbstractUnitAction a;
    public OrderableUnit b;
    public ActionFilter c;
    static UnitList d;
    static final UnitList e = new UnitList();

    private void K() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (d != null) {
            throw new RuntimeException("savedSelectedUnitsCache!=null");
        }
        d = gameEngine.gameUI.selectedUnitsList;
        e.clear();
        e.add(this.b);
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
        return this.a.getDisplayName();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: d */
    public String getDisplayName(BaseUnit baseUnit) {
        return this.a.getDisplayName(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return this.a.getDescription();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: e */
    public String getDescriptionForUnit(BaseUnit baseUnit) {
        return this.a.getDescriptionForUnit(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int getCostAmount() {
        return this.a.getCostAmount();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int getActiveCount(BaseUnit baseUnit, boolean z) {
        return this.a.getActiveCount(this.b, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n_ */
    public boolean isQueuable() {
        return this.a.isQueuable();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean canAfford(BaseUnit baseUnit, boolean z) {
        return this.a.canAfford(this.b, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: t */
    public int getQueueSize() {
        return this.a.getQueueSize();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public void onConfirmed(BaseUnit baseUnit) {
        this.a.onConfirmed(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isNotAvailable(BaseUnit baseUnit) {
        return this.a.isNotAvailable(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: u */
    public boolean isGuiBlinking() {
        return this.a.isGuiBlinking();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public boolean alwaysShowsTooltip() {
        return this.a.alwaysShowsTooltip();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public UnitType getUnitType() {
        return this.a.getUnitType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return this.a.isHighPriority();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType getActionType() {
        return this.a.getActionType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType getActionDisplayType() {
        return this.a.getActionDisplayType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public String d() {
        K();
        String strD = this.a.d();
        L();
        return strD;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h_ */
    public boolean shouldShowDisplayText() {
        return this.a.shouldShowDisplayText();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public void renderDisplayText(BaseUnit baseUnit, TextRenderQueue textRenderQueue, Paint paint, Paint paint2) {
        K();
        this.a.renderDisplayText(this.b, textRenderQueue, paint, paint2);
        L();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public void onPurchase(BaseUnit baseUnit, TextRenderQueue textRenderQueue) {
        K();
        this.a.onPurchase(this.b, textRenderQueue);
        L();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: j */
    public Texture getIconTexture() {
        return this.a.getIconTexture();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public Texture getExtraIconTexture(BaseUnit baseUnit) {
        return this.a.getExtraIconTexture(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: v */
    public Rect getIconRect() {
        return this.a.getIconRect();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public BaseUnit getUnitShownInUI(BaseUnit baseUnit) {
        return this.a.getUnitShownInUI(this.b);
    }

    public int hashCode() {
        return this.a.hashCode();
    }

    public String toString() {
        return this.a.toString();
    }

    public WrapperUnitAction(AbstractUnitAction abstractUnitAction, OrderableUnit orderableUnit, ActionId actionId) {
        super(actionId);
        this.c = ActionFilter.emptyActionFilter;
        this.a = abstractUnitAction;
        this.b = orderableUnit;
        this.sortOrder = this.a.sortOrder;
    }

    public AbstractUnitAction p_() {
        return this.a;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: x */
    public boolean isRightClickAction() {
        return this.a.isRightClickAction();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: s */
    public boolean isWaitingForTarget() {
        return this.a.isWaitingForTarget();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: y */
    public UnitType getAttachedUnitType() {
        return this.a.getAttachedUnitType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: z */
    public ActionId getQueueId() {
        return this.a.getActionId();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public void onTargetSelected(BaseUnit baseUnit, BaseUnit baseUnit2) {
        super.onTargetSelected(baseUnit, baseUnit2);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean appendTooltip(BaseUnit baseUnit, PlayerTeam playerTeam) {
        return this.a.appendTooltip(this.b, playerTeam);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: A */
    public boolean usesActionTarget() {
        return this.a.usesActionTarget();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean isTargetingGround(BaseUnit baseUnit) {
        return this.a.isTargetingGround((BaseUnit) this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: B */
    public UnitPrice getPrice() {
        return this.a.getPrice();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: j */
    public String getIcon(BaseUnit baseUnit) {
        return this.a.getIcon(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: d */
    public boolean canPlayerCancel(BaseUnit baseUnit, boolean z) {
        return this.a.canPlayerCancel(this.b, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: k */
    public boolean isAlwaysSinglePress(BaseUnit baseUnit) {
        return this.a.isAlwaysSinglePress(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: l */
    public boolean shouldHideQueueInterface(BaseUnit baseUnit) {
        return this.a.shouldHideQueueInterface(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: C */
    public boolean selectsUnitOnClick() {
        return this.a.selectsUnitOnClick();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: D */
    public boolean shouldShowUnitPreview() {
        return this.a.shouldShowUnitPreview();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: E */
    public UnitType getAiConsiderSameAsBuildingUnitType() {
        return this.a.getAiConsiderSameAsBuildingUnitType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: F */
    public boolean isDisplayOnly() {
        return this.a.isDisplayOnly();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m */
    public boolean isAiDisabled(BaseUnit baseUnit) {
        return this.a.isAiDisabled(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n */
    public boolean isAiHighPriority(BaseUnit baseUnit) {
        return this.a.isAiHighPriority(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public boolean onClicked(BaseUnit baseUnit, boolean z) {
        return this.a.onClicked(this.b, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: o */
    public boolean isAvailableAndVisible(BaseUnit baseUnit) {
        return this.a.isAvailableAndVisible(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: G */
    public boolean isBuildOption() {
        return this.a.isBuildOption();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public void c(BaseUnit baseUnit) {
        this.a.c(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: l */
    public float getBuildSpeed() {
        return this.a.getBuildSpeed();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m */
    public int getKeyBinding() {
        return this.a.getKeyBinding();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: H */
    public boolean isHighPriorityQueue() {
        return this.a.isHighPriorityQueue();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: I */
    public boolean isOnlyOneUnitAtATime() {
        return this.a.isOnlyOneUnitAtATime();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: p */
    public float getProgress(BaseUnit baseUnit) {
        return this.a.getProgress(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: q */
    public ArrayList getCandidateActionList(BaseUnit baseUnit) {
        return this.a.getCandidateActionList(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: r */
    public boolean isAvailable(BaseUnit baseUnit) {
        if (!this.c.isAvailable(this, baseUnit)) {
            return false;
        }
        return this.a.isAvailable(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        if (!this.c.isAvailable(this, baseUnit)) {
            return false;
        }
        return this.a.b(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: J */
    public int getExtraIconColor() {
        return this.a.getExtraIconColor();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: s */
    public boolean shouldShowUnitHealthBar(BaseUnit baseUnit) {
        return this.a.shouldShowUnitHealthBar(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: t */
    public boolean shouldShowUnitProgressBar(BaseUnit baseUnit) {
        return this.a.shouldShowUnitProgressBar(this.b);
    }

    public boolean a(WrapperUnitAction wrapperUnitAction) {
        return this.a == wrapperUnitAction.a && this.b == wrapperUnitAction.b && getActionId() == wrapperUnitAction.getActionId() && this.c == wrapperUnitAction.c;
    }
}
