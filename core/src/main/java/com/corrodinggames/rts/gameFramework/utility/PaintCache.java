package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.z */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/z.class */
public final class PaintCache {
    public int a;
    public KoolPaint.Style b;
    public GamePaint c;

    public PaintCache(int i, KoolPaint.Style style) {
        GamePaint gamePaint = new GamePaint();
        gamePaint.b(i);
        gamePaint.a(style);
        this.c = gamePaint;
        this.b = style;
        this.a = i;
    }
}
