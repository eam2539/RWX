package com.corrodinggames.rts.game.units.custom.condition;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.condition.resources.AbstractResource;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/c.class */
public class ResourceConditionChecker {
    public final FastArrayList a = new FastArrayList();

    public void a(Resource resource) {
        if (!this.a.contains(resource)) {
            this.a.add(resource);
        }
    }

    public void a(StoredResources storedResources, BaseUnit baseUnit, double d) {
        int i = storedResources.b.size;
        Object[] objArrA = storedResources.b.a();
        for (int i2 = 0; i2 < i; i2++) {
            StoredResourceEntry storedResourceEntry = (StoredResourceEntry) objArrA[i2];
            if (storedResourceEntry.a.a(baseUnit) < storedResourceEntry.b * d) {
                a(storedResourceEntry.a);
            }
        }
    }

    public void a(UnitPrice unitPrice, BaseUnit baseUnit, double d) {
        if (!unitPrice.k.c()) {
            a(unitPrice.k, baseUnit, d);
        }
        if (unitPrice.b > 0 && baseUnit.team.credits < ((double) unitPrice.b) * d) {
            a(AbstractResource.D);
        }
    }

    public boolean a(StoredResources storedResources) {
        int i = storedResources.b.size;
        Object[] objArrA = storedResources.b.a();
        for (int i2 = 0; i2 < i; i2++) {
            if (this.a.contains(((StoredResourceEntry) objArrA[i2]).a)) {
                return true;
            }
        }
        return false;
    }

    public boolean a(UnitPrice unitPrice) {
        if (unitPrice.b > 0 && this.a.contains(AbstractResource.D)) {
            return true;
        }
        if (!unitPrice.k.c() && a(unitPrice.k)) {
            return true;
        }
        return false;
    }

    public void a() {
        this.a.clear();
    }
}
