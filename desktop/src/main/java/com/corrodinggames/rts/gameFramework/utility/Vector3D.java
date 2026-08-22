package com.corrodinggames.rts.gameFramework.utility;

import android.graphics.PointF;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ai */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ai.class */
public final class Vector3D {

    /* JADX INFO: renamed from: a */
    public float x;

    /* JADX INFO: renamed from: b */
    public float y;

    /* JADX INFO: renamed from: c */
    public float z;

    public void a(PointF pointF) {
        this.x = pointF.x;
        this.y = pointF.y;
        this.z = 0.0f;
    }
}
