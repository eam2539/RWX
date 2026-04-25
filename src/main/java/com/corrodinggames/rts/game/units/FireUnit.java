package com.corrodinggames.rts.game.units;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.map.MapTile;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ai */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ai.class */
public class FireUnit extends NaturalUnit {
    Texture b;
    int c;
    int d;
    float e;
    float f;
    int g;
    int h;
    float i;
    float j;
    boolean k;
    float l;
    float m;
    float n;
    float o;
    float p;
    float q;
    boolean r;
    Rect u;
    static Texture[] a = new Texture[2];
    static Point s = new Point();
    public static FireUnitFinder t = new FireUnitFinder();

    public static void b() {
        a[0] = GameEngine.getInstance().graphicsEngine2.a(R.drawable.fire);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(this.c);
        gameOutputStream.writeInt(this.d);
        gameOutputStream.writeFloat(this.e);
        gameOutputStream.writeByte(0);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.c = gameInputStream.readInt();
        this.d = gameInputStream.readInt();
        this.e = gameInputStream.readFloat();
        gameInputStream.readByte();
        super.a(gameInputStream);
    }

    public Texture d() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        return false;
    }

    public FireUnit(boolean z) {
        super(z);
        this.d = 0;
        this.g = 0;
        this.h = 0;
        this.k = false;
        this.u = new Rect();
        a(0);
        this.speed = 20.0f;
        this.maxSpeed = this.speed + 1.0f;
        this.maxHealth = 100.0f;
        this.currentHealth = this.maxHealth;
        this.rotationSpeed = -90.0f;
        this.isAttacking = false;
        this.o = 0.05f;
        this.p = 120.0f;
        S(3);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void f_() {
        this.isAttacking = false;
    }

    public void a(int i) {
        this.c = i;
        if (this.c == 0) {
            T(20);
            U(20);
            this.g = 0;
            this.h = 0;
            this.b = a[0];
            return;
        }
        throw new RuntimeException("Fire type:" + this.c + " is not supported");
    }

    public void f() {
        this.k = true;
        this.i = Utility.getDeterministicRandomInt((GameObject) this, -5, 5, 1);
        this.j = Utility.getDeterministicRandomInt((GameObject) this, -5, 5, 2);
        this.e = Utility.getDeterministicRandomInt((GameObject) this, 1, 10, 3);
        this.d = Utility.getDeterministicRandomInt((GameObject) this, 0, 2, 4);
        this.f = Utility.getDeterministicRandomInt((GameObject) this, 7, 13, 5);
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        gameEngine.tileMap.setCursorTileIndexFromWorldPoint(this.posX, this.posY);
        int i = gameEngine.tileMap.cursorTileX;
        int i2 = gameEngine.tileMap.cursorTileY;
        if (!tileMap.isInBounds(i, i2)) {
            this.l = 0.0f;
            this.m = 0.0f;
            this.n = 2.0f;
            return;
        }
        MapTile tileAt = gameEngine.tileMap.groundLayer.getTileAt(i, i2);
        boolean z = false;
        if (tileAt.isWater || tileAt.isCliff || tileAt.hasLargeObject || tileAt.isWaterBridge) {
            z = true;
        }
        if (z) {
            this.l = 0.0f;
            this.m = 0.0f;
            this.n = 2.0f;
        } else {
            this.l = 5.0E-4f;
            this.m = 1.0f;
            this.n = 0.3f;
            this.o += Utility.getDeterministicRandomInt((GameObject) this, 0, 10, 10) / 1000.0f;
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!this.k) {
            f();
        }
        if (this.o < this.m) {
            this.o += this.l * f;
            if (this.o > this.m) {
                this.o = this.m;
            }
        }
        if (this.o > this.n) {
            this.q = (float) (((double) this.q) + (0.01d * ((double) f)));
            if ((!this.r && this.q > 1.0f) || this.q > 8.0f) {
                this.q = Utility.getDeterministicRandomInt((GameObject) this, 0, 10, 10) / 1000.0f;
                k();
            }
        }
        this.e += f;
        if (this.e > 10.0f) {
            this.e = 0.0f;
            this.d++;
            if (this.d > 3) {
                this.d = 0;
            }
        }
        if (this.o < 0.0f) {
            bv();
        }
    }

    public void k() {
        this.r = true;
        b(-1, -1);
        b(0, -1);
        b(1, -1);
        b(-1, 0);
        b(1, 0);
        b(-1, 1);
        b(0, 1);
        b(1, 1);
    }

    public void b(int i, int i2) {
        GameEngine gameEngine = GameEngine.getInstance();
        float f = (int) (this.posX + (i * gameEngine.tileMap.tileWorldSizeX));
        float f2 = (int) (this.posY + (i2 * gameEngine.tileMap.tileWorldSizeY));
        if (a(f, f2) == null) {
            FireUnit fireUnit = new FireUnit(false);
            fireUnit.posX = f;
            fireUnit.posY = f2;
            fireUnit.setUnitTeam(this.team);
            gameEngine.unitSpatialIndex.a(fireUnit);
            PlayerTeam.c(fireUnit);
            this.r = false;
        }
    }

    public static FireUnit a(float f, float f2) {
        GameEngine gameEngine = GameEngine.getInstance();
        t.a(f, f2);
        gameEngine.unitSpatialIndex.a(f, f2, 30.0f, null, 1.0f, t);
        return t.c;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        int i = this.g;
        int i2 = this.h;
        int i3 = i + (this.d * this.es);
        dC.a(i3, i2, i3 + this.es, i2 + this.et);
        return dC;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        Texture textureD = d();
        GameEngine gameEngine = GameEngine.getInstance();
        du.a(getUnitBounds());
        du.a(0.0f, (int) (-this.posZ));
        du.a(this.i, this.j);
        dv.a(a_(false));
        gameEngine.graphicsEngine2.k();
        float fD = du.d();
        float fE = du.e();
        gameEngine.graphicsEngine2.a(getUnitArmorRating(false), fD, fE);
        gameEngine.graphicsEngine2.a(this.o * 2.7f, this.o * 2.7f, fD, fE);
        gameEngine.graphicsEngine2.a(textureD, dv, du, (Paint) null);
        gameEngine.graphicsEngine2.l();
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
    /* JADX INFO: renamed from: ak */
    public boolean canMove() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: aj */
    public boolean canUnitAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: s_ */
    public boolean isBuilding() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean c_() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: s, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.spreadingFire;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void n() {
        super.n();
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
    public boolean P() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public float setTarget(BaseUnit baseUnit, float f, Projectile projectile) {
        this.o -= f / 100.0f;
        return super.setTarget(baseUnit, 0.0f, projectile);
    }
}
