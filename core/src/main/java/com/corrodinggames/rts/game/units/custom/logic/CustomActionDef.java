package com.corrodinggames.rts.game.units.custom.logic;

import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionType;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.game.units.custom.hooks.DecalListProcessor;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import io.github.rwx.geometry.PointF;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/d.class */
public class CustomActionDef {

    /* JADX INFO: renamed from: a */
    public int id;

    /* JADX INFO: renamed from: b */
    public String name;

    /* JADX INFO: renamed from: c */
    public String displayName;

    /* JADX INFO: renamed from: d */
    public LocalizedText text;

    /* JADX INFO: renamed from: e */
    public UnitReference.UnitReferenceOrUnitType textAddUnitName;

    /* JADX INFO: renamed from: f */
    public UnitReference.UnitReferenceOrUnitType descriptionAddFromUnit;

    /* JADX INFO: renamed from: g */
    public UnitReference.UnitReferenceOrUnitType descriptionAddUnitStats;

    /* JADX INFO: renamed from: h */
    public LocaleString textPostFix;

    /* JADX INFO: renamed from: i */
    public LocalizedText description;

    /* JADX INFO: renamed from: k */
    public String stringId;

    /* JADX INFO: renamed from: l */
    public UnitTypeReference whenBuildingTemporarilyConvertTo;

    /* JADX INFO: renamed from: m */
    public CustomUnitDataField[] whenBuildingTemporarilyConvertToKeepFields;

    /* JADX INFO: renamed from: n */
    public boolean addToBuildQueue;

    /* JADX INFO: renamed from: o */
    public boolean extraLagHidingInUI;

    /* JADX INFO: renamed from: q */
    public UnitPrice price;

    /* JADX INFO: renamed from: r */
    public UnitPrice streamingCost;

    /* JADX INFO: renamed from: s */
    public AnimationSet tags;

    /* JADX INFO: renamed from: t */
    public LogicBoolean requireConditional;

    /* JADX INFO: renamed from: u */
    public LogicBoolean isActive;

    /* JADX INFO: renamed from: v */
    public LogicBoolean isVisible;

    /* JADX INFO: renamed from: w */
    public boolean isAlsoViewableByAllies;

    /* JADX INFO: renamed from: x */
    public boolean isAlsoViewableByEnemies;

    /* JADX INFO: renamed from: y */
    public boolean hideInBuildMenu;

    /* JADX INFO: renamed from: z */
    public LogicBoolean isLocked;

    /* JADX INFO: renamed from: A */
    public LocalizedText isLockedMessage;

    /* JADX INFO: renamed from: B */
    public LogicBoolean isLockedAlt;

    /* JADX INFO: renamed from: C */
    public LocalizedText isLockedAltMessage;

    /* JADX INFO: renamed from: D */
    public LogicBoolean isLockedAlt2;

    /* JADX INFO: renamed from: E */
    public LocalizedText isLockedAlt2Message;

    /* JADX INFO: renamed from: F */
    public LogicBoolean aiHighPriorityCondition;

    /* JADX INFO: renamed from: G */
    public LogicBoolean aiDisabledCondition;

    /* JADX INFO: renamed from: H */
    public UnitTypeReference convertTo;

    /* JADX INFO: renamed from: I */
    public UnitTypeReference aiConsiderSameAsBuilding;

    /* JADX INFO: renamed from: J */
    public UnitTypeReference guiBuildUnit;

    /* JADX INFO: renamed from: Q */
    public boolean convertToKeepCurrentTags;

    /* JADX INFO: renamed from: R */
    public CustomUnitDataField[] convertToKeepCurrentFields;

    /* JADX INFO: renamed from: V */
    public CustomUnitAnimationReference whenBuildingPlayAnimation;

    /* JADX INFO: renamed from: W */
    public Float whenBuildingRotateTo;

    /* JADX INFO: renamed from: X */
    public boolean whenBuildingRotateToOrBackwards;

    /* JADX INFO: renamed from: Y */
    public boolean whenBuildingRotateToWaitTillRotated;

    /* JADX INFO: renamed from: Z */
    public boolean whenBuildingRotateToAimAtActionTarget;

    /* JADX INFO: renamed from: aa */
    public TurretConfig whenBuildingRotateToRotateTurretX;

    /* JADX INFO: renamed from: ab */
    public CustomUnitActionHandler whenBuildingTriggerAction;

    /* JADX INFO: renamed from: ad */
    public Float addEnergy;

    /* JADX INFO: renamed from: ae */
    public UnitPrice addResources;

    /* JADX INFO: renamed from: af */
    public UnitPrice addResourcesScaledByAIHandicaps;

    /* JADX INFO: renamed from: ag */
    public Integer fireTurretAtGroundIndex;

    /* JADX INFO: renamed from: ah */
    public PointF fireTurretAtGroundOffset;

    /* JADX INFO: renamed from: ai */
    public LogicBoolean fireTurretAtGroundTarget;

    /* JADX INFO: renamed from: aj */
    public CustomProjectileTemplate fireTurretAtGroundProjectile;

    /* JADX INFO: renamed from: ak */
    public int fireTurretAtGroundCount;

    /* JADX INFO: renamed from: al */
    public UnitMovementType fireTurretAtGroundTerrainFilter;

    /* JADX INFO: renamed from: am */
    public DecalListProcessor fireTurretAtGroundGuideDecals;

    /* JADX INFO: renamed from: an */
    public LogicBoolean alsoTriggerOrQueueActionTarget;

    /* JADX INFO: renamed from: ao */
    public LogicBoolean alsoTriggerOrQueueActionCondition;

    /* JADX INFO: renamed from: ap */
    public CustomUnitActionHandler alsoTriggerAction;

    /* JADX INFO: renamed from: aq */
    public CustomUnitActionHandler alsoQueueAction;

    /* JADX INFO: renamed from: ar */
    public LogicBoolean alsoTriggerActionRepeat;

    /* JADX INFO: renamed from: as */
    public CustomUnitSpawnList spawnEffects;

    public CustomUnitSpawnList spawnListAtActionTarget;

    /* JADX INFO: renamed from: at */
    public CustomUnitSpawnList spawnEffectsOnQueue;

    /* JADX INFO: renamed from: au */
    public SoundList playSoundAtUnit;

    /* JADX INFO: renamed from: av */
    public SoundList playSoundGlobally;

    /* JADX INFO: renamed from: aw */
    public SoundList playSoundToPlayer;

    /* JADX INFO: renamed from: ax */
    public SoundList playSoundToPlayerOnQueue;

    /* JADX INFO: renamed from: ay */
    public Texture iconImage;

    /* JADX INFO: renamed from: az */
    public Texture iconExtraImage;

    /* JADX INFO: renamed from: aA */
    public int iconExtraColor;

    /* JADX INFO: renamed from: aB */
    public LogicBoolean iconExtraIsVisible;

    /* JADX INFO: renamed from: aC */
    public UnitReference.UnitReferenceOrUnitType unitShownInUI;

    /* JADX INFO: renamed from: aD */
    public boolean unitShownInUIWithHpBar;

    /* JADX INFO: renamed from: aE */
    public boolean unitShownInUIWithProgressBar;

    /* JADX INFO: renamed from: aF */
    public LogicBoolean isGuiBlinking;

    /* JADX INFO: renamed from: aH */
    public LogicBoolean resetCustomTimer;

    /* JADX INFO: renamed from: aI */
    public boolean displayRemainingStockpile;

    /* JADX INFO: renamed from: aJ */
    public int techLevel;

    /* JADX INFO: renamed from: aK */
    public boolean forceNano;

    /* JADX INFO: renamed from: aL */
    public String buildType;

    /* JADX INFO: renamed from: j */
    public ActionType queueType = ActionType.popupQueue;

    /* JADX INFO: renamed from: p */
    public float pos = -999.0f;

    /* JADX INFO: renamed from: K */
    public boolean highPriorityQueue = false;

    /* JADX INFO: renamed from: L */
    public boolean onlyOneUnitAtATime = false;

    /* JADX INFO: renamed from: M */
    public boolean canPlayerCancel = true;

    /* JADX INFO: renamed from: N */
    public boolean allowMultipleInQueue = true;

    /* JADX INFO: renamed from: O */
    public boolean alwaysSinglePress = false;

    /* JADX INFO: renamed from: P */
    public boolean hideQueueInterface = false;

    /* JADX INFO: renamed from: S */
    public float buildSpeed = 0.01f;

    /* JADX INFO: renamed from: T */
    public boolean buildSpeedIgnoreFactorySpeedModifiers = false;

    /* JADX INFO: renamed from: U */
    public boolean whenBuildingCannotMove = false;
    /* JADX INFO: renamed from: ac */
    public FastArrayList logicActions = new FastArrayList();
    /* JADX INFO: renamed from: aG */
    public ActionDisplayType displayType = ActionDisplayType.queueUnit;
    /* JADX INFO: renamed from: aM */
    public BuildType actionType = BuildType.build;
    /* JADX INFO: renamed from: aN */
    public com.corrodinggames.rts.game.units.custom.logic.ActionType aiUse = com.corrodinggames.rts.game.units.custom.logic.ActionType.auto;

    public String a() {
        if (this.text != null) {
            return this.text.b();
        }
        return this.displayName;
    }
}
