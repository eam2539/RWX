package com.corrodinggames.rts.gameFramework.android.graphics.opengl;

import android.graphics.Paint;
import com.corrodinggames.rts.gameFramework.android.graphics.C0009fo;
import com.corrodinggames.rts.gameFramework.m.TeamColorTexture;
import com.corrodinggames.rts.gameFramework.m.UnitTexture;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.g */
/* JADX INFO: loaded from: classes.dex */
public final class AtlasRegion extends C0009fo {

    /* JADX INFO: renamed from: a */
    int f769a;
    boolean b;

    public AtlasRegion(String str) throws IOException {
        super(str);
        this.f769a = -99;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.C0009fo
    public final boolean a() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.C0009fo
    public final boolean b() {
        if (-16711936 == this.f769a) {
            return false;
        }
        a("teamColor", -16711936);
        this.f769a = -16711936;
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.C0009fo
    public final boolean a(Paint paint, UnitTexture unitTexture) {
        boolean z = false;
        if (unitTexture instanceof TeamColorTexture) {
            TeamColorTexture teamColorTexture = (TeamColorTexture) unitTexture;
            if (teamColorTexture.D != this.f769a) {
                a("teamColor", teamColorTexture.D);
                z = true;
                this.f769a = teamColorTexture.D;
            }
        }
        boolean z2 = z;
        super.a(paint, unitTexture);
        return z2;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.C0009fo
    public final void c() {
        super.c();
    }
}
