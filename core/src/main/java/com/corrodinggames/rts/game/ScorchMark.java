package com.corrodinggames.rts.game;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import io.github.rwx.geometry.Rect;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolPaint;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/l.class */
public class ScorchMark extends GameObject {

    /* JADX INFO: renamed from: a */
    int type;

    /* JADX INFO: renamed from: b */
    int variant;

    /* JADX INFO: renamed from: e */
    ExplosionType markType;

    /* JADX INFO: renamed from: f */
    int frame;
    static final Rect h = new Rect();
    static final Rect i = new Rect();
    static final KoolPaint j = GameViewUtils.b();
    static Texture k = null;
    static Texture l = null;
    static Texture m = null;
    static final RectF n = new RectF();

    /* JADX INFO: renamed from: c */
    int width = 50;

    /* JADX INFO: renamed from: d */
    int height = 40;

    /* JADX INFO: renamed from: g */
    int status = -1;

    public static void b() {
        GameEngine gameEngine = GameEngine.getInstance();
        k = gameEngine.renderGraphicsEngine.a(R.drawable.scorch_mark, true);
        k.m = true;
        l = gameEngine.renderGraphicsEngine.a(R.drawable.scorch_mark_nuke, true);
        l.m = true;
        m = gameEngine.renderGraphicsEngine.a(R.drawable.blood_mark, true);
        m.m = true;
    }

    public ScorchMark() {
        GameEngine gameEngine = GameEngine.getInstance();
        S(-1);
        this.frame = gameEngine.gameTimeMillis;
    }

    public static void a(float f, float f2) {
        a(f, f2, ExplosionType.normal);
    }

    public static void a(float f, float f2, ExplosionType explosionType) {
        if (!b(f, f2, explosionType)) {
            return;
        }
        ScorchMark scorchMark = new ScorchMark();
        scorchMark.posX = f;
        scorchMark.posY = f2;
        if (explosionType == ExplosionType.normal) {
            scorchMark.type = 0;
            scorchMark.variant = Utility.getDeterministicRandomInt(scorchMark, 0, 3, 0);
        } else {
            scorchMark.type = 2;
        }
        if (scorchMark.type == 2) {
            scorchMark.width = l.m();
            scorchMark.height = l.l();
        }
        scorchMark.markType = explosionType;
        scorchMark.d();
    }

    public static void a(OrderableUnit orderableUnit, int i2) {
        if (!orderableUnit.isOverWater()) {
            ExplosionType explosionType = i2 == 2 ? ExplosionType.nuke : ExplosionType.normal;
            if (!b(orderableUnit.posX, orderableUnit.posY, explosionType)) {
                return;
            }
            ScorchMark scorchMark = new ScorchMark();
            scorchMark.type = i2;
            if (scorchMark.type == 2) {
                scorchMark.width = l.m();
                scorchMark.height = l.l();
            }
            scorchMark.posX = orderableUnit.posX;
            scorchMark.posY = orderableUnit.posY;
            scorchMark.markType = explosionType;
            scorchMark.variant = Utility.getDeterministicRandomInt(scorchMark, 0, 3, 0);
            scorchMark.d();
        }
    }

    public static boolean b(float f, float f2, ExplosionType explosionType) {
        int i2 = 0;
        int i3 = 0;
        int i4 = 25;
        if (explosionType == ExplosionType.nuke) {
            i4 = 45;
        }
        GameObject[] gameObjectArrA = GameObject.fastGameObjectList.a();
        int size = GameObject.fastGameObjectList.size();
        for (int i5 = 0; i5 < size; i5++) {
            GameObject gameObject = gameObjectArrA[i5];
            if (gameObject instanceof ScorchMark) {
                ScorchMark scorchMark = (ScorchMark) gameObject;
                if (Utility.abs(scorchMark.posX - f) < i4 && Utility.abs(scorchMark.posY - f2) < i4 && scorchMark.markType == explosionType) {
                    i2++;
                    if (Utility.abs(scorchMark.posX - f) < 5 && Utility.abs(scorchMark.posY - f2) < 5) {
                        i3++;
                    }
                }
            }
        }
        if (i2 >= 3 || i3 >= 1) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean a(GameEngine gameEngine) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean f(float f) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return true;
    }

    public RectF c() {
        n.a = this.posX - (this.width * 0.5f);
        n.c = this.posX + (this.width * 0.5f);
        n.b = this.posY - (this.height * 0.5f);
        n.d = this.posY + (this.height * 0.5f);
        return n;
    }

    public void a(final GraphicsEngine y, final int integer2, final int integer3, final float float4) {
        final int a = this.variant * this.width;
        final int b = 0;
        Texture e = null;
        final int width = this.width;
        final int height = this.height;
        if (this.type == 0) {
            e = ScorchMark.k;
        } else if (this.type == 1) {
            e = ScorchMark.m;
        } else if (this.type == 2) {
            e = ScorchMark.l;
        }
        final Rect h = ScorchMark.h;
        final Rect i = ScorchMark.i;
        i.a = a;
        i.b = b;
        i.c = a + width;
        i.d = b + height;
        int n = (int) this.posX;
        int n2 = (int) this.posY;
        n -= integer2;
        n2 -= integer3;
        final int n3 = width >> 1;
        final int n4 = height >> 1;
        final float n5 = (float) (n - n3);
        final float n6 = (float) (n2 - n4);
        final float n7 = (float) (n + n3);
        final float n8 = (float) (n2 + n4);
        h.a = (int) (n5 * float4);
        h.b = (int) (n6 * float4);
        h.c = (int) (n7 * float4);
        h.d = (int) (n8 * float4);
        y.b(e, i, h, ScorchMark.j);
    }

    private void d() {
        GameEngine.getInstance().tileMap.enqueueScorchMark(this);
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void d(float f) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.posX);
        gameOutputStream.writeFloat(this.posY);
        gameOutputStream.writeInt(this.type);
        gameOutputStream.writeInt(this.variant);
        gameOutputStream.writeInt(this.width);
        gameOutputStream.writeInt(this.height);
        gameOutputStream.writeEnumOrdinal(this.markType);
        gameOutputStream.writeInt(this.frame);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.posX = gameInputStream.readFloat();
        this.posY = gameInputStream.readFloat();
        this.type = gameInputStream.readInt();
        this.variant = gameInputStream.readInt();
        this.width = gameInputStream.readInt();
        this.height = gameInputStream.readInt();
        if (gameInputStream.getProtocolVersion() >= 87) {
            this.markType = (ExplosionType) gameInputStream.readEnumOrdinalOrNull(ExplosionType.class);
            this.frame = gameInputStream.readInt();
        } else {
            this.markType = this.type == 2 ? ExplosionType.nuke : ExplosionType.normal;
            if (this.type == 2) {
                this.width = l.m();
                this.height = l.l();
            }
        }
        super.a(gameInputStream);
    }
}
