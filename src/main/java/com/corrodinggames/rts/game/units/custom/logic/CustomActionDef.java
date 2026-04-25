package com.corrodinggames.rts.game.units.custom.logic;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionType;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.CustomProjectileTemplate;
import com.corrodinggames.rts.game.units.custom.CustomUnitActionHandler;
import com.corrodinggames.rts.game.units.custom.CustomUnitAnimationReference;
import com.corrodinggames.rts.game.units.custom.CustomUnitDataField;
import com.corrodinggames.rts.game.units.custom.CustomUnitSpawnList;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.LocalizedText;
import com.corrodinggames.rts.game.units.custom.SoundList;
import com.corrodinggames.rts.game.units.custom.TurretConfig;
import com.corrodinggames.rts.game.units.custom.UnitTypeReference;
import com.corrodinggames.rts.game.units.custom.hooks.DecalListProcessor;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

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
    public LocalizedText description;

    /* JADX INFO: renamed from: e */
    public UnitReference.UnitReferenceOrUnitType targetUnitType;

    /* JADX INFO: renamed from: f */
    public UnitReference.UnitReferenceOrUnitType sourceUnitType;

    /* JADX INFO: renamed from: g */
    public UnitReference.UnitReferenceOrUnitType relatedUnitType;

    /* JADX INFO: renamed from: h */
    public LocaleString message;

    /* JADX INFO: renamed from: i */
    public LocalizedText requiredUnitType;

    /* JADX INFO: renamed from: k */
    public String stringId;

    /* JADX INFO: renamed from: l */
    public UnitTypeReference spawnUnitType;

    /* JADX INFO: renamed from: m */
    public CustomUnitDataField[] setUnitData;

    /* JADX INFO: renamed from: n */
    public boolean addToBuildQueue;

    /* JADX INFO: renamed from: o */
    public boolean isBuildAction;

    /* JADX INFO: renamed from: q */
    public UnitPrice buildCost;

    /* JADX INFO: renamed from: r */
    public UnitPrice resourceCost;

    /* JADX INFO: renamed from: s */
    public AnimationSet animationSet;

    /* JADX INFO: renamed from: t */
    public LogicBoolean displayCondition;

    /* JADX INFO: renamed from: u */
    public LogicBoolean availableCondition;

    /* JADX INFO: renamed from: v */
    public LogicBoolean enabledCondition;

    /* JADX INFO: renamed from: w */
    public boolean showInBuildMenu;

    /* JADX INFO: renamed from: x */
    public boolean disableInBuildMenu;

    /* JADX INFO: renamed from: y */
    public boolean hideInBuildMenu;

    /* JADX INFO: renamed from: z */
    public LogicBoolean highlightCondition;

    /* JADX INFO: renamed from: A */
    public LocalizedText highlightText;

    /* JADX INFO: renamed from: B */
    public LogicBoolean highlightColorCondition;

    /* JADX INFO: renamed from: C */
    public LocalizedText highlightColor;

    /* JADX INFO: renamed from: D */
    public LogicBoolean iconCondition;

    /* JADX INFO: renamed from: E */
    public LocalizedText icon;

    /* JADX INFO: renamed from: F */
    public LogicBoolean iconColorCondition;

    /* JADX INFO: renamed from: G */
    public LogicBoolean iconColor;

    /* JADX INFO: renamed from: H */
    public UnitTypeReference iconUnitType;

    /* JADX INFO: renamed from: I */
    public UnitTypeReference iconUnitType2;

    /* JADX INFO: renamed from: J */
    public UnitTypeReference iconUnitType3;

    /* JADX INFO: renamed from: Q */
    public boolean isDefaultBuildCommand5;

    /* JADX INFO: renamed from: R */
    public CustomUnitDataField[] setUnitData2;

    /* JADX INFO: renamed from: V */
    public CustomUnitAnimationReference animationReference;

    /* JADX INFO: renamed from: W */
    public Float animationSpeed;

    /* JADX INFO: renamed from: X */
    public boolean isAnimation;

    /* JADX INFO: renamed from: Y */
    public boolean isAnimation2;

    /* JADX INFO: renamed from: Z */
    public boolean isAnimation3;

    /* JADX INFO: renamed from: aa */
    public TurretConfig animation;

    /* JADX INFO: renamed from: ab */
    public CustomUnitActionHandler actionHandler;

    /* JADX INFO: renamed from: ad */
    public Float delay;

    /* JADX INFO: renamed from: ae */
    public UnitPrice energyCost;

    /* JADX INFO: renamed from: af */
    public UnitPrice energyCost2;

    /* JADX INFO: renamed from: ag */
    public Integer energyCost3;

    /* JADX INFO: renamed from: ah */
    public PointF offset;

    /* JADX INFO: renamed from: ai */
    public LogicBoolean condition;

    /* JADX INFO: renamed from: aj */
    public CustomProjectileTemplate condition2;

    /* JADX INFO: renamed from: ak */
    public int condition3;

    /* JADX INFO: renamed from: al */
    public UnitMovementType condition4;

    /* JADX INFO: renamed from: am */
    public DecalListProcessor condition5;

    /* JADX INFO: renamed from: an */
    public LogicBoolean condition6;

    /* JADX INFO: renamed from: ao */
    public LogicBoolean condition7;

    /* JADX INFO: renamed from: ap */
    public CustomUnitActionHandler actionHandler2;

    /* JADX INFO: renamed from: aq */
    public CustomUnitActionHandler actionHandler3;

    /* JADX INFO: renamed from: ar */
    public LogicBoolean condition8;

    /* JADX INFO: renamed from: as */
    public CustomUnitSpawnList spawnList;

    /* JADX INFO: renamed from: at */
    public CustomUnitSpawnList spawnList2;

    /* JADX INFO: renamed from: au */
    public SoundList spawnList3;

    /* JADX INFO: renamed from: av */
    public SoundList spawnList4;

    /* JADX INFO: renamed from: aw */
    public SoundList spawnList5;

    /* JADX INFO: renamed from: ax */
    public SoundList spawnList6;

    /* JADX INFO: renamed from: ay */
    public Texture texture;

    /* JADX INFO: renamed from: az */
    public Texture texture2;

    /* JADX INFO: renamed from: aA */
    public int texture3;

    /* JADX INFO: renamed from: aB */
    public LogicBoolean condition9;

    /* JADX INFO: renamed from: aC */
    public UnitReference.UnitReferenceOrUnitType unitType;

    /* JADX INFO: renamed from: aD */
    public boolean isUnitType;

    /* JADX INFO: renamed from: aE */
    public boolean isUnitType2;

    /* JADX INFO: renamed from: aF */
    public LogicBoolean condition10;

    /* JADX INFO: renamed from: aH */
    public LogicBoolean condition11;

    /* JADX INFO: renamed from: aI */
    public boolean isCondition;

    /* JADX INFO: renamed from: aJ */
    public int condition12;

    /* JADX INFO: renamed from: aK */
    public boolean isCondition2;

    /* JADX INFO: renamed from: aL */
    public String condition13;

    /* JADX INFO: renamed from: j */
    public ActionType queueType = ActionType.popupQueue;

    /* JADX INFO: renamed from: p */
    public float buildTime = -999.0f;

    /* JADX INFO: renamed from: K */
    public boolean isDefaultAction = false;

    /* JADX INFO: renamed from: L */
    public boolean isDefaultBuildCommand = false;

    /* JADX INFO: renamed from: M */
    public boolean isDefaultBuildCommand2 = true;

    /* JADX INFO: renamed from: N */
    public boolean isQueueUnitCommand = true;

    /* JADX INFO: renamed from: O */
    public boolean isDefaultBuildCommand3 = false;

    /* JADX INFO: renamed from: P */
    public boolean isDefaultBuildCommand4 = false;

    /* JADX INFO: renamed from: S */
    public float cooldownTime = 0.01f;

    /* JADX INFO: renamed from: T */
    public boolean autoRepeat = false;

    /* JADX INFO: renamed from: U */
    public boolean autoRepeat2 = false;
    public FastArrayList ac = new FastArrayList();
    public ActionDisplayType aG = ActionDisplayType.queueUnit;
    public BuildType aM = BuildType.build;
    public com.corrodinggames.rts.game.units.custom.logic.ActionType aN = com.corrodinggames.rts.game.units.custom.logic.ActionType.auto;

    public String a() {
        if (this.description != null) {
            return this.description.b();
        }
        return this.displayName;
    }
}
