package com.corrodinggames.rts.game;

import android.graphics.Paint;
import android.graphics.Rect;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.ShaderProgram;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/j.class */
public class FrameBufferHelper {
    public Texture a;
    GraphicsEngine b;
    GamePaint c;
    ShaderProgram d;
    Paint e;
    Rect f;
    boolean g;

    public FrameBufferHelper() {
        this.e = new Paint();
        this.f = new Rect(-101, 0, -1, 100);
        this.c = new GamePaint();
    }

    public FrameBufferHelper(String str) {
        this();
        try {
            this.d = new ShaderProgram(str);
            this.c.a(this.d);
            if (this.d.o != 0) {
                this.g = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean a() {
        if ((this.d != null && this.d.o != 0) || this.g) {
            return true;
        }
        return false;
    }

    public void a(GraphicsEngine graphicsEngine) {
        a(graphicsEngine, graphicsEngine.m(), graphicsEngine.n(), 10);
    }

    public void a(GraphicsEngine graphicsEngine, int i, int i2, int i3) {
        if (this.g) {
            return;
        }
        if (this.a != null && (i > this.a.m() || i2 > this.a.l())) {
            this.a.o();
            this.a = null;
            this.b = null;
        }
        if (this.a == null) {
            try {
                this.a = graphicsEngine.a(i + i3, i2 + i3, true);
                this.b = graphicsEngine.a(this.a);
            } catch (OutOfMemoryError e) {
                this.g = true;
                GameEngine.reportOOM(AssetType.gameImageCreate, e);
                return;
            }
        }
        this.b.a(i, i2);
    }

    public void b() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.renderGraphicsEngine.b(this.f, this.e);
        gameEngine.renderGraphicsEngine.b(this.a, 0.0f, 0.0f, this.c);
    }
}
