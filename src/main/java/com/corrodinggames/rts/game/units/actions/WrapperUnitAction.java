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
    public String getCostForUnit() {
        return this.a.getCostForUnit();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: d */
    public String isVisible(BaseUnit baseUnit) {
        return this.a.isVisible(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String isLocked() {
        return this.a.isLocked();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: e */
    public String getProducedUnitType(BaseUnit baseUnit) {
        return this.a.getProducedUnitType(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int isConfirmed() {
        return this.a.isConfirmed();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int isActive(BaseUnit baseUnit, boolean z) {
        return this.a.isActive(this.b, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n_ */
    public boolean isQueuable() {
        return this.a.isQueuable();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
        return this.a.drawTooltip(this.b, z);
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
    public boolean getIconForUnit() {
        return this.a.getIconForUnit();
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
    public ActionType e() {
        return this.a.e();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType isAlsoSelected() {
        return this.a.isAlsoSelected();
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
    public void isWaiting(BaseUnit baseUnit, TextRenderQueue textRenderQueue, Paint paint, Paint paint2) {
        K();
        this.a.isWaiting(this.b, textRenderQueue, paint, paint2);
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
    public Texture getIconColor() {
        return this.a.getIconColor();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public Texture isShowingNotEnoughEnergy(BaseUnit baseUnit) {
        return this.a.isShowingNotEnoughEnergy(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: v */
    public Rect getIconRect() {
        return this.a.getIconRect();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public BaseUnit isShowingNotEnoughResources(BaseUnit baseUnit) {
        return this.a.isShowingNotEnoughResources(this.b);
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
    public void isPickAction(BaseUnit baseUnit, BaseUnit baseUnit2) {
        super.isPickAction(baseUnit, baseUnit2);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean appendTooltip(BaseUnit baseUnit, PlayerTeam playerTeam) {
        return this.a.appendTooltip(this.b, playerTeam);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: A */
    public boolean getDescription() {
        return this.a.getDescription();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean isTargetingGround(BaseUnit baseUnit) {
        return this.a.isTargetingGround((BaseUnit) this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: B */
    public UnitPrice getDisplayText() {
        return this.a.getDisplayText();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: j */
    public String getIcon(BaseUnit baseUnit) {
        return this.a.getIcon(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: d */
    public boolean getDisplayTextWithQueueCount(BaseUnit baseUnit, boolean z) {
        return this.a.getDisplayTextWithQueueCount(this.b, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: k */
    public boolean isSingleUse(BaseUnit baseUnit) {
        return this.a.isSingleUse(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: l */
    public boolean isTargetingAction(BaseUnit baseUnit) {
        return this.a.isTargetingAction(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: C */
    public boolean getCost() {
        return this.a.getCost();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: D */
    public boolean getDisplayTextForUnit() {
        return this.a.getDisplayTextForUnit();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: E */
    public UnitType getActionType() {
        return this.a.getActionType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: F */
    public boolean getDisplayType() {
        return this.a.getDisplayType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m */
    public boolean getEnergyCost(BaseUnit baseUnit) {
        return this.a.getEnergyCost(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n */
    public boolean isSecondary(BaseUnit baseUnit) {
        return this.a.isSecondary(this.b);
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
    public boolean isAttack() {
        return this.a.isAttack();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: I */
    public boolean getTargetUnit() {
        return this.a.getTargetUnit();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: p */
    public float getProgress(BaseUnit baseUnit) {
        return this.a.getProgress(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: q */
    public ArrayList isPurchase(BaseUnit baseUnit) {
        return this.a.isPurchase(this.b);
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
    public int getNotAvailableReason() {
        return this.a.getNotAvailableReason();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: s */
    public boolean shouldShowProgress(BaseUnit baseUnit) {
        return this.a.shouldShowProgress(this.b);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: t */
    public boolean shouldShowCount(BaseUnit baseUnit) {
        return this.a.shouldShowCount(this.b);
    }

    public boolean a(WrapperUnitAction wrapperUnitAction) {
        return this.a == wrapperUnitAction.a && this.b == wrapperUnitAction.b && getActionId() == wrapperUnitAction.getActionId() && this.c == wrapperUnitAction.c;
    }
}
