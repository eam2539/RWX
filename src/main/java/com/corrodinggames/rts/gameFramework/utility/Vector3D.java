package com.corrodinggames.rts.gameFramework.utility;

import android.graphics.PointF;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ai */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ai.class */
public final class Vector3D {
    public float a;
    public float b;
    public float c;

    public void a(PointF pointF) {
        this.a = pointF.x;
        this.b = pointF.y;
        this.c = 0.0f;
    }
}
