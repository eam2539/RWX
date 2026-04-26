package com.corrodinggames.rts.game.units;

import android.graphics.*;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.air.AirUnit;
import com.corrodinggames.rts.game.units.buildings.BaseBuilding;
import com.corrodinggames.rts.game.units.buildings.LaserDefense;
import com.corrodinggames.rts.game.units.buildings.RepairBay;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.UnitEventType;
import com.corrodinggames.rts.game.units.custom.hooks.AttachmentSlotDefinition;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.game.units.custom.resources.Resource;
import com.corrodinggames.rts.game.units.custom.resources.StoredResources;
import com.corrodinggames.rts.game.units.custom.tracking.AnimationTrackingManager;
import com.corrodinggames.rts.game.units.land.BuilderUnit;
import com.corrodinggames.rts.game.units.land.HoverLandUnit;
import com.corrodinggames.rts.game.units.land.LandUnit;
import com.corrodinggames.rts.game.units.sea.WaterUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.SizedObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.effects.*;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.*;

import java.io.IOException;
import java.util.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.am */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/am.class */
public abstract class BaseUnit extends SizedObject {
    public float br;
    public int bs;

    /* JADX INFO: renamed from: bt */
    public BaseUnit unitTarget1;

    /* JADX INFO: renamed from: bu */
    public BaseUnit unitTarget2;

    /* JADX INFO: renamed from: bv */
    public BaseUnit unitTarget3;

    /* JADX INFO: renamed from: bw */
    public VariableScope unitVariables;

    /* JADX INFO: renamed from: bx */
    public UnitPrice unitData1;

    /* JADX INFO: renamed from: by */
    public UnitPrice unitData2;

    /* JADX INFO: renamed from: bz */
    public int unitFlags1;

    /* JADX INFO: renamed from: bA */
    public int unitFlags2;

    /* JADX INFO: renamed from: bB */
    public int unitFlags3;

    /* JADX INFO: renamed from: bC */
    public int unitFlags4;

    /* JADX INFO: renamed from: bD */
    public boolean isVisible;
    public static final UnitList bE = new UnitList();
    private static final TransactionalArrayList a = new TransactionalArrayList();
    public static HashMap bF = new HashMap();
    public static HashMap bG = new HashMap();
    public static HashMap bH = new HashMap();
    public static final Paint bI = new GamePaint();
    public static final Paint bJ;
    static final LightingColorFilter bK;

    /* JADX INFO: renamed from: bL */
    public boolean isHighlighted;

    /* JADX INFO: renamed from: bM */
    public boolean isActive;

    /* JADX INFO: renamed from: bN */
    public boolean isAIUnit;

    /* JADX INFO: renamed from: bO */
    public boolean isSelectable;

    /* JADX INFO: renamed from: bP */
    public boolean isTargetable;

    /* JADX INFO: renamed from: bQ */
    public BaseUnit targetUnit;

    /* JADX INFO: renamed from: bR */
    public BaseUnit attackTargetUnit;
    public float bS;

    /* JADX INFO: renamed from: bT */
    public boolean isAttacking;

    /* JADX INFO: renamed from: bU */
    public int attackCooldown;

    /* JADX INFO: renamed from: bV */
    public boolean isDestroyed;

    /* JADX INFO: renamed from: bW */
    public long unitCreationTime;

    /* JADX INFO: renamed from: bX */
    public PlayerTeam team;

    /* JADX INFO: renamed from: bY */
    public boolean isHidden;

    /* JADX INFO: renamed from: bZ */
    public float worldX;

    /* JADX INFO: renamed from: ca */
    public float worldY;

    /* JADX INFO: renamed from: cb */
    public boolean isMoving;

    /* JADX INFO: renamed from: cc */
    public float velocityX;

    /* JADX INFO: renamed from: cd */
    public float velocityY;

    /* JADX INFO: renamed from: ce */
    public float direction;

    /* JADX INFO: renamed from: cf */
    public float rotation;

    /* JADX INFO: renamed from: cg */
    public float rotationSpeed;

    /* JADX INFO: renamed from: ch */
    public float targetRotation;

    /* JADX INFO: renamed from: ci */
    public boolean isRotating;

    /* JADX INFO: renamed from: cj */
    public float speed;

    /* JADX INFO: renamed from: ck */
    public float maxSpeed;

    /* JADX INFO: renamed from: cl */
    public float spawnExitLockTimer;

    /* JADX INFO: renamed from: cm */
    public float deceleration;

    /* JADX INFO: renamed from: cn */
    public float movementAngle;

    /* JADX INFO: renamed from: co */
    public boolean isUnitStunned;

    /* JADX INFO: renamed from: cp */
    public boolean isUnitParalyzed;

    /* JADX INFO: renamed from: cq */
    public boolean isUnitInvulnerable;

    /* JADX INFO: renamed from: cr */
    public boolean isUnitUntargetable;

    /* JADX INFO: renamed from: cs */
    public boolean isUnitDisabled;

    /* JADX INFO: renamed from: ct */
    public boolean isUnitCapturable;

    /* JADX INFO: renamed from: cu */
    public float currentHealth;

    /* JADX INFO: renamed from: cv */
    public float maxHealth;

    /* JADX INFO: renamed from: cw */
    public float unitArmor;

    /* JADX INFO: renamed from: cx */
    public float shield;

    /* JADX INFO: renamed from: cy */
    public float unitShieldMax;

    /* JADX INFO: renamed from: cz */
    public float energy;

    /* JADX INFO: renamed from: cA */
    public float unitEnergyMax;

    /* JADX INFO: renamed from: cB */
    public float f0cB;
    public float cC;

    /* JADX INFO: renamed from: cD */
    public float unitDefenseMultiplier;

    /* JADX INFO: renamed from: cE */
    public int unitLevel;

    /* JADX INFO: renamed from: cF */
    public int unitExperience;

    /* JADX INFO: renamed from: cG */
    public boolean isSelected;

    /* JADX INFO: renamed from: cH */
    public int lastSelectedTick;

    /* JADX INFO: renamed from: cI */
    public boolean isUnitBuilt;

    /* JADX INFO: renamed from: cJ */
    public float unitBuildProgress;

    /* JADX INFO: renamed from: cK */
    public boolean isRepairing;

    /* JADX INFO: renamed from: cL */
    public UnitMovementData[] movementLevels;

    /* JADX INFO: renamed from: cM */
    public boolean isUnitTransporting;

    /* JADX INFO: renamed from: cN */
    public BaseUnit unitTransportTarget;

    /* JADX INFO: renamed from: cO */
    public OrderableUnit parentEntity;

    /* JADX INFO: renamed from: cP */
    public AttachmentSlotDefinition attachmentData;

    /* JADX INFO: renamed from: cQ */
    public int unitTransportCapacity;

    /* JADX INFO: renamed from: cR */
    public boolean isUnitFull;

    /* JADX INFO: renamed from: cS */
    public int unitCargoCount;

    /* JADX INFO: renamed from: cT */
    public int unitCargoMax;

    /* JADX INFO: renamed from: cU */
    public int unitCargoType;

    /* JADX INFO: renamed from: cV */
    public float unitCargoMass;
    public static final Paint cW;
    public static final Paint cX;
    public static final Paint cY;
    public static final Paint cZ;
    public static final Paint da;
    public static final Paint db;
    public static final Paint dc;
    public static final Paint dd;
    public static final Paint de;
    public static final Paint df;
    public static final Paint dg;
    public static final Paint dh;
    public static final Paint di;
    public static final Paint dj;
    public static final Paint dk;

    /* JADX INFO: renamed from: dl */
    public int unitAnimationFrame;

    /* JADX INFO: renamed from: dm */
    public int unitAnimationSpeed;

    /* JADX INFO: renamed from: dn */
    public int unitAnimationType;

    /* JADX INFO: renamed from: do */
    public float unitAnimationOffset;

    /* JADX INFO: renamed from: dp */
    public float unitAnimationScale;

    /* JADX INFO: renamed from: dq */
    public float unitAnimationRotation;
    static final RectF dr;
    static Paint ds;
    static Paint dt;
    public static final RectF du;
    public static final Rect dv;
    static final Rect dw;
    static final ArrayList dx;
    static ArrayList dy;

    /* JADX INFO: renamed from: dz */
    public UnitType unitType;
    static final RectF dA;
    static final RectF dB;
    static final Rect dC;
    static final PointF dD;
    static final PointF dE;

    /* JADX INFO: renamed from: dF */
    UnitEffectData[] unitEffects;
    static final PointF dG;

    /* JADX INFO: renamed from: dH */
    StoredResources unitCustomEffects;

    /* JADX INFO: renamed from: dI */
    public AnimationTrackingManager unitCustomComponents;

    /* JADX INFO: renamed from: dJ */
    UnitPrice unitCustomData;

    public abstract UnitMovementType h();

    public abstract boolean i();

    public abstract boolean Q();

    /* JADX INFO: renamed from: aj */
    public abstract boolean canUnitAttack();

    /* JADX INFO: renamed from: ak */
    public abstract boolean canMove();

    /* JADX INFO: renamed from: s_ */
    public abstract boolean isBuilding();

    public abstract UnitType r();

    /* JADX INFO: renamed from: l */
    public abstract boolean canAttack();

    @Override // com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.isActive);
        gameOutputStream.writeUnitIdOrNullBaseUnit(this.targetUnit);
        gameOutputStream.writeUnitIdOrNullBaseUnit(this.attackTargetUnit);
        gameOutputStream.writeFloat(this.bS);
        gameOutputStream.writeBoolean(this.isAttacking);
        gameOutputStream.writeBoolean(this.isDestroyed);
        gameOutputStream.writeLong(this.unitCreationTime);
        gameOutputStream.writeTeamIdByte(this.team);
        gameOutputStream.writeFloat(this.worldX);
        gameOutputStream.writeFloat(this.worldY);
        gameOutputStream.writeFloat(this.velocityX);
        gameOutputStream.writeFloat(this.velocityY);
        gameOutputStream.writeFloat(this.rotation);
        gameOutputStream.writeFloat(this.rotationSpeed);
        gameOutputStream.writeFloat(this.speed);
        gameOutputStream.writeFloat(this.maxSpeed);
        gameOutputStream.writeFloat(this.spawnExitLockTimer);
        gameOutputStream.writeFloat(this.deceleration);
        gameOutputStream.writeBoolean(this.isUnitParalyzed);
        gameOutputStream.writeBoolean(this.isUnitDisabled);
        gameOutputStream.writeFloat(this.currentHealth);
        gameOutputStream.writeFloat(this.maxHealth);
        gameOutputStream.writeBoolean(this.isRepairing);
        gameOutputStream.writeFloat(this.movementLevels[0].targetX);
        gameOutputStream.writeFloat(this.movementLevels[0].velocityY);
        gameOutputStream.writeUnitIdOrNullBaseUnit(this.unitTransportTarget);
        gameOutputStream.writeByte(26);
        gameOutputStream.writeInt(this.unitCargoType);
        gameOutputStream.writeFloat(this.unitCargoMass);
        gameOutputStream.writeFloat(this.direction);
        gameOutputStream.writeFloat(this.targetRotation);
        int techLevel = getTechLevel();
        gameOutputStream.writeInt(techLevel);
        for (int i = 0; i < techLevel; i++) {
            UnitMovementData unitMovementData = this.movementLevels[i];
            gameOutputStream.writeFloat(unitMovementData.targetX);
            gameOutputStream.writeFloat(unitMovementData.velocityX);
            gameOutputStream.writeFloat(unitMovementData.velocityY);
            gameOutputStream.writeFloat(unitMovementData.rotation);
            gameOutputStream.writeFloat(unitMovementData.speed);
            gameOutputStream.writeFloat(unitMovementData.h);
            gameOutputStream.writeFloat(unitMovementData.i);
            BaseUnit baseUnit = unitMovementData.targetUnit;
            if (baseUnit != null && baseUnit.isDestroyed) {
                baseUnit = null;
            }
            gameOutputStream.writeUnitIdOrNullBaseUnit(baseUnit);
            gameOutputStream.writeBoolean(this.isUnitTransporting);
        }
        gameOutputStream.writeInt(this.bs);
        gameOutputStream.writeFloat(this.shield);
        gameOutputStream.writeFloat(this.unitShieldMax);
        gameOutputStream.writeFloat(this.energy);
        gameOutputStream.writeFloat(this.unitEnergyMax);
        gameOutputStream.writeBoolean(this.isUnitInvulnerable);
        gameOutputStream.writeBoolean(this.isUnitUntargetable);
        gameOutputStream.writeBoolean(this.isUnitCapturable);
        gameOutputStream.writeBoolean(this.isAIUnit);
        gameOutputStream.writeFloat(this.f0cB);
        gameOutputStream.writeBoolean(this.isRotating);
        gameOutputStream.writeBoolean(this.unitEffects != null);
        if (this.unitEffects != null) {
            gameOutputStream.writeInt(this.unitEffects.length);
            for (int i2 = 0; i2 < this.unitEffects.length; i2++) {
                UnitEffectData unitEffectData = this.unitEffects[i2];
                gameOutputStream.writeBoolean(unitEffectData.a);
                gameOutputStream.writeInt(unitEffectData.b);
            }
        }
        gameOutputStream.writeFloat(this.unitArmor);
        gameOutputStream.writeUnitIdIfAlive(this.unitTarget1);
        gameOutputStream.writeInt(this.unitLevel);
        gameOutputStream.writeInt(this.unitExperience);
        gameOutputStream.writeInt(this.unitFlags1);
        gameOutputStream.writeInt(this.unitFlags2);
        gameOutputStream.writeInt(this.unitFlags3);
        gameOutputStream.writeInt(this.unitFlags4);
        gameOutputStream.writeBoolean(this.isSelectable);
        gameOutputStream.writeBoolean(this.isTargetable);
        this.unitCustomEffects.a(gameOutputStream);
        this.unitCustomComponents.a(gameOutputStream);
        gameOutputStream.writeUnitIdIfAlive((BaseUnit) this.parentEntity);
        short sA = -1;
        if (this.parentEntity != null && this.attachmentData != null) {
            sA = this.attachmentData.a();
        }
        gameOutputStream.writeShort(sA);
        gameOutputStream.writeInt(this.unitTransportCapacity);
        VariableScope.writeOutUnitOrPlaceholder(gameOutputStream, this.unitTarget2);
        VariableScope.writeOutUnitOrPlaceholder(gameOutputStream, this.unitTarget3);
        VariableScope.writeOut(gameOutputStream, this.unitVariables);
        UnitPrice.a(gameOutputStream, this.unitData1);
        UnitPrice.a(gameOutputStream, this.unitData2);
        gameOutputStream.writeFloat(this.movementAngle);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        AttachmentSlotDefinition attachmentSlotDefinitionA;
        this.isActive = gameInputStream.readBoolean();
        this.targetUnit = gameInputStream.readBaseUnit();
        this.attackTargetUnit = gameInputStream.readBaseUnit();
        this.bS = gameInputStream.readFloat();
        this.isAttacking = gameInputStream.readBoolean();
        this.isDestroyed = gameInputStream.readBoolean();
        this.unitCreationTime = gameInputStream.readLong();
        setUnitTeam(gameInputStream.readRequiredPlayerTeam());
        this.worldX = gameInputStream.readFloat();
        this.worldY = gameInputStream.readFloat();
        this.velocityX = gameInputStream.readFloat();
        this.velocityY = gameInputStream.readFloat();
        this.rotation = gameInputStream.readFloat();
        this.rotationSpeed = gameInputStream.readFloat();
        gameInputStream.readFloat();
        gameInputStream.readFloat();
        this.spawnExitLockTimer = gameInputStream.readFloat();
        this.deceleration = gameInputStream.readFloat();
        this.isUnitParalyzed = gameInputStream.readBoolean();
        this.isUnitDisabled = gameInputStream.readBoolean();
        o(gameInputStream.readFloat());
        this.maxHealth = gameInputStream.readFloat();
        this.isRepairing = gameInputStream.readBoolean();
        this.movementLevels[0].targetX = gameInputStream.readFloat();
        this.movementLevels[0].velocityY = gameInputStream.readFloat();
        this.unitTransportTarget = gameInputStream.readBaseUnit();
        byte b = gameInputStream.readByte();
        if (b >= 1) {
            this.unitCargoType = gameInputStream.readInt();
            this.unitCargoMass = gameInputStream.readFloat();
        }
        if (b >= 2) {
            this.direction = gameInputStream.readFloat();
            this.targetRotation = gameInputStream.readFloat();
            int i = gameInputStream.readInt();
            setUnitLevel(i);
            for (int i2 = 0; i2 < i; i2++) {
                UnitMovementData unitMovementData = this.movementLevels[i2];
                unitMovementData.targetX = gameInputStream.readFloat();
                unitMovementData.velocityX = gameInputStream.readFloat();
                unitMovementData.velocityY = gameInputStream.readFloat();
                unitMovementData.rotation = gameInputStream.readFloat();
                unitMovementData.speed = gameInputStream.readFloat();
                if (b >= 8) {
                    unitMovementData.h = gameInputStream.readFloat();
                    unitMovementData.i = gameInputStream.readFloat();
                    unitMovementData.targetUnit = gameInputStream.readBaseUnit();
                }
                if (b >= 12) {
                    this.isUnitTransporting = gameInputStream.readBoolean();
                }
            }
        }
        if (b >= 3) {
            this.bs = gameInputStream.readInt();
        }
        if (b >= 4) {
            this.shield = gameInputStream.readFloat();
            this.unitShieldMax = gameInputStream.readFloat();
            this.energy = gameInputStream.readFloat();
            this.unitEnergyMax = gameInputStream.readFloat();
        }
        if (b >= 5) {
            this.isUnitInvulnerable = gameInputStream.readBoolean();
            this.isUnitUntargetable = gameInputStream.readBoolean();
        }
        if (b >= 6) {
            this.isUnitCapturable = gameInputStream.readBoolean();
        }
        if (b >= 7) {
            this.isAIUnit = gameInputStream.readBoolean();
        }
        if (b >= 9) {
            this.f0cB = gameInputStream.readFloat();
        }
        if (b >= 10) {
            this.isRotating = gameInputStream.readBoolean();
        }
        if (b >= 11 && gameInputStream.readBoolean()) {
            this.unitEffects = new UnitEffectData[gameInputStream.readInt()];
            for (int i3 = 0; i3 < this.unitEffects.length; i3++) {
                this.unitEffects[i3] = new UnitEffectData();
                UnitEffectData unitEffectData = this.unitEffects[i3];
                unitEffectData.a = gameInputStream.readBoolean();
                unitEffectData.b = gameInputStream.readInt();
            }
        }
        if (b >= 13) {
            this.unitArmor = gameInputStream.readFloat();
        }
        if (b >= 14) {
            this.unitTarget1 = gameInputStream.readBaseUnit();
        }
        if (b >= 15) {
            this.unitLevel = gameInputStream.readInt();
            this.unitExperience = gameInputStream.readInt();
        }
        if (b >= 16) {
            this.unitFlags1 = gameInputStream.readInt();
            this.unitFlags2 = gameInputStream.readInt();
            this.unitFlags3 = gameInputStream.readInt();
        }
        if (b >= 17) {
            this.unitFlags4 = gameInputStream.readInt();
        }
        if (b >= 18) {
            this.isSelectable = gameInputStream.readBoolean();
            this.isTargetable = gameInputStream.readBoolean();
        }
        if (b >= 19) {
            this.unitCustomEffects.a(gameInputStream);
            this.unitCustomComponents.a(this, gameInputStream);
        }
        if (b >= 20) {
            OrderableUnit unitEntity = gameInputStream.readUnitEntity();
            short shortValue = gameInputStream.readShortValue();
            if (shortValue != -1) {
                boolean z = false;
                if (unitEntity != null && (this instanceof OrderableUnit) && (attachmentSlotDefinitionA = unitEntity.a(shortValue)) != null && unitEntity.a((OrderableUnit) this, attachmentSlotDefinitionA)) {
                    z = true;
                }
                if (!z) {
                    getUnitAIConditionTime();
                }
            }
        }
        if (b >= 21) {
            this.unitTransportCapacity = gameInputStream.readInt();
        }
        if (b >= 22) {
            if (b < 24) {
                throw new IOException("extension >=22 but <24");
            }
            this.unitTarget2 = VariableScope.readInUnitOrPlaceholder(gameInputStream);
            this.unitTarget3 = VariableScope.readInUnitOrPlaceholder(gameInputStream);
        }
        if (b >= 23) {
            this.unitVariables = VariableScope.readIn(gameInputStream);
        }
        if (b >= 25) {
            this.unitData1 = UnitPrice.a(gameInputStream);
            this.unitData2 = UnitPrice.a(gameInputStream);
        }
        if (b >= 26) {
            this.movementAngle = gameInputStream.readFloat();
        }
        if (this.isDestroyed) {
            GameEngine gameEngine = GameEngine.getInstance();
            bE.remove(this);
            gameEngine.unitSpatialIndex.a(this);
        }
        super.a(gameInputStream);
    }

    /* JADX INFO: renamed from: a */
    public static Texture getUnitTextureSize(Texture texture) {
        return attackUnit(texture, texture.m(), texture.l());
    }

    /* JADX INFO: renamed from: a */
    public static Texture attackUnit(Texture texture, int i, int i2) {
        Texture textureA = texture.a(i, i2, false);
        texture.x();
        textureA.j();
        int iM = textureA.m();
        int iL = textureA.l();
        for (int i3 = 0; i3 < iM; i3++) {
            for (int i4 = 0; i4 < iL; i4++) {
                textureA.a(i3, i4, Color.a(Color.a(texture.a(i3, i4)), 0, 0, 0));
            }
        }
        textureA.p();
        textureA.s();
        texture.y();
        textureA.a("shadow:" + texture.a());
        textureA.n = true;
        return textureA;
    }

    static {
        bI.a(true);
        bI.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_BUTTON_8, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_BUTTON_8, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_BUTTON_8);
        bJ = new GamePaint();
        bJ.a(true);
        bK = new LightingColorFilter(Color.a(255, 255, 255), Color.a(100, 100, 100));
        bJ.a(255, 255, 255, 255);
        bJ.a(bK);
        cW = new Paint();
        cX = new GamePaint();
        cY = new GamePaint();
        cZ = new GamePaint();
        da = new GamePaint();
        db = new GamePaint();
        dc = new GamePaint();
        dd = new Paint();
        de = new Paint();
        df = new Paint();
        dg = new GamePaint();
        dh = new GamePaint();
        di = new GamePaint();
        dj = new GamePaint();
        dk = new Paint();
        cW.a(Paint.Style.STROKE);
        cW.a(2.0f);
        setPosition(cW);
        cX.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0, 255, 0);
        cX.a(Paint.Style.STROKE);
        cX.a(2.0f);
        takeDamage(cX, true);
        cY.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0, 255, 0);
        cY.a(Paint.Style.STROKE);
        cY.a(2.0f);
        setPosition(cY);
        cZ.a(130, 0, 255, 0);
        cZ.a(Paint.Style.STROKE);
        cZ.a(2.0f);
        setPosition(cZ);
        dd.a(70, 0, 255, 0);
        dd.a(Paint.Style.STROKE);
        dd.a(1.0f);
        setPosition(dd);
        da.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 255, 0, 0);
        da.a(Paint.Style.STROKE);
        da.a(2.0f);
        setPosition(da);
        de.a(70, 255, 0, 0);
        de.a(Paint.Style.STROKE);
        de.a(1.0f);
        setPosition(de);
        dc.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 255, 255, 0);
        dc.a(Paint.Style.STROKE);
        dc.a(2.0f);
        setPosition(dc);
        df.a(70, 255, 255, 0);
        df.a(Paint.Style.STROKE);
        df.a(1.0f);
        setPosition(df);
        db.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 255, 255, 255);
        db.a(Paint.Style.STROKE);
        db.a(2.0f);
        setPosition(db);
        dg.a(90, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG);
        dg.a(Paint.Style.STROKE);
        dg.a(1.0f);
        setPosition(dg);
        dh.a(100, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG);
        dh.a(Paint.Style.STROKE);
        dh.a(2.0f);
        setPosition(dh);
        di.a(90, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, 0, 0);
        di.a(Paint.Style.STROKE);
        di.a(1.0f);
        setPosition(di);
        dj.a(Paint.Style.STROKE);
        dk.a(Paint.Style.STROKE);
        dr = new RectF();
        ds = new Paint();
        dt = new Paint();
        du = new RectF();
        dv = new Rect();
        dw = new Rect();
        dx = new ArrayList();
        dy = new ArrayList();
        dA = new RectF();
        dB = new RectF();
        dC = new Rect();
        dD = new PointF();
        dE = new PointF();
        dG = new PointF();
    }

    /* JADX INFO: renamed from: bF */
    public static TransactionalArrayList<BaseUnit> getGlobalUnitList() {
        a.a();
        return a;
    }

    public static void bG() {
        a.a();
    }

    /* JADX INFO: renamed from: bH */
    public static void loadAllUnits() {
        LandUnit.loadTextures();
        BaseBuilding.dt();
        HoverLandUnit.K();
        WaterUnit.loadTextures();
        AirUnit.loadAirUnitTextures();
        if (GameEngine.getInstance().isPaused()) {
            Iterator it = EnumSet.allOf(UnitTypeEnum.class).iterator();
            while (it.hasNext()) {
                ((UnitTypeEnum) it.next()).abstractMethodB();
            }
        } else {
            BuilderUnit.loadTextures();
            LaserDefense.initializeTextures();
            RepairBay.initializeTextures();
            Tree.b();
        }
        UnitTypeEnum.loadUnitTypeSounds();
    }

    public boolean bI() {
        return false;
    }

    public boolean bJ() {
        return false;
    }

    public static HashMap bK() {
        HashMap map = new HashMap();
        if (GameEngine.getInstance().isPaused()) {
            for (UnitTypeEnum unitTypeEnum : EnumSet.allOf(UnitTypeEnum.class)) {
                BaseUnit baseUnitCreateUnitInstanceWithBoolean = unitTypeEnum.createUnitInstanceWithBoolean(true);
                baseUnitCreateUnitInstanceWithBoolean.remove();
                baseUnitCreateUnitInstanceWithBoolean.setUnitTeam(PlayerTeam.TEAM_ALL);
                baseUnitCreateUnitInstanceWithBoolean.isUnitParalyzed = true;
                map.put(unitTypeEnum, baseUnitCreateUnitInstanceWithBoolean);
            }
        }
        for (CustomUnitConfig customUnitConfig : CustomUnitConfig.activeConfigs) {
            BaseUnit baseUnitCreateCustomUnit = customUnitConfig.createCustomUnit(true);
            baseUnitCreateCustomUnit.remove();
            baseUnitCreateCustomUnit.setUnitTeam(PlayerTeam.TEAM_ALL);
            baseUnitCreateCustomUnit.isUnitParalyzed = true;
            map.put(customUnitConfig, baseUnitCreateCustomUnit);
        }
        return map;
    }

    /* JADX INFO: renamed from: bL */
    public static void getAttachmentCount() {
        bG = bK();
        bH = bK();
        bF = bK();
    }

    /* JADX INFO: renamed from: a */
    public static BaseUnit findTurretPosition(UnitType unitType) {
        return (BaseUnit) bF.get(unitType);
    }

    /* JADX INFO: renamed from: b */
    public static BaseUnit findAttackDamageSource(UnitType unitType) {
        return canAttack(unitType);
    }

    /* JADX INFO: renamed from: c */
    public static BaseUnit canAttack(UnitType unitType) {
        BaseUnit baseUnit = (BaseUnit) bG.get(unitType);
        if (baseUnit == null) {
            if (CustomUnitConfig.instance == null) {
                GameEngine.isInSpace("Could not find:" + unitType.getUnitTypeDescriptionShort() + " and missing place holder is null");
                return null;
            }
            baseUnit = (BaseUnit) bG.get(CustomUnitConfig.instance);
            if (baseUnit == null) {
                GameEngine.isInSpace("name: " + CustomUnitConfig.instance.name);
                GameEngine.isInSpace("contains:" + bG.containsKey(CustomUnitConfig.instance));
                Iterator it = bG.keySet().iterator();
                while (it.hasNext()) {
                    GameEngine.isInSpace("has:" + ((UnitType) it.next()).getUnitTypeDescriptionShort());
                }
                GameEngine.isInSpace("Could not find:" + unitType.getUnitTypeDescriptionShort() + " and missing place holder could not be found");
            }
        }
        return baseUnit;
    }

    /* JADX INFO: renamed from: d */
    public static BaseUnit findCollisionSize(UnitType unitType) {
        BaseUnit baseUnit = (BaseUnit) bH.get(unitType);
        if (baseUnit == null) {
            baseUnit = (BaseUnit) bH.get(CustomUnitConfig.instance);
        }
        return baseUnit;
    }

    public static int bM() {
        int iBw = 0;
        if (GameEngine.getInstance().isPaused()) {
            Iterator it = EnumSet.allOf(UnitTypeEnum.class).iterator();
            while (it.hasNext()) {
                iBw = (iBw * 31) + findTurretPosition((UnitTypeEnum) it.next()).bw();
            }
        }
        return iBw;
    }

    protected BaseUnit(boolean z) {
        super(z);
        this.bs = -9999;
        this.unitTarget1 = null;
        this.unitTarget2 = null;
        this.unitTarget3 = null;
        this.unitFlags1 = -9999;
        this.unitFlags2 = -9999;
        this.unitFlags3 = -9999;
        this.isActive = false;
        this.isAIUnit = false;
        this.isSelectable = false;
        this.isTargetable = false;
        this.targetUnit = null;
        this.isAttacking = true;
        this.attackCooldown = 1;
        this.isDestroyed = false;
        this.unitCreationTime = 0L;
        this.worldX = 0.0f;
        this.worldY = 0.0f;
        this.isMoving = false;
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.direction = 0.0f;
        this.rotation = 0.0f;
        this.deceleration = 1.0f;
        this.movementAngle = 1.0f;
        this.isUnitStunned = false;
        this.isUnitParalyzed = false;
        this.isUnitInvulnerable = false;
        this.isUnitUntargetable = false;
        this.isUnitDisabled = false;
        this.isUnitCapturable = false;
        this.lastSelectedTick = -9999;
        this.unitBuildProgress = 0.0f;
        this.isRepairing = true;
        this.unitTransportTarget = null;
        this.parentEntity = null;
        this.attachmentData = null;
        this.unitTransportCapacity = -9999;
        this.unitAnimationFrame = -1;
        this.unitAnimationSpeed = -1;
        this.unitAnimationType = -99;
        this.unitAnimationRotation = 70.0f;
        this.unitCustomEffects = new StoredResources();
        this.unitCustomComponents = new AnimationTrackingManager();
        this.unitCustomData = null;
        bS();
        if (!z) {
            this.isHighlighted = true;
            bE.add(this);
            a.a(this);
        }
        this.unitFlags1 = GameEngine.getInstance().lastTick;
        this.unitType = r();
    }

    @Override // com.corrodinggames.rts.gameFramework.SizedObject, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void remove() {
        PlayerTeam.a(this);
        if (this.isHighlighted) {
            bE.remove(this);
            a.b(this);
        }
        GameEngine.getInstance().gameUI.deselectUnit(this);
        super.remove();
    }

    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 3000.0f;
    }

    /* JADX INFO: renamed from: s */
    public int getSoftCollisionDivisor(BaseUnit baseUnit) {
        return 0;
    }

    public boolean bO() {
        return false;
    }

    public boolean bP() {
        return false;
    }

    public float bQ() {
        return -1.0f;
    }

    final void bR() {
        int i = 1;
        if (i()) {
            i = 3;
        } else if (Q()) {
            i = 2;
        }
        if (this.unitTransportTarget != null) {
            i = -1;
        }
        if (!this.isAttacking) {
            i = -1;
        }
        this.attackCooldown = i;
    }

    public void o(float f) {
        this.currentHealth = f;
    }

    public void bS() {
        setUnitLevel(1);
    }

    /* JADX INFO: renamed from: O */
    public void setUnitLevel(int i) {
        int length;
        int techLevel = getTechLevel();
        if (techLevel < i) {
            techLevel = i;
        }
        if (this.movementLevels == null) {
            length = 0;
            this.movementLevels = new UnitMovementData[techLevel];
        } else if (this.movementLevels.length < techLevel) {
            length = this.movementLevels.length;
            this.movementLevels = (UnitMovementData[]) Arrays.copyOf(this.movementLevels, techLevel);
        } else {
            return;
        }
        for (int i2 = length; i2 < this.movementLevels.length; i2++) {
            this.movementLevels[i2] = new UnitMovementData();
        }
    }

    /* JADX INFO: renamed from: a */
    public static void setPosition(Paint paint) {
        takeDamage(paint, false);
    }

    /* JADX INFO: renamed from: a */
    public static void takeDamage(Paint paint, boolean z) {
        if (!GameEngine.isPC() && z) {
            paint.a(0.0f);
        }
    }

    /* JADX INFO: renamed from: d */
    public float getUnitArmorRating(boolean z) {
        return this.rotationSpeed + 90.0f;
    }

    /* JADX INFO: renamed from: bT */
    public final boolean isAlive() {
        return this.unitTransportTarget == null && this.deceleration >= 1.0f;
    }

    public float x() {
        if (this.currentHealth < this.maxHealth) {
            return this.currentHealth / this.maxHealth;
        }
        return -1.0f;
    }

    public boolean bU() {
        return true;
    }

    public float bV() {
        if (this.deceleration < 1.0f) {
            if (this.parentEntity == null || this.parentEntity.deceleration >= 1.0f) {
                return this.deceleration;
            }
            return -1.0f;
        }
        return -1.0f;
    }

    /* JADX INFO: renamed from: bW */
    public float getUnitTeamData() {
        return -1.0f;
    }

    /* JADX INFO: renamed from: bX */
    public boolean isUnitAtPositionX() {
        return false;
    }

    /* JADX INFO: renamed from: bY */
    public int getTransportedUnitsWeight() {
        return -1;
    }

    /* JADX INFO: renamed from: bZ */
    public int getMaxTransportWeight() {
        return -1;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
        float f2;
        int iLongToIntArray;
        int iLongToIntArray2;
        int iLongToIntArray3;
        int iLongToIntArray4;
        if (this.isDestroyed || this.unitTransportTarget != null) {
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        float f3 = this.speed;
        if (z) {
            return;
        }
        boolean z2 = false;
        int transportedUnitsWeight = getTransportedUnitsWeight();
        if (bV() >= 0.0f) {
            z2 = true;
        }
        if (getUnitTeamData() >= 0.0f) {
            z2 = true;
        }
        if (this.isSelected || gameEngine.settingsEngine.showHp) {
            if (x() >= 0.0f) {
                z2 = true;
            }
            if (transportedUnitsWeight >= 0) {
                z2 = true;
            }
        }
        if (!z2) {
            return;
        }
        float f4 = this.posX - gameEngine.viewpointXSnapped;
        float f5 = (this.posY - gameEngine.viewpointYSnapped) - this.posZ;
        float f6 = f3 + 4.0f;
        int i = 4;
        float f7 = 2.0f * f3;
        if (this.isUnitStunned) {
            f2 = 1.0f;
        } else {
            f2 = gameEngine.zoom;
        }
        if (f2 < 1.0f) {
            gameEngine.graphicsEngine2.k();
            gameEngine.restoreZoomTransform();
            f4 *= gameEngine.zoom;
            f5 *= gameEngine.zoom;
            f6 *= gameEngine.zoom;
        }
        float f8 = 3.0f;
        if (this.isSelected || gameEngine.settingsEngine.showHp) {
            if (x() >= 0.0f) {
                boolean z3 = false;
                boolean z4 = false;
                AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
                if (attachmentSlotDefinitionDn != null) {
                    z3 = attachmentSlotDefinitionDn.p;
                    z4 = attachmentSlotDefinitionDn.q;
                }
                if (!z4) {
                    if (gameEngine.playerTeam.c(this.team)) {
                        iLongToIntArray = Utility.longToIntArray(200, 183, 44, 44);
                        iLongToIntArray2 = Utility.longToIntArray(120, 255, 60, 60);
                    } else {
                        iLongToIntArray = Utility.longToIntArray(200, 0, 150, 0);
                        iLongToIntArray2 = Utility.longToIntArray(120, 0, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 0);
                    }
                    Paint paintA = GameViewUtils.a(iLongToIntArray, Paint.Style.FILL);
                    Paint paintA2 = GameViewUtils.a(iLongToIntArray2, Paint.Style.STROKE);
                    int i2 = 4;
                    if (z3) {
                        i2 = 1;
                    }
                    dr.a(f4 - f3, f5 + f6, (f4 - f3) + (f7 * x()), f5 + f6 + i2);
                    gameEngine.graphicsEngine2.a(dr, paintA);
                    dr.a(f4 - f3, f5 + f6, (f4 - f3) + f7, f5 + f6 + i2);
                    gameEngine.graphicsEngine2.a(dr, paintA2);
                    if (this.cC != 0.0f && bU() && gameEngine.settingsEngine.showHpChanges) {
                        float fX = x();
                        float f9 = fX + ((-this.cC) / this.maxHealth);
                        if (f9 < 0.0f) {
                            f9 = 0.0f;
                        }
                        if (f9 >= 1.0f) {
                            f9 = 1.0f;
                        }
                        Paint paintA3 = GameViewUtils.a(Utility.longToIntArray(100, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_RADIO_SERVICE, 208, 26), Paint.Style.FILL);
                        dr.a((f4 - f3) + (f7 * fX), f5 + f6, (f4 - f3) + (f7 * f9), f5 + f6 + i2);
                        gameEngine.graphicsEngine2.a(dr, paintA3);
                    }
                }
            }
            if (transportedUnitsWeight >= 0) {
                int maxTransportWeight = getMaxTransportWeight();
                float f10 = f7;
                if (maxTransportWeight > 10) {
                    f10 += 20.0f;
                }
                float f11 = f4 - (f10 / 2.0f);
                float f12 = (int) ((f10 / maxTransportWeight) + 0.5f);
                float f13 = f12 - 2.0f;
                for (int i3 = 1; i3 <= maxTransportWeight; i3++) {
                    float f14 = f11 + ((i3 - 1) * f12);
                    dr.a(f14, f5 + f6 + 3.0f, f14 + f13, f5 + f6 + 3.0f + 3.0f);
                    if (transportedUnitsWeight >= i3) {
                        gameEngine.graphicsEngine2.a(dr, GameViewUtils.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_SERVICE, 0, 0, 255, Paint.Style.FILL));
                    }
                    gameEngine.graphicsEngine2.a(dr, GameViewUtils.a(110, 0, 0, 210, Paint.Style.STROKE));
                }
                f8 = 3.0f + 5.0f;
            }
        }
        if (getUnitTeamData() >= 0.0f) {
            int i4 = 2 + 1;
            boolean zIsUnitAtPositionX = isUnitAtPositionX();
            dr.a(f4 - f3, f5 + f6 + i4 + f8, (f4 - f3) + (f7 * getUnitTeamData()), f5 + f6 + i4 + 2 + f8);
            if (zIsUnitAtPositionX) {
                iLongToIntArray3 = Utility.longToIntArray(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_PROG_YELLOW, 103, 117, 119);
            } else {
                iLongToIntArray3 = Utility.longToIntArray(200, 23, 179, 207);
            }
            gameEngine.graphicsEngine2.a(dr, GameViewUtils.a(iLongToIntArray3, Paint.Style.FILL));
            dr.a(f4 - f3, f5 + f6 + i4 + f8, (f4 - f3) + f7, f5 + f6 + i4 + 2 + f8);
            if (zIsUnitAtPositionX) {
                iLongToIntArray4 = Utility.longToIntArray(105, 123, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_AVR_INPUT, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_BUTTON_6);
            } else {
                iLongToIntArray4 = Utility.longToIntArray(120, 45, 211, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_NETWORK);
            }
            gameEngine.graphicsEngine2.a(dr, GameViewUtils.a(iLongToIntArray4, Paint.Style.STROKE));
            f8 += 2;
            i = 4;
        }
        if (bV() >= 0.0f) {
            int i5 = i + 1;
            dr.a(f4 - f3, f5 + f6 + i5 + f8, (f4 - f3) + (f7 * bV()), f5 + f6 + i5 + i + f8);
            gameEngine.graphicsEngine2.a(dr, GameViewUtils.a(200, 0, 0, 150, Paint.Style.FILL));
            dr.a(f4 - f3, f5 + f6 + i5 + f8, (f4 - f3) + f7, f5 + f6 + i5 + i + f8);
            gameEngine.graphicsEngine2.a(dr, GameViewUtils.a(120, 0, 0, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, Paint.Style.STROKE));
            float f15 = f8 + i;
        }
        if (f2 < 1.0f) {
            gameEngine.graphicsEngine2.l();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void d(float f) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void p(float f) {
        if (!this.isDestroyed && this.unitTransportTarget == null && this.isSelected) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (this.team == gameEngine.playerTeam || gameEngine.gameUI.canControlUnit(this)) {
                if (gameEngine.settingsEngine.showUnitWaypoints && gameEngine.lastPinchDistance <= 40) {
                    gameEngine.lastPinchDistance++;
                    O();
                }
                getUnitAISettings();
            }
            if (GameViewUtils.a(this)) {
                cb();
            }
        }
    }

    /* JADX INFO: renamed from: ca */
    public void getUnitAISettings() {
    }

    public void O() {
        GameEngine gameEngine = GameEngine.getInstance();
        UnitCommand unitCommand = null;
        UnitCommand unitCommand2 = null;
        if (this instanceof OrderableUnit) {
            OrderableUnit orderableUnit = (OrderableUnit) this;
            int waypointCount = orderableUnit.getWaypointCount();
            float f = this.posX;
            float f2 = this.posY;
            for (int i = 0; i < waypointCount; i++) {
                UnitCommand waypointAt = orderableUnit.getWaypointAt(i);
                if (waypointAt != null) {
                    if (GameEngine.isPC()) {
                        ds.a(2.0f);
                    } else {
                        ds.a(0.0f);
                    }
                    if (waypointAt.getCommandType() == UnitCommandType.attack) {
                        ds.b(Color.a(160, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0, 0));
                    } else if (waypointAt.getCommandType() == UnitCommandType.attackMove) {
                        ds.b(Color.a(160, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0));
                    } else if (waypointAt.getCommandType() == UnitCommandType.build || waypointAt.getCommandType() == UnitCommandType.repair) {
                        ds.b(Color.a(160, 0, 0, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT));
                    } else if (waypointAt.getCommandType() == UnitCommandType.loadInto || waypointAt.getCommandType() == UnitCommandType.loadUp) {
                        ds.b(Color.a(160, 0, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT));
                    } else if (waypointAt.getCommandType() == UnitCommandType.reclaim) {
                        ds.b(Color.a(160, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0, 42));
                    } else if (waypointAt.getCommandType() == UnitCommandType.guard || waypointAt.getCommandType() == UnitCommandType.guardAt) {
                        ds.b(Color.a(160, 97, 20, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_LAST_CHANNEL));
                    } else if (waypointAt.getCommandType() == UnitCommandType.patrol) {
                        ds.b(Color.a(160, 0, 210, 210));
                        if (unitCommand == null) {
                            unitCommand = waypointAt;
                        } else {
                            unitCommand2 = waypointAt;
                        }
                    } else {
                        ds.b(Color.a(160, 0, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0));
                    }
                    float targetX = waypointAt.getTargetX();
                    float targetY = waypointAt.getTargetY();
                    BaseUnit targetUnit = waypointAt.getTargetUnit();
                    if (targetUnit != null && waypointAt.isUnitTargetCommand() && !targetUnit.bI() && !targetUnit.d(gameEngine.playerTeam)) {
                        float angleBetweenPoints = Utility.getAngleBetweenPoints(f, f2, targetX, targetY);
                        targetX = f + (Utility.fastCos(angleBetweenPoints) * 400.0f);
                        targetY = f2 + (Utility.fastSin(angleBetweenPoints) * 400.0f);
                    }
                    gameEngine.graphicsEngine2.a(f - gameEngine.viewpointXSnapped, f2 - gameEngine.viewpointYSnapped, targetX - gameEngine.viewpointXSnapped, targetY - gameEngine.viewpointYSnapped, ds);
                    if (0 != 0) {
                        float fDistance = Utility.distance(f, f2, targetX, targetY);
                        float angleBetweenPoints2 = Utility.getAngleBetweenPoints(f, f2, targetX, targetY);
                        float f3 = gameEngine.gameUI.tooltipDelay * fDistance;
                        float fFastCos = f + (Utility.fastCos(angleBetweenPoints2) * f3);
                        float fFastSin = f2 + (Utility.fastSin(angleBetweenPoints2) * f3);
                        dr.a(fFastCos - 1.0f, fFastSin - 1.0f, fFastCos + 1.0f, fFastSin + 1.0f);
                        dr.a(-gameEngine.viewpointXSnapped, -gameEngine.viewpointYSnapped);
                        gameEngine.graphicsEngine2.a(dr, ds);
                    }
                    f = targetX;
                    f2 = targetY;
                }
            }
        }
        if (unitCommand != null && unitCommand2 != null && unitCommand != unitCommand2) {
            ds.b(Color.a(50, 0, 210, 210));
            UnitCommand unitCommand3 = unitCommand;
            gameEngine.graphicsEngine2.a(unitCommand2.getTargetX() - gameEngine.viewpointXSnapped, unitCommand2.getTargetY() - gameEngine.viewpointYSnapped, unitCommand3.getTargetX() - gameEngine.viewpointXSnapped, unitCommand3.getTargetY() - gameEngine.viewpointYSnapped, ds);
        }
    }

    public void cb() {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
        Paint paint;
        boolean z = false;
        if (this.unitBuildProgress != 0.0f) {
            this.unitBuildProgress = Utility.moveTowardsZero(this.unitBuildProgress, f);
            if (this.unitBuildProgress % 15.0f < 7.0f) {
                z = true;
            }
        }
        if (this.isSelected || z) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (dp()) {
                float f2 = this.posX - gameEngine.viewpointXSnapped;
                float f3 = (this.posY - gameEngine.viewpointYSnapped) - this.posZ;
                PlayerTeam playerTeam = gameEngine.playerTeam;
                if (playerTeam == this.team) {
                    if (this.maxSpeed < 8.0f) {
                        paint = cX;
                    } else {
                        paint = cY;
                    }
                } else if (playerTeam.c(this.team)) {
                    paint = da;
                } else if (this.team != null && gameEngine.replayEngine.j()) {
                    cW.b(PlayerTeam.i(this.team.teamColorId));
                    paint = cW;
                } else {
                    paint = dc;
                }
                if (z) {
                    paint = db;
                }
                if (bI()) {
                    if (paint == cY) {
                        paint = cZ;
                    }
                    Rect unitAIUpdateTime = getUnitAIUpdateTime();
                    if (unitAIUpdateTime != null) {
                        dr.a(unitAIUpdateTime);
                        dr.b *= gameEngine.tileMap.tileWorldSizeY;
                        dr.d *= gameEngine.tileMap.tileWorldSizeY;
                        dr.a *= gameEngine.tileMap.tileWorldSizeX;
                        dr.c *= gameEngine.tileMap.tileWorldSizeX;
                        float unitAIPathfindError = getUnitAIPathfindError();
                        dr.a(-(getUnitAIState() - gameEngine.tileMap.halfTileWorldSizeX), -(getUnitAIPathfindStatus() - gameEngine.tileMap.halfTileWorldSizeY));
                        Utility.grow(dr, unitAIPathfindError);
                        dr.a(f2, f3);
                        gameEngine.graphicsEngine2.a(dr.a - 11.0f, dr.b, dr.c + 11.0f, dr.b, paint);
                        gameEngine.graphicsEngine2.a(dr.a - 11.0f, dr.d, dr.c + 11.0f, dr.d, paint);
                        gameEngine.graphicsEngine2.a(dr.a, dr.b - 11.0f, dr.a, dr.d + 11.0f, paint);
                        gameEngine.graphicsEngine2.a(dr.c, dr.b - 11.0f, dr.c, dr.d + 11.0f, paint);
                        return;
                    }
                    return;
                }
                float unitSelectionFadeEffect = this.maxSpeed + gameEngine.gameUI.getUnitSelectionFadeEffect(this);
                if (gameEngine.handleNetworkPacket(f2, f3, unitSelectionFadeEffect)) {
                    gameEngine.graphicsEngine2.a(f2, f3, unitSelectionFadeEffect, paint);
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return true;
    }

    public Rect cc() {
        return dw;
    }

    public Rect cd() {
        return dw;
    }

    /* JADX INFO: renamed from: ce */
    public Rect getUnitAIUpdateTime() {
        return cc();
    }

    public Texture v() {
        return null;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean f(float f) {
        Paint paint;
        Texture textureV = v();
        if (textureV == null) {
            return false;
        }
        if (this.isDestroyed) {
            return true;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.graphicsEngine2.l();
        float f2 = (int) (this.posX - gameEngine.viewpointXSnapped);
        float f3 = (int) (this.posY - gameEngine.viewpointYSnapped);
        float f4 = f2 * gameEngine.zoom;
        float f5 = f3 * gameEngine.zoom;
        if (this.isSelected) {
            paint = bJ;
        } else {
            paint = bI;
        }
        gameEngine.graphicsEngine2.a(textureV, f4, f5, paint);
        gameEngine.graphicsEngine2.k();
        gameEngine.applyZoomTransform();
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean a(GameEngine gameEngine) {
        if (!gameEngine.cameraFollowSpeed.b(this.posX, this.posY) || this.unitTransportTarget != null) {
            return false;
        }
        if ((this.attachmentData != null && (this.attachmentData.I || this.attachmentData.C)) || !d(gameEngine.playerTeam)) {
            return false;
        }
        return true;
    }

    public boolean c_() {
        return true;
    }

    /* JADX INFO: renamed from: cf */
    public final boolean getWeight() {
        return d(GameEngine.getInstance().playerTeam);
    }

    public boolean d(PlayerTeam playerTeam) {
        TileMap tileMap = GameEngine.getInstance().tileMap;
        if ((this.team != playerTeam || this.parentEntity != null) && tileMap.fogEnabled && playerTeam.fogOfWarData != null) {
            tileMap.setCursorTileIndexFromWorldPoint(this.posX, this.posY);
            int i = tileMap.cursorTileX;
            int i2 = tileMap.cursorTileY;
            if (tileMap.isInBounds(i, i2) && playerTeam.fogOfWarData[i][i2] >= 5) {
                return false;
            }
            return true;
        }
        return true;
    }

    /* JADX INFO: renamed from: cg */
    public boolean getUnitAIPriority() {
        return true;
    }

    public void f_() {
        if (this.isDestroyed) {
            this.isAttacking = false;
        } else {
            this.isAttacking = true;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        Effect effectCreateEffectInternal;
        if (this.isDestroyed) {
            return;
        }
        if (this.unitArmor > 0.0f) {
            if (this.unitArmor > this.maxHealth * 2.0f) {
                this.unitArmor = this.maxHealth * 2.0f;
            }
            this.unitArmor = Utility.moveTowardsZero(this.unitArmor, f);
        }
        if (this.currentHealth < this.maxHealth * 0.33f && this.posZ > -1.0f) {
            GameEngine gameEngine = GameEngine.getInstance();
            this.unitAnimationOffset += f;
            this.unitAnimationScale += f;
            this.unitAnimationRotation += f;
            if (this.unitAnimationOffset > 10.0f && this.unitAnimationScale < 300.0f && !dl()) {
                this.unitAnimationOffset = 0.0f;
                if (this.flag3 && gameEngine.mouseScreenY && (effectCreateEffectInternal = gameEngine.effectManager.createEffectInternal(this.posX, this.posY, this.posZ, EffectType.custom, false, EffectQuality.verylow)) != null) {
                    EffectEmitter.b(effectCreateEffectInternal, true);
                    effectCreateEffectInternal.I = this.posX;
                    effectCreateEffectInternal.J = this.posY;
                    effectCreateEffectInternal.K = this.posZ;
                    effectCreateEffectInternal.P += Utility.randomFloatInRange(-0.1f, 0.1f) + this.velocityX;
                    effectCreateEffectInternal.Q += Utility.randomFloatInRange(-0.1f, 0.1f) + this.velocityY;
                    effectCreateEffectInternal.I += Utility.randomFloatInRange(-4.0f, 4.0f);
                    effectCreateEffectInternal.J += Utility.randomFloatInRange(-4.0f, 4.0f);
                }
            }
            if (this.unitAnimationRotation > 30.0f && this.unitAnimationScale < 600.0f && !dm()) {
                this.unitAnimationRotation = 0.0f;
                gameEngine.effectManager.setOnlyOnScreen();
                Effect effectCreateEffectInternal2 = gameEngine.effectManager.createEffectInternal(this.posX, this.posY, this.posZ, EffectType.custom, false, EffectQuality.verylow);
                if (effectCreateEffectInternal2 != null) {
                    EffectEmitter.a(effectCreateEffectInternal2, true);
                    effectCreateEffectInternal2.I = this.posX;
                    effectCreateEffectInternal2.J = this.posY;
                    effectCreateEffectInternal2.K = this.posZ;
                    effectCreateEffectInternal2.P += Utility.randomFloatInRange(-0.1f, 0.1f);
                    effectCreateEffectInternal2.Q += Utility.randomFloatInRange(-0.1f, 0.1f);
                    effectCreateEffectInternal2.I += Utility.randomFloatInRange(-4.0f, 4.0f);
                    effectCreateEffectInternal2.J += Utility.randomFloatInRange(-4.0f, 4.0f);
                }
            }
        } else if (this.unitAnimationScale != 0.0f) {
            this.unitAnimationScale = 0.0f;
        }
        if (this.cC != 0.0f) {
            this.cC = Utility.moveTowardsZero(this.cC, this.maxHealth * this.unitDefenseMultiplier * 0.005f * f);
            this.unitDefenseMultiplier += f + (0.2f * this.unitDefenseMultiplier * f);
            if (this.cC == 0.0f) {
                this.unitDefenseMultiplier = 0.0f;
            }
        }
        if (this.currentHealth <= 0.0f) {
            updateUnitAI();
        }
    }

    /* JADX INFO: renamed from: b */
    public float calculateTurnSpeed(BaseUnit baseUnit, float f, Projectile projectile) {
        float f2 = f;
        float f3 = 1.0f;
        float f4 = 1.0f;
        float f5 = 1.0f;
        if (projectile != null) {
            f3 = projectile.ak;
            f4 = projectile.al;
            f5 = projectile.am;
        }
        if (this.shield < this.unitEnergyMax) {
            float f6 = f2 * f3;
            if (this.unitEnergyMax - this.shield > f6) {
                this.shield += f6;
                f2 -= f6 * f4;
            } else {
                this.shield = this.unitEnergyMax;
                f2 -= f6 * f4;
            }
        }
        if (f2 > 0.0f && this.currentHealth < this.maxHealth) {
            float f7 = f2 * f5;
            float f8 = this.maxHealth - this.currentHealth;
            if (f8 > f7) {
                o(this.currentHealth + f7);
                return 0.0f;
            }
            o(this.maxHealth);
            float f9 = f2 - f8;
            return 0.0f;
        }
        return 0.0f;
    }

    /* JADX INFO: renamed from: J */
    public boolean mo193J() {
        return false;
    }

    /* JADX INFO: renamed from: a */
    public float setTarget(BaseUnit baseUnit, float f, Projectile projectile) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.deceleration < 1.0f) {
            f *= 1.75f;
        }
        float f2 = 1.0f;
        float f3 = 1.0f;
        float f4 = 1.0f;
        if (projectile != null) {
            f2 = projectile.ak;
            f3 = projectile.al;
            f4 = projectile.am;
        }
        float f5 = f;
        float f6 = 0.0f;
        if (this.energy == 0.0f && this.shield > 0.0f) {
            float f7 = f5 * f2;
            if (this.shield < f7) {
                f5 -= this.shield * f3;
                f6 = 0.0f + this.shield;
                this.unitShieldMax += this.shield;
                this.shield = 0.0f;
            } else {
                this.shield -= f7;
                this.unitShieldMax += f7;
                f6 = 0.0f + f7;
                f5 -= f5 * f3;
            }
        }
        if (f5 > 0.0f) {
            float f8 = f5 * f4;
            if (this.currentHealth < f8) {
                f5 -= this.currentHealth;
                float f9 = f6 + this.currentHealth;
                o(0.0f);
                this.cC += this.currentHealth;
            } else {
                o(this.currentHealth - f8);
                float f10 = f6 + f8;
                f5 -= f8;
                this.cC -= f8;
            }
        }
        this.bs = gameEngine.lastTick;
        if (baseUnit != null) {
            this.unitTarget1 = baseUnit;
        } else {
            this.unitTarget1 = null;
        }
        updateUnitAI();
        return f5;
    }

    public BaseUnit q(float f) {
        if (GameEngine.getInstance().lastTick - (f * 1000.0f) < this.bs) {
            return this.unitTarget1;
        }
        return null;
    }

    /* JADX INFO: renamed from: ch */
    public void updateUnitAI() {
        if (!this.isDestroyed && this.currentHealth <= 0.0f) {
            bv();
        }
    }

    public void n() {
    }

    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        return false;
    }

    public void bt() {
    }

    public void bu() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.gameUI.deselectUnit(this);
        PlayerTeam.a(this);
        if (bE.remove(this)) {
        }
        this.isDestroyed = true;
        this.unitCreationTime = gameEngine.lastTick;
        if (this.currentHealth > 0.0f) {
            this.currentHealth = 0.0f;
        }
        if (this.movementLevels != null) {
            int techLevel = getTechLevel();
            for (int i = 0; i < techLevel; i++) {
                this.movementLevels[i].targetUnit = null;
            }
        }
        gameEngine.unitSpatialIndex.a(this);
    }

    /* JADX INFO: renamed from: ci */
    public void getUnitAICondition() {
        bu();
        remove();
        bt();
    }

    /* JADX INFO: renamed from: cj */
    public void getUnitAIConditionTime() {
        this.currentHealth = -1.0f;
    }

    public void bv() {
        bu();
        if (!e()) {
            remove();
        }
        bt();
    }

    /* JADX INFO: renamed from: a */
    public boolean heal(RectF rectF) {
        if (this.posX + this.speed > rectF.a && this.posX - this.speed < rectF.c && this.posY + this.speed > rectF.b && this.posY - this.speed < rectF.d) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: c */
    public final boolean checkAttackCooldown(float f, float f2, float f3) {
        float fDistanceSq = Utility.distanceSq(this.posX, this.posY, f, f2);
        float f4 = this.speed + f3;
        if (fDistanceSq < f4 * f4) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: t */
    public boolean setWidth(BaseUnit baseUnit) {
        float fDistanceSq = Utility.distanceSq(this.posX, this.posY, baseUnit.posX, baseUnit.posY);
        float f = this.speed + baseUnit.speed;
        if (fDistanceSq < f * f) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: P */
    public final void setUnitSelected(int i) throws MapLoadException {
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            throw new MapLoadException("Could not find team with id: " + i);
        }
        isSelectable(playerTeamK);
    }

    /* JADX INFO: renamed from: e */
    public void isSelectable(PlayerTeam playerTeam) {
        if (this.team == playerTeam) {
            return;
        }
        if (playerTeam == null) {
            throw new RuntimeException("Could not set team to null");
        }
        if (this.team != null) {
            PlayerTeam.b(this);
            this.team.d(this);
        }
        setUnitTeam(playerTeam);
        PlayerTeam.c(this);
        if (playerTeam != PlayerTeam.TEAM_ALL) {
            c(false);
        }
    }

    /* JADX INFO: renamed from: b */
    public void setUnitTeam(PlayerTeam playerTeam) {
        if (playerTeam == null) {
            throw new RuntimeException("Could not set team to null");
        }
        this.team = playerTeam;
    }

    /* JADX INFO: renamed from: Q */
    public final void setTeam(int i) throws MapLoadException {
        this.team = PlayerTeam.k(i);
        if (this.team == null) {
            throw new MapLoadException("Could not find team with id: " + i);
        }
        setUnitTeam(this.team);
    }

    /* JADX INFO: renamed from: N */
    public ArrayList<AbstractUnitAction> getAvailableActions() {
        return dx;
    }

    /* JADX INFO: renamed from: V */
    public int getUpgradeLevel() {
        return 1;
    }

    /* JADX INFO: renamed from: a */
    public void performUnitAction(AbstractUnitAction abstractUnitAction, boolean z) {
    }

    public void a(AbstractUnitAction abstractUnitAction, boolean z, PointF pointF, BaseUnit baseUnit) {
        performUnitAction(abstractUnitAction, z);
    }

    /* JADX INFO: renamed from: b */
    public void stopMoving(AbstractUnitAction abstractUnitAction, boolean z) {
    }

    public void a(AbstractUnitAction abstractUnitAction) {
    }

    /* JADX INFO: renamed from: a */
    public AbstractUnitAction validateActionId(ActionId actionId) {
        ArrayList availableActions = getAvailableActions();
        int size = availableActions.size();
        for (int i = 0; i < size; i++) {
            AbstractUnitAction abstractUnitAction = (AbstractUnitAction) availableActions.get(i);
            if (abstractUnitAction.isAvailableForUnit(actionId)) {
                return abstractUnitAction;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: ck */
    public boolean isUnitEnergyCost() {
        if (bI()) {
            return AbstractUnitAction.isMove(cm());
        }
        return false;
    }

    /* JADX INFO: renamed from: cl */
    public boolean isUnitBuilding() {
        return false;
    }

    public ActionId cm() {
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    /* JADX INFO: renamed from: cn */
    public float getUnitAIPathfindState() {
        return -1.0f;
    }

    /* JADX INFO: renamed from: co */
    public boolean getUnitAIPathfindTarget() {
        return false;
    }

    /* JADX INFO: renamed from: a */
    public void clearAndAddAction(ArrayList arrayList) {
        arrayList.clear();
    }

    /* JADX INFO: renamed from: cp */
    public ActionId getUnitAIPathfindPath() {
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    /* JADX INFO: renamed from: e */
    public AbstractUnitAction getUnitAction(UnitType unitType) {
        return null;
    }

    /* JADX INFO: renamed from: cq */
    public final int getUnitAIPathfindDistance() {
        int i = 0;
        for (AbstractUnitAction abstractUnitAction : getAvailableActions()) {
            if (abstractUnitAction.b(this) || abstractUnitAction.isWaitingForTarget()) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: c */
    public boolean isUnitArmorEffective(BaseUnit baseUnit, boolean z) {
        BaseUnit baseUnit2 = baseUnit.unitTransportTarget;
        OrderableUnit orderableUnit = baseUnit.parentEntity;
        baseUnit.unitTransportTarget = null;
        baseUnit.parentEntity = null;
        boolean zD = d(baseUnit, z);
        baseUnit.unitTransportTarget = baseUnit2;
        baseUnit.parentEntity = orderableUnit;
        return zD;
    }

    public boolean d(BaseUnit baseUnit, boolean z) {
        return false;
    }

    public boolean e(BaseUnit baseUnit, boolean z) {
        return false;
    }

    /* JADX INFO: renamed from: cr */
    public boolean getUnitAIPathfindCost() {
        return false;
    }

    /* JADX INFO: renamed from: cs */
    public float getUnitAIPathfindHeuristic() {
        return 21.0f;
    }

    /* JADX INFO: renamed from: ct */
    public boolean getUnitAIPathfindMaxDepth() {
        return i();
    }

    /* JADX INFO: renamed from: cu */
    public boolean getUnitAIPathfindMaxNodes() {
        return false;
    }

    /* JADX INFO: renamed from: cv */
    public boolean getUnitAIPathfindTimeout() {
        return false;
    }

    public boolean P() {
        return false;
    }

    /* JADX INFO: renamed from: cw */
    public int getUnitAIPathfindIterations() {
        return 1;
    }

    public int y() {
        return 85;
    }

    public float f(UnitType unitType) {
        return unitType.a(this) + y();
    }

    /* JADX INFO: renamed from: u */
    public int setHeight(BaseUnit baseUnit) {
        return y() + baseUnit.r().a(this);
    }

    public int v(BaseUnit baseUnit) {
        return y() + baseUnit.r().a(this);
    }

    public boolean w(BaseUnit baseUnit) {
        return false;
    }

    public boolean x(BaseUnit baseUnit) {
        return false;
    }

    /* JADX INFO: renamed from: b */
    public float getDistanceToTarget(BaseUnit baseUnit) {
        return 1.0f;
    }

    public float c(BaseUnit baseUnit) {
        return 0.2f;
    }

    public boolean y(BaseUnit baseUnit) {
        boolean z = false;
        if (baseUnit.getUnitHealthPercent() > 0.0f) {
            z = true;
        }
        return z;
    }

    /* JADX INFO: renamed from: z */
    public float calculateUnitSpeed(BaseUnit baseUnit) {
        float fC = c(baseUnit) * 5.1f;
        if (baseUnit.getUnitHealthPercent() > 0.0f) {
            fC = baseUnit.getUnitHealthPercent();
        }
        return fC;
    }

    /* JADX INFO: renamed from: cx */
    public float getUnitAIPathfindMemory() {
        return 1.0f;
    }

    public float cy() {
        return 0.0f;
    }

    /* JADX INFO: renamed from: cz */
    public StoredResources getUnitAIPathfindResult() {
        float fCy = cy();
        if (fCy == 0.0f) {
            return StoredResources.a;
        }
        StoredResources storedResources = new StoredResources();
        storedResources.b(Resource.D, fCy);
        return storedResources;
    }

    /* JADX INFO: renamed from: cA */
    public StoredResources getUnitRotationData() {
        return StoredResources.a;
    }

    /* JADX INFO: renamed from: cB */
    public String getVelocityX() {
        return r().getUnitTypeDescriptionShort() + "(id:" + this.objectId + ")";
    }

    public static String f(BaseUnit baseUnit, boolean z) {
        if (baseUnit != null) {
            return baseUnit.r().getUnitName();
        }
        return "No unit";
    }

    /* JADX INFO: renamed from: A */
    public static String serialize(BaseUnit baseUnit) {
        if (baseUnit != null) {
            return baseUnit.getUnitShortName();
        }
        return "<null unit>";
    }

    /* JADX INFO: renamed from: c */
    public String getUnitShortName() {
        String str = r().getUnitTypeDescriptionShort() + "(pos:" + ((int) this.posX) + "," + ((int) this.posY) + " id:" + this.objectId + VariableScope.nullOrMissingString;
        if (this.team != null) {
            str = str + " t:" + this.team.teamId;
        }
        if (this.isDestroyed) {
            str = str + " [dead]";
        }
        if (this.isDestroyed) {
            str = str + " [deleted]";
        }
        return str + ")";
    }

    /* JADX INFO: renamed from: cC */
    public String getVelocityY() {
        String str = (r().getUnitTypeDescriptionShort() + "(pos:" + ((int) this.posX) + "," + ((int) this.posY) + " id:" + this.objectId + VariableScope.nullOrMissingString) + ", hp:" + this.currentHealth + ", dead:" + this.isDestroyed + ", deleted:" + this.isDestroyed + " tags:" + getUnitCombatAnimation();
        if (this.team != null) {
            str = str + " t:" + this.team.teamId;
        }
        return str + ")";
    }

    /* JADX INFO: renamed from: cD */
    public float getMaxHealth() {
        return 1.0f;
    }

    /* JADX INFO: renamed from: cE */
    public RectF getUnitMassBounds() {
        GameEngine gameEngine = GameEngine.getInstance();
        float maxHealth = getMaxHealth();
        dA.a((this.posX - (this.eu * maxHealth)) - gameEngine.viewpointXSnapped, (this.posY - (this.ev * maxHealth)) - gameEngine.viewpointYSnapped, (this.posX + (this.eu * maxHealth)) - gameEngine.viewpointXSnapped, (this.posY + (this.ev * maxHealth)) - gameEngine.viewpointYSnapped);
        return dA;
    }

    /* JADX INFO: renamed from: cF */
    public RectF getUnitBounds() {
        GameEngine gameEngine = GameEngine.getInstance();
        RectF rectF = dA;
        float f = gameEngine.viewpointXSnapped;
        float f2 = gameEngine.viewpointYSnapped;
        float f3 = this.eu;
        float f4 = this.ev;
        rectF.a = (this.posX - f3) - f;
        rectF.c = (this.posX + f3) - f;
        rectF.b = (this.posY - f4) - f2;
        rectF.d = (this.posY + f4) - f2;
        return rectF;
    }

    /* JADX INFO: renamed from: cG */
    public boolean isUnitTransportWeight() {
        return false;
    }

    public Rect a_(boolean z) {
        dC.a = 0;
        dC.b = 0;
        dC.c = 0 + this.es;
        dC.d = 0 + this.et;
        return dC;
    }

    public Rect a(boolean z, int i) {
        int i2 = 0 + (i * this.es);
        dC.a(i2, 0, i2 + this.es, 0 + this.et);
        return dC;
    }

    public Rect a(boolean z, int i, int i2) {
        int i3 = this.es;
        int i4 = this.et;
        int i5 = i * i3;
        int i6 = i2 * i4;
        dC.a = i5;
        dC.b = i6;
        dC.c = i5 + i3;
        dC.d = i6 + i4;
        return dC;
    }

    public boolean a(BaseUnit baseUnit, float f) {
        return false;
    }

    public void a_(String str) {
    }

    /* JADX INFO: renamed from: cH */
    public final boolean checkTransportSlots() {
        if (!m147cJ() || this.posZ > 2.0f) {
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: cI */
    public boolean isUnitTransportCategory() {
        return GameViewUtils.b(this.posX, this.posY);
    }

    /* JADX INFO: renamed from: cJ */
    public boolean m147cJ() {
        return GameViewUtils.c(this.posX, this.posY);
    }

    /* JADX INFO: renamed from: cK */
    public boolean isMoving() {
        return GameViewUtils.d(this.posX, this.posY);
    }

    public int bw() {
        return (((0 * 31) + ((int) getPushMass())) * 31) + ((int) this.maxHealth);
    }

    public int cL() {
        return r().b(getUpgradeLevel());
    }

    /* JADX INFO: renamed from: cM */
    public UnitPrice getUnitDescription() {
        return r().d(getUpgradeLevel());
    }

    /* JADX INFO: renamed from: cN */
    public UnitPrice getUnitDisplayName() {
        return null;
    }

    public PointF a(float f, float f2, float f3, float f4, float f5) {
        float fDistance = 0.0f;
        if (f3 > 0.1d && this.isRepairing) {
            float f6 = 1.0f / f3;
            for (int i = 0; i < 3; i++) {
                PointF pointFCalculateMaxAttackRange = calculateMaxAttackRange(fDistance);
                fDistance = Utility.distance(f, f2, pointFCalculateMaxAttackRange.x, pointFCalculateMaxAttackRange.y) * f6;
            }
        }
        if (fDistance > f4) {
            fDistance = f4;
        }
        PointF pointFCalculateMaxAttackRange2 = calculateMaxAttackRange(fDistance);
        float fDistanceSq = Utility.distanceSq(f, f2, pointFCalculateMaxAttackRange2.x, pointFCalculateMaxAttackRange2.y);
        if (f5 >= 0.0f && f5 * f5 < fDistanceSq) {
            float angleBetweenPoints = Utility.getAngleBetweenPoints(f, f2, pointFCalculateMaxAttackRange2.x, pointFCalculateMaxAttackRange2.y);
            pointFCalculateMaxAttackRange2.x = f + (Utility.fastCos(angleBetweenPoints) * f5);
            pointFCalculateMaxAttackRange2.y = f2 + (Utility.fastSin(angleBetweenPoints) * f5);
        }
        dD.a(pointFCalculateMaxAttackRange2);
        return dD;
    }

    /* JADX INFO: renamed from: m */
    PointF calculateMaxAttackRange(float f) {
        dE.a(this.posX + (this.velocityX * f), this.posY + (this.velocityY * f));
        return dE;
    }

    public boolean o() {
        return false;
    }

    public boolean p() {
        return false;
    }

    /* JADX INFO: renamed from: cO */
    public boolean isUnitIcon() {
        return false;
    }

    public void f(PlayerTeam playerTeam) {
        if (p()) {
            setUnitTeam(PlayerTeam.TEAM_ALL);
        } else {
            setUnitTeam(playerTeam);
        }
    }

    /* JADX INFO: renamed from: B */
    public void getUnitType(BaseUnit baseUnit) {
        if (baseUnit instanceof EditorOrBuilder) {
            baseUnit = null;
        }
        this.unitTarget2 = baseUnit;
    }

    /* JADX INFO: renamed from: cP */
    public void getUnitCategory() {
    }

    /* JADX INFO: renamed from: g */
    public float getUnitHealthPercent() {
        return 0.0f;
    }

    /* JADX INFO: renamed from: cQ */
    public int getUnitTypeName() {
        return Integer.MAX_VALUE;
    }

    /* JADX INFO: renamed from: cR */
    public AnimationSet getUnitTypeId() {
        return null;
    }

    public boolean g(BaseUnit baseUnit, boolean z) {
        return false;
    }

    public boolean h(BaseUnit baseUnit, boolean z) {
        return g(baseUnit, z);
    }

    /* JADX INFO: renamed from: cS */
    public int getUnitType() {
        return 500;
    }

    public boolean c(OrderableUnit orderableUnit) {
        int unitTypeName = getUnitTypeName();
        if (unitTypeName < Integer.MAX_VALUE && d(orderableUnit) >= unitTypeName) {
            return true;
        }
        return false;
    }

    public int d(OrderableUnit orderableUnit) {
        UnitCommand currentWaypoint;
        int i = 0;
        PlayerTeam playerTeam = orderableUnit.team;
        BaseUnit[] baseUnitArrA = bE.a();
        int size = bE.size();
        for (int i2 = 0; i2 < size; i2++) {
            BaseUnit baseUnit = baseUnitArrA[i2];
            if (baseUnit.team == playerTeam && (baseUnit instanceof OrderableUnit) && (currentWaypoint = ((OrderableUnit) baseUnit).getCurrentWaypoint()) != null && currentWaypoint.getCommandType() == UnitCommandType.reclaim && currentWaypoint.targetUnit == this && baseUnit != orderableUnit) {
                i++;
            }
        }
        return i;
    }

    public int e(OrderableUnit orderableUnit) {
        UnitCommand currentWaypoint;
        int i = 0;
        PlayerTeam playerTeam = orderableUnit.team;
        BaseUnit[] baseUnitArrA = bE.a();
        int size = bE.size();
        for (int i2 = 0; i2 < size; i2++) {
            BaseUnit baseUnit = baseUnitArrA[i2];
            if (baseUnit.team == playerTeam && (baseUnit instanceof OrderableUnit) && (currentWaypoint = ((OrderableUnit) baseUnit).getCurrentWaypoint()) != null && currentWaypoint.getCommandType() == UnitCommandType.repair && currentWaypoint.targetUnit == this && baseUnit != orderableUnit) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: bl */
    public int getTechLevel() {
        return 1;
    }

    public boolean u() {
        return false;
    }

    /* JADX INFO: renamed from: cT */
    public boolean getUnitVersion() {
        return u() || this.deceleration < 1.0f || this.team == PlayerTeam.TEAM_UNKNOWN;
    }

    /* JADX INFO: renamed from: cU */
    public boolean getUnitFlags() {
        return !u();
    }

    public boolean t() {
        return false;
    }

    /* JADX INFO: renamed from: cV */
    public boolean getUnitState() {
        return t();
    }

    /* JADX INFO: renamed from: cW */
    public boolean getUnitStatus() {
        return false;
    }

    public boolean d(BaseUnit baseUnit) {
        return true;
    }

    public void g(PlayerTeam playerTeam) {
        if (this.unitEffects == null || this.unitEffects.length != PlayerTeam.TEAM_NEUTRAL) {
            this.unitEffects = new UnitEffectData[PlayerTeam.TEAM_NEUTRAL];
            for (int i = 0; i < this.unitEffects.length; i++) {
                this.unitEffects[i] = new UnitEffectData();
            }
        }
        UnitEffectData unitEffectData = this.unitEffects[playerTeam.teamId];
        if (this.isDestroyed) {
            if (unitEffectData.a && d(playerTeam)) {
                unitEffectData.a = false;
                return;
            }
            return;
        }
        if (d(playerTeam)) {
            unitEffectData.a = true;
            unitEffectData.b = getUpgradeLevel();
        }
    }

    /* JADX INFO: renamed from: cX */
    public void getUnitAIType() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.playerTeam != null && this.team != gameEngine.playerTeam && gameEngine.playerTeam.teamId >= 0 && gameEngine.playerTeam.teamId < PlayerTeam.TEAM_NEUTRAL) {
            UnitEffectData unitEffectData = this.unitEffects[gameEngine.playerTeam.teamId];
            if (unitEffectData.c != null && unitEffectData.c.isDead) {
                unitEffectData.c = null;
            }
            if (unitEffectData.c == null && unitEffectData.a && !d(gameEngine.playerTeam)) {
                BuildPreview buildPreview = new BuildPreview();
                unitEffectData.c = buildPreview;
                buildPreview.unitType = r();
                buildPreview.tempPoint = this.posX;
                buildPreview.tempRectF1 = this.posY;
                buildPreview.isBuilding = false;
                buildPreview.team = this.team;
                buildPreview.someCounterF = unitEffectData.b;
                buildPreview.placingTeam = gameEngine.playerTeam;
                buildPreview.someFlagU = c_();
                buildPreview.attachedUnit = this;
            }
        }
    }

    /* JADX INFO: renamed from: cY */
    public PointF getUnitAIPosition() {
        dG.a(0.0f, 0.0f);
        return dG;
    }

    /* JADX INFO: renamed from: cZ */
    public float getUnitAIState() {
        return GameEngine.getInstance().tileMap.halfTileWorldSizeX;
    }

    /* JADX INFO: renamed from: da */
    public float getUnitAIPathfindStatus() {
        return GameEngine.getInstance().tileMap.halfTileWorldSizeY;
    }

    /* JADX INFO: renamed from: db */
    public strictfp float getUnitAIPathfindError() {
        final GameEngine instance = GameEngine.getInstance();
        return instance.tileMap.halfTileWorldSizeX + 2 + instance.gameUI.getUnitSelectionFadeEffect(this);
    }

    public Point a(TileMap tileMap, Point point) {
        point.worldX = (int) (((this.posX - getUnitAIState()) + 1.0f) * tileMap.tileScaleX);
        point.worldY = (int) (((this.posY - getUnitAIPathfindStatus()) + 1.0f) * tileMap.tileScaleY);
        return point;
    }

    public RectF a(TileMap tileMap, RectF rectF) {
        tileMap.setCursorTileIndexFromTileIndex((int) (((this.posX - getUnitAIState()) + 1.0f) * tileMap.tileScaleX), (int) (((this.posY - getUnitAIPathfindStatus()) + 1.0f) * tileMap.tileScaleY));
        float f = tileMap.cursorTileX;
        float f2 = tileMap.cursorTileY;
        Rect rectCd = cd();
        rectF.a(f + (rectCd.a * tileMap.tileWorldSizeX), f2 + (rectCd.b * tileMap.tileWorldSizeY), f + ((rectCd.c + 1) * tileMap.tileWorldSizeX), f2 + ((rectCd.d + 1) * tileMap.tileWorldSizeY));
        return rectF;
    }

    /* JADX INFO: renamed from: dc */
    public void getUnitAICombatState() {
    }

    /* JADX INFO: renamed from: dd */
    public boolean getUnitAICombatTarget() {
        return false;
    }

    public boolean q() {
        return false;
    }

    /* JADX INFO: renamed from: de */
    public AnimationSet getUnitCombatAnimation() {
        return null;
    }

    /* JADX INFO: renamed from: df */
    public StoredResources getUnitAICombatRange() {
        return this.unitCustomEffects;
    }

    public double a(Resource resource) {
        return this.unitCustomEffects.a(resource);
    }

    /* JADX INFO: renamed from: dg */
    public AnimationTrackingManager getUnitCombatCooldown() {
        return this.unitCustomComponents;
    }

    /* JADX INFO: renamed from: dh */
    public AnimationSet getUnitAICombatTimer() {
        return null;
    }

    public float bd() {
        return 0.0f;
    }

    /* JADX INFO: renamed from: di */
    public void updateUnitCombatTimer() {
    }

    /* JADX INFO: renamed from: dj */
    public void getUnitAICombatStateTime() {
    }

    public boolean dk() {
        return false;
    }

    public boolean dl() {
        return bO();
    }

    public boolean dm() {
        return bO();
    }

    public final AttachmentSlotDefinition dn() {
        return this.attachmentData;
    }

    public String toString() {
        return "unit(id=" + this.objectId + ",type=" + r().getUnitTypeDescriptionShort() + ")";
    }

    public void r(float f) {
        if (f >= 1.0f) {
            if (!(this.deceleration >= 1.0f)) {
                PlayerTeam.b(this);
                this.deceleration = 1.0f;
                PlayerTeam.c(this);
                return;
            }
            return;
        }
        if (this.deceleration >= 1.0f) {
            PlayerTeam.b(this);
            this.deceleration = f;
            PlayerTeam.c(this);
            return;
        }
        this.deceleration = f;
    }

    public final void a(UnitEventType unitEventType) {
        a(unitEventType, (BaseUnit) null, (AnimationSet) null, (VariableScope) null);
    }

    public final void a(UnitEventType unitEventType, BaseUnit baseUnit) {
        a(unitEventType, baseUnit, (AnimationSet) null, (VariableScope) null);
    }

    public void a(UnitEventType unitEventType, BaseUnit baseUnit, AnimationSet animationSet, VariableScope variableScope) {
    }

    public void h(float f) {
        this.rotationSpeed = f;
    }

    public int a(AnimationTag animationTag) {
        return 0;
    }

    public FastArrayList e(boolean z) {
        return null;
    }

    public boolean a(int i, int i2) {
        return false;
    }

    public void c(boolean z) {
    }

    /* JADX INFO: renamed from: do, reason: not valid java name */
    public float mo145do() {
        return this.speed;
    }

    public boolean dp() {
        return true;
    }

    /* JADX INFO: renamed from: bC */
    public void updateUnitMovement() {
    }

    public final UnitPrice dq() {
        return this.unitCustomData;
    }

    public final BaseUnit dr() {
        BaseUnit baseUnit = this.parentEntity;
        if (baseUnit == null && this.unitTransportTarget != null) {
            baseUnit = this.unitTransportTarget;
        }
        return baseUnit;
    }

    public void f(float f, float f2) {
        this.posX = f;
        this.posY = f2;
        c(true);
    }
}
