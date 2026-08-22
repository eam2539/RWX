package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.z */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/z.class */
public final class PaintCache {

    /* JADX INFO: renamed from: a */
    public int color;

    /* JADX INFO: renamed from: b */
    public KoolPaint.Style style;

    /* JADX INFO: renamed from: c */
    public GamePaint paint;

    public PaintCache(int i, KoolPaint.Style style) {
        GamePaint gamePaint = new GamePaint();
        gamePaint.b(i);
        gamePaint.a(style);
        this.paint = gamePaint;
        this.style = style;
        this.color = i;
    }
}
