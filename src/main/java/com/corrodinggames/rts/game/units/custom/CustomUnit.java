package com.corrodinggames.rts.game.units.custom;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.ScorchMark;
import com.corrodinggames.rts.game.units.AttackMode;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.CommandType;
import com.corrodinggames.rts.game.units.FireUnit;
import com.corrodinggames.rts.game.units.MovableUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.TransportUnitInterface;
import com.corrodinggames.rts.game.units.UnitBehaviorType;
import com.corrodinggames.rts.game.units.UnitCommand;
import com.corrodinggames.rts.game.units.UnitCommandType;
import com.corrodinggames.rts.game.units.UnitMovementData;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.UnitPathPoints;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface;
import com.corrodinggames.rts.game.units.buildings.FactoryQueueManager;
import com.corrodinggames.rts.game.units.buildings.FactoryWithQueue;
import com.corrodinggames.rts.game.units.buildings.NukeLauncher;
import com.corrodinggames.rts.game.units.custom.hooks.AttachmentManagerHook;
import com.corrodinggames.rts.game.units.custom.hooks.AttachmentSlotDefinition;
import com.corrodinggames.rts.game.units.custom.hooks.CustomUnitLegController;
import com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook;
import com.corrodinggames.rts.game.units.custom.hooks.LegInstance;
import com.corrodinggames.rts.game.units.custom.logic.ActionType;
import com.corrodinggames.rts.game.units.custom.logic.CustomAction;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.game.units.custom.resources.StoredResources;
import com.corrodinggames.rts.game.units.land.BuilderUnit;
import com.corrodinggames.rts.game.units.land.HovercraftUnit;
import com.corrodinggames.rts.gameFramework.FormationGroup;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.effects.Effect;
import com.corrodinggames.rts.gameFramework.effects.EffectEmitter;
import com.corrodinggames.rts.gameFramework.effects.EffectQuality;
import com.corrodinggames.rts.gameFramework.effects.EffectType;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import com.corrodinggames.rts.gameFramework.utility.Vector3D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/j.class */
public class CustomUnit extends MovableUnit implements TransportUnitInterface, UnitPathPoints, FactoryQueueInterface {

    /* JADX INFO: renamed from: a */
    public int animationFrameIndex;

    /* JADX INFO: renamed from: b */
    public final CustomUnitAnimationController animationController;

    /* JADX INFO: renamed from: c */
    float currentFrameTime;

    /* JADX INFO: renamed from: d */
    float frameAnimationSpeed;

    /* JADX INFO: renamed from: e */
    float frameAnimationTimer;

    /* JADX INFO: renamed from: f */
    public float frameAnimationDelay;

    /* JADX INFO: renamed from: g */
    boolean frameAnimationLooping;

    /* JADX INFO: renamed from: h */
    public boolean frameAnimationPlaying;

    /* JADX INFO: renamed from: i */
    public boolean frameAnimationReverse;

    /* JADX INFO: renamed from: j */
    float unknownFloat1;

    /* JADX INFO: renamed from: k */
    boolean unknownBoolean1;

    /* JADX INFO: renamed from: l */
    boolean unknownBoolean2;

    /* JADX INFO: renamed from: m */
    float unknownFloat2;

    /* JADX INFO: renamed from: n */
    boolean isUnloading;
    float o;
    public boolean p;
    float q;
    boolean r;
    float s;
    float t;
    public float u;
    public boolean v;
    float w;

    /* JADX INFO: renamed from: x */
    public CustomUnitConfig unitConfig;
    public UnitStats y;

    /* JADX INFO: renamed from: z */
    public CustomUnitConfig customUnitCOnfig;

    /* JADX INFO: renamed from: A */
    public CustomUnitDataField[] attachmentPoints;

    /* JADX INFO: renamed from: B */
    public final FastArrayList<BaseUnit> transportedUnits;
    public FastArrayList<BaseUnit> C;
    public float D;
    PointF[] E;
    PointF[] F;
    Projectile[] G;
    Paint J;

    /* JADX INFO: renamed from: dL */
    final FactoryQueueManager unitEffectManager;
    public static PointF dM;
    public static BaseUnit dN;
    public static int dO;
    public float dP;
    public float dQ;
    public float dR;
    public float dS;

    /* JADX INFO: renamed from: dT */
    public LegInstance[] legInstances;
    static boolean dY;
    static final HashMap dZ;
    static int ea;
    static String eb;
    static final PointF ec;

    /* JADX INFO: renamed from: ed */
    AnimationSet currentActionHandler;
    protected static final Vector3D ee;
    protected static final PointF ef;
    FastArrayList eg;
    static final PointF H = new PointF();
    static final PointF I = new PointF();
    protected static final PointF K = new PointF();
    protected static final Vector3D dK = new Vector3D();
    static ArrayList dU = new ArrayList();
    public static FastArrayList dV = new FastArrayList();
    public static FastArrayList dW = new FastArrayList();
    public static FastArrayList dX = new FastArrayList();

    /* JADX INFO: renamed from: K */
    public void getMaxTransportedUnits() {
        this.E = new PointF[6];
        this.F = new PointF[this.E.length];
        for (int i = 0; i < this.E.length; i++) {
            this.E[i] = new PointF();
            this.F[i] = new PointF();
        }
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] b() {
        if (this.E == null) {
            getMaxTransportedUnits();
        }
        return this.E;
    }

    @Override // com.corrodinggames.rts.game.units.UnitPathPoints
    public PointF[] e_() {
        if (this.E == null) {
            getMaxTransportedUnits();
        }
        return this.F;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(11);
        gameOutputStream.writeFloat(this.frameAnimationTimer);
        gameOutputStream.writeFloat(this.unknownFloat2);
        gameOutputStream.writeBoolean(this.isUnloading);
        gameOutputStream.writeInt(this.transportedUnits.size());
        Iterator it = this.transportedUnits.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeUnitIdOrNullBaseUnit((BaseUnit) it.next());
        }
        gameOutputStream.writeBoolean(this.r);
        gameOutputStream.writeFloat(this.o);
        gameOutputStream.writeFloat(this.frameAnimationDelay);
        gameOutputStream.writeFloat(this.s);
        gameOutputStream.writeBoolean(this.v);
        byte length = 0;
        if (this.legInstances != null) {
            length = (byte) this.legInstances.length;
        }
        gameOutputStream.writeByte(length);
        if (this.legInstances != null) {
            for (int i = 0; i < length; i++) {
                LegInstance legInstance = this.legInstances[i];
                gameOutputStream.writeFloat(legInstance.b);
                gameOutputStream.writeFloat(legInstance.c);
                gameOutputStream.writeFloat(legInstance.d);
                gameOutputStream.writeFloat(legInstance.i);
                gameOutputStream.writeBoolean(legInstance.k);
                gameOutputStream.writeBoolean(legInstance.j);
                gameOutputStream.writeBoolean(legInstance.m);
                gameOutputStream.writeBoolean(legInstance.n);
            }
        }
        this.unitEffectManager.a(gameOutputStream);
        gameOutputStream.writeUnitTypeId(this.customUnitCOnfig);
        gameOutputStream.writeBoolean(this.frameAnimationReverse);
        gameOutputStream.writeBoolean(this.frameAnimationPlaying);
        boolean z = this.currentActionHandler != this.unitConfig.image_shield;
        gameOutputStream.writeBoolean(z);
        if (z) {
            AnimationTag.a(this.currentActionHandler, gameOutputStream);
        }
        UnitStats.a(this.y, this, gameOutputStream);
        gameOutputStream.writeFloat(this.q);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        UnitType unitTypeQ;
        byte b;
        LegInstance legInstance;
        if (gameInputStream.getProtocolVersion() >= 32) {
            int i = gameInputStream.readInt();
            this.frameAnimationTimer = gameInputStream.readFloat();
            this.unknownFloat2 = gameInputStream.readFloat();
            this.isUnloading = gameInputStream.readBoolean();
            this.transportedUnits.clear();
            int i2 = gameInputStream.readInt();
            for (int i3 = 0; i3 < i2; i3++) {
                BaseUnit baseUnit = gameInputStream.readBaseUnit();
                if (baseUnit != null) {
                    this.transportedUnits.add(baseUnit);
                }
            }
            if (i >= 1) {
                this.r = gameInputStream.readBoolean();
            }
            if (i >= 2) {
                this.o = gameInputStream.readFloat();
            }
            if (i >= 3) {
                this.frameAnimationDelay = gameInputStream.readFloat();
                this.s = gameInputStream.readFloat();
            }
            if (i >= 4) {
                this.v = gameInputStream.readBoolean();
            }
            if (i >= 5 && (b = gameInputStream.readByte()) != 0) {
                du();
                if (this.legInstances == null) {
                    GameEngine.isInSpace("readIn: legs==null but leg data found in save, this might be due to missing or changed mods");
                }
                for (int i4 = 0; i4 < b; i4++) {
                    if (this.legInstances == null) {
                        legInstance = new LegInstance();
                    } else if (i4 >= this.legInstances.length) {
                        GameEngine.isInSpace("readIn: legs " + i4 + ">=" + this.legInstances.length);
                        legInstance = new LegInstance();
                    } else {
                        legInstance = this.legInstances[i4];
                    }
                    LegInstance legInstance2 = legInstance;
                    legInstance2.b = gameInputStream.readFloat();
                    legInstance2.c = gameInputStream.readFloat();
                    legInstance2.d = gameInputStream.readFloat();
                    legInstance2.i = gameInputStream.readFloat();
                    legInstance2.k = gameInputStream.readBoolean();
                    legInstance2.o = true;
                    legInstance2.j = gameInputStream.readBoolean();
                    legInstance2.m = gameInputStream.readBoolean();
                    legInstance2.n = gameInputStream.readBoolean();
                }
            }
            if (i >= 6) {
                this.unitEffectManager.a(gameInputStream);
            }
            if (i >= 7 && (unitTypeQ = gameInputStream.q()) != null) {
                if (unitTypeQ instanceof CustomUnitConfig) {
                    this.customUnitCOnfig = (CustomUnitConfig) unitTypeQ;
                } else {
                    GameEngine.updatePaintTextSizeIfNeeded("Got non CustomUnitMetadata object of:" + unitTypeQ.getUnitTypeDescriptionShort() + " loading real_meta");
                    this.customUnitCOnfig = CustomUnitConfig.instance;
                }
            }
            if (i >= 8) {
                this.frameAnimationReverse = gameInputStream.readBoolean();
                this.frameAnimationPlaying = gameInputStream.readBoolean();
            }
            if (i >= 9 && gameInputStream.readBoolean()) {
                a(AnimationTag.a(gameInputStream), true);
            }
            if (i >= 10) {
                UnitStats.a(this, gameInputStream, i);
            }
            if (i >= 11) {
                this.q = gameInputStream.readFloat();
            }
        }
        super.a(gameInputStream);
        if (this.legInstances != null) {
            this.dP = this.posX;
            this.dQ = this.posY;
            this.dR = this.posZ;
            this.dS = this.rotationSpeed;
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cr */
    public boolean getUnitAIPathfindCost() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags > 0;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.TransportUnitInterface
    /* JADX INFO: renamed from: bB */
    public int getTransportedUnitCount() {
        return this.transportedUnits.size();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.TransportUnitInterface
    /* JADX INFO: renamed from: bA */
    public boolean isTransportUnloadingActive() {
        return this.isUnloading;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean d(BaseUnit baseUnit, boolean z) {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags == 0 || this.isUnloading || this.deceleration < 1.0f || !getUnitArmor(baseUnit) || baseUnit == this) {
            return false;
        }
        if (this.team != baseUnit.team && !z && (!this.unitConfig.canBeBuiltByOrTagsAndLogicAndTags || this.team != PlayerTeam.TEAM_ALL)) {
            return false;
        }
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogicAndTags != null && !this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogicAndTags.a() && !AnimationTag.a(this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogicAndTags, baseUnit.getUnitCombatAnimation())) {
            return false;
        }
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogicOrTags.size() > 0) {
            boolean z2 = false;
            Iterator it = this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogicOrTags.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                if (((UnitMovementType) it.next()) == baseUnit.h()) {
                    z2 = true;
                    break;
                }
            }
            if (!z2) {
                return false;
            }
        }
        return GameViewUtils.a(baseUnit, this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogicAndTags, this.unitConfig.f8eS);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e(BaseUnit baseUnit, boolean z) {
        if (!d(baseUnit, z)) {
            return false;
        }
        loadTransportedUnit(baseUnit);
        return true;
    }

    /* JADX INFO: renamed from: C */
    public void loadTransportedUnit(BaseUnit baseUnit) {
        baseUnit.unitTransportTarget = this;
        this.transportedUnits.add(baseUnit);
        if (this.unitConfig.canBeBuiltByAndTagsAndLogicOrTags && this.team == PlayerTeam.TEAM_ALL) {
            isSelectable(baseUnit.team);
        }
        a(UnitEventType.transportingNewUnit, baseUnit);
        baseUnit.a(UnitEventType.enteredTransport, this);
        GameEngine.getInstance().gameUI.deselectUnit(baseUnit);
    }

    /* JADX INFO: renamed from: D */
    public void unloadTransportedUnit(BaseUnit baseUnit) {
        a(UnitEventType.transportUnloadedOrRemovedUnit, baseUnit);
        baseUnit.a(UnitEventType.leftTransport, this);
        if (this.unitConfig.canBeBuiltByAndTagsAndLogicAndTags && this.transportedUnits.size() == 0) {
            isSelectable(PlayerTeam.TEAM_ALL);
        }
    }

    @Override // com.corrodinggames.rts.game.units.TransportUnitInterface
    public void e(BaseUnit baseUnit) {
        if (baseUnit.unitTransportTarget == this) {
            this.transportedUnits.remove(baseUnit);
            baseUnit.unitTransportTarget = null;
            unloadTransportedUnit(baseUnit);
            return;
        }
        GameEngine.logWarningAndStack("Unit is not being transported");
    }

    /* JADX INFO: renamed from: L */
    public void getTransportedUnits() {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags == 0) {
            return;
        }
        this.isUnloading = true;
        this.unknownFloat2 = 30.0f;
    }

    /* JADX INFO: renamed from: M */
    public void getLoadedUnits() {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags == 0) {
            return;
        }
        this.isUnloading = false;
    }

    public boolean g(boolean z) {
        if (this.transportedUnits.size() == 0) {
            return false;
        }
        return b((BaseUnit) this.transportedUnits.remove(this.transportedUnits.size() - 1), z, this.transportedUnits.size() % 2 == 0);
    }

    /* JADX INFO: renamed from: a */
    public boolean canAttackTargetUnit(BaseUnit baseUnit, boolean z, boolean z2) {
        this.transportedUnits.remove(baseUnit);
        return b(baseUnit, z, z2);
    }

    private boolean b(BaseUnit baseUnit, boolean z, boolean z2) {
        boolean z3;
        float fFloatValue = 180.0f;
        if (this.unitConfig.spawnAngleOffset != null) {
            fFloatValue = this.unitConfig.spawnAngleOffset.floatValue();
        }
        float fFloatValue2 = 70.0f;
        if (this.unitConfig.spawnSideOffset != null) {
            fFloatValue2 = this.unitConfig.spawnSideOffset.floatValue();
        }
        float f = this.unitConfig.spawnOffsetLocalX;
        float f2 = this.unitConfig.spawnOffsetLocalY;
        float fFastCos = (this.posX + (Utility.fastCos(this.rotationSpeed + fFloatValue) * f2)) - (Utility.fastSin(this.rotationSpeed + fFloatValue) * f);
        float fFastSin = this.posY + (Utility.fastSin(this.rotationSpeed + fFloatValue) * f2) + (Utility.fastCos(this.rotationSpeed + fFloatValue) * f);
        float fFastCos2 = fFastCos + (Utility.fastCos(this.rotationSpeed + 90.0f) * (z2 ? -7.0f : 7.0f));
        float fFastSin2 = fFastSin + (Utility.fastSin(this.rotationSpeed + 90.0f) * (z2 ? -7.0f : 7.0f));
        if (!z && !bI()) {
            if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin2)) {
                fFastCos2 += 10.0f;
            }
            if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin2)) {
                fFastCos2 -= 20.0f;
            }
            if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin2)) {
                fFastCos2 -= 10.0f;
                fFastSin2 += 10.0f;
            }
            if (!GameViewUtils.a(baseUnit, fFastCos2, fFastSin2)) {
                fFastSin2 -= 20.0f;
            }
        }
        if (!z && !GameViewUtils.a(baseUnit, fFastCos2, fFastSin2) && !bI()) {
            z3 = false;
        } else {
            z3 = true;
            boolean z4 = false;
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if (orderableUnit.parentEntity == this) {
                    AttachmentSlotDefinition attachmentSlotDefinitionDn = orderableUnit.dn();
                    if (attachmentSlotDefinitionDn == null) {
                        GameEngine.isInSpace("Unload, attachment data is null");
                    }
                    if (attachmentSlotDefinitionDn != null) {
                        z4 = attachmentSlotDefinitionDn.E;
                    }
                }
            }
            GameViewUtils.a(baseUnit, this);
            float f3 = this.rotationSpeed + fFloatValue;
            if (!z4) {
                baseUnit.posX = fFastCos2;
                baseUnit.posY = fFastSin2;
                baseUnit.rotationSpeed = f3;
                baseUnit.velocityY = 0.0f;
                baseUnit.velocityX = 0.0f;
                baseUnit.worldX = 0.0f;
                baseUnit.worldY = 0.0f;
                baseUnit.worldX += 0.1f;
            }
            baseUnit.attackTargetUnit = this;
            baseUnit.bS = 45.0f;
            if (z4) {
                baseUnit.bS = 85.0f;
            }
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit2 = (OrderableUnit) baseUnit;
                if (!z4) {
                    orderableUnit2.j(baseUnit.rotationSpeed);
                }
                if (!this.unitConfig.f12eW.read(this)) {
                    orderableUnit2.clearAllWaypoints();
                    if (fFloatValue2 != 0.0f) {
                        orderableUnit2.appendMoveWaypoint(baseUnit.posX + (Utility.fastCos(f3 + (z2 ? -7.0f : 7.0f)) * fFloatValue2), baseUnit.posY + (Utility.fastSin(f3 + (z2 ? -7.0f : 7.0f)) * fFloatValue2));
                    }
                    orderableUnit2.waypointSyncGroupId = 0;
                } else {
                    orderableUnit2.clearPathData();
                }
            }
            if (!z4) {
                if (!this.unitConfig.keepOriginalZOnSpawn) {
                    baseUnit.posZ = this.posZ;
                }
                baseUnit.posZ += this.unitConfig.spawnOffsetZ;
            }
            if (baseUnit instanceof CustomUnit) {
                ((CustomUnit) baseUnit).dF();
            }
        }
        if (!z3) {
            this.transportedUnits.add(baseUnit);
        } else {
            unloadTransportedUnit(baseUnit);
        }
        return z3;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bY */
    public int getTransportedUnitsWeight() {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags == 0 || !this.unitConfig.canBuildOnWater) {
            return -1;
        }
        return dI();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bZ */
    public int getMaxTransportWeight() {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags == 0 || !this.unitConfig.canBuildOnWater) {
            return -1;
        }
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags;
    }

    public void ds() {
        if (this.transportedUnits.size > 0) {
            h(this.unitConfig.f11eV.read(this));
        }
    }

    public void h(boolean z) {
        for (BaseUnit baseUnit : this.transportedUnits) {
            baseUnit.unitTransportTarget = null;
            baseUnit.posX = this.posX + (Utility.fastCos(this.rotationSpeed) * (-9.0f));
            baseUnit.posY = this.posY + (Utility.fastSin(this.rotationSpeed) * (-9.0f));
            if (z) {
                baseUnit.getUnitAIConditionTime();
            }
        }
        this.transportedUnits.clear();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public void bu() {
        if (!this.isDestroyed) {
            a(UnitEventType.destroyed);
        }
        Object[] objArrA = this.unitConfig.onCreateListeners.a();
        for (int i = this.unitConfig.onCreateListeners.size - 1; i >= 0; i--) {
            ((CustomUnitRenderHook) objArrA[i]).b(this);
        }
        super.bu();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.SizedObject, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void remove() {
        ds();
        Object[] objArrA = this.unitConfig.onCreateListeners.a();
        for (int i = this.unitConfig.onCreateListeners.size - 1; i >= 0; i--) {
            ((CustomUnitRenderHook) objArrA[i]).c(this);
        }
        PlayerTeam.a((BaseUnit) this);
        this.unitEffectManager.a(true);
        super.remove();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: dt, reason: merged with bridge method [inline-methods] */
    public CustomUnitConfig r() {
        return this.unitConfig;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.OrderableUnit
    public boolean I() {
        if (this.unitConfig.isBuildingUnit || this.frameAnimationLooping || this.parentEntity != null) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: aR */
    public boolean canExecuteMovementCommands() {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsOrLogic) {
            return false;
        }
        AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
        if (attachmentSlotDefinitionDn != null && !attachmentSlotDefinitionDn.O) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean aS() {
        if (this.unitConfig.isBuildingUnit) {
            return false;
        }
        AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
        if (attachmentSlotDefinitionDn != null && !attachmentSlotDefinitionDn.H) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType h() {
        return this.unitConfig.f19fh;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return this.unitConfig.f19fh == UnitMovementType.AIR && this.posZ >= 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean Q() {
        return this.posZ <= -1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cv */
    public boolean getUnitAIPathfindTimeout() {
        if (h() == UnitMovementType.WATER) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ct */
    public boolean getUnitAIPathfindMaxDepth() {
        if (h() == UnitMovementType.AIR) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1 || this.unitConfig.energy == null) {
            return null;
        }
        return this.unitConfig.energy[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed && this.unitConfig.maxShield != null) {
            return this.unitConfig.maxShield;
        }
        return this.unitConfig.startShieldAtZero[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return this.unitConfig.shieldRegenMoving;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && (!this.isDestroyed || this.posZ >= 1.0f) && this.posZ >= -1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (turretConfig.logicCondition != null && turretConfig.logicCondition.read(this)) {
            return null;
        }
        if (turretConfig.textureArray != null) {
            return turretConfig.textureArray[this.team.getTeamColorIndex()];
        }
        if (turretConfig.texture != null) {
            return turretConfig.texture;
        }
        if (this.unitConfig.maxEnergy != null) {
            return this.unitConfig.maxEnergy[this.team.getTeamColorIndex()];
        }
        return this.unitConfig.shieldRegen;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float h(int i) {
        return this.unitConfig.turrets[i].shootSpeed6;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float i(int i) {
        return this.unitConfig.turrets[i].shootAccuracy3;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        PlayerTeam playerTeam;
        GameEngine gameEngine = GameEngine.getInstance();
        if (bI()) {
            gameEngine.pathfindingEngine.a(this);
        }
        if (this.deceleration < 1.0f) {
            if (this.unitConfig.canPatrol != null && this.unitConfig.canPatrol.b()) {
                this.unitConfig.canPatrol.a(this.posX, this.posY, this.posZ, this.rotationSpeed, null);
                return false;
            }
            if (this.unitConfig.canAttack != null && this.unitConfig.canAttack.b()) {
                this.unitConfig.canAttack.a(this.posX, this.posY, this.posZ, this.rotationSpeed, null);
                return false;
            }
            a(UnitSize.verysmall);
            return false;
        }
        if (!this.unitConfig.f20fi) {
            S(0);
        }
        if (this.unitConfig.canAttack != null && this.unitConfig.canAttack.b()) {
            this.unitConfig.canAttack.a(this.posX, this.posY, this.posZ, this.rotationSpeed, null);
        }
        if (this.unitConfig.canAttackUnderwater != -1) {
            a((BaseUnit) null, this.posX, this.posY, this.unitConfig.canAttackUnderwater, (CustomProjectileTemplate) null, 0);
        }
        if (this.unitConfig.canAttackNotTouchingWater != null) {
            if (this.unitConfig.targetGround && this.unitTarget1 != null && this.unitTarget1.team != null) {
                playerTeam = this.unitTarget1.team;
            } else {
                playerTeam = this.team;
            }
            if (!playerTeam.isTeamVictory) {
                this.unitConfig.canAttackNotTouchingWater.a(this.posX, this.posY, this.posZ, this.rotationSpeed, playerTeam, this.isSelected, this);
            }
        }
        this.isAttacking = false;
        if (!this.unitConfig.f20fi) {
            ds();
        }
        if (this.unitConfig.displayFootprint) {
            boolean z = false;
            if (this.unitConfig.pylonBuildWith && gameEngine.isInGameOrLobby() && gameEngine.networkEngine.roomSettings.noNukes) {
                z = true;
            }
            if (!z) {
                Projectile projectileA = NukeLauncher.a(this, this.posX, this.posY, this.posX, this.posY);
                projectileA.aH = false;
                projectileA.Z = this.unitConfig.constructionPylon;
                projectileA.Y = this.unitConfig.pylonRadius;
            }
        }
        if (this.unitConfig.footprint != 0) {
            FireUnit fireUnit = new FireUnit(false);
            fireUnit.posX = this.posX;
            fireUnit.posY = this.posY;
            fireUnit.setUnitTeam(PlayerTeam.TEAM_UNKNOWN);
            PlayerTeam.c(fireUnit);
        }
        if (this.unitConfig.canAttackFlying != null) {
            this.unitConfig.canAttackFlying.a(this.posX, this.posY, 1.0f);
        }
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsOrLogicAndTags) {
            if (this.unitConfig.canAttackFlying == null) {
                gameEngine.soundEngine.playSound(SoundEngine.bugDieSound, 0.8f, this.posX, this.posY);
            }
            if (this.unitConfig.softRadius) {
                if (!i() && !this.unitConfig.f6eJ) {
                    ScorchMark.a(this, 1);
                }
                if (this.unitConfig.unitSize != null) {
                    a(this.unitConfig.unitSize, true);
                } else if (this.unitConfig.isConstructionBox) {
                    a(UnitSize.large);
                }
            }
            if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicOrTags) {
                for (int i = 0; i < bp(); i++) {
                    gameEngine.effectManager.createBloodEffect(this.posX, this.posY, this.posZ);
                }
            }
        } else {
            if (this.legInstances != null) {
                dv();
                for (int i2 = 0; i2 < this.legInstances.length; i2++) {
                    LegInstance legInstance = this.legInstances[i2];
                    LegConfig legConfig = this.unitConfig.energyDisplayName[i2];
                    float f = this.posX + legInstance.b;
                    float f2 = this.posY + legInstance.c;
                    if (legConfig.J && !legConfig.p && (legConfig.q == null || !legConfig.q.read(this))) {
                        if (!GameViewUtils.d(f, f2) && !this.unitConfig.f6eJ) {
                            ScorchMark.a(f, f2);
                        }
                        gameEngine.effectManager.createSmallExplosion(f, f2, 0.0f);
                    }
                }
            }
            if (!i()) {
                if (this.unitConfig.softRadius) {
                    if (this.unitConfig.unitSize != null) {
                        a(this.unitConfig.unitSize, true);
                    } else {
                        a(UnitSize.small);
                    }
                }
            } else {
                if (this.unitConfig.softRadius) {
                    if (this.unitConfig.unitSize != null) {
                        a(this.unitConfig.unitSize, false);
                    } else {
                        gameEngine.effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
                    }
                }
                for (int i3 = 0; i3 < bp(); i3++) {
                    gameEngine.effectManager.createBloodEffect2(this.posX, this.posY, this.posZ);
                }
            }
        }
        if (this.unitConfig.f20fi) {
            return true;
        }
        if (this.unitConfig.maxShield != null) {
            this.baseTexture = this.unitConfig.maxShield;
            this.animationFrameIndex = 0;
            this.ew = true;
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void bq() {
        if (!this.unitConfig.f6eJ) {
            super.bq();
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void U() {
        super.U();
        for (int i = 0; i < this.unitConfig.turrets.length; i++) {
            UnitMovementData unitMovementData = this.movementLevels[i];
            GameEngine.isInSpace("Dir was:" + unitMovementData.targetX + " for name:" + this.unitConfig.turrets[i].name);
            GameEngine.isInSpace("lockDelay:" + unitMovementData.velocityY + " shootCooldown:" + unitMovementData.rotation);
            GameEngine.isInSpace("updateAndShouldResetTurret:" + b(i, 0.0f));
            float fC = C(i);
            GameEngine.isInSpace("idleDir:" + fC);
            GameEngine.isInSpace("diffDir:" + Utility.endsWith(unitMovementData.targetX, fC, 360.0f));
        }
    }

    public void a(CustomUnitConfig customUnitConfig, boolean z, boolean z2) {
        a(customUnitConfig, z, z2, (CustomUnitDataField[]) null);
    }

    public void a(CustomUnitConfig customUnitConfig, boolean z, boolean z2, CustomUnitDataField[] customUnitDataFieldArr) {
        GameEngine gameEngine = GameEngine.getInstance();
        CustomUnitConfig customUnitConfig2 = this.unitConfig;
        UnitStats unitStats = this.y;
        this.unitConfig = customUnitConfig;
        this.unitType = this.unitConfig;
        this.y = customUnitConfig.canBeBuiltByOrTagsAndLogicAndTagsOrLogic;
        if (z2) {
            UnitStats.a(this, unitStats, customUnitConfig2);
        } else if (customUnitDataFieldArr != null) {
            UnitStats.a(this, unitStats, customUnitDataFieldArr);
        }
        bS();
        if (!z2) {
            j(true);
        }
        if (!z) {
            if (customUnitConfig.turrets.length > 1) {
                boolean z3 = true;
                if (customUnitConfig.turrets.length != customUnitConfig2.turrets.length) {
                    z3 = false;
                } else {
                    int i = 0;
                    while (true) {
                        if (i >= customUnitConfig.turrets.length) {
                            break;
                        }
                        if (customUnitConfig.turrets[i].name.equalsIgnoreCase(customUnitConfig2.turrets[i].name)) {
                            i++;
                        } else {
                            z3 = false;
                            break;
                        }
                    }
                }
                if (!z3) {
                    UnitMovementData[] unitMovementDataArr = new UnitMovementData[customUnitConfig.turrets.length];
                    for (int i2 = 0; i2 < customUnitConfig.turrets.length; i2++) {
                        TurretConfig turretConfig = customUnitConfig.turrets[i2];
                        TurretConfig turretConfigFindProjectileConfigByName = customUnitConfig2.findProjectileConfigByName(turretConfig.name);
                        if (turretConfigFindProjectileConfigByName != null) {
                            unitMovementDataArr[turretConfig.rotationSpeed] = this.movementLevels[turretConfigFindProjectileConfigByName.rotationSpeed];
                            this.movementLevels[turretConfigFindProjectileConfigByName.rotationSpeed] = null;
                        }
                    }
                    for (int i3 = 0; i3 < customUnitConfig.turrets.length; i3++) {
                        if (unitMovementDataArr[i3] == null) {
                            int i4 = 0;
                            while (true) {
                                if (i4 >= customUnitConfig.turrets.length) {
                                    break;
                                }
                                if (this.movementLevels[i4] == null) {
                                    i4++;
                                } else {
                                    unitMovementDataArr[i3] = this.movementLevels[i4];
                                    this.movementLevels[i4] = null;
                                    break;
                                }
                            }
                            if (unitMovementDataArr[i3] == null) {
                                unitMovementDataArr[i3] = new UnitMovementData();
                            }
                            unitMovementDataArr[i3].a(this.rotationSpeed);
                        }
                    }
                    this.movementLevels = unitMovementDataArr;
                }
            }
        } else {
            for (int i5 = 0; i5 < customUnitConfig.turrets.length; i5++) {
                this.movementLevels[i5].a(this.rotationSpeed + B(i5));
            }
        }
        if (this.unitConfig.hp) {
            V(this.unitConfig.maxHp);
            W(this.unitConfig.mass);
        } else {
            T(this.unitConfig.maxHp);
            U(this.unitConfig.mass);
        }
        this.ew = false;
        this.speed = this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsAndLogic;
        this.maxSpeed = this.unitConfig.canBeBuiltByOrTagsAndLogicAndTagsAndLogicAndTags;
        if (this.isDestroyed) {
            this.animationFrameIndex = 0;
        } else {
            this.animationFrameIndex = this.unitConfig.lockTurretWithBody;
        }
        this.frameAnimationLooping = false;
        if (z) {
            this.posZ += this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogic;
        }
        float f = this.maxHealth;
        this.maxHealth = this.y.maxHp;
        if (z || f == 0.0f) {
            o(this.maxHealth);
        } else {
            o((this.currentHealth / f) * this.maxHealth);
        }
        float f2 = this.unitEnergyMax;
        this.unitEnergyMax = this.y.maxShield;
        if (this.unitConfig.canBeBuiltByAndTagsAndLogicOrTagsAndLogic) {
            if (this.shield > this.unitEnergyMax) {
                this.shield = this.unitEnergyMax;
            }
        } else if (z || f2 == 0.0f) {
            this.shield = this.unitEnergyMax;
        } else {
            this.shield = (this.shield / f2) * this.unitEnergyMax;
        }
        if (this.unitConfig.canBeBuiltByAndTagsAndLogicAndTagsAndLogic) {
            if (this.f0cB > this.y.maxEnergy) {
                this.f0cB = this.y.maxEnergy;
            }
        } else if (z) {
            this.f0cB = this.y.maxEnergy * this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogic;
        } else if (unitStats.maxEnergy == 0.0f) {
            this.f0cB = this.y.maxEnergy;
        } else {
            this.f0cB = (this.f0cB / unitStats.maxEnergy) * this.y.maxEnergy;
        }
        if (this.team == null) {
            this.baseTexture = this.unitConfig.baseTexture;
        } else {
            S();
        }
        if (this.unitConfig.isBuildingUnit && z) {
            this.rotationSpeed = -90.0f;
        }
        f_();
        if (!z && ((this.unitData1 != null || this.unitData2 != null) && (!UnitPrice.b(this.unitConfig.buildPrice, customUnitConfig2.buildPrice) || !UnitPrice.b(this.unitConfig.streamingPrice, customUnitConfig2.streamingPrice)))) {
            this.unitData1 = null;
            this.unitData2 = null;
        }
        if (!z) {
            boolean z4 = this.unitConfig.j() != customUnitConfig2.j();
            if (this.unitConfig.j()) {
                this.velocityY = 0.0f;
                this.velocityX = 0.0f;
                if (customUnitConfig2.j() && !this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogic.equals(customUnitConfig2.canBeBuiltByAndTagsOrLogicAndTagsOrLogic)) {
                    z4 = true;
                }
            }
            if (z4) {
                gameEngine.pathfindingEngine.a(this);
            }
        }
        this.unknownFloat1 = 1.0f;
        if (this.unitConfig.canBeBuiltByOrTagsAndLogicOrTagsAndLogic != -2) {
        }
        if (!this.isDestroyed) {
            dF();
        }
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicAndTags) {
        }
        du();
        getUnitCombatCooldown().a(this.unitConfig);
        if (!z) {
            int techLevel = getTechLevel();
            for (int i6 = 0; i6 < techLevel; i6++) {
                UnitMovementData unitMovementData = this.movementLevels[i6];
                TurretConfig turretConfig2 = this.unitConfig.turrets[i6];
                if (turretConfig2 != null) {
                    if (unitMovementData.rotation > turretConfig2.m) {
                        unitMovementData.rotation = turretConfig2.m;
                    }
                    if (unitMovementData.speed > turretConfig2.n) {
                        unitMovementData.speed = turretConfig2.n;
                    }
                }
            }
        }
        if (!z) {
            if (!this.unitConfig.canBeBuiltByOrTagsAndLogicAndTagsAndLogicOrTags) {
                this.unitEffectManager.b = null;
            }
            if (this.unitConfig.movementType != customUnitConfig2.movementType) {
                clearPathData();
            }
        }
        if (this.unitConfig.canBeBuiltByAndTagsAndLogicAndTags && this.transportedUnits.size() == 0) {
            setUnitTeam(PlayerTeam.TEAM_ALL);
        }
        if (this.isSelected && !gameEngine.gameUI.canUnitBeSelected(this)) {
            gameEngine.gameUI.deselectUnit(this);
        }
        if (!z) {
            Object[] objArrA = this.unitConfig.onCreateListeners.a();
            for (int i7 = this.unitConfig.onCreateListeners.size - 1; i7 >= 0; i7--) {
                ((CustomUnitRenderHook) objArrA[i7]).a(this, customUnitConfig2);
            }
            if (this.deceleration >= 1.0f) {
                if (this.y.fogOfWarSightRange > unitStats.fogOfWarSightRange) {
                    c(false);
                }
            } else {
                if ((this.unitConfig.canBeBuiltByAndTagsAndLogicOrTagsAndLogicAndTags != -1 ? this.unitConfig.canBeBuiltByAndTagsAndLogicOrTagsAndLogicAndTags : this.y.fogOfWarSightRange) > (customUnitConfig2.canBeBuiltByAndTagsAndLogicOrTagsAndLogicAndTags != -1 ? customUnitConfig2.canBeBuiltByAndTagsAndLogicOrTagsAndLogicAndTags : unitStats.fogOfWarSightRange)) {
                    c(false);
                }
            }
        }
        if (this.frameAnimationReverse && this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTags != null) {
            this.animationController.a(this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTags, 7, true);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void f_() {
        if (!this.unitConfig.isBuildingUnit && !this.isDestroyed) {
            this.isAttacking = true;
            if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsOrLogicOrTags) {
                this.isAttacking = false;
            }
        } else {
            this.isAttacking = false;
        }
        if (this.parentEntity != null) {
            this.isAttacking = false;
        }
    }

    public CustomUnit(boolean z, CustomUnitConfig customUnitConfig) {
        super(z);
        this.animationController = new CustomUnitAnimationController(this);
        this.currentFrameTime = 1.0f;
        this.frameAnimationPlaying = true;
        this.frameAnimationReverse = true;
        this.unknownFloat1 = 1.0f;
        this.unknownBoolean1 = false;
        this.p = true;
        this.transportedUnits = new FastArrayList();
        this.C = null;
        this.E = null;
        this.F = null;
        this.unitEffectManager = new FactoryQueueManager(this);
        this.legInstances = null;
        this.eg = new FastArrayList();
        a(customUnitConfig, true, false);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        float fAbs;
        Effect effectCreateSmallExplosion;
        float fA;
        UnitType unitTypeC;
        float fAbs2;
        float fAbs3;
        float f2;
        Effect effectCreateSmokeEffect;
        boolean z = this.frameAnimationReverse;
        if (z) {
            this.frameAnimationReverse = false;
            Object[] objArrA = this.unitConfig.onCreateListeners.a();
            for (int i = this.unitConfig.onCreateListeners.size - 1; i >= 0; i--) {
                ((CustomUnitRenderHook) objArrA[i]).a(this);
            }
            b(UnitEventType.created);
        }
        CustomUnitConfig customUnitConfig = this.unitConfig;
        super.update(f);
        if (this.isDestroyed && !this.unknownBoolean2) {
            if (this.posZ > 0.0f) {
                if (customUnitConfig.f20fi && !(this.rotation == 0.0f && this.velocityX == 0.0f && this.velocityY == 0.0f)) {
                    this.frameAnimationDelay += 0.017f * f;
                    this.posZ -= this.frameAnimationDelay * f;
                    PointF pointFN = getMovementDeltaOffset(f);
                    this.posX += pointFN.x;
                    this.posY += pointFN.y;
                    if (customUnitConfig.movementType == UnitMovementType.AIR) {
                        GameEngine gameEngine = GameEngine.getInstance();
                        this.unitAnimationRotation += f;
                        this.unitAnimationOffset += f;
                        if (customUnitConfig.f21fj && this.unitAnimationRotation > 9.0f) {
                            this.unitAnimationRotation = Utility.randomFloatInRange(1.0f, 3.0f);
                            Effect effectCreateEffectInternal = gameEngine.effectManager.createEffectInternal(this.posX, this.posY, this.posZ, EffectType.custom, false, EffectQuality.low);
                            if (effectCreateEffectInternal != null) {
                                effectCreateEffectInternal.aq = 0;
                                effectCreateEffectInternal.ap = 0;
                                effectCreateEffectInternal.ar = (short) 2;
                                effectCreateEffectInternal.r = true;
                                effectCreateEffectInternal.E = 0.5f;
                                effectCreateEffectInternal.W = 60.0f;
                                effectCreateEffectInternal.V = 60.0f;
                                effectCreateEffectInternal.G = 0.9f;
                                effectCreateEffectInternal.F = 1.2f;
                                effectCreateEffectInternal.as = false;
                                effectCreateEffectInternal.P = 0.0f;
                                effectCreateEffectInternal.Q = 0.0f;
                            }
                        }
                        if (this.unitAnimationOffset > 7.0f) {
                            this.unitAnimationOffset = 0.0f;
                            Effect effectCreateEffectInternal2 = gameEngine.effectManager.createEffectInternal(this.posX, this.posY, this.posZ, EffectType.custom, false, EffectQuality.verylow);
                            if (effectCreateEffectInternal2 != null) {
                                EffectEmitter.b(effectCreateEffectInternal2, true);
                                effectCreateEffectInternal2.I = this.posX;
                                effectCreateEffectInternal2.J = this.posY;
                                effectCreateEffectInternal2.K = this.posZ;
                                effectCreateEffectInternal2.P += Utility.randomFloatInRange(-0.1f, 0.1f) + this.velocityX;
                                effectCreateEffectInternal2.Q += Utility.randomFloatInRange(-0.1f, 0.1f) + this.velocityY;
                                effectCreateEffectInternal2.I += Utility.randomFloatInRange(-4.0f, 4.0f);
                                effectCreateEffectInternal2.J += Utility.randomFloatInRange(-4.0f, 4.0f);
                            }
                        }
                    }
                } else {
                    this.frameAnimationDelay += customUnitConfig.canBeBuiltByAndTagsOrLogicOrTagsAndLogicAndTagsAndLogic * f;
                    this.posZ -= this.frameAnimationDelay * f;
                }
            } else if (!this.unknownBoolean1) {
                this.unknownBoolean1 = true;
                if (customUnitConfig.f20fi) {
                    ds();
                    S(0);
                    if (customUnitConfig.maxShield != null) {
                        this.baseTexture = customUnitConfig.maxShield;
                        this.animationFrameIndex = 0;
                        this.ew = true;
                    } else {
                        getUnitAICondition();
                    }
                }
                if (this.frameAnimationDelay > 0.5d) {
                    if (customUnitConfig.canGuard != null && customUnitConfig.canGuard.a()) {
                        customUnitConfig.canGuard.a(this.posX, this.posY, this.posZ, this.rotationSpeed, null);
                    }
                    if (isMoving()) {
                        if (customUnitConfig.canRepair) {
                            a(UnitSize.verysmall);
                        }
                        if (m147cJ()) {
                            GameEngine.getInstance().effectManager.createDirectedExplosion(this.posX, this.posY, 0.0f, 0, 0.0f, 0.0f, this.rotationSpeed);
                        }
                    } else if (customUnitConfig.canRepair) {
                        a(UnitSize.small);
                    }
                }
                this.frameAnimationDelay = 0.0f;
            } else if (!isMoving()) {
                this.posZ = 0.0f;
                this.unknownBoolean2 = true;
            } else if (this.posZ > -10.0f) {
                this.frameAnimationDelay += 8.0E-4f * f;
                this.posZ -= this.frameAnimationDelay * f;
                if (m147cJ()) {
                    this.t += f;
                    if (this.t > 30.0f) {
                        this.t = 0.0f;
                        if (isBuilding() && (effectCreateSmokeEffect = GameEngine.getInstance().effectManager.createSmokeEffect(this.posX, this.posY, this.posZ, this.rotationSpeed)) != null) {
                            effectCreateSmokeEffect.P = 0.0f;
                            effectCreateSmokeEffect.Q = -0.1f;
                        }
                    }
                }
            } else {
                this.unknownBoolean2 = true;
            }
        }
        if (this.isDestroyed) {
            return;
        }
        GameEngine gameEngine2 = GameEngine.getInstance();
        if (customUnitConfig.isExtractor != 0.0f && this.deceleration < 1.0f) {
            float f3 = this.deceleration + (customUnitConfig.isExtractor * f);
            if (f3 >= 1.0f) {
                PlayerTeam.b((BaseUnit) this);
                this.deceleration = 1.0f;
                this.movementAngle = 1.0f;
                PlayerTeam.c(this);
            } else {
                this.deceleration = f3;
                this.movementAngle = f3;
            }
        }
        if (!isAlive()) {
            if (this.deceleration < 1.0f) {
                if (customUnitConfig.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTags != null) {
                    this.animationController.a(customUnitConfig.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTags, 8);
                    this.animationController.a(f);
                } else if (customUnitConfig.canBeBuiltByAndTagsOrLogicOrTagsAndLogicAndTags != null) {
                    this.animationController.a(customUnitConfig.canBeBuiltByAndTagsOrLogicOrTagsAndLogicAndTags, 99);
                    this.animationController.currentTime = this.deceleration;
                    this.animationController.speedMultiplier = 0.0f;
                    this.animationController.a(f);
                }
            }
            Object[] objArrA2 = customUnitConfig.onCreateListeners.a();
            for (int i2 = customUnitConfig.onCreateListeners.size - 1; i2 >= 0; i2--) {
                ((CustomUnitRenderHook) objArrA2[i2]).a(this, f);
            }
            boolean z2 = true;
            if ((this.deceleration < 1.0f && !customUnitConfig.canBeBuiltByUnit) || (this.unitTransportTarget != null && !customUnitConfig.canBeBuiltBy)) {
                z2 = false;
            }
            if (z2) {
                b(f, z);
                CustomUnitConfig customUnitConfig2 = this.unitConfig;
                return;
            }
            return;
        }
        Object[] objArrA3 = customUnitConfig.onCreateListeners.a();
        for (int i3 = customUnitConfig.onCreateListeners.size - 1; i3 >= 0; i3--) {
            ((CustomUnitRenderHook) objArrA3[i3]).b(this, f);
        }
        if (this.frameAnimationPlaying) {
            this.frameAnimationPlaying = false;
            a(UnitEventType.completeAndActive);
        }
        if (this.y.selfRegenRate != 0.0f && (this.currentHealth < this.maxHealth || this.y.selfRegenRate < 0.0f)) {
            this.currentHealth += this.y.selfRegenRate * f;
            if (this.currentHealth > this.maxHealth) {
                this.currentHealth = this.maxHealth;
            }
        }
        if (this.y.maxEnergy != 0.0f) {
            if (!this.v) {
                f2 = customUnitConfig.canBeBuiltByAndTagsAndLogicAndTagsOrLogic;
            } else {
                f2 = customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogic;
            }
            if (this.f0cB < this.y.maxEnergy || f2 < 0.0f) {
                this.f0cB += f2 * f;
            }
            if (this.f0cB >= this.y.maxEnergy) {
                this.f0cB = this.y.maxEnergy;
                this.v = false;
            }
            if (this.f0cB <= 0.0f) {
                if (customUnitConfig.canNotBeGivenOrdersByPlayer) {
                    bv();
                    return;
                }
                this.f0cB = 0.0f;
            }
        }
        if (this.unitEnergyMax != 0.0f) {
            if (this.y.shieldRegen != 0.0f) {
                this.shield += this.y.shieldRegen * f;
                if (this.shield > this.unitEnergyMax) {
                    this.shield = this.unitEnergyMax;
                }
            }
            if (this.shield < 0.0f) {
                this.shield = 0.0f;
            }
            if (this.unitShieldMax != 0.0f) {
                this.unitShieldMax -= (this.unitShieldMax * 0.02f) * f;
                this.unitShieldMax = Utility.distanceSq(this.unitShieldMax, 0.0f, customUnitConfig.canBeBuiltByAndTagsOrLogicOrTagsOrLogic * f);
                if (this.unitShieldMax > 50.0f) {
                    this.unitShieldMax = 50.0f;
                }
            }
        }
        this.unitEffectManager.a(f);
        CustomUnitConfig customUnitConfig3 = this.unitConfig;
        if (customUnitConfig3.isFlying) {
            BuilderUnit.updateTargetPriorityPoints(f, this);
        }
        if (customUnitConfig3.canBeBuiltByBuilder) {
            this.o += f;
            if (this.o > customUnitConfig3.canBeBuiltByNotTags - 0.1f) {
                this.o -= customUnitConfig3.canBeBuiltByNotTags;
                boolean z3 = customUnitConfig3.canBeBuiltByOrTagsOrLogic.read(this);
                if (this.p != z3) {
                    PlayerTeam.a((BaseUnit) this);
                    this.p = z3;
                    PlayerTeam.c(this);
                }
                if (this.p) {
                    customUnitConfig3.generationTemplate.g(this);
                }
            }
        }
        if (customUnitConfig3.updateUnitMemoryWriter != null) {
            this.q += f;
            if (this.q >= customUnitConfig3.updateUnitMemoryRate) {
                this.q = 0.0f;
                customUnitConfig3.updateUnitMemoryWriter.writeToUnit(this);
            }
        }
        if (this.isRepairing) {
            this.animationController.a(customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTags, 3);
        } else if (!this.animationController.animationPlaying || this.animationController.currentAnimation == customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTags) {
            this.animationController.a(customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTags, 2);
        }
        if (customUnitConfig3.targetGroundOnlyLast) {
            if (customUnitConfig3.targetAllyTeam && gameEngine2.mouseScreenY) {
                if (this.rotation > 0.0f || (this.rotation < 0.0f && customUnitConfig3.targetEnemyTeam)) {
                    this.t += f;
                }
                if (this.t > 10.0f) {
                    this.t = 0.0f;
                    if (this.flag3 && m147cJ()) {
                        float f4 = this.rotationSpeed + 180.0f;
                        if (this.rotation < 0.0f) {
                            f4 += 180.0f;
                        }
                        float f5 = this.speed - 6.0f;
                        if (f5 < 4.0f) {
                            f5 = 4.0f;
                        }
                        gameEngine2.effectManager.createSmokeEffect(this.posX + (Utility.fastCos(f4) * f5), this.posY + (Utility.fastSin(f4) * f5), 0.0f, f4);
                    }
                }
            }
            if (gameEngine2.mouseScreenX && ((this.rotation > 0.0f || this.rotation < 0.0f) && this.flag3)) {
                this.frameAnimationSpeed += f;
                if (this.frameAnimationSpeed > customUnitConfig3.showOnMinimapToEnemies) {
                    this.frameAnimationSpeed = 0.0f;
                    if (this.rotation > 0.0f) {
                        if (customUnitConfig3.canBeAttacked != null) {
                            customUnitConfig3.canBeAttacked.a(this.posX, this.posY, this.posZ, this.rotationSpeed, this);
                        }
                    } else if (customUnitConfig3.isSelectable != null) {
                        float f6 = this.rotationSpeed;
                        if (customUnitConfig3.showOnMinimap) {
                            f6 += 180.0f;
                        }
                        customUnitConfig3.isSelectable.a(this.posX, this.posY, this.posZ, f6, this);
                    }
                    if (customUnitConfig3.canAttackCondition && ((this.rotation > 0.0f || customUnitConfig3.canAttackWhileGarrisoned) && !m147cJ())) {
                        int i4 = 0;
                        while (i4 <= 1) {
                            float f7 = i4 == 0 ? -20 : 20;
                            float f8 = this.rotationSpeed + 180.0f;
                            if (this.rotation < 0.0f) {
                                f7 += 180.0f;
                                f8 += 180.0f;
                            }
                            Effect effectCreateMuzzleFlash = gameEngine2.effectManager.createMuzzleFlash(this.posX + (Utility.fastCos(this.rotationSpeed + 180.0f + f7) * (this.speed - 1.0f)), this.posY + (Utility.fastSin(this.rotationSpeed + 180.0f + f7) * (this.speed - 1.0f)), this.posZ, f8 + Utility.randomFloatInRange(-7.0f, 7.0f), 0);
                            if (effectCreateMuzzleFlash != null) {
                                effectCreateMuzzleFlash.P += Utility.randomFloatInRange(-0.15f, 0.15f);
                                effectCreateMuzzleFlash.Q += Utility.randomFloatInRange(-0.15f, 0.15f);
                            }
                            i4++;
                        }
                    }
                }
            }
        }
        if (customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags > 0) {
            if (customUnitConfig3.f14eY != 0.0f && this.transportedUnits.size > 0) {
                Object[] objArrA4 = this.transportedUnits.a();
                for (int i5 = 0; i5 < this.transportedUnits.size; i5++) {
                    BaseUnit baseUnit = (BaseUnit) objArrA4[i5];
                    if (baseUnit.currentHealth < baseUnit.maxHealth || customUnitConfig3.f14eY < 0.0f) {
                        baseUnit.currentHealth += customUnitConfig3.f14eY * f;
                        if (baseUnit.currentHealth > baseUnit.maxHealth) {
                            baseUnit.currentHealth = baseUnit.maxHealth;
                        }
                    }
                }
            }
            if (this.isUnloading && customUnitConfig3.f16fc.read(this)) {
                this.unknownFloat2 = Utility.moveTowardsZero(this.unknownFloat2, f);
                if (this.unknownFloat2 == 0.0f) {
                    this.unknownFloat2 = customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicAndTags;
                    if (this.transportedUnits.size() == 0) {
                        this.isUnloading = false;
                    } else {
                        g(false);
                        if (this.transportedUnits.size() == 0) {
                            this.isUnloading = false;
                        }
                    }
                }
            }
        }
        if (this.isRepairing) {
            this.s = 0.0f;
        } else {
            this.s += f;
        }
        if (customUnitConfig3.movementType != UnitMovementType.AIR && this.parentEntity == null) {
            dF();
        }
        if ((customUnitConfig3.movementType == UnitMovementType.OVER_CLIFF || customUnitConfig3.movementType == UnitMovementType.OVER_CLIFF_WATER) && this.isRepairing) {
            if (isUnitTransportCategory()) {
                this.unknownFloat1 = 0.7f;
            } else {
                this.unknownFloat1 = 1.0f;
            }
        }
        if (customUnitConfig3.movementType == UnitMovementType.AIR) {
            this.frameAnimationTimer += 2.0f * f;
            if (this.frameAnimationTimer > 360.0f) {
                this.frameAnimationTimer -= 360.0f;
            }
            boolean zI = i();
            boolean z4 = false;
            if (customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsAndLogic) {
                boolean zCK = isMoving();
                if (!this.isRepairing && !zCK && this.s > 3.0f && (!customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogic || hasNoCurrentWaypoint())) {
                    z4 = true;
                }
            }
            if (this.parentEntity == null) {
                if (z4) {
                    if (customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsAndLogic < 0.0f) {
                        fAbs3 = (Utility.abs(this.posZ - 2.0f) * 0.05f * 0.4f) + 0.2f;
                    } else {
                        fAbs3 = customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsAndLogic;
                    }
                    this.posZ = Utility.distanceSq(this.posZ, 2.0f, fAbs3 * f);
                } else {
                    float fFastSin = this.y.targetHeight + (Utility.fastSin(this.frameAnimationTimer) * customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic);
                    if (customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsAndLogic < 0.0f) {
                        fAbs2 = (Utility.abs(this.posZ - 2.0f) * 0.05f * 0.4f) + 0.2f;
                    } else {
                        fAbs2 = customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsAndLogic;
                    }
                    this.posZ = Utility.distanceSq(this.posZ, fFastSin, Utility.clamp(fAbs2, (Utility.abs(this.posZ - fFastSin) * 0.05f * 0.3f) + 0.1f) * f);
                }
                if (zI != i()) {
                    this.movementActiveThisFrame = true;
                    dF();
                }
            }
        } else {
            float f9 = this.y.targetHeight - customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic;
            if (this.posZ < f9) {
                this.posZ += 0.2f * f;
                if (this.posZ >= f9) {
                    this.posZ = f9;
                }
            }
            if ((this.y.targetHeight != 0.0f || customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic != 0.0f || this.posZ > 0.0f) && this.parentEntity == null) {
                float fFastSin2 = this.y.targetHeight;
                if (customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic != 0.0f) {
                    this.frameAnimationTimer += 2.0f * f;
                    if (this.frameAnimationTimer > 360.0f) {
                        this.frameAnimationTimer -= 360.0f;
                    }
                    fFastSin2 += Utility.fastSin(this.frameAnimationTimer) * customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic;
                }
                if (customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsAndLogic < 0.0f) {
                    fAbs = (Utility.abs(this.posZ - 2.0f) * 0.05f * 0.4f) + 0.2f;
                } else {
                    fAbs = customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsAndLogic;
                }
                this.posZ = Utility.distanceSq(this.posZ, fFastSin2, Utility.clamp(fAbs, (Utility.abs(this.posZ - fFastSin2) * 0.05f * 0.3f) + 0.1f) * f);
                boolean z5 = false;
                if (this.posZ > this.y.targetHeight + customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic + 1.0f) {
                    this.frameAnimationDelay += customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsAndLogicOrTagsOrLogic * f;
                    if (this.posZ < 0.0f) {
                        this.frameAnimationDelay = Utility.clamp(this.frameAnimationDelay, 0.2f);
                    }
                    this.posZ -= this.frameAnimationDelay * f;
                    if (this.frameAnimationDelay > 1.5d) {
                        this.unitAnimationRotation += f;
                        if (this.unitAnimationRotation > 0.5d) {
                            this.unitAnimationRotation = 0.0f;
                            Effect effectCreateEffectInternal3 = gameEngine2.effectManager.createEffectInternal(this.posX + Utility.randomFloatInRange(-this.speed, this.speed), this.posY + Utility.randomFloatInRange(-this.speed, this.speed), this.posZ, EffectType.custom, false, EffectQuality.high);
                            if (effectCreateEffectInternal3 != null) {
                                effectCreateEffectInternal3.aq = 0;
                                effectCreateEffectInternal3.ap = 0;
                                effectCreateEffectInternal3.ar = (short) 2;
                                effectCreateEffectInternal3.r = true;
                                effectCreateEffectInternal3.s = true;
                                effectCreateEffectInternal3.t = 40.0f;
                                effectCreateEffectInternal3.an = true;
                                effectCreateEffectInternal3.P = 0.1f;
                                effectCreateEffectInternal3.R = 0.0f;
                                effectCreateEffectInternal3.u = true;
                                effectCreateEffectInternal3.E = 0.4f;
                                effectCreateEffectInternal3.W = 380.0f;
                                effectCreateEffectInternal3.V = effectCreateEffectInternal3.W;
                                effectCreateEffectInternal3.G = 0.8f;
                                effectCreateEffectInternal3.F = 1.7f;
                                effectCreateEffectInternal3.as = false;
                                effectCreateEffectInternal3.P += Utility.randomFloatInRange(-0.04f, 0.04f);
                                effectCreateEffectInternal3.Q += Utility.randomFloatInRange(-0.04f, 0.04f);
                            }
                        }
                    }
                    if (this.posZ <= this.y.targetHeight + customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic + 1.0f) {
                        if (this.frameAnimationDelay > 2.0f) {
                            z5 = true;
                        }
                        if (this.posZ < this.y.targetHeight + customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic) {
                            this.posZ = this.y.targetHeight + customUnitConfig3.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsOrLogic;
                        }
                        this.frameAnimationDelay = 0.0f;
                    }
                } else {
                    if (this.frameAnimationDelay > 2.0f) {
                        z5 = true;
                    }
                    this.frameAnimationDelay = 0.0f;
                }
                if (z5 && (effectCreateSmallExplosion = gameEngine2.effectManager.createSmallExplosion(this.posX, this.posY, this.posZ, 0)) != null) {
                    effectCreateSmallExplosion.G = 0.8f;
                    effectCreateSmallExplosion.F = 1.4f;
                    effectCreateSmallExplosion.V = 60.0f;
                    effectCreateSmallExplosion.W = effectCreateSmallExplosion.V;
                }
            }
        }
        boolean z6 = false;
        boolean z7 = false;
        if (customUnitConfig3.isFactory) {
            z7 = true;
        }
        if (this.customUnitCOnfig != null && this.customUnitCOnfig.isFactory) {
            z7 = true;
        }
        if (z7) {
            AbstractUnitAction abstractUnitActionD = this.unitEffectManager.d();
            boolean zL = false;
            if (abstractUnitActionD != null) {
                if (abstractUnitActionD instanceof CustomAction) {
                    CustomAction customAction = (CustomAction) abstractUnitActionD;
                    CustomActionDef customActionDef = customAction.a;
                    boolean z8 = false;
                    zL = customAction.L();
                    if (customActionDef.spawnUnitType != null && (unitTypeC = customActionDef.spawnUnitType.c()) != null && (unitTypeC instanceof CustomUnitConfig)) {
                        z6 = true;
                        if (unitTypeC != customUnitConfig3) {
                            if (this.customUnitCOnfig != null) {
                                PlayerTeam.b((BaseUnit) this);
                                a(this.customUnitCOnfig, false, false, this.attachmentPoints);
                                this.customUnitCOnfig = null;
                                this.attachmentPoints = null;
                                customUnitConfig3 = this.unitConfig;
                                PlayerTeam.c(this);
                            }
                            PlayerTeam.b((BaseUnit) this);
                            this.customUnitCOnfig = customUnitConfig3;
                            this.attachmentPoints = customActionDef.setUnitData;
                            a((CustomUnitConfig) unitTypeC, false, false, customActionDef.setUnitData);
                            customUnitConfig3 = this.unitConfig;
                            PlayerTeam.c(this);
                        }
                    }
                    if (customActionDef.animationSpeed != null) {
                        float fFloatValue = customActionDef.animationSpeed.floatValue();
                        if (customActionDef.isAnimation3) {
                            float f10 = this.posX;
                            float f11 = this.posY;
                            com.corrodinggames.rts.game.units.buildings.Projectile projectileB = this.unitEffectManager.b();
                            if (projectileB != null) {
                                float f12 = Float.MIN_VALUE;
                                float f13 = Float.MIN_VALUE;
                                if (projectileB.i != null) {
                                    f12 = projectileB.i.posX;
                                    f13 = projectileB.i.posY;
                                } else if (projectileB.h != null) {
                                    f12 = projectileB.h.x;
                                    f13 = projectileB.h.y;
                                }
                                if (f12 > Float.MIN_VALUE) {
                                    fFloatValue += Utility.getAngleBetweenPoints(f10, f11, f12, f13);
                                }
                            }
                        }
                        if (customActionDef.animation == null) {
                            fA = rotateToward(f, fFloatValue, true, customActionDef.isAnimation);
                        } else {
                            int i6 = customActionDef.animation.rotationSpeed;
                            fA = a(f, fFloatValue, i6);
                            UnitMovementData unitMovementData = this.movementLevels[i6];
                            unitMovementData.b(5);
                            unitMovementData.targetY = unitMovementData.targetX;
                        }
                        if (customActionDef.isAnimation2 && Utility.abs(fA) > 5.0f) {
                            z8 = true;
                        }
                    }
                    if (customActionDef.animationReference != null && !z8) {
                        this.animationController.a(customActionDef.animationReference.b(), 10);
                    }
                    if (z8) {
                        this.unitEffectManager.e = 0.0f;
                    }
                }
                if (customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsOrLogicOrTags != null && abstractUnitActionD.getUnitType() != null && this.unitEffectManager.e >= customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsOrLogicOrTags.q) {
                    this.animationController.a(customUnitConfig3.canBeBuiltByAndTagsOrLogicOrTagsOrLogicOrTags, 5);
                }
            }
            this.frameAnimationLooping = zL;
            if (this.frameAnimationLooping) {
                this.velocityX = 0.0f;
                this.velocityY = 0.0f;
                this.rotation = 0.0f;
            }
        }
        if (this.customUnitCOnfig != null && !z6) {
            PlayerTeam.b((BaseUnit) this);
            a(this.customUnitCOnfig, false, false, this.attachmentPoints);
            this.customUnitCOnfig = null;
            this.attachmentPoints = null;
            CustomUnitConfig customUnitConfig4 = this.unitConfig;
            PlayerTeam.c(this);
        }
        this.animationController.a(f);
        b(f, z);
        CustomUnitConfig customUnitConfig5 = this.unitConfig;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float cy() {
        int i = this.unitConfig.generationTemplate.b;
        if (!this.p) {
            return 0.0f;
        }
        return i * this.unitConfig.canBeBuiltByOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cz */
    public StoredResources getUnitAIPathfindResult() {
        if (!this.p) {
            return StoredResources.a;
        }
        return this.unitConfig.generationCondition;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cA */
    public StoredResources getUnitRotationData() {
        if (!this.p) {
            return StoredResources.a;
        }
        return this.unitConfig.generationTagTemplate;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean a(int i, BaseUnit baseUnit, boolean z, boolean z2) {
        return a(this.unitConfig.turrets[i], i, baseUnit, z, z2);
    }

    public final boolean a(TurretConfig turretConfig, int i, float f, float f2, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        float fDistanceSq = Utility.distanceSq(this.posX, this.posY, f, f2);
        if (fDistanceSq > turretConfig.minRange2) {
            if (this.team == gameEngine.playerTeam) {
                gameEngine.gameUI.showMediumPriorityMessage("Location too far");
                return false;
            }
            return false;
        }
        if (fDistanceSq < turretConfig.ah) {
            if (this.team == gameEngine.playerTeam) {
                gameEngine.gameUI.showMediumPriorityMessage("Location too close");
                return false;
            }
            return false;
        }
        return true;
    }

    public final boolean a(TurretConfig turretConfig, int i, BaseUnit baseUnit, boolean z, boolean z2) {
        float f;
        if (!z && (turretConfig.canSpawn2 || z2)) {
            float fDistanceSq = Utility.distanceSq(this.posX, this.posY, baseUnit.posX, baseUnit.posY);
            if (fDistanceSq > turretConfig.minRange2 || fDistanceSq < turretConfig.ah) {
                return false;
            }
        }
        if (!turretConfig.canSpawn) {
            return true;
        }
        if (turretConfig.ai != -1.0f) {
            if (turretConfig.w != -1) {
                f = this.movementLevels[turretConfig.w].targetX + turretConfig.shootSpeed;
            } else {
                f = this.rotationSpeed + turretConfig.shootSpeed;
            }
            if (Utility.abs(Utility.endsWith(f, Utility.getAngleBetweenPoints(this.posX, this.posY, baseUnit.posX, baseUnit.posY), 360.0f)) > turretConfig.ai) {
                return false;
            }
        }
        if (turretConfig.spawnLogicCondition5 != null && !turretConfig.spawnLogicCondition5.read(this)) {
            return false;
        }
        if (turretConfig.animationSet != null && !AnimationTag.a(turretConfig.animationSet, baseUnit.getUnitCombatAnimation())) {
            return false;
        }
        if (turretConfig.animationSet2 != null && AnimationTag.a(turretConfig.animationSet2, baseUnit.getUnitCombatAnimation())) {
            return false;
        }
        if (baseUnit.i()) {
            if (turretConfig.spawnLogicCondition != null) {
                return turretConfig.spawnLogicCondition.read(this);
            }
            return true;
        }
        if (baseUnit.Q()) {
            if (turretConfig.spawnLogicCondition3 != null) {
                return turretConfig.spawnLogicCondition3.read(this);
            }
            return true;
        }
        if (turretConfig.spawnLogicCondition4 != null && !turretConfig.spawnLogicCondition4.read(this) && !baseUnit.checkTransportSlots()) {
            return false;
        }
        if (turretConfig.spawnLogicCondition2 != null) {
            return turretConfig.spawnLogicCondition2.read(this);
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (!turretConfig.B || !a(turretConfig, i, baseUnit, false, false)) {
            return;
        }
        for (int i2 = 0; i2 < this.unitConfig.turrets.length; i2++) {
            TurretConfig turretConfig2 = this.unitConfig.turrets[i2];
            if (turretConfig2 != null && i2 != i && turretConfig2.linkedTurret != null && turretConfig2.linkedTurret == turretConfig && this.movementLevels[i2].rotation < 9000.0f - ((b(i) * 0.5f) - turretConfig2.n)) {
                this.movementLevels[i2].rotation = 0.0f;
            }
        }
        if (turretConfig.w != -1) {
            TurretConfig turretConfig3 = this.unitConfig.turrets[turretConfig.w];
            if (!turretConfig3.B && turretConfig3.p != 0.0f) {
                this.movementLevels[turretConfig.w].rotation = b(turretConfig.w) + t(turretConfig.w);
            }
        }
        this.animationController.a(this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTags, 11, true);
        b(turretConfig);
        a(baseUnit, -1.0f, -1.0f, i, (CustomProjectileTemplate) null, 0);
    }

    public boolean a(TurretConfig turretConfig) {
        if (turretConfig.u > 0.0f && (turretConfig.u > this.f0cB || this.v)) {
            return false;
        }
        if (turretConfig.shootEffect != null && !turretConfig.shootEffect.b(this)) {
            return false;
        }
        return true;
    }

    public void b(TurretConfig turretConfig) {
        if (turretConfig.u > 0.0f) {
            this.f0cB -= turretConfig.u;
            if (this.f0cB < turretConfig.u && this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogic) {
                this.v = true;
            }
        }
        if (turretConfig.shootEffect != null) {
            turretConfig.shootEffect.a(this);
        }
    }

    public static Projectile a(BaseUnit baseUnit, int i, CustomProjectileTemplate customProjectileTemplate, float f, float f2, float f3, float f4) {
        Projectile projectileA = Projectile.a(baseUnit, f, f2, f3, i);
        a(projectileA, baseUnit, i, customProjectileTemplate, f, f2, f3, f4);
        return projectileA;
    }

    public static void a(Projectile projectile, BaseUnit baseUnit, int i, CustomProjectileTemplate customProjectileTemplate, float f, float f2, float f3, float f4) {
        Effect effectCreateLightEffect;
        GameEngine gameEngine = GameEngine.getInstance();
        projectile.az = f4;
        projectile.aT = f4;
        if (baseUnit == null) {
            throw new RuntimeException("Source cannot be null");
        }
        projectile.g = customProjectileTemplate;
        projectile.G = customProjectileTemplate.aI;
        projectile.aR = customProjectileTemplate.aJ;
        projectile.U = customProjectileTemplate.b;
        projectile.Y = customProjectileTemplate.c;
        if (!customProjectileTemplate.aN && (baseUnit instanceof CustomUnit)) {
            CustomUnit customUnit = (CustomUnit) baseUnit;
            projectile.U *= customUnit.y.shootDamageMultiplier;
            projectile.Y *= customUnit.y.shootDamageMultiplier;
        }
        projectile.Z = customProjectileTemplate.i;
        if (customProjectileTemplate.l) {
            projectile.aa = false;
            projectile.ab = true;
        } else {
            projectile.aa = !customProjectileTemplate.k;
        }
        projectile.ac = customProjectileTemplate.n;
        if (customProjectileTemplate.m) {
            projectile.ad = true;
            projectile.ae = false;
        }
        projectile.D = customProjectileTemplate.p;
        projectile.aY = customProjectileTemplate.q;
        projectile.aZ = customProjectileTemplate.r;
        if (customProjectileTemplate.o < 0.5d) {
            projectile.C = true;
        } else {
            projectile.H = customProjectileTemplate.o;
        }
        projectile.h = customProjectileTemplate.v;
        projectile.i = customProjectileTemplate.u;
        projectile.t = customProjectileTemplate.w;
        if (customProjectileTemplate.aM != 0.0f) {
            projectile.t += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-customProjectileTemplate.aM) * 100.0f), (int) (customProjectileTemplate.aM * 100.0f), 1) / 100.0f;
        }
        if (customProjectileTemplate.T && i != -1) {
            projectile.au = baseUnit;
            if (baseUnit instanceof OrderableUnit) {
                projectile.av = i;
                Vector3D vector3DD = ((OrderableUnit) baseUnit).D(i);
                projectile.aw = vector3DD.a;
                projectile.ax = vector3DD.b;
                projectile.ay = baseUnit.posZ + vector3DD.c;
            } else {
                projectile.aw = baseUnit.posX;
                projectile.ax = baseUnit.posY;
                projectile.ay = baseUnit.posZ;
            }
        }
        projectile.w = customProjectileTemplate.D;
        projectile.u = customProjectileTemplate.E;
        projectile.v = customProjectileTemplate.F;
        projectile.af = customProjectileTemplate.aO;
        projectile.ag = customProjectileTemplate.aP;
        projectile.ah = customProjectileTemplate.aQ;
        projectile.ai = customProjectileTemplate.aR;
        projectile.ak = customProjectileTemplate.aS;
        projectile.al = customProjectileTemplate.aT;
        projectile.am = customProjectileTemplate.aU;
        projectile.an = customProjectileTemplate.aV;
        if (customProjectileTemplate.aW > 0.0f) {
            projectile.ao = true;
            projectile.W = customProjectileTemplate.aW;
            projectile.X = projectile.W;
        }
        projectile.ar = customProjectileTemplate.aE;
        if (customProjectileTemplate.aG != 0.0f) {
            float f5 = customProjectileTemplate.aH;
            int iA = Color.a(projectile.ar);
            int iB = (int) (Color.b(projectile.ar) * f5);
            int iC = (int) (Color.c(projectile.ar) * f5);
            int iD = (int) (Color.d(projectile.ar) * f5);
            int teamUnitCount = baseUnit.team.getTeamUnitCount();
            projectile.ar = Color.a(iA, Utility.distance((int) (iB + (Color.b(teamUnitCount) * customProjectileTemplate.aG)), 0, 255), Utility.distance((int) (iC + (Color.c(teamUnitCount) * customProjectileTemplate.aG)), 0, 255), Utility.distance((int) (iD + (Color.d(teamUnitCount) * customProjectileTemplate.aG)), 0, 255));
        }
        projectile.P = customProjectileTemplate.x;
        projectile.R = customProjectileTemplate.y;
        projectile.S = !customProjectileTemplate.A;
        projectile.Q = customProjectileTemplate.z;
        if (customProjectileTemplate.B != null) {
            projectile.P = (short) 0;
            projectile.R = (short) 0;
        }
        projectile.x = customProjectileTemplate.aF;
        projectile.m = customProjectileTemplate.s;
        projectile.A = customProjectileTemplate.I;
        projectile.M = customProjectileTemplate.V;
        projectile.B = customProjectileTemplate.W;
        projectile.aH = customProjectileTemplate.ae;
        projectile.aG = customProjectileTemplate.aw;
        projectile.aM = customProjectileTemplate.af;
        if (customProjectileTemplate.ai != null) {
            customProjectileTemplate.ai.a(projectile.posX, projectile.posY, projectile.posZ, projectile.az, projectile);
        }
        if (customProjectileTemplate.ao != -1) {
            boolean z = false;
            Effect effect = projectile.aP;
            if (effect != null && effect.b == projectile && effect.d && effect != null) {
                if (effect.V < 150.0f) {
                    effect.V = 200.0f;
                }
                z = true;
            }
            if (!z && (effectCreateLightEffect = gameEngine.effectManager.createLightEffect(projectile, customProjectileTemplate.ao, customProjectileTemplate.ap)) != null) {
                if (customProjectileTemplate.aq) {
                    effectCreateLightEffect.c = true;
                }
                if (customProjectileTemplate.L) {
                    projectile.aP = effectCreateLightEffect;
                }
            }
        }
        projectile.aQ = customProjectileTemplate.ar;
        if (customProjectileTemplate.as != -1.0f) {
            projectile.aI = customProjectileTemplate.as;
        }
        if (customProjectileTemplate.at != -1.0f) {
            projectile.aJ = customProjectileTemplate.at;
        }
        projectile.aL = -1.0f;
        if (customProjectileTemplate.au != -1.0f) {
            projectile.r = customProjectileTemplate.au;
        }
        projectile.s = customProjectileTemplate.av;
        if (customProjectileTemplate.aZ != null) {
        }
        projectile.aE = customProjectileTemplate.bd;
        projectile.syncType = baseUnit.syncType;
        if (projectile.syncType < 4 && f3 >= -1.0f) {
            projectile.syncType = 4;
        }
        if (customProjectileTemplate.U) {
            projectile.syncType = 1;
        }
    }

    public Projectile a(BaseUnit baseUnit, float f, float f2, int i, CustomProjectileTemplate customProjectileTemplate, int i2) {
        CustomProjectileTemplate customProjectileTemplate2;
        GameEngine gameEngine = GameEngine.getInstance();
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (turretConfig.animationReference != null) {
            this.animationController.a(turretConfig.animationReference.b(), 6, true);
        }
        if (turretConfig.shootSpeed5 > 0.0f) {
            setBodyMovementFreezeTimer(turretConfig.shootSpeed5);
        }
        if (turretConfig.actionHandler != null) {
            turretConfig.actionHandler.a(this, new PointF(f, f2), baseUnit, i2 + 1, 0);
        }
        if (customProjectileTemplate == null) {
            customProjectileTemplate2 = this.unitConfig.f50fR[turretConfig.a(this)];
        } else {
            customProjectileTemplate2 = customProjectileTemplate;
        }
        PointF shadowTexture = getShadowTexture(i);
        Vector3D vector3DD = D(i);
        if (turretConfig.rotationSpeed5 > 0) {
            for (int i3 = 0; i3 < turretConfig.rotationSpeed5; i3++) {
                if (this.transportedUnits != null && this.transportedUnits.size() > 0) {
                    BaseUnit baseUnit2 = (BaseUnit) this.transportedUnits.remove(this.transportedUnits.size() - 1);
                    UnitMovementData unitMovementData = this.movementLevels[i];
                    GameViewUtils.a(baseUnit2, this);
                    baseUnit2.posX = vector3DD.a;
                    baseUnit2.posY = vector3DD.b;
                    baseUnit2.rotationSpeed = unitMovementData.targetX;
                    if (baseUnit2 instanceof OrderableUnit) {
                        OrderableUnit orderableUnit = (OrderableUnit) baseUnit2;
                        orderableUnit.clearAllWaypoints();
                        if (baseUnit != null) {
                            orderableUnit.appendAttackWaypoint(baseUnit);
                        } else {
                            orderableUnit.appendAttackMoveWaypoint(f, f2);
                        }
                    }
                }
            }
        }
        Projectile projectileA = null;
        if (customProjectileTemplate2.L && customProjectileTemplate == null) {
            if (this.G == null) {
                this.G = new Projectile[31];
            }
            if (this.G[i] != null && !this.G[i].isDestroyed) {
                projectileA = this.G[i];
                projectileA.a(this, vector3DD.a, vector3DD.b, this.posZ + vector3DD.c);
                if (!customProjectileTemplate2.N && projectileA.ap != null) {
                    projectileA.ap.clear();
                }
            }
        }
        this.unitFlags4 = (int) (((long) this.unitFlags4) + 1 + this.objectId);
        float f3 = this.movementLevels[i].targetX;
        boolean z = false;
        if (projectileA == null) {
            projectileA = Projectile.a(this, vector3DD.a, vector3DD.b, this.posZ + vector3DD.c, i);
            if (customProjectileTemplate2.L && customProjectileTemplate == null) {
                this.G[i] = projectileA;
            }
        } else {
            projectileA.g = customProjectileTemplate2;
            z = true;
        }
        a(projectileA, this, i, customProjectileTemplate2, vector3DD.a, vector3DD.b, this.posZ + vector3DD.c, f3);
        customProjectileTemplate2.a(this, projectileA, baseUnit, f, f2, m());
        if (!z && customProjectileTemplate2.R == 0.0f && customProjectileTemplate2.S == 0.0f) {
            projectileA.K = shadowTexture.x;
            projectileA.L = shadowTexture.y;
        }
        if (turretConfig.spawnCount != null) {
            gameEngine.effectManager.createLightEffect(vector3DD.a, vector3DD.b, this.posZ + vector3DD.c, turretConfig.spawnCount.intValue());
        }
        if (turretConfig.spawnList != null) {
            turretConfig.spawnList.a(vector3DD.a, vector3DD.b, this.posZ + vector3DD.c, this.movementLevels[i].targetX, this);
        }
        if (turretConfig.sound != null) {
            turretConfig.sound.a(vector3DD.a, vector3DD.b, 1.0f + Utility.randomFloatInRange(-0.07f, 0.07f));
        }
        if (this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogicOrTagsAndLogic) {
            this.attackTarget = null;
        }
        if (turretConfig.canTargetAir3) {
            this.movementLevels[i].targetUnit = null;
        }
        if (this.unitConfig.isConstructionBox && !this.isDestroyed) {
            bv();
        }
        if (this.unitConfig.canNotBeDirectlyAttacked && !this.isDestroyed) {
            remove();
            this.isDestroyed = true;
        }
        return projectileA;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return this.y.maxAttackRange;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public int y() {
        if (this.y.nanoRange != -1) {
            return this.y.nanoRange;
        }
        return super.y();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: u */
    public int setHeight(BaseUnit baseUnit) {
        int iA = baseUnit.r().a(this);
        if (this.unitConfig.f3aX != -1) {
            if (this.unitConfig.f4aY) {
                int i = (int) (this.unitConfig.f3aX + this.speed);
                if (baseUnit != null) {
                    i = (int) (i + baseUnit.speed);
                }
                return i + iA;
            }
            return this.unitConfig.f3aX + iA;
        }
        return y() + iA;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public int v(BaseUnit baseUnit) {
        int iA = baseUnit.r().a(this);
        if (this.unitConfig.isExperimental != -1) {
            if (this.unitConfig.isBuilding) {
                int i = (int) (this.unitConfig.isExperimental + this.speed);
                if (baseUnit != null) {
                    i = (int) (i + baseUnit.speed);
                }
                return i + iA;
            }
            return this.unitConfig.isExperimental + iA;
        }
        return y() + iA;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean w(BaseUnit baseUnit) {
        return this.unitConfig.isBuilding;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean x(BaseUnit baseUnit) {
        return this.unitConfig.f4aY;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cx */
    public float getUnitAIPathfindMemory() {
        return this.y.nanoFactorySpeed;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float c(BaseUnit baseUnit) {
        return this.unitConfig.fogOfWarSightRange;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: z */
    public float calculateUnitSpeed(BaseUnit baseUnit) {
        float unitHealthPercent = this.unitConfig.fogOfWarSightRangeWhileNotBuilt;
        if (baseUnit.getUnitHealthPercent() > 0.0f) {
            unitHealthPercent = baseUnit.getUnitHealthPercent() * this.unitConfig.useBuildingSmoke;
        }
        return unitHealthPercent;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: f */
    public float getReclaimRate(BaseUnit baseUnit) {
        return this.unitConfig.buildingToFootprintOffsetX;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b */
    public float getDistanceToTarget(BaseUnit baseUnit) {
        return this.unitConfig.buildingToFootprintOffsetY;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return this.y.moveSpeed * this.unknownFloat1;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: aZ */
    public float getMoveYAxisScaling() {
        return this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogicAndTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: ba */
    public float getMoveYAxisScalingInverse() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsAndLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return this.y.maxTurnSpeed;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (turretConfig.rotationSpeed2 != null) {
            return turretConfig.rotationSpeed2.floatValue();
        }
        return this.unitConfig.canBeBuiltByAndTagsOrLogicOrTagsOrLogicAndTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float x(int i) {
        return this.unitConfig.turrets[i].Q;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float w(int i) {
        return this.unitConfig.turrets[i].V;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float y(int i) {
        return this.unitConfig.turrets[i].W;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cD */
    public float getMaxHealth() {
        return this.unitConfig.targetOwnTeam * this.currentFrameTime;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float p(int i) {
        return this.unitConfig.targetNeutralTeam;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: d */
    public float getUnitArmorRating(boolean z) {
        if (!this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsAndLogicAndTags) {
            return 0.0f;
        }
        if (z && this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogicAndTags) {
            return this.movementLevels[this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic].targetX + 90.0f;
        }
        if (this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogicOrTags) {
            return this.movementLevels[this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic].targetX + 90.0f;
        }
        return super.getUnitArmorRating(z);
    }

    static {
        for (int i = 0; i < 10; i++) {
            dX.add(new CustomUnitEventData());
        }
        dY = true;
        dZ = new HashMap();
        ea = 0;
        eb = VariableScope.nullOrMissingString;
        ec = new PointF();
        ee = new Vector3D();
        ef = new PointF();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cY */
    public PointF getUnitAIPosition() {
        PointF pointF = H;
        CustomUnitConfig customUnitConfig = this.unitConfig;
        if (customUnitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogicOrTags && this.movementLevels[customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic].rotation != 0.0f && customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogic.p != 0.0f) {
            pointF.a(G(customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic));
            pointF.b(-this.posX, -this.posY);
            return pointF;
        }
        pointF.x = 0.0f;
        pointF.y = 0.0f;
        return pointF;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: aP */
    public PointF getShadowOffset() {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        PointF unitAIPosition = getUnitAIPosition();
        I.x = unitAIPosition.x + customUnitConfig.canBeBuiltByOrTagsAndLogicOrTagsOrLogic;
        I.y = unitAIPosition.y + customUnitConfig.canBeBuiltByOrTagsAndLogicAndTagsAndLogic;
        return I;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        Texture textureT;
        float fClamp;
        float f2;
        CustomUnitConfig customUnitConfig = this.unitConfig;
        boolean z = this.isDestroyed;
        if (this.legInstances != null && !z) {
            CustomUnitLegController.a(this, f, false, false);
        }
        GameEngine gameEngine = GameEngine.getInstance();
        GraphicsEngine graphicsEngine = gameEngine.graphicsEngine2;
        Paint renderPaint = getRenderPaint();
        float maxHealth = getMaxHealth();
        PointF unitAIPosition = getUnitAIPosition();
        drawShadow();
        int i = customUnitConfig.onDestroyListeners.size;
        if (i > 0) {
            Object[] objArrA = customUnitConfig.onDestroyListeners.a();
            for (int i2 = i - 1; i2 >= 0; i2--) {
                ((CustomUnitRenderHook) objArrA[i2]).d(this, f);
            }
        }
        if (this.ew) {
            float f3 = (this.posX + unitAIPosition.x) - gameEngine.viewpointXSnapped;
            float f4 = ((this.posY + unitAIPosition.y) - gameEngine.viewpointYSnapped) - this.posZ;
            if (maxHealth != 1.0f) {
                graphicsEngine.k();
                graphicsEngine.a(maxHealth, maxHealth, f3, f4);
            }
            graphicsEngine.a(this.baseTexture, f3, f4, getUnitArmorRating(false) - 90.0f, renderPaint);
            if (maxHealth != 1.0f) {
                graphicsEngine.l();
            }
        } else {
            RectF unitBounds = getUnitBounds();
            float f5 = unitAIPosition.x;
            float f6 = unitAIPosition.y - this.posZ;
            unitBounds.a += f5;
            unitBounds.b += f6;
            unitBounds.c += f5;
            unitBounds.d += f6;
            Rect rectA_ = a_(false);
            float f7 = (unitBounds.a + unitBounds.c) * 0.5f;
            float f8 = (unitBounds.b + unitBounds.d) * 0.5f;
            graphicsEngine.k();
            if (maxHealth != 1.0f) {
                graphicsEngine.a(maxHealth, maxHealth, f7, f8);
            }
            graphicsEngine.a(getUnitArmorRating(false), f7, f8);
            graphicsEngine.a(this.baseTexture, rectA_, unitBounds, renderPaint);
            graphicsEngine.l();
        }
        if (i > 0) {
            Object[] objArrA2 = customUnitConfig.onDestroyListeners.a();
            for (int i3 = i - 1; i3 >= 0; i3--) {
                ((CustomUnitRenderHook) objArrA2[i3]).e(this, f);
            }
        }
        GameViewUtils.a((OrderableUnit) this);
        if (this.legInstances != null && !z && customUnitConfig.energyStartPercentage) {
            CustomUnitLegController.a(this, f, true, false);
        }
        if (canMove() && customUnitConfig.defaultTurret != null && !z) {
            float fE = this.movementLevels[customUnitConfig.defaultTurret.rotationSpeed].speed / e(customUnitConfig.defaultTurret.rotationSpeed);
            if (fE != 0.0f) {
                boolean z2 = true;
                boolean zY = isCurrentCommandReclaim();
                if (zY && customUnitConfig.createNeutral) {
                    z2 = false;
                } else if (!zY && customUnitConfig.showActionsAndWaypoints) {
                    z2 = false;
                }
                if (z2 && customUnitConfig.turrets[customUnitConfig.defaultTurret.rotationSpeed].texture2 == null) {
                    Vector3D vector3DBn = bn();
                    gameEngine.graphicsEngine2.k();
                    gameEngine.graphicsEngine2.b(vector3DBn.a - gameEngine.viewpointXSnapped, ((vector3DBn.b - vector3DBn.c) - gameEngine.viewpointYSnapped) - this.posZ);
                    gameEngine.graphicsEngine2.a(fE, fE);
                    if (zY) {
                        gameEngine.graphicsEngine2.a(BuilderUnit.builderDechargeTexture, 0.0f, 0.0f, (Paint) null);
                    } else {
                        gameEngine.graphicsEngine2.a(BuilderUnit.builderChargeTexture, 0.0f, 0.0f, (Paint) null);
                    }
                    gameEngine.graphicsEngine2.l();
                }
            }
        }
        if (customUnitConfig.f49fP && !z) {
            int techLevel = getTechLevel();
            for (int i4 = 0; i4 < techLevel; i4++) {
                float fE2 = this.movementLevels[i4].speed / e(i4);
                TurretConfig turretConfig = customUnitConfig.turrets[i4];
                if (turretConfig != null && fE2 != 0.0f && turretConfig.texture2 != null) {
                    GameViewUtils.a(this, turretConfig.texture2, fE2, i4);
                }
            }
        }
        if (!z && this.shield > 0.0f && this.energy == 0.0f && (textureT = T()) != null) {
            if (!customUnitConfig.canBeBuiltByAndTagsOrLogicOrTagsAndLogic) {
                fClamp = 0.0f + 0.09f + ((this.shield / this.unitEnergyMax) * 0.4f) + ((Utility.clamp(this.unitShieldMax, 50.0f) / 50.0f) * 0.5f);
            } else {
                float fClamp2 = 0.0f + ((Utility.clamp(this.unitShieldMax, 50.0f) / 50.0f) * 0.5f);
                float f9 = this.unitShieldMax;
                if (f9 > 5.0f) {
                    f9 = 5.0f;
                }
                fClamp = fClamp2 + ((f9 / 5.0f) * 0.2f);
            }
            if (fClamp > 0.0f) {
                if (fClamp > 1.0f) {
                    fClamp = 1.0f;
                }
                if (this.J == null) {
                    this.J = GameViewUtils.a();
                }
                Paint paint = this.J;
                paint.a((int) (fClamp * 255.0f), 255, 255, 255);
                float f10 = this.posX - gameEngine.viewpointXSnapped;
                float f11 = (this.posY - gameEngine.viewpointYSnapped) - this.posZ;
                if (!customUnitConfig.energyRegenWhenRecharging) {
                    f2 = ((customUnitConfig.canBeBuiltByOrTagsAndLogicAndTagsOrLogicAndTags * 2) / 87.0f) * 1.25f;
                } else {
                    f2 = ((customUnitConfig.canBeBuiltByOrTagsAndLogicAndTagsOrLogicAndTags * 2) / textureT.p) * 1.25f;
                }
                gameEngine.graphicsEngine2.k();
                gameEngine.graphicsEngine2.a(f2, f2, f10, f11);
                gameEngine.graphicsEngine2.a(textureT, f10, f11, getUnitArmorRating(false) - 90.0f, paint);
                gameEngine.graphicsEngine2.l();
            }
        }
        if (i > 0) {
            Object[] objArrA3 = customUnitConfig.onDestroyListeners.a();
            for (int i5 = i - 1; i5 >= 0; i5--) {
                ((CustomUnitRenderHook) objArrA3[i5]).c(this, f);
            }
            return true;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture T() {
        return this.unitConfig.energyRegen;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bi */
    public boolean isSlidingMovement() {
        return this.unitConfig.canBeBuiltByAndTagsOrLogicOrTagsAndLogicAndTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bj */
    public boolean isIgnoreMoveOrders() {
        return this.unitConfig.canBeBuiltByAndTagsOrLogicOrTagsOrLogicOrTagsAndLogic;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: ag */
    public boolean canAttackSurfaceUnits() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsOrLogicAndTags.read(this);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsOrLogicOrTags.read(this);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: ae */
    public boolean canAttackSubmergedUnits() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsAndLogicOrTags.read(this);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean ah() {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsAndLogicAndTags == null) {
            return true;
        }
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsAndLogicAndTags.read(this);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: k */
    public boolean canAttackUnitType(BaseUnit baseUnit) {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        if (customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsOrLogicOrTags) {
            if (customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsOrLogicAndTags != null && !AnimationTag.a(customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsOrLogicAndTags, baseUnit.getUnitCombatAnimation())) {
                return false;
            }
            if (customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogicOrTags != null && AnimationTag.a(customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogicOrTags, baseUnit.getUnitCombatAnimation())) {
                return false;
            }
            if (customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogicAndTags) {
                boolean z = false;
                for (int i = 0; i < customUnitConfig.turrets.length; i++) {
                    TurretConfig turretConfig = customUnitConfig.turrets[i];
                    if ((turretConfig.animationSet2 == null || !AnimationTag.a(turretConfig.animationSet2, baseUnit.getUnitCombatAnimation())) && (turretConfig.animationSet == null || AnimationTag.a(turretConfig.animationSet, baseUnit.getUnitCombatAnimation()))) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    return false;
                }
            }
        }
        if (baseUnit.i()) {
            return canAttackFlyingUnits();
        }
        if (baseUnit.Q()) {
            return canAttackSubmergedUnits();
        }
        if (!ah() && !baseUnit.checkTransportSlots()) {
            return false;
        }
        return canAttackSurfaceUnits();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean E() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsOrLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return this.unitConfig.turrets[i].shootSpeed2;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getTurretTargetSearchRange(int i) {
        return this.unitConfig.turrets[i].ab;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float A(int i) {
        return this.unitConfig.turrets[i].ah;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B(int i) {
        return this.unitConfig.turrets[i].shootSpeed;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float C(int i) {
        float f;
        float f2;
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (turretConfig.w != -1) {
            f = this.movementLevels[turretConfig.w].targetX;
        } else {
            f = this.rotationSpeed;
        }
        if (this.isRotating && bc() > 0.95d) {
            f2 = f + turretConfig.shootAccuracy;
        } else {
            f2 = f + turretConfig.shootSpeed;
        }
        if (turretConfig.ar != 0.0f) {
            return 999.0f;
        }
        return f2;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean bm() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float q(int i) {
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (!turretConfig.B) {
            return 0.0f;
        }
        CustomProjectileTemplate customProjectileTemplate = this.unitConfig.f50fR[turretConfig.a(this)];
        float f = 0.0f;
        if (!customProjectileTemplate.s) {
            f = 0.0f + customProjectileTemplate.b;
        }
        float f2 = f + customProjectileTemplate.c;
        if (!customProjectileTemplate.aN) {
            f2 *= this.y.shootDamageMultiplier;
        }
        return f2;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return this.unitConfig.turrets[i].m * this.y.shootDelayMultiplier;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        return this.unitConfig.turrets[i].n;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float f(int i) {
        return this.unitConfig.turrets[i].o;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean s(int i) {
        return this.unitConfig.turrets[i].s;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float t(int i) {
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (turretConfig.shootAngle == 0.0f || turretConfig.n == 0.0f) {
            return 0.0f;
        }
        return -(turretConfig.shootAngle * (this.movementLevels[i].speed / turretConfig.n));
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean r(int i) {
        if (this.unitConfig.turrets[i].B) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void b(BaseUnit baseUnit, int i) {
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (turretConfig.spawnList2 != null) {
            PointF pointFE = E(i);
            turretConfig.spawnList2.a(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX, this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean u(int i) {
        if (!a(this.unitConfig.turrets[i])) {
            return false;
        }
        return super.u(i);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: s */
    public int getSoftCollisionDivisor(BaseUnit baseUnit) {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsAndLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bO() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsOrLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bP() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return this.y.mass;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cG */
    public boolean isUnitTransportWeight() {
        return this.unitConfig.shieldDisplayOnlyDeflection;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        float f;
        if (z && !this.unitConfig.shieldDisplayOnlyDeflection) {
            return super.a_(z);
        }
        if (this.isDestroyed) {
            return super.a_(z);
        }
        CustomUnitConfig customUnitConfig = this.unitConfig;
        int i = this.animationFrameIndex;
        int i2 = 0;
        CustomUnitDirectionConfig customUnitDirectionConfig = customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsAndLogic;
        if (this.animationController.currentAnimation != null && this.animationController.currentAnimation.k != null) {
            customUnitDirectionConfig = this.animationController.currentAnimation.k;
        }
        if (customUnitDirectionConfig != null) {
            float f2 = customUnitDirectionConfig.directionUnits;
            if (f2 < 0.0f) {
                f2 = -f2;
                f = -this.rotationSpeed;
                if (customUnitDirectionConfig.useMainTurret) {
                    f = -this.movementLevels[customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic].targetX;
                }
            } else {
                f = this.rotationSpeed;
                if (customUnitDirectionConfig.useMainTurret) {
                    f = this.movementLevels[customUnitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic].targetX;
                }
            }
            int i3 = (int) (360.0f / f2);
            int i4 = ((int) (((f - customUnitDirectionConfig.startingDirection) - (f2 * 0.5f)) / f2)) % i3;
            if (i4 < 0) {
                i4 += i3;
            }
            if (customUnitDirectionConfig.strideX > 0) {
                i += i4 * customUnitDirectionConfig.strideX;
            }
            if (customUnitDirectionConfig.strideY > 0) {
                i2 = 0 + (i4 * customUnitDirectionConfig.strideY);
            }
        }
        if (i >= customUnitConfig.turretTurnSpeed) {
            i2 += i / customUnitConfig.turretTurnSpeed;
            i %= customUnitConfig.turretTurnSpeed;
        }
        return super.a(z, i, i2);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cF */
    public RectF getUnitBounds() {
        RectF rectFCF = super.getUnitBounds();
        if (this.unitConfig.armourMinDamageToKeep) {
            rectFCF.a(this.unitConfig.maxMass, this.unitConfig.techLevel - this.unitConfig.armour);
        }
        return rectFCF;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bl */
    public int getTechLevel() {
        if (this.unitConfig == null) {
            return 1;
        }
        return this.unitConfig.turrets.length;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: v */
    public int getLinkedTurretIndex(int i) {
        return this.unitConfig.turrets[i].x;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Vector3D F(int i) {
        return a(i, false);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF G(int i) {
        Vector3D vector3DA = a(i, false);
        K.x = vector3DA.a;
        K.y = vector3DA.b;
        return K;
    }

    public Vector3D a(int i, boolean z) {
        float fFastCos;
        float fFastSin;
        float f;
        float f2;
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (turretConfig.w == -1) {
            fFastCos = this.posX;
            fFastSin = this.posY;
            f = 0.0f;
            f2 = this.rotationSpeed;
        } else {
            if (z) {
                throw new RuntimeException("Turret can not be attached to turret that is not attached to the body");
            }
            Vector3D vector3DA = a(turretConfig.w, true);
            fFastCos = vector3DA.a;
            fFastSin = vector3DA.b;
            f = vector3DA.c;
            f2 = this.movementLevels[turretConfig.w].targetX;
        }
        if (this.movementLevels[i].rotation > 0.0f && turretConfig.p != 0.0f) {
            float f3 = 0.0f;
            float fB = (b(i) + t(i)) - this.movementLevels[i].rotation;
            if (fB < turretConfig.q) {
                f3 = (fB / turretConfig.q) * turretConfig.p;
            } else if (fB < turretConfig.q + turretConfig.r) {
                f3 = turretConfig.p - (((fB - turretConfig.q) / turretConfig.r) * turretConfig.p);
            }
            if (f3 != 0.0f) {
                fFastCos += Utility.fastCos(this.movementLevels[i].targetX) * f3;
                fFastSin += Utility.fastSin(this.movementLevels[i].targetX) * f3;
            }
        }
        float f4 = turretConfig.range;
        float f5 = turretConfig.minRange;
        float f6 = turretConfig.shootDelay;
        if (f4 != 0.0f || f5 != 0.0f) {
            float fFastSin2 = Utility.fastSin(f2);
            float fFastCos2 = Utility.fastCos(f2);
            fFastCos += (fFastCos2 * f5) - (fFastSin2 * f4);
            fFastSin += ((fFastSin2 * f5) + (fFastCos2 * f4)) * turretConfig.i;
        }
        dK.a = fFastCos;
        dK.b = fFastSin;
        dK.c = f + f6;
        return dK;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        if (this.customUnitCOnfig != null) {
            return this.customUnitCOnfig.a(getUpgradeLevel());
        }
        return this.unitConfig.a(getUpgradeLevel());
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public AbstractUnitAction validateActionId(ActionId actionId) {
        CustomUnitConfig customUnitConfig;
        if (this.customUnitCOnfig != null) {
            customUnitConfig = this.customUnitCOnfig;
        } else {
            customUnitConfig = this.unitConfig;
        }
        return customUnitConfig.findActionById(actionId);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: V */
    public int getUpgradeLevel() {
        return this.unitConfig.canBeBuiltByTurret;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: e */
    public AbstractUnitAction getUnitAction(UnitType unitType) {
        return this.unitEffectManager.b(unitType);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void a(AbstractUnitAction abstractUnitAction, boolean z, PointF pointF, BaseUnit baseUnit) {
        if (abstractUnitAction == HovercraftUnit.i) {
            if (!z) {
                getTransportedUnits();
                return;
            } else {
                getLoadedUnits();
                return;
            }
        }
        if (abstractUnitAction == HovercraftUnit.j) {
            if (!z) {
                getLoadedUnits();
                return;
            }
            return;
        }
        if (!z) {
            if (pointF != null && !a(abstractUnitAction, pointF.x, pointF.y)) {
                return;
            }
            if (abstractUnitAction instanceof CustomAction) {
                CustomAction customAction = (CustomAction) abstractUnitAction;
                if (customAction.a.spawnList6 != null) {
                    GameEngine gameEngine = GameEngine.getInstance();
                    if (this.team == gameEngine.playerTeam && !gameEngine.isGameThreadRunning()) {
                        customAction.a.spawnList6.a();
                    }
                }
                if (customAction.a.spawnList2 != null) {
                    customAction.a.spawnList2.a(this.posX, this.posY, this.posZ, this.rotationSpeed, this);
                }
            }
        }
        if (z && (abstractUnitAction instanceof CustomAction) && !((CustomAction) abstractUnitAction).a.isDefaultBuildCommand2) {
            return;
        }
        com.corrodinggames.rts.game.units.buildings.Projectile projectileA = this.unitEffectManager.a(abstractUnitAction, z, pointF, baseUnit);
        if (!z) {
            if (projectileA != null) {
                a(UnitEventType.queueItemAdded, (BaseUnit) null, abstractUnitAction.getAnimationSet(), (VariableScope) null);
                return;
            }
            return;
        }
        if (projectileA != null) {
            a(UnitEventType.queueItemCancelled, (BaseUnit) null, abstractUnitAction.getAnimationSet(), (VariableScope) null);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void performUnitAction(AbstractUnitAction abstractUnitAction, boolean z) {
        a(abstractUnitAction, z, (PointF) null, (BaseUnit) null);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b */
    public void stopMoving(AbstractUnitAction abstractUnitAction, boolean z) {
        this.unitEffectManager.a(abstractUnitAction, z);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void a(AbstractUnitAction abstractUnitAction) {
        this.unitEffectManager.a(abstractUnitAction);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean a(AbstractUnitAction abstractUnitAction, float f, float f2) {
        if (abstractUnitAction instanceof CustomAction) {
            GameEngine gameEngine = GameEngine.getInstance();
            CustomAction customAction = (CustomAction) abstractUnitAction;
            if (customAction.a.energyCost3 != null && customAction.a.offset == null) {
                if (customAction.a.energyCost3.intValue() >= this.unitConfig.turrets.length) {
                    a("checkTargetedActionOrder: " + customAction.a.energyCost3 + " larger than max turret size");
                    return true;
                }
                if (!a(this.unitConfig.turrets[customAction.a.energyCost3.intValue()], customAction.a.energyCost3.intValue(), f, f2, true)) {
                    return false;
                }
                if (customAction.a.condition4 != null && GameViewUtils.a(f, f2, customAction.a.condition4)) {
                    if (this.team == gameEngine.playerTeam) {
                        gameEngine.gameUI.showMediumPriorityMessage("Invalid map location (Must be passable by " + customAction.a.condition4.name() + ")");
                        return false;
                    }
                    return false;
                }
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(AbstractUnitAction abstractUnitAction, boolean z, float f, float f2) {
        AbstractUnitAction abstractUnitActionValidateActionId;
        if ((abstractUnitAction instanceof CustomAction) && (abstractUnitActionValidateActionId = validateActionId(abstractUnitAction.getActionId())) != null) {
            CustomAction customAction = (CustomAction) abstractUnitActionValidateActionId;
            Integer num = customAction.a.energyCost3;
            if (num != null && customAction.a.offset == null && num.intValue() < this.unitConfig.turrets.length) {
                TurretConfig turretConfig = this.unitConfig.turrets[num.intValue()];
                if (turretConfig.ag > 0.0f) {
                    GameViewUtils.b((BaseUnit) this, turretConfig.ag, true);
                }
                GameViewUtils.a((BaseUnit) this, turretConfig.range2, true, true);
            }
            if (z && customAction.a.condition5 != null) {
                customAction.a.condition5.a(this, f, f2);
            }
        }
        super.a(abstractUnitAction, z, f, f2);
    }

    public boolean a(AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i, int i2) {
        PointF pointF2 = null;
        BaseUnit baseUnit2 = null;
        int i3 = 0;
        if (i > 0) {
            pointF2 = dM;
            baseUnit2 = dN;
            i3 = dO;
        }
        dM = pointF;
        dN = baseUnit;
        dO = i2;
        boolean zA = a(abstractUnitAction, pointF, baseUnit, i);
        dM = pointF2;
        dN = baseUnit2;
        dO = i3;
        return zA;
    }

    public boolean a(AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        BaseUnit unit;
        GameEngine gameEngine = GameEngine.getInstance();
        if (i > 10) {
            return false;
        }
        abstractUnitAction.isPickAction(this, baseUnit);
        if (abstractUnitAction instanceof CustomAction) {
            CustomAction customAction = (CustomAction) abstractUnitAction;
            CustomActionDef customActionDef = customAction.a;
            if (customActionDef.displayCondition != null && !customActionDef.displayCondition.read(this)) {
                return true;
            }
            boolean z = false;
            if (customActionDef.delay != null) {
                this.f0cB += customAction.a.delay.floatValue();
                z = true;
            }
            if (customActionDef.energyCost != null) {
                customActionDef.energyCost.h(this);
                z = true;
            }
            if (customActionDef.energyCost2 != null) {
                customActionDef.energyCost2.a((BaseUnit) this, this.team.getSpectatorEnergyFactor(), true);
                z = true;
            }
            if (customActionDef.condition11 != null) {
                if (customActionDef.condition11.read(this)) {
                    this.unitFlags2 = gameEngine.lastTick;
                }
                z = true;
            }
            if (customActionDef.energyCost3 != null) {
                PointF pointF2 = pointF;
                for (int i2 = 0; i2 < customActionDef.condition3; i2++) {
                    if (customActionDef.offset != null) {
                        pointF2 = new PointF();
                        if (customActionDef.condition != null && (unit = customActionDef.condition.readUnit(this)) != null) {
                            pointF2.a(unit.posX, unit.posY);
                        } else {
                            pointF2.a(this.posX, this.posY);
                        }
                        float fFastCos = Utility.fastCos(this.rotationSpeed);
                        float fFastSin = Utility.fastSin(this.rotationSpeed);
                        float f = customActionDef.offset.x;
                        float f2 = customActionDef.offset.y;
                        pointF2.b((fFastCos * f2) - (fFastSin * f), (fFastSin * f2) + (fFastCos * f));
                    }
                    if (pointF2 == null) {
                        NetworkEngine.g("completeQueueItem:" + customAction.getActionId() + " for fireTurretXAtGround needs points but it is missing");
                    } else {
                        a((BaseUnit) null, pointF2.x, pointF2.y, customAction.a.energyCost3.intValue(), customAction.a.condition2, i);
                    }
                }
                z = true;
            }
            if (customActionDef.spawnList != null) {
                customActionDef.spawnList.a(this.posX, this.posY, this.posZ, this.rotationSpeed, this);
                z = true;
            }
            if (customActionDef.spawnList3 != null) {
                customActionDef.spawnList3.a(this.posX, this.posY, 1.0f);
                z = true;
            }
            if (customActionDef.spawnList4 != null && !gameEngine.isGameThreadRunning()) {
                customActionDef.spawnList4.a();
                z = true;
            }
            if (customActionDef.spawnList5 != null) {
                if (this.team == gameEngine.playerTeam && !gameEngine.isGameThreadRunning()) {
                    customActionDef.spawnList5.a();
                }
                z = true;
            }
            if (customActionDef.ac.size > 0) {
                Object[] objArrA = customActionDef.ac.a();
                for (int i3 = 0; i3 < customActionDef.ac.size; i3++) {
                    if (((LogicAction) objArrA[i3]).doAction(this, abstractUnitAction, pointF, baseUnit, i)) {
                        z = true;
                    }
                }
            }
            PointF pointF3 = pointF;
            BaseUnit unit2 = baseUnit;
            if ((customActionDef.actionHandler2 != null || customAction.a.actionHandler3 != null) && customActionDef.condition6 != null) {
                unit2 = customActionDef.condition6.readUnit(this);
                pointF3 = new PointF();
                if (unit2 != null) {
                    pointF3.x = unit2.posX;
                    pointF3.y = unit2.posY;
                } else {
                    pointF3.x = this.posX;
                    pointF3.y = this.posY;
                }
            }
            if (customActionDef.actionHandler2 != null) {
                if (customActionDef.condition7 == null || customActionDef.condition7.read(this)) {
                    int number = 1;
                    if (customActionDef.condition8 != null) {
                        number = (int) customActionDef.condition8.readNumber(this);
                        if (number > 10000) {
                            number = 10000;
                        }
                    }
                    for (int i4 = 0; i4 < number; i4++) {
                        customAction.a.actionHandler2.a(this, pointF3, unit2, i + 1, i4);
                    }
                }
                z = true;
            }
            if (customAction.a.actionHandler3 != null) {
                if (customActionDef.condition7 == null || customActionDef.condition7.read(this)) {
                    customAction.a.actionHandler3.a(this, pointF3, unit2);
                }
                z = true;
            }
            UnitType unitTypeC = null;
            if (customActionDef.iconUnitType != null) {
                unitTypeC = customActionDef.iconUnitType.c();
            }
            if (unitTypeC != null) {
                if (GameEngine.isIOSVersionStatic) {
                    GameEngine.updatePaintTextSizeIfNeeded(getUnitShortName() + ": converting unit: " + abstractUnitAction.getActionIdString());
                }
                if (!(unitTypeC instanceof CustomUnitConfig)) {
                    BaseUnit baseUnitA = unitTypeC.a();
                    baseUnitA.posX = this.posX;
                    baseUnitA.posY = this.posY;
                    if (!baseUnitA.bI()) {
                        baseUnitA.rotationSpeed = this.rotationSpeed;
                    }
                    baseUnitA.f(this.team);
                    baseUnitA.getUnitType(null);
                    float f3 = this.maxHealth;
                    float f4 = baseUnitA.maxHealth;
                    if (f3 == 0.0f) {
                        baseUnitA.o(baseUnitA.maxHealth);
                    } else {
                        baseUnitA.o((this.currentHealth / f3) * f4);
                    }
                    if (this.isSelected) {
                        GameEngine.getInstance().gameUI.addToSelection(baseUnitA);
                    }
                    PlayerTeam.c(baseUnitA);
                    getUnitAICondition();
                } else {
                    AnimationSet unitCombatAnimation = null;
                    if (customActionDef.isDefaultBuildCommand5) {
                        unitCombatAnimation = getUnitCombatAnimation();
                    }
                    PlayerTeam.b((BaseUnit) this);
                    this.customUnitCOnfig = null;
                    a((CustomUnitConfig) unitTypeC, false, false, customActionDef.setUnitData2);
                    if (unitCombatAnimation != null) {
                        a(unitCombatAnimation, true);
                    }
                    S();
                    this.unitEffectManager.e();
                    this.unitFlags3 = GameEngine.getInstance().lastTick;
                    PlayerTeam.c(this);
                }
                z = true;
                if (!customAction.getDisplayText().c()) {
                    W();
                }
            }
            if (!z && customActionDef.addToBuildQueue) {
                GameEngine.isInSpace("completeQueueItem:" + customAction.getActionId() + " had no effect (but should have)");
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void b(com.corrodinggames.rts.game.units.buildings.Projectile projectile) {
        AbstractUnitAction abstractUnitActionValidateActionId = validateActionId(projectile.j);
        if (abstractUnitActionValidateActionId != null && (abstractUnitActionValidateActionId instanceof CustomAction)) {
            CustomActionDef customActionDef = ((CustomAction) abstractUnitActionValidateActionId).a;
            if (customActionDef.actionHandler != null) {
                ec.x = this.posX;
                ec.y = this.posY;
                customActionDef.actionHandler.a(this, ec, null, 0, 0);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public boolean c(com.corrodinggames.rts.game.units.buildings.Projectile projectile) {
        return true;
    }

    public void i(boolean z) {
        this.unitEffectManager.a(z);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(com.corrodinggames.rts.game.units.buildings.Projectile projectile) {
        float fFloatValue;
        AbstractUnitAction abstractUnitActionValidateActionId = validateActionId(projectile.j);
        if (abstractUnitActionValidateActionId != null && a(abstractUnitActionValidateActionId, projectile.h, projectile.i, 0, 0)) {
            return;
        }
        float fFloatValue2 = 0.0f;
        if (this.unitConfig.isBuildingUnit && this.unitConfig.spawnAngleOffset != null) {
            fFloatValue2 = this.rotationSpeed + this.unitConfig.spawnAngleOffset.floatValue() + 90.0f;
        }
        if (this.unitConfig.spawnSideOffset != null) {
            fFloatValue = this.unitConfig.spawnSideOffset.floatValue();
        } else if (this.unitEffectManager.b != null) {
            fFloatValue = this.speed * 3.0f;
        } else {
            fFloatValue = this.speed * 2.0f;
        }
        BaseUnit baseUnitA = this.unitEffectManager.a(projectile, fFloatValue, this.r, fFloatValue2);
        if (baseUnitA != null) {
            F(baseUnitA);
            PlayerTeam.c(baseUnitA);
            a(UnitEventType.queuedUnitFinished, baseUnitA);
        }
    }

    /* JADX INFO: renamed from: E */
    public void getAttackDamage(BaseUnit baseUnit) {
        float fFloatValue = 0.0f;
        if (this.unitConfig.isBuildingUnit && this.unitConfig.spawnAngleOffset != null) {
            fFloatValue = this.rotationSpeed + this.unitConfig.spawnAngleOffset.floatValue() + 90.0f;
        }
        baseUnit.rotationSpeed = 90.0f + fFloatValue;
        float fFloatValue2 = 70.0f;
        if (this.unitConfig.spawnSideOffset != null) {
            fFloatValue2 = this.unitConfig.spawnSideOffset.floatValue();
        }
        this.unitEffectManager.a(baseUnit, fFloatValue2, this.r);
    }

    public void F(BaseUnit baseUnit) {
        baseUnit.posX = this.posX + this.unitConfig.spawnOffsetLocalX;
        baseUnit.posY = this.posY + this.unitConfig.spawnOffsetLocalY;
        if (!this.unitConfig.isBuildingUnit) {
            float fFloatValue = 180.0f;
            if (this.unitConfig.spawnAngleOffset != null) {
                fFloatValue = this.unitConfig.spawnAngleOffset.floatValue();
            }
            float fFloatValue2 = 70.0f;
            if (this.unitConfig.spawnSideOffset != null) {
                fFloatValue2 = this.unitConfig.spawnSideOffset.floatValue();
            }
            boolean zA = HovercraftUnit.a(this, baseUnit, this.r, 7.0f, fFloatValue, fFloatValue2, this.unitConfig.spawnOffsetLocalX, this.unitConfig.spawnOffsetLocalY);
            if (!this.unitConfig.keepOriginalZOnSpawn) {
                baseUnit.posZ = this.posZ;
            }
            baseUnit.posZ += this.unitConfig.spawnOffsetZ;
            if (baseUnit instanceof CustomUnit) {
                ((CustomUnit) baseUnit).dF();
            }
            if ((i() || !zA || this.unitConfig.f10eU.read(this)) && getUnitAIPathfindCost()) {
                loadTransportedUnit(baseUnit);
            }
        }
        this.r = !this.r;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public UnitPrice by() {
        FastArrayList fastArrayListG = this.unitEffectManager.g();
        int size = fastArrayListG.size();
        if (size == 0) {
            return UnitPrice.a;
        }
        UnitPrice unitPrice = new UnitPrice();
        Object[] objArrA = fastArrayListG.a();
        for (int i = 0; i < size; i++) {
            AbstractUnitAction abstractUnitActionValidateActionId = validateActionId(((com.corrodinggames.rts.game.units.buildings.Projectile) objArrA[i]).j);
            if (abstractUnitActionValidateActionId != null && (abstractUnitActionValidateActionId instanceof CustomAction)) {
                CustomAction customAction = (CustomAction) abstractUnitActionValidateActionId;
                if (customAction.a.delay != null) {
                    unitPrice.c += customAction.a.delay.floatValue();
                }
                if (customAction.a.energyCost != null) {
                    UnitPrice unitPrice2 = customAction.a.energyCost;
                    if (!unitPrice2.c()) {
                        unitPrice = UnitPrice.a(unitPrice, unitPrice2);
                    }
                }
                if (customAction.a.energyCost2 != null) {
                    UnitPrice unitPrice3 = customAction.a.energyCost2;
                    if (!unitPrice3.c()) {
                        unitPrice = UnitPrice.a(unitPrice, unitPrice3);
                    }
                }
            }
        }
        return unitPrice;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public boolean dA() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags > 0 && dI() > this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public int h(UnitType unitType) {
        return this.unitEffectManager.a(unitType);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public int f(boolean z) {
        return this.unitEffectManager.a(AbstractUnitAction.NONE_ACTION_ID, z, true);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public int a(ActionId actionId, boolean z) {
        return this.unitEffectManager.a(actionId, z);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public com.corrodinggames.rts.game.units.buildings.Projectile dw() {
        return this.unitEffectManager.b();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bD */
    public UnitPrice getQueuedActionPriceDelta() {
        return this.unitEffectManager.c();
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public FastArrayList dx() {
        return this.unitEffectManager.c;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void dz() {
        this.unitEffectManager.e = 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public boolean dy() {
        return this.unitEffectManager.a();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public int a(AnimationTag animationTag) {
        return this.unitEffectManager.a(animationTag);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(PointF pointF) {
        if (this.unitConfig.canBeBuiltByOrTagsAndLogicAndTagsAndLogicOrTags) {
            this.unitEffectManager.b = pointF;
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float x() {
        if (!this.unitConfig.isBio) {
            return -1.0f;
        }
        return super.x();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bU() {
        return this.unitConfig.isBug;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float bV() {
        if (isAlive() && !this.unitEffectManager.a() && this.unitConfig.isAir) {
            return this.unitEffectManager.e;
        }
        return super.bV();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bW */
    public float getUnitTeamData() {
        if (this.y.maxEnergy > 0.0f && this.f0cB < this.y.maxEnergy && this.unitConfig.isBuilder) {
            return this.f0cB / this.y.maxEnergy;
        }
        if (this.unitEnergyMax > 0.0f && this.shield < this.unitEnergyMax && this.unitConfig.canBuildOnGround) {
            return this.shield / this.unitEnergyMax;
        }
        if (this.y.maxEnergy == 0.0f && this.unitEnergyMax == 0.0f) {
            if (this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicOrTags != -1 && this.movementLevels[this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicOrTags].rotation > 0.0f) {
                return 1.0f - (this.movementLevels[this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicOrTags].rotation / b(this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicOrTags));
            }
            if (this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicAndTags != -1 && this.movementLevels[this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicAndTags].speed != 0.0f) {
                return this.movementLevels[this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicAndTags].speed / e(this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsOrLogicAndTags);
            }
        }
        return super.getUnitTeamData();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean f(float f) {
        return super.f(f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void p(float f) {
        int i = this.unitConfig.onDestroyListeners.size;
        if (i > 0) {
            Object[] objArrA = this.unitConfig.onDestroyListeners.a();
            for (int i2 = i - 1; i2 >= 0; i2--) {
                ((CustomUnitRenderHook) objArrA[i2]).f(this, f);
            }
        }
        super.p(f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
        super.e(f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ca */
    public strictfp void getUnitAISettings() {
        if (this.unitEffectManager.b != null) {
            final GameEngine instance = GameEngine.getInstance();
            instance.graphicsEngine2.a((float)(int)(this.posX - instance.viewpointXSnapped), (float)(int)(this.posY - instance.viewpointYSnapped), (float)(int)(this.unitEffectManager.b.x - instance.viewpointXSnapped), (float)(int)(this.unitEffectManager.b.y - instance.viewpointYSnapped), FactoryWithQueue.y);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void cb() {
        boolean z = false;
        if ((this.y.maxAttackRange > 70.0f && this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogicAndTags && this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogic == null) || (this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogic != null && this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicAndTagsAndLogic.booleanValue())) {
            GameViewUtils.a((BaseUnit) this, m(), true);
            z = true;
        } else if (this.unitConfig.isBuildingUnit && this.y.nanoRange > 50 && !this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogicAndTags) {
            GameViewUtils.a((BaseUnit) this, this.y.nanoRange, true);
            z = true;
        }
        if (!this.unitConfig.customArms.isEmpty()) {
            for (Object object : this.unitConfig.customArms) {
                GameViewUtils.a((BaseUnit) this, ((CustomLimitedRange) object).value, true);
                z = true;
            }
        }
        if (this.unitConfig.targetBuildings) {
            int techLevel = getTechLevel();
            for (int i = 0; i < techLevel; i++) {
                TurretConfig turretConfig = this.unitConfig.turrets[i];
                if (turretConfig.animationSet3 != null && turretConfig.al > 0.0f) {
                    int i2 = 90;
                    if (z) {
                        i2 = 40;
                    }
                    GameViewUtils.a((BaseUnit) this, turretConfig.al, Color.a(i2, 35, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, 35), 1, true);
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void d(float f) {
        super.d(f);
        if (this.isDestroyed) {
            return;
        }
        this.unitCustomComponents.a(f, this);
        if (this.unitConfig.disableCustomShields != null) {
            GameEngine gameEngine = GameEngine.getInstance();
            float f2 = this.posX - gameEngine.viewpointXSnapped;
            float f3 = (this.posY - gameEngine.viewpointYSnapped) - this.posZ;
            float maxHealth = getMaxHealth();
            if (maxHealth != 1.0f) {
                gameEngine.graphicsEngine2.k();
                gameEngine.graphicsEngine2.a(maxHealth, maxHealth, f2, f3);
            }
            if (this.unitConfig.shield) {
                int i = this.unitConfig.disableCustomShields.p;
                int i2 = this.unitConfig.disableCustomShields.q;
                int i3 = i / 2;
                int i4 = i2 / 2;
                du.a(f2 - i3, f3 - i4, f2 + i3, f3 + i4);
                dv.a(0, 0, 0 + i, 0 + i2);
            } else {
                du.a(f2 - this.eu, f3 - this.ev, f2 + this.eu, f3 + this.ev);
                dv.a(0, 0, 0 + this.es, 0 + this.et);
            }
            gameEngine.graphicsEngine2.a(this.unitConfig.disableCustomShields, dv, du, getRenderPaint());
            if (maxHealth != 1.0f) {
                gameEngine.graphicsEngine2.l();
            }
        }
        if (this.unitConfig.energyCanBeRecievedFromInAnotherUnit && this.legInstances != null && !this.isDestroyed) {
            CustomUnitLegController.a(this, f, false, true);
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: aM */
    public float getPathStepScale() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: aN */
    public Paint getRenderPaint() {
        return super.getRenderPaint();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: aV */
    public boolean useVelocityExtendedRange() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bI() {
        return this.unitConfig.isBuildingUnit;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean q() {
        if (this.parentEntity != null && this.parentEntity.deceleration < 1.0f) {
            return true;
        }
        return this.unitConfig.transportUnitsUnloadRight;
    }

    /* JADX INFO: renamed from: H */
    private boolean getTransportedUnitCount(BaseUnit baseUnit) {
        if (baseUnit.q() || baseUnit == this) {
            return false;
        }
        if (baseUnit.bI()) {
            if (this.unitConfig.flag_f1aU) {
                return true;
            }
            return false;
        }
        if (this.unitConfig.f1aV) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: a */
    public boolean canRepairTarget(BaseUnit baseUnit) {
        if (this.unitConfig.f26fo != null && !AnimationTag.a(this.unitConfig.f26fo, baseUnit.getUnitCombatAnimation())) {
            return false;
        }
        return getTransportedUnitCount(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: l */
    public boolean canReclaimTarget(BaseUnit baseUnit) {
        if (baseUnit.getUnitHealthPercent() != 0.0f && h(baseUnit, true)) {
            return true;
        }
        if (this.unitConfig.f25fn != null && !AnimationTag.a(this.unitConfig.f25fn, baseUnit.getUnitCombatAnimation())) {
            return false;
        }
        return getTransportedUnitCount(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void m(BaseUnit baseUnit) {
        FormationGroup formationGroup;
        if (this.unitConfig.isTurret) {
            UnitCommand unitCommandAr = getCurrentWaypoint();
            if (unitCommandAr != null && (formationGroup = unitCommandAr.transportTarget) != null) {
                formationGroup.a(unitCommandAr);
            }
            if (this.isSelected && baseUnit != null) {
                GameEngine.getInstance().gameUI.addToSelection(baseUnit);
            }
            getUnitAICondition();
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: al */
    public boolean isTurretUnit() {
        if (this.unitConfig.isTurret) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: aj */
    public boolean canUnitAttack() {
        return this.unitConfig.hasTransportCapability;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cu */
    public boolean getUnitAIPathfindMaxNodes() {
        return this.unitConfig.f47fN;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ak */
    public boolean canMove() {
        return this.unitConfig.isFlying;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean g(BaseUnit baseUnit, boolean z) {
        if (!h(baseUnit, z)) {
            return false;
        }
        if (z && baseUnit.c((OrderableUnit) this)) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean h(BaseUnit baseUnit, boolean z) {
        if (!this.unitConfig.f22fk) {
            return false;
        }
        if (this.unitConfig.f23fl != null && !AnimationTag.a(this.unitConfig.f23fl, baseUnit.getUnitCombatAnimation())) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cS */
    public int getUnitType() {
        return this.unitConfig.f24fm;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bJ() {
        return this.unitConfig.f29fu;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
        super.a(f, z);
        if (!this.isDestroyed && canMove()) {
            if (isCurrentCommandReclaim()) {
                if (!this.unitConfig.createNeutral) {
                    BuilderUnit.drawTargetPriorityPoints(f, this);
                }
            } else if (!this.unitConfig.showActionsAndWaypoints) {
                BuilderUnit.drawTargetPriorityPoints(f, this);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean o() {
        return this.unitConfig.canBeBuiltByAndTagsAndLogic;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean p() {
        return this.unitConfig.canBeBuiltByAndTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cO */
    public boolean isUnitIcon() {
        return this.unitConfig.canBeBuiltByOrTagsAndLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void f(PlayerTeam playerTeam) {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicOrTags) {
            setUnitTeam(PlayerTeam.TEAM_UNKNOWN);
        } else if (this.unitConfig.canBeBuiltByAndTagsAndLogicAndTags && this.transportedUnits.size() == 0) {
            setUnitTeam(PlayerTeam.TEAM_ALL);
        } else {
            super.f(playerTeam);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: B */
    public void getUnitType(BaseUnit baseUnit) {
        super.getUnitType(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: g */
    public float getUnitHealthPercent() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cQ */
    public int getUnitTypeName() {
        return this.unitConfig.canBeBuiltByAndTagsOrLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cR */
    public AnimationSet getUnitTypeId() {
        return this.unitConfig.canBeBuiltByAndTagsOrLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cP */
    public void getUnitCategory() {
        if (this.unitConfig.isExtractor == 0.0f && getUnitHealthPercent() > 0.0f) {
            PlayerTeam.b((BaseUnit) this);
            this.deceleration = 1.0f;
            PlayerTeam.c(this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cp */
    public ActionId getUnitAIPathfindPath() {
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags != 0) {
            return HovercraftUnit.i.getActionId();
        }
        return super.getUnitAIPathfindPath();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float L(int i) {
        return this.unitConfig.turrets[i].shootDelay2;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: K */
    public PointF getShadowTexture(int i) {
        Projectile projectile;
        PointF pointFK = super.getShadowTexture(i);
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicOrTagsAndLogicOrTags) {
            if (this.unitConfig.f50fR[this.unitConfig.turrets[i].a(this)].M && this.G != null && (projectile = this.G[i]) != null && !projectile.isDestroyed) {
                pointFK.x += projectile.K;
                pointFK.y += projectile.L;
            }
        }
        return pointFK;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public float bd() {
        return this.y.maxEnergy;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public UnitBehaviorType be() {
        return this.unitConfig.attackMovementType;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean bf() {
        if (this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsAndLogicAndTagsOrLogic) {
            return false;
        }
        if (this.unitConfig.attackMovementType == UnitBehaviorType.bomber) {
            UnitCommand unitCommandAr = getCurrentWaypoint();
            if ((unitCommandAr != null && (unitCommandAr.getCommandType() == UnitCommandType.attackMove || unitCommandAr.getCommandType() == UnitCommandType.patrol)) || this.attackMode == AttackMode.outOfRange) {
                return true;
            }
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bX */
    public boolean isUnitAtPositionX() {
        return this.v;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean bg() {
        return this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogicAndTagsAndLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float bc() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsAndLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void f(float f, float f2) {
        super.f(f, f2);
        a(UnitEventType.teleported);
        float f3 = this.rotationSpeed;
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsAndLogic) {
            f3 = this.movementLevels[this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic].targetX;
        }
        this.dP = this.posX;
        this.dQ = this.posY;
        this.dR = this.posZ;
        this.dS = f3;
    }

    public void du() {
        if (this.unitConfig.energyDisplayName == null && this.legInstances == null) {
            return;
        }
        if (this.unitConfig.energyDisplayName == null || this.unitConfig.energyDisplayName.length == 0) {
            this.legInstances = null;
            return;
        }
        if (this.legInstances != null && this.legInstances.length == this.unitConfig.energyDisplayName.length) {
            return;
        }
        this.legInstances = new LegInstance[this.unitConfig.energyDisplayName.length];
        for (int i = 0; i < this.unitConfig.energyDisplayName.length; i++) {
            LegInstance legInstance = new LegInstance();
            this.legInstances[i] = legInstance;
            legInstance.a = i;
            legInstance.s = this.unitConfig.energyDisplayName[i].r;
        }
        float f = this.rotationSpeed;
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicOrTagsAndLogic) {
            f = this.movementLevels[this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTagsAndLogic].targetX;
        }
        this.dP = this.posX;
        this.dQ = this.posY;
        this.dR = this.posZ;
        this.dS = f;
        dv();
        for (int i2 = 0; i2 < this.unitConfig.energyDisplayName.length; i2++) {
            this.legInstances[i2].m = true;
        }
    }

    public void dv() {
        CustomUnitLegController.a.b(this, 0.0f);
    }

    public void dB() {
        if (this.legInstances != null) {
            for (int i = 0; i < this.legInstances.length; i++) {
                this.legInstances[i].n = true;
                this.legInstances[i].m = true;
            }
            dv();
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: aT */
    public int getDefaultTurretIndex() {
        if (this.unitConfig.defaultTurret == null) {
            return -1;
        }
        return this.unitConfig.defaultTurret.rotationSpeed;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public int s() {
        return this.y.fogOfWarSightRange;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public void c(boolean z) {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.unitTransportTarget != null || this.parentEntity != null) {
            return;
        }
        int i = this.y.fogOfWarSightRange;
        if (this.deceleration < 1.0f && customUnitConfig.canBeBuiltByAndTagsAndLogicOrTagsAndLogicAndTags != -1) {
            i = customUnitConfig.canBeBuiltByAndTagsAndLogicOrTagsAndLogicAndTags;
        }
        if (i > 0) {
            gameEngine.tileMap.updateFogVisibilityForTeamsAtWorldPoint(this.posX, this.posY, i, this.team, z);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect cc() {
        return this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ce */
    public Rect getUnitAIUpdateTime() {
        return this.unitConfig.canBeBuiltByOrTagsAndLogicOrTagsAndLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect cd() {
        return this.unitConfig.canBeBuiltByOrTagsAndLogicOrTagsAndLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean b(int i, float f) {
        float fC;
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        if (turretConfig.as != 0.0f) {
            boolean z = true;
            if (turretConfig.logicCondition3 != null && !turretConfig.logicCondition3.read(this)) {
                z = false;
            }
            if (z) {
                UnitMovementData unitMovementData = this.movementLevels[i];
                if (turretConfig.ar != 0.0f) {
                    fC = unitMovementData.targetX;
                } else if (!turretConfig.aq) {
                    fC = unitMovementData.targetY;
                } else {
                    fC = C(i);
                }
                unitMovementData.k += f;
                float f2 = f * turretConfig.rotationSpeed3;
                if (unitMovementData.l > 0.0f) {
                    if (unitMovementData.l < Float.POSITIVE_INFINITY && a(f2, fC + unitMovementData.l, i) == 0.0f) {
                        unitMovementData.l = Float.POSITIVE_INFINITY;
                    }
                } else if (unitMovementData.l > Float.NEGATIVE_INFINITY && a(f2, fC + unitMovementData.l, i) == 0.0f) {
                    unitMovementData.l = Float.NEGATIVE_INFINITY;
                }
                if (unitMovementData.k > turretConfig.at) {
                    unitMovementData.k = -Utility.readStreamToString(this, 0, (int) turretConfig.aw);
                    float fClamp = turretConfig.as;
                    if (turretConfig.shootSpeed4 > 0.0f) {
                        fClamp += Utility.clamp(this, 0.0f, turretConfig.shootSpeed4, i);
                    }
                    unitMovementData.l = unitMovementData.l > 0.0f ? -fClamp : fClamp;
                    return false;
                }
                return false;
            }
        }
        if (turretConfig.ar != 0.0f) {
            this.movementLevels[i].targetX += turretConfig.ar * f;
            if (this.movementLevels[i].targetX > 180.0f) {
                this.movementLevels[i].targetX -= 360.0f;
            }
            if (this.movementLevels[i].targetX < -180.0f) {
                this.movementLevels[i].targetX += 360.0f;
                return false;
            }
            return false;
        }
        return turretConfig.aq;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cw */
    public int getUnitAIPathfindIterations() {
        return this.unitConfig.f15eZ;
    }

    public ArrayList dC() {
        dU.clear();
        ArrayList<AbstractUnitAction> availableActions = getAvailableActions();
        if (!availableActions.isEmpty()) {
            for (AbstractUnitAction abstractUnitAction : availableActions) {
                if (abstractUnitAction instanceof CustomAction) {
                    CustomAction customAction = (CustomAction) abstractUnitAction;
                    if (customAction.c == ActionType.upgrade) {
                        dU.add(customAction);
                    }
                }
            }
        }
        return dU;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public ActionId cm() {
        ArrayList arrayListDC = dC();
        if (arrayListDC.size() > 0) {
            return ((AbstractUnitAction) arrayListDC.get(0)).getActionId();
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void clearAndAddAction(ArrayList arrayList) {
        arrayList.clear();
        ArrayList arrayListDC = dC();
        if (arrayListDC.size() < 2) {
            return;
        }
        arrayListDC.remove(0);
        Iterator it = arrayListDC.iterator();
        while (it.hasNext()) {
            arrayList.add(((AbstractUnitAction) it.next()).getActionId());
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cZ */
    public float getUnitAIState() {
        return this.unitConfig.canBeBuiltByOrTagsAndLogicOrTagsOrLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: da */
    public float getUnitAIPathfindStatus() {
        return this.unitConfig.canBeBuiltByOrTagsAndLogicOrTagsOrLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public void bv() {
        PlayerTeam.a((BaseUnit) this);
        this.unitEffectManager.a(true);
        super.bv();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: dc */
    public void getUnitAICombatState() {
        this.posZ = 170.0f;
        this.frameAnimationDelay = 1.5f;
        dB();
        super.getUnitAICombatState();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: dd */
    public boolean getUnitAICombatTarget() {
        return this.unitConfig.canBeBuiltByExtractor;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public int bp() {
        return this.unitConfig.pylonRequiresPower;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i, float f) {
        this.movementLevels[i].targetX += f;
        if (this.unitConfig.f51fU) {
            for (int i2 = 0; i2 < this.unitConfig.turrets.length; i2++) {
                if (this.unitConfig.turrets[i2].w == i) {
                    this.movementLevels[i2].targetX += f;
                    this.movementLevels[i2].a(2);
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: db */
    public float getUnitAIPathfindError() {
        return super.getUnitAIPathfindError() + this.unitConfig.canBeBuiltByAndTagsAndLogicOrTagsAndLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float H(int i) {
        return this.unitConfig.turrets[i].p;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float I(int i) {
        return this.unitConfig.turrets[i].q;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float J(int i) {
        return this.unitConfig.turrets[i].r;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public float setTarget(BaseUnit baseUnit, float f, Projectile projectile) {
        AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
        if (attachmentSlotDefinitionDn != null && this.parentEntity != null && attachmentSlotDefinitionDn.j) {
            int i = 0;
            if (projectile != null) {
                i = projectile.aD;
            }
            if (i >= 0) {
                Projectile projectileA = Projectile.a(projectile);
                if (attachmentSlotDefinitionDn.k) {
                    projectileA.am = 0.0f;
                }
                f = this.parentEntity.setTarget(baseUnit, f, projectileA);
                if (f < 0.0f) {
                    f = 0.0f;
                }
            }
        }
        if (mo193J()) {
            f = 0.0f;
        }
        if (this.y.armour > 0.0f && f > this.unitConfig.canBeBuiltByAndTagsAndLogicOrTagsOrLogic) {
            float f2 = this.y.armour;
            if (projectile != null) {
                f2 -= projectile.an;
            }
            if (f2 < 0.0f) {
                f2 = 0.0f;
            }
            f -= f2;
            if (f < this.unitConfig.canBeBuiltByAndTagsAndLogicOrTagsOrLogic) {
                f = this.unitConfig.canBeBuiltByAndTagsAndLogicOrTagsOrLogic;
            }
        }
        if (projectile != null) {
            a(UnitEventType.tookDamage, baseUnit, projectile.aE, (VariableScope) null);
        } else {
            a(UnitEventType.tookDamage, baseUnit);
        }
        return super.setTarget(baseUnit, f, projectile);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: aU */
    public float getBasePathingRange() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTagsOrLogic;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: ac */
    public boolean supportsIndependentTurretTargets() {
        if (!this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsOrLogicOrTagsOrLogic) {
            return false;
        }
        return super.supportsIndependentTurretTargets();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean a(CommandType commandType) {
        if (commandType == CommandType.attack) {
            return this.unitConfig.f0do.a();
        }
        if (commandType == CommandType.move) {
            return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsAndLogicAndTags.a();
        }
        if (commandType == CommandType.newSelection) {
            return this.unitConfig.canBeBuiltByOrTagsOrLogicOrTagsOrLogicOrTags.a();
        }
        return false;
    }

    public void b(UnitEventType unitEventType) {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        if (customUnitConfig.eventBindings.size == 0) {
            return;
        }
        Object[] objArrA = customUnitConfig.eventBindings.a();
        for (int i = customUnitConfig.eventBindings.size - 1; i >= 0; i--) {
            CustomEventBinding customEventBinding = (CustomEventBinding) objArrA[i];
            if (customEventBinding.a == unitEventType) {
                ec.x = this.posX;
                ec.y = this.posY;
                a(customEventBinding.b, ec, (BaseUnit) null, 0, 0);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void a(UnitEventType unitEventType, BaseUnit baseUnit, AnimationSet animationSet, VariableScope variableScope) {
        CustomUnitEventData customUnitEventData;
        CustomUnitConfig customUnitConfig = this.unitConfig;
        if (customUnitConfig.eventBindings.size == 0) {
            return;
        }
        Object[] objArrA = customUnitConfig.eventBindings.a();
        for (int i = customUnitConfig.eventBindings.size - 1; i >= 0; i--) {
            CustomEventBinding customEventBinding = (CustomEventBinding) objArrA[i];
            if (customEventBinding.a == unitEventType && (customEventBinding.d == null || AnimationTag.a(customEventBinding.d, animationSet))) {
                if (dX.size() > 0) {
                    customUnitEventData = (CustomUnitEventData) dX.b();
                } else {
                    customUnitEventData = new CustomUnitEventData();
                }
                customUnitEventData.eventInfo = customEventBinding;
                customUnitEventData.customUnit = this;
                customUnitEventData.unit = baseUnit;
                customUnitEventData.animationSet = animationSet;
                customUnitEventData.variableScope = variableScope;
                dW.add(customUnitEventData);
            }
        }
    }

    public static void s(float f) {
    }

    public static void dD() {
        if (dW.size == 0) {
            return;
        }
        dW = new FastArrayList();
    }

    public static void a(float f, int i) {
        int i2;
        if (dW.size == 0) {
            return;
        }
        int i3 = 0;
        while (true) {
            if (i3 >= 105 || (i2 = dW.size) == 0) {
                break;
            }
            Object[] objArrA = dW.a();
            for (int i4 = i2 - 1; i4 >= 0; i4--) {
                CustomUnitEventData customUnitEventData = (CustomUnitEventData) objArrA[i4];
                CustomEventBinding customEventBinding = customUnitEventData.eventInfo;
                CustomUnit customUnit = customUnitEventData.customUnit;
                if (customEventBinding.c == customUnit.unitConfig) {
                    ec.x = customUnit.posX;
                    ec.y = customUnit.posY;
                    PointF pointF = ec;
                    LogicBoolean.setContextEventSource(customUnitEventData);
                    customUnit.a(customEventBinding.b, pointF, (BaseUnit) null, 0, 0);
                    LogicBoolean.clearContext();
                }
            }
            if (i3 < 105) {
                if (i2 == dW.size) {
                    break;
                }
                Object[] objArrA2 = dW.a();
                int i5 = 0;
                for (int i6 = i2; i6 < dW.size; i6++) {
                    if (i3 < ((CustomUnitEventData) objArrA2[i6]).eventInfo.e) {
                        i5++;
                    }
                }
                if (i5 <= 0) {
                    break;
                }
                dV.clear();
                for (int i7 = 0; i7 < dW.size; i7++) {
                    CustomUnitEventData customUnitEventData2 = (CustomUnitEventData) objArrA2[i7];
                    boolean z = true;
                    if (i7 < i2) {
                        z = false;
                    } else if (i3 >= customUnitEventData2.eventInfo.e) {
                        z = false;
                    }
                    if (!z) {
                        customUnitEventData2.a();
                        dX.add(customUnitEventData2);
                    } else {
                        dV.add(customUnitEventData2);
                    }
                }
                dW.clear();
                FastArrayList fastArrayList = dV;
                dV = dW;
                dW = fastArrayList;
                if (i5 != dW.size) {
                    GameEngine.isInSpace("processAllQueuedEvents: " + i5 + "!=" + dW.size);
                }
                i3++;
            } else {
                GameEngine.isInSpace("processAllQueuedEvents: recursion limit reached");
                break;
            }
        }
        Object[] objArrA3 = dW.a();
        for (int i8 = dW.size - 1; i8 >= 0; i8--) {
            CustomUnitEventData customUnitEventData3 = (CustomUnitEventData) objArrA3[i8];
            customUnitEventData3.a();
            dX.add(customUnitEventData3);
        }
        dW.clear();
    }

    public static void dE() {
    }

    public void b(float f, boolean z) {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        if (!customUnitConfig.f52fX) {
            return;
        }
        if (this.w != 0.0f) {
            this.w = Utility.moveTowardsZero(this.w, f);
            if (this.w == 0.0f) {
                z = true;
            } else {
                return;
            }
        }
        CustomUnitCondition[] customUnitConditionArr = customUnitConfig.autoTriggerConditionsEveryFrame;
        if (customUnitConditionArr != null) {
            a(f, customUnitConditionArr);
            if (customUnitConfig != this.unitConfig) {
                return;
            }
        }
        CustomUnitCondition[] customUnitConditionArr2 = customUnitConfig.autoTriggerConditionsEvery4Frames;
        if (customUnitConditionArr2 != null && (((int) (((long) GameEngine.getInstance().currentTick) + this.objectId)) % 4 == 0 || z)) {
            a(f, customUnitConditionArr2);
            if (customUnitConfig != this.unitConfig) {
                return;
            }
        }
        CustomUnitCondition[] customUnitConditionArr3 = customUnitConfig.autoTriggerConditionsEvery8Frames;
        if (customUnitConditionArr3 != null) {
            if (((int) (((long) GameEngine.getInstance().currentTick) + this.objectId)) % 8 == 0 || z) {
                a(f, customUnitConditionArr3);
                if (customUnitConfig != this.unitConfig) {
                }
            }
        }
    }

    public void a(float f, CustomUnitCondition[] customUnitConditionArr) {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        GameEngine gameEngine = GameEngine.getInstance();
        for (CustomUnitCondition customUnitCondition : customUnitConditionArr) {
            if (customUnitCondition.logicBoolean.read(this)) {
                if (gameEngine.isNetworkGameActive && gameEngine.isNetworkServer && this.isSelected) {
                    if (customUnitCondition.action != null) {
                        String str = VariableScope.nullOrMissingString + customUnitCondition.action.getActionIdString();
                    }
                    String str2 = "autoTrigger fired on: " + getVelocityX() + " details: " + customUnitCondition.logicBoolean.getDebugDetails(this);
                    GameEngine.isInSpace(str2);
                    gameEngine.gameUI.warLogDisplay.a(str2, 2000);
                }
                ec.x = this.posX;
                ec.y = this.posY;
                a(customUnitCondition.action, ec, (BaseUnit) null, 0, 0);
                this.w = this.unitConfig.autoTriggerCooldownTime;
                if (customUnitConfig != this.unitConfig) {
                    return;
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: de */
    public AnimationSet getUnitCombatAnimation() {
        return this.currentActionHandler;
    }

    public void a(AnimationSet animationSet, boolean z) {
        if (z) {
            this.currentActionHandler = animationSet;
            return;
        }
        PlayerTeam.b((BaseUnit) this);
        this.currentActionHandler = animationSet;
        PlayerTeam.c(this);
    }

    public void j(boolean z) {
        a(this.unitConfig.image_shield, z);
    }

    public void a(AnimationSet animationSet) {
        AnimationSet unitCombatAnimation = getUnitCombatAnimation();
        if (unitCombatAnimation == null || unitCombatAnimation.b() == 0) {
            a(animationSet, false);
        } else {
            if (AnimationTag.b(unitCombatAnimation, animationSet)) {
                return;
            }
            CustomUnitAnimationTags customUnitAnimationTags = new CustomUnitAnimationTags(unitCombatAnimation);
            if (customUnitAnimationTags.a(animationSet)) {
                a(customUnitAnimationTags.a(), false);
            }
        }
    }

    public void b(AnimationSet animationSet) {
        AnimationSet unitCombatAnimation = getUnitCombatAnimation();
        if (unitCombatAnimation == null || unitCombatAnimation.b() == 0 || !AnimationTag.a(animationSet, unitCombatAnimation)) {
            return;
        }
        CustomUnitAnimationTags customUnitAnimationTags = new CustomUnitAnimationTags(unitCombatAnimation);
        if (customUnitAnimationTags.b(animationSet)) {
            a(customUnitAnimationTags.a(), false);
        }
    }

    public final void dF() {
        if (this.unitConfig.movementType == UnitMovementType.AIR) {
            if (i()) {
                S(5);
            } else if (getUnitAIPathfindCost() && this.spawnExitLockTimer == 0.0f) {
                S(3);
            } else {
                S(2);
            }
        } else if (this.spawnExitLockTimer == 0.0f) {
            S(this.unitConfig.canBeBuiltByOrTagsAndLogicOrTagsAndLogic);
        } else {
            S(2);
        }
        this.value2 = 0;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ck */
    public boolean isUnitEnergyCost() {
        if (!this.unitConfig.f56gi) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.TransportUnitInterface
    public boolean f() {
        return this.unitConfig.f17fd.read(this);
    }

    @Override // com.corrodinggames.rts.game.units.TransportUnitInterface
    public boolean j() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Vector3D D(int i) {
        UnitMovementData unitMovementData = this.movementLevels[i];
        TurretConfig turretConfig = this.unitConfig.turrets[i];
        float f = turretConfig.shootSpeed2;
        float f2 = turretConfig.shootAccuracy2;
        if (turretConfig.Z != 0.0f && unitMovementData.m) {
            f2 += turretConfig.Z;
        }
        float f3 = E() ? this.rotationSpeed : unitMovementData.targetX;
        Vector3D vector3DF = F(i);
        float fFastCos = Utility.fastCos(f3);
        float fFastSin = Utility.fastSin(f3);
        float f4 = vector3DF.a;
        float f5 = vector3DF.b;
        float f6 = vector3DF.c;
        ee.a = f4 + ((fFastCos * f) - (fFastSin * f2));
        ee.b = f5 + (fFastSin * f) + (fFastCos * f2);
        ee.c = f6 + turretConfig.shootAngle2;
        return ee;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF E(int i) {
        Vector3D vector3DD = D(i);
        ef.x = vector3DD.a;
        ef.y = vector3DD.b;
        return ef;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cl */
    public boolean isUnitBuilding() {
        return this.unitConfig.f44fJ;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cn */
    public float getUnitAIPathfindState() {
        return this.unitConfig.f45fK;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, float f, int i) {
        if (this.unitConfig.canBeBuiltByAndTagsOrLogicOrTagsOrLogicAndTags != null) {
            this.animationController.a(this.unitConfig.canBeBuiltByAndTagsOrLogicOrTagsOrLogicAndTags, 5);
        }
        if (this.unitConfig.showActionsAndWaypoints) {
            this.turretTurnSpeed = Utility.moveTowardsZero(this.turretTurnSpeed, f);
            if (this.turretTurnSpeed == 0.0f) {
                this.turretTurnSpeed = this.unitConfig.showOnHpHover;
                if (this.unitConfig.showInEditor != null) {
                    UnitMovementData unitMovementData = this.movementLevels[i];
                    PointF pointFE = E(i);
                    this.unitConfig.showInEditor.a(pointFE.x, pointFE.y, this.posZ, unitMovementData.targetX, this);
                }
                if (this.unitConfig.stayNeutral != null) {
                    this.unitConfig.stayNeutral.a(baseUnit.posX, baseUnit.posY, baseUnit.posZ, baseUnit.rotationSpeed, baseUnit);
                    return;
                }
                return;
            }
            return;
        }
        super.a(baseUnit, f, i);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void b(BaseUnit baseUnit, float f, int i) {
        if (this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsAndLogicOrTags != null) {
            this.animationController.a(this.unitConfig.canBeBuiltByAndTagsOrLogicAndTagsAndLogicOrTags, 5);
        }
        if (this.unitConfig.createNeutral) {
            this.turretTurnSpeed = Utility.moveTowardsZero(this.turretTurnSpeed, f);
            if (this.turretTurnSpeed == 0.0f) {
                this.turretTurnSpeed = this.unitConfig.createOnAggressiveTeam;
                if (this.unitConfig.createOnNeutralTeam != null) {
                    UnitMovementData unitMovementData = this.movementLevels[i];
                    PointF pointFE = E(i);
                    this.unitConfig.createOnNeutralTeam.a(pointFE.x, pointFE.y, this.posZ, unitMovementData.targetX, this);
                }
                if (this.unitConfig.createOnSameTeamAsParent != null) {
                    this.unitConfig.createOnSameTeamAsParent.a(baseUnit.posX, baseUnit.posY, baseUnit.posZ, baseUnit.rotationSpeed, baseUnit);
                    return;
                }
                return;
            }
            return;
        }
        super.b(baseUnit, f, i);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cg */
    public boolean getUnitAIPriority() {
        return this.y.m;
    }

    public boolean dG() {
        if (this.unitConfig.lockTurretWithBody_ignoreBaseTurret != null && !this.unitConfig.lockTurretWithBody_ignoreBaseTurret.read(this)) {
            return false;
        }
        if (!this.y.m) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine.playerTeam.c(this.team) && !gameEngine.playerTeam.addCredits()) {
                return false;
            }
        }
        AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
        if (attachmentSlotDefinitionDn != null && !attachmentSlotDefinitionDn.o) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean t() {
        AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
        if (attachmentSlotDefinitionDn != null && attachmentSlotDefinitionDn.m) {
            return true;
        }
        return this.unitConfig.transportUnitsUnloadAndGiveOrder;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cV */
    public boolean getUnitState() {
        AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
        if (attachmentSlotDefinitionDn != null && attachmentSlotDefinitionDn.n) {
            return true;
        }
        return this.unitConfig.transportSlotsNeeded;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean d(BaseUnit baseUnit) {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        return ((customUnitConfig.deathAnimation != null && !AnimationTag.a(customUnitConfig.deathAnimation, baseUnit.getUnitCombatAnimation())) || dH() || customUnitConfig.transportSwitchToSlots) ? false : true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cW */
    public boolean getUnitStatus() {
        return this.unitConfig.showStatusBar;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cT */
    public boolean getUnitVersion() {
        return this.unitConfig.transportSwitchToSlots || u() || (this.deceleration < 1.0f && this.unitConfig.isExtractor <= 0.0f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: dh */
    public AnimationSet getUnitAICombatTimer() {
        return this.unitConfig.image_turret;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: am */
    public float getPassiveTargetSearchRangeBonus() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsOrLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: an */
    public boolean isChaseAttackMode() {
        return super.isChaseAttackMode() || this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsAndLogicAndTagsAndLogicAndTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean a(GameEngine gameEngine) {
        if (!gameEngine.cameraFollowSpeed.b(this.posX, this.posY)) {
            if (!this.unitConfig.isHover) {
                return false;
            }
            boolean z = false;
            if (this.unitConfig.tech != null) {
                CustomUnitConfig.tempRect.a(this.unitConfig.tech);
                CustomUnitConfig.tempRect.a((int) this.posX, (int) this.posY);
                if (gameEngine.cameraSmoothing.b(CustomUnitConfig.tempRect)) {
                    z = true;
                }
            }
            if (gameEngine.cameraFollowSpeed.b(this.posX, this.posY - this.posZ)) {
                z = true;
            }
            if (!z) {
                return false;
            }
        }
        if (this.unitTransportTarget != null) {
            return false;
        }
        if ((this.attachmentData != null && this.attachmentData.I) || !d(gameEngine.playerTeam) || !dG()) {
            return false;
        }
        return true;
    }

    public OrderableUnit a(AttachmentSlotDefinition attachmentSlotDefinition) {
        return AttachmentManagerHook.a(this, attachmentSlotDefinition);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public AttachmentSlotDefinition a(short s) {
        return AttachmentManagerHook.a(this, s);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean a(OrderableUnit orderableUnit, AttachmentSlotDefinition attachmentSlotDefinition) {
        if (orderableUnit == this) {
            return false;
        }
        if (attachmentSlotDefinition == null) {
            GameEngine.updatePaintTextSizeIfNeeded("attachRequest: No attachedSlotData");
            return false;
        }
        OrderableUnit orderableUnitA = AttachmentManagerHook.a(this, attachmentSlotDefinition);
        if (orderableUnitA != null) {
            GameEngine.updatePaintTextSizeIfNeeded("attachRequest: a unit is already in slot (parent:" + getVelocityX() + " slot:" + attachmentSlotDefinition.b() + " existing:" + orderableUnitA.getVelocityY() + ")");
            return false;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        AttachmentManagerHook.a(this, attachmentSlotDefinition, orderableUnit);
        orderableUnit.parentEntity = this;
        orderableUnit.attachmentData = attachmentSlotDefinition;
        orderableUnit.unitTransportCapacity = gameEngine.lastTick;
        orderableUnit.isAttacking = false;
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean b(OrderableUnit orderableUnit) {
        if (orderableUnit.parentEntity != this) {
            GameEngine.updatePaintTextSizeIfNeeded("deattachRequest: unit is not attached");
            return false;
        }
        AttachmentSlotDefinition attachmentSlotDefinition = orderableUnit.attachmentData;
        if (attachmentSlotDefinition == null) {
            GameEngine.updatePaintTextSizeIfNeeded("deattachRequest: unit has no attachedSlotData");
            return false;
        }
        OrderableUnit orderableUnitA = AttachmentManagerHook.a(this, attachmentSlotDefinition);
        if (orderableUnitA == null) {
            GameEngine.updatePaintTextSizeIfNeeded("deattachRequest: Failed, no unit in slot");
            GameEngine.logWarningAndStack("deattachRequest");
            return false;
        }
        if (orderableUnit != orderableUnitA) {
            String strCB = "null";
            if (orderableUnitA != null) {
                strCB = orderableUnitA.getVelocityX();
            }
            GameEngine.updatePaintTextSizeIfNeeded("deattachRequest: unit and slot don't match - requested:" + orderableUnit.getVelocityX() + " current:" + strCB);
            return false;
        }
        if (this.transportedUnits.remove(orderableUnit)) {
            unloadTransportedUnit(orderableUnit);
        }
        AttachmentManagerHook.a(this, attachmentSlotDefinition, (OrderableUnit) null);
        orderableUnit.parentEntity = null;
        orderableUnit.attachmentData = null;
        orderableUnit.f_();
        a(UnitEventType.attachmentRemoved, this);
        return true;
    }

    public boolean dH() {
        AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
        if (attachmentSlotDefinitionDn != null && !attachmentSlotDefinitionDn.l) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: J */
    public boolean mo193J() {
        if (dH() || this.unitConfig.addResources) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: di */
    public void updateUnitCombatTimer() {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        if (!customUnitConfig.canBeBuiltByAndLogic.c()) {
            customUnitConfig.canBeBuiltByAndLogic.a(this);
        }
        if (!customUnitConfig.canBeBuiltByOrTagsAndLogic.c() && this.deceleration >= 1.0f) {
            customUnitConfig.canBeBuiltByOrTagsAndLogic.a(this);
        }
        super.updateUnitCombatTimer();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: dj */
    public void getUnitAICombatStateTime() {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        if (!customUnitConfig.canBeBuiltByAndLogic.c()) {
            customUnitConfig.canBeBuiltByAndLogic.h(this);
        }
        if (!customUnitConfig.canBeBuiltByOrTagsAndLogic.c() && this.deceleration >= 1.0f) {
            customUnitConfig.canBeBuiltByOrTagsAndLogic.h(this);
        }
        super.getUnitAICombatStateTime();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(UnitCommand unitCommand) {
        a(UnitEventType.newWaypointGivenByPlayer);
        AttachmentSlotDefinition attachmentSlotDefinitionDn = dn();
        if (attachmentSlotDefinitionDn != null && attachmentSlotDefinitionDn.H) {
            UnitCommandType commandType = unitCommand.getCommandType();
            if (commandType == UnitCommandType.attackMove || commandType == UnitCommandType.move) {
                bx();
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean c_() {
        CustomUnitConfig customUnitConfig = this.unitConfig;
        if (!customUnitConfig.cloakHiddenFromAllies && GameEngine.getInstance().playerTeam.c(this.team)) {
            return false;
        }
        return customUnitConfig.isCloaked;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean dk() {
        return this.unitConfig.alsoUseTurretImageForOtherSide;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean dl() {
        return this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsAndLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean dm() {
        return this.unitConfig.f7eL;
    }

    /* JADX INFO: renamed from: G */
    public boolean getUnitArmor(BaseUnit baseUnit) {
        int iDI = dI();
        int unitAIPathfindIterations = baseUnit.getUnitAIPathfindIterations();
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogicOrTags) {
            unitAIPathfindIterations = 1;
        }
        if (iDI + unitAIPathfindIterations <= this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicOrTagsOrLogicOrTags) {
            return true;
        }
        return false;
    }

    public int dI() {
        int unitAIPathfindIterations = 0;
        if (this.unitConfig.canBeBuiltByOrTagsOrLogicAndTagsOrLogicAndTagsAndLogicOrTags) {
            unitAIPathfindIterations = 0 + this.transportedUnits.size();
        } else if (this.transportedUnits.size > 0) {
            Iterator it = this.transportedUnits.iterator();
            while (it.hasNext()) {
                unitAIPathfindIterations += ((BaseUnit) it.next()).getUnitAIPathfindIterations();
            }
        }
        return unitAIPathfindIterations;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: e */
    public void isSelectable(PlayerTeam playerTeam) {
        AttachmentSlotDefinition attachmentSlotDefinitionDn;
        if (this.team == playerTeam) {
            return;
        }
        super.isSelectable(playerTeam);
        if (this.transportedUnits != null && !this.unitConfig.f13eX) {
            Iterator it = this.transportedUnits.iterator();
            while (it.hasNext()) {
                ((BaseUnit) it.next()).isSelectable(playerTeam);
            }
        }
        if (this.C != null) {
            for (BaseUnit baseUnit : this.C) {
                if (baseUnit != null && (attachmentSlotDefinitionDn = baseUnit.dn()) != null && !attachmentSlotDefinitionDn.z) {
                    baseUnit.isSelectable(playerTeam);
                }
            }
        }
        a(UnitEventType.teamChanged);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cN */
    public UnitPrice getUnitDisplayName() {
        return this.unitConfig.generationResources;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ch */
    public void updateUnitAI() {
        if (!this.unitConfig.pylonBuildWithOr) {
            super.updateUnitAI();
        } else if (this.currentHealth <= -1.0f) {
            this.currentHealth = -1.0f;
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bz */
    public FastArrayList getTransportedUnitList() {
        return this.transportedUnits;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public FastArrayList e(boolean z) {
        this.eg.clear();
        if (this.unitConfig.energyCanTransferToOtherUnits.size > 0) {
            AttachmentManagerHook.a(this, this.eg, z);
        }
        if (this.eg.size > 0) {
            return this.eg;
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: do */
    public float mo145do() {
        return this.unitConfig.canBeBuiltByOrTagsAndLogicAndTagsOrLogicOrTags;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean dp() {
        return this.unitConfig.isLand;
    }

    public void dJ() {
        if (this.y.a) {
            this.y = this.y.b();
        }
    }
}
