package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Paint;

public final class RwxBlendPaint extends Paint {
    public static final int BLEND_NORMAL = 0;
    public static final int BLEND_LIGHTING_ADD = 1;
    public static final int BLEND_TEAM_COPY = 2;
    public static final int BLEND_TEAM_ADDITIVE = 3;
    public static final int BLEND_SOURCE = 4;
    public static final int BLEND_ADD = 5;
    public static final int BLEND_MULTIPLY = 6;
    public static final int BLEND_SCREEN = 7;

    public int rwxBlendMode = BLEND_NORMAL;
    public C0009fo rwxShader;

    public RwxBlendPaint(int flags) {
        super(flags);
    }
}
