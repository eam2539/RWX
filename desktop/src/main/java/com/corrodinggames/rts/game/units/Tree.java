package com.corrodinggames.rts.game.units;

import android.graphics.Paint;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.effects.Effect;
import com.corrodinggames.rts.gameFramework.effects.EffectQuality;
import com.corrodinggames.rts.gameFramework.effects.EffectType;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.al */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/al.class */
public class Tree extends NaturalUnit {
    static Texture[] a = new Texture[3];
    static Texture b = null;
    Texture c;
    int d;
    int e;
    int f;
    float g;
    boolean h;
    float i;
    int j;
    int k;
    float l;
    boolean m;

    public static void b() {
        GameEngine gameEngine = GameEngine.getInstance();
        a[0] = gameEngine.renderGraphicsEngine.a(R.drawable.palm_tree);
        a[1] = gameEngine.renderGraphicsEngine.a(R.drawable.trees);
        a[2] = gameEngine.renderGraphicsEngine.a(R.drawable.trees_snow);
        b = gameEngine.renderGraphicsEngine.a(R.drawable.palm_leaves);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(this.d);
        gameOutputStream.writeInt(this.f);
        gameOutputStream.writeFloat(this.g);
        gameOutputStream.writeBoolean(this.h);
        gameOutputStream.writeFloat(this.i);
        gameOutputStream.writeByte(2);
        gameOutputStream.writeFloat(this.l);
        gameOutputStream.writeInt(this.e);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.d = gameInputStream.readInt();
        this.f = gameInputStream.readInt();
        this.g = gameInputStream.readFloat();
        this.h = gameInputStream.readBoolean();
        this.i = gameInputStream.readFloat();
        byte b2 = gameInputStream.readByte();
        if (b2 >= 1) {
            this.l = gameInputStream.readFloat();
        } else {
            this.l = 1.0f;
        }
        if (b2 >= 2) {
            this.e = gameInputStream.readInt();
        } else {
            this.e = 0;
        }
        b(this.d, this.e);
        super.a(gameInputStream);
        if (this.isDead) {
            this.m = false;
        }
    }

    public Texture d() {
        return this.c;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        k();
        return true;
    }

    public Tree(boolean z) {
        super(z);
        this.f = 0;
        this.j = 0;
        this.k = 0;
        this.l = 1.0f;
        this.m = false;
        b(1, -1);
        this.radius = 3.0f;
        this.displayRadius = this.radius + 1.0f;
        this.maxHealth = 100.0f;
        this.currentHealth = this.maxHealth;
        this.rotationSpeed = -90.0f;
        S(3);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void a_(String str) {
        int i = -1;
        String[] strArrSplit = str.split("\\.");
        if (strArrSplit.length != 0 && strArrSplit.length != 1) {
            if (strArrSplit.length == 2) {
                str = strArrSplit[0];
                try {
                    i = Integer.parseInt(strArrSplit[1]);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Tree sub type format error:" + strArrSplit[1]);
                }
            } else {
                throw new RuntimeException("Tree sub unknown format with parts:" + strArrSplit.length);
            }
        }
        try {
            b(Integer.parseInt(str), i);
        } catch (NumberFormatException e2) {
            throw new RuntimeException("Tree type format error:" + str);
        }
    }

    public void b(int i, int i2) {
        this.d = i;
        this.e = i2;
        if (this.d == 0) {
            T(27);
            U(41);
            this.j = 1;
            this.k = 1;
            this.c = a[0];
            return;
        }
        if (this.d == 1 || this.d == 2) {
            if (i2 == -1) {
                i2 = Utility.getDeterministicRandomIntInRange(0, 4, (int) this.objectId);
            }
            if (i2 < 0 || i2 > 4) {
                throw new RuntimeException("Tree subType out of range:" + i2);
            }
            T(25);
            U(30);
            if (this.d == 1) {
                this.c = a[1];
            } else {
                this.c = a[2];
            }
            this.j = 0;
            this.k = 30 * i2;
            if (i2 == 0) {
                this.l = Utility.getRandomIntInRange(1.0f, 1.2f, ((int) this.objectId) + 1);
            } else {
                this.l = Utility.getRandomIntInRange(1.0f, 2.0f, ((int) this.objectId) + 1);
            }
            this.m = true;
            return;
        }
        throw new RuntimeException("Tree type:" + this.d + " is not supported");
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        if (this.d == 0) {
            if (this.h) {
                if (this.f < 4) {
                    this.g += f;
                    if (this.g > 5.0f) {
                        this.g = 0.0f;
                        this.f++;
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.i != 0.0f) {
                this.i = Utility.moveTowardsZero(this.i, 0.1f * f);
                this.f = 2;
            } else if (this.f > 1) {
                this.f = 1;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        int i = this.j;
        int i2 = this.k;
        int i3 = i + (this.f * (this.es + 1));
        dC.a(i3, i2, i3 + this.es, i2 + this.et);
        return dC;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        Texture textureD = d();
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.zoom < 0.15d) {
            return false;
        }
        du.a(getUnitBounds());
        du.a(0.0f, (int) (-this.posZ));
        float fD = du.d();
        float fE = du.e();
        dv.a(a_(false));
        GraphicsEngine graphicsEngine = gameEngine.renderGraphicsEngine;
        graphicsEngine.k();
        if (this.l != 1.0f) {
            graphicsEngine.a(this.l, this.l, fD, fE);
        }
        if (this.m) {
            dv.a(this.es, 0);
            gameEngine.renderGraphicsEngine.a(textureD, dv, du, (Paint) null);
            dv.a(-this.es, 0);
        }
        graphicsEngine.a(getUnitArmorRating(false), fD, fE);
        graphicsEngine.a(textureD, dv, du, (Paint) null);
        graphicsEngine.l();
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType h() {
        return UnitMovementType.NONE;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean Q() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: aj */
    public boolean canUnitAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ak */
    public boolean canMove() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: s_ */
    public boolean isVisibleOnScreen() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean c_() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: f, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.tree;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean a(BaseUnit baseUnit, float f) {
        if (!this.h) {
            if (this.i == 0.0f) {
            }
            this.currentHealth -= (((baseUnit.getPushMass() / 3000.0f) * this.maxHealth) * 0.06f) * f;
            this.i = 1.0f;
            this.unitAnimationScale = 1000.0f;
            if (this.currentHealth <= 0.0f) {
                this.rotationSpeed = Utility.getAngleBetweenPoints(this.posX, this.posY, baseUnit.posX, baseUnit.posY) + 180.0f;
                k();
            }
            if (!this.h) {
                return false;
            }
            return true;
        }
        return true;
    }

    public void k() {
        if (!this.h) {
            GameEngine gameEngine = GameEngine.getInstance();
            this.f = 2;
            this.g = 0.0f;
            S(0);
            this.isAlive = false;
            this.isDead = true;
            this.unitCreationTime = gameEngine.gameTimeMillis;
            this.h = true;
            this.m = false;
            for (int i = 0; i < 1; i++) {
                gameEngine.effectManager.setOnlyOnScreen();
                Effect effectCreateEffectInternal = gameEngine.effectManager.createEffectInternal(this.posX + Utility.randomFloatInRange(-12.0f, 12.0f), this.posY + Utility.randomFloatInRange(-12.0f, 12.0f), this.posZ, EffectType.custom, false, EffectQuality.high);
                if (effectCreateEffectInternal != null) {
                    effectCreateEffectInternal.aq = 9;
                    effectCreateEffectInternal.ap = Utility.getRandomIntInRange(4, 5);
                    effectCreateEffectInternal.Y = Utility.randomFloatInRange(-180.0f, 180.0f);
                    effectCreateEffectInternal.an = true;
                    effectCreateEffectInternal.K = 5.0f + Utility.randomFloatInRange(0.0f, 3.0f);
                    effectCreateEffectInternal.P = Utility.randomFloatInRange(-0.05f, 0.05f) + (Utility.fastCos(this.rotationSpeed) * 0.4f);
                    effectCreateEffectInternal.Q = Utility.randomFloatInRange(-0.05f, 0.05f) + (Utility.fastSin(this.rotationSpeed) * 0.4f);
                    effectCreateEffectInternal.v = true;
                    effectCreateEffectInternal.w = 0.2f;
                    effectCreateEffectInternal.G = 0.4f * this.l;
                    effectCreateEffectInternal.F = 0.4f * this.l;
                    effectCreateEffectInternal.V = 90 + Utility.getRandomIntInRange(0, 40);
                    effectCreateEffectInternal.W = effectCreateEffectInternal.V;
                    effectCreateEffectInternal.r = true;
                    effectCreateEffectInternal.ar = (short) 2;
                }
            }
            float fFastCos = this.posX + (Utility.fastCos(this.rotationSpeed) * (this.et - 5));
            float fFastSin = this.posY + (Utility.fastSin(this.rotationSpeed) * (this.et - 5));
            boolean z = true;
            for (int i2 = 0; i2 < 1; i2++) {
                gameEngine.effectManager.setOnlyOnScreen();
                Effect effectCreateEffectInternal2 = gameEngine.effectManager.createEffectInternal(fFastCos + Utility.randomFloatInRange(-17, 17), fFastSin + Utility.randomFloatInRange(-17, 17), this.posZ, EffectType.custom, false, EffectQuality.high);
                if (effectCreateEffectInternal2 != null) {
                    effectCreateEffectInternal2.aq = 9;
                    effectCreateEffectInternal2.ap = Utility.getRandomIntInRange(4, 5);
                    if (z) {
                        z = false;
                        effectCreateEffectInternal2.ap = 3;
                    }
                    effectCreateEffectInternal2.Y = Utility.randomFloatInRange(-180.0f, 180.0f);
                    effectCreateEffectInternal2.an = true;
                    if (effectCreateEffectInternal2.ap == 3) {
                        effectCreateEffectInternal2.P = Utility.randomFloatInRange(-0.05f, 0.05f);
                        effectCreateEffectInternal2.Q = Utility.randomFloatInRange(-0.05f, 0.05f);
                        effectCreateEffectInternal2.G = 1.5f * this.l;
                        effectCreateEffectInternal2.F = 2.2f * this.l;
                        effectCreateEffectInternal2.V = 90 + Utility.getRandomIntInRange(0, 40);
                        effectCreateEffectInternal2.ar = (short) 2;
                    } else {
                        effectCreateEffectInternal2.P = Utility.randomFloatInRange(-0.05f, 0.05f);
                        effectCreateEffectInternal2.Q = Utility.randomFloatInRange(-0.05f, 0.0f);
                        effectCreateEffectInternal2.G = 1.3f;
                        effectCreateEffectInternal2.F = 1.3f;
                        effectCreateEffectInternal2.V = 60 + Utility.getRandomIntInRange(0, 40);
                        effectCreateEffectInternal2.ar = (short) 1;
                    }
                    effectCreateEffectInternal2.W = effectCreateEffectInternal2.V;
                    effectCreateEffectInternal2.r = true;
                }
            }
            if (this.d == 1 || this.d == 2) {
                this.posX += Utility.fastCos(this.rotationSpeed) * ((this.et / 2) - 3);
                this.posY += Utility.fastSin(this.rotationSpeed) * ((this.et / 2) - 3);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void n() {
        super.n();
        this.rotationSpeed = Utility.normalizeAngle((this.posY * 5.0f) + (this.posX * 3.0f), false);
        if (this.d == 0) {
            this.f = ((int) ((this.posY * 5.0f) + (this.posX * 3.0f))) % 1;
        }
        if (this.d == 1) {
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float x() {
        return -1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public float setTarget(BaseUnit baseUnit, float f, Projectile projectile) {
        boolean z = this.isDead;
        float fA = super.setTarget(baseUnit, f, projectile);
        if (!z && this.isDead && projectile != null) {
            this.rotationSpeed = Utility.getAngleBetweenPoints(this.posX, this.posY, projectile.posX, projectile.posY) + 180.0f;
        }
        return fA;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean q() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean t() {
        return true;
    }
}
