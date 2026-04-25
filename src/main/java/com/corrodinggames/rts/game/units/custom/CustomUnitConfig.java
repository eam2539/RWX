package com.corrodinggames.rts.game.units.custom;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.game.ColorMode;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitBehaviorType;
import com.corrodinggames.rts.game.units.UnitContainer;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.SelectUnitTypeAction;
import com.corrodinggames.rts.game.units.custom.hooks.AttachmentSlotDefinition;
import com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook;
import com.corrodinggames.rts.game.units.custom.hooks.DecalDefinition;
import com.corrodinggames.rts.game.units.custom.logic.CustomAction;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.game.units.custom.resources.Resource;
import com.corrodinggames.rts.game.units.custom.resources.ResourceDefinition;
import com.corrodinggames.rts.game.units.custom.resources.StoredResources;
import com.corrodinggames.rts.game.units.custom.tracking.AnimationTrackingEntry;
import com.corrodinggames.rts.game.units.land.HovercraftUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;
import com.corrodinggames.rts.gameFramework.mod.ModManager;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.RwmodFileLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/l.class */
public final class CustomUnitConfig implements UnitType {

    /* JADX INFO: renamed from: b */
    public static CustomUnitConfig instance;

    /* JADX INFO: renamed from: s */
    public boolean teamColorsOnTurret;

    /* JADX INFO: renamed from: t */
    public boolean isBio;

    /* JADX INFO: renamed from: u */
    public boolean isBug;

    /* JADX INFO: renamed from: v */
    public boolean isBuilder;

    /* JADX INFO: renamed from: w */
    public boolean isBuilderNonAssist;

    /* JADX INFO: renamed from: x */
    public boolean canBuildOnWater;

    /* JADX INFO: renamed from: y */
    public boolean canBuildOnGround;

    /* JADX INFO: renamed from: z */
    public boolean isAir;

    /* JADX INFO: renamed from: B */
    public boolean isHover;

    /* JADX INFO: renamed from: C */
    public Rect tech;

    /* JADX INFO: renamed from: D */
    public String configPath;

    /* JADX INFO: renamed from: E */
    public String generation_delay;

    /* JADX INFO: renamed from: F */
    public String generation_free_in_sandbox;

    /* JADX INFO: renamed from: H */
    public int generation_unit;

    /* JADX INFO: renamed from: I */
    public String overrideAndReplace;

    /* JADX INFO: renamed from: J */
    public ModInfo modInfo;

    /* JADX INFO: renamed from: K */
    public String onNewMapSpawn_ifUnitIsMissing;

    /* JADX INFO: renamed from: L */
    public String onNewMapSpawn_ifUnitIsPresent;

    /* JADX INFO: renamed from: M */
    public String onNewMapSpawn;

    /* JADX INFO: renamed from: O */
    public AnimationSet image_shield;

    /* JADX INFO: renamed from: P */
    public AnimationSet image_turret;

    /* JADX INFO: renamed from: Q */
    public String image_shadow;

    /* JADX INFO: renamed from: R */
    public int shadowOffsetX;

    /* JADX INFO: renamed from: S */
    public int shadowOffsetY;

    /* JADX INFO: renamed from: T */
    public SpawnPointType teamColors;

    /* JADX INFO: renamed from: Y */
    public int lockTurretWithBody;

    /* JADX INFO: renamed from: Z */
    public LogicBoolean lockTurretWithBody_ignoreBaseTurret;

    /* JADX INFO: renamed from: aa */
    public boolean alsoUseTurretImageForOtherSide;

    /* JADX INFO: renamed from: ab */
    public boolean turretMultiTargeting;

    /* JADX INFO: renamed from: ac */
    public ColorMode baseDamage;

    /* JADX INFO: renamed from: af */
    public int maxHp;

    /* JADX INFO: renamed from: ag */
    public int mass;

    /* JADX INFO: renamed from: ah */
    public int maxMass;

    /* JADX INFO: renamed from: ai */
    public int techLevel;

    /* JADX INFO: renamed from: aj */
    public float armour;

    /* JADX INFO: renamed from: ak */
    public boolean armourMinDamageToKeep;

    /* JADX INFO: renamed from: am */
    public boolean shield;

    /* JADX INFO: renamed from: aq */
    public boolean shieldDisplayOnlyDeflection;

    /* JADX INFO: renamed from: as */
    public Texture[] energy;

    /* JADX INFO: renamed from: aw */
    public Texture energyNeedsToRechargeToFull;

    /* JADX INFO: renamed from: aB */
    public boolean showTransportBar;

    /* JADX INFO: renamed from: aC */
    public LocaleString internalName;

    /* JADX INFO: renamed from: aD */
    public LocaleString displayName;

    /* JADX INFO: renamed from: aE */
    public String baseClassName;

    /* JADX INFO: renamed from: aF */
    public boolean transportUnitsKillOnDeath;

    /* JADX INFO: renamed from: aH */
    public boolean isBuildingUnit;

    /* JADX INFO: renamed from: aI */
    public boolean isMobileUnit;

    /* JADX INFO: renamed from: aJ */
    public boolean transportUnitsCanLoadUnitWithTags;

    /* JADX INFO: renamed from: aK */
    public boolean transportUnitsUnloadRight;

    /* JADX INFO: renamed from: aL */
    public float transportUnitsUnloadLeft;

    /* JADX INFO: renamed from: aM */
    public boolean transportUnitsUnloadAndGiveOrder;

    /* JADX INFO: renamed from: aN */
    public boolean transportSlotsNeeded;

    /* JADX INFO: renamed from: aO */
    public boolean transportSwitchToSlots;

    /* JADX INFO: renamed from: aP */
    public boolean addResources;

    /* JADX INFO: renamed from: aQ */
    public boolean isCloaked;

    /* JADX INFO: renamed from: aR */
    public boolean cloakHiddenFromAllies;

    /* JADX INFO: renamed from: aS */
    public AnimationSet deathAnimation;

    /* JADX INFO: renamed from: aT */
    public boolean showStatusBar;

    /* JADX INFO: renamed from: aU */
    public boolean flag_f1aU;

    /* JADX INFO: renamed from: aV */
    public boolean f1aV;

    /* JADX INFO: renamed from: aW */
    public boolean f2aW;

    /* JADX INFO: renamed from: aY */
    public boolean f4aY;

    /* JADX INFO: renamed from: ba */
    public boolean isBuilding;

    /* JADX INFO: renamed from: bb */
    public float fogOfWarSightRange;

    /* JADX INFO: renamed from: bc */
    public float fogOfWarSightRangeWhileNotBuilt;

    /* JADX INFO: renamed from: be */
    public float buildingToFootprintOffsetX;

    /* JADX INFO: renamed from: bf */
    public float buildingToFootprintOffsetY;

    /* JADX INFO: renamed from: bg */
    public boolean isFactory;

    /* JADX INFO: renamed from: bh */
    public float isExtractor;

    /* JADX INFO: renamed from: bi */
    public boolean isTurret;

    /* JADX INFO: renamed from: bj */
    public boolean isConstructionBox;

    /* JADX INFO: renamed from: bk */
    public boolean canNotBeGivenOrdersByPlayer;

    /* JADX INFO: renamed from: bl */
    public boolean canNotBeDirectlyAttacked;

    /* JADX INFO: renamed from: bm */
    public int footprint;

    /* JADX INFO: renamed from: bn */
    public boolean displayFootprint;

    /* JADX INFO: renamed from: bo */
    public float constructionPylon;

    /* JADX INFO: renamed from: bp */
    public float pylonRadius;

    /* JADX INFO: renamed from: bq */
    public int pylonRequiresPower;

    /* JADX INFO: renamed from: br */
    public boolean pylonBuildWith;

    /* JADX INFO: renamed from: bs */
    public boolean pylonBuildWithOr;

    /* JADX INFO: renamed from: bt */
    public UnitSize unitSize;

    /* JADX INFO: renamed from: bu */
    public boolean softRadius;

    /* JADX INFO: renamed from: bv */
    public boolean canRepair;

    /* JADX INFO: renamed from: bw */
    public CustomUnitSpawnList canGuard;

    /* JADX INFO: renamed from: bx */
    public CustomUnitSpawnList canPatrol;

    /* JADX INFO: renamed from: by */
    public CustomUnitSpawnList canAttack;

    /* JADX INFO: renamed from: bz */
    public SoundList canAttackFlying;

    /* JADX INFO: renamed from: bC */
    public UnitSpawner canAttackNotTouchingWater;

    /* JADX INFO: renamed from: bD */
    public boolean targetGround;

    /* JADX INFO: renamed from: bE */
    public boolean targetAir;

    /* JADX INFO: renamed from: bF */
    public boolean targetBuildings;

    /* JADX INFO: renamed from: bG */
    public boolean targetProjectiles;

    /* JADX INFO: renamed from: bJ */
    boolean targetAllyTeam;

    /* JADX INFO: renamed from: bK */
    boolean targetEnemyTeam;

    /* JADX INFO: renamed from: bL */
    boolean targetGroundOnlyLast;

    /* JADX INFO: renamed from: bM */
    boolean canAttackCondition;

    /* JADX INFO: renamed from: bN */
    boolean canAttackWhileGarrisoned;

    /* JADX INFO: renamed from: bO */
    CustomUnitSpawnList canBeAttacked;

    /* JADX INFO: renamed from: bP */
    CustomUnitSpawnList isSelectable;

    /* JADX INFO: renamed from: bQ */
    boolean showOnMinimap;

    /* JADX INFO: renamed from: bR */
    float showOnMinimapToEnemies;

    /* JADX INFO: renamed from: bS */
    boolean showActionsAndWaypoints;

    /* JADX INFO: renamed from: bT */
    float showOnHpHover;

    /* JADX INFO: renamed from: bU */
    CustomUnitSpawnList showInEditor;

    /* JADX INFO: renamed from: bV */
    CustomUnitSpawnList stayNeutral;

    /* JADX INFO: renamed from: bW */
    boolean createNeutral;

    /* JADX INFO: renamed from: bX */
    float createOnAggressiveTeam;

    /* JADX INFO: renamed from: bY */
    CustomUnitSpawnList createOnNeutralTeam;

    /* JADX INFO: renamed from: bZ */
    CustomUnitSpawnList createOnSameTeamAsParent;

    /* JADX INFO: renamed from: cc */
    public boolean canBeBuiltBy;

    /* JADX INFO: renamed from: cd */
    public boolean canBeBuiltByUnit;

    /* JADX INFO: renamed from: ce */
    public boolean canBeBuiltByFactory;

    /* JADX INFO: renamed from: cf */
    public boolean canBeBuiltByBuilding;

    /* JADX INFO: renamed from: cg */
    public boolean canBeBuiltByAir;

    /* JADX INFO: renamed from: ch */
    public UnitPrice buildPrice;

    /* JADX INFO: renamed from: ci */
    public UnitPrice generationResources;

    /* JADX INFO: renamed from: cj */
    public UnitPrice streamingPrice;

    /* JADX INFO: renamed from: cl */
    public int canBeBuiltByTurret;

    /* JADX INFO: renamed from: cm */
    public boolean canBeBuiltByExtractor;

    /* JADX INFO: renamed from: cn */
    public boolean canBeBuiltByBuilder;

    /* JADX INFO: renamed from: cr */
    public int canBeBuiltByNotTags;

    /* JADX INFO: renamed from: cs */
    public float canBeBuiltByOrTags;

    /* JADX INFO: renamed from: cx */
    public LogicBoolean canBeBuiltByOrTagsOrLogic;

    /* JADX INFO: renamed from: cy */
    public boolean canBeBuiltByAndTagsAndLogic;

    /* JADX INFO: renamed from: cz */
    public boolean canBeBuiltByAndTagsOrLogic;

    /* JADX INFO: renamed from: cA */
    public boolean canBeBuiltByOrTagsAndLogicOrTags;

    /* JADX INFO: renamed from: cB */
    public boolean canBeBuiltByOrTagsAndLogicAndTags;

    /* JADX INFO: renamed from: cC */
    public boolean canBeBuiltByAndTagsAndLogicOrTags;

    /* JADX INFO: renamed from: cD */
    public boolean canBeBuiltByAndTagsAndLogicAndTags;

    /* JADX INFO: renamed from: cE */
    public boolean canBeBuiltByOrTagsOrLogicOrTags;

    /* JADX INFO: renamed from: cF */
    public float canBeBuiltByOrTagsOrLogicAndTags;

    /* JADX INFO: renamed from: cG */
    public int canBeBuiltByAndTagsOrLogicOrTags;

    /* JADX INFO: renamed from: cH */
    public AnimationSet canBeBuiltByAndTagsOrLogicAndTags;

    /* JADX INFO: renamed from: cJ */
    public float canBeBuiltByOrTagsAndLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: cK */
    public float canBeBuiltByOrTagsAndLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: cM */
    public boolean canBeBuiltByAndTagsAndLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: cN */
    public float canBeBuiltByAndTagsAndLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: cO */
    public boolean canBeBuiltByAndTagsAndLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: cP */
    public float canBeBuiltByAndTagsAndLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: cQ */
    public float canBeBuiltByOrTagsOrLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: cR */
    public boolean canBeBuiltByOrTagsOrLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: cS */
    public float canBeBuiltByOrTagsOrLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: cT */
    public LocaleString canBeBuiltByOrTagsOrLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: cU */
    public boolean canBeBuiltByAndTagsOrLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: cV */
    public float canBeBuiltByAndTagsOrLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: cW */
    public int canBeBuiltByAndTagsOrLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: da */
    public float canBeBuiltByOrTagsAndLogicOrTagsOrLogicOrTags;

    /* JADX INFO: renamed from: db */
    public float canBeBuiltByOrTagsAndLogicOrTagsOrLogicAndTags;

    /* JADX INFO: renamed from: dc */
    public boolean canBeBuiltByOrTagsAndLogicAndTagsAndLogicOrTags;

    /* JADX INFO: renamed from: dd */
    public int canBeBuiltByOrTagsAndLogicAndTagsAndLogicAndTags;

    /* JADX INFO: renamed from: de */
    public float canBeBuiltByOrTagsAndLogicAndTagsOrLogicOrTags;

    /* JADX INFO: renamed from: df */
    public int canBeBuiltByOrTagsAndLogicAndTagsOrLogicAndTags;

    /* JADX INFO: renamed from: dg */
    public int canBeBuiltByAndTagsAndLogicOrTagsAndLogicOrTags;

    /* JADX INFO: renamed from: dh */
    public int canBeBuiltByAndTagsAndLogicOrTagsAndLogicAndTags;

    /* JADX INFO: renamed from: di */
    public float spawnOffsetLocalX;

    /* JADX INFO: renamed from: dj */
    public float spawnOffsetLocalY;

    /* JADX INFO: renamed from: dk */
    public Float spawnAngleOffset;

    /* JADX INFO: renamed from: dl */
    public float spawnOffsetZ;

    /* JADX INFO: renamed from: dm */
    public boolean keepOriginalZOnSpawn;

    /* JADX INFO: renamed from: dn */
    public Float spawnSideOffset;

    /* JADX INFO: renamed from: do, reason: not valid java name */
    public SoundList f0do;

    /* JADX INFO: renamed from: dp */
    public SoundList canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTags;

    /* JADX INFO: renamed from: dq */
    public SoundList canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTags;

    /* JADX INFO: renamed from: dv */
    AnimationConfig canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTags;

    /* JADX INFO: renamed from: dw */
    AnimationConfig canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTags;

    /* JADX INFO: renamed from: dx */
    AnimationConfig canBeBuiltByAndTagsOrLogicOrTagsAndLogicAndTags;

    /* JADX INFO: renamed from: dy */
    AnimationConfig canBeBuiltByAndTagsOrLogicOrTagsOrLogicOrTags;

    /* JADX INFO: renamed from: dz */
    AnimationConfig canBeBuiltByAndTagsOrLogicOrTagsOrLogicAndTags;

    /* JADX INFO: renamed from: dA */
    AnimationConfig canBeBuiltByAndTagsOrLogicAndTagsAndLogicOrTags;

    /* JADX INFO: renamed from: dB */
    public boolean canBeBuiltByAndTagsOrLogicAndTagsAndLogicAndTags;

    /* JADX INFO: renamed from: dC */
    public boolean canBeBuiltByAndTagsOrLogicAndTagsOrLogicOrTags;

    /* JADX INFO: renamed from: dD */
    public boolean canBeBuiltByAndTagsOrLogicAndTagsOrLogicAndTags;

    /* JADX INFO: renamed from: dE */
    public boolean canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: dF */
    public TurretConfig canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: dG */
    public int canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: dH */
    public float canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: dI */
    CustomUnitDirectionConfig canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: dJ */
    public float canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: dK */
    public Boolean canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: dL */
    public boolean canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: dM */
    public float canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: dN */
    public float canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: dO */
    public float canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: dP */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: dQ */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: dR */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: dT */
    public float canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: dX */
    public boolean canBeBuiltByAndTagsOrLogicOrTagsAndLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: dY */
    public boolean canBeBuiltByAndTagsOrLogicOrTagsOrLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: dZ */
    public int canBeBuiltByAndTagsOrLogicOrTagsOrLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: ea */
    public float canBeBuiltByAndTagsOrLogicOrTagsOrLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: eb */
    public float canBeBuiltByAndTagsOrLogicOrTagsOrLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: ec */
    public UnitBehaviorType attackMovementType;

    /* JADX INFO: renamed from: ed */
    public float attackMovementSpeed;

    /* JADX INFO: renamed from: ee */
    public float attackMovementSpread;

    /* JADX INFO: renamed from: ef */
    public boolean canBeBuiltByAndTagsOrLogicAndTagsAndLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: eg */
    public boolean canBeBuiltByAndTagsOrLogicAndTagsOrLogicOrTagsAndLogic;

    /* JADX INFO: renamed from: eh */
    public boolean canBeBuiltByAndTagsOrLogicAndTagsOrLogicOrTagsOrLogic;

    /* JADX INFO: renamed from: ei */
    public boolean canBeBuiltByAndTagsOrLogicAndTagsOrLogicAndTagsAndLogic;

    /* JADX INFO: renamed from: ej */
    public float canBeBuiltByAndTagsOrLogicAndTagsOrLogicAndTagsOrLogic;

    /* JADX INFO: renamed from: ek */
    public float canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsAndLogicOrTags;

    /* JADX INFO: renamed from: el */
    public float canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsAndLogicAndTags;

    /* JADX INFO: renamed from: eo */
    public float canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogicOrTags;

    /* JADX INFO: renamed from: ep */
    public boolean canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogicAndTags;

    /* JADX INFO: renamed from: eq */
    public LogicBoolean canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsOrLogicOrTags;

    /* JADX INFO: renamed from: er */
    public LogicBoolean canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsOrLogicAndTags;

    /* JADX INFO: renamed from: es */
    public LogicBoolean canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsAndLogicOrTags;

    /* JADX INFO: renamed from: et */
    public LogicBoolean canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsAndLogicAndTags;

    /* JADX INFO: renamed from: eu */
    public boolean canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsOrLogicOrTags;

    /* JADX INFO: renamed from: ev */
    public AnimationSet canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsOrLogicAndTags;

    /* JADX INFO: renamed from: ew */
    public AnimationSet canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogicOrTags;

    /* JADX INFO: renamed from: ex */
    public boolean canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogicAndTags;

    /* JADX INFO: renamed from: ey */
    public boolean canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsOrLogicOrTags;

    /* JADX INFO: renamed from: ez */
    public float canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsOrLogicAndTags;

    /* JADX INFO: renamed from: eA */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsAndLogicOrTags;

    /* JADX INFO: renamed from: eB */
    public int canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsAndLogicAndTags;

    /* JADX INFO: renamed from: eC */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsOrLogicOrTags;

    /* JADX INFO: renamed from: eD */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsOrLogicAndTags;

    /* JADX INFO: renamed from: eE */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicOrTags;

    /* JADX INFO: renamed from: eF */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicAndTags;

    /* JADX INFO: renamed from: eG */
    public float canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsOrLogicOrTags;

    /* JADX INFO: renamed from: eH */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsOrLogicAndTags;

    /* JADX INFO: renamed from: eI */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsAndLogicAndTags;

    /* JADX INFO: renamed from: eN */
    public float canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicAndTags;

    /* JADX INFO: renamed from: eO */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogicOrTags;

    /* JADX INFO: renamed from: eP */
    public AnimationSet canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogicAndTags;

    /* JADX INFO: renamed from: eR */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogicAndTags;

    /* JADX INFO: renamed from: eS */
    public boolean f8eS;

    /* JADX INFO: renamed from: eT */
    public boolean f9eT;

    /* JADX INFO: renamed from: eU */
    public LogicBoolean f10eU;

    /* JADX INFO: renamed from: eV */
    public LogicBoolean f11eV;

    /* JADX INFO: renamed from: eW */
    public LogicBoolean f12eW;

    /* JADX INFO: renamed from: eX */
    public boolean f13eX;

    /* JADX INFO: renamed from: eY */
    public float f14eY;

    /* JADX INFO: renamed from: fc */
    public LogicBoolean f16fc;

    /* JADX INFO: renamed from: fd */
    public LogicBoolean f17fd;

    /* JADX INFO: renamed from: ff */
    public PlacementRules customUnitMetadata;

    /* JADX INFO: renamed from: fg */
    public UnitMovementType movementType;

    /* JADX INFO: renamed from: fh */
    public UnitMovementType f19fh;

    /* JADX INFO: renamed from: fi */
    public boolean f20fi;

    /* JADX INFO: renamed from: fj */
    public boolean f21fj;

    /* JADX INFO: renamed from: fk */
    public boolean f22fk;

    /* JADX INFO: renamed from: fl */
    public AnimationSet f23fl;

    /* JADX INFO: renamed from: fm */
    public int f24fm;

    /* JADX INFO: renamed from: fn */
    public AnimationSet f25fn;

    /* JADX INFO: renamed from: fo */
    public AnimationSet f26fo;

    /* JADX INFO: renamed from: fp */
    public boolean isFlying;

    /* JADX INFO: renamed from: fq */
    public boolean hasTransportCapability;

    /* JADX INFO: renamed from: fr */
    public boolean isUnselectable;

    /* JADX INFO: renamed from: fs */
    public boolean f27fs;

    /* JADX INFO: renamed from: ft */
    public boolean f28ft;

    /* JADX INFO: renamed from: fu */
    public boolean f29fu;

    /* JADX INFO: renamed from: fv */
    public AnimationSet f30fv;

    /* JADX INFO: renamed from: fw */
    public boolean f31fw;

    /* JADX INFO: renamed from: fx */
    public int f32fx;

    /* JADX INFO: renamed from: fy */
    public int f33fy;

    /* JADX INFO: renamed from: fz */
    public float f34fz;

    /* JADX INFO: renamed from: fA */
    public int f35fA;

    /* JADX INFO: renamed from: fB */
    public float f36fB;

    /* JADX INFO: renamed from: fC */
    public float f37fC;

    /* JADX INFO: renamed from: fD */
    public float f38fD;

    /* JADX INFO: renamed from: fE */
    public int f39fE;

    /* JADX INFO: renamed from: fF */
    public int f40fF;

    /* JADX INFO: renamed from: fG */
    public boolean f41fG;

    /* JADX INFO: renamed from: fH */
    public AnimationSet f42fH;

    /* JADX INFO: renamed from: fI */
    public String f43fI;

    /* JADX INFO: renamed from: fJ */
    public boolean f44fJ;

    /* JADX INFO: renamed from: fM */
    public boolean f46fM;

    /* JADX INFO: renamed from: fN */
    public boolean f47fN;

    /* JADX INFO: renamed from: fO */
    public AnimationSet f48fO;

    /* JADX INFO: renamed from: fP */
    public boolean f49fP;

    /* JADX INFO: renamed from: fR */
    public CustomProjectileTemplate[] f50fR;

    /* JADX INFO: renamed from: fX */
    boolean f52fX;

    /* JADX INFO: renamed from: fY */
    CustomUnitCondition[] autoTriggerConditionsEveryFrame;

    /* JADX INFO: renamed from: fZ */
    CustomUnitCondition[] autoTriggerConditionsEvery4Frames;

    /* JADX INFO: renamed from: ga */
    CustomUnitCondition[] autoTriggerConditionsEvery8Frames;

    /* JADX INFO: renamed from: gi */
    public boolean f56gi;

    /* JADX INFO: renamed from: gk */
    String cachedUnitName;

    /* JADX INFO: renamed from: gl */
    String cachedDescription;

    /* JADX INFO: renamed from: gm */
    HashMap actionIdCache;

    /* JADX INFO: renamed from: gn */
    UnitContainer[] specialActionLists;

    /* JADX INFO: renamed from: gr */
    public boolean hasCreditResourceCost;

    /* JADX INFO: renamed from: gs */
    public boolean debugCreditResourceUsage;

    /* JADX INFO: renamed from: a */
    public static final Rect tempRect = new Rect();

    /* JADX INFO: renamed from: c */
    public static final ArrayList<CustomUnitConfig> allConfigs = new ArrayList();

    /* JADX INFO: renamed from: d */
    public static ArrayList<CustomUnitConfig> activeConfigs = new ArrayList();

    /* JADX INFO: renamed from: e */
    public static ArrayList validUnitsForSync = null;

    /* JADX INFO: renamed from: f */
    public static final HashMap<UnitType,UnitType> unitTypeOverrides = new HashMap();

    /* JADX INFO: renamed from: g */
    public static ArrayList<CustomUnitConfig> configsById = new ArrayList();

    /* JADX INFO: renamed from: fa */
    public static LogicBoolean logic_notOverLiquidAndNotMoving = LogicBoolean.create(null, "if not self.isOverLiquid() and not self.isMoving()");

    /* JADX INFO: renamed from: fb */
    public static LogicBoolean logic_notOverLiquid = LogicBoolean.create(null, "if not self.isOverLiquid()");

    /* JADX INFO: renamed from: ge */
    static final EffectTemplate[] f54ge = new EffectTemplate[0];

    /* JADX INFO: renamed from: gf */
    static final EffectTemplate[] f55gf = new EffectTemplate[0];

    /* JADX INFO: renamed from: h */
    public final FastArrayList onCreateListeners = new FastArrayList();

    /* JADX INFO: renamed from: i */
    public final FastArrayList onDestroyListeners = new FastArrayList();

    /* JADX INFO: renamed from: j */
    public final FastArrayList<ResourceDefinition> customResourcesList = new FastArrayList();

    /* JADX INFO: renamed from: k */
    final FastArrayList<FileWatcher> fileWatchers = new FastArrayList();

    /* JADX INFO: renamed from: l */
    public final FastArrayList<AnimationTrackingEntry> animationChannels = new FastArrayList();

    /* JADX INFO: renamed from: m */
    public final FastArrayList customTurrets = new FastArrayList();

    /* JADX INFO: renamed from: n */
    public final FastArrayList customLegs = new FastArrayList();

    /* JADX INFO: renamed from: o */
    public final FastArrayList customArms = new FastArrayList();

    /* JADX INFO: renamed from: p */
    final FastArrayList unitTypeReferences = new FastArrayList();

    /* JADX INFO: renamed from: q */
    public final FastArrayList<DecalDefinition> customAttachments = new FastArrayList();

    /* JADX INFO: renamed from: r */
    public final VariableScope.VariableMapping variableMapping = new VariableScope.VariableMapping();

    /* JADX INFO: renamed from: A */
    public boolean isLand = true;

    /* JADX INFO: renamed from: G */
    public boolean generation_repeat = true;

    /* JADX INFO: renamed from: N */
    public FastArrayList autoTriggerAction = new FastArrayList();

    /* JADX INFO: renamed from: U */
    public int turretSize = 1;

    /* JADX INFO: renamed from: V */
    public int turretTurnSpeed = Integer.MAX_VALUE;

    /* JADX INFO: renamed from: W */
    public int turretRotateWithBody = -1;
    public int X = -1;

    /* JADX INFO: renamed from: ad */
    public Texture baseTexture = null;

    /* JADX INFO: renamed from: ae */
    public boolean hp = true;

    /* JADX INFO: renamed from: al */
    public Texture disableCustomShields = null;

    /* JADX INFO: renamed from: an */
    public Texture maxShield = null;

    /* JADX INFO: renamed from: ao */
    public Texture shieldRegen = null;

    /* JADX INFO: renamed from: ap */
    public Texture shieldRegenMoving = null;

    /* JADX INFO: renamed from: ar */
    public Texture[] startShieldAtZero = new Texture[10];

    /* JADX INFO: renamed from: at */
    public Texture[] maxEnergy = null;

    /* JADX INFO: renamed from: au */
    public Texture energyRegen = null;

    /* JADX INFO: renamed from: av */
    public boolean energyRegenWhenRecharging = false;

    /* JADX INFO: renamed from: ax */
    public LegConfig[] energyDisplayName = null;

    /* JADX INFO: renamed from: ay */
    public boolean energyStartPercentage = false;

    /* JADX INFO: renamed from: az */
    public boolean energyCanBeRecievedFromInAnotherUnit = false;

    /* JADX INFO: renamed from: aA */
    public final FastArrayList<AttachmentSlotDefinition> energyCanTransferToOtherUnits = new FastArrayList();

    /* JADX INFO: renamed from: aG */
    public float transportUnitsHealBy = 1.0f;

    /* JADX INFO: renamed from: aX */
    public int f3aX = -1;

    /* JADX INFO: renamed from: aZ */
    public int isExperimental = -1;

    /* JADX INFO: renamed from: bd */
    public float useBuildingSmoke = 1.0f;

    /* JADX INFO: renamed from: bA */
    public int canAttackLand = -1;

    /* JADX INFO: renamed from: bB */
    public int canAttackUnderwater = -1;

    /* JADX INFO: renamed from: bH */
    public float targetOwnTeam = 1.0f;

    /* JADX INFO: renamed from: bI */
    public float targetNeutralTeam = 1.0f;

    /* JADX INFO: renamed from: ca */
    public float autoTriggerCooldownTime = 60.0f;

    /* JADX INFO: renamed from: cb */
    public UpdateFrequency autoTriggerCheckRate = UpdateFrequency.everyFrame;

    /* JADX INFO: renamed from: ck */
    public float buildTimeSeconds = 0.001f;

    /* JADX INFO: renamed from: co */
    public UnitPrice generationTemplate = UnitPrice.a;

    /* JADX INFO: renamed from: cp */
    public StoredResources generationCondition = StoredResources.a;

    /* JADX INFO: renamed from: cq */
    public StoredResources generationTagTemplate = StoredResources.a;

    /* JADX INFO: renamed from: ct */
    public VariableScope.CachedWriter updateUnitMemoryWriter = null;

    /* JADX INFO: renamed from: cu */
    public float updateUnitMemoryRate = 1.0f;

    /* JADX INFO: renamed from: cv */
    public UnitPrice canBeBuiltByAndLogic = UnitPrice.a;

    /* JADX INFO: renamed from: cw */
    public UnitPrice canBeBuiltByOrTagsAndLogic = UnitPrice.a;

    /* JADX INFO: renamed from: cI */
    public int canBeBuiltByOrTagsAndLogicOrTagsAndLogic = -2;

    /* JADX INFO: renamed from: cL */
    public UnitStats canBeBuiltByOrTagsAndLogicAndTagsOrLogic = new UnitStats(true);

    /* JADX INFO: renamed from: cX */
    public Rect canBeBuiltByAndTagsOrLogicAndTagsOrLogic = new Rect();

    /* JADX INFO: renamed from: cY */
    public Rect canBeBuiltByOrTagsAndLogicOrTagsAndLogicOrTags = new Rect();

    /* JADX INFO: renamed from: cZ */
    public Rect canBeBuiltByOrTagsAndLogicOrTagsAndLogicAndTags = new Rect();

    /* JADX INFO: renamed from: dr */
    FastArrayList<AnimationConfig> canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTags = new FastArrayList();

    /* JADX INFO: renamed from: ds */
    AnimationConfig canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTags = new AnimationConfig("moving");

    /* JADX INFO: renamed from: dt */
    AnimationConfig canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTags = new AnimationConfig("idle");

    /* JADX INFO: renamed from: du */
    AnimationConfig canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTags = new AnimationConfig("attack");

    /* JADX INFO: renamed from: dS */
    public float canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogic = 0.0f;

    /* JADX INFO: renamed from: dU */
    public float canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsAndLogic = -1.0f;

    /* JADX INFO: renamed from: dV */
    public float canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsOrLogic = 0.03f;

    /* JADX INFO: renamed from: dW */
    public float canBeBuiltByAndTagsOrLogicOrTagsAndLogicAndTagsAndLogic = 0.06f;

    /* JADX INFO: renamed from: em */
    public int canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicOrTags = -1;

    /* JADX INFO: renamed from: en */
    public int canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicAndTags = -1;

    /* JADX INFO: renamed from: eJ */
    public boolean f6eJ = false;

    /* JADX INFO: renamed from: eK */
    public boolean canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsAndLogicOrTags = false;

    /* JADX INFO: renamed from: eL */
    public boolean f7eL = false;

    /* JADX INFO: renamed from: eM */
    public int canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags = 0;

    /* JADX INFO: renamed from: eQ */
    public FastArrayList canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogicOrTags = new FastArrayList();

    /* JADX INFO: renamed from: eZ */
    public int f15eZ = 1;

    /* JADX INFO: renamed from: fe */
    public boolean f18fe = true;

    /* JADX INFO: renamed from: fK */
    public float f45fK = -1.0f;

    /* JADX INFO: renamed from: fL */
    public FastArrayList<UnitType> relatedUnits = new FastArrayList();

    /* JADX INFO: renamed from: fQ */
    public TurretConfig[] turrets = null;

    /* JADX INFO: renamed from: fS */
    ArrayList<TurretConfig> projectileConfigs = new ArrayList();

    /* JADX INFO: renamed from: fT */
    ArrayList<CustomProjectileTemplate> projectileTemplates = new ArrayList();

    /* JADX INFO: renamed from: fU */
    boolean f51fU = false;

    /* JADX INFO: renamed from: fV */
    TurretConfig defaultTurret = null;

    /* JADX INFO: renamed from: fW */
    FastArrayList<CustomUnitCondition> autoTriggerConditions = new FastArrayList();

    /* JADX INFO: renamed from: gb */
    FastArrayList configProcessors = new FastArrayList();

    /* JADX INFO: renamed from: gc */
    ArrayList f53gc = new ArrayList();

    /* JADX INFO: renamed from: gd */
    ArrayList<EffectTemplate> customEffects = new ArrayList();

    /* JADX INFO: renamed from: gg */
    ArrayList<CustomUnitTrigger> customUnitTriggers = new ArrayList();

    /* JADX INFO: renamed from: gh */
    ArrayList<CustomActionDef> customActionDefs = new ArrayList();

    /* JADX INFO: renamed from: gj */
    int localeReloadCount = -1;

    /* JADX INFO: renamed from: go */
    SelectUnitTypeAction selectUnitTypeAction = new SelectUnitTypeAction(this);

    /* JADX INFO: renamed from: gp */
    FastArrayList<CustomUnitActionHandler> actionHandlers = new FastArrayList();

    /* JADX INFO: renamed from: gq */
    FastArrayList eventBindings = new FastArrayList();

    /* JADX INFO: renamed from: gt */
    FastArrayList warnings = new FastArrayList();

    /* JADX INFO: renamed from: b */
    public String getConfigDisplayPath() {
        String strSubstring = this.configPath;
        if (this.modInfo != null) {
            String str = this.modInfo.sourceFolder;
            if (strSubstring.startsWith(str)) {
                strSubstring = strSubstring.substring(str.length());
                if (strSubstring.startsWith("/")) {
                    strSubstring = strSubstring.substring(1);
                }
                if (strSubstring.startsWith("\\")) {
                    strSubstring = strSubstring.substring(1);
                }
            }
            strSubstring = strSubstring + " (in mod " + this.modInfo.getDisplayTitle() + ")";
        }
        return strSubstring;
    }

    /* JADX INFO: renamed from: a */
    public CustomUnitAnimationReference loadCore(String str, CustomUnitAnimationReference customUnitAnimationReference) {
        if (str != null) {
            CustomUnitAnimationReference customUnitAnimationReference2 = new CustomUnitAnimationReference(this);
            customUnitAnimationReference2.animationName = str;
            customUnitAnimationReference2.a();
            return customUnitAnimationReference2;
        }
        if (customUnitAnimationReference != null) {
            CustomUnitAnimationReference customUnitAnimationReference3 = new CustomUnitAnimationReference(this);
            customUnitAnimationReference3.animationName = customUnitAnimationReference.animationName;
            customUnitAnimationReference3.a();
            return customUnitAnimationReference3;
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    AnimationConfig loadData(CustomUnitAction customUnitAction, AnimationConfig animationConfig, boolean z) throws ConfigParseException {
        AnimationConfig animationConfigFindAnimationForAction = findAnimationForAction(customUnitAction);
        if (animationConfigFindAnimationForAction != null) {
            if (z && animationConfig != null && animationConfig.a()) {
                throw new ConfigParseException("Cannot define animation " + customUnitAction.name() + " on graphics and with onAction at same time");
            }
            return animationConfigFindAnimationForAction;
        }
        return animationConfig;
    }

    /* JADX INFO: renamed from: a */
    public static String loadData(String str) {
        String strTrim = str.toLowerCase(Locale.ROOT).trim();
        if (strTrim.startsWith("arm_")) {
            strTrim = "arm" + strTrim.substring("arm_".length());
        }
        if (strTrim.startsWith("leg_")) {
            strTrim = "leg" + strTrim.substring("leg_".length());
        }
        return strTrim;
    }

    /* JADX INFO: renamed from: b */
    public int getConfigDisplayPath(String str) {
        String strLoadData = loadData(str);
        GameEngine.isInSpace("name:" + strLoadData);
        for (int i = 0; i < this.energyDisplayName.length; i++) {
            GameEngine.isInSpace("checking:" + this.energyDisplayName[i].b);
            if (strLoadData.equals(this.energyDisplayName[i].b)) {
                GameEngine.isInSpace("got");
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: renamed from: a */
    AnimationConfig findAnimationForAction(CustomUnitAction customUnitAction) {
        for (AnimationConfig animationConfig : this.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTags) {
            if (animationConfig.a(customUnitAction)) {
                return animationConfig;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    public void registerConfigProcessor(CustomUnitConfigProcessor customUnitConfigProcessor) {
        this.configProcessors.add(customUnitConfigProcessor);
    }

    /* JADX INFO: renamed from: a */
    public CustomUnitSpawnList addConfigExtension(String str, CustomUnitSpawnList customUnitSpawnList) throws ConfigParseException {
        if (str == null && customUnitSpawnList != null) {
            return customUnitSpawnList;
        }
        CustomUnitSpawnList customUnitSpawnList2 = new CustomUnitSpawnList(this, str);
        customUnitSpawnList2.c();
        return customUnitSpawnList2;
    }

    /* JADX INFO: renamed from: c */
    public CustomUnitSpawnList createSpawnList(String str) {
        return new CustomUnitSpawnList(this, str);
    }

    /* JADX INFO: renamed from: d */
    public EffectTemplate resolveEffect(String str) throws ConfigParseException {
        boolean z = false;
        boolean z2 = false;
        String upperCase = str.toUpperCase();
        if (upperCase.startsWith("CUSTOM:")) {
            str = str.substring("CUSTOM:".length()).trim();
            z = true;
        }
        if (upperCase.startsWith("CUSTOM|")) {
            str = str.substring("CUSTOM|".length()).trim();
            z = true;
        }
        if (upperCase.startsWith("BUILTIN:") || upperCase.startsWith("BUILTIN|")) {
            str = str.substring("BUILTIN:".length()).trim();
            z2 = true;
        }
        if (z) {
            for (EffectTemplate effectTemplate : this.customEffects) {
                if (str.equalsIgnoreCase(effectTemplate.name)) {
                    return effectTemplate;
                }
            }
            throw new ConfigParseException("Failed to find custom effect with the name:" + str);
        }
        if (str.contains(":")) {
            throw new ConfigParseException("Unknown effect format:" + str + " expected built-in effect or CUSTOM:");
        }
        if (str.contains("|")) {
            throw new ConfigParseException("Unknown effect format:" + str + " expected built-in effect or CUSTOM|");
        }
        if (!z2) {
            for (EffectTemplate effectTemplate2 : this.customEffects) {
                if (str.equalsIgnoreCase(effectTemplate2.name)) {
                    return effectTemplate2;
                }
            }
        }
        if ("small".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.small);
        }
        if ("medium".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.medium);
        }
        if ("large".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.large);
        }
        if ("smoke".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.smoke);
        }
        if ("shockwave".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.shockwave);
        }
        if ("largeExplosion".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.largeExplosion);
        }
        if ("smallExplosion".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.smallExplosion);
        }
        if ("resourcePoolSmoke".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.resourcePoolSmoke);
        }
        if ("none".equalsIgnoreCase(str)) {
            return new EffectTemplate(BuiltInEffectType.noneExplosion);
        }
        throw new ConfigParseException("Failed to find built-in or custom effect with the name:" + str);
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean C() {
        return this.canBeBuiltByFactory;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean w() {
        if (this.canBeBuiltByAir) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine.isInGameOrLobby() && gameEngine.networkEngine.roomSettings.noNukes) {
                return true;
            }
        }
        return this.canBeBuiltByBuilding;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public int c() {
        return this.buildPrice.a();
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public UnitPrice u() {
        return this.buildPrice;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public UnitPrice B() {
        return this.streamingPrice;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public UnitPrice d(int i) {
        return this.buildPrice;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public float D() {
        return this.buildTimeSeconds;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public int g() {
        return this.canBeBuiltByTurret;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public BaseUnit a() {
        return a(false, this);
    }

    /* JADX INFO: renamed from: a */
    public BaseUnit createCustomUnit(boolean z) {
        return a(z, this);
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    /* JADX INFO: renamed from: e */
    public String getUnitName() {
        if (this.localeReloadCount != com.corrodinggames.rts.gameFramework.local.Locale.reloadCount || this.cachedUnitName == null) {
            this.localeReloadCount = com.corrodinggames.rts.gameFramework.local.Locale.reloadCount;
            String strResolveText = this.internalName != null ? this.internalName.resolveText() : this.onNewMapSpawn;
            String str = this.onNewMapSpawn;
            if (this.baseClassName != null) {
                str = this.baseClassName;
            }
            this.cachedUnitName = com.corrodinggames.rts.gameFramework.local.Locale.getFormattedString("units." + str + ".name", strResolveText, new Object[0]);
        }
        return this.cachedUnitName;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public String f() {
        if (this.localeReloadCount != com.corrodinggames.rts.gameFramework.local.Locale.reloadCount || this.cachedDescription == null) {
            this.localeReloadCount = com.corrodinggames.rts.gameFramework.local.Locale.reloadCount;
            String strResolveText = this.displayName != null ? this.displayName.resolveText() : this.onNewMapSpawn;
            String str = this.onNewMapSpawn;
            if (this.baseClassName != null) {
                str = this.baseClassName;
            }
            this.cachedDescription = com.corrodinggames.rts.gameFramework.local.Locale.getFormattedString("units." + str + ".description", strResolveText, new Object[0]);
        }
        return this.cachedDescription;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean j() {
        return this.isBuildingUnit;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean k() {
        return this.isMobileUnit;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean m() {
        return this.hasTransportCapability;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean n() {
        return this.isUnselectable;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean l() {
        return this.isFlying;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public UnitMovementType o() {
        return this.movementType;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean p() {
        return this.transportUnitsCanLoadUnitWithTags;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public PlacementRules q() {
        return this.customUnitMetadata;
    }

    /* JADX INFO: renamed from: a */
    public void addEditorActionsForUnit(ArrayList arrayList, int i) {
        if (this.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags != 0 && this.f9eT) {
            arrayList.add(HovercraftUnit.i);
            arrayList.add(HovercraftUnit.j);
        }
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public void h() {
        this.actionIdCache = null;
        this.specialActionLists = new UnitContainer[3];
        for (int i = 1; i <= 3; i++) {
            UnitContainer unitContainer = new UnitContainer();
            addEditorActionsForUnit(unitContainer.a, i);
            this.specialActionLists[i - 1] = unitContainer;
        }
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public ArrayList<AbstractUnitAction> a(int i) {
        if (this.specialActionLists == null) {
            GameEngine gameEngine = GameEngine.getInstance();
            throw new RuntimeException("specialActionLists==null for:" + this.onNewMapSpawn + " t:" + i + " networked:" + gameEngine.isNetworkConnected() + " replay:" + gameEngine.replayEngine.j() + " sandbox:" + gameEngine.isGameStarted + " active: " + activeConfigs.contains(this));
        }
        return this.specialActionLists[i - 1].a;
    }

    /* JADX INFO: renamed from: r */
    public void rebuildActionIdCache() {
        ArrayList arrayListA = a(this.canBeBuiltByTurret);
        if (arrayListA.size() > 4) {
            this.actionIdCache = new HashMap();
            int size = arrayListA.size();
            for (int i = 0; i < size; i++) {
                AbstractUnitAction abstractUnitAction = (AbstractUnitAction) arrayListA.get(i);
                if (this.actionIdCache.get(abstractUnitAction.getActionId()) == null) {
                    this.actionIdCache.put(abstractUnitAction.getActionId(), abstractUnitAction);
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public AbstractUnitAction findActionById(ActionId actionId) {
        if (this.actionIdCache != null) {
            return (AbstractUnitAction) this.actionIdCache.get(actionId);
        }
        ArrayList arrayListA = a(this.canBeBuiltByTurret);
        int size = arrayListA.size();
        for (int i = 0; i < size; i++) {
            AbstractUnitAction abstractUnitAction = (AbstractUnitAction) arrayListA.get(i);
            if (abstractUnitAction.isAvailableForUnit(actionId)) {
                return abstractUnitAction;
            }
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    /* JADX INFO: renamed from: i */
    public String getUnitTypeDescriptionShort() {
        return this.onNewMapSpawn;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public String v() {
        return this.onNewMapSpawn;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public int b(int i) {
        return c();
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public SelectUnitTypeAction d() {
        return this.selectUnitTypeAction;
    }

    /* JADX INFO: renamed from: a */
    public UnitTypeReference reloadAllCustomUnits(String str, String str2, String str3) {
        if (str == null) {
            return null;
        }
        UnitTypeReference unitTypeReference = new UnitTypeReference();
        unitTypeReference.configKey = str2;
        unitTypeReference.sectionName = str3;
        unitTypeReference.unitTypeName = str;
        this.unitTypeReferences.add(unitTypeReference);
        return unitTypeReference;
    }

    /* JADX INFO: renamed from: b */
    public CustomUnitProjectileReference getConfigDisplayPath(String str, String str2, String str3) {
        CustomUnitProjectileReference customUnitProjectileReference = new CustomUnitProjectileReference();
        customUnitProjectileReference.configKey = str2;
        customUnitProjectileReference.sectionName = str3;
        customUnitProjectileReference.unitTypeName = "(known unit:)" + getUnitTypeDescriptionShort();
        customUnitProjectileReference.unitType = this;
        customUnitProjectileReference.e = true;
        customUnitProjectileReference.projectileName = str;
        this.unitTypeReferences.add(customUnitProjectileReference);
        return customUnitProjectileReference;
    }

    /* JADX INFO: renamed from: c */
    public CustomUnitActionHandler addActionHandler(String str, String str2, String str3) {
        if (str == null || str.trim().equals(VariableScope.nullOrMissingString)) {
            return null;
        }
        CustomUnitActionHandler customUnitActionHandler = new CustomUnitActionHandler();
        customUnitActionHandler.actionName = str2;
        customUnitActionHandler.actionDescription = str3;
        for (String str4 : Utility.splitByChar(str, ',')) {
            customUnitActionHandler.actionNames.add(str4.trim());
        }
        this.actionHandlers.add(customUnitActionHandler);
        return customUnitActionHandler;
    }

    public static UnitTypeReference a(UnitType unitType) {
        if (unitType == null) {
            return null;
        }
        UnitTypeReference unitTypeReference = new UnitTypeReference();
        unitTypeReference.configKey = "known";
        unitTypeReference.unitType = unitType;
        unitTypeReference.e = true;
        return unitTypeReference;
    }

    public static CustomUnitConfig c(int i) {
        int i2;
        if (i >= 100 && (i2 = i - 100) < configsById.size()) {
            return (CustomUnitConfig) configsById.get(i2);
        }
        return null;
    }

    /* JADX INFO: renamed from: s */
    public static ArrayList getAllCustomUnitTypeIds() {
        ArrayList arrayList = new ArrayList();
        int i = 100;
        for (CustomUnitConfig customUnitConfig : configsById) {
            arrayList.add(Integer.valueOf(i));
            i++;
        }
        return arrayList;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public AnimationSet x() {
        return this.image_shield;
    }

    /* JADX INFO: renamed from: e */
    public TurretConfig findProjectileConfigByName(String str) {
        for (TurretConfig turretConfig : this.projectileConfigs) {
            if (turretConfig.name.equalsIgnoreCase(str)) {
                return turretConfig;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: f */
    public CustomProjectileTemplate findProjectileTemplateByName(String str) {
        for (CustomProjectileTemplate customProjectileTemplate : this.projectileTemplates) {
            if (customProjectileTemplate.projectileName.equalsIgnoreCase(str)) {
                return customProjectileTemplate;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: g */
    public CustomActionDef findCustomActionDefByDisplayName(String str) {
        for (CustomActionDef customActionDef : this.customActionDefs) {
            if (customActionDef.displayName != null && customActionDef.displayName.equalsIgnoreCase(str)) {
                return customActionDef;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: h */
    public AbstractUnitAction findCustomActionByDisplayName(String str) {
        for (AbstractUnitAction abstractUnitAction : a(this.canBeBuiltByTurret)) {
            if (abstractUnitAction instanceof CustomAction) {
                CustomAction customAction = (CustomAction) abstractUnitAction;
                if (customAction.a.displayName.equalsIgnoreCase(str)) {
                    return customAction;
                }
            }
        }
        return null;
    }

    public void a(CustomUnitRenderHook customUnitRenderHook) {
        if (!this.onCreateListeners.contains(customUnitRenderHook)) {
            this.onCreateListeners.add(customUnitRenderHook);
        }
    }

    public void b(CustomUnitRenderHook customUnitRenderHook) {
        if (!this.onDestroyListeners.contains(customUnitRenderHook)) {
            this.onDestroyListeners.add(customUnitRenderHook);
        }
    }

    public void a(UnitPrice unitPrice) {
        if (unitPrice != null && unitPrice.b != 0) {
            if (this.debugCreditResourceUsage) {
                GameEngine.logWarningAndStack("usesCreditResources:" + unitPrice);
            }
            this.hasCreditResourceCost = true;
        }
    }

    /* JADX INFO: renamed from: i */
    public AttachmentSlotDefinition findEnergyTransferRuleByName(String str) {
        for (AttachmentSlotDefinition attachmentSlotDefinition : this.energyCanTransferToOtherUnits) {
            if (attachmentSlotDefinition.b().equalsIgnoreCase(str)) {
                return attachmentSlotDefinition;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: j */
    public Resource findOrCreateCustomResource(String str) {
        Resource resourceA = Resource.a(str);
        if (resourceA != null) {
            return resourceA;
        }
        return findCustomResourceInList(str);
    }

    /* JADX INFO: renamed from: k */
    public Resource findCustomResourceInList(String str) {
        for (ResourceDefinition resourceDefinition : this.customResourcesList) {
            if (resourceDefinition.a.equalsIgnoreCase(str)) {
                return resourceDefinition.b;
            }
        }
        return null;
    }

    public ResourceDefinition a(Resource resource) {
        for (ResourceDefinition resourceDefinition : this.customResourcesList) {
            if (resourceDefinition.b == resource) {
                return resourceDefinition;
            }
        }
        return null;
    }

    public AnimationTrackingEntry a(AnimationTag animationTag) {
        for (AnimationTrackingEntry animationTrackingEntry : this.animationChannels) {
            if (animationTrackingEntry.g == animationTag) {
                return animationTrackingEntry;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: l */
    public AnimationTrackingEntry findAnimationChannelByTagName(String str) {
        for (AnimationTrackingEntry animationTrackingEntry : this.animationChannels) {
            if (animationTrackingEntry.g.tagName.equals(str)) {
                return animationTrackingEntry;
            }
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean y() {
        return this.hasCreditResourceCost;
    }

    /* JADX INFO: renamed from: t */
    public String getOwningModDisplayTitle() {
        if (this.modInfo != null) {
            return this.modInfo.getDisplayTitle();
        }
        return null;
    }

    public static void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.startBlock("customUnits");
        gameOutputStream.writeInt(1);
        gameOutputStream.writeInt(activeConfigs.size());
        for (CustomUnitConfig customUnitConfig : activeConfigs) {
            gameOutputStream.writeStringUTF(customUnitConfig.onNewMapSpawn);
            gameOutputStream.writeInt(customUnitConfig.generation_unit);
            gameOutputStream.writeBoolean(true);
            gameOutputStream.writeStringNullable(customUnitConfig.getOwningModDisplayTitle());
            long j = 0;
            if (customUnitConfig.modInfo != null && customUnitConfig.modInfo.steamId != 0) {
                j = customUnitConfig.modInfo.steamId;
            }
            gameOutputStream.writeLong(j);
            gameOutputStream.writeLong(0L);
        }
        gameOutputStream.endBlock("customUnits");
    }

    /* JADX INFO: renamed from: a */
    public static void validateUnitCompatibility(UnitCompatibilityReport report, HashMap<String,LanguageValue> map) throws ConfigValidationException {
        String str;
        ArrayList<LanguageValue> arrayList = new ArrayList();
        ArrayList<LanguageValue> arrayList2 = new ArrayList();
        if (!(report.modIdentifier == null)) {
            for (LanguageValue languageValue : map.values()) {
                if (languageValue.b == 0 && languageValue.d == 0 && languageValue.c > 0) {
                    arrayList.add(languageValue);
                } else if (languageValue.c > 0 || languageValue.d > 0) {
                    arrayList2.add(languageValue);
                }
            }
            String str2 = null;
            String str3 = VariableScope.nullOrMissingString;
            if (arrayList.size() > 0) {
                String str4 = VariableScope.nullOrMissingString;
                boolean z = true;
                for (LanguageValue languageValue2 : arrayList) {
                    if (z) {
                        z = false;
                    } else {
                        str4 = str4 + ", \n";
                    }
                    str4 = str4 + "'" + languageValue2.a + "'";
                }
                String strTruncateWithEllipsis = Utility.truncateWithEllipsis(str4, 200);
                if (arrayList.size() == 1) {
                    str2 = "Missing 1 mod.";
                    str = "Missing mod: '" + ((LanguageValue) arrayList.get(0)).a + "'";
                } else {
                    str2 = "Missing " + arrayList.size() + " mods";
                    str = "missing mods: " + strTruncateWithEllipsis;
                }
                str3 = str + "\n Required by this server.";
                if (arrayList2.size() > 0) {
                    str3 = str3 + "\n and " + arrayList2.size() + " mods are different.";
                }
            } else if (arrayList2.size() > 0) {
                String str5 = VariableScope.nullOrMissingString;
                boolean z2 = true;
                for (LanguageValue languageValue3 : arrayList2) {
                    if (z2) {
                        z2 = false;
                    } else {
                        str5 = str5 + ", \n";
                    }
                    str5 = str5 + "'" + languageValue3.a + "'";
                }
                str2 = "Different mod data.";
                str3 = "Different mod data for: " + Utility.truncateWithEllipsis(str5, 200) + " \n Check these mods are the same version as the server you are connecting to.";
            } else {
                GameEngine.isInSpace("Skipping nice message: completelyMissedMods:" + arrayList.size() + " differentMods:" + arrayList2.size());
            }
            if (str2 != null) {
                ConfigValidationException configValidationException = new ConfigValidationException(str3, VariableScope.nullOrMissingString);
                configValidationException.a = str2;
                throw configValidationException;
            }
        }
        String str6 = "from internal units";
        if (report.modIdentifier != null) {
            str6 = "from mod:'" + report.modIdentifier + "'";
        }
        ModInfo modByName = GameEngine.getInstance().modManager.getModByName(report.modIdentifier);
        if (modByName != null) {
            if (!modByName.isEnabled()) {
                str6 = str6 + " (You seem to have this mod but it is not enabled)";
            } else {
                str6 = str6 + " (You seem to have this mod but it might be a different version)";
            }
        }
        if (report.clientChecksum == -1) {
            throw new ConfigValidationException("The server requires the unit:" + report.unitName + " that was not found " + str6, VariableScope.nullOrMissingString);
        }
        throw new ConfigValidationException("Found unit:" + report.unitName + " but it does not match the server's copy " + str6, "checksum c:" + report.clientChecksum + " s:" + report.serverChecksum);
    }

    /* JADX INFO: renamed from: a */
    public static void loadAndValidateCustomUnits(GameInputStream inputStream) throws IOException, ConfigValidationException {
        inputStream.startBlockNamed("customUnits");
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList<UnitCompatibilityReport> arrayList2 = new ArrayList();
            HashMap<String,LanguageValue> map = new HashMap();
            boolean z = false;
            if (inputStream.readInt() >= 2) {
                z = inputStream.readBoolean();
                inputStream.readBoolean();
            }
            int i = inputStream.readInt();
            for (int i2 = 0; i2 < i; i2++) {
                String utf = inputStream.readUTF();
                int i3 = inputStream.readInt();
                inputStream.readBoolean();
                String nullableString = inputStream.readNullableString();
                inputStream.readLong();
                inputStream.readLong();
                String nullableString2 = null;
                if (z) {
                    nullableString2 = inputStream.readNullableString();
                }
                CustomUnitConfig customUnitConfig = null;
                int i4 = -1;
                CustomUnitConfig customUnitConfig2 = null;
                synchronized (allConfigs) {
                    for (CustomUnitConfig customUnitConfig3 : allConfigs) {
                        if (utf.equals(customUnitConfig3.onNewMapSpawn)) {
                            if (i3 == customUnitConfig3.generation_unit) {
                                customUnitConfig = customUnitConfig3;
                            } else {
                                customUnitConfig2 = customUnitConfig3;
                                i4 = customUnitConfig3.generation_unit;
                            }
                        }
                    }
                }
                LanguageValue languageValue = (LanguageValue) map.get(nullableString);
                if (languageValue == null) {
                    languageValue = new LanguageValue(nullableString);
                    map.put(nullableString, languageValue);
                }
                if (customUnitConfig == null) {
                    if (customUnitConfig2 != null) {
                        languageValue.d++;
                    } else {
                        languageValue.c++;
                    }
                    UnitCompatibilityReport unitCompatibilityReport = new UnitCompatibilityReport();
                    unitCompatibilityReport.modIdentifier = nullableString;
                    unitCompatibilityReport.unitName = utf;
                    unitCompatibilityReport.clientChecksum = i4;
                    unitCompatibilityReport.serverChecksum = i3;
                    unitCompatibilityReport.serverUnitConfig = customUnitConfig2;
                    unitCompatibilityReport.clientUnitConfig = nullableString2;
                    arrayList2.add(unitCompatibilityReport);
                    GameEngine.updatePaintTextSizeIfNeeded(unitCompatibilityReport.generateErrorMessage());
                } else {
                    languageValue.b++;
                    arrayList.add(customUnitConfig);
                }
            }
            if (arrayList2.size() > 0) {
                for (UnitCompatibilityReport unitCompatibilityReport2 : arrayList2) {
                    if (unitCompatibilityReport2.modIdentifier == null) {
                        validateUnitCompatibility(unitCompatibilityReport2, map);
                    }
                }
                validateUnitCompatibility((UnitCompatibilityReport) arrayList2.get(0), map);
            }
            validUnitsForSync = arrayList;
            inputStream.d("customUnits");
        } catch (Throwable th) {
            inputStream.d("customUnits");
            throw th;
        }
    }

    public void b(UnitType unitType) {
        if (!this.relatedUnits.contains(unitType) && unitType != this) {
            this.relatedUnits.add(unitType);
        }
        if (unitType instanceof CustomUnitConfig) {
            for (UnitType unitType2 : ((CustomUnitConfig) unitType).relatedUnits) {
                if (!this.relatedUnits.contains(unitType2) && unitType != this) {
                    this.relatedUnits.add(unitType2);
                }
            }
        }
    }

    public static CustomUnitConfig a(CustomUnitConfig customUnitConfig) {
        for (CustomUnitConfig customUnitConfig2 : activeConfigs) {
            if (customUnitConfig.configPath.equals(customUnitConfig2.configPath)) {
                return customUnitConfig2;
            }
        }
        for (CustomUnitConfig customUnitConfig3 : activeConfigs) {
            if (customUnitConfig.onNewMapSpawn.equals(customUnitConfig3.onNewMapSpawn)) {
                return customUnitConfig3;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: A */
    public static void load() {
        for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
            if (baseUnit instanceof CustomUnit) {
                CustomUnit customUnit = (CustomUnit) baseUnit;
                CustomUnitConfig customUnitConfig = customUnit.unitConfig;
                if (!activeConfigs.contains(customUnitConfig)) {
                    CustomUnitConfig customUnitConfigA = a(customUnitConfig);
                    if (customUnitConfigA == null) {
                        customUnitConfigA = instance;
                    }
                    if (customUnitConfigA != null) {
                        customUnit.a(customUnitConfigA, false, true);
                    }
                }
            }
        }
    }

    public Texture[] a(Texture texture, ColorMode colorMode) {
        boolean z = false;
        if ((this.modInfo != null || this.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicOrTags) && !GameEngine.getInstance().settingsEngine.disableModLazyLoad) {
            z = true;
        }
        if ((this.canBeBuiltByAndTagsAndLogic && this.canBeBuiltByAndTagsOrLogic) || this.canBeBuiltByOrTagsOrLogicOrTags) {
            z = true;
        }
        Texture[] textureArrCompareToNew = PlayerTeam.compareToNew(texture, colorMode, z);
        for (int i = 0; i < textureArrCompareToNew.length; i++) {
            if (z && this.modInfo != null && this.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicOrTags && i == 1) {
                textureArrCompareToNew[i].w();
            }
        }
        CustomUnitConfigParser.a_texture_array(textureArrCompareToNew);
        return textureArrCompareToNew;
    }

    public Texture a(IniFile iniFile, String str, String str2) {
        return a(iniFile, str, str2, this.turretMultiTargeting);
    }

    public Texture a(IniFile iniFile, String str, String str2, boolean z) {
        return a(this.generation_free_in_sandbox, iniFile.getString(str, str2, (String) null), z, str, str2);
    }

    public Texture a(String str, String str2, boolean z, String str3, String str4) {
        return CustomUnitConfigParser.cacheTexture(str, str2, z, this, str3, str4);
    }

    /* JADX INFO: renamed from: m */
    public static UnitType findUnitTypeByShortName(String str) {
        for (UnitType unitType : unitTypeOverrides.keySet()) {
            if (unitType.getUnitTypeDescriptionShort().equals(str)) {
                return (UnitType) unitTypeOverrides.get(unitType);
            }
        }
        return null;
    }

    public static UnitType c(UnitType unitType) {
        return (UnitType) unitTypeOverrides.get(unitType);
    }

    /* JADX INFO: renamed from: n */
    public static CustomUnitConfig findConfigByName(String str) {
        for (CustomUnitConfig customUnitConfig : activeConfigs) {
            if (str.equals(customUnitConfig.onNewMapSpawn)) {
                return customUnitConfig;
            }
        }
        for (CustomUnitConfig customUnitConfig2 : activeConfigs) {
            if (customUnitConfig2.autoTriggerAction.contains(str)) {
                return customUnitConfig2;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: E */
    public static String getAllUnitAndTriggerNames() {
        String str = VariableScope.nullOrMissingString;
        Iterator it = activeConfigs.iterator();
        while (it.hasNext()) {
            str = str + ((CustomUnitConfig) it.next()).onNewMapSpawn + ", ";
        }
        Iterator it2 = activeConfigs.iterator();
        while (it2.hasNext()) {
            Iterator it3 = ((CustomUnitConfig) it2.next()).autoTriggerAction.iterator();
            while (it3.hasNext()) {
                str = str + ((String) it3.next()) + ", ";
            }
        }
        return str;
    }

    public static CustomUnit a(boolean z, CustomUnitConfig customUnitConfig) {
        return new CustomUnit(z, customUnitConfig);
    }

    /* JADX INFO: renamed from: o */
    public void registerConfigWatcher(String str) {
        if (RwmodFileLoader.i(str)) {
            return;
        }
        this.fileWatchers.add(new FileWatcher(str));
    }

    /* JADX INFO: renamed from: p */
    public void throwConfigError(String str) {
        CustomUnitConfigParser.validateModFilePath(getUnitTypeDescriptionShort(), new ConfigParseException(str), this);
    }

    /* JADX INFO: renamed from: q */
    public void logWarning(String str) {
        String str2 = "Warning (on " + getConfigDisplayPath() + "): " + str;
        GameEngine.updatePaintTextSizeIfNeeded(str2);
        this.warnings.add(str);
        if (this.modInfo == null) {
            GameEngine.getInstance().alert(str2, 1);
            if (GameEngine.isNetworkServerStatic2) {
                GameEngine.isInSpace("Crashing on allowed unit warning because automated testing is active");
                throw new RuntimeException(str2);
            }
        }
    }

    /* JADX INFO: renamed from: r */
    public void logWarningToMod(String str) {
        String str2 = "Warning (on " + getConfigDisplayPath() + "): " + str;
        GameEngine.updatePaintTextSizeIfNeeded(str2);
        this.warnings.add(str);
        if (this.modInfo == null) {
            GameEngine.getInstance().alert(str2, 1);
            if (GameEngine.isNetworkServerStatic2) {
                GameEngine.isInSpace("Crashing on allowed unit warning because automated testing is active");
                throw new RuntimeException(str2);
            }
            return;
        }
        this.modInfo.addWarning(str2);
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public Texture z() {
        return this.energyNeedsToRechargeToFull;
    }

    /* JADX INFO: renamed from: F */
    public static void spawnOnNewMapAccordingToTeamColors() {
        GameEngine gameEngine = GameEngine.getInstance();
        for (CustomUnitConfig customUnitConfig : activeConfigs) {
            SpawnPointType spawnPointType = customUnitConfig.teamColors;
            if (spawnPointType != null) {
                if (spawnPointType == SpawnPointType.emptyResourcePools_asNeutral || spawnPointType == SpawnPointType.emptyOrOccupiedResourcePools_asNeutral) {
                    for (Point point : gameEngine.tileMap.unitObjects) {
                        BaseUnit baseUnitA = customUnitConfig.a();
                        baseUnitA.setUnitTeam(PlayerTeam.TEAM_ALL);
                        gameEngine.tileMap.setCursorTileIndexFromTileIndex(point.worldX, point.worldY);
                        baseUnitA.posX = gameEngine.tileMap.cursorTileX;
                        baseUnitA.posY = gameEngine.tileMap.cursorTileY;
                        baseUnitA.posX += baseUnitA.getUnitAIState();
                        baseUnitA.posY += baseUnitA.getUnitAIPathfindStatus();
                        if (spawnPointType == SpawnPointType.emptyResourcePools_asNeutral && (baseUnitA instanceof OrderableUnit) && ((OrderableUnit) baseUnitA).hasBlockingUnitNearby((BaseUnit) null, (PlayerTeam) null)) {
                            baseUnitA.getUnitAICondition();
                        } else {
                            PlayerTeam.c(baseUnitA);
                        }
                    }
                } else if (spawnPointType == SpawnPointType.mapCenter_asNeutral || spawnPointType == SpawnPointType.mapCenter_eachActiveTeam) {
                    if (spawnPointType == SpawnPointType.mapCenter_asNeutral) {
                        BaseUnit baseUnitA2 = customUnitConfig.a();
                        baseUnitA2.setUnitTeam(PlayerTeam.TEAM_ALL);
                        gameEngine.tileMap.exportTmxToFile(gameEngine.tileMap.getWorldWidth() / 2.0f, gameEngine.tileMap.getWorldHeight() / 2.0f);
                        baseUnitA2.posX = gameEngine.tileMap.cursorTileX;
                        baseUnitA2.posY = gameEngine.tileMap.cursorTileY;
                        baseUnitA2.posX += baseUnitA2.getUnitAIState();
                        baseUnitA2.posY += baseUnitA2.getUnitAIPathfindStatus();
                        PlayerTeam.c(baseUnitA2);
                    }
                    if (spawnPointType == SpawnPointType.mapCenter_eachActiveTeam) {
                        for (PlayerTeam playerTeam : PlayerTeam.addEnergy()) {
                            if (playerTeam.addUnitToTeam(true, false) > 0) {
                                BaseUnit baseUnitA3 = customUnitConfig.a();
                                baseUnitA3.setUnitTeam(playerTeam);
                                gameEngine.tileMap.exportTmxToFile(gameEngine.tileMap.getWorldWidth() / 2.0f, gameEngine.tileMap.getWorldHeight() / 2.0f);
                                baseUnitA3.posX = gameEngine.tileMap.cursorTileX;
                                baseUnitA3.posY = gameEngine.tileMap.cursorTileY;
                                baseUnitA3.posX += baseUnitA3.getUnitAIState();
                                baseUnitA3.posY += baseUnitA3.getUnitAIPathfindStatus();
                                PlayerTeam.c(baseUnitA3);
                            }
                        }
                    }
                } else if (spawnPointType == SpawnPointType.spawnPoint_eachActiveTeam) {
                    for (PlayerTeam playerTeam2 : PlayerTeam.addEnergy()) {
                        if (playerTeam2.addUnitToTeam(true, false) > 0) {
                            PointF pointF = new PointF();
                            GameViewUtils.a(playerTeam2, pointF);
                            BaseUnit baseUnitA4 = customUnitConfig.a();
                            baseUnitA4.setUnitTeam(playerTeam2);
                            gameEngine.tileMap.exportTmxToFile(pointF.x, pointF.y);
                            baseUnitA4.posX = gameEngine.tileMap.cursorTileX;
                            baseUnitA4.posY = gameEngine.tileMap.cursorTileY;
                            baseUnitA4.posX += baseUnitA4.getUnitAIState();
                            baseUnitA4.posY += baseUnitA4.getUnitAIPathfindStatus();
                            PlayerTeam.c(baseUnitA4);
                        }
                    }
                } else {
                    GameEngine.updatePaintTextSizeIfNeeded("onNewMapSpawn unhandled: " + customUnitConfig.teamColors);
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public int a(BaseUnit baseUnit) {
        int i = 0;
        if (this.transportUnitsUnloadLeft > 0.0f) {
            return (int) this.transportUnitsUnloadLeft;
        }
        if (this.transportUnitsCanLoadUnitWithTags && this.isBuildingUnit && baseUnit.y() < 20) {
            i = 0 + 17;
        }
        return i;
    }

    public void a(String str, int i, String str2, String str3) throws ConfigParseException {
        if (this.modInfo == null || this.modInfo.customUnitCount >= i || !this.modInfo.dataRefreshed) {
            return;
        }
        if (this.modInfo.minVersion == null) {
            throw new ConfigParseException("[" + str2 + "] minVersion of " + str + " is required to be set in mod-info.txt at the root of this mod to use " + str3);
        }
        try {
            ModManager.checkVersion(str, this.modInfo.minVersion);
            this.modInfo.customUnitCount = i;
        } catch (ConfigParseException e) {
            throw new ConfigParseException("[" + str2 + "]" + str3 + " " + e.getMessage() + " to be set as minVersion in mod-info.txt");
        }
    }

    /* JADX INFO: renamed from: s */
    public static UnitType getUnitTypeByName(String str) {
        return a(str, true);
    }

    public static UnitType a(String str, boolean z) {
        UnitType unitTypeByNameWithBoolean = UnitTypeEnum.getUnitTypeByNameWithBoolean(str, z);
        if (unitTypeByNameWithBoolean == UnitTypeEnum.editorOrBuilder) {
            return UnitTypeEnum.builder;
        }
        return unitTypeByNameWithBoolean;
    }
}
