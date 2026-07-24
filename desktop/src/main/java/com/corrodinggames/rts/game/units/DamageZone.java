package com.corrodinggames.rts.game.units;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.effects.Effect;
import com.corrodinggames.rts.gameFramework.effects.EffectQuality;
import com.corrodinggames.rts.gameFramework.effects.EffectType;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsUtils;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f.class */
public class DamageZone extends DummyUnit {
    public float a;
    public float b;
    public float c;
    public float d;
    public float e;
    public float f;
    public boolean g;
    public float h;
    public boolean i;
    public float j;
    static Paint k = new Paint();
    static Paint l;
    static Paint m;
    static Paint n;
    static Paint o;
    static Paint p;
    boolean q;
    static final PointF r;

    static {
        k.a(10.0f);
        k.b(Color.a(100, 160, 0, 0));
        k.a(Paint.Style.STROKE);
        m = new Paint();
        m.a(k);
        m.b(Color.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 160, 0, 0));
        l = new Paint();
        l.a(2.0f);
        l.b(Color.a(100, 160, 0, 0));
        l.a(Paint.Style.STROKE);
        n = new Paint();
        n.a(l);
        n.b(Color.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 160, 0, 0));
        o = new Paint();
        o.a(2.0f);
        o.b(Color.a(50, 255, 255, 255));
        o.a(Paint.Style.STROKE);
        p = new Paint(o);
        r = new PointF();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeByte(0);
        gameOutputStream.writeFloat(this.a);
        gameOutputStream.writeFloat(this.b);
        gameOutputStream.writeFloat(this.c);
        gameOutputStream.writeFloat(this.d);
        gameOutputStream.writeFloat(this.e);
        gameOutputStream.writeFloat(this.f);
        gameOutputStream.writeBoolean(this.g);
        gameOutputStream.writeFloat(this.h);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        gameInputStream.readByte();
        this.a = gameInputStream.readFloat();
        this.b = gameInputStream.readFloat();
        this.c = gameInputStream.readFloat();
        this.d = gameInputStream.readFloat();
        this.e = gameInputStream.readFloat();
        this.f = gameInputStream.readFloat();
        this.g = gameInputStream.readBoolean();
        this.h = gameInputStream.readFloat();
        super.a(gameInputStream);
        if (!this.isDead) {
            GameEngine.getInstance().minimap.addGraphicsOperation(this);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        if (this.q) {
            return UnitTypeEnum.zoneMarker;
        }
        return UnitTypeEnum.damagingBorder;
    }

    public static void d_() {
        GameEngine.getInstance();
    }

    public DamageZone(boolean z) {
        super(z);
        this.a = 2000.0f;
        this.b = 0.0f;
        this.c = 0.0f;
        this.d = 2000.0f;
        this.g = true;
        this.h = 1.0f;
    }

    public DamageZone f() {
        for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
            if ((baseUnit instanceof DamageZone) && !baseUnit.isDead && baseUnit != this) {
                DamageZone damageZone = (DamageZone) baseUnit;
                if (damageZone.q == this.q) {
                    return damageZone;
                }
            }
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.DummyUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (this.isDead) {
            return;
        }
        if (this.g) {
            this.g = false;
            DamageZone damageZoneF = f();
            if (damageZoneF != null) {
                damageZoneF.e = this.posX;
                damageZoneF.f = this.posY;
                damageZoneF.d = this.d;
                removeFromGame();
            } else {
                this.e = this.posX;
                this.f = this.posY;
                if (!this.q) {
                    GameEngine.log("DamagingBorder created " + this.e + "," + this.f + " size:" + this.d);
                }
                GameEngine.getInstance().minimap.addGraphicsOperation(this);
            }
        }
        if (this.q) {
            this.a = this.d;
            this.posX = this.e;
            this.posY = this.f;
        } else if (this.a > this.d) {
            this.b += 2.5E-4f * f;
            this.a -= this.b;
            this.i = true;
            float fDistance = Utility.distance(this.posX, this.posY, this.e, this.f);
            float angleBetweenPoints = Utility.getAngleBetweenPoints(this.posX, this.posY, this.e, this.f);
            if (fDistance > 1.0f) {
                float f2 = this.b;
                if (f2 > fDistance * f) {
                    f2 = fDistance * f;
                }
                this.posX += f2 * Utility.fastCos(angleBetweenPoints) * f;
                this.posY += f2 * Utility.fastSin(angleBetweenPoints) * f;
            }
        } else {
            this.i = false;
            this.posX = (float) (((double) this.posX) + (((double) (this.e - this.posX)) * 0.003d * ((double) f)));
            this.posY = (float) (((double) this.posY) + (((double) (this.f - this.posY)) * 0.003d * ((double) f)));
        }
        if (this.a < this.d) {
            this.a = this.d;
            this.b = 0.0f;
        }
        if (this.d < 0.0f) {
            removeFromGame();
            return;
        }
        this.c -= f;
        if (!this.isDead && this.c <= 0.0f && !this.q) {
            this.c = 2.0f;
            float fFastCos = this.a * Utility.fastCos(45.0f);
            float f3 = this.posX - fFastCos;
            float f4 = this.posX + fFastCos;
            float f5 = this.posY - fFastCos;
            float f6 = this.posY + fFastCos;
            float f7 = this.a * this.a;
            for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
                if (baseUnit.posX <= f3 || baseUnit.posX >= f4 || baseUnit.posY <= f5 || baseUnit.posY >= f6) {
                    if (Utility.distanceSq(this.posX, this.posY, baseUnit.posX, baseUnit.posY) >= f7 && !baseUnit.isDead && !(baseUnit instanceof Tree) && !baseUnit.u() && baseUnit.unitTransportTarget == null) {
                        baseUnit.setTarget(this, (0.5f + (baseUnit.currentHealth * 0.002f) + (baseUnit.maxHealth * 0.001f)) * this.h, (Projectile) null);
                    }
                }
            }
        }
        if (!this.q) {
            GameEngine gameEngine = GameEngine.getInstance();
            this.j += f;
            if (this.j > 3.0f) {
                this.j = 0.0f;
                int randomIntInRange = gameEngine.viewpointXInt + Utility.getRandomIntInRange(0, (int) gameEngine.visibleWorldWidth);
                int randomIntInRange2 = gameEngine.viewpointYInt + Utility.getRandomIntInRange(0, (int) gameEngine.visibleWorldHeight);
                if (Utility.distanceSq(this.posX, this.posY, randomIntInRange, randomIntInRange2) > (this.a + 30.0f) * (this.a + 30.0f)) {
                    gameEngine.tileMap.setCursorTileIndexFromWorldPoint(randomIntInRange, randomIntInRange2);
                    gameEngine.tileMap.setCursorTileIndexFromTileIndex(gameEngine.tileMap.cursorTileX, gameEngine.tileMap.cursorTileY);
                    Effect effectCreateEffectInternal = gameEngine.effectManager.createEffectInternal(gameEngine.tileMap.cursorTileX + 10, (gameEngine.tileMap.cursorTileY - 10) + 10, 0.0f, EffectType.custom, true, EffectQuality.verylow);
                    if (effectCreateEffectInternal != null) {
                        effectCreateEffectInternal.aq = 19;
                        effectCreateEffectInternal.Y = Utility.randomFloatInRange(-180.0f, 180.0f);
                        effectCreateEffectInternal.r = true;
                        effectCreateEffectInternal.ar = (short) 1;
                        effectCreateEffectInternal.E = 0.7f;
                        effectCreateEffectInternal.V = 30.0f;
                        effectCreateEffectInternal.W = effectCreateEffectInternal.V;
                        effectCreateEffectInternal.G = 0.2f;
                        effectCreateEffectInternal.F = 1.2f;
                        effectCreateEffectInternal.x = Color.a(255, 173, 12, 12);
                    }
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public int s() {
        return 0;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean t() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.DummyUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean u() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean a(GameEngine gameEngine) {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.DummyUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
        DamageZone damageZoneF;
        GameEngine gameEngine = GameEngine.getInstance();
        float f2 = this.posX - gameEngine.viewpointXSnapped;
        float f3 = this.posY - gameEngine.viewpointYSnapped;
        Paint paint = this.i ? m : k;
        if (this.q) {
            paint = o;
        }
        float f4 = this.a;
        if (this.g && (damageZoneF = f()) != null) {
            f4 = damageZoneF.d - 300.0f;
        }
        gameEngine.renderGraphicsEngine.a(f2, f3, f4, paint);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean a(int i, int i2) {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.renderGraphicsEngine.i();
        gameEngine.renderGraphicsEngine.a(gameEngine.minimap.minimapBoundsRect);
        float fWorldToMinimapX = gameEngine.minimap.worldToMinimapX(this.a);
        Paint paint = this.i ? n : l;
        if (this.q) {
            paint = p;
        }
        GraphicsUtils.a(gameEngine.renderGraphicsEngine, i, i2, fWorldToMinimapX, paint);
        gameEngine.renderGraphicsEngine.j();
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
        this.a = i * 100;
        this.d = i * 100;
    }

    public boolean a(float f, float f2) {
        return Utility.distanceSq(this.e, this.f, f, f2) >= this.d * this.d;
    }

    public PointF a(float f, float f2, float f3) {
        if (f3 > this.d) {
            f3 = this.d;
        }
        float angleBetweenPoints = Utility.getAngleBetweenPoints(this.posX, this.posY, f, f2);
        float f4 = this.d - f3;
        float fFastCos = this.posX + (Utility.fastCos(angleBetweenPoints) * f4);
        float fFastSin = this.posY + (Utility.fastSin(angleBetweenPoints) * f4);
        r.x = fFastCos;
        r.y = fFastSin;
        return r;
    }
}
