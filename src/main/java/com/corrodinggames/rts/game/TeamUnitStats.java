package com.corrodinggames.rts.game;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface;
import com.corrodinggames.rts.game.units.buildings.Projectile;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.game.units.custom.resources.StoredResources;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.s */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/s.class */
public final class TeamUnitStats {
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public int g;
    public boolean m;
    public int n;
    public int o;
    public int a = 5;
    public StoredResources h = new StoredResources();
    public StoredResources i = new StoredResources();
    public StoredResources j = new StoredResources();
    public StoredResources k = new StoredResources();
    public StoredResources l = new StoredResources();
    public AnimationTagList p = new AnimationTagList();
    public AnimationTagList q = new AnimationTagList();

    /* JADX WARN: Multi-variable type inference failed */
    public void a(BaseUnit baseUnit) {
        this.d++;
        if (baseUnit.deceleration < 1.0f) {
            this.f++;
        } else {
            this.c++;
        }
        UnitType unitTypeR = baseUnit.r();
        if (!unitTypeR.k()) {
            this.b++;
        }
        if (!this.m && !baseUnit.u() && baseUnit.r().y()) {
            this.m = true;
        }
        UnitPrice unitPriceDq = baseUnit.dq();
        if (unitPriceDq != null) {
            this.k.a(unitPriceDq, 0.0d, Double.MAX_VALUE);
            this.l.a(unitPriceDq, -1.7976931348623157E308d, 0.0d);
        }
        if (baseUnit instanceof FactoryQueueInterface) {
            FactoryQueueInterface factoryQueueInterface = (FactoryQueueInterface) baseUnit;
            int iF = factoryQueueInterface.f(false);
            this.b += iF;
            this.e += iF;
            if (iF != 0) {
                a(factoryQueueInterface);
            }
        }
        c(baseUnit);
        float fCy = baseUnit.cy();
        if (fCy != 0.0f && baseUnit.deceleration >= 1.0f) {
            this.g = (int) (this.g + fCy);
        }
        StoredResources unitRotationData = baseUnit.getUnitRotationData();
        if (!unitRotationData.c() && baseUnit.deceleration >= 1.0f) {
            this.h.b(unitRotationData);
            this.i.a(unitRotationData, 0.0d, Double.MAX_VALUE);
            this.j.a(unitRotationData, -1.7976931348623157E308d, 0.0d);
        }
        if (baseUnit.getUnitFlags()) {
            int iB = baseUnit.getUnitDescription().b();
            UnitPrice unitPriceB = unitTypeR.B();
            if (unitPriceB != null) {
                iB += unitPriceB.b();
            }
            if (unitTypeR.j()) {
                this.o += iB;
            } else {
                this.n += iB;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void b(BaseUnit baseUnit) {
        this.d--;
        if (baseUnit.deceleration < 1.0f) {
            this.f--;
        } else {
            this.c--;
        }
        UnitType unitTypeR = baseUnit.r();
        if (!unitTypeR.k()) {
            this.b--;
        }
        UnitPrice unitPriceDq = baseUnit.dq();
        if (unitPriceDq != null) {
            this.k.b(unitPriceDq, 0.0d, Double.MAX_VALUE);
            this.l.b(unitPriceDq, -1.7976931348623157E308d, 0.0d);
        }
        if (baseUnit instanceof FactoryQueueInterface) {
            FactoryQueueInterface factoryQueueInterface = (FactoryQueueInterface) baseUnit;
            int iF = factoryQueueInterface.f(false);
            this.b -= iF;
            this.e -= iF;
            if (iF != 0) {
                b(factoryQueueInterface);
            }
        }
        d(baseUnit);
        float fCy = baseUnit.cy();
        if (fCy != 0.0f && baseUnit.deceleration >= 1.0f) {
            this.g = (int) (this.g - fCy);
        }
        StoredResources unitRotationData = baseUnit.getUnitRotationData();
        if (!unitRotationData.c() && baseUnit.deceleration >= 1.0f) {
            this.h.c(unitRotationData);
            this.i.b(unitRotationData, 0.0d, Double.MAX_VALUE);
            this.j.b(unitRotationData, -1.7976931348623157E308d, 0.0d);
        }
        if (baseUnit.getUnitFlags()) {
            int iB = baseUnit.getUnitDescription().b();
            UnitPrice unitPriceB = unitTypeR.B();
            if (unitPriceB != null) {
                iB += unitPriceB.b();
            }
            if (unitTypeR.j()) {
                this.o -= iB;
            } else {
                this.n -= iB;
            }
        }
    }

    private final void c(BaseUnit baseUnit) {
        AnimationSet unitCombatAnimation = baseUnit.getUnitCombatAnimation();
        if (unitCombatAnimation != null) {
            for (AnimationTag animationTag : unitCombatAnimation.a) {
                AnimationTagEntry animationTagEntryA = a(animationTag);
                if (baseUnit.deceleration < 1.0f) {
                    animationTagEntryA.c++;
                } else {
                    animationTagEntryA.b++;
                }
            }
        }
    }

    private final void d(BaseUnit baseUnit) {
        AnimationSet unitCombatAnimation = baseUnit.getUnitCombatAnimation();
        if (unitCombatAnimation != null) {
            for (AnimationTag animationTag : unitCombatAnimation.a) {
                AnimationTagEntry animationTagEntryA = a(animationTag);
                if (baseUnit.deceleration < 1.0f) {
                    animationTagEntryA.c--;
                } else {
                    animationTagEntryA.b--;
                }
            }
        }
    }

    private final void a(FactoryQueueInterface factoryQueueInterface) {
        UnitType unitType;
        AnimationSet animationSetX;
        FastArrayList<Projectile> fastArrayListDx = factoryQueueInterface.dx();
        if (fastArrayListDx.size != 0) {
            for (Projectile projectile : fastArrayListDx) {
                if (projectile.f && (unitType = projectile.g) != null && (animationSetX = unitType.x()) != null) {
                    for (AnimationTag animationTag : animationSetX.a) {
                        a(animationTag).d += projectile.a;
                    }
                }
            }
        }
    }

    private final void b(FactoryQueueInterface factoryQueueInterface) {
        UnitType unitType;
        AnimationSet animationSetX;
        FastArrayList<Projectile> fastArrayListDx = factoryQueueInterface.dx();
        if (fastArrayListDx.size != 0) {
            for (Projectile projectile : fastArrayListDx) {
                if (projectile.f && (unitType = projectile.g) != null && (animationSetX = unitType.x()) != null) {
                    for (AnimationTag animationTag : animationSetX.a) {
                        a(animationTag).d -= projectile.a;
                    }
                }
            }
        }
    }

    public final AnimationTagEntry a(AnimationTag animationTag) {
        AnimationTagEntry[] animationTagEntryArr = this.q.b;
        int i = this.q.c;
        for (int i2 = 0; i2 < i; i2++) {
            AnimationTagEntry animationTagEntry = animationTagEntryArr[i2];
            if (animationTagEntry.a == animationTag) {
                return animationTagEntry;
            }
            if (animationTagEntry.a == null) {
                animationTagEntry.a = animationTag;
                return animationTagEntry;
            }
        }
        AnimationTagEntry animationTagEntry2 = new AnimationTagEntry();
        animationTagEntry2.a = animationTag;
        this.q.a(animationTagEntry2);
        return animationTagEntry2;
    }
}
