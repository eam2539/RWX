package com.corrodinggames.rts.gameFramework.graphics.opengl;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/f.class */
public class TextureAtlas {
    public Texture a;
    public GraphicsEngine b;
    public Paint c;
    boolean e;
    int f;
    int d = 0;
    boolean g = false;
    int h = 0;
    int i = 0;
    int j = 0;
    int k = 1;
    HashMap l = new HashMap();
    ArrayList m = new ArrayList();

    public TextureAtlas(int i, int i2) {
        a(i, i2);
    }

    public void a(int i, int i2) {
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.isInSpace("Creating BitmapOrTextureAlias: " + i + "x" + i2);
        this.a = gameEngine.graphicsEngine2.a(i, i2, true);
        this.b = gameEngine.graphicsEngine2.b(this.a);
        this.c = new GamePaint();
        this.c.a(new TeamColorFilter(BlendMode.copy));
    }

    public void a(Texture texture, int i, int i2) {
        this.b.b(texture, i, i2, this.c);
        this.b.p();
    }

    public void a() {
        this.b.a(0, PorterDuff.Mode.CLEAR);
    }

    public void b() {
        this.d = 0;
        this.e = false;
        this.f = 0;
        this.h = 0;
        this.i = 0;
        this.j = 0;
        this.l.clear();
        a();
    }

    public void c() {
        this.f++;
        if (this.e && this.f > 600) {
            this.g = true;
            this.m.clear();
        }
    }

    public void d() {
        if (this.g) {
            this.g = false;
            b();
            Iterator it = this.m.iterator();
            while (it.hasNext()) {
                a((Texture) it.next());
            }
            this.m.clear();
        }
    }

    public AtlasRegion a(Texture texture) {
        AtlasRegion atlasRegion = (AtlasRegion) this.l.get(texture);
        if (atlasRegion != null) {
            if (this.g) {
                this.m.add(texture);
            }
            if (atlasRegion.f != texture.e) {
                GameEngine.isInSpace("BitmapOrTextureAlias: Image was updated: " + texture.a());
                this.l.remove(texture);
            } else {
                return atlasRegion;
            }
        }
        AtlasRegion atlasRegionB = b(texture);
        if (atlasRegionB != null) {
            return atlasRegionB;
        }
        return null;
    }

    public AtlasRegion b(Texture texture) {
        int iM = texture.m();
        int iL = texture.l();
        int iM2 = this.a.m();
        int iL2 = this.a.l();
        if (this.h + iM > iM2) {
            this.h = 0;
            this.i += this.j + this.k;
            this.j = 0;
        }
        if (this.i + iL > iL2) {
            if (!this.e) {
                this.e = true;
                return null;
            }
            return null;
        }
        AtlasRegion atlasRegion = new AtlasRegion();
        atlasRegion.a = this.a;
        int i = this.h;
        int i2 = this.i;
        this.h += iM + this.k;
        if (this.j < iL) {
            this.j = iL;
        }
        a(texture, i, i2);
        atlasRegion.b = i;
        atlasRegion.c = i2;
        atlasRegion.d = iM;
        atlasRegion.e = iL;
        atlasRegion.f = texture.e;
        this.d++;
        this.l.put(texture, atlasRegion);
        return atlasRegion;
    }
}
