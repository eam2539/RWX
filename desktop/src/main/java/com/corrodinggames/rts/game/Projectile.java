package com.corrodinggames.rts.game;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.FogRevealer;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.buildings.BaseBuilding;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.BuiltInEffectType;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitSpawnList;
import com.corrodinggames.rts.game.units.custom.EffectTemplate;
import com.corrodinggames.rts.game.units.custom.UnitEventType;
import com.corrodinggames.rts.game.units.custom.UnitSpawner;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.PositionedObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.effects.Effect;
import com.corrodinggames.rts.gameFramework.effects.EffectEmitter;
import com.corrodinggames.rts.gameFramework.effects.EffectQuality;
import com.corrodinggames.rts.gameFramework.effects.EffectType;
import com.corrodinggames.rts.gameFramework.effects.SpriteSheet;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.ConnectionStatus;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import com.corrodinggames.rts.gameFramework.utility.UnitList;
import com.corrodinggames.rts.gameFramework.utility.Vector3D;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/f.class */
public class Projectile extends PositionedObject {
    public ProjectileTemplate g;
    public float h;
    public float i;
    public BaseUnit j;
    public short k;
    public BaseUnit l;
    public boolean m;
    public float n;
    public float o;
    public float p;
    public Projectile q;
    public float r;
    public float s;
    public float t;
    public float u;
    public float v;
    public float w;
    public float x;
    public float y;
    public boolean z;
    public boolean A;
    public boolean B;
    public boolean C;
    public boolean D;
    public boolean E;
    public float F;
    public boolean G;
    public float H;
    public float I;
    public float J;
    public float K;
    public float L;
    public boolean M;
    public float N;
    public float[] O;
    public short P;
    public short Q;
    public short R;
    public boolean S;
    public boolean T;
    public float U;
    public boolean V;
    public float W;
    public float X;
    public float Y;
    public float Z;
    public boolean aa;
    public boolean ab;
    public boolean ac;
    public boolean ad;
    public boolean ae;
    public boolean af;
    public float ag;
    public float ah;
    public float ai;
    public float aj;
    public float ak;
    public float al;
    public float am;
    public float an;
    public boolean ao;
    public FastArrayList ap;
    public int ar;
    public boolean as;
    public boolean at;
    public GameObject au;
    public int av;
    public float aw;
    public float ax;
    public float ay;
    public float az;
    public float aA;
    public boolean aB;
    public boolean aC;
    public int aD;
    public AnimationSet aE;
    public float aF;
    public boolean aG;
    public boolean aH;
    public float aI;
    public float aJ;
    public boolean aK;
    public float aL;
    public boolean aM;
    public float aN;
    public float aO;
    public Effect aP;
    public boolean aQ;
    public boolean aR;
    private boolean bn;
    public boolean aS;
    public float aT;
    public boolean aU;
    float aV;
    float aW;
    float aX;
    public boolean aY;
    public boolean aZ;
    public static final UnitList bi;
    public GamePaint bj;
    public static GamePaint bk;
    public static int bl;
    public static final FastArrayList<Projectile> a = new FastArrayList();
    private static final Projectile bm = new Projectile(true);
    static Texture b = null;
    static Texture c = null;
    static Texture d = null;
    static final Rect e = new Rect();
    static final RectF f = new RectF();
    static final int aq = Color.a(255, 255, 255, 255);
    public static final GamePaint ba = new GamePaint();
    public static final Paint bb = new Paint();
    public static final Paint bd = new Paint();
    public static final Paint be = new Paint();
    public static final Paint bf = new Paint();
    public static final Paint bg = new Paint();
    public static final Paint bh = new Paint();
    public static final Paint bc = new GamePaint();

    static {
        bc.b(-16777216);
        bc.c(108);
        bd.a(80, 255, 0, 0);
        bd.a(true);
        bd.a(5.0f);
        be.a(30, 255, 0, 0);
        be.a(true);
        be.a(8.0f);
        bf.a(80, 128, 166, 255);
        bf.a(true);
        bf.a(5.0f);
        bg.a(150, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_WAKEUP, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_CS, 255);
        bg.a(true);
        bg.a(3.0f);
        bh.a(110, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_WAKEUP, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_CS, 255);
        bh.a(true);
        bh.a(8.0f);
        bi = new UnitList();
        bk = null;
        bl = 0;
    }

    public Projectile(boolean z) {
        super(z);
        this.g = ProjectileTemplate.a;
        this.k = (short) -1;
        this.r = -1.0f;
        this.s = 0.1f;
        this.x = 2.0f;
        this.y = -1.0f;
        this.z = true;
        this.H = 1.0f;
        this.P = (short) -1;
        this.Q = (short) -1;
        this.R = (short) 0;
        this.S = true;
        this.V = false;
        this.W = 0.0f;
        this.X = 0.0f;
        this.ab = false;
        this.ac = false;
        this.ad = false;
        this.ae = true;
        this.ai = 1.0f;
        this.aj = 1.0f;
        this.ak = 1.0f;
        this.al = 1.0f;
        this.am = 1.0f;
        this.ar = aq;
        this.av = -1;
        this.aI = 40.0f;
        this.aJ = 60.0f;
        this.aK = false;
        this.aL = 2.0f;
        this.aR = true;
        this.aT = 0.0f;
        if (!z) {
            a.add(this);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void remove() {
        a.remove(this);
        super.remove();
    }

    public static Projectile a(Projectile projectile) {
        Projectile projectile2 = bm;
        projectile2.aD = -1;
        if (projectile == null) {
            projectile2.am = 1.0f;
            projectile2.ak = 1.0f;
            projectile2.al = 1.0f;
            projectile2.an = 0.0f;
        } else {
            projectile2.am = projectile.am;
            projectile2.ak = projectile.ak;
            projectile2.al = projectile.al;
            projectile2.an = projectile.an;
        }
        return projectile2;
    }

    public void a(BaseUnit baseUnit, float f2, float f3, float f4) {
        this.j = baseUnit;
        this.posX = f2;
        this.posY = f3;
        this.posZ = f4;
        this.bn = false;
        this.V = false;
    }

    public void b() {
        if (this.D) {
            GameEngine gameEngine = GameEngine.getInstance();
            Effect effectCreateSmallExplosionInternal = gameEngine.effectManager.createSmallExplosionInternal(this.posX, this.posY, this.posZ, 0);
            if (effectCreateSmallExplosionInternal != null) {
                effectCreateSmallExplosionInternal.G = 0.7f;
                effectCreateSmallExplosionInternal.F = 2.1f;
                effectCreateSmallExplosionInternal.ar = (short) 2;
                effectCreateSmallExplosionInternal.V = 90.0f;
                effectCreateSmallExplosionInternal.W = effectCreateSmallExplosionInternal.V;
            }
            gameEngine.soundEngine.playSound(SoundEngine.buildingExplodeSound, 0.8f, this.posX, this.posY);
        }
        remove();
    }

    @Override // com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.h);
        gameOutputStream.writeUnitIdOrNullBaseUnit(this.j);
        gameOutputStream.writeUnitIdOrNullBaseUnit(this.l);
        gameOutputStream.writeFloat(this.t);
        gameOutputStream.writeInt(99);
        gameOutputStream.writeBoolean(this.A);
        gameOutputStream.writeBoolean(this.B);
        gameOutputStream.writeBoolean(this.S);
        gameOutputStream.writeBoolean(this.T);
        gameOutputStream.writeFloat(this.U);
        gameOutputStream.writeFloat(this.Y);
        gameOutputStream.writeFloat(this.Z);
        gameOutputStream.writeInt(this.ar);
        gameOutputStream.writeBoolean(this.aH);
        gameOutputStream.writeFloat(this.aI);
        gameOutputStream.writeFloat(this.aJ);
        gameOutputStream.writeBoolean(this.aK);
        gameOutputStream.writeFloat(this.aL);
        gameOutputStream.writeBoolean(this.aM);
        gameOutputStream.writeFloat(this.aN);
        gameOutputStream.writeBoolean(this.aQ);
        gameOutputStream.writeBoolean(this.aR);
        gameOutputStream.writeBoolean(this.bn);
        gameOutputStream.writeBoolean(this.aS);
        gameOutputStream.writeBoolean(this.M);
        gameOutputStream.writeShort(this.P);
        gameOutputStream.writeFloat(this.r);
        gameOutputStream.writeFloat(this.s);
        gameOutputStream.writeBoolean(this.as);
        gameOutputStream.writeBoolean(this.at);
        gameOutputStream.writeFloat(this.az);
        gameOutputStream.writeFloat(this.aA);
        gameOutputStream.writeBoolean(this.aB);
        gameOutputStream.writeBoolean(this.aC);
        gameOutputStream.writeBoolean(false);
        gameOutputStream.writeFloat(0.0f);
        gameOutputStream.writeFloat(0.0f);
        gameOutputStream.writeBoolean(this.E);
        gameOutputStream.writeFloat(this.F);
        gameOutputStream.writeFloat(this.J);
        gameOutputStream.writeFloat(this.K);
        gameOutputStream.writeFloat(this.L);
        gameOutputStream.writeBoolean(this.m);
        gameOutputStream.writeFloat(this.n);
        gameOutputStream.writeFloat(this.o);
        gameOutputStream.writeBoolean(this.C);
        gameOutputStream.writeBoolean(this.D);
        gameOutputStream.writeObjectId(this.q);
        gameOutputStream.writeFloat(this.aV);
        gameOutputStream.writeFloat(this.aW);
        gameOutputStream.writeFloat(this.aX);
        gameOutputStream.writeBoolean(this.V);
        gameOutputStream.writeFloat(this.W);
        gameOutputStream.writeFloat(this.X);
        gameOutputStream.writeBoolean(this.aU);
        gameOutputStream.writeShort(this.R);
        gameOutputStream.writeBoolean(this.ao);
        gameOutputStream.startBlockInternal(this.ap);
        gameOutputStream.writeShort(this.Q);
        gameOutputStream.writeFloat(this.x);
        gameOutputStream.writeBoolean(this.aa);
        gameOutputStream.writeBoolean(this.ad);
        gameOutputStream.writeBoolean(this.G);
        gameOutputStream.writeFloat(this.H);
        gameOutputStream.writeBoolean(this.ae);
        gameOutputStream.writeBoolean(this.aG);
        gameOutputStream.writeBoolean(this.z);
        gameOutputStream.writeFloat(this.y);
        gameOutputStream.writeFloat(this.aO);
        gameOutputStream.writeFloat(this.i);
        gameOutputStream.writeBoolean(this.aY);
        gameOutputStream.writeBoolean(this.af);
        gameOutputStream.writeFloat(this.ag);
        gameOutputStream.writeFloat(this.ah);
        gameOutputStream.writeFloat(this.ai);
        gameOutputStream.writeFloat(this.aj);
        gameOutputStream.writeInt(0);
        gameOutputStream.writeFloat(0.0f);
        gameOutputStream.writeFloat(0.0f);
        gameOutputStream.writeUnitTypeId((UnitType) null);
        gameOutputStream.writeInt(0);
        gameOutputStream.writeBoolean(false);
        AnimationTag.a(this.aE, gameOutputStream);
        gameOutputStream.writeFloat(this.ak);
        gameOutputStream.writeFloat(this.al);
        gameOutputStream.writeBoolean(this.ab);
        gameOutputStream.writeBoolean(this.ac);
        gameOutputStream.writeFloat(this.an);
        gameOutputStream.writeBoolean(false);
        ProjectileTemplate.a(this.g, gameOutputStream);
        boolean z = (this.au == null || this.au.isDestroyed) ? false : true;
        gameOutputStream.writeBoolean(z);
        if (z) {
            gameOutputStream.writeObjectId(this.au);
            gameOutputStream.writeFloat(this.aw);
            gameOutputStream.writeFloat(this.ax);
            gameOutputStream.writeFloat(this.ay);
        }
        gameOutputStream.writeShort(this.k);
        gameOutputStream.writeInt(this.aD);
        gameOutputStream.writeFloat(this.am);
        gameOutputStream.writeFloat(this.p);
        gameOutputStream.writeInt(this.av);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.h = gameInputStream.readFloat();
        this.j = gameInputStream.readBaseUnit();
        this.l = gameInputStream.startBlockNamed(ConnectionStatus.expected);
        this.t = gameInputStream.readFloat();
        this.x = gameInputStream.readInt();
        this.A = gameInputStream.readBoolean();
        this.B = gameInputStream.readBoolean();
        this.S = gameInputStream.readBoolean();
        this.T = gameInputStream.readBoolean();
        this.U = gameInputStream.readFloat();
        this.Y = gameInputStream.readFloat();
        this.Z = gameInputStream.readFloat();
        this.ar = gameInputStream.readInt();
        this.aH = gameInputStream.readBoolean();
        this.aI = gameInputStream.readFloat();
        this.aJ = gameInputStream.readFloat();
        this.aK = gameInputStream.readBoolean();
        this.aL = gameInputStream.readFloat();
        this.aM = gameInputStream.readBoolean();
        this.aN = gameInputStream.readFloat();
        this.aQ = gameInputStream.readBoolean();
        this.aR = gameInputStream.readBoolean();
        this.bn = gameInputStream.readBoolean();
        if (gameInputStream.getProtocolVersion() >= 7) {
            this.aS = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 13) {
            this.M = gameInputStream.readBoolean();
            this.P = gameInputStream.readShortValue();
        }
        if (gameInputStream.getProtocolVersion() >= 16) {
            this.r = gameInputStream.readFloat();
            this.s = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 17) {
            this.as = gameInputStream.readBoolean();
            this.at = gameInputStream.readBoolean();
            this.az = gameInputStream.readFloat();
            this.aA = gameInputStream.readFloat();
            this.aB = gameInputStream.readBoolean();
            this.aC = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 18) {
            gameInputStream.readBoolean();
            gameInputStream.readFloat();
            gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 28) {
            this.E = gameInputStream.readBoolean();
            this.F = gameInputStream.readFloat();
            this.J = gameInputStream.readFloat();
            this.K = gameInputStream.readFloat();
            this.L = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 29) {
            this.m = gameInputStream.readBoolean();
            this.n = gameInputStream.readFloat();
            this.o = gameInputStream.readFloat();
            this.C = gameInputStream.readBoolean();
            this.D = gameInputStream.readBoolean();
            this.q = (Projectile) gameInputStream.readBaseUnitWithStatus(Projectile.class);
            this.aV = gameInputStream.readFloat();
            this.aW = gameInputStream.readFloat();
            this.aX = gameInputStream.readFloat();
            this.V = gameInputStream.readBoolean();
            this.W = gameInputStream.readFloat();
            this.X = gameInputStream.readFloat();
            this.aU = gameInputStream.readBoolean();
            this.R = gameInputStream.readShortValue();
            this.ao = gameInputStream.readBoolean();
            FastArrayList fastArrayList = new FastArrayList();
            gameInputStream.assertMagicShort(fastArrayList, BaseUnit.class);
            if (fastArrayList.size() > 0) {
                this.ap = fastArrayList;
            }
            this.Q = gameInputStream.readShortValue();
        }
        if (gameInputStream.getProtocolVersion() >= 35) {
            this.x = gameInputStream.readFloat();
            this.aa = gameInputStream.readBoolean();
            this.ad = gameInputStream.readBoolean();
            this.G = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 38) {
            this.H = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 39) {
            this.ae = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 41) {
            this.aG = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 43) {
            this.z = gameInputStream.readBoolean();
            this.y = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 44) {
            this.aO = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 47) {
            this.i = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 48) {
            this.aY = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 59) {
            this.af = gameInputStream.readBoolean();
            this.ag = gameInputStream.readFloat();
            this.ah = gameInputStream.readFloat();
            this.ai = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 60) {
            this.aj = gameInputStream.readFloat();
            gameInputStream.readInt();
            gameInputStream.readFloat();
            gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 62) {
            gameInputStream.q();
            gameInputStream.readInt();
            gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 63) {
            this.aE = AnimationTag.a(gameInputStream);
        }
        if (gameInputStream.getProtocolVersion() >= 64) {
            this.ak = gameInputStream.readFloat();
            this.al = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 66) {
            this.ab = gameInputStream.readBoolean();
            this.ac = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 67 && gameInputStream.getProtocolVersion() < 78) {
            UnitSpawner.a(gameInputStream, true);
        }
        if (gameInputStream.getProtocolVersion() >= 68) {
            this.an = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 77) {
            gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 78) {
            this.g = ProjectileTemplate.a(gameInputStream);
        }
        if (gameInputStream.getProtocolVersion() >= 81 && gameInputStream.readBoolean()) {
            this.au = gameInputStream.readBaseUnitWithStatus(GameObject.class);
            this.aw = gameInputStream.readFloat();
            this.ax = gameInputStream.readFloat();
            this.ay = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 83) {
            this.k = gameInputStream.readShortValue();
            this.aD = gameInputStream.readInt();
        }
        if (gameInputStream.getProtocolVersion() >= 88) {
            this.am = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 89) {
            this.p = gameInputStream.readFloat();
            this.av = gameInputStream.readInt();
        }
        super.a(gameInputStream);
    }

    public static void c() {
        GameEngine gameEngine = GameEngine.getInstance();
        b = gameEngine.renderGraphicsEngine.a(com.corrodinggames.rts.R.drawable.projectiles);
        c = gameEngine.renderGraphicsEngine.a(com.corrodinggames.rts.R.drawable.projectiles2);
        d = gameEngine.renderGraphicsEngine.a(com.corrodinggames.rts.R.drawable.projectiles_large);
    }

    public void d() {
        this.aS = true;
    }

    public static Projectile a(BaseUnit baseUnit, float f2, float f3) {
        Projectile projectile = new Projectile(false);
        projectile.j = baseUnit;
        projectile.posX = f2;
        projectile.posY = f3;
        projectile.ar = Color.a(255, 100, 30, 30);
        projectile.drawOrder = baseUnit.drawOrder + 1;
        projectile.drawLayer = 4;
        return projectile;
    }

    public static Projectile a(BaseUnit baseUnit, float f2, float f3, float f4, int i) {
        Projectile projectileA = a(baseUnit, f2, f3);
        projectileA.posZ = f4;
        projectileA.k = (short) i;
        projectileA.I = Utility.clamp(baseUnit, 0.0f, 1.0f, baseUnit.unitFlags4);
        baseUnit.unitFlags4++;
        return projectileA;
    }

    public void a(BaseUnit baseUnit) {
        float angleBetweenPoints;
        if ((this.ag == 0.0f && this.ah == 0.0f) || baseUnit.bI()) {
            return;
        }
        if (Utility.distanceSq(this.aV, this.aW, baseUnit.posX, baseUnit.posY) > 100.0f) {
            angleBetweenPoints = Utility.getAngleBetweenPoints(this.aV, this.aW, baseUnit.posX, baseUnit.posY);
        } else {
            angleBetweenPoints = this.az;
        }
        float pushMass = this.ah + (this.ag / baseUnit.getPushMass());
        baseUnit.velocityX += Utility.fastCos(angleBetweenPoints) * pushMass;
        baseUnit.velocityY += Utility.fastSin(angleBetweenPoints) * pushMass;
    }

    public static void a(BaseUnit baseUnit, BaseUnit baseUnit2, float f2, Projectile projectile, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.isUnitInvincibilityEnabled && f2 > 0.0f) {
            f2 = 0.0f;
        }
        if (baseUnit2 != null && !baseUnit2.isDead) {
            if (projectile != null && projectile.g.convertHitToSourceTeam && baseUnit != null) {
                baseUnit2.isSelectable(baseUnit.team);
            }
            if (projectile != null) {
                if (projectile.ai != 1.0f && baseUnit2.bI()) {
                    f2 *= projectile.ai;
                }
                if (projectile.aj != 1.0f && baseUnit2.i()) {
                    f2 *= projectile.aj;
                }
            }
            if (f2 < 0.0f) {
                baseUnit2.calculateTurnSpeed(baseUnit, -f2, projectile);
            } else {
                boolean z2 = !baseUnit2.isDead && baseUnit2.currentHealth > 0.0f;
                baseUnit2.setTarget(baseUnit, f2, projectile);
                float f3 = f2;
                if (baseUnit2.isDamageImmune()) {
                    f3 = 0.0f;
                }
                if (f3 > 0.0f) {
                    gameEngine.gameStatistics.a(baseUnit, baseUnit2, f3);
                }
                if (baseUnit != null) {
                    baseUnit.unitCargoMass += f3;
                    if (z2 && (baseUnit2.isDead || baseUnit2.currentHealth < 0.0f)) {
                        baseUnit.unitCargoType++;
                        baseUnit.a(UnitEventType.killedAnyUnit, baseUnit2);
                    }
                }
            }
            if (projectile != null && !baseUnit2.isDead) {
                float fBQ = baseUnit2.bQ();
                if (fBQ != -1.0f) {
                    float angleBetweenPoints = Utility.getAngleBetweenPoints(projectile.posX, projectile.posY, baseUnit2.posX, baseUnit2.posY);
                    float f4 = 100.0f / fBQ;
                    baseUnit2.velocityX += Utility.fastCos(angleBetweenPoints) * f4;
                    baseUnit2.velocityY += Utility.fastSin(angleBetweenPoints) * f4;
                }
            }
        }
    }

    public float e() {
        float f2 = 1.0f;
        if (this.J < this.F) {
            f2 = this.J / this.F;
        }
        return f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f2) {
        float angleBetweenPoints;
        float fDistanceSq;
        float f3;
        float f4;
        Effect effectCreateSmallExplosionInternal;
        Effect effectCreateSmallExplosionInternal2;
        CustomUnitSpawnList customUnitSpawnListA;
        Effect effectCreateEffectInternal;
        float f5;
        float f6;
        float f7;
        float f8;
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.aS) {
            remove();
        }
        if (this.l == null && !this.aC) {
            remove();
            return;
        }
        if (this.i > 0.0f) {
            this.i = Utility.moveTowardsZero(this.i, f2);
            if (this.i > 0.0f) {
                return;
            }
        }
        ProjectileTemplate projectileTemplate = this.g;
        if (this.i == 0.0f) {
            this.i = -1.0f;
            if (projectileTemplate.spawnProjectilesOnCreate != null) {
                projectileTemplate.spawnProjectilesOnCreate.a(this.posX, this.posY, this.posZ, this.az, this.j, null, false, this.aD + 1, this, null);
            }
        }
        this.h = Utility.moveTowardsZero(this.h, f2);
        boolean z = false;
        if (this.aG && (this.l == null || this.l.isDead)) {
            z = true;
        }
        if (z) {
            a(projectileTemplate.autoTargetingOnDeadTargetRange, projectileTemplate.autoTargetingOnDeadTargetLead, (AnimationSet) null);
        }
        if (projectileTemplate.retargetingInFlight) {
            this.aF = Utility.moveTowardsZero(this.aF, f2);
            if (this.aF == 0.0f) {
                this.aF = projectileTemplate.retargetingInFlightSearchDelay;
                a(projectileTemplate.retargetingInFlightSearchRange, projectileTemplate.retargetingInFlightSearchLead, projectileTemplate.retargetingInFlightSearchOnlyTags);
            }
        }
        if (projectileTemplate.R != 0.0f || projectileTemplate.S != 0.0f) {
            float f9 = projectileTemplate.R;
            if (this.l != null) {
                f9 += this.l.radius * projectileTemplate.S;
            }
            this.K = Utility.fastSin(((360.0f * this.I) + (this.J * 1.0f)) % 360.0f) * f9;
            this.L = Utility.fastSin(((360.0f * this.I) + (this.J * 1.5f)) % 360.0f) * f9;
        }
        if (this.E && this.l != null) {
            this.K = Utility.fastSin((this.J * 1.0f) % 360.0f) * this.l.radius * 0.4f;
            this.L = Utility.fastSin((this.J * 1.5f) % 360.0f) * this.l.radius * 0.4f;
            float f10 = this.l.posX + this.K;
            float f11 = this.l.posY + this.L;
            if (this.shouldDraw) {
                this.aN += f2;
                this.aO += f2;
                if (this.aN > 11.0f) {
                    this.aN = Utility.randomFloatInRange(1.0f, 4.0f);
                    Effect effectCreateEffectInternal2 = gameEngine.effectManager.createEffectInternal(f10, f11, this.l.posZ, EffectType.custom, false, EffectQuality.low);
                    if (effectCreateEffectInternal2 != null) {
                        effectCreateEffectInternal2.aq = 0;
                        effectCreateEffectInternal2.ap = 0;
                        effectCreateEffectInternal2.ar = (short) 2;
                        effectCreateEffectInternal2.r = true;
                        effectCreateEffectInternal2.E = 0.5f;
                        effectCreateEffectInternal2.W = 60.0f;
                        effectCreateEffectInternal2.V = 60.0f;
                        effectCreateEffectInternal2.G = 0.7f;
                        effectCreateEffectInternal2.F = 0.3f;
                        effectCreateEffectInternal2.as = false;
                        effectCreateEffectInternal2.P = Utility.randomFloatInRange(-0.3f, 0.3f);
                        effectCreateEffectInternal2.Q = (-0.9f) + Utility.randomFloatInRange(-0.3f, 0.3f);
                    }
                }
                if (this.aO > 75.0f) {
                    this.aO = Utility.randomFloatInRange(1.0f, 20.0f);
                    gameEngine.effectManager.createSmallExplosion(f10, f11, this.l.posZ);
                }
            }
        }
        float f12 = 5.0f;
        boolean z2 = false;
        boolean z3 = false;
        if (!this.aC) {
            float f13 = this.l.posX + this.K;
            float f14 = this.l.posY + this.L;
            float f15 = this.l.posZ;
            angleBetweenPoints = Utility.getAngleBetweenPoints(this.posX, this.posY, f13, f14);
            fDistanceSq = Utility.distanceSq(this.posX, this.posY, f13, f14);
            f3 = f15;
            f4 = f3 - this.posZ;
            f12 = this.l.radius;
            z2 = this.l instanceof BaseBuilding;
            z3 = this.l.shield > 10.0f + this.U;
        } else {
            float f16 = this.az;
            if (this.q != null) {
                float f17 = this.q.posX + this.K;
                float f18 = this.q.posY + this.L;
                float f19 = this.q.posZ;
                angleBetweenPoints = Utility.getAngleBetweenPoints(this.posX, this.posY, f17, f18);
                fDistanceSq = Utility.distanceSq(this.posX, this.posY, f17, f18);
                f3 = f19;
                f4 = f3 - this.posZ;
            } else if (this.l != null) {
                float f20 = this.l.posX + this.K;
                float f21 = this.l.posY + this.L;
                float f22 = this.l.posZ;
                angleBetweenPoints = Utility.getAngleBetweenPoints(this.posX, this.posY, f20, f21);
                fDistanceSq = Utility.distanceSq(this.posX, this.posY, f20, f21);
                f3 = f22;
                f4 = f3 - this.posZ;
                f12 = this.l.radius;
                z2 = this.l instanceof BaseBuilding;
                z3 = this.l.shield > 10.0f + this.U;
            } else if (this.m) {
                float f23 = this.n + this.K;
                float f24 = this.o + this.L;
                float f25 = this.p;
                angleBetweenPoints = Utility.getAngleBetweenPoints(this.posX, this.posY, f23, f24);
                fDistanceSq = Utility.distanceSq(this.posX, this.posY, f23, f24);
                f3 = f25;
                f4 = f3 - this.posZ;
            } else {
                float f26 = this.n + this.K;
                float f27 = this.o + this.L;
                angleBetweenPoints = Utility.getAngleBetweenPoints(this.posX, this.posY, f26, f27);
                fDistanceSq = Utility.distanceSq(this.posX, this.posY, f26, f27);
                f3 = 0.0f;
                f4 = 0.0f - this.posZ;
            }
        }
        float f28 = projectileTemplate.O;
        if (fDistanceSq < 225.0f) {
            f28 = projectileTemplate.P;
        }
        if (f28 >= 0.0f) {
            this.az += Utility.endsWith(this.az, angleBetweenPoints, f28 * f2);
            angleBetweenPoints = this.az;
        } else {
            this.az = angleBetweenPoints;
        }
        boolean z4 = false;
        boolean z5 = false;
        float f29 = angleBetweenPoints;
        if (this.au != null && !this.au.isDestroyed) {
            if (this.av >= 0) {
                OrderableUnit orderableUnit = (OrderableUnit) this.au;
                if (this.av >= orderableUnit.getTechLevel()) {
                    this.av = 0;
                }
                Vector3D vector3DD = orderableUnit.D(this.av);
                f6 = vector3DD.a;
                f7 = vector3DD.b;
                f8 = this.j.posZ + vector3DD.c;
            } else {
                f6 = this.au.posX;
                f7 = this.au.posY;
                f8 = this.au.posZ;
            }
            float f30 = f6 - this.aw;
            float f31 = f7 - this.ax;
            float f32 = f8 - this.ay;
            this.posX += f30;
            this.posY += f31;
            this.posZ += f32;
            this.aw = f6;
            this.ax = f7;
            this.ay = f8;
        }
        if (!this.A) {
            this.posX += this.u * f2;
            this.posY += this.v * f2;
            if (this.w != 0.0f) {
                this.posZ += this.w * f2;
                f4 = f3 - this.posZ;
            }
            if (this.posZ > 0.0f) {
                if (projectileTemplate.G != 0.0f) {
                    this.posZ -= projectileTemplate.G * f2;
                    f4 = f3 - this.posZ;
                }
                if (projectileTemplate.H != 0.0f) {
                    this.w -= projectileTemplate.H * f2;
                }
            }
            if (!this.aH || this.aI < this.posZ || this.aK) {
                float fSortRect = this.t * f2;
                z4 = true;
                if (fDistanceSq < fSortRect * fSortRect) {
                    fSortRect = Utility.sortRect(fDistanceSq);
                    fDistanceSq = 0.0f;
                }
                this.posX += Utility.fastCos(angleBetweenPoints) * fSortRect;
                this.posY += Utility.fastSin(angleBetweenPoints) * fSortRect;
            }
            if (this.aH) {
                if (this.aL < 0.0f) {
                    f5 = this.t * f2;
                    z4 = true;
                } else {
                    f5 = this.aL * f2;
                }
                if (!this.aK) {
                    this.posZ = Utility.distanceSq(this.posZ, this.aJ, f5);
                    if (this.posZ < this.aI) {
                        f29 = -90.0f;
                    }
                    if (this.posZ >= this.aJ) {
                        this.aK = true;
                    }
                } else if (fDistanceSq < 400.0f) {
                    this.posZ = Utility.distanceSq(this.posZ, f3, f5);
                    if (Utility.abs(this.posZ - f3) > 0.5f) {
                        f29 = 90.0f;
                        z5 = true;
                    }
                }
            } else {
                float f33 = f4;
                float fClamp = this.t * f2;
                z4 = true;
                if (f33 != 0.0f) {
                    if (fDistanceSq > 0.1d) {
                        fClamp = Utility.clamp((Utility.abs(f33) / Utility.sortRect(fDistanceSq)) * this.t * f2, this.t * f2);
                    }
                    this.posZ += Utility.clamp(f4, fClamp);
                    f4 = f3 - this.posZ;
                }
            }
        }
        if (z4 && this.r > 0.0f) {
            this.t = Utility.distanceSq(this.t, this.r, this.s * f2);
        }
        if (projectileTemplate.wobbleAmplitude != 0.0f) {
            float fFastSin = Utility.fastSin((((this.J * 360.0f) / projectileTemplate.wobbleFrequency) + (360.0f * this.I)) % 360.0f) * projectileTemplate.wobbleAmplitude * f2;
            this.posX += Utility.fastCos(angleBetweenPoints + 90.0f) * fFastSin;
            this.posY += Utility.fastSin(angleBetweenPoints + 90.0f) * fFastSin;
        }
        if (this.shouldDraw && ((this.aM || projectileTemplate.trailEffect != null) && !this.bn)) {
            this.aN += f2;
            if (this.aN > projectileTemplate.ag) {
                this.aN = 0.0f;
                boolean z6 = false;
                if (this.D) {
                    z6 = true;
                }
                if (projectileTemplate.trailEffect != null) {
                    projectileTemplate.trailEffect.a(this.posX, this.posY, this.posZ, this.aT, this);
                }
                if (this.aM && (effectCreateEffectInternal = gameEngine.effectManager.createEffectInternal(this.posX, this.posY, this.posZ, EffectType.custom, z6, EffectQuality.low)) != null) {
                    if (this.posZ >= 0.0f) {
                        effectCreateEffectInternal.aq = 0;
                        effectCreateEffectInternal.ap = 0;
                        effectCreateEffectInternal.ar = (short) 2;
                        effectCreateEffectInternal.r = true;
                        effectCreateEffectInternal.E = 0.5f;
                        effectCreateEffectInternal.V = 70.0f;
                        effectCreateEffectInternal.W = effectCreateEffectInternal.V;
                        effectCreateEffectInternal.as = true;
                        if (z5) {
                            effectCreateEffectInternal.as = false;
                        }
                        effectCreateEffectInternal.Q = 0.1f;
                        effectCreateEffectInternal.s = true;
                        effectCreateEffectInternal.t = 5.0f;
                        effectCreateEffectInternal.G = 0.5f;
                        effectCreateEffectInternal.F = 1.2f;
                        effectCreateEffectInternal.Y = Utility.randomFloatInRange(-180.0f, 180.0f);
                        if (this.D) {
                            effectCreateEffectInternal.G = 0.5f;
                            effectCreateEffectInternal.F = 2.1f;
                        }
                    } else {
                        effectCreateEffectInternal.aq = 9;
                        effectCreateEffectInternal.ap = 1;
                        effectCreateEffectInternal.ar = (short) 1;
                        effectCreateEffectInternal.r = true;
                        effectCreateEffectInternal.E = 0.5f;
                        effectCreateEffectInternal.W = 60.0f;
                        effectCreateEffectInternal.V = 60.0f;
                        effectCreateEffectInternal.Q = 0.1f;
                    }
                }
            }
        }
        if (!this.bn) {
            boolean z7 = false;
            BaseUnit baseUnit = null;
            boolean z8 = false;
            float f34 = 6.0f;
            if (z2) {
                f34 = f12 * 0.8f;
                if (f34 < 6.0f) {
                    f34 = 6.0f;
                }
            }
            if (z3) {
                f34 = f12 * 1.1f;
            }
            float fAbs = 3.0f;
            if (this.w != 0.0f || projectileTemplate.G != 0.0f) {
                fAbs = 3.0f + Utility.abs(this.w * f2) + Utility.abs(projectileTemplate.G * f2);
            }
            if (fDistanceSq < f34 * f34 && Utility.abs(f4) < fAbs) {
                z7 = true;
                baseUnit = this.l;
            }
            if (this.A) {
                z7 = true;
                baseUnit = this.l;
            }
            if (this.af && this.h == 0.0f) {
                z7 = true;
            }
            if (this.as) {
                float f35 = this.aA + 50.0f;
                BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
                int size = BaseUnit.bE.size();
                for (int i = 0; i < size; i++) {
                    BaseUnit baseUnit2 = baseUnitArrA[i];
                    if (baseUnit2.posX + f35 > this.posX && baseUnit2.posX - f35 < this.posX && baseUnit2.posY + f35 > this.posY && baseUnit2.posY - f35 < this.posY && baseUnit2.isAlive && false == baseUnit2.i() && baseUnit2.unitTransportTarget == null) {
                        float fDistanceSq2 = Utility.distanceSq(this.posX, this.posY, baseUnit2.posX, baseUnit2.posY);
                        float f36 = this.aA + baseUnit2.radius;
                        if (fDistanceSq2 < f36 * f36) {
                            z7 = true;
                            baseUnit = baseUnit2;
                        }
                    }
                }
            }
            if (this.at) {
                gameEngine.tileMap.setCursorTileIndexFromWorldPoint(this.posX, this.posY);
                if (gameEngine.pathfindingEngine.a(UnitMovementType.HOVER, gameEngine.tileMap.cursorTileX, gameEngine.tileMap.cursorTileY)) {
                    z7 = true;
                    z8 = true;
                }
            }
            if (this.aC) {
            }
            if (this.aY && (((this.aH && z5 && this.posZ < 30.0f) || z7) && this.j != null)) {
                this.aY = false;
                FogRevealer fogRevealer = new FogRevealer(false);
                fogRevealer.posX = this.posX;
                fogRevealer.posY = this.posY;
                fogRevealer.setUnitTeam(this.j.team);
                fogRevealer.sightRange = 15;
                fogRevealer.lifeTimer = 360.0f;
                PlayerTeam.c(fogRevealer);
            }
            if (z7) {
                this.bn = true;
                this.aV = this.posX;
                this.aW = this.posY;
                this.aX = this.posZ;
                if (this.A) {
                    if (this.aC) {
                        this.aV = this.n;
                        this.aW = this.o;
                        this.aX = 0.0f;
                    }
                    if (this.l != null) {
                        this.aV = this.l.posX + this.K;
                        this.aW = this.l.posY + this.L;
                        this.aX = this.l.posZ;
                    }
                }
                if (!this.B && !this.M && !projectileTemplate.X) {
                    this.S = false;
                }
                boolean z9 = false;
                if (this.l != null) {
                    z9 = this.l.shield > 10.0f;
                }
                CustomUnitSpawnList customUnitSpawnList = projectileTemplate.explodeEffect;
                if (z9) {
                    customUnitSpawnList = projectileTemplate.explodeEffectOnShield;
                }
                if (this.l != null && (customUnitSpawnListA = projectileTemplate.a(this.l)) != null) {
                    customUnitSpawnList = customUnitSpawnListA;
                }
                if (customUnitSpawnList != null) {
                    customUnitSpawnList.a(this.aV, this.aW, this.aX, this.aT, this.l);
                }
                if (projectileTemplate.spawnProjectilesOnExplode != null) {
                    projectileTemplate.spawnProjectilesOnExplode.a(this.posX, this.posY, this.posZ, this.az, this.j, null, false, this.aD + 1, this, this.l);
                }
                if (projectileTemplate.spawnUnit != null && this.j != null) {
                    projectileTemplate.spawnUnit.a(this.aV, this.aW, 0.0f, this.az, this.j.team, false, this.j);
                }
                if (projectileTemplate.unloadUpToXUnitsFromSource > 0 && this.j != null && (this.j instanceof CustomUnit)) {
                    CustomUnit customUnit = (CustomUnit) this.j;
                    for (int i2 = 0; i2 < projectileTemplate.unloadUpToXUnitsFromSource; i2++) {
                        if (customUnit.transportedUnits != null && customUnit.transportedUnits.size() > 0) {
                            BaseUnit baseUnit3 = (BaseUnit) customUnit.transportedUnits.remove(customUnit.transportedUnits.size() - 1);
                            GameViewUtils.a(baseUnit3, customUnit);
                            baseUnit3.posX = this.aV;
                            baseUnit3.posY = this.aW;
                            baseUnit3.rotationSpeed = this.az;
                            baseUnit3.velocityY = 0.0f;
                            baseUnit3.velocityX = 0.0f;
                            baseUnit3.worldX = 0.0f;
                            baseUnit3.worldY = 0.0f;
                            if (baseUnit3 instanceof OrderableUnit) {
                                OrderableUnit orderableUnit2 = (OrderableUnit) baseUnit3;
                                orderableUnit2.clearAllWaypoints();
                                orderableUnit2.j(baseUnit3.rotationSpeed);
                                if (baseUnit3 instanceof CustomUnit) {
                                    ((CustomUnit) baseUnit3).dF();
                                }
                            }
                            customUnit.unloadTransportedUnit(baseUnit3);
                        }
                    }
                }
                if (projectileTemplate.teleportSource && this.j != null) {
                    this.j.f(this.aV, this.aW);
                }
                if (!z8 && baseUnit != null) {
                    if (this.E) {
                        this.bn = false;
                        float fE = (this.U / 60.0f) * f2 * e();
                        if (this.Z == 0.0f) {
                            a(baseUnit);
                        }
                        a(this.j, baseUnit, projectileTemplate.a(baseUnit, fE, true), this, false);
                    } else {
                        if (this.Z == 0.0f) {
                            a(baseUnit);
                        }
                        a(this.j, baseUnit, projectileTemplate.a(baseUnit, this.U, false), this, false);
                    }
                }
                if (this.q != null) {
                    if (projectileTemplate.d) {
                        this.q.h = 0.0f;
                    } else {
                        this.q.b();
                    }
                    remove();
                }
                if (!this.E) {
                    boolean z10 = true;
                    if (this.l != null && this.l.shield > 10.0f) {
                        z10 = false;
                        if (projectileTemplate.explodeEffectOnShield == null && (effectCreateSmallExplosionInternal2 = gameEngine.effectManager.createSmallExplosionInternal(this.aV, this.aW, this.aX, -1127220)) != null) {
                            effectCreateSmallExplosionInternal2.V = 10.0f;
                            effectCreateSmallExplosionInternal2.F = 0.5f;
                            if (this.aQ) {
                                effectCreateSmallExplosionInternal2.V = 25.0f;
                                effectCreateSmallExplosionInternal2.F = 1.0f;
                            }
                            effectCreateSmallExplosionInternal2.ar = (short) 2;
                            effectCreateSmallExplosionInternal2.W = effectCreateSmallExplosionInternal2.V;
                        }
                    }
                    if (this.G) {
                        z10 = false;
                        EffectEmitter.b(this.posX, this.posY).a = 21.0f;
                    }
                    if (z10) {
                        if (!this.aQ) {
                            if (projectileTemplate.explodeEffect == null) {
                                gameEngine.effectManager.createLargeExplosion(this.aV, this.aW, this.aX);
                            }
                        } else if (projectileTemplate.explodeEffect == null) {
                            if (this.Z > 10.0f && (effectCreateSmallExplosionInternal = gameEngine.effectManager.createSmallExplosionInternal(this.aV, this.aW, this.aX, 0)) != null) {
                                effectCreateSmallExplosionInternal.F = this.Z / 25.0f;
                                effectCreateSmallExplosionInternal.E = 0.7f;
                                if (this.aX > 5.0f) {
                                    effectCreateSmallExplosionInternal.ar = (short) 2;
                                }
                            }
                            gameEngine.effectManager.createSmallExplosion(this.aV, this.aW, this.aX);
                            if (this.aR && !this.D) {
                                gameEngine.soundEngine.playSoundAt(SoundEngine.missileHitSound, 0.5f, 1.0f + Utility.randomFloatInRange(-0.06f, 0.06f), this.aV, this.aW);
                            }
                        }
                        if (this.D && projectileTemplate.explodeEffect == null) {
                            gameEngine.soundEngine.playSoundAt(SoundEngine.nukeExplodeSound, 1.6f, 0.7f, this.aV, this.aW);
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateLightEffect = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(255, 255, 255, 255));
                            if (effectCreateLightEffect != null) {
                                effectCreateLightEffect.G = 14.0f;
                                effectCreateLightEffect.F = 8.0f;
                                effectCreateLightEffect.E = 0.9f;
                                effectCreateLightEffect.V = 35.0f;
                                effectCreateLightEffect.W = effectCreateLightEffect.V;
                                effectCreateLightEffect.r = true;
                            }
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateSmallExplosion = gameEngine.effectManager.createSmallExplosion(this.aV, this.aW, this.aX, -1127220);
                            if (effectCreateSmallExplosion != null) {
                                effectCreateSmallExplosion.G = 1.5f;
                                effectCreateSmallExplosion.F = 3.0f;
                                effectCreateSmallExplosion.ar = (short) 2;
                                effectCreateSmallExplosion.V = 20.0f;
                                effectCreateSmallExplosion.W = effectCreateSmallExplosion.V;
                                effectCreateSmallExplosion.U = 0.0f;
                            }
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateSmallExplosion2 = gameEngine.effectManager.createSmallExplosion(this.aV, this.aW, this.aX, -1127220);
                            if (effectCreateSmallExplosion2 != null) {
                                effectCreateSmallExplosion2.G = 0.2f;
                                effectCreateSmallExplosion2.F = 5.0f;
                                effectCreateSmallExplosion2.ar = (short) 2;
                                effectCreateSmallExplosion2.V = 65.0f;
                                effectCreateSmallExplosion2.W = effectCreateSmallExplosion2.V;
                                effectCreateSmallExplosion2.U = 0.0f;
                            }
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateLightEffect2 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(255, 255, 255, 255));
                            if (effectCreateLightEffect2 != null) {
                                effectCreateLightEffect2.G = 3.0f;
                                effectCreateLightEffect2.F = 6.0f;
                                effectCreateLightEffect2.E = 0.9f;
                                effectCreateLightEffect2.V = 290.0f;
                                effectCreateLightEffect2.W = effectCreateLightEffect2.V;
                            }
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateLightEffect3 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(255, 255, 244, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE));
                            if (effectCreateLightEffect3 != null) {
                                effectCreateLightEffect3.G = 2.0f;
                                effectCreateLightEffect3.F = 6.0f;
                                effectCreateLightEffect3.E = 0.5f;
                                effectCreateLightEffect3.V = 370.0f;
                                effectCreateLightEffect3.W = effectCreateLightEffect3.V;
                                effectCreateLightEffect3.U = 10.0f;
                            }
                            for (int i3 = 0; i3 < 1; i3++) {
                                gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                                Effect effectCreateLightEffect4 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(255, 255, 244, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE));
                                if (effectCreateLightEffect4 != null) {
                                    effectCreateLightEffect4.G = 0.2f;
                                    effectCreateLightEffect4.F = 9.0f;
                                    effectCreateLightEffect4.E = 0.7f;
                                    effectCreateLightEffect4.V = 210.0f;
                                    effectCreateLightEffect4.W = effectCreateLightEffect4.V;
                                    effectCreateLightEffect4.U = 20 + (i3 * 110);
                                }
                            }
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateLightEffect5 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(255, 255, 255, 255));
                            if (effectCreateLightEffect5 != null) {
                                effectCreateLightEffect5.G = 3.0f;
                                effectCreateLightEffect5.F = 4.0f;
                                effectCreateLightEffect5.E = 0.2f;
                                effectCreateLightEffect5.V = 870.0f;
                                effectCreateLightEffect5.W = effectCreateLightEffect5.V;
                                effectCreateLightEffect5.r = true;
                                effectCreateLightEffect5.U = 70.0f;
                            }
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateLightEffect6 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_3D_MODE, 255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_CS));
                            if (effectCreateLightEffect6 != null) {
                                effectCreateLightEffect6.G = 4.0f;
                                effectCreateLightEffect6.F = 1.0f;
                                effectCreateLightEffect6.E = 0.9f;
                                effectCreateLightEffect6.V = 320.0f;
                                effectCreateLightEffect6.W = effectCreateLightEffect6.V;
                            }
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateLightEffect7 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(255, 255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_ANTENNA_CABLE, 129));
                            if (effectCreateLightEffect7 != null) {
                                effectCreateLightEffect7.G = 2.0f;
                                effectCreateLightEffect7.F = 1.0f;
                                effectCreateLightEffect7.E = 1.0f;
                                effectCreateLightEffect7.V = 340.0f;
                                effectCreateLightEffect7.W = effectCreateLightEffect7.V;
                                effectCreateLightEffect7.s = true;
                                effectCreateLightEffect7.t = 20.0f;
                            }
                            gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                            Effect effectCreateLightEffect8 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(245, 255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_AVR_INPUT, 110));
                            if (effectCreateLightEffect8 != null) {
                                effectCreateLightEffect8.G = 1.5f;
                                effectCreateLightEffect8.F = 1.5f;
                                effectCreateLightEffect8.E = 0.3f;
                                effectCreateLightEffect8.V = 1340.0f;
                                effectCreateLightEffect8.W = effectCreateLightEffect8.V;
                                effectCreateLightEffect8.s = true;
                                effectCreateLightEffect8.t = 40.0f;
                                effectCreateLightEffect8.U = 140.0f;
                            }
                            for (int i4 = 0; i4 < 4; i4++) {
                                gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                                Effect effectCreateLightEffect9 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_PAIRING, 255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_ANTENNA_CABLE, 129));
                                if (effectCreateLightEffect9 != null) {
                                    effectCreateLightEffect9.G = 1.5f;
                                    effectCreateLightEffect9.F = 1.4f;
                                    effectCreateLightEffect9.E = 1.3f;
                                    effectCreateLightEffect9.V = 340.0f;
                                    effectCreateLightEffect9.W = effectCreateLightEffect9.V;
                                    effectCreateLightEffect9.Q = -0.29f;
                                    effectCreateLightEffect9.s = true;
                                    effectCreateLightEffect9.t = 50.0f;
                                    effectCreateLightEffect9.U = 30 + (i4 * 40);
                                }
                            }
                            for (int i5 = 0; i5 < 2; i5++) {
                                gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                                Effect effectCreateLightEffect10 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, Color.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_PROG_YELLOW, 255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_ANTENNA_CABLE, 129));
                                if (effectCreateLightEffect10 != null) {
                                    effectCreateLightEffect10.G = 1.3f;
                                    effectCreateLightEffect10.F = 1.0f;
                                    effectCreateLightEffect10.E = 1.0f;
                                    effectCreateLightEffect10.V = 340.0f;
                                    effectCreateLightEffect10.W = effectCreateLightEffect10.V;
                                    effectCreateLightEffect10.Q = -0.14f;
                                    effectCreateLightEffect10.s = true;
                                    effectCreateLightEffect10.t = 50.0f;
                                    effectCreateLightEffect10.U = 70 + (i5 * 70);
                                }
                            }
                            for (int i6 = 0; i6 < 4; i6++) {
                                gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                                Effect effectCreateLightEffect11 = gameEngine.effectManager.createLightEffect(this.aV, this.aW - 30.0f, this.posZ, -16711936);
                                if (effectCreateLightEffect11 != null) {
                                    effectCreateLightEffect11.G = 1.5f;
                                    effectCreateLightEffect11.F = 2.6f;
                                    effectCreateLightEffect11.E = 1.3f;
                                    effectCreateLightEffect11.V = 510.0f;
                                    effectCreateLightEffect11.W = effectCreateLightEffect11.V;
                                    effectCreateLightEffect11.Q = -0.2f;
                                    effectCreateLightEffect11.s = true;
                                    effectCreateLightEffect11.t = 50.0f;
                                    effectCreateLightEffect11.B = null;
                                    effectCreateLightEffect11.x = Color.a(175, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG);
                                    effectCreateLightEffect11.U = 20 + (i6 * 40);
                                }
                            }
                            for (int i7 = 0; i7 < 2; i7++) {
                                gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                                Effect effectCreateLightEffect12 = gameEngine.effectManager.createLightEffect(this.aV, this.aW - 30.0f, this.posZ, -16711936);
                                if (effectCreateLightEffect12 != null) {
                                    effectCreateLightEffect12.G = 1.5f;
                                    effectCreateLightEffect12.F = 3.8f;
                                    effectCreateLightEffect12.E = 0.8f;
                                    effectCreateLightEffect12.V = 590.0f;
                                    effectCreateLightEffect12.W = effectCreateLightEffect12.V;
                                    effectCreateLightEffect12.Q = -0.2f;
                                    effectCreateLightEffect12.s = true;
                                    effectCreateLightEffect12.t = 50.0f;
                                    effectCreateLightEffect12.B = null;
                                    effectCreateLightEffect12.x = Color.a(105, 115, 115, 115);
                                    effectCreateLightEffect12.U = 20 + (i7 * 40);
                                }
                            }
                            for (int i8 = 0; i8 < 1; i8++) {
                                EffectEmitter effectEmitterA = EffectEmitter.a(this.aV + Utility.getRandomIntInRange(-10.0f, 10.0f, (int) this.objectId), this.aW + Utility.getRandomIntInRange(-10.0f, 10.0f, ((int) this.objectId) + i8));
                                if (effectEmitterA != null) {
                                    effectEmitterA.t = 200 + (i8 * 70);
                                    effectEmitterA.a = 980 + (i8 * 800);
                                }
                            }
                            if (!GameViewUtils.d(this.aV, this.aW)) {
                                ScorchMark.a(this.aV, this.aW, ExplosionType.nuke);
                            }
                            if (GameEngine.isFancyWaterSupported()) {
                                if (gameEngine.effectManager.texture2 == null) {
                                    gameEngine.effectManager.texture2 = gameEngine.renderGraphicsEngine.a(com.corrodinggames.rts.R.drawable.shockwave_normal_256, true);
                                }
                                gameEngine.effectManager.setOverrideEffectQuality(EffectQuality.critical);
                                Effect effectCreateLightEffect13 = gameEngine.effectManager.createLightEffect(this.aV, this.aW, this.posZ, -1);
                                if (effectCreateLightEffect13 != null && gameEngine.effectManager.texture2 != null) {
                                    effectCreateLightEffect13.a = new EffectTemplate((BuiltInEffectType) null);
                                    effectCreateLightEffect13.a.imageStrip = new SpriteSheet();
                                    effectCreateLightEffect13.a.imageStrip.k = true;
                                    effectCreateLightEffect13.a.imageStrip.i = gameEngine.effectManager.texture2;
                                    effectCreateLightEffect13.a.imageStrip.b = effectCreateLightEffect13.a.imageStrip.i.m();
                                    effectCreateLightEffect13.a.imageStrip.c = effectCreateLightEffect13.a.imageStrip.i.l();
                                    effectCreateLightEffect13.ar = (short) 3;
                                    effectCreateLightEffect13.G = 0.5f;
                                    effectCreateLightEffect13.F = 3.5f;
                                    effectCreateLightEffect13.E = 0.5f;
                                    effectCreateLightEffect13.V = 60.0f;
                                    effectCreateLightEffect13.W = effectCreateLightEffect13.V;
                                    effectCreateLightEffect13.Q = -0.2f;
                                    effectCreateLightEffect13.s = true;
                                    effectCreateLightEffect13.t = 1.0f;
                                    effectCreateLightEffect13.B = null;
                                    effectCreateLightEffect13.U = 0.0f;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.bn && !this.V) {
            this.W = Utility.moveTowardsZero(this.W, f2);
            if (this.ao) {
                b(1.0f - (this.W / this.X));
            }
            if (this.W == 0.0f) {
                this.V = true;
                b(1.0f);
                if (!this.B && !this.M && !projectileTemplate.X) {
                    remove();
                }
            }
        }
        this.J += f2;
        if (this.h == 0.0f && (!this.bn || this.V)) {
            if (projectileTemplate.spawnProjectilesOnEndOfLife != null) {
                projectileTemplate.spawnProjectilesOnEndOfLife.a(this.posX, this.posY, this.posZ, this.az, this.j, null, false, this.aD + 1, this, null);
            }
            remove();
        }
        if (!this.aU) {
            this.aT = f29;
            this.aU = true;
        }
        this.aT += Utility.endsWith(this.aT, f29, 12.0f * f2);
    }

    public void b(float f2) {
        boolean z = false;
        if (this.g.f) {
            return;
        }
        if (this.g.e) {
            z = true;
        }
        if (!z) {
            if (this.Y != 0.0f && this.Z > 0.0f) {
                z = true;
            }
            if ((this.ag != 0.0f || this.ah != 0.0f) && this.Z > 0.0f) {
                z = true;
            }
        }
        if (!z) {
            return;
        }
        float f3 = this.Z * f2;
        float f4 = f3;
        if (this.g.h) {
            f4 += 150.0f;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        bi.clear();
        gameEngine.unitSpatialIndex.b(this.aV, this.aW, f4, bi);
        BaseUnit[] baseUnitArrA = bi.a();
        int size = bi.size();
        for (int i = 0; i < size; i++) {
            b(baseUnitArrA[i], f2, f3);
        }
        bi.clear();
    }

    public void b(BaseUnit baseUnit, float f2, float f3) {
        if (baseUnit.unitTransportTarget != null) {
            return;
        }
        if (this.ap != null && this.ap.contains(baseUnit)) {
            return;
        }
        if (this.j != null) {
            PlayerTeam playerTeam = this.j.team;
            PlayerTeam playerTeam2 = baseUnit.team;
            if (playerTeam2 != playerTeam && playerTeam.d(playerTeam2)) {
                return;
            }
            if (this.aa && !playerTeam.c(playerTeam2)) {
                return;
            }
            if (this.ab && playerTeam.c(playerTeam2)) {
                return;
            }
        }
        if (baseUnit.posZ < -5.0f && this.aX >= -2.0f && !this.ac) {
            return;
        }
        if (this.ae) {
            if (baseUnit.i() != (this.aX >= 5.0f)) {
                return;
            }
        } else if (!this.ad && baseUnit.i()) {
            return;
        }
        float fDistanceSq = Utility.distanceSq(this.aV, this.aW, baseUnit.posX, baseUnit.posY);
        if (fDistanceSq > f3 * f3 && !this.g.h) {
            return;
        }
        float fSqrt = (float) StrictMath.sqrt(fDistanceSq);
        if (this.g.h) {
            fSqrt -= baseUnit.radius;
            if (fSqrt < 0.0f) {
                fSqrt = 0.0f;
            }
        }
        if (fSqrt > f3 || fSqrt < this.g.j) {
            return;
        }
        a(f2, baseUnit, fSqrt);
    }

    public void a(float f2, BaseUnit baseUnit, float f3) {
        float f4 = (float) (((double) (1.0f - (f3 / this.Z))) + 0.1d);
        if (f4 > 1.0f) {
            f4 = 1.0f;
        }
        if (this.g.g) {
            f4 = 1.0f;
        }
        float f5 = f4 * this.Y;
        a(baseUnit);
        a(this.j, baseUnit, this.g.a(baseUnit, f5, true), this, true);
        if (this.ao) {
            if (this.ap == null) {
                this.ap = new FastArrayList();
            }
            this.ap.add(baseUnit);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean a(GameEngine gameEngine) {
        if (gameEngine.bufferedVisibleWorldRectF.b(this.posX, this.posY)) {
            return true;
        }
        if ((this.B || this.E || this.g.X) && this.l != null && gameEngine.bufferedVisibleWorldRectF.b(this.l.posX, this.l.posY)) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f2) {
        float f3;
        float f4;
        float f5;
        if (!this.S || this.i > 0.0f) {
            return false;
        }
        ProjectileTemplate projectileTemplate = this.g;
        GameEngine gameEngine = GameEngine.getInstance();
        GraphicsEngine graphicsEngine = gameEngine.renderGraphicsEngine;
        float f6 = this.posX - gameEngine.viewpointXSnapped;
        float f7 = this.posY - gameEngine.viewpointYSnapped;
        if (this.l != null) {
            f3 = this.l.posX;
            f4 = this.l.posY;
            f5 = this.l.posZ;
        } else {
            f3 = this.n;
            f4 = this.o;
            f5 = this.p;
        }
        if (!this.aZ && !this.D) {
            boolean z = false;
            if (this.A) {
                if (this.l != null) {
                    if (!gameEngine.tileMap.isWorldPointVisibleForTeam(this.l.posX, this.l.posY, gameEngine.playerTeam)) {
                        z = true;
                    }
                } else if (this.m && !gameEngine.tileMap.isWorldPointVisibleForTeam(this.n, this.o, gameEngine.playerTeam)) {
                    z = true;
                }
            }
            if (!gameEngine.tileMap.isWorldPointVisibleForTeam(this.posX, this.posY, gameEngine.playerTeam) && !z) {
                return false;
            }
            this.aZ = true;
        }
        if (this.E || projectileTemplate.X) {
            if (projectileTemplate.Y != null) {
                Paint paintF = f();
                float f8 = 0.0f;
                if (projectileTemplate.beamImageOffsetRate != 0.0f) {
                    f8 = 0.0f + (projectileTemplate.beamImageOffsetRate * this.J);
                }
                float f9 = this.posX - gameEngine.viewpointXSnapped;
                float f10 = (this.posY - gameEngine.viewpointYSnapped) - this.posZ;
                float f11 = (f3 - gameEngine.viewpointXSnapped) + this.K;
                float f12 = ((f4 - f5) - gameEngine.viewpointYSnapped) + this.L;
                float f13 = (f11 + f9) * 0.5f;
                float f14 = (f12 + f10) * 0.5f;
                float fDistance = Utility.distance(f13, f14, f11, f12);
                float angleBetweenPoints = Utility.getAngleBetweenPoints(f13, f14, f11, f12);
                graphicsEngine.k();
                f.a(f13 - projectileTemplate.Y.r, f14 - fDistance, f13 + projectileTemplate.Y.r, f14 + fDistance);
                graphicsEngine.a(angleBetweenPoints + 90.0f, f13, f14);
                graphicsEngine.a(projectileTemplate.Y, f, paintF, 0.0f, f8, 0, 0);
                graphicsEngine.l();
                if (projectileTemplate.Z != null) {
                    if (projectileTemplate.aa) {
                        graphicsEngine.k();
                        graphicsEngine.a(angleBetweenPoints + 90.0f, f9, f10);
                        graphicsEngine.a(projectileTemplate.Z, f9, f10, paintF);
                        graphicsEngine.l();
                    } else {
                        graphicsEngine.a(projectileTemplate.Z, f9, f10, paintF);
                    }
                }
                if (projectileTemplate.ab != null) {
                    if (projectileTemplate.ac) {
                        graphicsEngine.k();
                        graphicsEngine.a(angleBetweenPoints + 90.0f, f11, f12);
                        graphicsEngine.a(projectileTemplate.ab, f11, f12, paintF);
                        graphicsEngine.l();
                        return true;
                    }
                    graphicsEngine.a(projectileTemplate.ab, f11, f12, paintF);
                    return true;
                }
                return true;
            }
            bf.c((int) (60.0f + (e() * 60.0f)));
            float f15 = (f3 - gameEngine.viewpointXSnapped) + this.K;
            float f16 = ((f4 - f5) - gameEngine.viewpointYSnapped) + this.L;
            bf.a(6.0f);
            graphicsEngine.a(this.posX - gameEngine.viewpointXSnapped, (this.posY - gameEngine.viewpointYSnapped) - this.posZ, f15, f16, bf);
            bf.a(3.0f);
            graphicsEngine.a(this.posX - gameEngine.viewpointXSnapped, (this.posY - gameEngine.viewpointYSnapped) - this.posZ, f15, f16, bf);
            graphicsEngine.a(f15, f16, 8.0f, bf);
            graphicsEngine.a(f15, f16, 5.0f, bf);
            return true;
        }
        if (this.B) {
            float f17 = (f3 - gameEngine.viewpointXSnapped) + this.K;
            float f18 = ((f4 - f5) - gameEngine.viewpointYSnapped) + this.L;
            bd.b(this.ar);
            be.b(this.ar);
            be.c((int) (be.f() * 0.5f));
            graphicsEngine.a(this.posX - gameEngine.viewpointXSnapped, (this.posY - gameEngine.viewpointYSnapped) - this.posZ, f17, f18, be);
            graphicsEngine.a(this.posX - gameEngine.viewpointXSnapped, (this.posY - gameEngine.viewpointYSnapped) - this.posZ, f17, f18, bd);
            graphicsEngine.a(f17, f18, 5.0f, bd);
            return true;
        }
        if (this.M) {
            this.N = Utility.moveTowardsZero(this.N, f2);
            if (this.O == null) {
                this.O = new float[20];
                this.N = 0.0f;
            }
            if (this.N == 0.0f) {
                this.N = 4.0f;
                for (int i = 0; i < this.O.length; i++) {
                    this.O[i] = Utility.randomFloatInRange(-10.0f, 10.0f);
                }
            }
            float f19 = this.posX - gameEngine.viewpointXSnapped;
            float f20 = (this.posY - gameEngine.viewpointYSnapped) - this.posZ;
            float f21 = f3 - gameEngine.viewpointXSnapped;
            float f22 = (f4 - f5) - gameEngine.viewpointYSnapped;
            float fDistanceInt = Utility.distanceInt(f19, f20, f21, f22);
            int length = this.O.length;
            if (fDistanceInt < 200.0f) {
                length = Utility.max(0, length - 5);
            } else if (fDistanceInt < 100.0f) {
                length = Utility.max(0, length - 10);
            }
            float f23 = fDistanceInt / (length - 1);
            float angleBetweenPoints2 = Utility.getAngleBetweenPoints(f19, f20, f21, f22);
            float f24 = f19;
            float f25 = f20;
            float fFastCos = Utility.fastCos(angleBetweenPoints2);
            float fFastSin = Utility.fastSin(angleBetweenPoints2);
            for (int i2 = 0; i2 < length; i2++) {
                float f26 = this.O[i2];
                float f27 = f19 + (fFastCos * i2 * f23);
                float f28 = f20 + (fFastSin * i2 * f23);
                if (i2 != length - 1) {
                    f27 -= fFastSin * f26;
                    f28 += fFastCos * f26;
                }
                graphicsEngine.a(f24, f25, f27, f28, bg);
                f24 = f27;
                f25 = f28;
            }
            return true;
        }
        if (this.P != -1) {
            Texture texture = b;
            int i3 = 20;
            int i4 = 20;
            if (this.R == 1) {
                texture = d;
                i3 = 60;
                i4 = 60;
            } else if (this.R == 2) {
                texture = c;
                i3 = 20;
                i4 = 20;
            }
            if (projectileTemplate.C != null) {
                GameViewUtils.a(projectileTemplate.C, f6, f7, 0.0f, this.aT, this.x, bc, projectileTemplate.C.p, projectileTemplate.C.q, 0);
            } else if (this.Q != -1 && this.z) {
                GameViewUtils.a(texture, f6, f7, 0.0f, this.aT, this.x, bc, i3, i4, this.Q);
            }
            if (projectileTemplate.B != null) {
                texture = projectileTemplate.B;
                i3 = projectileTemplate.B.p;
                i4 = projectileTemplate.B.q;
            }
            GameViewUtils.a(texture, f6, f7, this.posZ, this.aT, this.x, f(), i3, i4, this.P);
            return true;
        }
        bb.b(this.ar);
        if (this.posZ > 0.0f && this.z) {
            graphicsEngine.a(f6, f7, this.x, bc);
        }
        graphicsEngine.a(f6, f7 - this.posZ, this.x, bb);
        if (this.y > 0.0f) {
            bb.c(bb.f() / 3);
            graphicsEngine.a(f6, f7 - this.posZ, this.y, bb);
            return true;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f2, boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void d(float f2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean f(float f2) {
        return false;
    }

    public Paint f() {
        Paint paintA;
        if (this.ar != aq) {
            if (GameEngine.isAndroidPlatform()) {
                paintA = a(this.ar);
            } else {
                paintA = bb;
                paintA.b(this.ar);
            }
        } else {
            paintA = ba;
        }
        return paintA;
    }

    public GamePaint a(int i) {
        if (this.bj != null) {
            return this.bj;
        }
        if (bk != null && bl == i) {
            this.bj = bk;
            return this.bj;
        }
        GamePaint gamePaint = new GamePaint();
        gamePaint.a(new LightingColorFilter(i, 0));
        gamePaint.b(i);
        bk = gamePaint;
        bl = i;
        this.bj = gamePaint;
        return this.bj;
    }

    public void a(float f2, float f3, AnimationSet animationSet) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.j == null) {
            GameEngine.logColored("Projectile: cannot Retarget: source==null");
            return;
        }
        float fFastCos = this.posX + (Utility.fastCos(this.az) * f3);
        float fFastSin = this.posY + (Utility.fastSin(this.az) * f3);
        float f4 = f2 * f2;
        float f5 = -1.0f;
        OrderableUnit orderableUnit = null;
        BaseUnit commandOrAttackTarget = null;
        if (this.j instanceof OrderableUnit) {
            orderableUnit = (OrderableUnit) this.j;
            commandOrAttackTarget = orderableUnit.getCommandOrAttackTarget();
        }
        for (BaseUnit baseUnit : gameEngine.unitSpatialIndex.a(fFastCos, fFastSin, f2)) {
            if (this.j.team != baseUnit.team) {
                boolean zB = true;
                if (orderableUnit != null) {
                    zB = orderableUnit.b(baseUnit, true);
                }
                if (zB && this.k >= 0 && orderableUnit != null && this.k < orderableUnit.getTechLevel() && !orderableUnit.a((int) this.k, baseUnit, true, false)) {
                    zB = false;
                }
                if (animationSet != null && !AnimationTag.a(animationSet, baseUnit.getTags())) {
                    zB = false;
                }
                if (zB) {
                    float fDistanceSq = Utility.distanceSq(fFastCos, fFastSin, baseUnit.posX, baseUnit.posY);
                    boolean z = false;
                    if (f5 == -1.0f || fDistanceSq < f5) {
                        z = true;
                    }
                    if (commandOrAttackTarget == baseUnit) {
                        z = true;
                    }
                    if (z && fDistanceSq < f4) {
                        f5 = fDistanceSq;
                        this.l = baseUnit;
                    }
                }
            }
        }
    }
}
