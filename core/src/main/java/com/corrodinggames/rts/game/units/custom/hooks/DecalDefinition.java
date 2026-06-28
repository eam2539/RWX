package com.corrodinggames.rts.game.units.custom.hooks;

import com.corrodinggames.rts.game.TeamRelation;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b/d.class */
public class DecalDefinition implements Comparable<DecalDefinition> {
    String a;
    boolean c;
    boolean d;
    boolean e;
    boolean f;
    boolean g;
    boolean i;
    public float j;
    public boolean k;
    public boolean l;
    public boolean m;
    public boolean n;
    public boolean q;
    public LogicBoolean r;
    public LogicBoolean s;
    public LogicBoolean t;
    public boolean u;
    public DecalImageSlice v;
    public DecalImageSlice[] w;
    public float x;
    public int y;
    public boolean z;
    LogicBoolean A;
    LogicBoolean B;
    public Texture C;
    public float D;
    public float E;
    public LogicBoolean F;
    public DecalLayer G;
    public float H;
    public boolean I;
    public boolean M;
    public LogicBoolean N;
    public int O;
    public int P;
    public float Q;
    public float R;
    public float S;
    public float T;
    public float U;
    public float V;
    public LogicBoolean W;
    public LogicBoolean X;
    public boolean Y;
    public boolean Z;
    public float aa;
    public float ab;
    public LogicBoolean ac;
    public LogicBoolean ad;
    public boolean af;
    public GamePaint ah;
    public LogicBoolean ai;
    boolean b = false;
    TeamRelation h = TeamRelation.any;
    public int o = -1;
    public float p = 1.0f;
    public int J = -1;
    public int K = -1;
    public int L = -1;
    public int ae = -1;
    public int ag = -1;

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(DecalDefinition decalDefinition) {
        if (decalDefinition == null) {
            return 0;
        }
        float f = this.H - decalDefinition.H;
        if (f < 0.0f) {
            return -1;
        }
        return f > 0.0f ? 1 : 0;
    }
}
